package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.library.dao.TitleDAO;

import java.util.List;

@Controller
public class TitleController {
    private final TitleDAO titledao; 

    public TitleController(TitleDAO titledao) {
        this.titledao = titledao;
    }

    @GetMapping("/titles")
    public String listTitles(Model model) {
        model.addAttribute("titles", titledao.findAll());
        return "titles";
    }
}
