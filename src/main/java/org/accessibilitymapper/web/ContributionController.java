package org.accessibilitymapper.web;

import jakarta.validation.Valid;
import org.accessibilitymapper.model.Account;
import org.accessibilitymapper.model.AccessibilityReport;
import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.service.FacilityService;
import org.accessibilitymapper.service.PhotoStorageService;
import org.accessibilitymapper.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class ContributionController {
    private final FacilityService facilities;
    private final ReviewService reviews;
    private final PhotoStorageService photoStorage;

    public ContributionController(FacilityService facilities, ReviewService reviews,
                                  PhotoStorageService photoStorage) {
        this.facilities = facilities;
        this.reviews = reviews;
        this.photoStorage = photoStorage;
    }

    @GetMapping("/contribute")
    public String contribute(Model model) {
        model.addAttribute("facility", new Facility());
        model.addAttribute("categories", HomeController.CATEGORIES);
        return "contribute";
    }

    @PostMapping("/contribute")
    public String submitFacility(@Valid @ModelAttribute("facility") Facility facility, BindingResult errors,
                                 @RequestParam(required = false) MultipartFile photo, Model model,
                                 RedirectAttributes redirect) throws IOException {
        if (errors.hasErrors()) {
            model.addAttribute("categories", HomeController.CATEGORIES);
            return "contribute";
        }
        try {
            facility.setCoverImageUrl(photoStorage.store(photo));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("categories", HomeController.CATEGORIES);
            model.addAttribute("uploadError", ex.getMessage());
            return "contribute";
        }
        try {
            facilities.submit(facility);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("categories", HomeController.CATEGORIES);
            model.addAttribute("locationError", ex.getMessage());
            return "contribute";
        }
        redirect.addFlashAttribute("notice", "Thanks for adding to the map. Your place is in the verification queue.");
        return "redirect:/";
    }

    @PostMapping("/places/{id}/reviews")
    public String submitReview(@PathVariable Long id,
                               @RequestParam(required = false) Integer rating,
                               @RequestParam(required = false) String comments,
                               @RequestParam(required = false) String wheelchairRampReport,
                               @RequestParam(required = false) String workingElevatorReport,
                               @RequestParam(required = false) String accessibleWashroomReport,
                               @RequestParam(required = false) String tactilePavingReport,
                               @RequestParam(required = false) String wideDoorwaysReport,
                               @RequestParam(required = false) MultipartFile photo,
                               @AuthenticationPrincipal Account account,
                               RedirectAttributes redirect) throws IOException {
        if (rating == null || rating < 1 || rating > 5 || comments == null
                || comments.isBlank() || comments.length() > 2000) {
            redirect.addFlashAttribute("notice", "Add a rating from 1 to 5 and a visit note of up to 2,000 characters.");
            return "redirect:/places/" + id;
        }
        AccessibilityReport report;
        try {
            report = new AccessibilityReport(
                    parseStatus(wheelchairRampReport),
                    parseStatus(workingElevatorReport),
                    parseStatus(accessibleWashroomReport),
                    parseStatus(tactilePavingReport),
                    parseStatus(wideDoorwaysReport));
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("notice", ex.getMessage());
            return "redirect:/places/" + id;
        }
        Facility facility = facilities.approvedFacility(id);
        String photoUrl;
        try {
            photoUrl = photoStorage.store(photo);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("notice", ex.getMessage());
            return "redirect:/places/" + id;
        }
        reviews.submit(facility, account, rating, comments, photoUrl, report);
        redirect.addFlashAttribute("notice",
                "Your visit note and facility checks were sent to the Bangalore moderation queue.");
        return "redirect:/places/" + id;
    }

    private Boolean parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return switch (status) {
            case "available" -> true;
            case "unavailable" -> false;
            default -> throw new IllegalArgumentException("Choose a valid facility status for every update.");
        };
    }
}
