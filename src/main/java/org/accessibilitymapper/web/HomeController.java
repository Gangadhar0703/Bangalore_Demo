package org.accessibilitymapper.web;

import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.service.FacilityService;
import org.accessibilitymapper.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    public static final List<String> CATEGORIES = List.of("Parks", "Theatres", "Malls");
    private final FacilityService facilityService;
    private final ReviewService reviewService;

    public HomeController(FacilityService facilityService, ReviewService reviewService) {
        this.facilityService = facilityService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "All places") String category,
                       @RequestParam(defaultValue = "Bangalore") String location,
                       @RequestParam(defaultValue = "list") String view,
                       @RequestParam(required = false) List<String> features,
                       Model model) {
        List<String> selectedFeatures = features == null ? List.of() : features;
        List<Facility> results = facilityService.search(q, category, location, selectedFeatures);
        Map<Long, Double> ratings = results.stream().collect(java.util.stream.Collectors.toMap(
                Facility::getId, facility -> reviewService.averageRating(facility.getId())));
        model.addAttribute("places", results);
        model.addAttribute("ratings", ratings);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("location", location);
        model.addAttribute("features", selectedFeatures);
        model.addAttribute("view", "map".equals(view) ? "map" : "list");
        return "home";
    }

    @GetMapping("/places/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Facility facility = facilityService.approvedFacility(id);
        model.addAttribute("place", facility);
        model.addAttribute("reviews", reviewService.approvedFor(id));
        model.addAttribute("rating", reviewService.averageRating(id));
        return "place";
    }
}
