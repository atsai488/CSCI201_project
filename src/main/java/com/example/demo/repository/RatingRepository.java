package com.example.demo.repository;

import com.example.demo.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating,Long> {
  List<Rating> findBySellerId(Long sellerId);

  @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.seller.id = :sellerId")
  Double findAverageStars(Long sellerId);
}
