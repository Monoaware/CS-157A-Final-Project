package edu.sjsu.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.library.dao.UserDAO;

import java.util.List;

@Controller
public class UserController {

    private final UserDAO userdao;

    public UserController(UserDAO userdao) {
        this.userdao = userdao;
    }

    @GetMapping("/users")
    public String listUsers(Model model){
        model.addAttribute("users", userdao.findAll());
        return "users";
    }
}
