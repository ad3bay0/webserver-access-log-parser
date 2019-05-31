/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *Database connection class
 * 
 * @author Adebayo Adeniyan
 */
public class DBConnect {
    
        private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/parser?useSSL=false";
        private static final String DATABASE_USER = "psuser";
        private static final String DATABSE_PASSWORD = "ps!234";
    
    static Connection getDbConnection() throws SQLException{
   
       return DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABSE_PASSWORD);
  
   }
    
}
