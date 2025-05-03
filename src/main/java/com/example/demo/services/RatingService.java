package com.example.demo.service;

import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RatingService {
  private final RatingRepository ratingRepo;
  private final UserRepository userRepo;

  public RatingService(RatingRepository ratingRepo, UserRepository userRepo) {
    this.ratingRepo = ratingRepo;
    this.userRepo = userRepo;
  }

  @Transactional
  public Rating submitRating(Long buyerId, Long sellerId, int stars, String comment) {
    User buyer  = userRepo.findById(buyerId).orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
    User seller = userRepo.findById(sellerId).orElseThrow(() -> new IllegalArgumentException("Seller not found"));

    // Error handling, in case
    if (!"BUYER".equals(buyer.getRole()))
      throw new IllegalArgumentException("Only buyers can give ratings");
    if (!"SELLER".equals(seller.getRole()))
      throw new IllegalArgumentException("Can only rate seller accounts");
    if (buyerId.equals(sellerId))
      throw new IllegalArgumentException("Cannot rate yourself");

    Rating r = new Rating();
    r.setBuyer(buyer);
    r.setSeller(seller);
    r.setStars(stars);
    r.setComment(comment);
    return ratingRepo.save(r);
  }

  public List<Rating> getRatingsForSeller(Long sellerId) {
    return ratingRepo.findBySellerId(sellerId);
  }

  public double getAverageRating(Long sellerId) {
    Double avg = ratingRepo.findAverageStars(sellerId);
    return (avg != null ? avg : 0.0);
  }
}
