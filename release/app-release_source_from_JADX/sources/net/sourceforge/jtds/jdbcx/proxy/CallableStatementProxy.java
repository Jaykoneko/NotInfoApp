package net.sourceforge.jtds.jdbcx.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;

public class CallableStatementProxy extends PreparedStatementProxy implements CallableStatement {
    private JtdsCallableStatement _callableStatement;

    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    CallableStatementProxy(ConnectionProxy connectionProxy, JtdsCallableStatement jtdsCallableStatement) {
        super(connectionProxy, jtdsCallableStatement);
        this._callableStatement = jtdsCallableStatement;
    }

    public void registerOutParameter(int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void registerOutParameter(int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2, i3);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public boolean wasNull() throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.wasNull();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String getString(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getString(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean getBoolean(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBoolean(i);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public byte getByte(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getByte(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Byte.MIN_VALUE;
        }
    }

    public short getShort(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getShort(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Short.MIN_VALUE;
        }
    }

    public int getInt(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getInt(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public long getLong(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getLong(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Long.MIN_VALUE;
        }
    }

    public float getFloat(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getFloat(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Float.MIN_VALUE;
        }
    }

    public double getDouble(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDouble(i);
        } catch (SQLException e) {
            processSQLException(e);
            return Double.MIN_VALUE;
        }
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(i, i2);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public byte[] getBytes(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBytes(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Date getDate(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Time getTime(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Object getObject(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Object getObject(int i, Map map) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(i, map);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Ref getRef(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getRef(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Blob getBlob(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBlob(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Clob getClob(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getClob(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Array getArray(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getArray(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(i, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(i, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(i, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void registerOutParameter(int i, int i2, String str) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2, str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void registerOutParameter(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void registerOutParameter(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void registerOutParameter(String str, int i, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i, str2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public URL getURL(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getURL(i);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void setURL(String str, URL url) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setURL(str, url);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setNull(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setNull(str, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBoolean(String str, boolean z) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBoolean(str, z);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setByte(String str, byte b) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setByte(str, b);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setShort(String str, short s) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setShort(str, s);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setInt(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setInt(str, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setLong(String str, long j) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setLong(str, j);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setFloat(String str, float f) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setFloat(str, f);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setDouble(String str, double d) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDouble(str, d);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBigDecimal(str, bigDecimal);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setString(String str, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setString(str, str2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBytes(String str, byte[] bArr) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBytes(str, bArr);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setDate(String str, Date date) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDate(str, date);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTime(String str, Time time) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTime(str, time);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTimestamp(str, timestamp);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setAsciiStream(str, inputStream, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBinaryStream(str, inputStream, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(String str, Object obj, int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj, i, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(String str, Object obj, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(String str, Object obj) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setCharacterStream(String str, Reader reader, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setCharacterStream(str, reader, i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setDate(String str, Date date, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDate(str, date, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTime(String str, Time time, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTime(str, time, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTimestamp(str, timestamp, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setNull(String str, int i, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setNull(str, i, str2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public String getString(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getString(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean getBoolean(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBoolean(str);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public byte getByte(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getByte(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Byte.MIN_VALUE;
        }
    }

    public short getShort(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getShort(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Short.MIN_VALUE;
        }
    }

    public int getInt(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getInt(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public long getLong(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getLong(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Long.MIN_VALUE;
        }
    }

    public float getFloat(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getFloat(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Float.MIN_VALUE;
        }
    }

    public double getDouble(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDouble(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Double.MIN_VALUE;
        }
    }

    public byte[] getBytes(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBytes(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Date getDate(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Time getTime(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Object getObject(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Object getObject(String str, Map map) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(str, map);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Ref getRef(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getRef(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Blob getBlob(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBlob(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Clob getClob(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getClob(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Array getArray(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getArray(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(str, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(str, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(str, calendar);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public URL getURL(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getURL(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Reader getCharacterStream(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public Reader getCharacterStream(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public Reader getNCharacterStream(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public Reader getNCharacterStream(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public NClob getNClob(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public NClob getNClob(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public String getNString(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public String getNString(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public RowId getRowId(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public RowId getRowId(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public SQLXML getSQLXML(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public SQLXML getSQLXML(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setAsciiStream(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setAsciiStream(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBinaryStream(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBinaryStream(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBlob(String str, Blob blob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBlob(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBlob(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setCharacterStream(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setCharacterStream(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClob(String str, Clob clob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClob(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClob(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNCharacterStream(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNCharacterStream(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(String str, NClob nClob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNString(String str, String str2) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setRowId(String str, RowId rowId) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setSQLXML(String str, SQLXML sqlxml) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setAsciiStream(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBinaryStream(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBlob(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setBlob(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setCharacterStream(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setCharacterStream(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClob(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClob(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNCharacterStream(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(int i, NClob nClob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNClob(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNString(int i, String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setRowId(int i, RowId rowId) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isClosed() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isPoolable() throws SQLException {
        throw new AbstractMethodError();
    }

    public void setPoolable(boolean z) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }
}
