package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.sjsu.library.dao.HoldDAO;
import edu.sjsu.library.dao.UserDAO;

@Controller
public class HoldController {
    private final HoldDAO hdao;

    public HoldController(HoldDAO hdao) {
        this.hdao = hdao;
    }

    @GetMapping("/holds")
    public String listUsers(Model model){
        model.addAttribute("users", hdao.findAll());
        return "users";
    }
}
