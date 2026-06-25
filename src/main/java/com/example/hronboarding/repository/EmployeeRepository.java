package com.example.hronboarding.repository;

import com.example.hronboarding.model.entity.Employee;
import com.example.hronboarding.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);

    List<Employee> findByManager(User manager);
}
