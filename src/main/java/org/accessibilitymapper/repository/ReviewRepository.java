package org.accessibilitymapper.repository;

import org.accessibilitymapper.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r join fetch r.user where r.facility.id = :facilityId "
            + "and r.approved = true order by r.createdAt desc")
    List<Review> findByFacilityIdAndApprovedTrueOrderByCreatedAtDesc(@Param("facilityId") Long facilityId);
    @Query("select r from Review r join fetch r.user join fetch r.facility "
            + "where r.approved = false order by r.createdAt asc")
    List<Review> findByApprovedFalseOrderByCreatedAtAsc();
    List<Review> findByFacilityIdAndApprovedTrue(Long facilityId);
}
