package com.example.loginuser.controller;

//import com.example.loginuser.model.Users;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.loginuser.model.Users;
import com.example.loginuser.service.UpdateService;
import com.example.loginuser.service.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    String loginCookieName = "isloggedIn";
    Cookie cookie = new Cookie(loginCookieName, "true");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpdateService updateService;
    //private BCryptPasswordEncoder bCryptPasswordEncoder; //to encrypt password

    UserController(UserRepository userRepository)//, //BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userRepository = userRepository;
        //this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    //this is dummy api and u can see all users here--> remove it once we implement everything
    @GetMapping("/user")
    List<Users> getUser()
    {
        return (List<Users>) userRepository.findAll();
    }

    @GetMapping("/isLoggedIn") //this is for other services to check 
//    whether user is logged in or not...we can do JWT in general and give them token if user is logged in
    public Map<String, Object> isLoggedIn(@RequestParam("userId") int userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        List<Users> users  =  userRepository.findUserByID(userId);
        Map<String, Object> responseMap = new HashMap<>();
        if (users.size() >= 1) {
            responseMap.put("userLoggedIn", userRepository.isLoggedIn(userId));
        } else {
            //user doesn't exist
            response.sendError(500);

            System.out.println("Uer dones't exist");
        }
        return responseMap;
    }


    @PostMapping(path = "/login", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {
    	ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            Users checkUser = mapper.readValue(jsonString, Users.class);
//           System.out.println("USERS = " + checkUser.getEmail() + " " + checkUser.getPassword());
            List<Users> users = userRepository.findByEmailAndPassword(checkUser.getEmail(), checkUser.getPassword());
            if (users.size() >= 1) {
                System.out.println("+++");
                id = users.get(0).getUserId();
                responseMap.put("userId", id);
                int row = updateService.loggedIn(true, id);
                if (row >= 1) {
                    System.out.println("change vlaue");
                }

            }else {
            	responseMap.put("error", "incorrect email / password");
                //set failed login in cookie
            }
            System.out.println("UserID " + id);

        } catch (JsonProcessingException e) {
            System.out.println("signup Json parse error");
        } catch (@SuppressWarnings("hiding") IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
    }





    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody String jsonString)  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            Users newUser = mapper.readValue(jsonString, Users.class);
            userRepository.save(new Users(newUser.getUserName(), newUser.getEmail(), newUser.getAge(), newUser.getCity(), newUser.getPassword(), false));

            List<Users> users = userRepository.findByEmail(newUser.getEmail());
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
    
    //implement save-edr api 
}
