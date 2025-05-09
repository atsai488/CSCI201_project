package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AddListingRedirect {
    @GetMapping("/createlisting")
    public String index() {
        return "create-listing";
    }
}