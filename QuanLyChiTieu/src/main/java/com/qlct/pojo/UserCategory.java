package com.qlct.pojo;

public class UserCategory {
    private int id;
    private int userId;
    private int categoryId;

    public UserCategory() {
    }

    public UserCategory(int id, int userId, int categoryId) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public UserCategory(int userId, int categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
}