package com.andpedroso.java.webcafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class WebcafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcafeApplication.class, args);
	}

}
