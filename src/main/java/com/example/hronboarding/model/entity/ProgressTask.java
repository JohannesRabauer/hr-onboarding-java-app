package com.example.hronboarding.model.entity;

import com.example.hronboarding.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress_tasks")
public class ProgressTask extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", nullable = false)
    private OnboardingProgress progress;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_task_id", nullable = false)
    private TemplateTask templateTask;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    @Column
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String notes;

    public ProgressTask() {
    }

    public ProgressTask(OnboardingProgress progress, TemplateTask templateTask, TaskStatus status, LocalDateTime completedAt, String notes) {
        this.progress = progress;
        this.templateTask = templateTask;
        this.status = status;
        this.completedAt = completedAt;
        this.notes = notes;
    }

    public static ProgressTaskBuilder builder() {
        return new ProgressTaskBuilder();
    }

    public OnboardingProgress getProgress() {
        return progress;
    }

    public void setProgress(OnboardingProgress progress) {
        this.progress = progress;
    }

    public TemplateTask getTemplateTask() {
        return templateTask;
    }

    public void setTemplateTask(TemplateTask templateTask) {
        this.templateTask = templateTask;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static class ProgressTaskBuilder {
        private OnboardingProgress progress;
        private TemplateTask templateTask;
        private TaskStatus status = TaskStatus.NOT_STARTED;
        private LocalDateTime completedAt;
        private String notes;

        public ProgressTaskBuilder progress(OnboardingProgress progress) {
            this.progress = progress;
            return this;
        }

        public ProgressTaskBuilder templateTask(TemplateTask templateTask) {
            this.templateTask = templateTask;
            return this;
        }

        public ProgressTaskBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public ProgressTaskBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public ProgressTaskBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ProgressTask build() {
            return new ProgressTask(progress, templateTask, status, completedAt, notes);
        }
    }
}

