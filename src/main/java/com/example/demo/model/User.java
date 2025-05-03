package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"user\"") // May need to change to "User"
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true)
  private String username;

  @Column(nullable=false)
  private String role;   // "BUYER" or "SELLER"

  // getters & setters
  public Long getId() { return id; }
  public String getUsername() { return username; }
  public void setUsername(String u) { this.username = u; }
  public String getRole() { return role; }
  public void setRole(String r) { this.role = r; }
}
