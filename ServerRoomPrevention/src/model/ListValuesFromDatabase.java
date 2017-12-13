/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import classes.DatabaseValues;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author YunusEmre
 */
public class ListValuesFromDatabase {
    
    PreparedStatement ps;
    ResultSet rs;
    
    private String date;
    private String admin;
    private String value;

    public String getDate() {
        return date;
    }

    public String getAdmin() {
        return admin;
    }

    public String getValue() {
        return value;
    }

    
    public void setDate(String date) {
        this.date = date;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
    public List<ListValuesFromDatabase> listValuesFromTempTable(){
        int sayac = 0, i = 0;
        
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connectToDatabase();
        
        List<ListValuesFromDatabase> vList = new ArrayList<ListValuesFromDatabase>();
        ListValuesFromDatabase[] vArray;
        
        try{
            String sql1 = "SELECT u.username, t.temp_date, t.temp_value FROM users u " +
                    "INNER JOIN temperatures t ON u.user_id = t.user_id";
            ps = databaseConnection.connection.prepareStatement(sql1);
            rs = ps.executeQuery();
            while(rs.next()){
                sayac = sayac + 1;
            }
            vArray = new ListValuesFromDatabase[sayac];
            String sql2 = "SELECT u.username, t.temp_date, t.temp_value FROM users u " +
                    "INNER JOIN temperatures t ON u.user_id = t.user_id";
            ps = databaseConnection.connection.prepareStatement(sql2);
            rs = ps.executeQuery();
            while(rs.next()){
                vArray[i] = new ListValuesFromDatabase();
                vArray[i].setDate(rs.getString(1));
                vArray[i].setAdmin(rs.getString(2));
                vArray[i].setValue(rs.getString(3));
                vList.add(vArray[i]);
                i++;
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
        return vList;
    }
    
    public List<ListValuesFromDatabase> listValuesFromSmokeTable(){
        int sayac = 0, i = 0;
        
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connectToDatabase();
        
        List<ListValuesFromDatabase> vList = new ArrayList<ListValuesFromDatabase>();
        ListValuesFromDatabase[] vArray;
        
        try{
            String sql1 = "SELECT u.username, s.smoke_date, s.smoke_state FROM users u " +
                    "INNER JOIN smoke_states s ON u.user_id = s.user_id";
            ps = databaseConnection.connection.prepareStatement(sql1);
            rs = ps.executeQuery();
            while(rs.next()){
                sayac = sayac + 1;
            }
            vArray = new ListValuesFromDatabase[sayac];
            String sql2 = "SELECT u.username, s.smoke_date, s.smoke_state FROM users u " +
                    "INNER JOIN smoke_states s ON u.user_id = s.user_id";
            ps = databaseConnection.connection.prepareStatement(sql2);
            rs = ps.executeQuery();
            while(rs.next()){
                vArray[i] = new ListValuesFromDatabase();
                vArray[i].setDate(rs.getString(1));
                vArray[i].setAdmin(rs.getString(2));
                vArray[i].setValue(rs.getString(3));
                vList.add(vArray[i]);
                i++;
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
        return vList;
    }
    
        
    
        
        
}

    
