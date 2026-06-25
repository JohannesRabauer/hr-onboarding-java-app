package com.example.hronboarding.repository;

import com.example.hronboarding.model.entity.OnboardingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingProgressRepository extends JpaRepository<OnboardingProgress, Long> {
    List<OnboardingProgress> findByEmployeeId(Long employeeId);

    List<OnboardingProgress> findByTemplateId(Long templateId);

    Optional<OnboardingProgress> findByEmployeeIdAndTemplateId(Long employeeId, Long templateId);
}
