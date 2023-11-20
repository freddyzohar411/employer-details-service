package com.avensys.rts.employerdetailsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmployerDetailsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployerDetailsServiceApplication.class, args);
	}

}
