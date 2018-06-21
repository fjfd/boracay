package com.hex.bigdata.udsp.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by PC on 2016/9/30.
 */
public class FTPClientConfig {
    private static String hostname;
    private static int port = 21;
    private static String username;
    private static String password;
    private static String rootpath = "~";

    public static void loadConf() {
        loadConf("ftp.config.properties");
    }

    public static void loadConf(String configFilePath) {
        InputStream in = null;
        try {
            in = FTPClientConfig.class.getClassLoader().getResourceAsStream(
                    configFilePath);
            Properties props = new Properties();
            props.load(in);
            hostname = props.getProperty("ftp.hostname");
            String portStr = props.getProperty("ftp.port", "21");
            if (StringUtils.isNotBlank(portStr)) port = Integer.valueOf(portStr);
            username = props.getProperty("ftp.username");
            password = props.getProperty("ftp.password");
            rootpath = props.getProperty("ftp.rootpath", "/home/ftp");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getHostname() {
        return hostname;
    }

    public static void setHostname(String hostname) {
        FTPClientConfig.hostname = hostname;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        FTPClientConfig.port = port;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        FTPClientConfig.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        FTPClientConfig.password = password;
    }

    public static String getRootpath() {
        return rootpath;
    }

    public static void setRootpath(String rootpath) {
        FTPClientConfig.rootpath = rootpath;
    }

}
