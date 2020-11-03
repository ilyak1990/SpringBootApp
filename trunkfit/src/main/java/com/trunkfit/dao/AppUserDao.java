package com.trunkfit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.trunkfit.mapper.AppUserMapper;
import com.trunkfit.model.AppUser;
import com.trunkfit.utils.SQLUtils;
 
@Repository
@Transactional
public class AppUserDao extends JdbcDaoSupport {
 
    @Autowired
    public AppUserDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
    
 
    public AppUser findUserAccountByUsername(String userName) {
        // Select .. from App_User u Where u.User_Name = ?
        String sql = SQLUtils.BASE_SQL + " where u.User_Name = ? ";
 
        Object[] params = new Object[] { userName };
        AppUserMapper mapper = new AppUserMapper();
        try {
            AppUser userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            System.out.println(userInfo + " info");
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public AppUser findUserAccountByConfirmationToken(String confirmationToken) {
      // Select .. from App_User u Where u.User_Name = ?
      String sql = SQLUtils.BASE_SQL + " where u.confirmation_token = ? ";
System.out.println("sql statement " + sql + confirmationToken);
      Object[] params = new Object[] { confirmationToken };
      AppUserMapper mapper = new AppUserMapper();
      try {
          AppUser userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
          System.out.println(" user by token " + userInfo);
          return userInfo;
      } catch (EmptyResultDataAccessException e) {
          return null;
      }
  }
    public boolean removeToken(String userName) {
      // Select .. from App_User u Where u.User_Name = ?
      String sql = SQLUtils.basicUpdateStatement("app_user",Collections.singletonMap("confirmation_token", "null"),"user_name = '"+ userName+"'");
          int updatedUser = this.getJdbcTemplate().update(sql);
          System.out.println( " update result(removing token)"+updatedUser);
          return updatedUser==1;
      
  }
    
    public boolean activateUser(String userName) {
      // Select .. from App_User u Where u.User_Name = ?
      String sql = SQLUtils.basicUpdateStatement("app_user",Collections.singletonMap("verified", "true"),"user_name = '" + userName + "'");
      System.out.println(sql + " update result (activated)");

          int updatedUser = this.getJdbcTemplate().update(sql);
          System.out.println(updatedUser + " update result (activated)");
          return updatedUser==1;
      
  }
    public boolean addUser(AppUser user) {
      String username = user.getUserName();
      String encryptedPassword = user.getEncrytedPassword();
      String confirmationToken = user.getConfirmationToken();

      System.out.println("fffffff" + user);
      if(this.doesUserExist(user.getUserName())) {
        return false;
      }
     String userSQL ="INSERT INTO app_user(user_name, encryted_password,enabled,confirmation_token) VALUES(?,?,?,?) RETURNING user_id";
     System.out.println(userSQL);
     Object[] params = new Object[] { username , encryptedPassword, true, confirmationToken};

     String userId = getJdbcTemplate().query(userSQL,params, new ResultSetExtractor<String>() {
       @Override
       public String extractData(ResultSet rs) throws SQLException,
                                                      DataAccessException {
           return rs.next() ? rs.getString("user_id") : null;
       }
   });
     if(StringUtils.isEmpty(userId))
     {
       return false;
     }
     
     String userRoleSQL ="INSERT INTO user_role(user_id, role_id) VALUES(?,?) RETURNING id";
     Object[] params2 = new Object[] { Integer.valueOf(userId) , 1};

     String userRoleId = getJdbcTemplate().query(userRoleSQL,params2, new ResultSetExtractor<String>() {
       @Override
       public String extractData(ResultSet rs) throws SQLException,
                                                      DataAccessException {
           return rs.next() ? rs.getString("id") : null;
       }
   });
  return !StringUtils.isEmpty(userRoleId);
    }

    public boolean doesUserExist(String username) {
      String sql = "SELECT count(*) FROM APP_USER WHERE user_name = ?";
      int count = getJdbcTemplate().queryForObject(sql, new Object[] { username }, Integer.class);
      return count > 0;
    }
    
//    private SqlParameterSource getSqlParameterByUser(AppUser user) {
//      MapSqlParameterSource paramSource = new MapSqlParameterSource();
//      if(user!=null)
//      {
//        paramSource.addValue("username", user.getEncrytedPassword());
//        paramSource.addValue("password", user.getUserName());
//  
//
//      }
//      return paramSource;
//    }
}