package com.example.hronboarding.controller;

import com.example.hronboarding.model.entity.OnboardingTemplate;
import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.service.OnboardingTemplateService;
import com.example.hronboarding.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_TEAM')")
public class HrController {

    private final OnboardingTemplateService templateService;
    private final UserService userService;

    public HrController(OnboardingTemplateService templateService, UserService userService) {
        this.templateService = templateService;
        this.userService = userService;
    }

    @GetMapping("/templates")
    public String listTemplates(Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName()).orElseThrow();
        List<OnboardingTemplate> templates = templateService.getTemplatesByCreator(currentUser);
        model.addAttribute("templates", templates);
        return "hr/templates/list";
    }

    @GetMapping("/templates/new")
    public String createTemplateForm() {
        return "hr/templates/form";
    }

    @PostMapping("/templates")
    public String saveTemplate(
        @RequestParam String name,
        @RequestParam String description,
        Authentication authentication
    ) {
        User currentUser = userService.getUserByEmail(authentication.getName()).orElseThrow();
        templateService.createTemplate(name, description, currentUser);
        return "redirect:/hr/templates";
    }

    @GetMapping("/templates/{id}")
    public String viewTemplate(@PathVariable Long id, Model model) {
        OnboardingTemplate template = templateService.getTemplateById(id)
            .orElseThrow(() -> new RuntimeException("Template not found"));
        model.addAttribute("template", template);
        model.addAttribute("tasks", templateService.getTasksByTemplate(id));
        return "hr/templates/view";
    }

    @PostMapping("/templates/{id}/tasks")
    public String addTask(
        @PathVariable Long id,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam Integer durationDays,
        @RequestParam Integer taskOrder
    ) {
        templateService.addTaskToTemplate(id, title, description, durationDays, taskOrder);
        return "redirect:/hr/templates/" + id;
    }

}
