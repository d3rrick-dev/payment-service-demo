package com.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoPaymentServiceApplication.class, args);
	}

}
