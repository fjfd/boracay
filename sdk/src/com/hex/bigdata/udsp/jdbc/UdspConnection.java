package com.hex.bigdata.udsp.jdbc;

import com.hex.bigdata.udsp.config.NettyClientConfig;
import com.hex.bigdata.udsp.jdbc.netty.Client;
import com.hex.bigdata.udsp.jdbc.netty.ClientFactory;
import com.hex.bigdata.udsp.jdbc.netty.NettyClientFactory;
import com.hex.bigdata.udsp.jdbc.netty.RemotingUrl;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * UDSP JDBC Connection
 */
public class UdspConnection implements Connection {
    private JdbcConnectionParams connParams;
    private String host;
    private int port;
    private String serviceName;
    private final Map<String, String> sessVarMap;
    private final Map<String, String> udspVarMap;
    private final Map<String, String> dataVarMap;

    private RemotingUrl url;

    private int loginTimeout = NettyClientConfig.getConnectTimeout ();

    private SQLWarning warningChain = null;
    private ClientFactory factory;
    private Client client;

    public UdspConnection(String uri, Properties info) throws SQLException {
        setupLoginTimeout();
        try {
            connParams = Utils.parseURL(uri);
        } catch (JdbcUriParseException e) {
            throw new SQLException(e);
        }

        host = connParams.getHost();
        port = connParams.getPort();
        serviceName = connParams.getServiceName();

        sessVarMap = connParams.getSessionVars();
        udspVarMap = connParams.getUdspVars();
        dataVarMap = connParams.getDataVars();

        // 如果JDBC连接属性没有在连接URL中提供，则从其提取用户/密码
        if (info.containsKey(JdbcConnectionParams.AUTH_USER)) {
            sessVarMap.put(JdbcConnectionParams.AUTH_USER, info.getProperty(JdbcConnectionParams.AUTH_USER));
            if (info.containsKey(JdbcConnectionParams.AUTH_PASSWD)) {
                sessVarMap.put(JdbcConnectionParams.AUTH_PASSWD, info.getProperty(JdbcConnectionParams.AUTH_PASSWD));
            }
        }

        factory = new NettyClientFactory();
        url = new RemotingUrl();
        url.setHost(host);
        url.setPort(port);
        url.setServiceName(serviceName);
        url.setUsername(sessVarMap.get(JdbcConnectionParams.AUTH_USER));
        url.setPassword(sessVarMap.get(JdbcConnectionParams.AUTH_PASSWD));
        url.setConnectionTimeout(loginTimeout);
        try {
            client = factory.get(url);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // copy loginTimeout from driver manager. timeout needs to be in millis
    private void setupLoginTimeout() {
        long timeOut = TimeUnit.SECONDS.toMillis(DriverManager.getLoginTimeout());
        if (timeOut > Integer.MAX_VALUE) {
            loginTimeout = Integer.MAX_VALUE;
        } else if (timeOut != 0) {
            loginTimeout = (int) timeOut;
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Can't create Statement, connection is closed");
        }
        return new UdspStatement(this, factory, client, url);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new UdspPreparedStatement(this, factory, client, url, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (autoCommit) {
            throw new SQLException("enabling autocommit is not supported");
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return true;
    }

    @Override
    public void commit() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed()) {
            try {
                factory.remove(url);
            } catch (Exception e) {
                client.close();
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return client == null || !client.isConnected();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (isClosed()) {
            throw new SQLException("Connection is closed");
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new SQLClientInfoException("Method not supported", null);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warningChain;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warningChain = null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        if (isClosed()) {
            throw new SQLException("Can't create Statement, connection is closed");
        }
        return new UdspStatement(this, factory, client, url);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new SQLClientInfoException("Method not supported", null);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException("Method not supported", null);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public String getSchema() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
}
