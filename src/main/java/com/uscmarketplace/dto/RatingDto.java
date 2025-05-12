package com.uscmarketplace.dto;

public class RatingDto {
  private Long sellerId;
  private int stars;
  private String comment;


  public Long getSellerId() { return sellerId; }
  public void setSellerId(Long s) { this.sellerId = s; }
  public int getStars() { return stars; }
  public void setStars(int s) { this.stars = s; }
  public String getComment() { return comment; }
  public void setComment(String c) { this.comment = c; }
}
