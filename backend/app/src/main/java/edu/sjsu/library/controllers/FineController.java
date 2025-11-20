package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.sjsu.library.dao.FineDAO;

@Controller
public class FineController {
    private final FineDAO fdao;

    public FineController(FineDAO fdao) {
        this.fdao = fdao;
    }

    @GetMapping("/fines")
    public String listUsers(Model model){
        model.addAttribute("users", fdao.findAll());
        return "users";
    }
}
