package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class JtdsCallableStatement extends JtdsPreparedStatement implements CallableStatement {
    protected boolean paramWasNull;

    JtdsCallableStatement(JtdsConnection jtdsConnection, String str, int i, int i2) throws SQLException {
        super(jtdsConnection, str, i, i2, false);
    }

    /* access modifiers changed from: 0000 */
    public final int findParameter(String str, boolean z) throws SQLException {
        checkOpen();
        String str2 = "@";
        if (!str.startsWith(str2)) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(str);
            str = sb.toString();
        }
        for (int i = 0; i < this.parameters.length; i++) {
            if (this.parameters[i].name != null && this.parameters[i].name.equalsIgnoreCase(str)) {
                return i + 1;
            }
        }
        if (z && !str.equalsIgnoreCase("@return_status")) {
            for (int i2 = 0; i2 < this.parameters.length; i2++) {
                if (this.parameters[i2].name == null) {
                    this.parameters[i2].name = str;
                    return i2 + 1;
                }
            }
        }
        throw new SQLException(Messages.get("error.callable.noparam", (Object) str), "07000");
    }

    /* access modifiers changed from: protected */
    public Object getOutputValue(int i) throws SQLException {
        checkOpen();
        ParamInfo parameter = getParameter(i);
        if (parameter.isOutput) {
            Object outValue = parameter.getOutValue();
            this.paramWasNull = outValue == null;
            return outValue;
        }
        throw new SQLException(Messages.get("error.callable.notoutput", (Object) new Integer(i)), "07000");
    }

    /* access modifiers changed from: protected */
    public void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "CallableStatement"), "HY010");
        }
    }

    /* access modifiers changed from: protected */
    public SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        int i3 = i;
        if (this.parameters.length == 0) {
            return super.executeMSBatch(i, i2, arrayList);
        }
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            Object obj = this.batchValues.get(i4);
            i4++;
            boolean z = i4 % i2 == 0 || i4 == i3;
            this.tds.startBatch();
            this.tds.executeSQL(this.sql, this.procName, (ParamInfo[]) obj, false, 0, -1, -1, z);
            if (z) {
                sQLException = this.tds.getBatchCounts(arrayList, sQLException);
                if (!(sQLException == null || arrayList.size() == i4)) {
                    break;
                }
            } else {
                ArrayList arrayList2 = arrayList;
            }
        }
        return sQLException;
    }

    /* access modifiers changed from: protected */
    public SQLException executeSybaseBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(i, i2, arrayList);
        }
        SQLException sQLException = null;
        int i3 = 0;
        while (i3 < i) {
            Object obj = this.batchValues.get(i3);
            i3++;
            this.tds.executeSQL(this.sql, this.procName, (ParamInfo[]) obj, false, 0, -1, -1, true);
            sQLException = this.tds.getBatchCounts(arrayList, sQLException);
            if (sQLException != null && arrayList.size() != i3) {
                break;
            }
        }
        return sQLException;
    }

    public boolean wasNull() throws SQLException {
        checkOpen();
        return this.paramWasNull;
    }

    public byte getByte(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), -6, null)).byteValue();
    }

    public double getDouble(int i) throws SQLException {
        return ((Double) Support.convert(this, getOutputValue(i), 8, null)).doubleValue();
    }

    public float getFloat(int i) throws SQLException {
        return ((Float) Support.convert(this, getOutputValue(i), 7, null)).floatValue();
    }

    public int getInt(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), 4, null)).intValue();
    }

    public long getLong(int i) throws SQLException {
        return ((Long) Support.convert(this, getOutputValue(i), -5, null)).longValue();
    }

    public short getShort(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), 5, null)).shortValue();
    }

    public boolean getBoolean(int i) throws SQLException {
        return ((Boolean) Support.convert(this, getOutputValue(i), 16, null)).booleanValue();
    }

    public byte[] getBytes(int i) throws SQLException {
        checkOpen();
        return (byte[]) Support.convert(this, getOutputValue(i), -3, this.connection.getCharset());
    }

    public void registerOutParameter(int i, int i2) throws SQLException {
        if (i2 == 3 || i2 == 2) {
            registerOutParameter(i, i2, 10);
        } else {
            registerOutParameter(i, i2, 0);
        }
    }

    public void registerOutParameter(int i, int i2, int i3) throws SQLException {
        checkOpen();
        String str = "HY092";
        if (i3 < 0 || i3 > this.connection.getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), str);
        }
        ParamInfo parameter = getParameter(i);
        parameter.isOutput = true;
        if (!"ERROR".equals(Support.getJdbcTypeName(i2))) {
            if (i2 == 2005) {
                parameter.jdbcType = -1;
            } else if (i2 == 2004) {
                parameter.jdbcType = -4;
            } else {
                parameter.jdbcType = i2;
            }
            parameter.scale = i3;
            return;
        }
        throw new SQLException(Messages.get("error.generic.badtype", (Object) Integer.toString(i2)), str);
    }

    public Object getObject(int i) throws SQLException {
        Object outputValue = getOutputValue(i);
        if (outputValue instanceof UniqueIdentifier) {
            return outputValue.toString();
        }
        if (!this.connection.getUseLOBs()) {
            outputValue = Support.convertLOB(outputValue);
        }
        return outputValue;
    }

    public String getString(int i) throws SQLException {
        checkOpen();
        return (String) Support.convert(this, getOutputValue(i), 12, this.connection.getCharset());
    }

    public void registerOutParameter(int i, int i2, String str) throws SQLException {
        notImplemented("CallableStatement.registerOutParameter(int, int, String");
    }

    public byte getByte(String str) throws SQLException {
        return getByte(findParameter(str, false));
    }

    public double getDouble(String str) throws SQLException {
        return getDouble(findParameter(str, false));
    }

    public float getFloat(String str) throws SQLException {
        return getFloat(findParameter(str, false));
    }

    public int getInt(String str) throws SQLException {
        return getInt(findParameter(str, false));
    }

    public long getLong(String str) throws SQLException {
        return getLong(findParameter(str, false));
    }

    public short getShort(String str) throws SQLException {
        return getShort(findParameter(str, false));
    }

    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(findParameter(str, false));
    }

    public byte[] getBytes(String str) throws SQLException {
        return getBytes(findParameter(str, false));
    }

    public void setByte(String str, byte b) throws SQLException {
        setByte(findParameter(str, true), b);
    }

    public void setDouble(String str, double d) throws SQLException {
        setDouble(findParameter(str, true), d);
    }

    public void setFloat(String str, float f) throws SQLException {
        setFloat(findParameter(str, true), f);
    }

    public void registerOutParameter(String str, int i) throws SQLException {
        registerOutParameter(findParameter(str, true), i);
    }

    public void setInt(String str, int i) throws SQLException {
        setInt(findParameter(str, true), i);
    }

    public void setNull(String str, int i) throws SQLException {
        setNull(findParameter(str, true), i);
    }

    public void registerOutParameter(String str, int i, int i2) throws SQLException {
        registerOutParameter(findParameter(str, true), i, i2);
    }

    public void setLong(String str, long j) throws SQLException {
        setLong(findParameter(str, true), j);
    }

    public void setShort(String str, short s) throws SQLException {
        setShort(findParameter(str, true), s);
    }

    public void setBoolean(String str, boolean z) throws SQLException {
        setBoolean(findParameter(str, true), z);
    }

    public void setBytes(String str, byte[] bArr) throws SQLException {
        setBytes(findParameter(str, true), bArr);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return (BigDecimal) Support.convert(this, getOutputValue(i), 3, null);
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        return ((BigDecimal) Support.convert(this, getOutputValue(i), 3, null)).setScale(i2);
    }

    public URL getURL(int i) throws SQLException {
        checkOpen();
        String str = (String) Support.convert(this, getOutputValue(i), 12, this.connection.getCharset());
        try {
            return new URL(str);
        } catch (MalformedURLException unused) {
            throw new SQLException(Messages.get("error.resultset.badurl", (Object) str), "22000");
        }
    }

    public Array getArray(int i) throws SQLException {
        notImplemented("CallableStatement.getArray");
        return null;
    }

    public Blob getBlob(int i) throws SQLException {
        byte[] bytes = getBytes(i);
        if (bytes == null) {
            return null;
        }
        return new BlobImpl(this.connection, bytes);
    }

    public Clob getClob(int i) throws SQLException {
        String string = getString(i);
        if (string == null) {
            return null;
        }
        return new ClobImpl(this.connection, string);
    }

    public Date getDate(int i) throws SQLException {
        return (Date) Support.convert(this, getOutputValue(i), 91, null);
    }

    public Ref getRef(int i) throws SQLException {
        notImplemented("CallableStatement.getRef");
        return null;
    }

    public Time getTime(int i) throws SQLException {
        return (Time) Support.convert(this, getOutputValue(i), 92, null);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return (Timestamp) Support.convert(this, getOutputValue(i), 93, null);
    }

    public void setAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        setAsciiStream(findParameter(str, true), inputStream, i);
    }

    public void setBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        setBinaryStream(findParameter(str, true), inputStream, i);
    }

    public void setCharacterStream(String str, Reader reader, int i) throws SQLException {
        setCharacterStream(findParameter(str, true), reader, i);
    }

    public Object getObject(String str) throws SQLException {
        return getObject(findParameter(str, false));
    }

    public void setObject(String str, Object obj) throws SQLException {
        setObject(findParameter(str, true), obj);
    }

    public void setObject(String str, Object obj, int i) throws SQLException {
        setObject(findParameter(str, true), obj, i);
    }

    public void setObject(String str, Object obj, int i, int i2) throws SQLException {
        setObject(findParameter(str, true), obj, i, i2);
    }

    public Object getObject(int i, Map map) throws SQLException {
        notImplemented("CallableStatement.getObject(int, Map)");
        return null;
    }

    public String getString(String str) throws SQLException {
        return getString(findParameter(str, false));
    }

    public void registerOutParameter(String str, int i, String str2) throws SQLException {
        notImplemented("CallableStatement.registerOutParameter(String, int, String");
    }

    public void setNull(String str, int i, String str2) throws SQLException {
        notImplemented("CallableStatement.setNull(String, int, String");
    }

    public void setString(String str, String str2) throws SQLException {
        setString(findParameter(str, true), str2);
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(findParameter(str, false));
    }

    public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        setBigDecimal(findParameter(str, true), bigDecimal);
    }

    public URL getURL(String str) throws SQLException {
        return getURL(findParameter(str, false));
    }

    public void setURL(String str, URL url) throws SQLException {
        setObject(findParameter(str, true), url);
    }

    public Array getArray(String str) throws SQLException {
        return getArray(findParameter(str, false));
    }

    public Blob getBlob(String str) throws SQLException {
        return getBlob(findParameter(str, false));
    }

    public Clob getClob(String str) throws SQLException {
        return getClob(findParameter(str, false));
    }

    public Date getDate(String str) throws SQLException {
        return getDate(findParameter(str, false));
    }

    public void setDate(String str, Date date) throws SQLException {
        setDate(findParameter(str, true), date);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        Date date = getDate(i);
        return (date == null || calendar == null) ? date : new Date(Support.timeToZone(date, calendar));
    }

    public Ref getRef(String str) throws SQLException {
        return getRef(findParameter(str, false));
    }

    public Time getTime(String str) throws SQLException {
        return getTime(findParameter(str, false));
    }

    public void setTime(String str, Time time) throws SQLException {
        setTime(findParameter(str, true), time);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        Time time = getTime(i);
        return (time == null || calendar == null) ? time : new Time(Support.timeToZone(time, calendar));
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(findParameter(str, false));
    }

    public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
        setTimestamp(findParameter(str, true), timestamp);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        Timestamp timestamp = getTimestamp(i);
        return (timestamp == null || calendar == null) ? timestamp : new Timestamp(Support.timeToZone(timestamp, calendar));
    }

    public Object getObject(String str, Map map) throws SQLException {
        return getObject(findParameter(str, false), map);
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        return getDate(findParameter(str, false), calendar);
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        return getTime(findParameter(str, false), calendar);
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        return getTimestamp(findParameter(str, false), calendar);
    }

    public void setDate(String str, Date date, Calendar calendar) throws SQLException {
        setDate(findParameter(str, true), date, calendar);
    }

    public void setTime(String str, Time time, Calendar calendar) throws SQLException {
        setTime(findParameter(str, true), time, calendar);
    }

    public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
        setTimestamp(findParameter(str, true), timestamp, calendar);
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

    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }
}
