package com.example.loginuser.controller;


import com.example.loginuser.model.Users;
import com.example.loginuser.model.Config;
import com.example.loginuser.model.EdrForm;
import com.example.loginuser.service.UpdateService;
import com.example.loginuser.service.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@RestController
public class UserController {
    EdrForm edr;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UpdateService updateService;

    UserController(UserRepository userRepository) 
    {
        this.userRepository = userRepository;
    }
    boolean isFave = false;
    boolean isSearch = false;
    boolean isLogin = false;
    boolean isAnalytics = false;

    @GetMapping("/user")
    List<Users> getUser()
    {
        System.out.println("Analytics: " + isAnalytics);
        Instant start = Instant.now();
        List<Users> result = (List<Users>) userRepository.findAll();
        Instant stop = Instant.now();
        edr = new EdrForm("get", "/user", (int)Duration.between(start, stop).toMillis(), "200", "loginService/user", true, Long.toString(System.currentTimeMillis()), "test");
        saveEdr(edr);
        return result;
    }

//
    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Config jsonString) {
        Map<String, Object> responseMap = new HashMap<>();
        isFave =  jsonString.isFaves();
        isSearch =   jsonString.isSearch();
        isLogin = jsonString.isLogin();
        System.out.println("Analystics before: " + isAnalytics);
        isAnalytics =  jsonString.isAnalytics();
        System.out.println("Analystics after: " + isAnalytics);
        responseMap.put("confirm" , true);
        responseMap.put("IsAnalyticsOn" , isAnalytics);
        responseMap.put("message", "Config updated successfully");
        return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
    }


    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn( @RequestParam("userId") int userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    	  Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        //check user based on user id
        Users user  =  userRepository.findUserByID(userId);
        Map<String, Object> responseMap = new HashMap<>();
        if (user != null) {
            responseMap.put("userLoggedIn", userRepository.isLoggedIn(userId));
        } else {
            responseStatus = HttpStatus.BAD_REQUEST;
            responseMap.put("error" ,"user does not exist, please try again");
        }
        //for Save-edr === START
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
            successValue = true;
        }
        String userName = "";
        if (!responseMap.containsKey("error")) {
            userName = user.getUserName();
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/isLoggedIn", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        return new ResponseEntity<Object>(responseMap, responseStatus);
    }



    @PostMapping(path = "/logout", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> logout(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {
        Instant startTime = Instant.now(); //for save-edr
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        String userName = "";
        HttpStatus responseStatus = HttpStatus.OK;
        //Convert JSON to POJO
        try {
            //check if the user exists or not
            Users checkUser = mapper.readValue(jsonString, Users.class);
            int id = checkUser.getUserId();
            responseMap.put("userId", id);
            Date date = new Date();
            int row = updateService.logout(false, date, id);
            responseMap.put("logout success", "true");
        } catch (JsonProcessingException e) {
            responseStatus = HttpStatus.BAD_REQUEST;
            responseMap.put("logout success", "false");
            System.out.println("logout Json parse error");
        }
        //save Edr
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
        	successValue = true;
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        return new ResponseEntity<Object>(responseMap, responseStatus);
    }

    @PostMapping(path = "/login", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {
        Instant startTime = Instant.now(); //for save-edr
        ObjectMapper mapper = new ObjectMapper();
        List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        String userName = "";
        HttpStatus responseStatus = HttpStatus.OK;
        //Convert JSON to POJO
        try {
            //check if the user exists or not
            Users checkUser = mapper.readValue(jsonString, Users.class);
            users = userRepository.findByEmailAndPassword(checkUser.getEmail(), checkUser.getPassword());
            if (users.size() >= 1) {
                //user is found and log in current user
                userName = users.get(0).getUserName();
                id = users.get(0).getUserId();
                responseMap.put("userId", id);
                Date date = new Date();
                int row = updateService.loggedIn(true, date, id);
            }else {
                //user is not found
                userName = userRepository.findUserNameByEmail(checkUser.getEmail());
                //set responsecode
                if(userName == null || userName == "")
                {	response.setStatus(400);
                    responseStatus = HttpStatus.BAD_REQUEST;
                }
                else {
                    response.setStatus(401);
                    responseStatus = HttpStatus.UNAUTHORIZED;
                }
                responseMap.put("error", "incorrect email / password");
            }

        } catch (JsonProcessingException e) {
            System.out.println("signup Json parse error");
        }
        //save Edr
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
            successValue = true;
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        return new ResponseEntity<Object>(responseMap, responseStatus);
    }












    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request)  {
    	Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        ObjectMapper mapper = new ObjectMapper();
        List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            Users newUser = mapper.readValue(jsonString, Users.class);
            // check if user exists
            String userName  =  userRepository.findUserNameByEmail(newUser.getEmail());
            if (userName == null || userName.equals("")) {
                userRepository.save(new Users(newUser.getUserName(), newUser.getEmail(), newUser.getAge(), newUser.getCity(), newUser.getPassword(), false));
                users = userRepository.findByEmail(newUser.getEmail());
                if (users.size() >= 1) {
                    id = users.get(0).getUserId();
                    responseMap.put("userId", id);
                } else {
                    responseMap.put("error", "error when creating new user, please try again");
                    responseStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                responseMap.put("error", "user already exists, please try again");
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (IOException e) {
            responseMap.put("error", "some error occurs, please try again");
            responseStatus = HttpStatus.BAD_REQUEST;
        }
        //saveEDR code
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now(); //for save-edr
        String userName = "";
        if (!responseMap.containsKey("error")) {
            userName = users.get(0).getUserName();
        }
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/signup", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        return new ResponseEntity(responseMap, responseStatus);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?>  userInf(@RequestParam("userId")  Integer[] userIds, HttpServletResponse response, HttpServletRequest request) {
        Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        Map<String, Object> responseMap = new HashMap<>();
        List<Users> users  =  userRepository.findUserByIDs(userIds);
        List<Map<String, Object>> responseArray = new ArrayList<>();
        Map<Integer, Map<String, Object>> userIdMap = new HashMap<>();
        List<String> userNames = new ArrayList<>();
        if (users != null && users.size() >= 1) {
            for (int i = 0; i < users.size(); i++) {
                Users user = users.get(i);
                Map<String, Object> subResponseMap = new HashMap<>();
                subResponseMap.put("userName", user.getUserName());
                subResponseMap.put("email", user.getEmail());
                subResponseMap.put("age", user.getAge());
                subResponseMap.put("city", user.getCity());
                subResponseMap.put("userId", user.getUserId());
                userIdMap.put(user.getUserId(), subResponseMap);
            }

            for (int index = 0; index < userIds.length; index++) {
                if (userIdMap.containsKey(userIds[index])) {
                    Map<String, Object> subResponseMap = userIdMap.get(userIds[index]);
                    responseArray.add(subResponseMap);
                    userNames.add(subResponseMap.get("userName").toString());
                }
            }

            responseMap.put("users", responseArray);
        } else {
            responseMap.put("error", "user does not exist, please try again");
            responseStatus = HttpStatus.BAD_REQUEST;
        }
        //START saveEDR code
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now(); //for save-edr

        if (responseMap.containsKey("error")) {
            edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int) Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/getUserInfo", successValue, Long.toString(System.currentTimeMillis()), "");
            saveEdr(edr);
        } else {
            for (String userName : userNames) {
                edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int) Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/getUserInfo", successValue, Long.toString(System.currentTimeMillis()), userName);
                saveEdr(edr);
            }
        }
        return new ResponseEntity(responseMap, responseStatus);
    }


    public void saveEdr(EdrForm edr) {
        if (isAnalytics) {
            System.out.println("call anaylistic team");
            Gson gson = new Gson();
            String jsonString = gson.toJson(edr); // this gives me request body
            System.out.println("String = " + jsonString);
            try {
                URL url = new URL ("http://analytics-boot.herokuapp.com/saveEdr");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException e) {
                    System.out.println(e);
                }
                try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("response" + response.toString());
                } catch (IOException e) {
                    System.out.println(e);
                }
                System.out.println("response code from savedir" + con.getResponseCode());
            } catch (IOException e) {
                System.out.println("response form savedir" + 400);
                System.out.println( e.getStackTrace());

            }
        } else {
            System.out.println("not call anaylistic team");
        }


    }

}

