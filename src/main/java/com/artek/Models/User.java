package com.artek.Models;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "USER")
@Table (name = "users")
public class User {

    @Id
    @Column(name = "userId")
    private int userId;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "isActive")
    private int isActive;

    @Column(name = "state")
    private int state;

    @Column(name = "allMarksLast")
    private String allMarksLast;

    public User() {}

    public User(int userId, String login, String password, int isActive, int state) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.isActive = isActive;
        this.state = state;
    }

    public int getId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getState() { return state; }

    public int getIsActive() {
        return isActive;
    }

    public String getAllMarksLast() { return allMarksLast; }

    public void setIsActive(int isActive) { this.isActive = isActive; }

    public void setState(int state) { this.state = state; }

    public void setId(int userId) {
        this.userId = userId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAllMarksLast(String allMarksLast) { this.allMarksLast = allMarksLast; }

    @Override
    public String toString() {
        return "models.User{userId= " + userId + ", login= " + login + ", password= " + password + " state= " + state + " }";
    }
}
