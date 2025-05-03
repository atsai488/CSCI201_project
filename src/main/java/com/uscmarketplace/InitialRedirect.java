package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class InitialRedirect {
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}