package com.comcast.simpleadserver.main;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * SimpleAdServerApplication main class
 * 
 * @author nithya
 *
 */
@ComponentScan(basePackages="com.comcast.*")
@EnableAutoConfiguration
@SpringBootApplication
public class SimpleAdServerApplication {
	
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleAdServerApplication.class, args);
	}
	
}
