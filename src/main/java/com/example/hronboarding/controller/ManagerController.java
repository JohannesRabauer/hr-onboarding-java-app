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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ManagerController {

    private final EmployeeService employeeService;
    private final OnboardingProgressService progressService;
    private final UserService userService;

    public ManagerController(EmployeeService employeeService, OnboardingProgressService progressService, UserService userService) {
        this.employeeService = employeeService;
        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping("/team")
    public String listTeam(Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName()).orElseThrow();
        List<Employee> team = employeeService.getEmployeesByManager(currentUser);
        model.addAttribute("team", team);
        return "manager/team";
    }

    @GetMapping("/employee/{employeeId}/progress")
    public String viewEmployeeProgress(@PathVariable Long employeeId, Model model) {
        List<OnboardingProgress> progressList = progressService.getProgressByEmployee(employeeId);
        model.addAttribute("progressList", progressList);
        return "manager/progress";
    }

    @GetMapping("/progress/{progressId}")
    public String viewProgressDetail(@PathVariable Long progressId, Model model) {
        OnboardingProgress progress = progressService.getProgressById(progressId)
            .orElseThrow(() -> new RuntimeException("Progress not found"));
        List<ProgressTask> tasks = progressService.getTasksByProgress(progressId);
        model.addAttribute("progress", progress);
        model.addAttribute("tasks", tasks);
        return "manager/progress-detail";
    }

}
