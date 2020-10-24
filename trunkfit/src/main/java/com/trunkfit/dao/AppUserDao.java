package com.trunkfit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
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
 
@Repository
@Transactional
public class AppUserDao extends JdbcDaoSupport {
 
    @Autowired
    public AppUserDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
    
 
    public AppUser findUserAccount(String userName) {
        // Select .. from App_User u Where u.User_Name = ?
        String sql = AppUserMapper.BASE_SQL + " where u.User_Name = ? ";
 
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
    public boolean addUser(AppUser user) {
      String username = user.getUserName();
      String encryptedPassword = user.getEncrytedPassword();
      System.out.println("fffffff" + user);
      if(this.doesUserExist(user.getUserName())) {
        return false;
      }
     String userSQL ="INSERT INTO app_user(user_name, encryted_password,enabled) VALUES(?,?,?) RETURNING user_id";
     System.out.println(userSQL);
     Object[] params = new Object[] { username , encryptedPassword, 0};

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