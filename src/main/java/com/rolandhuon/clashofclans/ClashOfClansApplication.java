package com.rolandhuon.clashofclans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClashOfClansApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClashOfClansApplication.class, args);
	}

}
