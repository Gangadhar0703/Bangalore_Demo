package org.accessibilitymapper.web;

import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.exception.ResourceNotFoundException;
import org.accessibilitymapper.repository.FacilityRepository;
import org.accessibilitymapper.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
public class AdminController {
    private final FacilityRepository facilities;
    private final ReviewService reviews;

    public AdminController(FacilityRepository facilities, ReviewService reviews) {
        this.facilities = facilities;
        this.reviews = reviews;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("pendingPlaces", facilities.findByApprovedFalseOrderByIdAsc());
        model.addAttribute("pendingReviews", reviews.pending());
        return "admin";
    }

    @PostMapping("/admin/facilities/{id}/approve")
    public String approvePlace(@PathVariable Long id, RedirectAttributes redirect) {
        Facility facility = facilities.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found."));
        facility.setApproved(true);
        facility.setLastAccessibilityCheck(LocalDateTime.now());
        checkAllFacilities(facility, facility.getLastAccessibilityCheck());
        facilities.save(facility);
        redirect.addFlashAttribute("notice", "Place approved and added to the public map.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/facilities/{id}/verify")
    public String verifyPlace(@PathVariable Long id, RedirectAttributes redirect) {
        Facility facility = facilities.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found."));
        facility.setApproved(true);
        facility.setVerified(true);
        facility.setLastAccessibilityCheck(LocalDateTime.now());
        checkAllFacilities(facility, facility.getLastAccessibilityCheck());
        facilities.save(facility);
        redirect.addFlashAttribute("notice", "Place approved with a Verified Accessible badge.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/reviews/{id}/approve")
    public String approveReview(@PathVariable Long id, RedirectAttributes redirect) {
        reviews.approve(id);
        redirect.addFlashAttribute("notice", "Visit note published and the checked facilities were updated.");
        return "redirect:/admin";
    }

    private void checkAllFacilities(Facility facility, LocalDateTime checkedAt) {
        facility.setWheelchairRampCheckedAt(checkedAt);
        facility.setWorkingElevatorCheckedAt(checkedAt);
        facility.setAccessibleWashroomCheckedAt(checkedAt);
        facility.setTactilePavingCheckedAt(checkedAt);
        facility.setWideDoorwaysCheckedAt(checkedAt);
    }
}
