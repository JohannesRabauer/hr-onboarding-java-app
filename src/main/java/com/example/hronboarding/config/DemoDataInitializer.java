package com.example.hronboarding.config;

import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.model.enums.Role;
import com.example.hronboarding.service.EmployeeService;
import com.example.hronboarding.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final EmployeeService employeeService;

    public DemoDataInitializer(UserService userService, EmployeeService employeeService) {
        this.userService = userService;
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) {
        createDemoUser("hr@example.com", "password", "Helen", "Ross", Role.HR_TEAM);
        createDemoUser("manager@example.com", "password", "Mark", "Miller", Role.MANAGER);
        User employeeUser = createDemoUser("employee@example.com", "password", "Emma", "Parker", Role.EMPLOYEE);
        User managerUser = userService.getUserByEmail("manager@example.com").orElseThrow();

        if (employeeService.getEmployeeByUser(employeeUser).isEmpty()) {
            employeeService.createEmployee(employeeUser, LocalDate.now().minusDays(7), "Operations", managerUser);
        }
    }

    private User createDemoUser(String email, String password, String firstName, String lastName, Role role) {
        return userService.getUserByEmail(email)
            .orElseGet(() -> userService.createUser(email, password, firstName, lastName, role));
    }
}
