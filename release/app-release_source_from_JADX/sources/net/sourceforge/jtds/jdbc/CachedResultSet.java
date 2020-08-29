package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashSet;

public class CachedResultSet extends JtdsResultSet {
    protected JtdsConnection connection;
    protected final TdsCore cursorTds;
    protected ParamInfo[] insertRow;
    protected boolean isKeyed;
    protected boolean isSybase;
    protected boolean onInsertRow;
    protected final String procName;
    protected final ParamInfo[] procedureParams;
    protected boolean rowDeleted;
    protected boolean rowUpdated;
    protected boolean sizeChanged;
    protected String sql;
    protected String tableName;
    protected final boolean tempResultSet;
    protected ParamInfo[] updateRow;
    protected final TdsCore updateTds;

    CachedResultSet(JtdsStatement jtdsStatement, String str, String str2, ParamInfo[] paramInfoArr, int i, int i2) throws SQLException {
        super(jtdsStatement, i, i2, null);
        this.connection = (JtdsConnection) jtdsStatement.getConnection();
        this.cursorTds = jtdsStatement.getTds();
        this.sql = str;
        this.procName = str2;
        this.procedureParams = paramInfoArr;
        if (i != 1003 || i2 == 1007 || this.cursorName == null) {
            this.updateTds = this.cursorTds;
        } else {
            this.updateTds = new TdsCore(this.connection, jtdsStatement.getMessages());
        }
        this.isSybase = 2 == this.connection.getServerType();
        this.tempResultSet = false;
        cursorCreate();
    }

