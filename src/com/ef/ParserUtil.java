package com.ef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsing utility class that handles command line arguments request and save logs/blocked ip to database
 * Also contains some utility methods
 *
 *
 * @author Adebayo Adeniyan
 */
public class ParserUtil {

    private static final String ARGS_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    protected static final String LOGS_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final String DURATION_HOURLY = "hourly";
    private static final String DURATION_DAILY = "daily";
    
    private static final String QUERY_INSERT_LOG = "INSERT INTO logs(request_ip, request_date, request_method , request_status, request_agent, created_date) VALUES (?,?,?,?,?,?)";
    private static final String QUERY_INSERT_BLOCKEDIP = "INSERT INTO blocked_ips(request_ip, block_message, created_date) VALUES (?,?,?)";

    /**
     * validates arguments from command line and delegate methods to handle parsing and database saving
     * 
     * @param filePath 
     * @param startDateString  
     * @param duration  
     * @param threshold  
     * 
     */
    protected static void processArgumentQuery(String filePath, String startDateString, String duration, String threshold) throws Exception {

        if (isValidAccesLog(filePath) && isValidDate(startDateString) && isValidDuration(duration) && isValidThreshold(threshold)) {

            int hours = duration.equals(DURATION_DAILY) ? 24 : 1;

            Date startDate = convertArgumentDateStringToDate(startDateString, ARGS_DATE_PATTERN);

            Date endDate = addHoursToJavaUtilDate(startDate, hours);

            //retrieve logs from access file and add logs to database
            List<LogObj> logs = getAllLogsFromFileAndAddToDataBase(filePath);

            if (!logs.isEmpty()) {

                processThresholdRequestsAndSaveBlockeIpToDatabase(logs, startDate, endDate, convertStringToInt(threshold), hours);

            } else {

                System.out.println("acccess log is empty");
            }

        } else {
            System.out.println("Invalid arguments entered");
        }
    }
/**
 * validates threshold to make sure its an integer data type
 * @param threshold (string integer)
 * @return true if threshold is a valid integer
 */
    private static boolean isValidThreshold(String threshold) {
        boolean isValid = false;

        try {
            convertStringToInt(threshold);
            isValid = true;

        } catch (NumberFormatException ex) {
            System.out.println("ex " + ex.getMessage());
            System.out.println("Invalid threshold entered. Please provide a valid threshold number");
        }
        return isValid;

    }

    /**
     * validates duration argument to make sure either daily or hourly was inputed
     * @param duration
     * @return true if duration is either hourly or daily
     */
    private static boolean isValidDuration(String duration) {

        boolean isValid = false;

        if (DURATION_DAILY.equals(duration.toLowerCase()) || DURATION_HOURLY.equals(duration.toLowerCase())) {

            isValid = true;

        } else {

            System.out.println("Invalid duration entered. Please provide a duration e.g " + DURATION_DAILY + ", " + DURATION_HOURLY);
        }

        return isValid;

    }

    /**
     * validates date argument 
     * @param date
     * @return true if valid date was entered
     */
    private static boolean isValidDate(String date) {
        boolean isValid = false;
        try {

            convertArgumentDateStringToDate(date, ARGS_DATE_PATTERN);

            isValid = true;

        } catch (ParseException ex) {

            System.out.println("Invalid date format entered. Please provide a valid start date");
        }
        return isValid;
    }

    /**
     * validates if access log uuri path entered in command line argument is an existing file
     * @param accesslogPath
     * @return true if file path exists
     */
    private static boolean isValidAccesLog(String accesslogPath) {

        boolean isValid = false;

        File file = new File(accesslogPath);

        if (file.exists()) {

            isValid = true;

        } else {

            System.out.print("access log file not found. Please provide a valid path to access log file");
        }

        return isValid;
    }

    /**
     * reads access.log for request logs delimited by |
     * @param filePath
     * @return  an array List of LogObj
     */
    private static List<LogObj> readAccessLogFile(String filePath) {

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
/**
 * converts argument date string that contains a . to Java date
 * @param date
 * @param pattern
 * @return
 * @throws ParseException 
 */
    private static Date convertArgumentDateStringToDate(String date, String pattern) throws ParseException {

        date = date.replace(".", " ");

        return convertStringToDate(date, pattern);
    }

    /**
     * string to java date converter
     * @param date
     * @param pattern
     * @return
     * @throws ParseException 
     */
    protected static Date convertStringToDate(String date, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(date);

    }

    /*
    convert java date to string
    */
    protected static String convertToDateString(Date date, String pattern) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);

    }

    /**
     * converts sting to integer
     * @param intString
     * @return
     * @throws NumberFormatException 
     */
    public static int convertStringToInt(String intString) throws NumberFormatException {

        return Integer.parseInt(intString);

    }
