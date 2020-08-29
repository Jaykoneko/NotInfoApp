package net.sourceforge.jtds.jdbc;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class JtdsPreparedStatement extends JtdsStatement implements PreparedStatement {

    /* renamed from: f */
    private static final NumberFormat f114f = NumberFormat.getInstance();
    Collection handles;
    private final String originalSql;
    protected ParamInfo[] paramMetaData;
    protected ParamInfo[] parameters;
    protected String procName;
    private boolean returnKeys;
    protected final String sql;
    protected String sqlWord;

    JtdsPreparedStatement(JtdsConnection jtdsConnection, String str, int i, int i2, boolean z) throws SQLException {
        super(jtdsConnection, i, i2);
        this.originalSql = str;
        boolean z2 = this instanceof JtdsCallableStatement;
        if (z2) {
            str = normalizeCall(str);
        }
        ArrayList arrayList = new ArrayList();
        String[] parse = SQLParser.parse(str, arrayList, jtdsConnection, false);
        if (parse[0].length() != 0) {
            if (parse[1].length() > 1 && z2) {
                this.procName = parse[1];
            }
            this.sqlWord = parse[2];
            if (z) {
                String str2 = "_JTDS_GENE_R_ATED_KEYS_";
                if (jtdsConnection.getServerType() != 1 || jtdsConnection.getDatabaseMajorVersion() < 8) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(parse[0]);
                    sb.append(" SELECT @@IDENTITY AS ");
                    sb.append(str2);
                    this.sql = sb.toString();
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(parse[0]);
                    sb2.append(" SELECT SCOPE_IDENTITY() AS ");
                    sb2.append(str2);
                    this.sql = sb2.toString();
                }
                this.returnKeys = true;
            } else {
                this.sql = parse[0];
                this.returnKeys = false;
            }
            this.parameters = (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]);
            return;
        }
        throw new SQLException(Messages.get("error.prepare.nosql"), "07000");
    }

    public String toString() {
        return this.originalSql;
    }

    protected static String normalizeCall(String str) throws SQLException {
        try {
            return normalize(str, 0);
        } catch (SQLException e) {
            if (e.getSQLState() == null) {
                return str;
            }
            throw e;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c5, code lost:
        throw new java.sql.SQLException();
     */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x00fe A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String normalize(java.lang.String r13, int r14) throws java.sql.SQLException {
        /*
            r0 = 1
            if (r14 > r0) goto L_0x01a3
            int r1 = r13.length()
            r2 = 0
            r3 = -1
            r4 = 0
            r5 = -1
            r6 = -1
            r7 = -1
        L_0x000d:
            java.lang.String r8 = "call "
            if (r4 >= r1) goto L_0x0134
            if (r5 >= 0) goto L_0x0134
        L_0x0013:
            char r9 = r13.charAt(r4)
            boolean r9 = java.lang.Character.isWhitespace(r9)
            if (r9 == 0) goto L_0x0020
            int r4 = r4 + 1
            goto L_0x0013
        L_0x0020:
            char r9 = r13.charAt(r4)
            r10 = 45
            if (r9 == r10) goto L_0x0110
            r10 = 47
            if (r9 == r10) goto L_0x00c6
            r10 = 61
            if (r9 == r10) goto L_0x00b9
            r10 = 63
            if (r9 == r10) goto L_0x00ae
            r5 = 123(0x7b, float:1.72E-43)
            if (r9 == r5) goto L_0x00ad
            int r5 = r1 - r4
            r9 = 4
            if (r5 <= r9) goto L_0x004b
            int r10 = r4 + 5
            java.lang.String r10 = r13.substring(r4, r10)
            java.lang.String r11 = "exec "
            boolean r10 = r10.equalsIgnoreCase(r11)
            if (r10 != 0) goto L_0x0057
        L_0x004b:
            int r10 = r4 + 5
            java.lang.String r10 = r13.substring(r4, r10)
            boolean r8 = r10.equalsIgnoreCase(r8)
            if (r8 == 0) goto L_0x0078
        L_0x0057:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r13.substring(r2, r4)
            r0.append(r1)
            int r4 = r4 + r9
            int r1 = r13.length()
            java.lang.String r13 = r13.substring(r4, r1)
            r0.append(r13)
            java.lang.String r13 = r0.toString()
            java.lang.String r13 = normalize(r13, r14)
            return r13
        L_0x0078:
            r8 = 7
            if (r5 <= r8) goto L_0x00aa
            int r5 = r4 + 8
            java.lang.String r5 = r13.substring(r4, r5)
            java.lang.String r9 = "execute "
            boolean r5 = r5.equalsIgnoreCase(r9)
            if (r5 == 0) goto L_0x00aa
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r13.substring(r2, r4)
            r0.append(r1)
            int r4 = r4 + r8
            int r1 = r13.length()
            java.lang.String r13 = r13.substring(r4, r1)
            r0.append(r13)
            java.lang.String r13 = r0.toString()
            java.lang.String r13 = normalize(r13, r14)
            return r13
        L_0x00aa:
            r5 = r4
            goto L_0x0131
        L_0x00ad:
            return r13
        L_0x00ae:
            if (r6 != r3) goto L_0x00b3
            r6 = r4
            goto L_0x0131
        L_0x00b3:
            java.sql.SQLException r13 = new java.sql.SQLException
            r13.<init>()
            throw r13
        L_0x00b9:
            if (r7 != r3) goto L_0x00c0
            if (r6 < 0) goto L_0x00c0
            r7 = r4
            goto L_0x0131
        L_0x00c0:
            java.sql.SQLException r13 = new java.sql.SQLException
            r13.<init>()
            throw r13
        L_0x00c6:
            int r8 = r4 + 1
            if (r8 >= r1) goto L_0x0131
            char r9 = r13.charAt(r8)
            r11 = 42
            if (r9 != r11) goto L_0x0131
            r4 = 1
        L_0x00d3:
            int r9 = r1 + -1
            if (r8 >= r9) goto L_0x0100
            int r8 = r8 + 1
            char r9 = r13.charAt(r8)
            if (r9 != r10) goto L_0x00eb
            int r9 = r8 + 1
            char r12 = r13.charAt(r9)
            if (r12 != r11) goto L_0x00eb
            int r4 = r4 + 1
        L_0x00e9:
            r8 = r9
            goto L_0x00fc
        L_0x00eb:
            char r9 = r13.charAt(r8)
            if (r9 != r11) goto L_0x00fc
            int r9 = r8 + 1
            char r12 = r13.charAt(r9)
            if (r12 != r10) goto L_0x00fc
            int r4 = r4 + -1
            goto L_0x00e9
        L_0x00fc:
            if (r4 > 0) goto L_0x00d3
            r4 = r8
            goto L_0x0131
        L_0x0100:
            java.sql.SQLException r13 = new java.sql.SQLException
            java.lang.String r14 = "error.parsesql.missing"
            java.lang.String r0 = "*/"
            java.lang.String r14 = net.sourceforge.jtds.jdbc.Messages.get(r14, r0)
            java.lang.String r0 = "22025"
            r13.<init>(r14, r0)
            throw r13
        L_0x0110:
            int r8 = r4 + 1
            if (r8 >= r1) goto L_0x0131
            char r8 = r13.charAt(r8)
            if (r8 != r10) goto L_0x0131
            int r4 = r4 + 2
        L_0x011c:
            if (r4 >= r1) goto L_0x0131
            char r8 = r13.charAt(r4)
            r9 = 10
            if (r8 == r9) goto L_0x0131
            char r8 = r13.charAt(r4)
            r9 = 13
            if (r8 == r9) goto L_0x0131
            int r4 = r4 + 1
            goto L_0x011c
        L_0x0131:
            int r4 = r4 + r0
            goto L_0x000d
        L_0x0134:
            if (r7 != r3) goto L_0x013f
            if (r6 != r3) goto L_0x0139
            goto L_0x013f
        L_0x0139:
            java.sql.SQLException r13 = new java.sql.SQLException
            r13.<init>()
            throw r13
        L_0x013f:
            int r14 = r5 + 7
            if (r14 >= r1) goto L_0x0170
            java.lang.String r14 = r13.substring(r5, r14)
            if (r14 == 0) goto L_0x0170
            java.lang.String r0 = "insert "
            boolean r0 = r14.equalsIgnoreCase(r0)
            if (r0 != 0) goto L_0x0162
            java.lang.String r0 = "update "
            boolean r0 = r14.equalsIgnoreCase(r0)
            if (r0 != 0) goto L_0x0162
            java.lang.String r0 = "delete "
            boolean r14 = r14.equalsIgnoreCase(r0)
            if (r14 != 0) goto L_0x0162
            goto L_0x0170
        L_0x0162:
            java.sql.SQLException r13 = new java.sql.SQLException
            java.lang.String r14 = "error.parsesql.noprocedurecall"
            java.lang.String r14 = net.sourceforge.jtds.jdbc.Messages.get(r14)
            java.lang.String r0 = "07000"
            r13.<init>(r14, r0)
            throw r13
        L_0x0170:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r0 = "{"
            r14.append(r0)
            java.lang.String r0 = r13.substring(r2, r5)
            r14.append(r0)
            r14.append(r8)
            java.lang.String r0 = r13.substring(r5)
            r14.append(r0)
            boolean r13 = openComment(r13, r5)
            if (r13 == 0) goto L_0x0194
            java.lang.String r13 = "\n"
            goto L_0x0196
        L_0x0194:
            java.lang.String r13 = ""
        L_0x0196:
            r14.append(r13)
            java.lang.String r13 = "}"
            r14.append(r13)
            java.lang.String r13 = r14.toString()
            return r13
        L_0x01a3:
            java.sql.SQLException r13 = new java.sql.SQLException
            r13.<init>()
            goto L_0x01aa
        L_0x01a9:
            throw r13
        L_0x01aa:
            goto L_0x01a9
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.normalize(java.lang.String, int):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x004d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean openComment(java.lang.String r7, int r8) throws java.sql.SQLException {
        /*
            int r0 = r7.length()
        L_0x0004:
            if (r8 >= r0) goto L_0x0086
            char r1 = r7.charAt(r8)
            r2 = 45
            r3 = 1
            if (r1 == r2) goto L_0x005f
            r2 = 47
            if (r1 == r2) goto L_0x0015
            goto L_0x0083
        L_0x0015:
            int r1 = r8 + 1
            if (r1 >= r0) goto L_0x0083
            char r4 = r7.charAt(r1)
            r5 = 42
            if (r4 != r5) goto L_0x0083
            r8 = 1
        L_0x0022:
            int r4 = r0 + -1
            if (r1 >= r4) goto L_0x004f
            int r1 = r1 + 1
            char r4 = r7.charAt(r1)
            if (r4 != r2) goto L_0x003a
            int r4 = r1 + 1
            char r6 = r7.charAt(r4)
            if (r6 != r5) goto L_0x003a
            int r8 = r8 + 1
        L_0x0038:
            r1 = r4
            goto L_0x004b
        L_0x003a:
            char r4 = r7.charAt(r1)
            if (r4 != r5) goto L_0x004b
            int r4 = r1 + 1
            char r6 = r7.charAt(r4)
            if (r6 != r2) goto L_0x004b
            int r8 = r8 + -1
            goto L_0x0038
        L_0x004b:
            if (r8 > 0) goto L_0x0022
            r8 = r1
            goto L_0x0083
        L_0x004f:
            java.sql.SQLException r7 = new java.sql.SQLException
            java.lang.String r8 = "error.parsesql.missing"
            java.lang.String r0 = "*/"
            java.lang.String r8 = net.sourceforge.jtds.jdbc.Messages.get(r8, r0)
            java.lang.String r0 = "22025"
            r7.<init>(r8, r0)
            throw r7
        L_0x005f:
            int r1 = r8 + 1
            if (r1 >= r0) goto L_0x0083
            char r1 = r7.charAt(r1)
            if (r1 != r2) goto L_0x0083
            int r8 = r8 + 2
        L_0x006b:
            if (r8 >= r0) goto L_0x0080
            char r1 = r7.charAt(r8)
            r2 = 10
            if (r1 == r2) goto L_0x0080
            char r1 = r7.charAt(r8)
            r2 = 13
            if (r1 == r2) goto L_0x0080
            int r8 = r8 + 1
            goto L_0x006b
        L_0x0080:
            if (r8 != r0) goto L_0x0083
            return r3
        L_0x0083:
            int r8 = r8 + r3
            goto L_0x0004
        L_0x0086:
            r7 = 0
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.openComment(java.lang.String, int):boolean");
    }

    /* access modifiers changed from: protected */
    public void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "PreparedStatement"), "HY010");
        }
    }

    /* access modifiers changed from: protected */
    public void notSupported(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notsup", (Object) str), "HYC00");
    }

    /* access modifiers changed from: protected */
    public SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        String[] strArr;
        int i3 = i;
        if (this.parameters.length == 0) {
            return super.executeMSBatch(i, i2, arrayList);
        }
        SQLException sQLException = null;
        if (this.connection.getPrepareSql() == 1 || this.connection.getPrepareSql() == 3) {
            strArr = new String[i3];
            for (int i4 = 0; i4 < i3; i4++) {
                strArr[i4] = this.connection.prepareSQL(this, this.sql, (ParamInfo[]) this.batchValues.get(i4), false, false);
            }
        } else {
            strArr = null;
        }
        int i5 = 0;
        while (i5 < i3) {
            Object obj = this.batchValues.get(i5);
            String str = strArr == null ? this.procName : strArr[i5];
            i5++;
            boolean z = i5 % i2 == 0 || i5 == i3;
            this.tds.startBatch();
            this.tds.executeSQL(this.sql, str, (ParamInfo[]) obj, false, 0, -1, -1, z);
            if (z) {
                sQLException = this.tds.getBatchCounts(arrayList, sQLException);
                if (!(sQLException == null || arrayList.size() == i5)) {
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
        int i3;
        int i4 = i;
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(i, i2, arrayList);
        }
        int i5 = (this.connection.getDatabaseMajorVersion() < 12 || (this.connection.getDatabaseMajorVersion() == 12 && this.connection.getDatabaseMinorVersion() < 50)) ? Callback.DEFAULT_DRAG_ANIMATION_DURATION : 1000;
        StringBuilder sb = new StringBuilder(i4 * 32);
        SQLException sQLException = null;
        ParamInfo[] paramInfoArr = this.parameters;
        if (paramInfoArr.length * i2 > i5) {
            i3 = i5 / paramInfoArr.length;
            if (i3 == 0) {
                i3 = 1;
            }
        } else {
            i3 = i2;
        }
        ArrayList arrayList2 = new ArrayList();
        int i6 = 0;
        while (i6 < i4) {
            Object obj = this.batchValues.get(i6);
            i6++;
            boolean z = i6 % i3 == 0 || i6 == i4;
            int length = sb.length();
            sb.append(this.sql);
            sb.append(' ');
            for (int i7 = 0; i7 < this.parameters.length; i7++) {
                ParamInfo paramInfo = ((ParamInfo[]) obj)[i7];
                paramInfo.markerPos += length;
                arrayList2.add(paramInfo);
            }
            if (z) {
                this.tds.executeSQL(sb.toString(), null, (ParamInfo[]) arrayList2.toArray(new ParamInfo[arrayList2.size()]), false, 0, -1, -1, true);
                sb.setLength(0);
                arrayList2.clear();
                sQLException = this.tds.getBatchCounts(arrayList, sQLException);
                if (!(sQLException == null || arrayList.size() == i6)) {
                    break;
                }
            } else {
                ArrayList arrayList3 = arrayList;
            }
        }
        return sQLException;
    }

    /* access modifiers changed from: protected */
    public ParamInfo getParameter(int i) throws SQLException {
        checkOpen();
        if (i >= 1) {
            ParamInfo[] paramInfoArr = this.parameters;
            if (i <= paramInfoArr.length) {
                return paramInfoArr[i - 1];
            }
        }
        throw new SQLException(Messages.get("error.prepare.paramindex", (Object) Integer.toString(i)), "07009");
    }

    public void setObjectBase(int i, Object obj, int i2, int i3) throws SQLException {
        int i4;
        Object obj2;
        int length;
        Object characterStream;
        checkOpen();
        int i5 = i2 == 2005 ? -1 : i2 == 2004 ? -4 : i2;
        if (obj != null) {
            obj = Support.convert(this, obj, i5, this.connection.getCharset());
            if (i3 >= 0) {
                if (obj instanceof BigDecimal) {
                    obj = ((BigDecimal) obj).setScale(i3, 4);
                } else if (obj instanceof Number) {
                    synchronized (f114f) {
                        f114f.setGroupingUsed(false);
                        f114f.setMaximumFractionDigits(i3);
                        obj = Support.convert(this, f114f.format(obj), i5, this.connection.getCharset());
                    }
                }
            }
            if (obj instanceof Blob) {
                Blob blob = (Blob) obj;
                length = (int) blob.length();
                characterStream = blob.getBinaryStream();
            } else if (obj instanceof Clob) {
                Clob clob = (Clob) obj;
                length = (int) clob.length();
                characterStream = clob.getCharacterStream();
            }
            obj2 = characterStream;
            i4 = length;
            setParameter(i, obj2, i5, i3, i4);
        }
        obj2 = obj;
        i4 = 0;
        setParameter(i, obj2, i5, i3, i4);
    }

    /* access modifiers changed from: protected */
    public void setParameter(int i, Object obj, int i2, int i3, int i4) throws SQLException {
        DateTime dateTime;
        ParamInfo parameter = getParameter(i);
        if (!"ERROR".equals(Support.getJdbcTypeName(i2))) {
            if (i2 == 3 || i2 == 2) {
                parameter.precision = this.connection.getMaxPrecision();
                if (obj instanceof BigDecimal) {
                    obj = Support.normalizeBigDecimal((BigDecimal) obj, parameter.precision);
                    parameter.scale = ((BigDecimal) obj).scale();
                } else {
                    if (i3 < 0) {
                        i3 = 10;
                    }
                    parameter.scale = i3;
                }
            } else {
                if (i3 < 0) {
                    i3 = 0;
                }
                parameter.scale = i3;
            }
            if (obj instanceof String) {
                parameter.length = ((String) obj).length();
            } else if (obj instanceof byte[]) {
                parameter.length = ((byte[]) obj).length;
            } else {
                parameter.length = i4;
            }
            if (obj instanceof Date) {
                dateTime = new DateTime((Date) obj);
            } else if (obj instanceof Time) {
                dateTime = new DateTime((Time) obj);
            } else {
                if (obj instanceof Timestamp) {
                    dateTime = new DateTime((Timestamp) obj);
                }
                parameter.value = obj;
                parameter.jdbcType = i2;
                parameter.isSet = true;
                parameter.isUnicode = this.connection.getUseUnicode();
                return;
            }
            obj = dateTime;
            parameter.value = obj;
            parameter.jdbcType = i2;
            parameter.isSet = true;
            parameter.isUnicode = this.connection.getUseUnicode();
            return;
        }
        throw new SQLException(Messages.get("error.generic.badtype", (Object) Integer.toString(i2)), "HY092");
    }

    /* access modifiers changed from: 0000 */
    public void setColMetaData(ColInfo[] colInfoArr) {
        this.colMetaData = colInfoArr;
    }

    /* access modifiers changed from: 0000 */
    public void setParamMetaData(ParamInfo[] paramInfoArr) {
        int i = 0;
        while (i < paramInfoArr.length) {
            ParamInfo[] paramInfoArr2 = this.parameters;
            if (i < paramInfoArr2.length) {
                if (!paramInfoArr2[i].isSet) {
                    this.parameters[i].jdbcType = paramInfoArr[i].jdbcType;
                    this.parameters[i].isOutput = paramInfoArr[i].isOutput;
                    this.parameters[i].precision = paramInfoArr[i].precision;
                    this.parameters[i].scale = paramInfoArr[i].scale;
                    this.parameters[i].sqlType = paramInfoArr[i].sqlType;
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void close() throws SQLException {
        try {
            super.close();
        } finally {
            this.handles = null;
            this.parameters = null;
        }
    }

    public int executeUpdate() throws SQLException {
        checkOpen();
        reset();
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            executeSQL(this.sql, this.procName, this.parameters, true, false);
        } else {
            synchronized (this.connection) {
                executeSQL(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, false), this.parameters, true, false);
            }
        }
        int updateCount = getUpdateCount();
        if (updateCount == -1) {
            return 0;
        }
        return updateCount;
    }

    public void addBatch() throws SQLException {
        checkOpen();
        if (this.batchValues == null) {
            this.batchValues = new ArrayList();
        }
        if (this.parameters.length == 0) {
            this.batchValues.add(this.sql);
            return;
        }
        this.batchValues.add(this.parameters);
        ParamInfo[] paramInfoArr = new ParamInfo[this.parameters.length];
        int i = 0;
        while (true) {
            ParamInfo[] paramInfoArr2 = this.parameters;
            if (i < paramInfoArr2.length) {
                paramInfoArr[i] = (ParamInfo) paramInfoArr2[i].clone();
                i++;
            } else {
                this.parameters = paramInfoArr;
                return;
            }
        }
    }

    public void clearParameters() throws SQLException {
        checkOpen();
        int i = 0;
        while (true) {
            ParamInfo[] paramInfoArr = this.parameters;
            if (i < paramInfoArr.length) {
                paramInfoArr[i].clearInValue();
                i++;
            } else {
                return;
            }
        }
    }

    public boolean execute() throws SQLException {
        boolean executeSQL;
        checkOpen();
        reset();
        boolean useCursor = useCursor(this.returnKeys, this.sqlWord);
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            return executeSQL(this.sql, this.procName, this.parameters, false, useCursor);
        }
        synchronized (this.connection) {
            executeSQL = executeSQL(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, useCursor), this.parameters, false, useCursor);
        }
        return executeSQL;
    }

    public void setByte(int i, byte b) throws SQLException {
        setParameter(i, new Integer(b & 255), -6, 0, 0);
    }

    public void setDouble(int i, double d) throws SQLException {
        setParameter(i, new Double(d), 8, 0, 0);
    }

    public void setFloat(int i, float f) throws SQLException {
        setParameter(i, new Float(f), 7, 0, 0);
    }

    public void setInt(int i, int i2) throws SQLException {
        setParameter(i, new Integer(i2), 4, 0, 0);
    }

    public void setNull(int i, int i2) throws SQLException {
        int i3 = i2 == 2005 ? -1 : i2 == 2004 ? -4 : i2;
        setParameter(i, null, i3, -1, 0);
    }

    public void setLong(int i, long j) throws SQLException {
        setParameter(i, new Long(j), -5, 0, 0);
    }

    public void setShort(int i, short s) throws SQLException {
        setParameter(i, new Integer(s), 5, 0, 0);
    }

    public void setBoolean(int i, boolean z) throws SQLException {
        setParameter(i, z ? Boolean.TRUE : Boolean.FALSE, 16, 0, 0);
    }

    public void setBytes(int i, byte[] bArr) throws SQLException {
        setParameter(i, bArr, -2, 0, 0);
    }

    public void setAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        if (inputStream == null || i2 < 0) {
            setParameter(i, null, -1, 0, 0);
            return;
        }
        try {
            setCharacterStream(i, (Reader) new InputStreamReader(inputStream, "US-ASCII"), i2);
        } catch (UnsupportedEncodingException unused) {
        }
    }

    public void setBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        checkOpen();
        if (inputStream == null || i2 < 0) {
            setBytes(i, null);
        } else {
            setParameter(i, inputStream, -4, 0, i2);
        }
    }

    public void setUnicodeStream(int i, InputStream inputStream, int i2) throws SQLException {
        checkOpen();
        if (inputStream == null || i2 < 0) {
            setString(i, null);
            return;
        }
        try {
            int i3 = i2 / 2;
            char[] cArr = new char[i3];
            int read = inputStream.read();
            int read2 = inputStream.read();
            int i4 = 0;
            while (read >= 0 && read2 >= 0 && i4 < i3) {
                int i5 = i4 + 1;
                cArr[i4] = (char) (((read << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (read2 & 255));
                read = inputStream.read();
                read2 = inputStream.read();
                i4 = i5;
            }
            setString(i, new String(cArr, 0, i4));
        } catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
        }
    }

    public void setCharacterStream(int i, Reader reader, int i2) throws SQLException {
        if (reader == null || i2 < 0) {
            setParameter(i, null, -1, 0, 0);
        } else {
            setParameter(i, reader, -1, 0, i2);
        }
    }

    public void setObject(int i, Object obj) throws SQLException {
        setObjectBase(i, obj, Support.getJdbcType(obj), -1);
    }

    public void setObject(int i, Object obj, int i2) throws SQLException {
        setObjectBase(i, obj, i2, -1);
    }

    public void setObject(int i, Object obj, int i2, int i3) throws SQLException {
        checkOpen();
        if (i3 < 0 || i3 > this.connection.getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
        }
        setObjectBase(i, obj, i2, i3);
    }

    public void setNull(int i, int i2, String str) throws SQLException {
        notImplemented("PreparedStatement.setNull(int, int, String)");
    }

    public void setString(int i, String str) throws SQLException {
        setParameter(i, str, 12, 0, 0);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        setParameter(i, bigDecimal, 3, -1, 0);
    }

    public void setURL(int i, URL url) throws SQLException {
        setString(i, url == null ? null : url.toString());
    }

    public void setArray(int i, Array array) throws SQLException {
        notImplemented("PreparedStatement.setArray");
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        if (blob == null) {
            setBytes(i, null);
        } else if (blob.length() <= 2147483647L) {
            setBinaryStream(i, blob.getBinaryStream(), (int) blob.length());
        } else {
            throw new SQLException(Messages.get("error.resultset.longblob"), "24000");
        }
    }

    public void setClob(int i, Clob clob) throws SQLException {
        if (clob == null) {
            setString(i, null);
        } else if (clob.length() <= 2147483647L) {
            setCharacterStream(i, clob.getCharacterStream(), (int) clob.length());
        } else {
            throw new SQLException(Messages.get("error.resultset.longclob"), "24000");
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        setParameter(i, date, 91, 0, 0);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        checkOpen();
        if (this.connection.getServerType() == 2) {
            this.connection.prepareSQL(this, this.sql, new ParamInfo[0], false, false);
        }
        try {
            return (ParameterMetaData) Class.forName("net.sourceforge.jtds.jdbc.ParameterMetaDataImpl").getConstructor(new Class[]{ParamInfo[].class, JtdsConnection.class}).newInstance(new Object[]{this.parameters, this.connection});
        } catch (Exception unused) {
            notImplemented("PreparedStatement.getParameterMetaData");
            return null;
        }
    }

    public void setRef(int i, Ref ref) throws SQLException {
        notImplemented("PreparedStatement.setRef");
    }

    public ResultSet executeQuery() throws SQLException {
        ResultSet executeSQLQuery;
        checkOpen();
        reset();
        boolean useCursor = useCursor(false, null);
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            return executeSQLQuery(this.sql, this.procName, this.parameters, useCursor);
        }
        synchronized (this.connection) {
            executeSQLQuery = executeSQLQuery(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, false, useCursor), this.parameters, useCursor);
        }
        return executeSQLQuery;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003c, code lost:
        if ("with".equals(r9.sqlWord) != false) goto L_0x003e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException {
        /*
            r9 = this;
            r9.checkOpen()
            net.sourceforge.jtds.jdbc.ColInfo[] r0 = r9.colMetaData
            if (r0 != 0) goto L_0x0096
            net.sourceforge.jtds.jdbc.JtdsResultSet r0 = r9.currentResult
            if (r0 == 0) goto L_0x0013
            net.sourceforge.jtds.jdbc.JtdsResultSet r0 = r9.currentResult
            net.sourceforge.jtds.jdbc.ColInfo[] r0 = r0.columns
            r9.colMetaData = r0
            goto L_0x0096
        L_0x0013:
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r9.connection
            int r0 = r0.getServerType()
            r1 = 2
            r2 = 0
            if (r0 != r1) goto L_0x002a
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r9.connection
            java.lang.String r5 = r9.sql
            net.sourceforge.jtds.jdbc.ParamInfo[] r6 = new net.sourceforge.jtds.jdbc.ParamInfo[r2]
            r7 = 0
            r8 = 0
            r4 = r9
            r3.prepareSQL(r4, r5, r6, r7, r8)
            goto L_0x0096
        L_0x002a:
            java.lang.String r0 = r9.sqlWord
            java.lang.String r1 = "select"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x003e
            java.lang.String r0 = r9.sqlWord
            java.lang.String r1 = "with"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0096
        L_0x003e:
            net.sourceforge.jtds.jdbc.ParamInfo[] r0 = r9.parameters
            int r0 = r0.length
            net.sourceforge.jtds.jdbc.ParamInfo[] r1 = new net.sourceforge.jtds.jdbc.ParamInfo[r0]
            r3 = 0
        L_0x0044:
            if (r3 >= r0) goto L_0x005b
            net.sourceforge.jtds.jdbc.ParamInfo r4 = new net.sourceforge.jtds.jdbc.ParamInfo
            net.sourceforge.jtds.jdbc.ParamInfo[] r5 = r9.parameters
            r5 = r5[r3]
            int r5 = r5.markerPos
            r4.<init>(r5, r2)
            r1[r3] = r4
            r4 = r1[r3]
            r5 = 1
            r4.isSet = r5
            int r3 = r3 + 1
            goto L_0x0044
        L_0x005b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r2 = r9.sql
            int r2 = r2.length()
            int r2 = r2 + 128
            r0.<init>(r2)
            java.lang.String r2 = "SET FMTONLY ON; "
            r0.append(r2)
            java.lang.String r2 = r9.sql
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r9.connection
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r2, r1, r3)
            r0.append(r1)
            java.lang.String r1 = "\r\n; SET FMTONLY OFF"
            r0.append(r1)
            net.sourceforge.jtds.jdbc.TdsCore r1 = r9.tds     // Catch:{ SQLException -> 0x008f }
            java.lang.String r0 = r0.toString()     // Catch:{ SQLException -> 0x008f }
            r1.submitSQL(r0)     // Catch:{ SQLException -> 0x008f }
            net.sourceforge.jtds.jdbc.TdsCore r0 = r9.tds     // Catch:{ SQLException -> 0x008f }
            net.sourceforge.jtds.jdbc.ColInfo[] r0 = r0.getColumns()     // Catch:{ SQLException -> 0x008f }
            r9.colMetaData = r0     // Catch:{ SQLException -> 0x008f }
            goto L_0x0096
        L_0x008f:
            net.sourceforge.jtds.jdbc.TdsCore r0 = r9.tds
            java.lang.String r1 = "SET FMTONLY OFF"
            r0.submitSQL(r1)
        L_0x0096:
            net.sourceforge.jtds.jdbc.ColInfo[] r0 = r9.colMetaData
            if (r0 != 0) goto L_0x009c
            r0 = 0
            goto L_0x00af
        L_0x009c:
            net.sourceforge.jtds.jdbc.JtdsResultSetMetaData r0 = new net.sourceforge.jtds.jdbc.JtdsResultSetMetaData
            net.sourceforge.jtds.jdbc.ColInfo[] r1 = r9.colMetaData
            net.sourceforge.jtds.jdbc.ColInfo[] r2 = r9.colMetaData
            int r2 = net.sourceforge.jtds.jdbc.JtdsResultSet.getColumnCount(r2)
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r9.connection
            boolean r3 = r3.getUseLOBs()
            r0.<init>(r1, r2, r3)
        L_0x00af:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.getMetaData():java.sql.ResultSetMetaData");
    }

    public void setTime(int i, Time time) throws SQLException {
        setParameter(i, time, 92, 0, 0);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        setParameter(i, timestamp, 93, 0, 0);
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        if (!(date == null || calendar == null)) {
            date = new Date(Support.timeFromZone(date, calendar));
        }
        setDate(i, date);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        if (!(time == null || calendar == null)) {
            time = new Time(Support.timeFromZone(time, calendar));
        }
        setTime(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        if (!(timestamp == null || calendar == null)) {
            timestamp = new Timestamp(Support.timeFromZone(timestamp, calendar));
        }
        setTimestamp(i, timestamp);
    }

    public int executeUpdate(String str) throws SQLException {
        notSupported("executeUpdate(String)");
        return 0;
    }

    public void addBatch(String str) throws SQLException {
        notSupported("executeBatch(String)");
    }

    public boolean execute(String str) throws SQLException {
        notSupported("execute(String)");
        return false;
    }

    public int executeUpdate(String str, int i) throws SQLException {
        notSupported("executeUpdate(String, int)");
        return 0;
    }

    public boolean execute(String str, int i) throws SQLException {
        notSupported("execute(String, int)");
        return false;
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        notSupported("executeUpdate(String, int[])");
        return 0;
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        notSupported("execute(String, int[])");
        return false;
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        notSupported("executeUpdate(String, String[])");
        return 0;
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        notSupported("execute(String, String[])");
        return false;
    }

    public ResultSet executeQuery(String str) throws SQLException {
        notSupported("executeQuery(String)");
        return null;
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
}
