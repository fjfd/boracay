package com.hex.bigdata.udsp.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.jdbc.netty.Client;
import com.hex.bigdata.udsp.jdbc.netty.ClientFactory;
import com.hex.bigdata.udsp.jdbc.netty.RemotingUrl;
import com.hex.bigdata.udsp.model.request.SqlRequest;
import com.hex.bigdata.udsp.model.response.SyncResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * UDSP JDBC Statement
 */
public class UdspStatement implements Statement {

    private final UdspConnection connection;
    private final ClientFactory factory;
    private final Client client;
    private final RemotingUrl url;
    private ResultSet resultSet = null;
    private SQLWarning warningChain = null;
    private int fetchSize = 50;
    private int maxRows = 0;

    public UdspStatement(UdspConnection connection, ClientFactory factory, Client client,
                         RemotingUrl url) {
        this.connection = connection;
        this.factory = factory;
        this.client = client;
        this.url = url;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if (!execute (sql)) {
            throw new SQLException ("The query did not generate a result set!");
        }
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        execute (sql);
        return 0;
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed ()) {
            // 不做任何操作
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getMaxRows() throws SQLException {
        checkConnection ("getMaxRows");
        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkConnection ("setMaxRows");
        if (max < 0) {
            throw new SQLException ("max must be >= 0");
        }
        maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        if (enable) {
            throw new SQLException ("Method not supported");
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        checkConnection ("getQueryTimeout");
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void cancel() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkConnection ("getWarnings");
        return warningChain;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warningChain = null;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException ("Method not supported");
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkConnection ("execute");

        SqlRequest request = new SqlRequest ();
        request.setServiceName (url.getServiceName ());
        request.setEntity (ConsumerEntity.START.getValue ());
        request.setType (ConsumerType.SYNC.getValue ());
        request.setUdspUser (url.getUsername ());
        request.setToken (url.getPassword ());
        request.setSql (sql);
        JSONObject jsonObject = (JSONObject) JSON.toJSON (request);
        String reqJson = jsonObject.toJSONString ();
        try {
            String rspJson = client.send (reqJson);
            SyncResponse response = null;
            try {
                response = JSON.parseObject (rspJson, SyncResponse.class);
            } catch (Exception e) {
                if (e.toString ().contains ("unclosed")) {
                    throw new RuntimeException ("The returned result data is too large for the client to parse");
                } else {
                    throw new RuntimeException (e);
                }
            }
            if (!StatusCode.SUCCESS.getValue ().equals (response.getStatusCode ())) {
                throw new SQLException (response.getMessage ());
            }
            List<Map<String, String>> records = response.getRecords ();
            LinkedHashMap<String, String> columns = response.getReturnColumns ();
            List<String> columnNames = new ArrayList<> ();
            List<String> columnTypes = new ArrayList<> ();
            if (columns != null && columns.size () != 0) { // 有返回字段信息
                for (Map.Entry<String, String> entry : columns.entrySet ()) {
                    columnNames.add (entry.getKey ());
                    columnTypes.add (entry.getValue ());
                }
            } else if (records != null && records.size () != 0) { // 无返回字段信息
                for (Map.Entry<String, String> entry : records.get (0).entrySet ()) {
                    columnNames.add (entry.getKey ());
                    columnTypes.add (DataType.STRING.getValue ());
                }
            }
            resultSet = new UdspQueryResultSet
                    .Builder (this) //
                    .setClient (client) //
                    .setColumnNames (columnNames) //
                    .setColumnTypes (columnTypes) //
                    .setRecords (records) //
                    .build ();
        } catch (Exception e) {
            throw new SQLException (e);
        }

        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkConnection ("getResultSet");
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkConnection ("getUpdateCount");
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkConnection ("setFetchDirection");
        if (direction != ResultSet.FETCH_FORWARD) {
            throw new SQLException ("Not supported direction " + direction);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkConnection ("getFetchDirection");
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkConnection ("setFetchSize");
        fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkConnection ("getFetchSize");
        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getResultSetType() throws SQLException {
        checkConnection ("getResultSetType");
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    private void checkConnection(String action) throws SQLException {
        if (isClosed ()) {
            throw new SQLException ("Can't " + action + " after statement has been closed");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkConnection ("getConnection");
        return this.connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new SQLFeatureNotSupportedException ("Method not supported");
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException ("Method not supported");
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return client == null || !client.isConnected ();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException ("Cannot unwrap to " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