/**
 * add hours to Java date to return the Java date after the hour added
 * @param date
 * @param hours
 * @return date after hour has been added
 */
    private static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * retrieve logs variable object from file
     * @param filePath
     * @return array list of Log objects
     */
    private static List<LogObj> getAllLogsFromFileAndAddToDataBase(String filePath) {

        //get all logs from file
        List<LogObj> logs = readAccessLogFile(filePath);

        if (!logs.isEmpty()) {
            //add logs to database
            addLogsToDatabase(logs);

        }

        return logs;
    }

    /**
     * process the argument requests from command line to display the ips within the specified threshold in command line
     * and also save them to database
     * @param listOfLogs
     * @param start
     * @param end
     * @param threshold
     * @param hours 
     */
    public static void processThresholdRequestsAndSaveBlockeIpToDatabase(List<LogObj> listOfLogs, Date start, Date end, int threshold, int hours) {

        Map<String, Integer> ips = new HashMap<>();
        List<BlockedIpObj> bips = new ArrayList<>();

        for (LogObj l : listOfLogs) {

            if (l.getDate().after(start) && l.getDate().before(end)) {

                Integer ipInt = ips.get(l.getIp());

                ips.put(l.getIp(), (ipInt == null) ? 1 : ipInt + 1);

            }

        }

        for (Map.Entry<String, Integer> val : ips.entrySet()) {

            if (val.getValue() >= threshold) {

                String ip = val.getKey();
                Integer noOfRequest = val.getValue();
                String message = "blocked for making " + noOfRequest + " requests in " + hours + " hours.";
                System.out.println("IP " + ip + " has made more than " + threshold + " requests.");

                BlockedIpObj blockedIp = new BlockedIpObj(ip, message);

                bips.add(blockedIp);
            }
        }

        if (!bips.isEmpty()) {

            //save blocked ip into database
            addBlockedIpsToDatabase(bips);

        } else {
            System.out.println("no IP meets the argument request!");
        }

    }

    /**
     *batch adds logs to database
     * @param logs 
     */
    private static void addLogsToDatabase(List<LogObj> logs) {

        try (Connection con = DBConnect.getDbConnection()) {

            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(QUERY_INSERT_LOG);

            System.out.println("Saving logs in database, Please wait................. ");

            for (LogObj log : logs) {
                ps.setString(1, log.getIp());
                ps.setTimestamp(2, new Timestamp(log.getDate().getTime()));
                ps.setString(3, log.getRequest());
                ps.setString(4, log.getStatus());
                ps.setString(5, log.getUserAgent());
                ps.setTimestamp(6, new Timestamp(new Date().getTime()));
                ps.addBatch();

            }

            ps.executeBatch();
            con.commit();

            System.out.println("Logs saved successfully!");

        } catch (SQLException ex) {

            System.out.println("database connection error: " + ex.getMessage());
        }

    }
/**
 * batch adds blocked ips to database
 * @param blockedIps 
 */
    private static void addBlockedIpsToDatabase(List<BlockedIpObj> blockedIps) {

        try (Connection con = DBConnect.getDbConnection()) {

            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(QUERY_INSERT_BLOCKEDIP);

            System.out.println("Saving blocked IPs to database, Please wait.................. ");

            for (BlockedIpObj bip : blockedIps) {

                ps.setString(1, bip.getRequestIp());
                ps.setString(2, bip.getBlockMessage());
                ps.setTimestamp(3, new Timestamp(new Date().getTime()));
                ps.addBatch();

            }

            ps.executeBatch();
            con.commit();

            System.out.println("Blocked IPs saved successfully!");

        } catch (SQLException ex) {

            System.out.println("database connection error: " + ex.getMessage());
        }

    }
/**
 * delete logs and blocked ip tables
 */
    protected static void cleanAllDatabase() {

        clearDb("DELETE FROM logs");
        clearDb("DELETE FROM blocked_ips");
    }

    /*'
    handle database delete by passing in query
    */
    private static void clearDb(String query) {
        try (Connection con = DBConnect.getDbConnection()) {

            Statement st = con.createStatement();
            int deletedRows = st.executeUpdate(query);

            if (deletedRows > 0) {
                System.out.println("deleted successfully");
            } else {

            }

        } catch (SQLException ex) {

            System.out.println("database connection error: " + ex.getMessage());
        }
    }
}
