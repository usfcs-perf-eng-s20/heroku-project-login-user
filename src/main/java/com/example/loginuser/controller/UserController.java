package com.example.loginuser.controller;

import com.example.loginuser.model.Users;
import com.example.loginuser.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/user")
    List<Users> getUser()
    {
        return (List<Users>) userRepository.findAll();
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
}
