package com.example.hronboarding.repository;

import com.example.hronboarding.model.entity.TemplateTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateTaskRepository extends JpaRepository<TemplateTask, Long> {
    List<TemplateTask> findByTemplateIdOrderByTaskOrder(Long templateId);
}
