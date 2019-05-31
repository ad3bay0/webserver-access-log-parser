package com.ef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Arguments validator utility class
 *
 *
 * @author Adebeslick
 */
public class ParserUtil {

    public static final String ARGS_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
      public static final String LOGS_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    
    public static final String DURATION_HOURLY = "hourly";
    public static final String DURATION_DAILY = "daily";

    public static void isVaildArguments(String filePath, String startDateString, String duration, String threshold) throws Exception {

        if (isValidAccesLog(filePath) && isValidDate(startDateString) && isValidDuration(duration) && isValidThreshold(threshold)) {

            int hours = duration.equals(DURATION_DAILY) ? 24 : 1;

            Date startDate =  convertArgumentDateStringToDate(startDateString, ARGS_DATE_PATTERN);

            Date endDate = addHoursToJavaUtilDate(startDate, hours);
            
            retrieveThresholdRequests(getAllLogs(filePath), startDate, endDate);
  
        } else {
             System.out.println("Invalid arguments entered");
        }
    }

    public static boolean isValidThreshold(String threshold) {
        boolean isValid = false;

        try {
            validInt(threshold);
            isValid = true;

        } catch (Exception ex) {
            System.out.println("ex " + ex.getMessage());
            System.out.println("Invalid threshold entered. Please provide a valid threshold number");
        }
        return isValid;

    }

    public static boolean isValidDuration(String duration) {

        boolean isValid = false;

        if (DURATION_DAILY.equals(duration.toLowerCase()) || DURATION_HOURLY.equals(duration.toLowerCase())) {

            isValid = true;

        } else {

            System.out.println("Invalid duration entered. Please provide a duration e.g " + DURATION_DAILY + ", " + DURATION_HOURLY);
        }

        return isValid;

    }

    public static boolean isValidDate(String date) {
        boolean isValid = false;
        try {

            convertArgumentDateStringToDate(date, ARGS_DATE_PATTERN);

            isValid = true;

        } catch (ParseException ex) {

            System.out.println("Invalid date format entered. Please provide a valid start date");
        }
        return isValid;
    }

    public static boolean isValidAccesLog(String accesslogPath) {

        boolean isValid = false;

        File file = new File(accesslogPath);

        if (file.exists()) {

            isValid = true;
            

        } else {

            System.out.print("access log file not found. Please provide a valid path to access log file");
        }

        return isValid;
    }

    public static List<LogObj> readAccessLogFile(String filePath) {

        List<LogObj> listOfLogs = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;

            while ((line = br.readLine()) != null) {

                String[] arr = line.split("\\|");

                LogObj log = new LogObj(convertStringToDate(arr[0], LOGS_DATE_PATTERN), arr[1], arr[2], arr[3], arr[4]);

               // System.out.println(log);

                listOfLogs.add(log);
            }

        } catch (Exception ex) {

            System.out.println("ex " + ex.getMessage());
        }

        return listOfLogs;

    }
    
    private static Date convertArgumentDateStringToDate(String date,String pattern) throws ParseException{
    
          date = date.replace(".", " ");
    
          return convertStringToDate(date, pattern);
    }

    public static Date convertStringToDate(String date,String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(date);

    }

    public static String convertToDateString(Date date,String pattern) throws ParseException {

   
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);

    }

    public static void validInt(String intString) throws NumberFormatException {

        Integer.parseInt(intString);

    }

    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    
    
    public static List<LogObj> getAllLogs(String filePath){
    
        List<LogObj> logs = readAccessLogFile(filePath);
   
           return  logs;
    }
    
    public static void retrieveThresholdRequests (List<LogObj> lists, Date start, Date end){
    
       List<LogObj> thresh = new ArrayList<>();
        
       for(LogObj l:lists){
           
           if(l.getDate().after(start)&&l.getDate().before(end)){
               
               thresh.add(l);
                System.out.println(l);
           
           }
            
       }
        System.out.println("log size " + lists.size());
         System.out.println("thresh size " + thresh.size());
    
    }

}
