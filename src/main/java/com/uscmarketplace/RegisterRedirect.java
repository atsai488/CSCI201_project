package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterRedirect {

	@GetMapping("/register")
	public String index() {
		return "register";
	}

}