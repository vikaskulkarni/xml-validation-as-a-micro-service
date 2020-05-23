package com.validate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.validate.config.FileStorageProperties;

@SpringBootApplication
//@EnableEurekaClient
@EnableConfigurationProperties({ FileStorageProperties.class })
public class ValidationServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(com.validate.ValidationServicesApplication.class, args);
	}

}
