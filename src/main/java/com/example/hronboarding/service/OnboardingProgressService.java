package com.example.hronboarding.service;

import com.example.hronboarding.model.entity.*;
import com.example.hronboarding.model.enums.TaskStatus;
import com.example.hronboarding.repository.OnboardingProgressRepository;
import com.example.hronboarding.repository.ProgressTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingProgressService {

    private final OnboardingProgressRepository progressRepository;
    private final ProgressTaskRepository progressTaskRepository;

    public OnboardingProgressService(OnboardingProgressRepository progressRepository, ProgressTaskRepository progressTaskRepository) {
        this.progressRepository = progressRepository;
        this.progressTaskRepository = progressTaskRepository;
    }

    public OnboardingProgress startOnboarding(Employee employee, OnboardingTemplate template, LocalDate startDate) {
        Optional<OnboardingProgress> existing = progressRepository
            .findByEmployeeIdAndTemplateId(employee.getId(), template.getId());

        if (existing.isPresent()) {
            throw new RuntimeException("Onboarding already started for this employee and template");
        }

        LocalDate expectedEndDate = calculateExpectedEndDate(template, startDate);

        OnboardingProgress progress = OnboardingProgress.builder()
            .employee(employee)
            .template(template)
            .startDate(startDate)
            .expectedEndDate(expectedEndDate)
            .build();

        return progressRepository.save(progress);
    }

    public Optional<OnboardingProgress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }

    public List<OnboardingProgress> getProgressByEmployee(Long employeeId) {
        return progressRepository.findByEmployeeId(employeeId);
    }

    public List<OnboardingProgress> getProgressByTemplate(Long templateId) {
        return progressRepository.findByTemplateId(templateId);
    }

    public void updateTaskStatus(Long progressTaskId, TaskStatus status, String notes) {
        ProgressTask task = progressTaskRepository.findById(progressTaskId)
            .orElseThrow(() -> new RuntimeException("Progress task not found"));

        task.setStatus(status);
        task.setNotes(notes);

        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        progressTaskRepository.save(task);
    }

    public List<ProgressTask> getTasksByProgress(Long progressId) {
        return progressTaskRepository.findByProgressId(progressId);
    }

    public OnboardingProgress completeOnboarding(Long progressId) {
        OnboardingProgress progress = progressRepository.findById(progressId)
            .orElseThrow(() -> new RuntimeException("Progress not found"));

        progress.setActualEndDate(LocalDate.now());
        return progressRepository.save(progress);
    }

    private LocalDate calculateExpectedEndDate(OnboardingTemplate template, LocalDate startDate) {
        return startDate.plusDays(30);
    }

}
