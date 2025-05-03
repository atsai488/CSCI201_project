package com.uscmarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class UscMarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UscMarketplaceApplication.class, args);
	}
}
