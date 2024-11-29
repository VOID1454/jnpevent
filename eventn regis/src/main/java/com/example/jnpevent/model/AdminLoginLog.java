package com.example.jnpevent.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AdminLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String ipAddress;
    private Date loginTime; // Stores the login time

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    // Setter for loginTime using Date
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    // Setter for loginTime using LocalDateTime
    public void setLoginTime(LocalDateTime now) {
        if (now != null) {
            // Converts LocalDateTime to Date
            this.loginTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            this.loginTime = null;
        }
    }
}
