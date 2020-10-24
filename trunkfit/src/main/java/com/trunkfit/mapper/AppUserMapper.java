package com.trunkfit.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
 
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import com.trunkfit.model.AppUser;
 
public class AppUserMapper implements RowMapper<AppUser> {
 
    public static final String BASE_SQL //
            = "Select u.User_Id, u.User_Name, u.Encryted_Password From App_User u ";
 
    @Override
    public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
 
        Long userId = rs.getLong("User_Id");
        String userName = rs.getString("User_Name");
        String encrytedPassword = rs.getString("Encryted_Password");
        System.out.println("mapping fir syre");
        return new AppUser(userId, userName, encrytedPassword, "USER");
    }
 
}