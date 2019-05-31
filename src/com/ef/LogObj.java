package com.ef;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Value Object representing log properties(Date, IP, Request, Status, User
 * Agent)
 *
 * @author Adebeslick
 */
public class LogObj {

    private Date date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;

    public LogObj(Date date, String ip, String request, String status, String userAgent) {
        this.date = date;
        this.ip = ip;
        this.request = request;
        this.status = status;
        this.userAgent = userAgent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

 

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

   
    public String toString() {
        
        
        String dateString = "";
        
        try {
            
       dateString =      ParserUtil.convertToDateString(date, ParserUtil.LOGS_DATE_PATTERN);
            
        } catch (ParseException ex) {
           
        }
        return ip+" "+dateString;
    }
    
    

}
