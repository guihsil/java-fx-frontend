package com.freelamarket.model;

public class Publisher {

    private Integer id;
    private User user;
    private Integer amtPosts;
    private Integer limitPosts;
    private Double rating;

    public Publisher(Integer id, User user, Integer amtPosts, Integer limitPosts, Double rating) {
        this.id = id;
        this.user = user;
        this.amtPosts = amtPosts;
        this.limitPosts = limitPosts;
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAmtPosts() {
        return amtPosts;
    }

    public void setAmtPosts(Integer amtPosts) {
        this.amtPosts = amtPosts;
    }

    public Integer getLimitPosts() {
        return limitPosts;
    }

    public void setLimitPosts(Integer limitPosts) {
        this.limitPosts = limitPosts;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
