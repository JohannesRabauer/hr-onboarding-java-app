package com.example.hronboarding.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "onboarding_progress")
public class OnboardingProgress extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private OnboardingTemplate template;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate expectedEndDate;

    @Column
    private LocalDate actualEndDate;

    public OnboardingProgress() {
    }

    public OnboardingProgress(Employee employee, OnboardingTemplate template, LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate) {
        this.employee = employee;
        this.template = template;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.actualEndDate = actualEndDate;
    }

    public static OnboardingProgressBuilder builder() {
        return new OnboardingProgressBuilder();
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public OnboardingTemplate getTemplate() {
        return template;
    }

    public void setTemplate(OnboardingTemplate template) {
        this.template = template;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public static class OnboardingProgressBuilder {
        private Employee employee;
        private OnboardingTemplate template;
        private LocalDate startDate;
        private LocalDate expectedEndDate;
        private LocalDate actualEndDate;

        public OnboardingProgressBuilder employee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public OnboardingProgressBuilder template(OnboardingTemplate template) {
            this.template = template;
            return this;
        }

        public OnboardingProgressBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public OnboardingProgressBuilder expectedEndDate(LocalDate expectedEndDate) {
            this.expectedEndDate = expectedEndDate;
            return this;
        }

        public OnboardingProgressBuilder actualEndDate(LocalDate actualEndDate) {
            this.actualEndDate = actualEndDate;
            return this;
        }

        public OnboardingProgress build() {
            return new OnboardingProgress(employee, template, startDate, expectedEndDate, actualEndDate);
        }
    }
}

