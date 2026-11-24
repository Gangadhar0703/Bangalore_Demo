package org.accessibilitymapper.repository;

import org.accessibilitymapper.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long>, JpaSpecificationExecutor<Facility> {
    List<Facility> findByApprovedTrueOrderByNameAsc();
    List<Facility> findByApprovedFalseOrderByIdAsc();
}
