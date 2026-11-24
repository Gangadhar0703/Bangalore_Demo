package org.accessibilitymapper.service;

import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.exception.ResourceNotFoundException;
import org.accessibilitymapper.repository.FacilityRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class FacilityService {
    private final FacilityRepository facilities;

    public FacilityService(FacilityRepository facilities) {
        this.facilities = facilities;
    }

    @Transactional(readOnly = true)
    public List<Facility> search(String q, String category, String location, List<String> features) {
        Specification<Facility> spec = (root, query, cb) -> cb.and(
                cb.isTrue(root.get("approved")),
                cb.between(root.<Double>get("latitude"), 12.70, 13.20),
                cb.between(root.<Double>get("longitude"), 77.30, 77.90));
        if (hasText(q)) {
            String pattern = "%" + escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern, '\\'),
                    cb.like(cb.lower(root.get("category")), pattern, '\\'),
                    cb.like(cb.lower(root.get("address")), pattern, '\\')));
        }
        if (hasText(category) && !category.equalsIgnoreCase("All places")) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("category")),
                    category.trim().toLowerCase(Locale.ROOT)));
        }
        if (hasText(location)) {
            String pattern = "%" + escapeLike(location.trim().toLowerCase(Locale.ROOT)) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("address")), pattern, '\\'));
        }
        for (String feature : features) {
            String field = switch (feature) {
                case "ramp" -> "wheelchairRamp";
                case "elevator" -> "workingElevator";
                case "washroom" -> "accessibleWashroom";
                case "tactile" -> "tactilePaving";
                case "wide-doors" -> "wideDoorways";
                default -> null;
            };
            if (field != null) {
                spec = spec.and((root, query, cb) -> cb.isTrue(root.get(field)));
            }
        }
        return facilities.findAll(spec, Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional(readOnly = true)
    public Facility approvedFacility(Long id) {
        return facilities.findById(id).filter(Facility::isApproved)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found."));
    }

    @Transactional(readOnly = true)
    public List<Facility> approvedPlaces() {
        return facilities.findByApprovedTrueOrderByNameAsc();
    }

    @Transactional
    public Facility submit(Facility facility) {
        if (facility.getLatitude() < 12.70 || facility.getLatitude() > 13.20
                || facility.getLongitude() < 77.30 || facility.getLongitude() > 77.90
                || !facility.getAddress().toLowerCase(Locale.ROOT).contains("bangalore")) {
            throw new IllegalArgumentException("Only parks, theatres, and malls within Bangalore can be added.");
        }
        facility.setApproved(false);
        facility.setVerified(false);
        facility.setLastAccessibilityCheck(null);
        facility.setWheelchairRampCheckedAt(null);
        facility.setWorkingElevatorCheckedAt(null);
        facility.setAccessibleWashroomCheckedAt(null);
        facility.setTactilePavingCheckedAt(null);
        facility.setWideDoorwaysCheckedAt(null);
        return facilities.save(facility);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
