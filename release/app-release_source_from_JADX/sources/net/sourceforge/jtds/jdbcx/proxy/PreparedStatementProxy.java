package net.sourceforge.jtds.jdbcx.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;

public class PreparedStatementProxy extends StatementProxy implements PreparedStatement {
    private JtdsPreparedStatement _preparedStatement;

    PreparedStatementProxy(ConnectionProxy connectionProxy, JtdsPreparedStatement jtdsPreparedStatement) {
        super(connectionProxy, jtdsPreparedStatement);
        this._preparedStatement = jtdsPreparedStatement;
    }

    public ResultSet executeQuery() throws SQLException {
        validateConnection();
        try {
            return this._preparedStatement.executeQuery();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int executeUpdate() throws SQLException {
        validateConnection();
        try {
            return this._preparedStatement.executeUpdate();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setNull(int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setNull(i, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBoolean(int i, boolean z) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setBoolean(i, z);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setByte(int i, byte b) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setByte(i, b);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setShort(int i, short s) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setShort(i, s);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setInt(int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setInt(i, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setLong(int i, long j) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setLong(i, j);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setFloat(int i, float f) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setFloat(i, f);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setDouble(int i, double d) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setDouble(i, d);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setBigDecimal(i, bigDecimal);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setString(int i, String str) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setString(i, str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBytes(int i, byte[] bArr) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setBytes(i, bArr);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setDate(i, date);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTime(int i, Time time) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setTime(i, time);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setTimestamp(i, timestamp);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setAsciiStream(i, inputStream, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setUnicodeStream(int i, InputStream inputStream, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setUnicodeStream(i, inputStream, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setBinaryStream(i, inputStream, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void clearParameters() throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.clearParameters();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(int i, Object obj, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setObject(i, obj, i2, i3);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(int i, Object obj, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setObject(i, obj, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setObject(int i, Object obj) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setObject(i, obj);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public boolean execute() throws SQLException {
        validateConnection();
        try {
            return this._preparedStatement.execute();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public void addBatch() throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.addBatch();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setCharacterStream(int i, Reader reader, int i2) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setCharacterStream(i, reader, i2);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setRef(int i, Ref ref) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setRef(i, ref);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setBlob(i, blob);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setClob(int i, Clob clob) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setClob(i, clob);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setArray(int i, Array array) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setArray(i, array);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        validateConnection();
        try {
            return this._preparedStatement.getMetaData();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setDate(i, date, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setTime(i, time, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setTimestamp(i, timestamp, calendar);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setNull(int i, int i2, String str) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setNull(i, i2, str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setURL(int i, URL url) throws SQLException {
        validateConnection();
        try {
            this._preparedStatement.setURL(i, url);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        validateConnection();
        try {
            return this._preparedStatement.getParameterMetaData();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
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
}
