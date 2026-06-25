package com.example.hronboarding.repository;

import com.example.hronboarding.model.entity.OnboardingTemplate;
import com.example.hronboarding.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingTemplateRepository extends JpaRepository<OnboardingTemplate, Long> {
    List<OnboardingTemplate> findByActiveTrue();

    List<OnboardingTemplate> findByCreatedByOrderByCreatedAtDesc(User createdBy);
}
