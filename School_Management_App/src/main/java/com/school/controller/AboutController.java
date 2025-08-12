package com.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping({"/about/admin"})
    public String aboutAdmin(Model model){
        model.addAttribute("pageTitle","About Admin Portal");
        return "about/admin-about";
    }

    @GetMapping({"/about/teacher"})
    public String aboutTeacher(Model model){
        model.addAttribute("pageTitle","About Teacher Portal");
        return "about/teacher-about";
    }

    @GetMapping({"/about/student"})
    public String aboutStudent(Model model){
        model.addAttribute("pageTitle","About Student Portal");
        return "about/student-about";
    }
}
