package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ListingRedirect {
    @GetMapping("/listing/{id}")
    public String index() {
        return "listing";
    }
}