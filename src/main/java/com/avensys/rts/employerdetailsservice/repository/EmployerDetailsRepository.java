package com.avensys.rts.employerdetailsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.employerdetailsservice.entity.EmployerDetailsEntity;

import java.util.List;

public interface EmployerDetailsRepository extends JpaRepository<EmployerDetailsEntity, Integer> {
    List<EmployerDetailsEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);
}
