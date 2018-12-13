package com.artek.Models;

import javax.persistence.*;

@Entity
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

    public User() {}

    public User(int userId, String login, String password, int isActive) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.isActive = isActive;
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


    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) { this.isActive = isActive; }

    public void setId(int userId) {
        this.userId = userId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "models.User{userId= " + userId + ", login= " + login + ", password= " + password + " }";
    }
}
