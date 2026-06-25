package com.example.hronboarding.controller;

import com.example.hronboarding.model.entity.Employee;
import com.example.hronboarding.model.entity.OnboardingProgress;
import com.example.hronboarding.model.entity.ProgressTask;
import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.service.EmployeeService;
import com.example.hronboarding.service.OnboardingProgressService;
import com.example.hronboarding.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final OnboardingProgressService progressService;
    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, OnboardingProgressService progressService, UserService userService) {
        this.employeeService = employeeService;
        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName()).orElseThrow();
        Employee employee = employeeService.getEmployeeByUser(currentUser)
            .orElseThrow(() -> new RuntimeException("Employee record not found"));

        List<OnboardingProgress> progressList = progressService.getProgressByEmployee(employee.getId());
        model.addAttribute("progressList", progressList);
        return "employee/dashboard";
    }

    @GetMapping("/progress/{progressId}")
    public String viewProgress(@PathVariable Long progressId, Model model) {
        OnboardingProgress progress = progressService.getProgressById(progressId)
            .orElseThrow(() -> new RuntimeException("Progress not found"));
        List<ProgressTask> tasks = progressService.getTasksByProgress(progressId);
        model.addAttribute("progress", progress);
        model.addAttribute("tasks", tasks);
        return "employee/progress";
    }

}
