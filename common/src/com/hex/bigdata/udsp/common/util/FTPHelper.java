package com.hex.bigdata.udsp.common.util;

import org.apache.commons.net.ftp.*;
import org.apache.commons.net.ftp.FTPClientConfig;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PC on 2017/4/17.
 */

public class FTPHelper {

    private FTPClient ftp = new FTPClient();
    private String hostname = com.hex.bigdata.udsp.common.util.FTPClientConfig.getHostname();
    private int port = com.hex.bigdata.udsp.common.util.FTPClientConfig.getPort();
    private String username = com.hex.bigdata.udsp.common.util.FTPClientConfig.getUsername();
    private String password = com.hex.bigdata.udsp.common.util.FTPClientConfig.getPassword();

    public FTPHelper() {
    }

    public FTPHelper(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        if (this.port > 0) {
            this.port = port;
        }
        this.username = username;
        this.password = password;
    }

    /**
     * 连接FTP服务器
     *
     * @return
     * @throws Exception
     */
    public FTPClient connectFTPServer() throws Exception {
        try {
            ftp.configure(getFTPClientConfig());
            ftp.connect(this.hostname, this.port);
            if (!ftp.login(this.username, this.password)) {
                ftp.logout();
                ftp = null;
                return ftp;
            }
            // 文件类型,默认是ASCII
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.setControlEncoding("GBK");
            // 设置被动模式
            ftp.enterLocalPassiveMode();
            ftp.setConnectTimeout(2000);
            ftp.setBufferSize(1024);
            // 响应信息
            int replyCode = ftp.getReplyCode();
            if ((!FTPReply.isPositiveCompletion(replyCode))) {
                // 关闭Ftp连接
                closeFTPClient();
                // 释放空间
                ftp = null;
                throw new Exception("登录FTP服务器失败,请检查![hostname:" + hostname + "、"
                        + "username:" + username + "、" + "password:" + password);
            } else {
                return ftp;
            }
        } catch (Exception e) {
            ftp.disconnect();
            ftp = null;
            throw e;
        }
    }

    /**
     * 配置FTP连接参数
     *
     * @return
     * @throws Exception
     */
    public FTPClientConfig getFTPClientConfig() throws Exception {
        String systemKey = FTPClientConfig.SYST_NT;
        String serverLanguageCode = "zh";
        FTPClientConfig conf = new FTPClientConfig(systemKey);
        conf.setServerLanguageCode(serverLanguageCode);
        conf.setDefaultDateFormatStr("yyyy-MM-dd");
        return conf;
    }

