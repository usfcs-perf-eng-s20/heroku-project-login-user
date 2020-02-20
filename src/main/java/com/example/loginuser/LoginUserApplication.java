package com.example.loginuser;

import com.example.loginuser.model.Users;
import com.example.loginuser.service.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class LoginUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginUserApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository)
	{
		return args -> {
			userRepository.save(new Users("hello"));

		};
	}
}
