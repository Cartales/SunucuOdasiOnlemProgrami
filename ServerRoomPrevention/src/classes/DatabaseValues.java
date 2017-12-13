/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 *
 * @author YunusEmre
 */
public class DatabaseValues {
    
    private static LocalDateTime tempDate;
    private static LocalDateTime smokeDate;
    private static String tempValue;
    private static String smokeValue;
    private static String tempAdmin;
    private static String smokeAdmin;

    public LocalDateTime getTempDate() {
        return tempDate;
    }

    public LocalDateTime getSmokeDate() {
        return smokeDate;
    }

    public String getTempValue() {
        return tempValue;
    }

    public String getSmokeValue() {
        return smokeValue;
    }

    public String getTempAdmin() {
        return tempAdmin;
    }

    public String getSmokeAdmin() {
        return smokeAdmin;
    }

    public void setTempDate(LocalDateTime tempDate) {
        this.tempDate = tempDate;
    }

    public void setSmokeDate(LocalDateTime smokeDate) {
        this.smokeDate = smokeDate;
    }

    public void setTempValue(String tempValue) {
        this.tempValue = tempValue;
    }

    public void setSmokeValue(String smokeValue) {
        this.smokeValue = smokeValue;
    }

    public void setTempAdmin(String tempAdmin) {
        this.tempAdmin = tempAdmin;
    }

    public void setSmokeAdmin(String smokeAdmin) {
        this.smokeAdmin = smokeAdmin;
    }
    
    
}
