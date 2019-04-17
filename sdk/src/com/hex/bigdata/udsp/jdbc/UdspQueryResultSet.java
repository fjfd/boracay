package com.hex.bigdata.udsp.jdbc;

import com.hex.bigdata.udsp.jdbc.netty.Client;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2018-7-26.
 */
public class UdspQueryResultSet implements ResultSet {
    private Connection connection;
    private Statement statement;
    private Client client;
    private boolean wasNull = false;
    private SQLWarning warningChain;
    private List<String> columnNames;
    private List<String> columnTypes;
    private Map<String, String> row;

    private int rowsFetched = 0;
    private boolean fetchFirst = false;
    private int maxRows;

    private List<Map<String, String>> records;
    private Iterator<Map<String, String>> iterator;

    @Override
    public boolean next() throws SQLException {
        maxRows = records.size ();
        if (records == null || maxRows == 0) {
            return false;
        }
        if (maxRows > 0 && rowsFetched >= maxRows) {
            return false;
        }
        if (fetchFirst) {
            rowsFetched = 0;
            fetchFirst = false;
        }
        if (iterator.hasNext ()) {
            row = iterator.next ();
        } else {
            return false;
        }
        rowsFetched++;
        return true;
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed()) {
            // 不做任何操作
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        Object value = getColumnValue (columnIndex);
        if (wasNull) {
            return null;
        }
        if (value instanceof byte[]) {
            return new String ((byte[]) value);
        }
        return value.toString ();
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        Object obj = getObject (columnIndex);
        if (Boolean.class.isInstance (obj)) {
            return (Boolean) obj;
        } else if (obj == null) {
            return false;
        } else if (Number.class.isInstance (obj)) {
            return ((Number) obj).intValue () != 0;
        } else if (String.class.isInstance (obj)) {
            return !((String) obj).equals ("0");
        }
        throw new SQLException ("Cannot convert column " + columnIndex + " to boolean");
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        Object obj = getObject (columnIndex);
        if (Number.class.isInstance (obj)) {
            return ((Number) obj).byteValue ();
        } else if (obj == null) {
            return 0;
        }
        throw new SQLException ("Cannot convert column " + columnIndex + " to byte");
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        try {
            Object obj = getObject (columnIndex);
            if (Number.class.isInstance (obj)) {
                return ((Number) obj).shortValue ();
            } else if (obj == null) {
                return 0;
            } else if (String.class.isInstance (obj)) {
                return Short.valueOf ((String) obj);
            }
            throw new Exception ("Illegal conversion");
        } catch (Exception e) {
            throw new SQLException ("Cannot convert column " + columnIndex
                    + " to short: " + e.toString (), e);
        }
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        try {
            Object obj = getObject (columnIndex);
            if (Number.class.isInstance (obj)) {
                return ((Number) obj).intValue ();
            } else if (obj == null) {
                return 0;
            } else if (String.class.isInstance (obj)) {
                return Integer.valueOf ((String) obj);
            }
            throw new Exception ("Illegal conversion");
        } catch (Exception e) {
            throw new SQLException (
                    "Cannot convert column " + columnIndex + " to integer" + e.toString (),
                    e);
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        try {
            Object obj = getObject (columnIndex);
            if (Number.class.isInstance (obj)) {
                return ((Number) obj).longValue ();
            } else if (obj == null) {
                return 0;
            } else if (String.class.isInstance (obj)) {
                return Long.valueOf ((String) obj);
            }
            throw new Exception ("Illegal conversion");
        } catch (Exception e) {
            throw new SQLException (
                    "Cannot convert column " + columnIndex + " to long: " + e.toString (),
                    e);
        }
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        try {
            Object obj = getObject (columnIndex);
            if (Number.class.isInstance (obj)) {
                return ((Number) obj).floatValue ();
            } else if (obj == null) {
                return 0;
            } else if (String.class.isInstance (obj)) {
                return Float.valueOf ((String) obj);
            }
            throw new Exception ("Illegal conversion");
        } catch (Exception e) {
            throw new SQLException ("Cannot convert column " + columnIndex
                    + " to float: " + e.toString (), e);
        }
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        try {
            Object obj = getObject (columnIndex);
            if (Number.class.isInstance (obj)) {
                return ((Number) obj).doubleValue ();
            } else if (obj == null) {
                return 0;
            } else if (String.class.isInstance (obj)) {
                return Double.valueOf ((String) obj);
            }
            throw new Exception ("Illegal conversion");
        } catch (Exception e) {
            throw new SQLException ("Cannot convert column " + columnIndex
                    + " to double: " + e.toString (), e);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        Object val = getObject (columnIndex);

        if (val == null || val instanceof BigDecimal) {
            return (BigDecimal) val;
        }

        throw new SQLException ("Illegal conversion");
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        Object obj = getObject (columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        try {
            if (obj instanceof String) {
                return Date.valueOf ((String) obj);
            }
        } catch (Exception e) {
            throw new SQLException ("Cannot convert column " + columnIndex
                    + " to date: " + e.toString (), e);
        }
        // If we fell through to here this is not a valid type conversion
        throw new SQLException ("Cannot convert column " + columnIndex
                + " to date: Illegal conversion");
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        Object obj = getObject (columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Timestamp) {
            return (Timestamp) obj;
        }
        if (obj instanceof String) {
            return Timestamp.valueOf ((String) obj);
        }
        throw new SQLException ("Illegal conversion");
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        Object obj = getObject (columnIndex);
        if (obj == null) {
            return null;
        } else if (obj instanceof InputStream) {
            return (InputStream) obj;
        } else if (obj instanceof byte[]) {
            byte[] byteArray = (byte[]) obj;
            InputStream is = new ByteArrayInputStream (byteArray);
            return is;
        } else if (obj instanceof String) {
            String str = (String) obj;
            InputStream is = null;
            try {
                is = new ByteArrayInputStream (str.getBytes ("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new SQLException ("Illegal conversion to binary stream from column " +
                        columnIndex + " - Unsupported encoding exception");
            }
            return is;
        }
        throw new SQLException ("Illegal conversion to binary stream from column " + columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString (findColumn (columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean (findColumn (columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte (findColumn (columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort (findColumn (columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt (findColumn (columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong (findColumn (columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat (findColumn (columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble (findColumn (columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal (findColumn (columnLabel), scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate (findColumn (columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp (findColumn (columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream (findColumn (columnLabel));
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
    public String getCursorName() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new UdspResultSetMetaData (columnNames, columnTypes);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return getColumnValue (columnIndex);
    }

    private Object getColumnValue(int columnIndex) throws SQLException {
        if (row == null) {
            throw new SQLException ("No row found.");
        }
        if (row.size () == 0) {
            throw new SQLException ("RowSet does not contain any columns!");
        }
        if (columnIndex > row.size ()) {
            throw new SQLException ("Invalid columnIndex: " + columnIndex);
        }

        String columnType = columnTypes.get (columnIndex - 1);
        String columnName = columnNames.get (columnIndex - 1);

        try {
            Object evaluated = evaluate (columnType, row.get (columnName));
            wasNull = evaluated == null;
            return evaluated;
        } catch (Exception e) {
            e.printStackTrace ();
            throw new SQLException ("Unrecognized column type:" + columnType, e);
        }
    }

    private Object evaluate(String type, String value) {
        if (StringUtils.isBlank (value)) {
            return null;
        }
        if ("TIMESTAMP".equals (type)) {
            return Timestamp.valueOf (value);
        } else if ("DECIMAL".equals (type)) {
            return new BigDecimal (value);
        } else if ("DATE".equals (type)) {
            return Date.valueOf (value);
        } else if ("BINARY".equals (type)) {
            return value.getBytes ();
        } else if ("BINARY".equals (type)) {
            return value.getBytes ();
        } else if ("DOUBLE".equals (type)) {
            return Double.valueOf (value);
        } else if ("FLOAT".equals (type)) {
            return Float.valueOf (value);
        } else if ("INT".equals (type)) {
            return Integer.valueOf (value);
        } else if ("BOOLEAN".equals (type)) {
            return Boolean.valueOf (value);
        } else if ("TINYINT".equals (type) || "SMALLINT".equals (type)) {
            return Short.valueOf (value);
        } else if ("BIGINT".equals (type)) {
            return Long.valueOf (value);
        }
        return value;
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject (findColumn (columnLabel));
    }

    public String findColumn(int columnIndex) {
        return columnNames.get (columnIndex - 1);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        int columnIndex = 0;
        boolean findColumn = false;
        for (String columnName : columnNames) {
            ++columnIndex;
            String[] names = columnName.split ("\\.");
            String name = names[names.length - 1];
            if (name.equalsIgnoreCase (columnLabel) || columnName.equalsIgnoreCase (columnLabel)) {
                findColumn = true;
                break;
            }
        }
        if (!findColumn) {
            throw new SQLException ("Could not find " + columnLabel + " in " + columnNames);
        } else {
            return columnIndex;
        }
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        Object val = getObject (columnIndex);

        if (val == null || val instanceof BigDecimal) {
            return (BigDecimal) val;
        }

        throw new SQLException ("Illegal conversion");
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal (findColumn (columnLabel));
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isLast() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void beforeFirst() throws SQLException {
        fetchFirst = true;
        rowsFetched = 0;
    }

    @Override
    public void afterLast() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean previous() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Statement getStatement() throws SQLException {
        return this.statement;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return true;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException ("Method not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException ("Method not supported");
    }


    public static class Builder {
        private final Connection connection;
        private final Statement statement;
        private Client client;

        private List<String> columnNames;
        private List<String> columnTypes;

        private List<Map<String, String>> records;

        public Builder(Statement statement) throws SQLException {
            this.statement = statement;
            this.connection = statement.getConnection ();
        }

        public Builder setClient(Client client) {
            this.client = client;
            return this;
        }

        public Builder setColumnNames(List<String> columnNames) {
            this.columnNames = columnNames;
            return this;
        }

        public Builder setColumnTypes(List<String> columnTypes) {
            this.columnTypes = columnTypes;
            return this;
        }

        public Builder setRecords(List<Map<String, String>> records) {
            this.records = records;
            return this;
        }

        public UdspQueryResultSet build() throws SQLException {
            return new UdspQueryResultSet (this);
        }
    }

    protected UdspQueryResultSet(Builder builder) throws SQLException {
        this.connection = builder.connection;
        this.statement = builder.statement;
        this.client = builder.client;
        this.columnNames = builder.columnNames;
        this.columnTypes = builder.columnTypes;
        this.records = builder.records;
        this.maxRows = 0;
        if (this.records != null && this.records.size () != 0) {
            this.maxRows = this.records.size ();
            this.iterator = this.records.iterator ();
        }
    }
}
