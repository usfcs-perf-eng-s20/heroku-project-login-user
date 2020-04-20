package com.example.loginuser.controller;


import com.example.loginuser.model.*;
import com.example.loginuser.service.UpdateService;
import com.example.loginuser.service.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
public class UserController {
    EdrForm edr;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UpdateService updateService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    //testing loader.io for production
    @GetMapping("/loaderio-e6333d328b79c44bd3b57d59c14ef283")
    public String verifyLoaderIO() {
        return "loaderio-e6333d328b79c44bd3b57d59c14ef283";
    }
    //testing loader.io for testing environment
    @GetMapping("/loaderio-403761023865bb2e806c58aac59dd063")
    public String verifyLoaderIO_test() {
        return "loaderio-403761023865bb2e806c58aac59dd063";
    }

    @GetMapping("/loaderio-38eebd0bc479135b5108105cb8105ca3")
    public String verifyLoaderIOProduction2() {
        return "loaderio-38eebd0bc479135b5108105cb8105ca3";
    }
    
    //loader.io for the account set up byProfessor
    @GetMapping("loaderio-ed7a9189a064008ce181d27ebc3edbc9")
    public String verifyLoaderIOProductionByProfessor() {
        return "loaderio-ed7a9189a064008ce181d27ebc3edbc9";
    }

    boolean isFave = false;
    boolean isSearch = false;
    boolean isLogin = false;
    boolean isAnalytics = false;
    

