package com.example.hronboarding.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "template_tasks")
public class TemplateTask extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private OnboardingTemplate template;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Column(nullable = false)
    private Integer durationDays = 1;

    @NotNull
    @Column(nullable = false)
    private Integer taskOrder = 0;

    public TemplateTask() {
    }

    public TemplateTask(OnboardingTemplate template, String title, String description, Integer durationDays, Integer taskOrder) {
        this.template = template;
        this.title = title;
        this.description = description;
        this.durationDays = durationDays;
        this.taskOrder = taskOrder;
    }

    public static TemplateTaskBuilder builder() {
        return new TemplateTaskBuilder();
    }

    public OnboardingTemplate getTemplate() {
        return template;
    }

    public void setTemplate(OnboardingTemplate template) {
        this.template = template;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public static class TemplateTaskBuilder {
        private OnboardingTemplate template;
        private String title;
        private String description;
        private Integer durationDays = 1;
        private Integer taskOrder = 0;

        public TemplateTaskBuilder template(OnboardingTemplate template) {
            this.template = template;
            return this;
        }

        public TemplateTaskBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TemplateTaskBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TemplateTaskBuilder durationDays(Integer durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public TemplateTaskBuilder taskOrder(Integer taskOrder) {
            this.taskOrder = taskOrder;
            return this;
        }

        public TemplateTask build() {
            return new TemplateTask(template, title, description, durationDays, taskOrder);
        }
    }
}

