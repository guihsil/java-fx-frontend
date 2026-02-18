package com.freelamarket.model;

public class Provider {

    private Integer id;
    private User user;
    private Integer amtProposes;
    private Integer limitProposes;
    private Double rating;

    public Provider(Integer id, User user, Integer amtProposes, Integer limitProposes, Double rating) {
        this.id = id;
        this.user = user;
        this.amtProposes = amtProposes;
        this.limitProposes = limitProposes;
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

    public Integer getAmtProposts() {
        return amtProposes;
    }

    public void setAmtProposts(Integer amtProposts) {
        this.amtProposes = amtProposts;
    }

    public Integer getLimitProposts() {
        return limitProposes;
    }

    public void setLimitProposts(Integer limitProposts) {
        this.limitProposes = limitProposts;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