    /**
     * 向FTP根目录上传文件
     *
     * @param localFile
     * @param newName   新文件名
     * @throws Exception
     */
    public Boolean uploadFile(String localFile, String newName)
            throws Exception {
        InputStream input = null;
        boolean success = false;
        try {
            File file = null;
            if (checkFileExist(localFile)) {
                file = new File(localFile);
            }
            input = new FileInputStream(file);
            success = ftp.storeFile(newName, input);
            if (!success) {
                throw new Exception("文件上传失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return success;
    }

    /**
     * 向FTP根目录上传文件
     *
     * @param input
     * @param newName 新文件名
     * @throws Exception
     */
    public Boolean uploadFile(InputStream input, String newName)
            throws Exception {
        boolean success = false;
        try {
            success = ftp.storeFile(newName, input);
            if (!success) {
                throw new Exception("文件上传失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return success;
    }

    /**
     * 向FTP指定路径上传文件
     *
     * @param localFile
     * @param newName        新文件名
     * @param remoteFoldPath
     * @throws Exception
     */
    public Boolean uploadFile(String localFile, String newName,
                              String remoteFoldPath) throws Exception {
        InputStream input = null;
        boolean success = false;
        try {
            File file = null;
            if (checkFileExist(localFile)) {
                file = new File(localFile);
            }
            input = new FileInputStream(file);
            // 改变当前路径到指定路径
            if (!this.changeWorkingDirectory(remoteFoldPath)) {
                System.out.println("服务器路径不存!");
                return false;
            }
            success = ftp.storeFile(newName, input);
            if (!success) {
                throw new Exception("文件上传失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return success;
    }

    /**
     * 向FTP指定路径上传文件
     *
     * @param input
     * @param newName        新文件名
     * @param remoteFoldPath
     * @throws Exception
     */
    public Boolean uploadFile(InputStream input, String newName,
                              String remoteFoldPath) throws Exception {
        boolean success = false;
        try {
            // 改变当前路径到指定路径
            if (!this.changeWorkingDirectory(remoteFoldPath)) {
                System.out.println("服务器路径不存!");
                return false;
            }
            success = ftp.storeFile(newName, input);
            if (!success) {
                throw new Exception("文件上传失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return success;
    }

    /**
     * 从FTP服务器下载文件
     *
     * @param remotePath FTP路径(不包含文件名)
     * @param fileName   下载文件名
     * @param localPath  本地路径
     */
    public Boolean downloadFile(String remotePath, String fileName,
                                String localPath) throws Exception {
        BufferedOutputStream output = null;
        boolean success = false;
        try {
            // 检查本地路径
            this.checkFileExist(localPath);
            // 改变工作路径
            if (!this.changeWorkingDirectory(remotePath)) {
                System.out.println("服务器路径不存在");
                return false;
            }
            // 列出当前工作路径下的文件列表
            List<String> fileNameList = this.getListFileName(remotePath);
            if (fileNameList == null || fileNameList.size() == 0) {
                System.out.println("服务器当前路径下不存在文件！");
                return success;
            }
            for (String ftpfileName : fileNameList) {
                if (ftpfileName.equals(fileName)) {
                    File localFilePath = new File(localPath + File.separator
                            + ftpfileName);
                    output = new BufferedOutputStream(new FileOutputStream(
                            localFilePath));
                    success = ftp.retrieveFile(ftpfileName, output);
                }
            }
            if (!success) {
                throw new Exception("文件下载失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return success;
    }

    /**
     * 从FTP服务器获取文件流
     *
     * @param remoteFilePath
     * @return
     * @throws Exception
     */
    public InputStream downloadFile(String remoteFilePath) throws Exception {
        return ftp.retrieveFileStream(remoteFilePath);
    }

    /**
     * 从FTP服务器获取文件流
     *
     * @param remoteFilePath
     * @param output
     * @return
     * @throws IOException
     */
    public boolean downloadFile(String remoteFilePath, OutputStream output) throws IOException {
        return ftp.retrieveFile(remoteFilePath, output);
    }

    /**
     * 获取FTP服务器上指定路径下的文件列表
     *
     * @param remotePath
     * @return
     */
    public List<FTPFile> getFtpServerFileList(String remotePath)
            throws Exception {
        FTPListParseEngine engine = ftp.initiateListParsing(remotePath);
        List<FTPFile> ftpfiles = Arrays.asList(engine.getNext(25));
        return ftpfiles;
    }

    /**
     * 获取FTP服务器上[指定路径下的文件列表
     *
     * @param remotePath
     * @return
     * @throws Exception
     */
    public List<FTPFile> getFileList(String remotePath) throws Exception {
        List<FTPFile> ftpfiles = Arrays.asList(ftp.listFiles(remotePath));
        return ftpfiles;
    }

    /**
     * 获取FTP服务器[当前工作路径下的文件列表
     *
     * @return
     * @throws Exception
     */
    public List<FTPFile> getFileList() throws Exception {
        List<FTPFile> ftpfiles = Arrays.asList(ftp.listFiles());
        return ftpfiles;
    }

    public List<String> getListFileName(String remotePath) throws Exception {
        List<String> ftpfiles = Arrays.asList(ftp.listNames(remotePath));
        return ftpfiles;
    }


    /**
     * 改变FTP服务器工作路径
     *
     * @param remoteDirPath
     */
    public Boolean changeWorkingDirectory(String remoteDirPath) throws Exception {
        boolean flag = ftp.changeWorkingDirectory(remoteDirPath);
        if (!flag) {
            throw new Exception("改变FTP服务器工作路径失败");
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param remoteFilePath
     * @return
     * @throws Exception
     */
    public Boolean removeFile(String remoteFilePath) throws Exception {
        boolean flag = ftp.deleteFile(remoteFilePath);
        if (!flag) {
            throw new Exception("删除文件失败");
        }
        return flag;
    }

    /**
     * 创建目录
     *
     * @param remoteDirPath
     * @return
     */
    public boolean makeDirectory(String remoteDirPath) throws Exception {
        if (ftp.changeWorkingDirectory(remoteDirPath)) {
            return true;
        }
        boolean flag = ftp.makeDirectory(remoteDirPath);
        if (!flag) {
            throw new Exception("创建目录失败");
        }
        return false;
    }

    /**
     * 删除目录
     *
     * @param remoteDirPath
     * @return
     * @throws Exception
     */
    public boolean removeDirectory(String remoteDirPath) throws Exception {
        return ftp.removeDirectory(remoteDirPath);
    }

    /**
     * 删除目录以及文件
     *
     * @param remoteFoldPath
     * @return
     */
    public boolean removeDirAndsubFiles(String remoteFoldPath)
            throws Exception {
        boolean success = false;
        List<FTPFile> list = this.getFileList(remoteFoldPath);
        if (list == null || list.size() == 0) {
            return removeDirectory(remoteFoldPath);
        }
        for (FTPFile ftpFile : list) {

            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                success = removeDirAndsubFiles(remoteFoldPath + "/" + name);
                if (!success)
                    break;
            } else {
                success = removeFile(remoteFoldPath + "/" + name);
                if (!success)
                    break;
            }
        }
        if (!success)
            return false;
        success = removeDirectory(remoteFoldPath);
        return success;
    }

    /**
     * 检查本地路径是否存在
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public boolean checkFileExist(String filePath) throws Exception {
        boolean flag = false;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("本地路径不存在,请检查!");
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 关闭FTP连接
     *
     * @param ftp
     * @throws Exception
     */
    public void closeFTPClient(FTPClient ftp) throws Exception {
        if (ftp != null) {
            try {
                if (ftp.isConnected())
                    ftp.logout();
                ftp.disconnect();
            } catch (Exception e) {
                throw new Exception("关闭FTP服务出错!");
            }
        }
    }

    /**
     * 关闭FTP连接
     *
     * @throws Exception
     */
    public void closeFTPClient() throws Exception {
        this.closeFTPClient(this.ftp);
    }

    public FTPClient getFtp() {
        return ftp;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public void setFtp(FTPClient ftp) {
        this.ftp = ftp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * 主方法(测试)
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FTPHelper helper = new FTPHelper("10.1.97.1", 21, "UDSP", "UDSP");
        helper.connectFTPServer();
        //boolean flg = helper.makeDirectory("/home/ftp/UDSP/20170427");
        //System.out.println(flg);
        helper.downloadFile("/home/ftp/UDSP/20170531/d00b1ed0041e404ba17320a8574fd7f9_20170531142618931.dat");
    }
}
