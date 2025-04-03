/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.pojo;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author QUI
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private DateTimeFormatter createdDate;

    public User() {
    }

    public User(int id, String firstName, String lastName, String username, String password, String email, String avatar, DateTimeFormatter createdDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar = avatar;
        this.createdDate = createdDate;
    }
    
     public User(int id, String firstName, String lastName, String username, String email) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return this.username; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
        
     }
    /**
     * @return the id
    
    

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return the createdDate
     */
    public DateTimeFormatter getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(DateTimeFormatter createdDate) {
        this.createdDate = createdDate;
    }
}
