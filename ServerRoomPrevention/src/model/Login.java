/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import classes.Users;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author YunusEmre
 */
public class Login {
    
    PreparedStatement ps;
    ResultSet rs;
    
    public boolean loginToSystem(String username, String password) throws SQLException{
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connectToDatabase();
        
        String sql = "SELECT password FROM users WHERE username = ?";
        
        ps = databaseConnection.connection.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        while(rs.next()){
            String dbPassword = rs.getString("password");
            if(dbPassword.equals(password)){
                Users user = new Users();
                user.setUsername(username);
                return true;
            }
            else
                return false;
        }
        return false;
    }
}
