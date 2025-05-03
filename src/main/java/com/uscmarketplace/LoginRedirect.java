package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LoginRedirect {

	@GetMapping("/login")
	public String index() {
		return "login";
	}

}