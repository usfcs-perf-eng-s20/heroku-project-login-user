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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {
    EdrForm edr;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UpdateService updateService;
   // private boolean willSaveEdr = false; --> remove this
    UserController(UserRepository userRepository) 
    {
        this.userRepository = userRepository;
    }
    //read config files and get values for config and all
    Config config = readJsonFile("config.json");
   
    //TODO:// this is dummy remove it while cleaning 
    @GetMapping("/user")
    List<Users> getUser()
    {
    	System.out.println(config.isAnalytics());
    	Instant start = Instant.now();
        List<Users> result = (List<Users>) userRepository.findAll();
        Instant stop = Instant.now();
        edr = new EdrForm("get", "/user", (int)Duration.between(start, stop).toMillis(), "200", "loginService/user", true, Long.toString(System.currentTimeMillis()), "test");
        if (config.isAnalytics()) {
            saveEdr(edr);
            System.out.println("call anaylistic team");
        } else {
            System.out.println("not call anaylistic team");
        }
        return result;
    }


//    @GetMapping("config")
//    void config(@RequestParam(defaultValue = "false", required=false) String analytics) {
//        if (analytics != null && analytics.equals("true")) {
//            willSaveEdr = true;
//            System.out.println("call anaylistic team");
//        } else {
//            willSaveEdr = false;
//            System.out.println("not call anaylistic team");
//        }
//    }
    
    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Config jsonString) {
    	Map<String, Object> responseMap = new HashMap<>();
    	config.setFaves(jsonString.isFaves());
    	config.setSearch(jsonString.isSearch());
    	config.setLogin(jsonString.isLogin());
    	config.setAnalytics(jsonString.isAnalytics());
    	responseMap.put("confirm" , true);
    	responseMap.put("message", "Config updated successfully");
    	//TODO:// i dont know if I need to change response code here or not ??
    	return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
    }

    //MOST IMPORTANT API
    @GetMapping("/isLoggedIn") //this is for other services to check
    public ResponseEntity<?> isLoggedIn( @RequestParam("userId") int userId, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    	Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
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
        if (config.isAnalytics()) {
            saveEdr(edr);
        }
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
            Users checkUser = mapper.readValue(jsonString, Users.class);
            users = userRepository.findByEmailAndPassword(checkUser.getEmail(), checkUser.getPassword());
            if (users.size() >= 1) {
            	userName = users.get(0).getUserName();
                System.out.println("+++");
                id = users.get(0).getUserId();
                responseMap.put("userId", id);
                int row = updateService.loggedIn(true, id);

            }else {

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
                //set failed login in cookie
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
        if (config.isAnalytics()) {
            saveEdr(edr);
        }
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
        if (config.isAnalytics()) {
            saveEdr(edr);
        }
        return new ResponseEntity(responseMap, responseStatus);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?>  userInf(@RequestParam("userId")  Integer[] userIds, HttpServletResponse response, HttpServletRequest request) {
        Instant startTime = Instant.now(); //for save-edr
        HttpStatus responseStatus = HttpStatus.OK;
        Map<String, Object> responseMap = new HashMap<>();
        List<Users> users  =  userRepository.findUserByIDs(userIds);
        List<Object> responseArray = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        if (users != null && users.size() >= 1) {
            for (int i = 0; i < users.size(); i++) {
                Users user = users.get(i);
                Map<String, Object> subResponseMap = new HashMap<>();
                subResponseMap.put("userName", user.getUserName());
                subResponseMap.put("email", user.getEmail());
                subResponseMap.put("age", user.getAge());
                subResponseMap.put("city", user.getCity());
                responseArray.add(subResponseMap);
                userNames.add(user.getUserName());
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
            if (config.isAnalytics()) {
                saveEdr(edr);
            }
        } else {
            for (String userName : userNames) {
                edr = new EdrForm(request.getMethod(), request.getRequestURI(), (int) Duration.between(startTime, stopTime).toMillis(), Integer.toString(responseCode), "loginService/getUserInfo", successValue, Long.toString(System.currentTimeMillis()), userName);
                if (config.isAnalytics()) {
                    saveEdr(edr);
                }
            }
        }
        return new ResponseEntity(responseMap, responseStatus);
    }

//    @PostMapping(path = "/saveEdr", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveEdr(EdrForm edr) {
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
        //TODO:// dont know what will come here, Are we suppose to pass some response or change it to void
        return null;
    }
    
    Config readJsonFile(String filename)
	{
		Config config = new Config();
		Gson gson = new Gson();
		System.out.println(filename);
		if(filename != null)
		{
			try {
				JsonReader reader = new JsonReader(new FileReader(filename));
				config = gson.fromJson(reader, Config.class);
			} catch (FileNotFoundException e) 
			{
				System.exit(0);
			}
		}
		return config;
	}
}
