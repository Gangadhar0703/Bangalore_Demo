package org.accessibilitymapper.service;

import org.accessibilitymapper.model.Account;
import org.accessibilitymapper.model.AccessibilityReport;
import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.model.Review;
import org.accessibilitymapper.exception.ResourceNotFoundException;
import org.accessibilitymapper.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class ReviewService {
    private final ReviewRepository reviews;

    public ReviewService(ReviewRepository reviews) {
        this.reviews = reviews;
    }

    @Transactional(readOnly = true)
    public List<Review> approvedFor(Long facilityId) {
        return reviews.findByFacilityIdAndApprovedTrueOrderByCreatedAtDesc(facilityId);
    }

    @Transactional(readOnly = true)
    public List<Review> pending() {
        return reviews.findByApprovedFalseOrderByCreatedAtAsc();
    }

    @Transactional
    public Review submit(Facility facility, Account user, Integer rating, String comments, String photoUrl,
                         AccessibilityReport report) {
        return reviews.save(new Review(facility, user, rating, comments.trim(), photoUrl, report));
    }

    @Transactional
    public void approve(Long id) {
        Review review = reviews.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found."));
        review.setApproved(true);
        Facility facility = review.getFacility();
        LocalDateTime checkedAt = LocalDateTime.now();
        boolean updated = false;
        if (review.getWheelchairRampReport() != null) {
            facility.setWheelchairRamp(review.getWheelchairRampReport());
            facility.setWheelchairRampCheckedAt(checkedAt);
            updated = true;
        }
        if (review.getWorkingElevatorReport() != null) {
            facility.setWorkingElevator(review.getWorkingElevatorReport());
            facility.setWorkingElevatorCheckedAt(checkedAt);
            updated = true;
        }
        if (review.getAccessibleWashroomReport() != null) {
            facility.setAccessibleWashroom(review.getAccessibleWashroomReport());
            facility.setAccessibleWashroomCheckedAt(checkedAt);
            updated = true;
        }
        if (review.getTactilePavingReport() != null) {
            facility.setTactilePaving(review.getTactilePavingReport());
            facility.setTactilePavingCheckedAt(checkedAt);
            updated = true;
        }
        if (review.getWideDoorwaysReport() != null) {
            facility.setWideDoorways(review.getWideDoorwaysReport());
            facility.setWideDoorwaysCheckedAt(checkedAt);
            updated = true;
        }
        if (updated) facility.setLastAccessibilityCheck(checkedAt);
    }

    @Transactional(readOnly = true)
    public double averageRating(Long facilityId) {
        return reviews.findByFacilityIdAndApprovedTrue(facilityId).stream()
                .mapToInt(Review::getRating).average().orElse(0.0);
    }
}
