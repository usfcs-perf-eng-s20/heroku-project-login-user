package com.example.loginuser.model;

import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;


import javax.persistence.*;

@Entity
public class Users {

    @Id
    @GeneratedValue
    private int userId;

    @Column(name = "userName", length = 50)
    private String userName;

    @Column(name = "email", unique = true, length = 50, nullable = false) //nullable = false
    private String email;

    @Column(name = "password", length = 50, nullable = false) //nullable false
    private String password;

    @Column(name = "age")
    private int age;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "loginFrequency")
    private int loginFrequency;

    @Column(name = "lastLogin")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastLogin;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getLoginFrequency() {
        return loginFrequency;
    }

    public void setLoginFrequency(int loginFrequency) {
        this.loginFrequency = loginFrequency;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Users() {}

    public Users(String userName, String email, int age, String city, String password)
    {
        this.userName = userName;
        this.email = email;
        this.age = age;
        this.city = city;
        this.password = password;
    }
    
    public Users(String email, String password)
    {
    	this.email = email;
    	this.password = password;
    }

}