    CachedResultSet(JtdsStatement jtdsStatement, String[] strArr, int[] iArr) throws SQLException {
        super(jtdsStatement, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_TEXT, null);
        this.columns = new ColInfo[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.name = strArr[i];
            colInfo.realName = strArr[i];
            colInfo.jdbcType = iArr[i];
            colInfo.isCaseSensitive = false;
            colInfo.isIdentity = false;
            colInfo.isWriteable = false;
            colInfo.nullable = 2;
            colInfo.scale = 0;
            TdsData.fillInType(colInfo);
            this.columns[i] = colInfo;
        }
        this.columnCount = getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    CachedResultSet(JtdsResultSet jtdsResultSet, boolean z) throws SQLException {
        super((JtdsStatement) jtdsResultSet.getStatement(), jtdsResultSet.getStatement().getResultSetType(), jtdsResultSet.getStatement().getResultSetConcurrency(), null);
        JtdsStatement jtdsStatement = (JtdsStatement) jtdsResultSet.getStatement();
        String str = "01000";
        String str2 = "warning.cursordowngraded";
        if (this.concurrency != 1007) {
            this.concurrency = PointerIconCompat.TYPE_CROSSHAIR;
            jtdsStatement.addWarning(new SQLWarning(Messages.get(str2, (Object) "CONCUR_READ_ONLY"), str));
        }
        if (this.resultSetType >= 1005) {
            this.resultSetType = PointerIconCompat.TYPE_WAIT;
            jtdsStatement.addWarning(new SQLWarning(Messages.get(str2, (Object) "TYPE_SCROLL_INSENSITIVE"), str));
        }
        this.columns = jtdsResultSet.getColumns();
        this.columnCount = getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
        if (z) {
            while (jtdsResultSet.next()) {
                this.rowData.add(copyRow(jtdsResultSet.getCurrentRow()));
            }
            this.rowsInResult = this.rowData.size();
        }
    }

    CachedResultSet(JtdsStatement jtdsStatement, ColInfo[] colInfoArr, Object[] objArr) throws SQLException {
        super(jtdsStatement, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, null);
        this.columns = colInfoArr;
        this.columnCount = getColumnCount(colInfoArr);
        this.rowData = new ArrayList(1);
        this.rowsInResult = 1;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.rowData.add(copyRow(objArr));
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    /* access modifiers changed from: 0000 */
    public void addRow(Object[] objArr) {
        this.rowsInResult++;
        this.rowData.add(copyRow(objArr));
    }

    /* access modifiers changed from: 0000 */
    public void setConcurrency(int i) {
        this.concurrency = i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x02b7  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x02cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void cursorCreate() throws java.sql.SQLException {
        /*
            r21 = this;
            r0 = r21
            int r1 = r0.concurrency
            int r2 = r0.resultSetType
            java.lang.String r3 = r0.cursorName
            r4 = 1003(0x3eb, float:1.406E-42)
            r5 = 1007(0x3ef, float:1.411E-42)
            if (r3 != 0) goto L_0x0026
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r0.connection
            boolean r3 = r3.getUseCursors()
            if (r3 == 0) goto L_0x0026
            int r3 = r0.resultSetType
            if (r3 != r4) goto L_0x0026
            int r3 = r0.concurrency
            if (r3 != r5) goto L_0x0026
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r0.connection
            java.lang.String r3 = r3.getCursorName()
            r0.cursorName = r3
        L_0x0026:
            int r3 = r0.resultSetType
            r6 = 1004(0x3ec, float:1.407E-42)
            r7 = 0
            r8 = 1
            if (r3 != r4) goto L_0x0036
            int r3 = r0.concurrency
            if (r3 != r5) goto L_0x0036
            java.lang.String r3 = r0.cursorName
            if (r3 == 0) goto L_0x0075
        L_0x0036:
            java.lang.String r3 = r0.sql
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            net.sourceforge.jtds.jdbc.JtdsStatement r10 = r0.statement
            java.sql.Connection r10 = r10.getConnection()
            net.sourceforge.jtds.jdbc.JtdsConnection r10 = (net.sourceforge.jtds.jdbc.JtdsConnection) r10
            java.lang.String[] r3 = net.sourceforge.jtds.jdbc.SQLParser.parse(r3, r9, r10, r8)
            r9 = 2
            r9 = r3[r9]
            java.lang.String r10 = "select"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x006a
            r9 = 3
            r10 = r3[r9]
            if (r10 == 0) goto L_0x0066
            r10 = r3[r9]
            int r10 = r10.length()
            if (r10 <= 0) goto L_0x0066
            r3 = r3[r9]
            r0.tableName = r3
            goto L_0x0068
        L_0x0066:
            r0.concurrency = r5
        L_0x0068:
            r3 = 1
            goto L_0x0076
        L_0x006a:
            r3 = 0
            r0.cursorName = r3
            r0.concurrency = r5
            int r3 = r0.resultSetType
            if (r3 == r4) goto L_0x0075
            r0.resultSetType = r6
        L_0x0075:
            r3 = 0
        L_0x0076:
            java.lang.String r9 = r0.cursorName
            java.lang.String r10 = "24000"
            java.lang.String r11 = "error.statement.noresult"
            if (r9 == 0) goto L_0x01b1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = r0.sql
            int r4 = r4.length()
            java.lang.String r5 = r0.cursorName
            int r5 = r5.length()
            int r4 = r4 + r5
            int r4 = r4 + 128
            r3.<init>(r4)
            java.lang.String r4 = "DECLARE "
            r3.append(r4)
            java.lang.String r4 = r0.cursorName
            r3.append(r4)
            java.lang.String r4 = " CURSOR FOR "
            r3.append(r4)
            net.sourceforge.jtds.jdbc.ParamInfo[] r4 = r0.procedureParams
            if (r4 == 0) goto L_0x00ca
            int r5 = r4.length
            if (r5 <= 0) goto L_0x00ca
            int r4 = r4.length
            net.sourceforge.jtds.jdbc.ParamInfo[] r5 = new net.sourceforge.jtds.jdbc.ParamInfo[r4]
            int r6 = r3.length()
            r9 = 0
        L_0x00b0:
            if (r9 >= r4) goto L_0x00c8
            net.sourceforge.jtds.jdbc.ParamInfo[] r12 = r0.procedureParams
            r12 = r12[r9]
            java.lang.Object r12 = r12.clone()
            net.sourceforge.jtds.jdbc.ParamInfo r12 = (net.sourceforge.jtds.jdbc.ParamInfo) r12
            r5[r9] = r12
            r12 = r5[r9]
            int r13 = r12.markerPos
            int r13 = r13 + r6
            r12.markerPos = r13
            int r9 = r9 + 1
            goto L_0x00b0
        L_0x00c8:
            r15 = r5
            goto L_0x00cb
        L_0x00ca:
            r15 = r4
        L_0x00cb:
            java.lang.String r4 = r0.sql
            r3.append(r4)
            net.sourceforge.jtds.jdbc.TdsCore r12 = r0.cursorTds
            java.lang.String r13 = r3.toString()
            r14 = 0
            r16 = 0
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r0.statement
            int r17 = r4.getQueryTimeout()
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r0.statement
            int r18 = r4.getMaxRows()
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r0.statement
            int r19 = r4.getMaxFieldSize()
            r20 = 1
            r12.executeSQL(r13, r14, r15, r16, r17, r18, r19, r20)
            net.sourceforge.jtds.jdbc.TdsCore r4 = r0.cursorTds
            r4.clearResponseQueue()
            net.sourceforge.jtds.jdbc.TdsCore r4 = r0.cursorTds
            net.sourceforge.jtds.jdbc.SQLDiagnostic r4 = r4.getMessages()
            r4.checkErrors()
            r3.setLength(r7)
            java.lang.String r4 = "\r\nOPEN "
            r3.append(r4)
            java.lang.String r4 = r0.cursorName
            r3.append(r4)
            int r4 = r0.fetchSize
            if (r4 <= r8) goto L_0x0127
            boolean r4 = r0.isSybase
            if (r4 == 0) goto L_0x0127
            java.lang.String r4 = "\r\nSET CURSOR ROWS "
            r3.append(r4)
            int r4 = r0.fetchSize
            r3.append(r4)
            java.lang.String r4 = " FOR "
            r3.append(r4)
            java.lang.String r4 = r0.cursorName
            r3.append(r4)
        L_0x0127:
            java.lang.String r4 = "\r\nFETCH "
            r3.append(r4)
            java.lang.String r4 = r0.cursorName
            r3.append(r4)
            net.sourceforge.jtds.jdbc.TdsCore r12 = r0.cursorTds
            java.lang.String r13 = r3.toString()
            r14 = 0
            r15 = 0
            r16 = 0
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r17 = r3.getQueryTimeout()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r18 = r3.getMaxRows()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r19 = r3.getMaxFieldSize()
            r20 = 1
            r12.executeSQL(r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x0152:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.getMoreResults()
            if (r3 != 0) goto L_0x0163
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isEndOfResponse()
            if (r3 != 0) goto L_0x0163
            goto L_0x0152
        L_0x0163:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isResultSet()
            if (r3 == 0) goto L_0x019c
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r3.getColumns()
            r0.columns = r3
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r0.connection
            int r3 = r3.getServerType()
            if (r3 != r8) goto L_0x018a
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r0.columns
            int r3 = r3.length
            if (r3 <= 0) goto L_0x018a
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r0.columns
            net.sourceforge.jtds.jdbc.ColInfo[] r4 = r0.columns
            int r4 = r4.length
            int r4 = r4 - r8
            r3 = r3[r4]
            r3.isHidden = r8
        L_0x018a:
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r0.columns
            int r3 = getColumnCount(r3)
            r0.columnCount = r3
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isDataInResultSet()
            r0.rowsInResult = r3
            goto L_0x02af
        L_0x019c:
            java.sql.SQLException r1 = new java.sql.SQLException
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r11)
            r1.<init>(r2, r10)
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            java.sql.SQLException r2 = r2.exceptions
            r1.setNextException(r2)
            throw r1
        L_0x01b1:
            r8 = 1000(0x3e8, float:1.401E-42)
            if (r3 == 0) goto L_0x0251
            int r3 = r0.concurrency
            if (r3 != r5) goto L_0x01bf
            int r3 = r0.resultSetType
            r9 = 1005(0x3ed, float:1.408E-42)
            if (r3 < r9) goto L_0x0251
        L_0x01bf:
            net.sourceforge.jtds.jdbc.TdsCore r12 = r0.cursorTds
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r9 = r0.sql
            r3.append(r9)
            java.lang.String r9 = " FOR BROWSE"
            r3.append(r9)
            java.lang.String r13 = r3.toString()
            r14 = 0
            net.sourceforge.jtds.jdbc.ParamInfo[] r15 = r0.procedureParams
            r16 = 0
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r17 = r3.getQueryTimeout()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r18 = r3.getMaxRows()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r19 = r3.getMaxFieldSize()
            r20 = 1
            r12.executeSQL(r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x01f0:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.getMoreResults()
            if (r3 != 0) goto L_0x0201
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isEndOfResponse()
            if (r3 != 0) goto L_0x0201
            goto L_0x01f0
        L_0x0201:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isResultSet()
            if (r3 == 0) goto L_0x023c
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r3.getColumns()
            r0.columns = r3
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r0.columns
            int r3 = getColumnCount(r3)
            r0.columnCount = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r8)
            r0.rowData = r3
            r21.cacheResultSetRows()
            java.util.ArrayList r3 = r0.rowData
            int r3 = r3.size()
            r0.rowsInResult = r3
            r0.pos = r7
            boolean r3 = r21.isCursorUpdateable()
            if (r3 != 0) goto L_0x02af
            r0.concurrency = r5
            int r3 = r0.resultSetType
            if (r3 == r4) goto L_0x02af
            r0.resultSetType = r6
            goto L_0x02af
        L_0x023c:
            java.sql.SQLException r1 = new java.sql.SQLException
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r11)
            r1.<init>(r2, r10)
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            java.sql.SQLException r2 = r2.exceptions
            r1.setNextException(r2)
            throw r1
        L_0x0251:
            net.sourceforge.jtds.jdbc.TdsCore r12 = r0.cursorTds
            java.lang.String r13 = r0.sql
            java.lang.String r14 = r0.procName
            net.sourceforge.jtds.jdbc.ParamInfo[] r15 = r0.procedureParams
            r16 = 0
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r17 = r3.getQueryTimeout()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r18 = r3.getMaxRows()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            int r19 = r3.getMaxFieldSize()
            r20 = 1
            r12.executeSQL(r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x0272:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.getMoreResults()
            if (r3 != 0) goto L_0x0283
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isEndOfResponse()
            if (r3 != 0) goto L_0x0283
            goto L_0x0272
        L_0x0283:
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            boolean r3 = r3.isResultSet()
            if (r3 == 0) goto L_0x02e5
            net.sourceforge.jtds.jdbc.TdsCore r3 = r0.cursorTds
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r3.getColumns()
            r0.columns = r3
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r0.columns
            int r3 = getColumnCount(r3)
            r0.columnCount = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r8)
            r0.rowData = r3
            r21.cacheResultSetRows()
            java.util.ArrayList r3 = r0.rowData
            int r3 = r3.size()
            r0.rowsInResult = r3
            r0.pos = r7
        L_0x02af:
            int r3 = r0.concurrency
            java.lang.String r4 = "01000"
            java.lang.String r5 = "warning.cursordowngraded"
            if (r3 >= r1) goto L_0x02c7
            net.sourceforge.jtds.jdbc.JtdsStatement r1 = r0.statement
            java.sql.SQLWarning r3 = new java.sql.SQLWarning
            java.lang.String r6 = "CONCUR_READ_ONLY"
            java.lang.String r6 = net.sourceforge.jtds.jdbc.Messages.get(r5, r6)
            r3.<init>(r6, r4)
            r1.addWarning(r3)
        L_0x02c7:
            int r1 = r0.resultSetType
            if (r1 >= r2) goto L_0x02db
            net.sourceforge.jtds.jdbc.JtdsStatement r1 = r0.statement
            java.sql.SQLWarning r2 = new java.sql.SQLWarning
            java.lang.String r3 = "TYPE_SCROLL_INSENSITIVE"
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r5, r3)
            r2.<init>(r3, r4)
            r1.addWarning(r2)
        L_0x02db:
            net.sourceforge.jtds.jdbc.JtdsStatement r1 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r1 = r1.getMessages()
            r1.checkErrors()
            return
        L_0x02e5:
            java.sql.SQLException r1 = new java.sql.SQLException
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r11)
            r1.<init>(r2, r10)
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            java.sql.SQLException r2 = r2.exceptions
            r1.setNextException(r2)
            goto L_0x02fb
        L_0x02fa:
            throw r1
        L_0x02fb:
            goto L_0x02fa
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.CachedResultSet.cursorCreate():void");
    }

    /* access modifiers changed from: 0000 */
    public boolean isCursorUpdateable() throws SQLException {
        String str;
        String str2;
        this.isKeyed = false;
        HashSet hashSet = new HashSet();
        int i = 0;
        while (true) {
            str = "image";
            str2 = "text";
            if (i >= this.columns.length) {
                break;
            }
            ColInfo colInfo = this.columns[i];
            if (colInfo.isKey) {
                if (str2.equals(colInfo.sqlType) || str.equals(colInfo.sqlType)) {
                    colInfo.isKey = false;
                } else {
                    this.isKeyed = true;
                }
            } else if (colInfo.isIdentity) {
                colInfo.isKey = true;
                this.isKeyed = true;
            }
            StringBuilder sb = new StringBuilder();
            if (colInfo.tableName != null && colInfo.tableName.length() > 0) {
                sb.setLength(0);
                if (colInfo.catalog != null) {
                    sb.append(colInfo.catalog);
                    sb.append('.');
                    if (colInfo.schema == null) {
                        sb.append('.');
                    }
                }
                if (colInfo.schema != null) {
                    sb.append(colInfo.schema);
                    sb.append('.');
                }
                sb.append(colInfo.tableName);
                String sb2 = sb.toString();
                this.tableName = sb2;
                hashSet.add(sb2);
            }
            i++;
        }
        if (this.tableName.startsWith("#") && this.cursorTds.getTdsVersion() >= 3) {
            StringBuilder sb3 = new StringBuilder(1024);
            sb3.append("SELECT ");
            for (int i2 = 1; i2 <= 8; i2++) {
                if (i2 > 1) {
                    sb3.append(',');
                }
                sb3.append("index_col('tempdb..");
                sb3.append(this.tableName);
                sb3.append("', indid, ");
                sb3.append(i2);
                sb3.append(')');
            }
            sb3.append(" FROM tempdb..sysindexes WHERE id = object_id('tempdb..");
            sb3.append(this.tableName);
            sb3.append("') AND indid > 0 AND ");
            sb3.append("(status & 2048) = 2048");
            this.cursorTds.executeSQL(sb3.toString(), null, null, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults()) {
                if (this.cursorTds.isEndOfResponse()) {
                    break;
                }
            }
            if (this.cursorTds.isResultSet() && this.cursorTds.getNextRow()) {
                Object[] rowData = this.cursorTds.getRowData();
                for (Object obj : rowData) {
                    String str3 = (String) obj;
                    if (str3 != null) {
                        int i3 = 0;
                        while (true) {
                            if (i3 < this.columns.length) {
                                if (this.columns[i3].realName != null && this.columns[i3].realName.equalsIgnoreCase(str3)) {
                                    this.columns[i3].isKey = true;
                                    this.isKeyed = true;
                                    break;
                                }
                                i3++;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            this.statement.getMessages().checkErrors();
        }
        if (!this.isKeyed) {
            for (int i4 = 0; i4 < this.columns.length; i4++) {
                String str4 = this.columns[i4].sqlType;
                if (!"ntext".equals(str4) && !str2.equals(str4) && !str.equals(str4) && !"timestamp".equals(str4) && this.columns[i4].tableName != null) {
                    this.columns[i4].isKey = true;
                    this.isKeyed = true;
                }
            }
        }
        if (hashSet.size() != 1 || !this.isKeyed) {
            return false;
        }
        return true;
    }

    private boolean cursorFetch(int i) throws SQLException {
        boolean z = false;
        this.rowUpdated = false;
        if (this.cursorName != null) {
            if (!this.cursorTds.getNextRow()) {
                StringBuilder sb = new StringBuilder(128);
                if (this.isSybase && this.sizeChanged) {
                    sb.append("SET CURSOR ROWS ");
                    sb.append(this.fetchSize);
                    sb.append(" FOR ");
                    sb.append(this.cursorName);
                    sb.append("\r\n");
                }
                sb.append("FETCH ");
                sb.append(this.cursorName);
                this.cursorTds.executeSQL(sb.toString(), null, null, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                while (!this.cursorTds.getMoreResults()) {
                    if (this.cursorTds.isEndOfResponse()) {
                        break;
                    }
                }
                this.sizeChanged = false;
                if (!this.cursorTds.isResultSet() || !this.cursorTds.getNextRow()) {
                    this.pos = -1;
                    this.currentRow = null;
                    this.statement.getMessages().checkErrors();
                    return false;
                }
            }
            this.currentRow = this.statement.getTds().getRowData();
            this.pos++;
            this.rowsInResult = this.pos;
            this.statement.getMessages().checkErrors();
            if (this.currentRow != null) {
                z = true;
            }
            return z;
        } else if (this.rowsInResult == 0) {
            this.pos = 0;
            this.currentRow = null;
            return false;
        } else if (i == this.pos) {
            return true;
        } else {
            if (i < 1) {
                this.currentRow = null;
                this.pos = 0;
                return false;
            } else if (i > this.rowsInResult) {
                this.currentRow = null;
                this.pos = -1;
                return false;
            } else {
                this.pos = i;
                this.currentRow = (Object[]) this.rowData.get(i - 1);
                if (this.currentRow == null) {
                    z = true;
                }
                this.rowDeleted = z;
                if (this.resultSetType >= 1005 && this.currentRow != null) {
                    refreshRow();
                }
                return true;
            }
        }
    }

    private void cursorClose() throws SQLException {
        String str;
        if (this.cursorName != null) {
            this.statement.clearWarnings();
            String str2 = "CLOSE ";
            if (this.isSybase) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(this.cursorName);
                sb.append("\r\nDEALLOCATE CURSOR ");
                sb.append(this.cursorName);
                str = sb.toString();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(this.cursorName);
                sb2.append("\r\nDEALLOCATE ");
                sb2.append(this.cursorName);
                str = sb2.toString();
            }
            this.cursorTds.submitSQL(str);
        }
        this.rowData = null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005f, code lost:
        if (r8 == false) goto L_0x0062;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static net.sourceforge.jtds.jdbc.ParamInfo buildParameter(int r5, net.sourceforge.jtds.jdbc.ColInfo r6, java.lang.Object r7, boolean r8) throws java.sql.SQLException {
        /*
            boolean r0 = r7 instanceof java.lang.String
            r1 = 0
            if (r0 == 0) goto L_0x000d
            r0 = r7
            java.lang.String r0 = (java.lang.String) r0
            int r0 = r0.length()
            goto L_0x003b
        L_0x000d:
            boolean r0 = r7 instanceof byte[]
            if (r0 == 0) goto L_0x0018
            r0 = r7
            byte[] r0 = (byte[]) r0
            byte[] r0 = (byte[]) r0
            int r0 = r0.length
            goto L_0x003b
        L_0x0018:
            boolean r0 = r7 instanceof net.sourceforge.jtds.jdbc.BlobImpl
            if (r0 == 0) goto L_0x002b
            net.sourceforge.jtds.jdbc.BlobImpl r7 = (net.sourceforge.jtds.jdbc.BlobImpl) r7
            java.io.InputStream r0 = r7.getBinaryStream()
            long r2 = r7.length()
        L_0x0026:
            int r7 = (int) r2
            r4 = r0
            r0 = r7
            r7 = r4
            goto L_0x003b
        L_0x002b:
            boolean r0 = r7 instanceof net.sourceforge.jtds.jdbc.ClobImpl
            if (r0 == 0) goto L_0x003a
            net.sourceforge.jtds.jdbc.ClobImpl r7 = (net.sourceforge.jtds.jdbc.ClobImpl) r7
            java.io.Reader r0 = r7.getCharacterStream()
            long r2 = r7.length()
            goto L_0x0026
        L_0x003a:
            r0 = 0
        L_0x003b:
            net.sourceforge.jtds.jdbc.ParamInfo r2 = new net.sourceforge.jtds.jdbc.ParamInfo
            r3 = 0
            r2.<init>(r6, r3, r7, r0)
            java.lang.String r7 = r6.sqlType
            java.lang.String r0 = "nvarchar"
            boolean r7 = r0.equals(r7)
            if (r7 != 0) goto L_0x0061
            java.lang.String r7 = r6.sqlType
            java.lang.String r0 = "nchar"
            boolean r7 = r0.equals(r7)
            if (r7 != 0) goto L_0x0061
            java.lang.String r6 = r6.sqlType
            java.lang.String r7 = "ntext"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0061
            if (r8 == 0) goto L_0x0062
        L_0x0061:
            r1 = 1
        L_0x0062:
            r2.isUnicode = r1
            r2.markerPos = r5
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.CachedResultSet.buildParameter(int, net.sourceforge.jtds.jdbc.ColInfo, java.lang.Object, boolean):net.sourceforge.jtds.jdbc.ParamInfo");
    }

    /* access modifiers changed from: protected */
    public Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        ParamInfo paramInfo;
        Object colValue = super.setColValue(i, i2, obj, i3);
        if (this.onInsertRow || this.currentRow != null) {
            int i4 = i - 1;
            ColInfo colInfo = this.columns[i4];
            boolean isUnicode = TdsData.isUnicode(colInfo);
            if (this.onInsertRow) {
                paramInfo = this.insertRow[i4];
                if (paramInfo == null) {
                    paramInfo = new ParamInfo(-1, isUnicode);
                    paramInfo.collation = colInfo.collation;
                    paramInfo.charsetInfo = colInfo.charsetInfo;
                    this.insertRow[i4] = paramInfo;
                }
            } else {
                if (this.updateRow == null) {
                    this.updateRow = new ParamInfo[this.columnCount];
                }
                paramInfo = this.updateRow[i4];
                if (paramInfo == null) {
                    paramInfo = new ParamInfo(-1, isUnicode);
                    paramInfo.collation = colInfo.collation;
                    paramInfo.charsetInfo = colInfo.charsetInfo;
                    this.updateRow[i4] = paramInfo;
                }
            }
            if (colValue == null) {
                paramInfo.value = null;
                paramInfo.length = 0;
                paramInfo.jdbcType = colInfo.jdbcType;
                paramInfo.isSet = true;
                if (paramInfo.jdbcType == 2 || paramInfo.jdbcType == 3) {
                    paramInfo.scale = 10;
                } else {
                    paramInfo.scale = 0;
                }
            } else {
                paramInfo.value = colValue;
                paramInfo.length = i3;
                paramInfo.isSet = true;
                paramInfo.jdbcType = i2;
                if (paramInfo.value instanceof BigDecimal) {
                    paramInfo.scale = ((BigDecimal) paramInfo.value).scale();
                } else {
                    paramInfo.scale = 0;
                }
            }
            return colValue;
        }
        throw new SQLException(Messages.get("error.resultset.norow"), "24000");
    }

    /* access modifiers changed from: 0000 */
    public ParamInfo[] buildWhereClause(StringBuilder sb, ArrayList arrayList, boolean z) throws SQLException {
        sb.append(" WHERE ");
        if (this.cursorName != null) {
            sb.append(" CURRENT OF ");
            sb.append(this.cursorName);
        } else {
            int i = 0;
            for (int i2 = 0; i2 < this.columns.length; i2++) {
                String str = "image";
                String str2 = "ntext";
                String str3 = "text";
                String str4 = " AND ";
                if (this.currentRow[i2] != null) {
                    String str5 = "=?";
                    if (!this.isKeyed || !z) {
                        if (!str3.equals(this.columns[i2].sqlType) && !str2.equals(this.columns[i2].sqlType) && !str.equals(this.columns[i2].sqlType) && this.columns[i2].tableName != null) {
                            if (i > 0) {
                                sb.append(str4);
                            }
                            sb.append(this.columns[i2].realName);
                            sb.append(str5);
                            i++;
                            arrayList.add(buildParameter(sb.length() - 1, this.columns[i2], this.currentRow[i2], this.connection.getUseUnicode()));
                        }
                    } else if (this.columns[i2].isKey) {
                        if (i > 0) {
                            sb.append(str4);
                        }
                        sb.append(this.columns[i2].realName);
                        sb.append(str5);
                        i++;
                        arrayList.add(buildParameter(sb.length() - 1, this.columns[i2], this.currentRow[i2], this.connection.getUseUnicode()));
                    }
                } else if (!str3.equals(this.columns[i2].sqlType) && !str2.equals(this.columns[i2].sqlType) && !str.equals(this.columns[i2].sqlType) && this.columns[i2].tableName != null) {
                    if (i > 0) {
                        sb.append(str4);
                    }
                    sb.append(this.columns[i2].realName);
                    sb.append(" IS NULL");
                }
            }
        }
        return (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]);
    }

    /* access modifiers changed from: protected */
    public void refreshKeyedRows() throws SQLException {
        StringBuilder sb = new StringBuilder((this.columns.length * 10) + 100);
        sb.append("SELECT ");
        int i = 0;
        for (int i2 = 0; i2 < this.columns.length; i2++) {
            if (!this.columns[i2].isKey && this.columns[i2].tableName != null) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(this.columns[i2].realName);
                i++;
            }
        }
        if (i != 0) {
            sb.append(" FROM ");
            sb.append(this.tableName);
            ArrayList arrayList = new ArrayList();
            buildWhereClause(sb, arrayList, true);
            ParamInfo[] paramInfoArr = (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]);
            TdsCore tds = this.statement.getTds();
            tds.executeSQL(sb.toString(), null, paramInfoArr, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            if (tds.isEndOfResponse()) {
                this.currentRow = null;
            } else if (!tds.getMoreResults() || !tds.getNextRow()) {
                this.currentRow = null;
            } else {
                Object[] rowData = tds.getRowData();
                int i3 = 0;
                for (int i4 = 0; i4 < this.columns.length; i4++) {
                    if (!this.columns[i4].isKey) {
                        int i5 = i3 + 1;
                        this.currentRow[i4] = rowData[i3];
                        i3 = i5;
                    }
                }
            }
            tds.clearResponseQueue();
            this.statement.getMessages().checkErrors();
            if (this.currentRow == null) {
                this.rowData.set(this.pos - 1, null);
                this.rowDeleted = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void refreshReRead() throws SQLException {
        int i = this.pos;
        cursorCreate();
        absolute(i);
    }

    public void setFetchSize(int i) throws SQLException {
        this.sizeChanged = i != this.fetchSize;
        super.setFetchSize(i);
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != -1) {
            cursorFetch(this.rowsInResult + 1);
        }
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != 0) {
            cursorFetch(0);
        }
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        } else if (this.updateRow != null) {
            int i = 0;
            this.rowUpdated = false;
            while (true) {
                ParamInfo[] paramInfoArr = this.updateRow;
                if (i < paramInfoArr.length) {
                    if (paramInfoArr[i] != null) {
                        paramInfoArr[i].clearInValue();
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                cursorClose();
            } finally {
                this.closed = true;
                this.statement = null;
            }
        }
    }

    public void deleteRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        String str = "24000";
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), str);
        } else if (!this.onInsertRow) {
            StringBuilder sb = new StringBuilder(128);
            ArrayList arrayList = new ArrayList();
            sb.append("DELETE FROM ");
            sb.append(this.tableName);
            int i = 0;
            this.updateTds.executeSQL(sb.toString(), null, buildWhereClause(sb, arrayList, false), false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.updateTds.isEndOfResponse()) {
                if (!this.updateTds.getMoreResults() && this.updateTds.isUpdateCount()) {
                    i = this.updateTds.getUpdateCount();
                }
            }
            this.updateTds.clearResponseQueue();
            this.statement.getMessages().checkErrors();
            if (i != 0) {
                this.rowDeleted = true;
                this.currentRow = null;
                if (this.resultSetType != 1003) {
                    this.rowData.set(this.pos - 1, null);
                    return;
                }
                return;
            }
            throw new SQLException(Messages.get("error.resultset.deletefail"), str);
        } else {
            throw new SQLException(Messages.get("error.resultset.insrow"), str);
        }
    }

    public void insertRow() throws SQLException {
        String str;
        checkOpen();
        checkUpdateable();
        String str2 = "24000";
        if (this.onInsertRow) {
            int i = 0;
            if (!this.tempResultSet) {
                StringBuilder sb = new StringBuilder(128);
                ArrayList arrayList = new ArrayList();
                sb.append("INSERT INTO ");
                sb.append(this.tableName);
                int length = sb.length();
                sb.append(" (");
                int i2 = 0;
                int i3 = 0;
                while (true) {
                    str = ", ";
                    if (i2 >= this.columnCount) {
                        break;
                    }
                    if (this.insertRow[i2] != null) {
                        if (i3 > 0) {
                            sb.append(str);
                        }
                        sb.append(this.columns[i2].realName);
                        i3++;
                    }
                    i2++;
                }
                sb.append(") VALUES(");
                int i4 = 0;
                for (int i5 = 0; i5 < this.columnCount; i5++) {
                    if (this.insertRow[i5] != null) {
                        if (i4 > 0) {
                            sb.append(str);
                        }
                        sb.append('?');
                        this.insertRow[i5].markerPos = sb.length() - 1;
                        arrayList.add(this.insertRow[i5]);
                        i4++;
                    }
                }
                sb.append(')');
                if (i4 == 0) {
                    sb.setLength(length);
                    if (this.isSybase) {
                        sb.append(" VALUES()");
                    } else {
                        sb.append(" DEFAULT VALUES");
                    }
                }
                this.updateTds.executeSQL(sb.toString(), null, (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]), false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                int i6 = 0;
                while (!this.updateTds.isEndOfResponse()) {
                    if (!this.updateTds.getMoreResults() && this.updateTds.isUpdateCount()) {
                        i6 = this.updateTds.getUpdateCount();
                    }
                }
                this.updateTds.clearResponseQueue();
                this.statement.getMessages().checkErrors();
                if (i6 < 1) {
                    throw new SQLException(Messages.get("error.resultset.insertfail"), str2);
                }
            }
            if (this.resultSetType >= 1005 || (this.resultSetType == 1003 && this.cursorName == null)) {
                JtdsConnection jtdsConnection = (JtdsConnection) this.statement.getConnection();
                Object[] newRow = newRow();
                int i7 = 0;
                while (true) {
                    ParamInfo[] paramInfoArr = this.insertRow;
                    if (i7 >= paramInfoArr.length) {
                        break;
                    }
                    if (paramInfoArr[i7] != null) {
                        newRow[i7] = Support.convert(jtdsConnection, paramInfoArr[i7].value, this.columns[i7].jdbcType, jtdsConnection.getCharset());
                    }
                    i7++;
                }
                this.rowData.add(newRow);
            }
            this.rowsInResult++;
            while (true) {
                ParamInfo[] paramInfoArr2 = this.insertRow;
                if (paramInfoArr2 != null && i < paramInfoArr2.length) {
                    if (paramInfoArr2[i] != null) {
                        paramInfoArr2[i].clearInValue();
                    }
                    i++;
                } else {
                    return;
                }
            }
        } else {
            throw new SQLException(Messages.get("error.resultset.notinsrow"), str2);
        }
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.insertRow = null;
        this.onInsertRow = false;
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.insertRow = new ParamInfo[this.columnCount];
        this.onInsertRow = true;
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        if (!this.onInsertRow) {
            if (this.concurrency != 1007) {
                cancelRowUpdates();
                this.rowUpdated = false;
            }
            if (this.resultSetType != 1003 && this.currentRow != null) {
                if (this.isKeyed) {
                    refreshKeyedRows();
                } else {
                    refreshReRead();
                }
            }
        } else {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
    }

    public void updateRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        int i = 0;
        this.rowUpdated = false;
        this.rowDeleted = false;
        String str = "24000";
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), str);
        } else if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), str);
        } else if (this.updateRow != null) {
            StringBuilder sb = new StringBuilder(128);
            ArrayList arrayList = new ArrayList();
            sb.append("UPDATE ");
            sb.append(this.tableName);
            sb.append(" SET ");
            int i2 = 0;
            boolean z = false;
            for (int i3 = 0; i3 < this.columnCount; i3++) {
                if (this.updateRow[i3] != null) {
                    if (i2 > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.columns[i3].realName);
                    sb.append("=?");
                    this.updateRow[i3].markerPos = sb.length() - 1;
                    arrayList.add(this.updateRow[i3]);
                    i2++;
                    if (this.columns[i3].isKey) {
                        z = true;
                    }
                }
            }
            if (i2 != 0) {
                this.updateTds.executeSQL(sb.toString(), null, buildWhereClause(sb, arrayList, false), false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                int i4 = 0;
                while (!this.updateTds.isEndOfResponse()) {
                    if (!this.updateTds.getMoreResults() && this.updateTds.isUpdateCount()) {
                        i4 = this.updateTds.getUpdateCount();
                    }
                }
                this.updateTds.clearResponseQueue();
                this.statement.getMessages().checkErrors();
                if (i4 != 0) {
                    if (this.resultSetType != 1004) {
                        JtdsConnection jtdsConnection = (JtdsConnection) this.statement.getConnection();
                        while (true) {
                            ParamInfo[] paramInfoArr = this.updateRow;
                            if (i >= paramInfoArr.length) {
                                break;
                            }
                            if (paramInfoArr[i] != null) {
                                if (!(paramInfoArr[i].value instanceof byte[]) || !(this.columns[i].jdbcType == 1 || this.columns[i].jdbcType == 12 || this.columns[i].jdbcType == -1)) {
                                    this.currentRow[i] = Support.convert(jtdsConnection, this.updateRow[i].value, this.columns[i].jdbcType, jtdsConnection.getCharset());
                                } else {
                                    try {
                                        this.currentRow[i] = new String((byte[]) this.updateRow[i].value, jtdsConnection.getCharset());
                                    } catch (UnsupportedEncodingException unused) {
                                        this.currentRow[i] = new String((byte[]) this.updateRow[i].value);
                                    }
                                }
                            }
                            i++;
                        }
                    }
                    if (!z || this.resultSetType < 1005) {
                        this.rowUpdated = true;
                    } else {
                        this.rowData.add(this.currentRow);
                        this.rowsInResult = this.rowData.size();
                        this.rowData.set(this.pos - 1, null);
                        this.currentRow = null;
                        this.rowDeleted = true;
                    }
                    cancelRowUpdates();
                    return;
                }
                throw new SQLException(Messages.get("error.resultset.updatefail"), str);
            }
        }
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        return cursorFetch(1);
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        return cursorFetch(this.rowsInResult);
    }

    public boolean next() throws SQLException {
        checkOpen();
        if (this.pos != -1) {
            return cursorFetch(this.pos + 1);
        }
        return false;
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos == -1) {
            this.pos = this.rowsInResult + 1;
        }
        return cursorFetch(this.pos - 1);
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        return this.rowDeleted;
    }

    public boolean rowInserted() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean rowUpdated() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean absolute(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        if (i < 1) {
            i += this.rowsInResult + 1;
        }
        return cursorFetch(i);
    }

    public boolean relative(int i) throws SQLException {
        checkScrollable();
        if (this.pos == -1) {
            return absolute(this.rowsInResult + 1 + i);
        }
        return absolute(this.pos + i);
    }

    public String getCursorName() throws SQLException {
        checkOpen();
        if (this.cursorName != null && !this.cursorName.startsWith("_jtds")) {
            return this.cursorName;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }
}
