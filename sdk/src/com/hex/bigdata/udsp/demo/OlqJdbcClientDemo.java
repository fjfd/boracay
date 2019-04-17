package com.hex.bigdata.udsp.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Created by JunjieM on 2018-7-26.
 */
public class OlqJdbcClientDemo {
    private static Logger logger = LogManager.getLogger (OlqJdbcClientDemo.class);

    public static void main(String[] args) throws Exception {
        // jdbc:udsp://[hostname]:[port]/[service_name]
        String jdbcUrl = "jdbc:udsp://localhost:9089/hex_oracle";
        String username = "admin";
        String password = "000000";
        Class.forName ("com.hex.bigdata.udsp.jdbc.UdspDriver");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection (jdbcUrl, username, password);
            stmt = conn.createStatement ();
            rs = stmt.executeQuery ("select * from T_GF_FUNCATION t");
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
        System.exit (0);
    }
}
