package com.example.hronboarding.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "onboarding_templates")
public class OnboardingTemplate extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private boolean active = true;

    public OnboardingTemplate() {
    }

    public OnboardingTemplate(String name, String description, User createdBy, Integer version, boolean active) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.version = version;
        this.active = active;
    }

    public static OnboardingTemplateBuilder builder() {
        return new OnboardingTemplateBuilder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class OnboardingTemplateBuilder {
        private String name;
        private String description;
        private User createdBy;
        private Integer version = 1;
        private boolean active = true;

        public OnboardingTemplateBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OnboardingTemplateBuilder description(String description) {
            this.description = description;
            return this;
        }

        public OnboardingTemplateBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public OnboardingTemplateBuilder version(Integer version) {
            this.version = version;
            return this;
        }

        public OnboardingTemplateBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public OnboardingTemplate build() {
            return new OnboardingTemplate(name, description, createdBy, version, active);
        }
    }
}

