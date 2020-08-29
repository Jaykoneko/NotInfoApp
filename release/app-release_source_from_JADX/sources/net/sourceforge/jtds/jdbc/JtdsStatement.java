package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class JtdsStatement implements Statement {
    static final int BOOLEAN = 16;
    static final int CLOSE_ALL_RESULTS = 3;
    static final int CLOSE_CURRENT_RESULT = 1;
    static final int DATALINK = 70;
    static final int DEFAULT_FETCH_SIZE = 100;
    static final Integer EXECUTE_FAILED = new Integer(-3);
    static final String GENKEYCOL = "_JTDS_GENE_R_ATED_KEYS_";
    static final int KEEP_CURRENT_RESULT = 2;
    static final int NO_GENERATED_KEYS = 2;
    static final int RETURN_GENERATED_KEYS = 1;
    static final Integer SUCCESS_NO_INFO = new Integer(-2);
    private final AtomicInteger _Closed = new AtomicInteger();
    protected ArrayList batchValues;
    protected ColInfo[] colMetaData;
    protected JtdsConnection connection;
    protected JtdsResultSet currentResult;
    protected String cursorName;
    protected boolean escapeProcessing = true;
    protected int fetchDirection = 1000;
    protected int fetchSize = 100;
    protected CachedResultSet genKeyResultSet;
    protected int maxFieldSize;
    protected int maxRows;
    protected final SQLDiagnostic messages;
    protected ArrayList openResultSets;
    protected int queryTimeout;
    protected final LinkedList resultQueue = new LinkedList();
    protected int resultSetConcurrency = PointerIconCompat.TYPE_CROSSHAIR;
    protected int resultSetType = PointerIconCompat.TYPE_HELP;
    protected TdsCore tds;
    private int updateCount = -1;

    JtdsStatement(JtdsConnection jtdsConnection, int i, int i2) throws SQLException {
        String str = "HY092";
        String str2 = "error.generic.badparam";
        String str3 = "prepareStatement";
        String str4 = "createStatement";
        String str5 = "prepareCall";
        if (i < 1003 || i > 1006) {
            if (this instanceof JtdsCallableStatement) {
                str3 = str5;
            } else if (!(this instanceof JtdsPreparedStatement)) {
                str3 = str4;
            }
            throw new SQLException(Messages.get(str2, "resultSetType", str3), str);
        } else if (i2 < 1007 || i2 > 1010) {
            if (this instanceof JtdsCallableStatement) {
                str3 = str5;
            } else if (!(this instanceof JtdsPreparedStatement)) {
                str3 = str4;
            }
            throw new SQLException(Messages.get(str2, "resultSetConcurrency", str3), str);
        } else {
            this.connection = jtdsConnection;
            this.resultSetType = i;
            this.resultSetConcurrency = i2;
            TdsCore cachedTds = jtdsConnection.getCachedTds();
            this.tds = cachedTds;
            if (cachedTds == null) {
                this.messages = new SQLDiagnostic(jtdsConnection.getServerType());
                this.tds = new TdsCore(this.connection, this.messages);
                return;
            }
            this.messages = cachedTds.getMessages();
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        try {
            close();
        } catch (SQLException unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public TdsCore getTds() {
        return this.tds;
    }

    /* access modifiers changed from: 0000 */
    public SQLDiagnostic getMessages() {
        return this.messages;
    }

    /* access modifiers changed from: protected */
    public void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Statement"), "HY010");
        }
    }

    /* access modifiers changed from: protected */
    public void checkCursorException(SQLException sQLException) throws SQLException {
        JtdsConnection jtdsConnection = this.connection;
        if (jtdsConnection != null && !jtdsConnection.isClosed()) {
            if (!"HYT00".equals(sQLException.getSQLState())) {
                if (!"HY008".equals(sQLException.getSQLState())) {
                    if (this.connection.getServerType() != 2) {
                        int errorCode = sQLException.getErrorCode();
                        if ((errorCode < 16900 || errorCode > 16999) && errorCode != 6819 && errorCode != 8654 && errorCode != 8162) {
                            throw sQLException;
                        }
                        return;
                    }
                    return;
                }
            }
        }
        throw sQLException;
    }

    static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    /* access modifiers changed from: 0000 */
    public void closeCurrentResultSet() throws SQLException {
        try {
            if (this.currentResult != null) {
                this.currentResult.close();
            }
        } finally {
            this.currentResult = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void closeAllResultSets() throws SQLException {
        try {
            if (this.openResultSets != null) {
                for (int i = 0; i < this.openResultSets.size(); i++) {
                    JtdsResultSet jtdsResultSet = (JtdsResultSet) this.openResultSets.get(i);
                    if (jtdsResultSet != null) {
                        jtdsResultSet.close();
                    }
                }
            }
            closeCurrentResultSet();
        } finally {
            this.openResultSets = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void addWarning(SQLWarning sQLWarning) {
        this.messages.addWarning(sQLWarning);
    }

    /* access modifiers changed from: protected */
    public SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        int i3 = i;
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            Object obj = this.batchValues.get(i4);
            i4++;
            boolean z = i4 % i2 == 0 || i4 == i3;
            this.tds.startBatch();
            this.tds.executeSQL((String) obj, null, null, false, 0, -1, -1, z);
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
        int i3 = i;
        StringBuilder sb = new StringBuilder(i3 * 32);
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            Object obj = this.batchValues.get(i4);
            i4++;
            boolean z = i4 % i2 == 0 || i4 == i3;
            sb.append((String) obj);
            sb.append(' ');
            if (z) {
                this.tds.executeSQL(sb.toString(), null, null, false, 0, -1, -1, true);
                sb.setLength(0);
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
    public ResultSet executeSQLQuery(String str, String str2, ParamInfo[] paramInfoArr, boolean z) throws SQLException {
        String str3;
        if (z) {
            try {
                if (this.connection.getServerType() == 1) {
                    MSCursorResultSet mSCursorResultSet = new MSCursorResultSet(this, str, str2, paramInfoArr, this.resultSetType, this.resultSetConcurrency);
                    this.currentResult = mSCursorResultSet;
                    return mSCursorResultSet;
                }
                CachedResultSet cachedResultSet = new CachedResultSet(this, str, str2, paramInfoArr, this.resultSetType, this.resultSetConcurrency);
                this.currentResult = cachedResultSet;
                return cachedResultSet;
            } catch (SQLException e) {
                checkCursorException(e);
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                sb.append(e.getSQLState());
                sb.append("] ");
                sb.append(e.getMessage());
                str3 = sb.toString();
            }
        } else {
            str3 = null;
            if (str2 == null || !this.connection.getUseMetadataCache() || this.connection.getPrepareSql() != 3 || this.colMetaData == null || this.connection.getServerType() != 1) {
                this.tds.executeSQL(str, str2, paramInfoArr, false, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
            } else {
                this.tds.setColumns(this.colMetaData);
                this.tds.executeSQL(str, str2, paramInfoArr, true, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
            }
            if (str3 != null) {
                addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) str3), "01000"));
            }
            while (!this.tds.getMoreResults()) {
                if (this.tds.isEndOfResponse()) {
                    break;
                }
            }
            this.messages.checkErrors();
            if (this.tds.isResultSet()) {
                JtdsResultSet jtdsResultSet = new JtdsResultSet(this, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, this.tds.getColumns());
                this.currentResult = jtdsResultSet;
                return jtdsResultSet;
            }
            throw new SQLException(Messages.get("error.statement.noresult"), "24000");
        }
    }

    /* access modifiers changed from: protected */
    public boolean executeSQL(String str, String str2, ParamInfo[] paramInfoArr, boolean z, boolean z2) throws SQLException {
        String str3;
        boolean z3 = z;
        if (this.connection.getServerType() != 1 || z3 || !z2) {
            str3 = null;
        } else {
            try {
                MSCursorResultSet mSCursorResultSet = new MSCursorResultSet(this, str, str2, paramInfoArr, this.resultSetType, this.resultSetConcurrency);
                this.currentResult = mSCursorResultSet;
                return true;
            } catch (SQLException e) {
                checkCursorException(e);
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                sb.append(e.getSQLState());
                sb.append("] ");
                sb.append(e.getMessage());
                str3 = sb.toString();
            }
        }
        this.tds.executeSQL(str, str2, paramInfoArr, false, this.queryTimeout, this.maxRows, this.maxFieldSize, true);
        if (str3 != null) {
            addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) str3), "01000"));
        }
        if (!processResults(z3)) {
            return false;
        }
        Object removeFirst = this.resultQueue.removeFirst();
        if (removeFirst instanceof Integer) {
            this.updateCount = ((Integer) removeFirst).intValue();
            return false;
        }
        this.currentResult = (JtdsResultSet) removeFirst;
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00c3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean processResults(boolean r6) throws java.sql.SQLException {
        /*
            r5 = this;
            java.util.LinkedList r0 = r5.resultQueue
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x00e6
        L_0x0008:
            net.sourceforge.jtds.jdbc.TdsCore r0 = r5.tds
            boolean r0 = r0.isEndOfResponse()
            r1 = 1
            if (r0 != 0) goto L_0x00d7
            net.sourceforge.jtds.jdbc.TdsCore r0 = r5.tds
            boolean r0 = r0.getMoreResults()
            if (r0 != 0) goto L_0x0041
            net.sourceforge.jtds.jdbc.TdsCore r0 = r5.tds
            boolean r0 = r0.isUpdateCount()
            if (r0 == 0) goto L_0x0008
            if (r6 == 0) goto L_0x0030
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r5.connection
            boolean r0 = r0.getLastUpdateCount()
            if (r0 == 0) goto L_0x0030
            java.util.LinkedList r0 = r5.resultQueue
            r0.clear()
        L_0x0030:
            java.util.LinkedList r0 = r5.resultQueue
            java.lang.Integer r1 = new java.lang.Integer
            net.sourceforge.jtds.jdbc.TdsCore r2 = r5.tds
            int r2 = r2.getUpdateCount()
            r1.<init>(r2)
            r0.addLast(r1)
            goto L_0x0008
        L_0x0041:
            net.sourceforge.jtds.jdbc.TdsCore r0 = r5.tds
            net.sourceforge.jtds.jdbc.ColInfo[] r0 = r0.getColumns()
            int r2 = r0.length
            if (r2 != r1) goto L_0x008a
            r2 = 0
            r3 = r0[r2]
            java.lang.String r3 = r3.name
            java.lang.String r4 = "_JTDS_GENE_R_ATED_KEYS_"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x008a
            r0 = r0[r2]
            java.lang.String r1 = "ID"
            r0.name = r1
            r0 = 0
            r5.genKeyResultSet = r0
        L_0x0060:
            net.sourceforge.jtds.jdbc.TdsCore r0 = r5.tds
            boolean r0 = r0.getNextRow()
            if (r0 == 0) goto L_0x0008
            net.sourceforge.jtds.jdbc.CachedResultSet r0 = r5.genKeyResultSet
            if (r0 != 0) goto L_0x0080
            net.sourceforge.jtds.jdbc.CachedResultSet r0 = new net.sourceforge.jtds.jdbc.CachedResultSet
            net.sourceforge.jtds.jdbc.TdsCore r1 = r5.tds
            net.sourceforge.jtds.jdbc.ColInfo[] r1 = r1.getColumns()
            net.sourceforge.jtds.jdbc.TdsCore r2 = r5.tds
            java.lang.Object[] r2 = r2.getRowData()
            r0.<init>(r5, r1, r2)
            r5.genKeyResultSet = r0
            goto L_0x0060
        L_0x0080:
            net.sourceforge.jtds.jdbc.TdsCore r1 = r5.tds
            java.lang.Object[] r1 = r1.getRowData()
            r0.addRow(r1)
            goto L_0x0060
        L_0x008a:
            if (r6 == 0) goto L_0x00aa
            java.util.LinkedList r6 = r5.resultQueue
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0095
            goto L_0x00aa
        L_0x0095:
            java.sql.SQLException r6 = new java.sql.SQLException
            java.lang.String r0 = "error.statement.nocount"
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Messages.get(r0)
            java.lang.String r1 = "07000"
            r6.<init>(r0, r1)
            net.sourceforge.jtds.jdbc.SQLDiagnostic r0 = r5.messages
            java.sql.SQLException r0 = r0.exceptions
            r6.setNextException(r0)
            throw r6
        L_0x00aa:
            net.sourceforge.jtds.jdbc.TdsCore r6 = r5.tds
            java.lang.Object[] r6 = r6.getComputedRowData()
            if (r6 == 0) goto L_0x00c3
            java.util.LinkedList r0 = r5.resultQueue
            net.sourceforge.jtds.jdbc.CachedResultSet r2 = new net.sourceforge.jtds.jdbc.CachedResultSet
            net.sourceforge.jtds.jdbc.TdsCore r3 = r5.tds
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r3.getComputedColumns()
            r2.<init>(r5, r3, r6)
            r0.add(r2)
            goto L_0x00d7
        L_0x00c3:
            java.util.LinkedList r6 = r5.resultQueue
            net.sourceforge.jtds.jdbc.JtdsResultSet r0 = new net.sourceforge.jtds.jdbc.JtdsResultSet
            r2 = 1003(0x3eb, float:1.406E-42)
            r3 = 1007(0x3ef, float:1.411E-42)
            net.sourceforge.jtds.jdbc.TdsCore r4 = r5.tds
            net.sourceforge.jtds.jdbc.ColInfo[] r4 = r4.getColumns()
            r0.<init>(r5, r2, r3, r4)
            r6.add(r0)
        L_0x00d7:
            net.sourceforge.jtds.jdbc.SQLDiagnostic r6 = r5.getMessages()
            r6.checkErrors()
            java.util.LinkedList r6 = r5.resultQueue
            boolean r6 = r6.isEmpty()
            r6 = r6 ^ r1
            return r6
        L_0x00e6:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.String r0 = "There should be no queued results."
            r6.<init>(r0)
            goto L_0x00ef
        L_0x00ee:
            throw r6
        L_0x00ef:
            goto L_0x00ee
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsStatement.processResults(boolean):boolean");
    }

    /* access modifiers changed from: protected */
    public void cacheResults() throws SQLException {
        processResults(false);
    }

    /* access modifiers changed from: protected */
    public void reset() throws SQLException {
        this.updateCount = -1;
        this.resultQueue.clear();
        this.genKeyResultSet = null;
        this.tds.clearResponseQueue();
        this.messages.clearWarnings();
        this.messages.exceptions = null;
        closeAllResultSets();
    }

    private boolean executeImpl(String str, int i, boolean z) throws SQLException {
        String str2;
        String str3;
        boolean z2;
        reset();
        if (str == null || str.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        if (this.escapeProcessing) {
            String[] parse = SQLParser.parse(str, null, this.connection, false);
            if (parse[1].length() == 0) {
                str3 = parse[0];
                str2 = parse[2];
            } else {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
        } else {
            str3 = str.trim();
            str2 = str3.length() > 5 ? str3.substring(0, 6).toLowerCase() : "";
        }
        if (i == 1) {
            z2 = true;
        } else if (i == 2) {
            z2 = false;
        } else {
            throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "autoGeneratedKeys"), "HY092");
        }
        if (z2) {
            if (this.connection.getServerType() != 1 || this.connection.getDatabaseMajorVersion() < 8) {
                StringBuilder sb = new StringBuilder();
                sb.append(str3);
                sb.append(" SELECT @@IDENTITY AS _JTDS_GENE_R_ATED_KEYS_");
                str3 = sb.toString();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str3);
                sb2.append(" SELECT SCOPE_IDENTITY() AS _JTDS_GENE_R_ATED_KEYS_");
                str3 = sb2.toString();
            }
        }
        return executeSQL(str3, null, null, z, !z && useCursor(z2, str2));
    }

    /* access modifiers changed from: protected */
    public boolean useCursor(boolean z, String str) {
        return !(this.resultSetType == 1003 && this.resultSetConcurrency == 1007 && !this.connection.getUseCursors() && this.cursorName == null) && !z && (str == null || "select".equals(str) || str.startsWith("exec"));
    }

    /* access modifiers changed from: 0000 */
    public int getDefaultFetchSize() {
        int i = this.maxRows;
        if (i <= 0 || i >= 100) {
            return 100;
        }
        return i;
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return this.fetchDirection;
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return this.fetchSize;
    }

    public int getMaxFieldSize() throws SQLException {
        checkOpen();
        return this.maxFieldSize;
    }

    public int getMaxRows() throws SQLException {
        checkOpen();
        return this.maxRows;
    }

    public int getQueryTimeout() throws SQLException {
        checkOpen();
        return this.queryTimeout;
    }

    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return this.resultSetConcurrency;
    }

    public int getResultSetHoldability() throws SQLException {
        checkOpen();
        return 1;
    }

    public int getResultSetType() throws SQLException {
        checkOpen();
        return this.resultSetType;
    }

    public int getUpdateCount() throws SQLException {
        checkOpen();
        return this.updateCount;
    }

    public void cancel() throws SQLException {
        checkOpen();
        TdsCore tdsCore = this.tds;
        if (tdsCore != null) {
            tdsCore.cancel(false);
        }
    }

    public void clearBatch() throws SQLException {
        checkOpen();
        ArrayList arrayList = this.batchValues;
        if (arrayList != null) {
            arrayList.clear();
        }
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() throws java.sql.SQLException {
        /*
            r4 = this;
            java.util.concurrent.atomic.AtomicInteger r0 = r4._Closed
            r1 = 0
            r2 = 1
            boolean r0 = r0.compareAndSet(r1, r2)
            if (r0 == 0) goto L_0x0062
            r0 = 0
            r4.reset()     // Catch:{ all -> 0x005d }
            r1 = 2
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r4.connection     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            boolean r2 = r2.isClosed()     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            if (r2 != 0) goto L_0x001e
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r4.connection     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            net.sourceforge.jtds.jdbc.TdsCore r3 = r4.tds     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            r2.releaseTds(r3)     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
        L_0x001e:
            net.sourceforge.jtds.jdbc.TdsCore r2 = r4.tds     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            r2.checkErrors()     // Catch:{ SQLException -> 0x0046, all -> 0x0036 }
            java.util.concurrent.atomic.AtomicInteger r2 = r4._Closed     // Catch:{ all -> 0x005d }
            r2.set(r1)     // Catch:{ all -> 0x005d }
            r4.tds = r0     // Catch:{ all -> 0x005d }
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r4.connection     // Catch:{ all -> 0x005d }
            r1.removeStatement(r4)     // Catch:{ all -> 0x005d }
            r4.connection = r0     // Catch:{ all -> 0x005d }
            goto L_0x0056
        L_0x0036:
            r2 = move-exception
            java.util.concurrent.atomic.AtomicInteger r3 = r4._Closed     // Catch:{ all -> 0x005d }
            r3.set(r1)     // Catch:{ all -> 0x005d }
            r4.tds = r0     // Catch:{ all -> 0x005d }
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r4.connection     // Catch:{ all -> 0x005d }
            r1.removeStatement(r4)     // Catch:{ all -> 0x005d }
            r4.connection = r0     // Catch:{ all -> 0x005d }
            throw r2     // Catch:{ all -> 0x005d }
        L_0x0046:
            r2 = move-exception
            java.util.concurrent.atomic.AtomicInteger r3 = r4._Closed     // Catch:{ all -> 0x005a }
            r3.set(r1)     // Catch:{ all -> 0x005a }
            r4.tds = r0     // Catch:{ all -> 0x005a }
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r4.connection     // Catch:{ all -> 0x005a }
            r1.removeStatement(r4)     // Catch:{ all -> 0x005a }
            r4.connection = r0     // Catch:{ all -> 0x005a }
            r0 = r2
        L_0x0056:
            if (r0 != 0) goto L_0x0059
            goto L_0x0062
        L_0x0059:
            throw r0
        L_0x005a:
            r1 = move-exception
            r0 = r2
            goto L_0x005e
        L_0x005d:
            r1 = move-exception
        L_0x005e:
            if (r0 == 0) goto L_0x0061
            throw r0
        L_0x0061:
            throw r1
        L_0x0062:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsStatement.close():void");
    }

    public boolean getMoreResults() throws SQLException {
        checkOpen();
        return getMoreResults(3);
    }

    public int[] executeBatch() throws SQLException, BatchUpdateException {
        SQLException sQLException;
        checkOpen();
        reset();
        ArrayList arrayList = this.batchValues;
        if (arrayList == null || arrayList.size() == 0) {
            return new int[0];
        }
        int size = this.batchValues.size();
        int batchSize = this.connection.getBatchSize();
        if (batchSize == 0) {
            batchSize = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
        ArrayList arrayList2 = new ArrayList(size);
        try {
            synchronized (this.connection) {
                if (this.connection.getServerType() == 2 && this.connection.getTdsVersion() == 2) {
                    sQLException = executeSybaseBatch(size, batchSize, arrayList2);
                } else {
                    sQLException = executeMSBatch(size, batchSize, arrayList2);
                }
            }
            int[] iArr = new int[size];
            int size2 = arrayList2.size();
            int i = 0;
            while (i < size2 && i < size) {
                iArr[i] = ((Integer) arrayList2.get(i)).intValue();
                i++;
            }
            while (size2 < size) {
                iArr[size2] = EXECUTE_FAILED.intValue();
                size2++;
            }
            if (sQLException == null) {
                clearBatch();
                return iArr;
            }
            BatchUpdateException batchUpdateException = new BatchUpdateException(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode(), iArr);
            batchUpdateException.setNextException(sQLException.getNextException());
            throw batchUpdateException;
        } catch (BatchUpdateException e) {
            throw e;
        } catch (SQLException e2) {
            try {
                throw new BatchUpdateException(e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), new int[0]);
            } catch (Throwable th) {
                clearBatch();
                throw th;
            }
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        checkOpen();
        switch (i) {
            case 1000:
            case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
            case PointerIconCompat.TYPE_HAND /*1002*/:
                this.fetchDirection = i;
                return;
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "direction"), "24000");
        }
    }

    public void setFetchSize(int i) throws SQLException {
        checkOpen();
        String str = "HY092";
        if (i >= 0) {
            int i2 = this.maxRows;
            if (i2 <= 0 || i <= i2) {
                if (i == 0) {
                    i = getDefaultFetchSize();
                }
                this.fetchSize = i;
                return;
            }
            throw new SQLException(Messages.get("error.statement.gtmaxrows"), str);
        }
        throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setFetchSize"), str);
    }

    public void setMaxFieldSize(int i) throws SQLException {
        checkOpen();
        if (i >= 0) {
            this.maxFieldSize = i;
            return;
        }
        throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setMaxFieldSize"), "HY092");
    }

    public void setMaxRows(int i) throws SQLException {
        checkOpen();
        if (i >= 0) {
            if (i > 0 && i < this.fetchSize) {
                this.fetchSize = i;
            }
            this.maxRows = i;
            return;
        }
        throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setMaxRows"), "HY092");
    }

    public void setQueryTimeout(int i) throws SQLException {
        checkOpen();
        if (i >= 0) {
            this.queryTimeout = i;
            return;
        }
        throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setQueryTimeout"), "HY092");
    }

    public boolean getMoreResults(int i) throws SQLException {
        checkOpen();
        if (i == 1) {
            this.updateCount = -1;
            closeCurrentResultSet();
        } else if (i == 2) {
            this.updateCount = -1;
            if (this.openResultSets == null) {
                this.openResultSets = new ArrayList();
            }
            JtdsResultSet jtdsResultSet = this.currentResult;
            if ((jtdsResultSet instanceof MSCursorResultSet) || (jtdsResultSet instanceof CachedResultSet)) {
                this.openResultSets.add(this.currentResult);
            } else if (jtdsResultSet != null) {
                jtdsResultSet.cacheResultSetRows();
                this.openResultSets.add(this.currentResult);
            }
            this.currentResult = null;
        } else if (i == 3) {
            this.updateCount = -1;
            closeAllResultSets();
        } else {
            throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "current"), "HY092");
        }
        this.messages.checkErrors();
        if (this.resultQueue.isEmpty() && !processResults(false)) {
            return false;
        }
        Object removeFirst = this.resultQueue.removeFirst();
        if (removeFirst instanceof Integer) {
            this.updateCount = ((Integer) removeFirst).intValue();
            return false;
        }
        this.currentResult = (JtdsResultSet) removeFirst;
        return true;
    }

    public void setEscapeProcessing(boolean z) throws SQLException {
        checkOpen();
        this.escapeProcessing = z;
    }

    public int executeUpdate(String str) throws SQLException {
        return executeUpdate(str, 2);
    }

    public void addBatch(String str) throws SQLException {
        checkOpen();
        if (str != null) {
            if (this.batchValues == null) {
                this.batchValues = new ArrayList();
            }
            if (this.escapeProcessing) {
                String[] parse = SQLParser.parse(str, null, this.connection, false);
                if (parse[1].length() == 0) {
                    str = parse[0];
                } else {
                    throw new SQLException(Messages.get("error.statement.badsql"), "07000");
                }
            }
            this.batchValues.add(str);
            return;
        }
        throw null;
    }

    public void setCursorName(String str) throws SQLException {
        checkOpen();
        this.cursorName = str;
        if (str != null) {
            this.resultSetType = PointerIconCompat.TYPE_HELP;
            this.fetchSize = 1;
        }
    }

    public boolean execute(String str) throws SQLException {
        checkOpen();
        return executeImpl(str, 2, false);
    }

    public int executeUpdate(String str, int i) throws SQLException {
        checkOpen();
        executeImpl(str, i, true);
        int updateCount2 = getUpdateCount();
        if (updateCount2 == -1) {
            return 0;
        }
        return updateCount2;
    }

    public boolean execute(String str, int i) throws SQLException {
        checkOpen();
        return executeImpl(str, i, false);
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        checkOpen();
        String str2 = "HY092";
        String str3 = "executeUpdate";
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (iArr.length == 1) {
            return executeUpdate(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) str3), str2);
        }
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        checkOpen();
        String str2 = "HY092";
        String str3 = "execute";
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (iArr.length == 1) {
            return executeImpl(str, 1, false);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) str3), str2);
        }
    }

    public Connection getConnection() throws SQLException {
        checkOpen();
        return this.connection;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        checkOpen();
        if (this.genKeyResultSet == null) {
            this.genKeyResultSet = new CachedResultSet(this, new String[]{"ID"}, new int[]{4});
        }
        this.genKeyResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return this.genKeyResultSet;
    }

    public ResultSet getResultSet() throws SQLException {
        checkOpen();
        JtdsResultSet jtdsResultSet = this.currentResult;
        if ((jtdsResultSet instanceof MSCursorResultSet) || (jtdsResultSet instanceof CachedResultSet)) {
            return this.currentResult;
        }
        if (jtdsResultSet == null || (this.resultSetType == 1003 && this.resultSetConcurrency == 1007)) {
            return this.currentResult;
        }
        CachedResultSet cachedResultSet = new CachedResultSet(this.currentResult, true);
        this.currentResult = cachedResultSet;
        return cachedResultSet;
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.messages.getWarnings();
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        checkOpen();
        String str2 = "HY092";
        String str3 = "executeUpdate";
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (strArr.length == 1) {
            return executeUpdate(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) str3), str2);
        }
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        checkOpen();
        String str2 = "HY092";
        String str3 = "execute";
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (strArr.length == 1) {
            return executeImpl(str, 1, false);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) str3), str2);
        }
    }

    public ResultSet executeQuery(String str) throws SQLException {
        checkOpen();
        reset();
        if (str == null || str.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        if (this.escapeProcessing) {
            String[] parse = SQLParser.parse(str, null, this.connection, false);
            if (parse[1].length() == 0) {
                str = parse[0];
            } else {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
        }
        return executeSQLQuery(str, null, null, useCursor(false, null));
    }

    public boolean isClosed() throws SQLException {
        return this._Closed.get() == 2;
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

    public <T> T unwrap(Class<T> cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }
}
