package com.meanymellow.youdontknowchampion.controller;

import com.meanymellow.youdontknowchampion.storage.StudentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class LoginController {
    private final StudentStorage studentStorage;

    @Autowired
    public LoginController(StudentStorage studentStorage) {
        this.studentStorage = studentStorage;
    }

    @GetMapping("/login")
    public String login(Model model) throws IOException {
        return "login";
    }

    @GetMapping("/")
    public String homepage(Model model) throws IOException {
        model.addAttribute("students", studentStorage.getAll());

        return "homepage";
    }
}
