package com.trunkfit.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
 
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import com.trunkfit.model.AppUser;
 
public class AppUserMapper implements RowMapper<AppUser> {
 
 
    @Override
    public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
 
        Long userId = rs.getLong("User_Id");
        String userName = rs.getString("User_Name");
        String encrytedPassword = rs.getString("Encryted_Password");
        String confirmationToken = rs.getString("confirmation_token");
        boolean isVerified = rs.getBoolean("verified");
        boolean isEnabled = rs.getBoolean("enabled");

        System.out.println("mapping user");
        return new AppUser(userId, userName, encrytedPassword, "USER", confirmationToken,isVerified,isEnabled);
    }
 
}