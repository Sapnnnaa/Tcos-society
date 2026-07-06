package com.telco.members.tcos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;



//extends SpringBootServletInitializer
@SpringBootApplication
public class TcosApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(TcosApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TcosApplication.class);
	}
}
