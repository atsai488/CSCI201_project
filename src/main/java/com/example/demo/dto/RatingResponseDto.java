package com.example.demo.dto;

import java.time.Instant;

public class RatingResponseDto {
  private Long id;
  private int stars;
  private String comment;
  private String buyerUsername;
  private Instant createdAt;

  public RatingResponseDto(Long id, int stars, String comment, String buyerUsername, Instant createdAt) {
    this.id = id;
    this.stars = stars;
    this.comment = comment;
    this.buyerUsername = buyerUsername;
    this.createdAt = createdAt;
  }

  public Long getId() { return id; }
  public int getStars() { return stars; }
  public String getComment() { return comment; }
  public String getBuyerUsername() { return buyerUsername; }
  public Instant getCreatedAt() { return createdAt; }
}
