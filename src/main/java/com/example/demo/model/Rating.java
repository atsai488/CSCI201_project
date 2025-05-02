package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(
  uniqueConstraints = @UniqueConstraint(columnNames = {"buyer_id","seller_id"})
)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="buyer_id")
    private User buyer;

    @ManyToOne(optional = false)
    @JoinColumn(name="seller_id")
    private User seller;

    @Min(1) @Max(5)
    private int stars;

    @Size(max=500)
    private String comment;

    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
}
