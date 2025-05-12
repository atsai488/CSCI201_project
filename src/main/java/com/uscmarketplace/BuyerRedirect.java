package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BuyerRedirect {
    @GetMapping("/buyeraccount.html")
    public String buyerPage() {
        return "buyer"; 
    }
}
