package edu.sjsu.library.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class HomeController {

    private static final String SESSION_USER_ID = "USER_ID";

    @GetMapping("/")
    public String home() {
        return "home";  // home.html
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            return "redirect:/"; // not logged in
        }
        return "dashboard"; // dashboard.html
    }

    @GetMapping("/dev/login-as/{id}")
    public String devLoginAs(@PathVariable int id, HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        session.setAttribute(SESSION_USER_ID, id);
        return "redirect:/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile"; 
    }


    @GetMapping("/loans")
    public String loans() {
        return "loans";
    }

    @GetMapping("/users")
    public String usersPage() {
        return "users"; 
    }

}
