package com.hex.bigdata.udsp.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FtpClinetConfig {
    private static String hostname;
    private static int port = 21;
    private static String username;
    private static String password;
    private static String datafilePostfix = ".dat";
    private static String flgfilePostfix = ".log";

    static {
        try {
            loadConf ("ftp.config.properties");
        } catch (Exception e) {
            //
        }
    }

    public static void loadConf(String configFilePath) {
        InputStream in = null;
        try {
            in = FtpClinetConfig.class.getClassLoader ().getResourceAsStream (
                    configFilePath);
            Properties props = new Properties ();
            props.load (in);
            hostname = props.getProperty ("udsp.ftp.hostname");
            String portStr = props.getProperty ("udsp.ftp.port", "21");
            if (StringUtils.isNotBlank (portStr)) {
                port = Integer.valueOf (portStr);
            }
            username = props.getProperty ("udsp.ftp.username");
            password = props.getProperty ("udsp.ftp.password");
            datafilePostfix = props.getProperty ("udsp.ftp.datafile.postfix", ".dat");
            flgfilePostfix = props.getProperty ("udsp.ftp.logfile.postfix", ".log");
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (in != null) {
                try {
                    in.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }

    public static String getHostname() {
        return hostname;
    }

    public static void setHostname(String hostname) {
        FtpClinetConfig.hostname = hostname;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        FtpClinetConfig.port = port;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        FtpClinetConfig.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        FtpClinetConfig.password = password;
    }

    public static String getDatafilePostfix() {
        return datafilePostfix;
    }

    public static void setDatafilePostfix(String datafilePostfix) {
        FtpClinetConfig.datafilePostfix = datafilePostfix;
    }

    public static String getFlgfilePostfix() {
        return flgfilePostfix;
    }

    public static void setFlgfilePostfix(String flgfilePostfix) {
        FtpClinetConfig.flgfilePostfix = flgfilePostfix;
    }
}
