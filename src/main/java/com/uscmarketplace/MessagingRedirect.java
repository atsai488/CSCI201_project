package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessagingRedirect {
    @GetMapping("/messaging")
    public String index() {
        return "messaging";
    }
}