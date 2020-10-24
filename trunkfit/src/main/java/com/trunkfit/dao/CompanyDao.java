package com.trunkfit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.trunkfit.model.Company;

@Repository
@Transactional
public class CompanyDao extends JdbcDaoSupport {
 
    @Autowired
    public CompanyDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
 
    public String getUserCompanyName(Long userId) {
        String sql = "Select c.name " //
                + " from Company c, App_User au " //
                + " where c.user_id = au.user_id and c.User_Id = ? ";
 
        Object[] params = new Object[] { userId };
 
        List<Company> company = this.getJdbcTemplate().query(sql, params, new CompanyMapper());
     if(company.size()==0)
     {
       throw new RuntimeException("No company returned for user: " +userId);
     }
     return company.get(0).getCompanyName();
    }
    
    public List<Company> getCompanies() {
      String sql = "Select * from company";

      List<Company> companies = this.getJdbcTemplate().query(sql, new CompanyMapper());

      return companies;
  }
    
    private static final class CompanyMapper implements RowMapper<Company> {
      public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getLong("company_id"));
        company.setCompanyName(rs.getString("name"));
        return company;
        }
      }
    
    public boolean addCompany(Company company) {
      System.out.println(company);

     String userSQL ="INSERT INTO company(name) VALUES(?) RETURNING company_id";
     System.out.println(userSQL);
     Object[] params = new Object[] { company.getCompanyName()};

     String companyId = getJdbcTemplate().query(userSQL,params, new ResultSetExtractor<String>() {
       @Override
       public String extractData(ResultSet rs) throws SQLException,
                                                      DataAccessException {
           return rs.next() ? rs.getString("company_id") : null;
       }
   });
     
     return !StringUtils.isEmpty(companyId);
     
}
}