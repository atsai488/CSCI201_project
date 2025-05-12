package com.uscmarketplace;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellerAccountRedirect {

  @GetMapping("/selleraccount")
  public String sellerAccount(HttpSession session) {
    Long id = (Long) session.getAttribute("userId");
    return "redirect:/seller.html?sellerId=" + id;
  }
}
