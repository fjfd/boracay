package com.hex.bigdata.udsp.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Created by JunjieM on 2019-1-4.
 */
public class IqJdbcClientDemo {
    private static Logger logger = LogManager.getLogger (IqJdbcClientDemo.class);

    // jdbc:udsp://[hostname]:[port]
    private static String jdbcUrl = "jdbc:udsp://localhost:9089";
    private static String username = "admin";
    private static String password = "000000";

    static {
        try {
            Class.forName ("com.hex.bigdata.udsp.jdbc.UdspDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace ();
        }
    }

    public static void showServices() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection (jdbcUrl, username, password);
            stmt = conn.createStatement ();
            rs = stmt.executeQuery ("show services");
            ResultSetMetaData rsmd = rs.getMetaData ();
            while (rs.next ()) {
                logger.info ("-----------------------------------------");
                for (int i = 1; i <= rsmd.getColumnCount (); i++) {
                    logger.info ("colName:" + rsmd.getColumnLabel (i) + ", colValue:" + rs.getString (i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            if (rs != null) {
                rs.close ();
            }
            if (stmt != null) {
                stmt.close ();
            }
            if (conn != null) {
                conn.close ();
            }
        }
    }

    public static void describeService() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection (jdbcUrl, username, password);
            stmt = conn.createStatement ();
            rs = stmt.executeQuery ("describe hbase_test1");
            ResultSetMetaData rsmd = rs.getMetaData ();
            while (rs.next ()) {
                logger.info ("-----------------------------------------");
                for (int i = 1; i <= rsmd.getColumnCount (); i++) {
                    logger.info ("colName:" + rsmd.getColumnLabel (i) + ", colValue:" + rs.getString (i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            if (rs != null) {
                rs.close ();
            }
            if (stmt != null) {
                stmt.close ();
            }
            if (conn != null) {
                conn.close ();
            }
        }
    }

    public static void select() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection (jdbcUrl, username, password);
            stmt = conn.createStatement ();
            rs = stmt.executeQuery ("select * from TD2005012 " +
                    "where edmsSvrId='1' and edmsSysId= '1' and edmsBranchId='1' and edmsUserId='1' and edmsToken='1' " +
                    "and edmsValidDays='1' and edmsProdId='1' and edmsCustNo='1' and edmsCorp='1' and edmsReqData='1'");
            ResultSetMetaData rsmd = rs.getMetaData ();
            while (rs.next ()) {
                logger.info ("-----------------------------------------");
                for (int i = 1; i <= rsmd.getColumnCount (); i++) {
                    logger.info ("colName:" + rsmd.getColumnLabel (i) + ", colValue:" + rs.getString (i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            if (rs != null) {
                rs.close ();
            }
            if (stmt != null) {
                stmt.close ();
            }
            if (conn != null) {
                conn.close ();
            }
        }
    }

    public static void selectCount() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection (jdbcUrl, username, password);
            stmt = conn.createStatement ();
            rs = stmt.executeQuery ("select count(*) from TD2005012");
            ResultSetMetaData rsmd = rs.getMetaData ();
            while (rs.next ()) {
                logger.info ("-----------------------------------------");
                for (int i = 1; i <= rsmd.getColumnCount (); i++) {
                    logger.info ("colName:" + rsmd.getColumnLabel (i) + ", colValue:" + rs.getString (i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            if (rs != null) {
                rs.close ();
            }
            if (stmt != null) {
                stmt.close ();
            }
            if (conn != null) {
                conn.close ();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        showServices();
        describeService();
//        select();
//        selectCount();
        System.exit (0);
    }
}
