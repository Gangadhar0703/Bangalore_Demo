package org.accessibilitymapper.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String name;

    @NotBlank
    @Pattern(regexp = "Parks|Theatres|Malls")
    @Column(nullable = false, length = 60)
    private String category;

    @NotBlank
    @Size(max = 240)
    @Column(nullable = false, length = 240)
    private String address;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(nullable = false)
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(nullable = false)
    private Double longitude;

    @Column(name = "wheelchair_ramp", nullable = false)
    private boolean wheelchairRamp;
    @Column(name = "working_elevator", nullable = false)
    private boolean workingElevator;
    @Column(name = "accessible_washroom", nullable = false)
    private boolean accessibleWashroom;
    @Column(name = "tactile_paving", nullable = false)
    private boolean tactilePaving;
    @Column(name = "wide_doorways", nullable = false)
    private boolean wideDoorways;
    @Column(name = "is_verified", nullable = false)
    private boolean verified;
    @Column(name = "is_approved", nullable = false)
    private boolean approved;
    @Column(name = "last_accessibility_check")
    private LocalDateTime lastAccessibilityCheck;
    @Column(name = "wheelchair_ramp_checked_at")
    private LocalDateTime wheelchairRampCheckedAt;
    @Column(name = "working_elevator_checked_at")
    private LocalDateTime workingElevatorCheckedAt;
    @Column(name = "accessible_washroom_checked_at")
    private LocalDateTime accessibleWashroomCheckedAt;
    @Column(name = "tactile_paving_checked_at")
    private LocalDateTime tactilePavingCheckedAt;
    @Column(name = "wide_doorways_checked_at")
    private LocalDateTime wideDoorwaysCheckedAt;
    @Size(max = 500)
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    public Facility() {}

    public Facility(String name, String category, String address, double latitude, double longitude,
                    boolean wheelchairRamp, boolean workingElevator, boolean accessibleWashroom,
                    boolean tactilePaving, boolean wideDoorways, boolean verified, boolean approved,
                    String coverImageUrl) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wheelchairRamp = wheelchairRamp;
        this.workingElevator = workingElevator;
        this.accessibleWashroom = accessibleWashroom;
        this.tactilePaving = tactilePaving;
        this.wideDoorways = wideDoorways;
        this.verified = verified;
        this.approved = approved;
        this.coverImageUrl = coverImageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public boolean isWheelchairRamp() { return wheelchairRamp; }
    public boolean isWorkingElevator() { return workingElevator; }
    public boolean isAccessibleWashroom() { return accessibleWashroom; }
    public boolean isTactilePaving() { return tactilePaving; }
    public boolean isWideDoorways() { return wideDoorways; }
    public boolean isVerified() { return verified; }
    public boolean isApproved() { return approved; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public LocalDateTime getLastAccessibilityCheck() { return lastAccessibilityCheck; }
    public LocalDateTime getWheelchairRampCheckedAt() { return wheelchairRampCheckedAt; }
    public LocalDateTime getWorkingElevatorCheckedAt() { return workingElevatorCheckedAt; }
    public LocalDateTime getAccessibleWashroomCheckedAt() { return accessibleWashroomCheckedAt; }
    public LocalDateTime getTactilePavingCheckedAt() { return tactilePavingCheckedAt; }
    public LocalDateTime getWideDoorwaysCheckedAt() { return wideDoorwaysCheckedAt; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public void setWheelchairRamp(boolean wheelchairRamp) { this.wheelchairRamp = wheelchairRamp; }
    public void setWorkingElevator(boolean workingElevator) { this.workingElevator = workingElevator; }
    public void setAccessibleWashroom(boolean accessibleWashroom) { this.accessibleWashroom = accessibleWashroom; }
    public void setTactilePaving(boolean tactilePaving) { this.tactilePaving = tactilePaving; }
    public void setWideDoorways(boolean wideDoorways) { this.wideDoorways = wideDoorways; }
    public void setLastAccessibilityCheck(LocalDateTime lastAccessibilityCheck) {
        this.lastAccessibilityCheck = lastAccessibilityCheck;
    }
    public void setWheelchairRampCheckedAt(LocalDateTime checkedAt) { this.wheelchairRampCheckedAt = checkedAt; }
    public void setWorkingElevatorCheckedAt(LocalDateTime checkedAt) { this.workingElevatorCheckedAt = checkedAt; }
    public void setAccessibleWashroomCheckedAt(LocalDateTime checkedAt) { this.accessibleWashroomCheckedAt = checkedAt; }
    public void setTactilePavingCheckedAt(LocalDateTime checkedAt) { this.tactilePavingCheckedAt = checkedAt; }
    public void setWideDoorwaysCheckedAt(LocalDateTime checkedAt) { this.wideDoorwaysCheckedAt = checkedAt; }
}
