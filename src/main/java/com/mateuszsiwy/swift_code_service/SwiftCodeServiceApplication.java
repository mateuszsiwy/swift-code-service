package com.mateuszsiwy.swift_code_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SwiftCodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftCodeServiceApplication.class, args);
	}


	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
