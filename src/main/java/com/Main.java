/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com;

import com.service.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


//Controller
@SpringBootApplication
public class Main {

//  @Value("${spring.datasource.url}")
//  private String dbUrl;
//
//  @Autowired
//  private ;




  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  ApplicationRunner applicationRunner(UserRepository userRepository)
  {
    return args -> {};
  }

//  @RequestMapping("/")
//  String index() {
//    return "index";
//  }
//  //User user = new User();
//  @RequestMapping("/hello")
//  String hello(Map<String, Object> model) {
//    RelativisticModel.select();
//    Amount<Mass> m = Amount.valueOf("12 GeV").to(KILOGRAM);
//    //model.put("DATA = ",user.getUserName());
//    model.put("science", "E=mc^2: 12 GeV = " + m.toString());
//    return "hello";
//  }
//
//  ///isLoggedIn
//  @RequestMapping(value ="/isLoggedIn",produces = "application/json")
//  public String loggedIn(HttpServletRequest request){
//    String test = request.getRequestURI(); //request get id
//    throw new ResponseStatusException(
//        HttpStatus.UNAUTHORIZED, "Unauthorized user");
//  }
//
//
//  ///login
//  @PostMapping(path= "/login", consumes = "application/json", produces = "application/json")
//  public ResponseEntity<Object> addEmployee( ) throws Exception
//  {
//    throw new ResponseStatusException(
//        HttpStatus.NOT_FOUND, "Not found");
//  }
//
//  ///signup
//  @RequestMapping(value ="/signup",produces = "application/json")
//  public String signup(HttpServletRequest request){
//    String test = request.getRequestURI(); //request get id
//    throw new ResponseStatusException(
//        HttpStatus.FORBIDDEN, "duplicate user");
//  }
//
//  ///getUserInfo
//  @RequestMapping(value ="/getUserInfo",produces = "application/json")
//  public String getUserInfo(HttpServletRequest request){
//    String test = request.getRequestURI();
//    throw new ResponseStatusException(
//        HttpStatus.NOT_FOUND, "user is not found");
//  }
//
//
//
//  ///getUserInfo
//  @RequestMapping(value ="/pageView",produces = "application/json")
//  public String getPageView(HttpServletRequest request){
//    String test = request.getRequestURI();
//    throw new ResponseStatusException(
//        HttpStatus.SERVICE_UNAVAILABLE, "This server is not accessible");
//  }
//
//
//
//
//
//
//  @RequestMapping("/db")
//  String db(Map<String, Object> model) {
//    try (Connection connection = dataSource.getConnection()) {
//      Statement stmt = connection.createStatement();
//      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
//      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
//      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
//
//      ArrayList<String> output = new ArrayList<String>();
//      while (rs.next()) {
//        output.add("Read from DB: " + rs.getTimestamp("tick"));
//      }
//
//      model.put("records", output);
//      return "db";
//    } catch (Exception e) {
//      model.put("message", e.getMessage());
//      return "error";
//    }
//  }

//  @Bean
//  public DataSource dataSource() throws SQLException {
//    if (dbUrl == null || dbUrl.isEmpty()) {
//      return new HikariDataSource();
//    } else {
//      HikariConfig config = new HikariConfig();
//      config.setJdbcUrl(dbUrl);
//      return new HikariDataSource(config);
//    }
//  }

}
