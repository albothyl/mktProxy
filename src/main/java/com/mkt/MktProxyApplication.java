package com.mkt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.mkt")
public class MktProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MktProxyApplication.class, args);
	}
}
