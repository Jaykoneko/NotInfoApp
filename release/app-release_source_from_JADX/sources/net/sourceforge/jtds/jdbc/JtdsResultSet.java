package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JtdsResultSet implements ResultSet {
    static final int CLOSE_CURSORS_AT_COMMIT = 2;
    static final int HOLD_CURSORS_OVER_COMMIT = 1;
    protected static final int INITIAL_ROW_COUNT = 1000;
    protected static final int POS_AFTER_LAST = -1;
    protected static final int POS_BEFORE_FIRST = 0;

    /* renamed from: f */
    private static NumberFormat f115f = NumberFormat.getInstance();
    protected boolean cancelled;
    protected boolean closed;
    protected int columnCount;
    private HashMap columnMap;
    protected ColInfo[] columns;
    protected int concurrency;
    protected Object[] currentRow;
    protected String cursorName;
    protected int direction = 1000;
    protected int fetchDirection = 1000;
    protected int fetchSize;
    protected int pos = 0;
    protected int resultSetType;
    protected ArrayList rowData;
    protected int rowPtr;
    protected int rowsInResult;
    protected JtdsStatement statement;
    protected boolean wasNull;

    JtdsResultSet(JtdsStatement jtdsStatement, int i, int i2, ColInfo[] colInfoArr) throws SQLException {
        if (jtdsStatement != null) {
            this.statement = jtdsStatement;
            this.resultSetType = i;
            this.concurrency = i2;
            this.columns = colInfoArr;
            this.fetchSize = jtdsStatement.fetchSize;
            this.fetchDirection = jtdsStatement.fetchDirection;
            this.cursorName = jtdsStatement.cursorName;
            if (colInfoArr != null) {
                this.columnCount = getColumnCount(colInfoArr);
                this.rowsInResult = jtdsStatement.getTds().isDataInResultSet() ? 1 : 0;
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Statement parameter must not be null");
    }

    protected static int getColumnCount(ColInfo[] colInfoArr) {
        int length = colInfoArr.length - 1;
        while (length >= 0 && colInfoArr[length].isHidden) {
            length--;
        }
        return length + 1;
    }

    /* access modifiers changed from: protected */
    public ColInfo[] getColumns() {
        return this.columns;
    }

    /* access modifiers changed from: protected */
    public void setColName(int i, String str) {
        if (i >= 1) {
            ColInfo[] colInfoArr = this.columns;
            if (i <= colInfoArr.length) {
                colInfoArr[i - 1].realName = str;
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("columnIndex ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void setColLabel(int i, String str) {
        if (i >= 1) {
            ColInfo[] colInfoArr = this.columns;
            if (i <= colInfoArr.length) {
                colInfoArr[i - 1].name = str;
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("columnIndex ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void setColType(int i, int i2) {
        if (i >= 1) {
            ColInfo[] colInfoArr = this.columns;
            if (i <= colInfoArr.length) {
                colInfoArr[i - 1].jdbcType = i2;
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("columnIndex ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        DateTime dateTime;
        checkOpen();
        checkUpdateable();
        if (i < 1 || i > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", (Object) Integer.toString(i)), "07009");
        }
        if (obj instanceof Timestamp) {
            dateTime = new DateTime((Timestamp) obj);
        } else if (obj instanceof Date) {
            dateTime = new DateTime((Date) obj);
        } else if (!(obj instanceof Time)) {
            return obj;
        } else {
            dateTime = new DateTime((Time) obj);
        }
        return dateTime;
    }

    /* access modifiers changed from: protected */
    public void setColumnCount(int i) {
        if (i < 1 || i > this.columns.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("columnCount ");
            sb.append(i);
            sb.append(" is invalid");
            throw new IllegalArgumentException(sb.toString());
        }
        this.columnCount = i;
    }

    /* access modifiers changed from: protected */
    public Object getColumn(int i) throws SQLException {
        checkOpen();
        boolean z = true;
        if (i < 1 || i > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", (Object) Integer.toString(i)), "07009");
        }
        Object[] objArr = this.currentRow;
        if (objArr != null) {
            Object obj = objArr[i - 1];
            if (obj != null) {
                z = false;
            }
            this.wasNull = z;
            return obj;
        }
        throw new SQLException(Messages.get("error.resultset.norow"), "24000");
    }

    /* access modifiers changed from: protected */
    public void checkOpen() throws SQLException {
        String str = "HY010";
        String str2 = "ResultSet";
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) str2), str);
        } else if (this.cancelled) {
            throw new SQLException(Messages.get("error.generic.cancelled", (Object) str2), str);
        }
    }

    /* access modifiers changed from: protected */
    public void checkScrollable() throws SQLException {
        if (this.resultSetType == 1003) {
            throw new SQLException(Messages.get("error.resultset.fwdonly"), "24000");
        }
    }

    /* access modifiers changed from: protected */
    public void checkUpdateable() throws SQLException {
        if (this.concurrency == 1007) {
            throw new SQLException(Messages.get("error.resultset.readonly"), "24000");
        }
    }

    protected static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    /* access modifiers changed from: protected */
    public Object[] newRow() {
        return new Object[this.columns.length];
    }

    /* access modifiers changed from: protected */
    public Object[] copyRow(Object[] objArr) {
        Object[] objArr2 = new Object[this.columns.length];
        System.arraycopy(objArr, 0, objArr2, 0, objArr.length);
        return objArr2;
    }

    /* access modifiers changed from: protected */
    public ColInfo[] copyInfo(ColInfo[] colInfoArr) {
        ColInfo[] colInfoArr2 = new ColInfo[colInfoArr.length];
        System.arraycopy(colInfoArr, 0, colInfoArr2, 0, colInfoArr.length);
        return colInfoArr2;
    }

    /* access modifiers changed from: protected */
    public Object[] getCurrentRow() {
        return this.currentRow;
    }

    /* access modifiers changed from: protected */
    public void cacheResultSetRows() throws SQLException {
        if (this.rowData == null) {
            this.rowData = new ArrayList(1000);
        }
        Object[] objArr = this.currentRow;
        if (objArr != null) {
            this.currentRow = copyRow(objArr);
        }
        while (this.statement.getTds().getNextRow()) {
            this.rowData.add(copyRow(this.statement.getTds().getRowData()));
        }
        this.statement.cacheResults();
    }

    private JtdsConnection getConnection() throws SQLException {
        return (JtdsConnection) this.statement.getConnection();
    }

    public int getConcurrency() throws SQLException {
        checkOpen();
        return this.concurrency;
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return this.fetchDirection;
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return this.fetchSize;
    }

    public int getRow() throws SQLException {
        checkOpen();
        int i = this.pos;
        if (i > 0) {
            return i;
        }
        return 0;
    }

    public int getType() throws SQLException {
        checkOpen();
        return this.resultSetType;
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
        this.statement.clearWarnings();
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!getConnection().isClosed()) {
                    do {
                    } while (next());
                }
            } finally {
                this.closed = true;
                this.statement = null;
            }
        }
    }

    public void deleteRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void insertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void updateRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean isAfterLast() throws SQLException {
        checkOpen();
        return this.pos == -1 && this.rowsInResult != 0;
    }

    public boolean isBeforeFirst() throws SQLException {
        checkOpen();
        return this.pos == 0 && this.rowsInResult != 0;
    }

    public boolean isFirst() throws SQLException {
        checkOpen();
        return this.pos == 1;
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        if (this.statement.getTds().isDataInResultSet()) {
            this.rowsInResult = this.pos + 1;
        }
        int i = this.pos;
        int i2 = this.rowsInResult;
        if (i != i2 || i2 == 0) {
            return false;
        }
        return true;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean next() throws SQLException {
        checkOpen();
        boolean z = false;
        if (this.pos == -1) {
            return false;
        }
        try {
            if (this.rowData != null) {
                if (this.rowPtr < this.rowData.size()) {
                    this.currentRow = (Object[]) this.rowData.get(this.rowPtr);
                    ArrayList arrayList = this.rowData;
                    int i = this.rowPtr;
                    this.rowPtr = i + 1;
                    arrayList.set(i, null);
                    int i2 = this.pos + 1;
                    this.pos = i2;
                    this.rowsInResult = i2;
                } else {
                    this.pos = -1;
                    this.currentRow = null;
                }
            } else if (!this.statement.getTds().getNextRow()) {
                this.statement.cacheResults();
                this.pos = -1;
                this.currentRow = null;
            } else {
                this.currentRow = this.statement.getTds().getRowData();
                int i3 = this.pos + 1;
                this.pos = i3;
                this.rowsInResult = i3;
            }
            this.statement.getMessages().checkErrors();
            if (this.currentRow != null) {
                z = true;
            }
            return z;
        } catch (NullPointerException unused) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "ResultSet"), "HY010");
        }
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean rowInserted() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean rowUpdated() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean wasNull() throws SQLException {
        checkOpen();
        return this.wasNull;
    }

    public byte getByte(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), -6, null)).byteValue();
    }

    public short getShort(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), 5, null)).shortValue();
    }

    public int getInt(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), 4, null)).intValue();
    }

    public long getLong(int i) throws SQLException {
        return ((Long) Support.convert(this, getColumn(i), -5, null)).longValue();
    }

    public float getFloat(int i) throws SQLException {
        return ((Float) Support.convert(this, getColumn(i), 7, null)).floatValue();
    }

    public double getDouble(int i) throws SQLException {
        return ((Double) Support.convert(this, getColumn(i), 8, null)).doubleValue();
    }

    public void setFetchDirection(int i) throws SQLException {
        checkOpen();
        String str = "24000";
        switch (i) {
            case 1000:
                break;
            case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
            case PointerIconCompat.TYPE_HAND /*1002*/:
                if (this.resultSetType == 1003) {
                    throw new SQLException(Messages.get("error.resultset.fwdonly"), str);
                }
                break;
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "direction"), str);
        }
        this.fetchDirection = i;
    }

    public void setFetchSize(int i) throws SQLException {
        checkOpen();
        if (i < 0 || (this.statement.getMaxRows() > 0 && i > this.statement.getMaxRows())) {
            throw new SQLException(Messages.get("error.generic.badparam", Integer.toString(i), "rows"), "HY092");
        }
        if (i == 0) {
            i = this.statement.getDefaultFetchSize();
        }
        this.fetchSize = i;
    }

    public void updateNull(int i) throws SQLException {
        setColValue(i, 0, null, 0);
    }

    public boolean absolute(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean getBoolean(int i) throws SQLException {
        return ((Boolean) Support.convert(this, getColumn(i), 16, null)).booleanValue();
    }

    public boolean relative(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public byte[] getBytes(int i) throws SQLException {
        checkOpen();
        return (byte[]) Support.convert(this, getColumn(i), -2, getConnection().getCharset());
    }

    public void updateByte(int i, byte b) throws SQLException {
        setColValue(i, 4, new Integer(b & 255), 0);
    }

    public void updateDouble(int i, double d) throws SQLException {
        setColValue(i, 8, new Double(d), 0);
    }

    public void updateFloat(int i, float f) throws SQLException {
        setColValue(i, 7, new Float(f), 0);
    }

    public void updateInt(int i, int i2) throws SQLException {
        setColValue(i, 4, new Integer(i2), 0);
    }

    public void updateLong(int i, long j) throws SQLException {
        setColValue(i, -5, new Long(j), 0);
    }

    public void updateShort(int i, short s) throws SQLException {
        setColValue(i, 4, new Integer(s), 0);
    }

    public void updateBoolean(int i, boolean z) throws SQLException {
        setColValue(i, -7, z ? Boolean.TRUE : Boolean.FALSE, 0);
    }

    public void updateBytes(int i, byte[] bArr) throws SQLException {
        setColValue(i, -3, bArr, bArr != null ? bArr.length : 0);
    }

    public InputStream getAsciiStream(int i) throws SQLException {
        Clob clob = getClob(i);
        if (clob == null) {
            return null;
        }
        return clob.getAsciiStream();
    }

    public InputStream getBinaryStream(int i) throws SQLException {
        Blob blob = getBlob(i);
        if (blob == null) {
            return null;
        }
        return blob.getBinaryStream();
    }

    public InputStream getUnicodeStream(int i) throws SQLException {
        ClobImpl clobImpl = (ClobImpl) getClob(i);
        if (clobImpl == null) {
            return null;
        }
        return clobImpl.getBlobBuffer().getUnicodeStream();
    }

    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        if (inputStream == null || i2 < 0) {
            updateCharacterStream(i, (Reader) null, 0);
            return;
        }
        try {
            updateCharacterStream(i, (Reader) new InputStreamReader(inputStream, "US-ASCII"), i2);
        } catch (UnsupportedEncodingException unused) {
        }
    }

    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        if (inputStream == null || i2 < 0) {
            updateBytes(i, (byte[]) null);
        } else {
            setColValue(i, -3, inputStream, i2);
        }
    }

    public Reader getCharacterStream(int i) throws SQLException {
        Clob clob = getClob(i);
        if (clob == null) {
            return null;
        }
        return clob.getCharacterStream();
    }

    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        if (reader == null || i2 < 0) {
            updateString(i, (String) null);
        } else {
            setColValue(i, 12, reader, i2);
        }
    }

    public Object getObject(int i) throws SQLException {
        Object column = getColumn(i);
        if (column instanceof UniqueIdentifier) {
            return column.toString();
        }
        if (column instanceof DateTime) {
            return ((DateTime) column).toObject();
        }
        if (!getConnection().getUseLOBs()) {
            column = Support.convertLOB(column);
        }
        return column;
    }

    public void updateObject(int i, Object obj) throws SQLException {
        int i2;
        Object characterStream;
        long length;
        checkOpen();
        int i3 = 0;
        if (obj != null) {
            i2 = Support.getJdbcType(obj);
            if (obj instanceof BigDecimal) {
                obj = Support.normalizeBigDecimal((BigDecimal) obj, getConnection().getMaxPrecision());
            } else {
                if (obj instanceof Blob) {
                    Blob blob = (Blob) obj;
                    characterStream = blob.getBinaryStream();
                    length = blob.length();
                } else if (obj instanceof Clob) {
                    Clob clob = (Clob) obj;
                    characterStream = clob.getCharacterStream();
                    length = clob.length();
                } else if (obj instanceof String) {
                    i3 = ((String) obj).length();
                } else if (obj instanceof byte[]) {
                    i3 = ((byte[]) obj).length;
                }
                Object obj2 = characterStream;
                i3 = (int) length;
                obj = obj2;
            }
            if (i2 == 2000) {
                if (i < 1 || i > this.columnCount) {
                    throw new SQLException(Messages.get("error.resultset.colindex", (Object) Integer.toString(i)), "07009");
                }
                throw new SQLException(Messages.get("error.convert.badtypes", obj.getClass().getName(), Support.getJdbcTypeName(this.columns[i - 1].jdbcType)), "22005");
            }
        } else {
            i2 = 12;
        }
        setColValue(i, i2, obj, i3);
    }

    public void updateObject(int i, Object obj, int i2) throws SQLException {
        checkOpen();
        if (i2 < 0 || i2 > getConnection().getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
        } else if (obj instanceof BigDecimal) {
            updateObject(i, (Object) ((BigDecimal) obj).setScale(i2, 4));
        } else if (obj instanceof Number) {
            synchronized (f115f) {
                f115f.setGroupingUsed(false);
                f115f.setMaximumFractionDigits(i2);
                updateObject(i, (Object) f115f.format(obj));
            }
        } else {
            updateObject(i, obj);
        }
    }

    public String getCursorName() throws SQLException {
        checkOpen();
        String str = this.cursorName;
        if (str != null) {
            return str;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }

    public String getString(int i) throws SQLException {
        Object column = getColumn(i);
        if (column instanceof String) {
            return (String) column;
        }
        return (String) Support.convert(this, column, 12, getConnection().getCharset());
    }

    public void updateString(int i, String str) throws SQLException {
        setColValue(i, 12, str, str != null ? str.length() : 0);
    }

    public byte getByte(String str) throws SQLException {
        return getByte(findColumn(str));
    }

    public double getDouble(String str) throws SQLException {
        return getDouble(findColumn(str));
    }

    public float getFloat(String str) throws SQLException {
        return getFloat(findColumn(str));
    }

    public int findColumn(String str) throws SQLException {
        checkOpen();
        HashMap hashMap = this.columnMap;
        if (hashMap == null) {
            this.columnMap = new HashMap(this.columnCount);
        } else {
            Object obj = hashMap.get(str);
            if (obj != null) {
                return ((Integer) obj).intValue();
            }
        }
        for (int i = 0; i < this.columnCount; i++) {
            if (this.columns[i].name.equalsIgnoreCase(str)) {
                int i2 = i + 1;
                this.columnMap.put(str, new Integer(i2));
                return i2;
            }
        }
        throw new SQLException(Messages.get("error.resultset.colname", (Object) str), "07009");
    }

    public int getInt(String str) throws SQLException {
        return getInt(findColumn(str));
    }

    public long getLong(String str) throws SQLException {
        return getLong(findColumn(str));
    }

    public short getShort(String str) throws SQLException {
        return getShort(findColumn(str));
    }

    public void updateNull(String str) throws SQLException {
        updateNull(findColumn(str));
    }

    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(findColumn(str));
    }

    public byte[] getBytes(String str) throws SQLException {
        return getBytes(findColumn(str));
    }

    public void updateByte(String str, byte b) throws SQLException {
        updateByte(findColumn(str), b);
    }

    public void updateDouble(String str, double d) throws SQLException {
        updateDouble(findColumn(str), d);
    }

    public void updateFloat(String str, float f) throws SQLException {
        updateFloat(findColumn(str), f);
    }

    public void updateInt(String str, int i) throws SQLException {
        updateInt(findColumn(str), i);
    }

    public void updateLong(String str, long j) throws SQLException {
        updateLong(findColumn(str), j);
    }

    public void updateShort(String str, short s) throws SQLException {
        updateShort(findColumn(str), s);
    }

    public void updateBoolean(String str, boolean z) throws SQLException {
        updateBoolean(findColumn(str), z);
    }

    public void updateBytes(String str, byte[] bArr) throws SQLException {
        updateBytes(findColumn(str), bArr);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return (BigDecimal) Support.convert(this, getColumn(i), 3, null);
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        BigDecimal bigDecimal = (BigDecimal) Support.convert(this, getColumn(i), 3, null);
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(i2, 4);
    }

    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        checkOpen();
        checkUpdateable();
        if (bigDecimal != null) {
            bigDecimal = Support.normalizeBigDecimal(bigDecimal, getConnection().getMaxPrecision());
        }
        setColValue(i, 3, bigDecimal, 0);
    }

    public URL getURL(int i) throws SQLException {
        String string = getString(i);
        try {
            return new URL(string);
        } catch (MalformedURLException unused) {
            throw new SQLException(Messages.get("error.resultset.badurl", (Object) string), "22000");
        }
    }

    public Array getArray(int i) throws SQLException {
        checkOpen();
        notImplemented("ResultSet.getArray()");
        return null;
    }

    public void updateArray(int i, Array array) throws SQLException {
        checkOpen();
        checkUpdateable();
        notImplemented("ResultSet.updateArray()");
    }

    public Blob getBlob(int i) throws SQLException {
        return (Blob) Support.convert(this, getColumn(i), 2004, null);
    }

    public void updateBlob(int i, Blob blob) throws SQLException {
        if (blob == null) {
            updateBinaryStream(i, (InputStream) null, 0);
        } else {
            updateBinaryStream(i, blob.getBinaryStream(), (int) blob.length());
        }
    }

    public Clob getClob(int i) throws SQLException {
        return (Clob) Support.convert(this, getColumn(i), 2005, null);
    }

    public void updateClob(int i, Clob clob) throws SQLException {
        if (clob == null) {
            updateCharacterStream(i, (Reader) null, 0);
        } else {
            updateCharacterStream(i, clob.getCharacterStream(), (int) clob.length());
        }
    }

    public Date getDate(int i) throws SQLException {
        return (Date) Support.convert(this, getColumn(i), 91, null);
    }

    public void updateDate(int i, Date date) throws SQLException {
        setColValue(i, 91, date, 0);
    }

    public Ref getRef(int i) throws SQLException {
        checkOpen();
        notImplemented("ResultSet.getRef()");
        return null;
    }

    public void updateRef(int i, Ref ref) throws SQLException {
        checkOpen();
        checkUpdateable();
        notImplemented("ResultSet.updateRef()");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        checkOpen();
        return new JtdsResultSetMetaData(this.columns, this.columnCount, (!(this instanceof CachedResultSet) || !this.statement.isClosed()) ? getConnection().getUseLOBs() : false);
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.statement.getWarnings();
    }

    public Statement getStatement() throws SQLException {
        checkOpen();
        return this.statement;
    }

    public Time getTime(int i) throws SQLException {
        return (Time) Support.convert(this, getColumn(i), 92, null);
    }

    public void updateTime(int i, Time time) throws SQLException {
        setColValue(i, 92, time, 0);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return (Timestamp) Support.convert(this, getColumn(i), 93, null);
    }

    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        setColValue(i, 93, timestamp, 0);
    }

    public InputStream getAsciiStream(String str) throws SQLException {
        return getAsciiStream(findColumn(str));
    }

    public InputStream getBinaryStream(String str) throws SQLException {
        return getBinaryStream(findColumn(str));
    }

    public InputStream getUnicodeStream(String str) throws SQLException {
        return getUnicodeStream(findColumn(str));
    }

    public void updateAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        updateAsciiStream(findColumn(str), inputStream, i);
    }

    public void updateBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        updateBinaryStream(findColumn(str), inputStream, i);
    }

    public Reader getCharacterStream(String str) throws SQLException {
        return getCharacterStream(findColumn(str));
    }

    public void updateCharacterStream(String str, Reader reader, int i) throws SQLException {
        updateCharacterStream(findColumn(str), reader, i);
    }

    public Object getObject(String str) throws SQLException {
        return getObject(findColumn(str));
    }

    public void updateObject(String str, Object obj) throws SQLException {
        updateObject(findColumn(str), obj);
    }

    public void updateObject(String str, Object obj, int i) throws SQLException {
        updateObject(findColumn(str), obj, i);
    }

    public Object getObject(int i, Map map) throws SQLException {
        notImplemented("ResultSet.getObject(int, Map)");
        return null;
    }

    public String getString(String str) throws SQLException {
        return getString(findColumn(str));
    }

    public void updateString(String str, String str2) throws SQLException {
        updateString(findColumn(str), str2);
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(findColumn(str));
    }

    public BigDecimal getBigDecimal(String str, int i) throws SQLException {
        return getBigDecimal(findColumn(str), i);
    }

    public void updateBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        updateObject(findColumn(str), (Object) bigDecimal);
    }

    public URL getURL(String str) throws SQLException {
        return getURL(findColumn(str));
    }

    public Array getArray(String str) throws SQLException {
        return getArray(findColumn(str));
    }

    public void updateArray(String str, Array array) throws SQLException {
        updateArray(findColumn(str), array);
    }

    public Blob getBlob(String str) throws SQLException {
        return getBlob(findColumn(str));
    }

    public void updateBlob(String str, Blob blob) throws SQLException {
        updateBlob(findColumn(str), blob);
    }

    public Clob getClob(String str) throws SQLException {
        return getClob(findColumn(str));
    }

    public void updateClob(String str, Clob clob) throws SQLException {
        updateClob(findColumn(str), clob);
    }

    public Date getDate(String str) throws SQLException {
        return getDate(findColumn(str));
    }

    public void updateDate(String str, Date date) throws SQLException {
        updateDate(findColumn(str), date);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        Date date = getDate(i);
        return (date == null || calendar == null) ? date : new Date(Support.timeToZone(date, calendar));
    }

    public Ref getRef(String str) throws SQLException {
        return getRef(findColumn(str));
    }

    public void updateRef(String str, Ref ref) throws SQLException {
        updateRef(findColumn(str), ref);
    }

    public Time getTime(String str) throws SQLException {
        return getTime(findColumn(str));
    }

    public void updateTime(String str, Time time) throws SQLException {
        updateTime(findColumn(str), time);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        checkOpen();
        Time time = getTime(i);
        return (time == null || calendar == null) ? time : new Time(Support.timeToZone(time, calendar));
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(findColumn(str));
    }

    public void updateTimestamp(String str, Timestamp timestamp) throws SQLException {
        updateTimestamp(findColumn(str), timestamp);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        checkOpen();
        Timestamp timestamp = getTimestamp(i);
        return (timestamp == null || calendar == null) ? timestamp : new Timestamp(Support.timeToZone(timestamp, calendar));
    }

    public Object getObject(String str, Map map) throws SQLException {
        return getObject(findColumn(str), map);
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        return getDate(findColumn(str), calendar);
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        return getTime(findColumn(str), calendar);
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        return getTimestamp(findColumn(str), calendar);
    }

    public int getHoldability() throws SQLException {
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

    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateAsciiStream(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateAsciiStream(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateAsciiStream(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBinaryStream(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBinaryStream(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBinaryStream(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBlob(String str, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBlob(int i, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateBlob(String str, InputStream inputStream, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateCharacterStream(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateCharacterStream(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateCharacterStream(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateClob(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateClob(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateClob(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateClob(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNCharacterStream(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNCharacterStream(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNCharacterStream(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(int i, NClob nClob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(String str, NClob nClob) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(int i, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(String str, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(int i, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNClob(String str, Reader reader, long j) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNString(int i, String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateNString(String str, String str2) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateRowId(int i, RowId rowId) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateRowId(String str, RowId rowId) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        throw new AbstractMethodError();
    }

    public void updateSQLXML(String str, SQLXML sqlxml) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }
}
