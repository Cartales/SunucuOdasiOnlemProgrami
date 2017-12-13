/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author YunusEmre
 */
public class DatabaseConnection {
    
    private String url ="jdbc:mysql://localhost:3306/";
    private String driver ="com.mysql.jdbc.Driver";
    private String dbName ="db_svr";
    private String username ="root";
    private String password ="";
    
    public Connection connection;
    
    public void connectToDatabase(){
        try{
            
            Class.forName(driver).newInstance();
            connection = (Connection)DriverManager.getConnection(url+dbName,
                    username, password);
            System.out.println("Bağlantı Başarılı.");
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Bağlantı Başarısız Hata: "
                    + e);
            System.out.println("Bağlantı Başarısız Hata: "+ e);
        }
    }
            
}
