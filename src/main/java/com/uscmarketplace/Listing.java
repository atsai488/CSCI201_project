package com.uscmarketplace;

public class Listing{
    public int id;
    public String product_name;
    public float price;
    public String description;
    public String image1;
    public String image2;
    public String image3;
    public int sellerId;
    public String category;

    public Listing(int id, String product_name, float price, String description, String image1, String image2, String image3, int sellerId) {
        this.id = id;
        this.product_name = product_name;
        this.price = price;
        this.description = description;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.sellerId = sellerId;
    }
}