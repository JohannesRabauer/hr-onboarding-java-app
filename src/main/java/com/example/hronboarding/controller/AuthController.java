package com.example.hronboarding.controller;

import com.example.hronboarding.model.entity.User;
import com.example.hronboarding.model.enums.Role;
import com.example.hronboarding.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String firstName,
        @RequestParam String lastName
    ) {
        try {
            userService.createUser(email, password, firstName, lastName, Role.EMPLOYEE);
            return "redirect:/auth/login?success";
        } catch (RuntimeException e) {
            return "redirect:/auth/register?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

}
