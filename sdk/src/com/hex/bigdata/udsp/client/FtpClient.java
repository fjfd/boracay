package com.hex.bigdata.udsp.client;

import com.hex.bigdata.udsp.config.FtpClinetConfig;

public class FtpClient {

    private String hostname = FtpClinetConfig.getHostname();
    private int port = FtpClinetConfig.getPort();
    private String username = FtpClinetConfig.getUsername();
    private String password = FtpClinetConfig.getPassword();
    private String datafilePostfix = FtpClinetConfig.getDatafilePostfix();
    private String flgfilePostfix = FtpClinetConfig.getDatafilePostfix();

    public FtpClient() {
    }

    public FtpClient(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public FtpClient(String hostname, int port, String username, String password, String datafilePostfix, String flgfilePostfix) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.datafilePostfix = datafilePostfix;
        this.flgfilePostfix = flgfilePostfix;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatafilePostfix() {
        return datafilePostfix;
    }

    public void setDatafilePostfix(String datafilePostfix) {
        this.datafilePostfix = datafilePostfix;
    }

    public String getFlgfilePostfix() {
        return flgfilePostfix;
    }

    public void setFlgfilePostfix(String flgfilePostfix) {
        this.flgfilePostfix = flgfilePostfix;
    }
}
