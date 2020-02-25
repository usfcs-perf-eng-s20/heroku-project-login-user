package com.example.loginuser.controller;

import com.example.loginuser.model.User;
import com.example.loginuser.model.Users;
import com.example.loginuser.service.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody String jsonString)  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            User newUser = mapper.readValue(jsonString, User.class);
            userRepository.save(new Users(newUser.getUserName(), newUser.getEmail(), newUser.getAge(), newUser.getCity(), newUser.getPassword()));

            List<Users> users = userRepository.findByEmailAndPassword(newUser.getEmail());
            if (users.size() >= 1) {
                id = users.get(0).getUserId();
            }
            System.out.println("UserID " + id);

            responseMap.put("userId", id);
        } catch (JsonProcessingException e) {
            System.out.println("signup Json parse error");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(responseMap, HttpStatus.OK);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?>  userInf(@RequestParam("userId") int userId) {
        Map<String, Object> responseMap = new HashMap<>();
        List<Users> users  =  userRepository.findUserByID(userId);
        if (users.size() >= 1) {
            Users user = users.get(0);
            responseMap.put("userName", user.getUserName());
            responseMap.put("email", user.getEmail());
            responseMap.put("age", user.getAge());
            responseMap.put("city", user.getCity());

        }
        return new ResponseEntity(responseMap, HttpStatus.OK);
    }

    @GetMapping("/pageView")
    public String pageView() {
        return "";
    }
}
