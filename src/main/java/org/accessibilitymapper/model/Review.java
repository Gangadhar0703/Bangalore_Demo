package org.accessibilitymapper.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String comments;

    @Size(max = 500)
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_approved", nullable = false)
    private boolean approved;

    @Column(name = "report_wheelchair_ramp")
    private Boolean wheelchairRampReport;
    @Column(name = "report_working_elevator")
    private Boolean workingElevatorReport;
    @Column(name = "report_accessible_washroom")
    private Boolean accessibleWashroomReport;
    @Column(name = "report_tactile_paving")
    private Boolean tactilePavingReport;
    @Column(name = "report_wide_doorways")
    private Boolean wideDoorwaysReport;

    protected Review() {}

    public Review(Facility facility, Account user, Integer rating, String comments, String photoUrl) {
        this(facility, user, rating, comments, photoUrl, null);
    }

    public Review(Facility facility, Account user, Integer rating, String comments, String photoUrl,
                  AccessibilityReport report) {
        this.facility = facility;
        this.user = user;
        this.rating = rating;
        this.comments = comments;
        this.photoUrl = photoUrl;
        this.createdAt = LocalDateTime.now();
        if (report != null) {
            this.wheelchairRampReport = report.wheelchairRamp();
            this.workingElevatorReport = report.workingElevator();
            this.accessibleWashroomReport = report.accessibleWashroom();
            this.tactilePavingReport = report.tactilePaving();
            this.wideDoorwaysReport = report.wideDoorways();
        }
    }

    public Long getId() { return id; }
    public Facility getFacility() { return facility; }
    public Account getUser() { return user; }
    public Integer getRating() { return rating; }
    public String getComments() { return comments; }
    public String getPhotoUrl() { return photoUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public Boolean getWheelchairRampReport() { return wheelchairRampReport; }
    public Boolean getWorkingElevatorReport() { return workingElevatorReport; }
    public Boolean getAccessibleWashroomReport() { return accessibleWashroomReport; }
    public Boolean getTactilePavingReport() { return tactilePavingReport; }
    public Boolean getWideDoorwaysReport() { return wideDoorwaysReport; }
}
