package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SellerRedirect {
    @GetMapping("/seller.html")
    public String sellerPage() {
        return "seller"; 
    }
}

