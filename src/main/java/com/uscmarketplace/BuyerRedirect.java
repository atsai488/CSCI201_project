package com.uscmarketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class BuyerRedirect {
    @GetMapping("/buyeraccount")
    public String BuyerAccount(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("userId", userId);
        return "buyeraccount";
    }
}

