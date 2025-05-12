package com.uscmarketplace.dto;

import java.util.List;

public class SellerRatingsDto {
  private double average;
  private List<RatingResponseDto> ratings;

  public SellerRatingsDto(double average, List<RatingResponseDto> ratings){
    this.average = average;
    this.ratings = ratings;
  }

  public double getAverage() { return average; }
  public List<RatingResponseDto> getRatings() { return ratings; }
}
