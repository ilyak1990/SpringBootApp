package com.trunkfit.utils;

import java.util.HashMap;
import java.util.Map;

public class SQLUtils {
  
  public static final String BASE_SQL //
  = "Select u.User_Id, u.User_Name, u.Encryted_Password, u.confirmation_token, u.verified,u.enabled From App_User u ";


public static String basicUpdateStatement(String tableName , Map<String,String> columnValueMap, String condition) {
  
  StringBuilder sqlStatement = new StringBuilder();
  
  sqlStatement.append("UPDATE ").append(tableName).append(" SET ");
  columnValueMap.forEach((k,v) -> sqlStatement.append(k).append(" = ").append(v));
  sqlStatement.append(" WHERE ").append(condition);
  
  return sqlStatement.toString();
  
}
}
