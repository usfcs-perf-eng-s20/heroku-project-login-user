package com.example.loginuser;

import com.example.loginuser.model.Users;
import com.example.loginuser.service.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
//@EnableJpaRepositories(basePackages="com.example.loginuser")
//@EntityScan(basePackages="com.example.loginuser")
//@ComponentScan(basePackages="com.example.loginuser")
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
