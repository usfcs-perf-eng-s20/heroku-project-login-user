package com.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Arrays;
import java.util.Map;

@Controller
public class MainController {
    @GetMapping("/{id}")
    public String index(@RequestHeader Map<String, String> headers, @PathVariable("id") int id) {
        return "index";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/isLoggedIn")
    public String isLoggedIn() {
        return "index";
    }

    @GetMapping("/login")
    public String loginCheck() {
        return "";
    }

    @GetMapping("/signup")
    public String signup() {
        return "";
    }

    @GetMapping("/getUserInfo")
    public String userInf() {
        return "";
    }

    @GetMapping("/pageView")
    public String pageView() {
        return "";
    }

    @GetMapping("/db")
    public String db(Model model) {
        model.addAttribute("records", Arrays.asList("A", "B", "C"));
        return "db";
    }
}
