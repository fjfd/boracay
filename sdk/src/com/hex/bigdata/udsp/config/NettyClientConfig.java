package com.hex.bigdata.udsp.config;

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by JunjieM on 2019-2-15.
 */
public class NettyClientConfig {

    private static int connectTimeout = 3000;
    private static int maximumSize = 65535;
    private static int expireAfterAccess = 30;
    private static int bufferSize = 134217728;

    static {
        loadConf ("netty.config.properties");
    }

    public static void loadConf(String configFilePath) {
        InputStream in = null;
        try {
            in = SdkFtpClientConfig.class.getClassLoader ().getResourceAsStream (
                    configFilePath);
            Properties props = new Properties ();
            props.load (in);
            String connectTimeoutStr = props.getProperty ("udsp.netty.connect.timeout", "3000");
            if (StringUtils.isNotBlank (connectTimeoutStr)) {
                connectTimeout = Integer.valueOf (connectTimeoutStr);
            }
            String maximumSizeStr = props.getProperty ("udsp.netty.maximum.size", "65535");
            if (StringUtils.isNotBlank (maximumSizeStr)) {
                maximumSize = Integer.valueOf (maximumSizeStr);
            }
            String expireAfterAccessStr = props.getProperty ("udsp.netty.expire.after.access", "30");
            if (StringUtils.isNotBlank (expireAfterAccessStr)) {
                expireAfterAccess = Integer.valueOf (expireAfterAccessStr);
            }
            String bufferSizeStr = props.getProperty ("udsp.netty.buffer.size", "134217728");
            if (StringUtils.isNotBlank (bufferSizeStr)) {
                bufferSize = Integer.valueOf (bufferSizeStr);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(int connectTimeout) {
        NettyClientConfig.connectTimeout = connectTimeout;
    }

    public static int getMaximumSize() {
        return maximumSize;
    }

    public static void setMaximumSize(int maximumSize) {
        NettyClientConfig.maximumSize = maximumSize;
    }

    public static int getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public static void setExpireAfterAccess(int expireAfterAccess) {
        NettyClientConfig.expireAfterAccess = expireAfterAccess;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static void setBufferSize(int bufferSize) {
        NettyClientConfig.bufferSize = bufferSize;
    }
}