    @GetMapping("/user")
    List<Users> getUser()
    {
        Instant start = Instant.now();
        List<Users> result = (List<Users>) userRepository.findAll();
        Instant stop = Instant.now();
        edr = new EdrForm("get", "/user", (int)Duration.between(start, stop).toMillis(), "200", "login", true, Long.toString(System.currentTimeMillis()), "test");
        saveEdr(edr);
        return result;
    }

    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Config jsonString) {
        Map<String, Object> responseMap = new HashMap<>();
        isFave = jsonString.isFaves();
        isSearch = jsonString.isSearch();
        isLogin = jsonString.isLogin();
        isAnalytics = jsonString.isAnalytics();
        responseMap.put("confirm" , true);
        responseMap.put("IsAnalyticsOn" , isAnalytics);
        responseMap.put("message", "Config updated successfully");
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, HttpStatus.OK);
    }


    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn( @RequestParam("userId") int userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        Map<String, Object> responseMap = new HashMap<>();
        Users user = null;
        String message = "";

        //check user based on user id
        try {
            user = userRepository.findUserByID(userId);
            if (user != null) {
                responseMap.put("userLoggedIn", userRepository.isLoggedIn(userId));
                message = "Checking user login succeed";
            } else {
                responseStatus = HttpStatus.BAD_REQUEST;
                message = "user does not exist, please try again";
                responseMap.put("error" , message);
            }
        } catch (Exception e) {
            message = "Querying the existing user failed";
        }

        //for Save-edr === START
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
            successValue = true;
        }
        String userName = "";
        if (!responseMap.containsKey("error") && user != null) {
            userName = user.getUserName();
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);

        logger.info(new LogErrorMessage("login", "isLoggedIn", (int)Duration.between(startTime, stopTime).toMillis(), responseCode, message).toString());
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, responseStatus);
    }



    @PostMapping(path = "/logout", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> logout(@RequestBody String jsonString2, HttpServletResponse response, HttpServletRequest request) {
        String jsonString = paramJson(jsonString2);
        Instant startTime = Instant.now(); //for save-edr
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        String userName = "";
        HttpStatus responseStatus = HttpStatus.OK;
        String message = "";

        //Convert JSON to POJO
        try {
            //check if the user exists or not
            Users checkUser = mapper.readValue(jsonString, Users.class);
            int id = checkUser.getUserId();
            responseMap.put("userId", id);
            Date date = new Date();
            int row = updateService.logout(false, date, id);
            message = "logout succeed";
            responseMap.put(message, "true");
        } catch (JsonProcessingException e) {
            responseStatus = HttpStatus.BAD_REQUEST;
            responseMap.put("logout success", "false");
            message = "Json parse error";
        } catch (Exception e) {
            message = "Updating the existing user failed";
        }
        //save Edr
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
        	successValue = true;
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        logger.info(new LogErrorMessage("login","logout", (int)Duration.between(startTime, stopTime).toMillis(), responseCode, message).toString());
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, responseStatus);
    }

    @PostMapping(path = "/login", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody String jsonString2, HttpServletResponse response, HttpServletRequest request) {
        String jsonString = paramJson(jsonString2);
        Instant startTime = Instant.now(); //for save-edr
        List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        String userName = "";
        HttpStatus responseStatus = HttpStatus.OK;
        String message = "";

        try {
            //check if the user exists or not
            Gson gson = new Gson();
        	  Login user = gson.fromJson(jsonString, Login.class);
            users = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
            if (users.size() >= 1) {
                //user is found and log in current user
                userName = users.get(0).getUserName();
                id = users.get(0).getUserId();
                responseMap.put("userId", id);
                Date date = new Date();
                int row = updateService.loggedIn(true, date, id);
                message = "Login succeed";
            }else {
                //user is not found
                userName = userRepository.findUserNameByEmail(user.getEmail());
                //set responsecode
                if(userName == null || userName == "") {
                    response.setStatus(400);
                    responseStatus = HttpStatus.BAD_REQUEST;
                }
                else {
                    response.setStatus(400);
                    responseStatus = HttpStatus.BAD_REQUEST;
                }
                message = "incorrect email / password";
                responseMap.put("error", message);
            }

        } catch (JsonSyntaxException e) {
            response.setStatus(400);
            responseStatus = HttpStatus.BAD_REQUEST;
            message = "Json parse error";
        } catch (Exception e) {
            response.setStatus(400);
            responseStatus = HttpStatus.BAD_REQUEST;
            message = "Updating the existing user failed";
        }

        //save Edr
        int responseCode = responseStatus.value();
        boolean successValue = false;
        if(responseCode == 200) {
            successValue = true;
        }
        Instant stopTime = Instant.now();
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        logger.info(new LogErrorMessage("login", "login", (int)Duration.between(startTime, stopTime).toMillis(), responseCode, message).toString());
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, responseStatus);
    }

    //helper function to convert query parameter to json format
    public static String paramJson(String paramIn) {
        paramIn = paramIn.replaceAll("=", "\":\"");
        paramIn = paramIn.replaceAll("&", "\",\"");
        return paramIn;
    }

    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody String jsonString2, HttpServletResponse response, HttpServletRequest request)  {
        String jsonString = paramJson(jsonString2);
    	  Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        ObjectMapper mapper = new ObjectMapper();
        List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        String message = "";

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
                    message = "signup succeed";
                } else {
                    message = "error when creating new user, please try again";
                    responseMap.put("error", message);
                    responseStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                message = "user already exists, please try again";
                responseMap.put("error", message);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            responseMap.put("error", "some error occurs, please try again");
            responseStatus = HttpStatus.BAD_REQUEST;
            message = "Inserting a new user failed";
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
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), userName);
        saveEdr(edr);
        logger.info(new LogErrorMessage("login","signup", (int)Duration.between(startTime, stopTime).toMillis(), responseCode, message).toString());
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, responseStatus);
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
        String message = "";

        try {
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
                        userIdMap.remove(userIds[index]);
                    }
                }

                responseMap.put("users", responseArray);
                message = "getUserInfo succeed";
            } else {
                message = "user does not exist, please try again";
                responseMap.put("error", message);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            responseMap.put("error", "some error occurs, please try again");
            responseStatus = HttpStatus.BAD_REQUEST;
            message = "Querying a list of users failed";
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
            edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int) Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), "");
            saveEdr(edr);
        } else {
            for (String userName : userNames) {
                edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int) Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login", successValue, Long.toString(System.currentTimeMillis()), userName);
                saveEdr(edr);
            }
        }
        logger.info(new LogErrorMessage("login","getUserInfo", (int)Duration.between(startTime, stopTime).toMillis(), responseCode, message).toString());
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<Object>(responseMap, responseHeaders, responseStatus);
    }



    public void saveEdr(EdrForm edr) {
    	//System.out.println(edr.getServiceName());
        if (isAnalytics) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(edr); // this gives me request body
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
                    logger.error("saveEdr: write to stream error" + e);
                }
                try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                } catch (IOException e) {
                    logger.error("saveEdr: get response error" + e);
                }
            } catch (IOException e) {
                logger.error("saveEdr faield" + e);
            }
        }

    }

}

