/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import classes.DatabaseValues;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author YunusEmre
 */
public class InsertValueToDatabase {
    
    PreparedStatement ps;
    
    public void insertToTempTable(DatabaseValues dbv) throws SQLException{
        
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connectToDatabase();
        
        String sql = "insert into temperatures(temp_date, user_id, temp_value)"
                + "values(?, (select user_id from users where username = ?), ?)";
        ps = databaseConnection.connection.prepareStatement(sql);
        ps.setString(1, dbv.getTempDate().toString());
        ps.setString(2, dbv.getTempAdmin());
        ps.setString(3, dbv.getTempValue());
        ps.execute();
              
    }
    
    public void insertToSmokeTable(DatabaseValues dbv) throws SQLException{
        
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connectToDatabase();
        
        String sql = "insert into smoke_states(smoke_date, user_id, smoke_state)"
                + "values(?, (select user_id from users where username = ?), ?)";
        ps = databaseConnection.connection.prepareStatement(sql);
        ps.setString(1, dbv.getSmokeDate().toString());
        ps.setString(2, dbv.getSmokeAdmin());
        ps.setString(3, dbv.getSmokeValue());
        ps.execute();
              
    }
    
}
