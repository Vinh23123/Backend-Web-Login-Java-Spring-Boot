package com.Vinh.spring.login.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class PublicController {

    public String publicMessages(Model model){
        model.addAttribute("body", "nobody");
        return "response";
    }
}
