package com.example.loginuser.controller;

//import com.example.loginuser.model.Users;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.loginuser.model.Users;
import com.example.loginuser.model.EdrForm;
import com.example.loginuser.service.UpdateService;
import com.example.loginuser.service.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
public class UserController {
    String loginCookieName = "isloggedIn";
    //Cookie cookie = new Cookie(loginCookieName, "true");
    EdrForm edr;
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
    	Instant start = Instant.now();
    	String hello = "hello";
        Instant stop = Instant.now();
        edr = new EdrForm("get", "/user", (int)Duration.between(start, stop).toMillis(), "200", "login-team", true, Long.toString(System.currentTimeMillis()), "test");
        saveEdr(edr);
        return hello;
    }

    //this is dummy api and u can see all users here--> remove it once we implement everything
    
    @GetMapping("/user")
    List<Users> getUser()
    {
    	Instant start = Instant.now();
        List<Users> result = (List<Users>) userRepository.findAll();
        Instant stop = Instant.now();
        edr = new EdrForm("get", "/user", (int)Duration.between(start, stop).toMillis(), "200", "login-team", true, Long.toString(System.currentTimeMillis()), "test");
        saveEdr(edr);
        return result;
    }

    @GetMapping("/isLoggedIn") //this is for other services to check 
//    whether user is logged in or not...we can do JWT in general and give them token if user is logged in
    public Map<String, Object> isLoggedIn(@RequestParam("userId") int userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    	Instant startTime = Instant.now(); //for save-edr
        List<Users> users  =  userRepository.findUserByID(userId);
        Map<String, Object> responseMap = new HashMap<>();
        if (users.size() >= 1) {
            responseMap.put("userLoggedIn", userRepository.isLoggedIn(userId));
        } else {
            //user doesn't exist
            response.sendError(500);
            System.out.println("Uer dones't exist");
        }
        //for Save-edr === START
        int responseCode = response.getStatus();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now();
        //System.out.println("users = "+ users.get(0).getUserName());
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login-service", successValue, Long.toString(System.currentTimeMillis()), users.get(0).getUserName());  
        saveEdr(edr);
        //END OF SAVE_EDR
        return responseMap;
    }


    @PostMapping(path = "/login", consumes ="application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {
    	Instant startTime = Instant.now(); //for save-edr
    	ObjectMapper mapper = new ObjectMapper();
    	List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            Users checkUser = mapper.readValue(jsonString, Users.class);
//           System.out.println("USERS = " + checkUser.getEmail() + " " + checkUser.getPassword());
            users = userRepository.findByEmailAndPassword(checkUser.getEmail(), checkUser.getPassword());
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
      //for Save-edr === START
        int responseCode = response.getStatus();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now();
        System.out.println("users = "+ users.get(0).getUserName());
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login-service", successValue, Long.toString(System.currentTimeMillis()), users.get(0).getUserName());  
        saveEdr(edr);
        //END OF SAVE_EDR
        return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
    }

    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request)  {
    	Instant startTime = Instant.now(); //for save-edr
        ObjectMapper mapper = new ObjectMapper();
        List<Users> users = null;
        Map<String, Object> responseMap = new HashMap<>();
        int id = -1;
        //Convert JSON to POJO
        try {
            Users newUser = mapper.readValue(jsonString, Users.class);
            userRepository.save(new Users(newUser.getUserName(), newUser.getEmail(), newUser.getAge(), newUser.getCity(), newUser.getPassword(), false));

            users = userRepository.findByEmail(newUser.getEmail());
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
        //START saveEDR code
        int responseCode = response.getStatus();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now(); //for save-edr
        System.out.println("users = "+ users.get(0).getUserName());
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login-service", successValue, Long.toString(System.currentTimeMillis()), users.get(0).getUserName());  
        saveEdr(edr);
        return new ResponseEntity(responseMap, HttpStatus.OK);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?>  userInf(@RequestParam("userId") int userId, HttpServletResponse response, HttpServletRequest request) {
    	Instant startTime = Instant.now(); //for save-edr
        Map<String, Object> responseMap = new HashMap<>();
        List<Users> users  =  userRepository.findUserByID(userId);
        if (users.size() >= 1) {
            Users user = users.get(0);
            responseMap.put("userName", user.getUserName());
            responseMap.put("email", user.getEmail());
            responseMap.put("age", user.getAge());
            responseMap.put("city", user.getCity());
        }
        //START saveEDR code
        int responseCode = response.getStatus();
        boolean successValue = false;
        if(responseCode == 200)
        {
        	successValue = true;
        }
        Instant stopTime = Instant.now(); //for save-edr
        System.out.println("users = "+ users.get(0).getUserName());
        edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int)Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "login-service", successValue, Long.toString(System.currentTimeMillis()), users.get(0).getUserName());  
        saveEdr(edr);
        return new ResponseEntity(responseMap, HttpStatus.OK);
    }

    
    //implement save-edr api 
//    @PostMapping(path = "/saveEdr", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveEdr(EdrForm edr) {
    	Gson gson = new Gson();
    	String jsonString = gson.toJson(edr); // this gives me request body
    	System.out.println(jsonString);
    	//now attached this requestBody with api of analytics team and send them. 
    	String pathForRequest = "https://prod-analytics-boot.herokuapp.com/";
    	//now need to create request and pass this "jsonString" as request body to /saveEdr path of Analytics team.
        try {
            sendPOST(jsonString);
        } catch (IOException e) {
            System.out.println("gg" + e.getStackTrace());
        }

		return null;
    	
    }

    private static void sendPOST(String jsonInputString) throws IOException {
        URL url = new URL ("https://prod-analytics-boot.herokuapp.com/saveEdr");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("response" + response.toString());
        }
    }


}
