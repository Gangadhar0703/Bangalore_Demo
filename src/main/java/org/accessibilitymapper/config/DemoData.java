package org.accessibilitymapper.config;

import org.accessibilitymapper.model.Account;
import org.accessibilitymapper.model.AccessibilityReport;
import org.accessibilitymapper.model.Facility;
import org.accessibilitymapper.model.Review;
import org.accessibilitymapper.repository.AccountRepository;
import org.accessibilitymapper.repository.FacilityRepository;
import org.accessibilitymapper.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DemoData {
    @Bean
    CommandLineRunner seedDemoData(AppProperties properties, AccountRepository accounts,
                                   FacilityRepository facilities, ReviewRepository reviews,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (!properties.seedDemoData()
                    || facilities.findByApprovedTrueOrderByNameAsc().stream()
                    .anyMatch(facility -> facility.getLatitude() >= 12.70 && facility.getLatitude() <= 13.20
                            && facility.getLongitude() >= 77.30 && facility.getLongitude() <= 77.90)) {
                return;
            }
            Account admin = accounts.findByEmailIgnoreCase(properties.adminEmail()).orElse(null);
            if (admin == null) {
                accounts.save(new Account("Mapper Admin", properties.adminEmail(),
                        passwordEncoder.encode(properties.adminPassword()), "ADMIN"));
            } else if (!"ADMIN".equals(admin.getRole())) {
                admin.setRole("ADMIN");
            }
            Account member = accounts.findByEmailIgnoreCase(properties.memberEmail()).orElseGet(() -> accounts.save(
                    new Account("Alex Morgan", properties.memberEmail(),
                            passwordEncoder.encode(properties.memberPassword()), "USER")));

            Facility cubbonPark = demoFacility(facilities, "Cubbon Park", "Parks",
                    "Kasturba Road, Sampangi Rama Nagar, Bangalore", 12.9763, 77.5929,
                    true, false, true, true, true, true,
                    "https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=960&q=80");
            Facility lalbagh = demoFacility(facilities, "Lalbagh Botanical Garden", "Parks",
                    "Mavalli, Bangalore", 12.9507, 77.5848,
                    true, false, false, true, false, false,
                    "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=960&q=80");
            Facility orionMall = demoFacility(facilities, "Orion Mall", "Malls",
                    "Brigade Gateway, Dr Rajkumar Road, Bangalore", 13.0108, 77.5548,
                    true, true, true, true, true, true,
                    "https://images.unsplash.com/photo-1519567241046-7f570eee3ce6?auto=format&fit=crop&w=960&q=80");
            Facility ubCity = demoFacility(facilities, "UB City", "Malls",
                    "Vittal Mallya Road, Ashok Nagar, Bangalore", 12.9719, 77.6016,
                    true, true, true, false, true, false,
                    "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=960&q=80");
            Facility vegaCity = demoFacility(facilities, "PVR INOX Vega City", "Theatres",
                    "Vega City Mall, Bannerghatta Road, Bangalore", 12.9069, 77.6018,
                    true, true, true, false, true, true,
                    "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=960&q=80");
            Facility lido = demoFacility(facilities, "INOX Lido Mall", "Theatres",
                    "Lido Mall, Ulsoor Road, Bangalore", 12.9721, 77.6181,
                    true, true, true, true, true, false,
                    "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=960&q=80");
            addDemoReport(reviews, cubbonPark, member, 5,
                    "Checked the Kasturba Road entrance: step-free paths, accessible washrooms, and tactile paving were available along the main route.",
                    true, null, true, true, true);
            addDemoReport(reviews, orionMall, member, 5,
                    "The west entry ramp was clear. Lifts and the accessible washroom on the mall level were in use during my visit.",
                    true, true, true, null, true);
            addDemoReport(reviews, vegaCity, member, 4,
                    "Step-free mall entry and working lifts. The accessible washroom was open when I visited.",
                    true, true, true, null, true);
            addDemoReport(reviews, lalbagh, member, 4,
                    "A step-free route and tactile paving were available at the west gate. Washroom access still needs another check.",
                    true, null, false, true, null);
            addDemoReport(reviews, ubCity, member, 4,
                    "Ramps, lifts, and wide entrances were in good shape. Check the directory for the accessible washroom location.",
                    true, true, null, null, true);
            addDemoReport(reviews, lido, member, 4,
                    "Ramps, lifts, washrooms, and tactile guidance were easy to find. Adding a recent access report for cinema visitors.",
                    true, true, true, true, true);
        };
    }

    private Facility demoFacility(FacilityRepository facilities, String name, String category, String address,
                                  double latitude, double longitude, boolean ramp, boolean elevator,
                                  boolean washroom, boolean tactile, boolean wideDoors, boolean verified,
                                  String image) {
        Facility facility = new Facility(name, category, address, latitude, longitude,
                ramp, elevator, washroom, tactile, wideDoors, verified, true, image);
        LocalDateTime checkedAt = LocalDateTime.now().minusDays(2);
        facility.setLastAccessibilityCheck(checkedAt);
        facility.setWheelchairRampCheckedAt(checkedAt);
        facility.setWorkingElevatorCheckedAt(checkedAt);
        facility.setAccessibleWashroomCheckedAt(checkedAt);
        facility.setTactilePavingCheckedAt(checkedAt);
        facility.setWideDoorwaysCheckedAt(checkedAt);
        return facilities.save(facility);
    }

    private void addDemoReport(ReviewRepository reviews, Facility facility, Account member, int rating,
                               String comments, Boolean ramp, Boolean elevator, Boolean washroom,
                               Boolean tactile, Boolean wideDoors) {
        Review review = new Review(facility, member, rating, comments, null,
                new AccessibilityReport(ramp, elevator, washroom, tactile, wideDoors));
        review.setApproved(true);
        reviews.save(review);
    }
}
