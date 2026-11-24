package org.accessibilitymapper;

import org.junit.jupiter.api.Test;
import org.accessibilitymapper.model.AccessibilityReport;
import org.accessibilitymapper.model.Account;
import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.model.Review;
import org.accessibilitymapper.repository.AccountRepository;
import org.accessibilitymapper.repository.FacilityRepository;
import org.accessibilitymapper.repository.ReviewRepository;
import org.accessibilitymapper.service.FacilityService;
import org.accessibilitymapper.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:accessibility-mapper-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.seed-demo-data=true"
})
class AccessibilityMapperApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private FacilityService facilityService;
    @Autowired private ReviewService reviewService;
    @Autowired private FacilityRepository facilities;
    @Autowired private AccountRepository accounts;
    @Autowired private ReviewRepository reviews;

    @Test
    void homeShowsSeedPlacesAndAccessibleSearch() throws Exception {
        mockMvc.perform(get("/").param("features", "washroom"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cubbon Park")))
                .andExpect(content().string(containsString("Orion Mall")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Lalbagh Botanical Garden"))));
    }

    @Test
    void switchingToMapKeepsTheSearchCriteria() throws Exception {
        mockMvc.perform(get("/").param("q", "cubbon").param("location", "Bangalore")
                        .param("features", "ramp").param("view", "map"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "cubbon"))
                .andExpect(model().attribute("location", "Bangalore"))
                .andExpect(model().attribute("features", java.util.List.of("ramp")))
                .andExpect(model().attribute("view", "map"))
                .andExpect(content().string(containsString("Cubbon Park")));
    }

    @Test
    void adminQueueRequiresAnAdminAccount() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void placeDetailsShowApprovedVisitNotes() throws Exception {
        mockMvc.perform(get("/places/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cubbon Park")))
                .andExpect(content().string(containsString("Kasturba Road")))
                .andExpect(content().string(containsString("Step-free: available")))
                .andExpect(content().string(containsString("Last checked")));
    }

    @Test
    void loggedOutVisitorsAreAskedToSignInBeforeSharingVisitNotes() throws Exception {
        mockMvc.perform(get("/places/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Log in to share a visit note")))
                .andExpect(content().string(containsString("create an account")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Share visit note"))));
    }

    @Test
    void anonymousVisitNoteSubmissionRedirectsToLoginWithoutSaving() throws Exception {
        MvcResult login = mockMvc.perform(get("/login")).andReturn();
        Matcher csrf = Pattern.compile("<input[^>]*name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(login.getResponse().getContentAsString());
        assertTrue(csrf.find());
        long reviewCount = reviews.count();

        mockMvc.perform(post("/places/1/reviews")
                        .session((MockHttpSession) login.getRequest().getSession(false))
                        .param("_csrf", csrf.group(1))
                        .param("rating", "5")
                        .param("comments", "A test visit note"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertEquals(reviewCount, reviews.count());
    }

    @Test
    void addingPlacesRequiresAnAccount() throws Exception {
        mockMvc.perform(get("/contribute"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void searchesAreLimitedToTheBangaloreArea() throws Exception {
        mockMvc.perform(get("/").param("location", "Mumbai"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No places found just yet.")));
        assertTrue(facilityService.search("", "All places", "Wellington", java.util.List.of()).isEmpty());
    }

    @Test
    void approvingAVisitUpdatesOnlyTheFacilitiesTheMemberChecked() {
        Facility cubbonPark = facilities.findAll().stream()
                .filter(facility -> facility.getName().equals("Cubbon Park"))
                .findFirst().orElseThrow();
        var existingWashroomCheck = cubbonPark.getAccessibleWashroomCheckedAt();
        var existingTactileCheck = cubbonPark.getTactilePavingCheckedAt();
        Account member = accounts.findByEmailIgnoreCase("mapper@access.org").orElseThrow();
        Review report = reviews.save(new Review(cubbonPark, member, 4, "Checked the lift and entry today.", null,
                new AccessibilityReport(false, true, null, null, null)));

        reviewService.approve(report.getId());

        Facility updatedPark = facilities.findById(cubbonPark.getId()).orElseThrow();
        assertFalse(updatedPark.isWheelchairRamp());
        assertTrue(updatedPark.isWorkingElevator());
        assertTrue(updatedPark.isAccessibleWashroom());
        assertTrue(updatedPark.isTactilePaving());
        assertNotNull(updatedPark.getLastAccessibilityCheck());
        assertNotNull(updatedPark.getWheelchairRampCheckedAt());
        assertNotNull(updatedPark.getWorkingElevatorCheckedAt());
        assertEquals(existingWashroomCheck, updatedPark.getAccessibleWashroomCheckedAt());
        assertEquals(existingTactileCheck, updatedPark.getTactilePavingCheckedAt());
    }
}
