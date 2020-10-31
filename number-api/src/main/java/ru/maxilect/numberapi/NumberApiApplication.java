package ru.maxilect.numberapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class NumberApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NumberApiApplication.class, args);
	}
}
