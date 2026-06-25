package com.example.hronboarding.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotNull
    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    public Employee() {
    }

    public Employee(User user, LocalDate hireDate, String department, User manager) {
        this.user = user;
        this.hireDate = hireDate;
        this.department = department;
        this.manager = manager;
    }

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public static class EmployeeBuilder {
        private User user;
        private LocalDate hireDate;
        private String department;
        private User manager;

        public EmployeeBuilder user(User user) {
            this.user = user;
            return this;
        }

        public EmployeeBuilder hireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public EmployeeBuilder department(String department) {
            this.department = department;
            return this;
        }

        public EmployeeBuilder manager(User manager) {
            this.manager = manager;
            return this;
        }

        public Employee build() {
            return new Employee(user, hireDate, department, manager);
        }
    }
}

