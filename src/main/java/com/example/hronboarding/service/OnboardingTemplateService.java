package com.example.hronboarding.service;

import com.example.hronboarding.model.entity.OnboardingTemplate;
import com.example.hronboarding.model.entity.TemplateTask;
import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.repository.OnboardingTemplateRepository;
import com.example.hronboarding.repository.TemplateTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingTemplateService {

    private final OnboardingTemplateRepository templateRepository;
    private final TemplateTaskRepository taskRepository;

    public OnboardingTemplateService(OnboardingTemplateRepository templateRepository, TemplateTaskRepository taskRepository) {
        this.templateRepository = templateRepository;
        this.taskRepository = taskRepository;
    }

    public OnboardingTemplate createTemplate(String name, String description, User createdBy) {
        OnboardingTemplate template = OnboardingTemplate.builder()
            .name(name)
            .description(description)
            .createdBy(createdBy)
            .active(true)
            .version(1)
            .build();

        return templateRepository.save(template);
    }

    public Optional<OnboardingTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    public List<OnboardingTemplate> getAllActiveTemplates() {
        return templateRepository.findByActiveTrue();
    }

    public List<OnboardingTemplate> getTemplatesByCreator(User createdBy) {
        return templateRepository.findByCreatedByOrderByCreatedAtDesc(createdBy);
    }

    public OnboardingTemplate updateTemplate(OnboardingTemplate template) {
        return templateRepository.save(template);
    }

    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    public TemplateTask addTaskToTemplate(Long templateId, String title, String description, Integer durationDays, Integer taskOrder) {
        OnboardingTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));

        TemplateTask task = TemplateTask.builder()
            .template(template)
            .title(title)
            .description(description)
            .durationDays(durationDays)
            .taskOrder(taskOrder)
            .build();

        return taskRepository.save(task);
    }

    public List<TemplateTask> getTasksByTemplate(Long templateId) {
        return taskRepository.findByTemplateIdOrderByTaskOrder(templateId);
    }

}
