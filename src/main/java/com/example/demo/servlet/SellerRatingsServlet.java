package com.example.demo.servlet;

import com.example.demo.dto.RatingResponseDto;
import com.example.demo.dto.SellerRatingsDto;
import com.example.demo.model.Rating;
import com.example.demo.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.io.PrintWriter;


@WebServlet("/api/ratings/seller/*")
@Component
public class SellerRatingsServlet extends HttpServlet {
  @Autowired
  private RatingService ratingService;
  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Override
  public void init() throws ServletException {
    super.init();
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String path = req.getPathInfo();       
    Long sellerId = Long.valueOf(path.substring(1));

    List<Rating> ratings = ratingService.getRatingsForSeller(sellerId);
    double avg = ratingService.getAverageRating(sellerId);

    List<RatingResponseDto> dtoList = ratings.stream()
      .map(r -> new RatingResponseDto(
        r.getId(), r.getStars(), r.getComment(),
        r.getBuyer().getUsername(), r.getCreatedAt()
      ))
      .collect(Collectors.toList());

    SellerRatingsDto out = new SellerRatingsDto(avg, dtoList);

    resp.setContentType("application/json");
    try (PrintWriter writer = resp.getWriter()) {
      mapper.writeValue(writer, out);
      writer.flush();
    }
  }

}
