package com.trunkfit.model;


public class AppUser {
 
    private Long userId;
    private String userName;
    public String getConfirmationToken() {
      System.out.println("right off of user" + confirmationToken);
      return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
      this.confirmationToken = confirmationToken;
    }

    private String encrytedPassword;
    private String confirmationToken;
    private String customRole;
    private boolean isVerified;
    private boolean isEnabled;


    public boolean isVerified() {
      return isVerified;
    }

    public void setVerified(boolean isVerified) {
      this.isVerified = isVerified;
    }

    public boolean isEnabled() {
      return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
    }

    public AppUser() {

    }
 
    public AppUser(Long userId, String userName, String encrytedPassword, String customRole, String confirmationToken, boolean isVerified, boolean isEnabled) {
        this.userId = userId;
        this.customRole = customRole;
        this.userName = userName;
        this.encrytedPassword = encrytedPassword;
        this.confirmationToken = confirmationToken;
        this.isVerified = isVerified;
        this.isEnabled = isEnabled;

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