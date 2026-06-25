package com.example.hronboarding.repository;

import com.example.hronboarding.model.entity.ProgressTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressTaskRepository extends JpaRepository<ProgressTask, Long> {
    List<ProgressTask> findByProgressId(Long progressId);
}
