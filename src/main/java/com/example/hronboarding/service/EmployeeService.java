package com.example.hronboarding.service;

import com.example.hronboarding.model.entity.Employee;
import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(User user, LocalDate hireDate, String department, User manager) {
        if (employeeRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Employee record already exists for user: " + user.getEmail());
        }

        Employee employee = Employee.builder()
            .user(user)
            .hireDate(hireDate)
            .department(department)
            .manager(manager)
            .build();

        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeByUser(User user) {
        return employeeRepository.findByUser(user);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> getEmployeesByManager(User manager) {
        return employeeRepository.findByManager(manager);
    }

    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

}
