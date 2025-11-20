package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.sjsu.library.dao.BookRecordDAO;

@Controller
public class BookRecordController {
    private final BookRecordDAO brdao;

    public BookRecordController(BookRecordDAO brdao) {
        this.brdao = brdao;
    }

    @GetMapping("/bookrecords")
    public String listUsers(Model model){
        model.addAttribute("users", brdao.findAll());
        return "users";
    }
}
