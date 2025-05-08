package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeRedirect {
    @GetMapping("/home")
    public String index() {
        return "home";
    }
}