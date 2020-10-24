package com.trunkfit.model;


public class AppUser {
 
    private Long userId;
    private String userName;
    private String encrytedPassword;
    private String customRole;

    public AppUser() {

    }
 
    public AppUser(Long userId, String userName, String encrytedPassword, String customRole) {
        this.userId = userId;
        this.customRole = customRole;
        this.userName = userName;
        this.encrytedPassword = encrytedPassword;
    }
 
    public String getCustomRole() {
      return customRole;
    }

    public void setCustomRole(String customRole) {
      this.customRole = customRole;
    }

    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public String getUserName() {
        return userName;
    }
 
    public void setUserName(String userName) {
        this.userName = userName;
    }
 
    public String getEncrytedPassword() {
        return encrytedPassword;
    }
 
    public void setEncrytedPassword(String encrytedPassword) {
        this.encrytedPassword = encrytedPassword;
    }
 
    @Override
    public String toString() {
        return this.userName + "/" + this.encrytedPassword;
    }
 
}