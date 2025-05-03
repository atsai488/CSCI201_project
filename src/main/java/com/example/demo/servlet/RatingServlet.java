package com.example.demo.servlet;

import com.example.demo.dto.RatingDto;
import com.example.demo.dto.RatingResponseDto;
import com.example.demo.model.Rating;
import com.example.demo.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;

@WebServlet("/api/ratings")
@Component
public class RatingServlet extends HttpServlet {

    @Autowired
    private RatingService ratingService;

    private ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ==== STUB SESSION-USER FOR TESTING ====
        Long buyerId = (Long) req.getSession().getAttribute("userId");
        if (buyerId == null) {
            buyerId = 1L;
            req.getSession().setAttribute("userId", buyerId);
        }
        // =======================================

        // parse the incoming JSON
        RatingDto dto = mapper.readValue(req.getReader(), RatingDto.class);

       
        Rating saved = ratingService.submitRating(
            buyerId,
            dto.getSellerId(),
            dto.getStars(),
            dto.getComment()
        );

        // build response DTO
        RatingResponseDto out = new RatingResponseDto(
            saved.getId(),
            saved.getStars(),
            saved.getComment(),
            saved.getBuyer().getUsername(),
            saved.getCreatedAt()
        );

        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), out);
    }
}
