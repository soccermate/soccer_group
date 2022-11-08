package com.example.soccergroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
public class SoccerGroupApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoccerGroupApplication.class, args);
	}

}
