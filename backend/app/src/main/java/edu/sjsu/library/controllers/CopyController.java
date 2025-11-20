package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.sjsu.library.dao.CopyDAO;

@Controller
public class CopyController {
    private final CopyDAO cdao;

    public CopyController(CopyDAO cdao) {
        this.cdao = cdao;
    }

    @GetMapping("/copies")
    public String listUsers(Model model){
        model.addAttribute("users", cdao.findAll());
        return "users";
    }
}
