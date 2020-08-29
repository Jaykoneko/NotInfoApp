package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.sourceforge.jtds.ssl.Ssl;
import net.sourceforge.jtds.util.Logger;
import net.sourceforge.jtds.util.SSPIJNIClient;
import net.sourceforge.jtds.util.TimerThread;
import net.sourceforge.jtds.util.TimerThread.TimerListener;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class TdsCore {
    private static final byte ALTMETADATA_TOKEN = -120;
    private static final int ASYNC_CANCEL = 0;
    public static final byte CANCEL_PKT = 6;
    public static final int DEFAULT_MIN_PKT_SIZE_TDS70 = 4096;
    static final byte DONE_CANCEL = 32;
    private static final byte DONE_END_OF_RESPONSE = Byte.MIN_VALUE;
    private static final byte DONE_ERROR = 2;
    private static final byte DONE_MORE_RESULTS = 1;
    private static final byte DONE_ROW_COUNT = 16;
    private static final ParamInfo[] EMPTY_PARAMETER_INFO = new ParamInfo[0];
    public static final int EXECUTE_SQL = 2;
    public static final byte LOGIN_PKT = 2;
    public static final int MAX_PKT_SIZE = 32768;
    public static final int MIN_PKT_SIZE = 512;
    public static final byte MSDTC_PKT = 14;
    public static final byte MSLOGIN_PKT = 16;
    public static final byte NTLMAUTH_PKT = 17;
    public static final int PKT_HDR_LEN = 8;
    public static final byte PRELOGIN_PKT = 18;
    public static final int PREPARE = 3;
    public static final byte QUERY_PKT = 1;
    public static final byte REPLY_PKT = 4;
    public static final byte RPC_PKT = 3;
    public static final int SSL_CLIENT_FORCE_ENCRYPT = 1;
    public static final int SSL_ENCRYPT_LOGIN = 0;
    public static final int SSL_NO_ENCRYPT = 2;
    public static final int SSL_SERVER_FORCE_ENCRYPT = 3;
    public static final byte SYBQUERY_PKT = 15;
    static final int SYB_BIGINT = 64;
    static final int SYB_BITNULL = 4;
    static final int SYB_DATETIME = 2;
    static final int SYB_EXTCOLINFO = 8;
    static final int SYB_LONGDATA = 1;
    static final int SYB_UNICODE = 16;
    static final int SYB_UNITEXT = 32;
    private static final byte TDS5_DYNAMIC_TOKEN = -25;
    private static final byte TDS5_PARAMFMT2_TOKEN = 32;
    private static final byte TDS5_PARAMFMT_TOKEN = -20;
    private static final byte TDS5_PARAMS_TOKEN = -41;
    private static final byte TDS5_WIDE_RESULT = 97;
    private static final byte TDS7_RESULT_TOKEN = -127;
    private static final byte TDS_ALTROW = -45;
    private static final byte TDS_AUTH_TOKEN = -19;
    private static final byte TDS_CAP_TOKEN = -30;
    private static final byte TDS_CLOSE_TOKEN = 113;
    private static final byte TDS_COLFMT_TOKEN = -95;
    private static final byte TDS_COLINFO_TOKEN = -91;
    private static final byte TDS_COLNAME_TOKEN = -96;
    private static final byte TDS_COMP_NAMES_TOKEN = -89;
    private static final byte TDS_COMP_RESULT_TOKEN = -88;
    private static final byte TDS_CONTROL_TOKEN = -82;
    private static final byte TDS_DBRPC_TOKEN = -26;
    private static final byte TDS_DONEINPROC_TOKEN = -1;
    private static final byte TDS_DONEPROC_TOKEN = -2;
    private static final byte TDS_DONE_TOKEN = -3;
    private static final byte TDS_ENVCHANGE_TOKEN = -29;
    private static final byte TDS_ENV_CHARSET = 3;
    private static final byte TDS_ENV_DATABASE = 1;
    private static final byte TDS_ENV_LANG = 2;
    private static final byte TDS_ENV_LCID = 5;
    private static final byte TDS_ENV_PACKSIZE = 4;
    private static final byte TDS_ENV_SQLCOLLATION = 7;
    private static final byte TDS_ERROR_TOKEN = -86;
    private static final byte TDS_INFO_TOKEN = -85;
    private static final byte TDS_LANG_TOKEN = 33;
    private static final byte TDS_LOGINACK_TOKEN = -83;
    private static final byte TDS_MSG50_TOKEN = -27;
    private static final byte TDS_OFFSETS_TOKEN = 120;
    private static final byte TDS_ORDER_TOKEN = -87;
    private static final byte TDS_PARAM_TOKEN = -84;
    private static final byte TDS_PROCID = 124;
    private static final byte TDS_RESULT_TOKEN = -18;
    private static final byte TDS_RETURNSTATUS_TOKEN = 121;
    private static final byte TDS_ROW_TOKEN = -47;
    private static final byte TDS_TABNAME_TOKEN = -92;
    public static final int TEMPORARY_STORED_PROCEDURES = 1;
    private static final int TIMEOUT_CANCEL = 1;
    public static final int UNPREPARED = 0;
    private static String hostName;
    private static SSPIJNIClient sspiJNIClient;
    private static HashMap tds8SpNames;
    private boolean _ErrorReceived;
    private GSSContext _gssContext;
    private final int[] cancelMonitor = new int[1];
    private boolean cancelPending;
    private ColInfo[] columns;
    private ColInfo[] computedColumns;
    private Object[] computedRowData;
    private final JtdsConnection connection;
    private Semaphore connectionLock;
    private final TdsToken currentToken = new TdsToken();
    private boolean endOfResponse = true;
    private boolean endOfResults = true;
    private boolean fatalError;

    /* renamed from: in */
    private final ResponseStream f122in;
    private boolean inBatch;
    private boolean isClosed;
    private final SQLDiagnostic messages;
    private int nextParam = -1;
    byte[] nonce;
    private boolean ntlmAuthSSO;
    byte[] ntlmMessage;
    byte[] ntlmTarget;
    private final RequestStream out;
    private ParamInfo[] parameters;
    private ParamInfo returnParam;
    private Integer returnStatus;
    private Object[] rowData;
    private final int serverType;
    private final SharedSocket socket;
    private int sslMode = 2;
    private TableMetaData[] tables;
    private int tdsVersion;

    private static class TableMetaData {
        String catalog;
        String name;
        String schema;

        private TableMetaData() {
        }
    }

    private static class TdsToken {
        Object[] dynamParamData;
        ColInfo[] dynamParamInfo;
        byte operation;
        byte status;
        byte token;
        int updateCount;

        private TdsToken() {
        }

        /* access modifiers changed from: 0000 */
        public boolean isUpdateCount() {
            byte b = this.token;
            return (b == -3 || b == -1) && (this.status & 16) != 0;
        }

        /* access modifiers changed from: 0000 */
        public boolean isEndToken() {
            byte b = this.token;
            return b == -3 || b == -1 || b == -2;
        }

        /* access modifiers changed from: 0000 */
        public boolean isAuthToken() {
            return this.token == -19;
        }

        /* access modifiers changed from: 0000 */
        public boolean resultsPending() {
            return !isEndToken() || (this.status & 1) != 0;
        }

        /* access modifiers changed from: 0000 */
        public boolean isResultSet() {
            byte b = this.token;
            return b == -95 || b == -127 || b == -18 || b == 97 || b == -91 || b == -47 || b == -120 || b == -45;
        }

        public boolean isRowData() {
            byte b = this.token;
            return b == -47 || b == -45;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        tds8SpNames = hashMap;
        hashMap.put("sp_cursor", new Integer(1));
        tds8SpNames.put("sp_cursoropen", new Integer(2));
        tds8SpNames.put("sp_cursorprepare", new Integer(3));
        tds8SpNames.put("sp_cursorexecute", new Integer(4));
        tds8SpNames.put("sp_cursorprepexec", new Integer(5));
        tds8SpNames.put("sp_cursorunprepare", new Integer(6));
        tds8SpNames.put("sp_cursorfetch", new Integer(7));
        tds8SpNames.put("sp_cursoroption", new Integer(8));
        tds8SpNames.put("sp_cursorclose", new Integer(9));
        tds8SpNames.put("sp_executesql", new Integer(10));
        tds8SpNames.put("sp_prepare", new Integer(11));
        tds8SpNames.put("sp_execute", new Integer(12));
        tds8SpNames.put("sp_prepexec", new Integer(13));
        tds8SpNames.put("sp_prepexecrpc", new Integer(14));
        tds8SpNames.put("sp_unprepare", new Integer(15));
    }

    TdsCore(JtdsConnection jtdsConnection, SQLDiagnostic sQLDiagnostic) {
        this.connection = jtdsConnection;
        this.socket = jtdsConnection.getSocket();
        this.messages = sQLDiagnostic;
        this.serverType = jtdsConnection.getServerType();
        this.tdsVersion = this.socket.getTdsVersion();
        RequestStream requestStream = this.socket.getRequestStream(jtdsConnection.getNetPacketSize(), jtdsConnection.getMaxPrecision());
        this.out = requestStream;
        this.f122in = this.socket.getResponseStream(requestStream, jtdsConnection.getNetPacketSize());
    }

    private void checkOpen() throws SQLException {
        if (this.connection.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Connection"), "HY010");
        }
    }

    /* access modifiers changed from: 0000 */
    public int getTdsVersion() {
        return this.tdsVersion;
    }

    /* access modifiers changed from: 0000 */
    public ColInfo[] getColumns() {
        return this.columns;
    }

    /* access modifiers changed from: 0000 */
    public void setColumns(ColInfo[] colInfoArr) {
        this.columns = colInfoArr;
        this.rowData = new Object[colInfoArr.length];
        this.tables = null;
    }

    /* access modifiers changed from: 0000 */
    public ParamInfo[] getParameters() {
        if (this.currentToken.dynamParamInfo == null) {
            return EMPTY_PARAMETER_INFO;
        }
        int length = this.currentToken.dynamParamInfo.length;
        ParamInfo[] paramInfoArr = new ParamInfo[length];
        for (int i = 0; i < length; i++) {
            ColInfo colInfo = this.currentToken.dynamParamInfo[i];
            paramInfoArr[i] = new ParamInfo(colInfo, colInfo.realName, (Object) null, 0);
        }
        return paramInfoArr;
    }

    /* access modifiers changed from: 0000 */
    public Object[] getRowData() {
        return this.rowData;
    }

    /* access modifiers changed from: 0000 */
    public void negotiateSSL(String str, String str2) throws IOException, SQLException {
        if (!str2.equalsIgnoreCase("off")) {
            if (str2.equalsIgnoreCase(Ssl.SSL_REQUIRE) || str2.equalsIgnoreCase(Ssl.SSL_AUTHENTICATE)) {
                sendPreLoginPacket(str, true);
                int readPreLoginPacket = readPreLoginPacket();
                this.sslMode = readPreLoginPacket;
                if (!(readPreLoginPacket == 1 || readPreLoginPacket == 3)) {
                    throw new SQLException(Messages.get("error.ssl.encryptionoff"), "08S01");
                }
            } else {
                sendPreLoginPacket(str, false);
                this.sslMode = readPreLoginPacket();
            }
            if (this.sslMode != 2) {
                this.socket.enableEncryption(str2);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void login(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i) throws SQLException {
        try {
            String hostName2 = str9.length() == 0 ? getHostName() : str9;
            if (this.tdsVersion >= 3) {
                sendMSLoginPkt(str, str2, str3, str4, str5, str7, str8, hostName2, str10, str11, i);
            } else if (this.tdsVersion == 2) {
                send50LoginPkt(str, str3, str4, str6, str7, str8, hostName2, str10, i);
            } else {
                send42LoginPkt(str, str3, str4, str6, str7, str8, hostName2, str10, i);
            }
            if (this.sslMode == 0) {
                this.socket.disableEncryption();
            }
            nextToken();
            while (!this.endOfResponse) {
                if (this.currentToken.isAuthToken()) {
                    if (this._gssContext != null) {
                        sendGssToken();
                    } else {
                        sendNtlmChallengeResponse(str3, str4, str5);
                        nextToken();
                    }
                }
                String str12 = str3;
                String str13 = str4;
                String str14 = str5;
                nextToken();
            }
            this.messages.checkErrors();
        } catch (IOException e) {
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "08S01"), (Throwable) e);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean getMoreResults() throws SQLException {
        checkOpen();
        nextToken();
        while (!this.endOfResponse && !this.currentToken.isUpdateCount() && !this.currentToken.isResultSet()) {
            nextToken();
        }
        if (this.currentToken.isResultSet()) {
            byte b = this.currentToken.token;
            try {
                int peek = this.f122in.peek();
                while (true) {
                    byte b2 = (byte) peek;
                    if (b2 != -92 && b2 != -91 && b2 != -82) {
                        break;
                    }
                    nextToken();
                    peek = this.f122in.peek();
                }
                this.currentToken.token = b;
            } catch (IOException e) {
                this.connection.setClosed();
                throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "08S01"), (Throwable) e);
            }
        }
        return this.currentToken.isResultSet();
    }

    /* access modifiers changed from: 0000 */
    public boolean isResultSet() {
        return this.currentToken.isResultSet();
    }

    /* access modifiers changed from: 0000 */
    public boolean isRowData() {
        return this.currentToken.isRowData();
    }

    /* access modifiers changed from: 0000 */
    public boolean isUpdateCount() {
        return this.currentToken.isUpdateCount();
    }

    /* access modifiers changed from: 0000 */
    public int getUpdateCount() {
        if (this.currentToken.isEndToken()) {
            return this.currentToken.updateCount;
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public boolean isEndOfResponse() {
        return this.endOfResponse;
    }

    /* access modifiers changed from: 0000 */
    public void clearResponseQueue() throws SQLException {
        checkOpen();
        while (!this.endOfResponse) {
            nextToken();
        }
    }

    /* access modifiers changed from: 0000 */
    public void consumeOneResponse() throws SQLException {
        checkOpen();
        while (!this.endOfResponse) {
            nextToken();
            if (this.currentToken.isEndToken() && (this.currentToken.status & DONE_END_OF_RESPONSE) != 0) {
                return;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean getNextRow() throws SQLException {
        if (this.endOfResponse || this.endOfResults) {
            return false;
        }
        checkOpen();
        nextToken();
        while (!this.currentToken.isRowData() && !this.currentToken.isEndToken()) {
            nextToken();
        }
        if (this.endOfResults) {
            return false;
        }
        return this.currentToken.isRowData();
    }

    /* access modifiers changed from: 0000 */
    public boolean isDataInResultSet() throws SQLException {
        int i;
        byte b;
        checkOpen();
        try {
            if (this.endOfResponse) {
                b = TDS_DONE_TOKEN;
            } else {
                i = this.f122in.peek();
                b = (byte) i;
            }
            if (b != -47 || b == -45 || b == -3 || b == -1 || b == -2) {
                this.messages.checkErrors();
                return b != -47 || b == -45;
            }
            nextToken();
            i = this.f122in.peek();
            b = (byte) i;
            if (b != -47) {
            }
            this.messages.checkErrors();
            if (b != -47) {
            }
        } catch (IOException e) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "08S01"), (Throwable) e);
        }
    }

    /* access modifiers changed from: 0000 */
    public Integer getReturnStatus() {
        return this.returnStatus;
    }

    /* access modifiers changed from: 0000 */
    public synchronized void closeConnection() {
        try {
            if (this.tdsVersion == 2) {
                this.socket.setTimeout(1000);
                this.out.setPacketType(SYBQUERY_PKT);
                this.out.write((byte) TDS_CLOSE_TOKEN);
                this.out.write(0);
                this.out.flush();
                this.endOfResponse = false;
                clearResponseQueue();
            }
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public void close() throws SQLException {
        if (!this.isClosed) {
            try {
                clearResponseQueue();
                this.out.close();
                this.f122in.close();
            } finally {
                this.isClosed = true;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void cancel(boolean z) {
        Semaphore semaphore = null;
        try {
            semaphore = this.connection.getMutex();
            synchronized (this.cancelMonitor) {
                if (!this.cancelPending && !this.endOfResponse) {
                    this.cancelPending = this.socket.cancel(this.out.getVirtualSocket());
                }
                if (this.cancelPending) {
                    this.cancelMonitor[0] = z ? 1 : 0;
                    this.endOfResponse = false;
                }
            }
            if (semaphore != null) {
                semaphore.release();
            }
        } catch (Throwable th) {
            if (semaphore != null) {
                semaphore.release();
            }
            throw th;
        }
    }

    /* access modifiers changed from: 0000 */
    public void submitSQL(String str) throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
        if (str.length() != 0) {
            executeSQL(str, null, null, false, 0, -1, -1, true);
            clearResponseQueue();
            this.messages.checkErrors();
            return;
        }
        throw new IllegalArgumentException("submitSQL() called with empty SQL String");
    }

    /* access modifiers changed from: 0000 */
    public void startBatch() {
        this.inBatch = true;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00c6 A[Catch:{ IOException -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00f8 A[Catch:{ IOException -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0101 A[Catch:{ IOException -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0120 A[SYNTHETIC, Splitter:B:81:0x0120] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0156  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void executeSQL(java.lang.String r12, java.lang.String r13, net.sourceforge.jtds.jdbc.ParamInfo[] r14, boolean r15, int r16, int r17, int r18, boolean r19) throws java.sql.SQLException {
        /*
            r11 = this;
            r7 = r11
            r0 = r12
            r1 = r14
            monitor-enter(r11)
            r8 = 0
            r7._ErrorReceived = r8     // Catch:{ all -> 0x0162 }
            r9 = 1
            r10 = 0
            net.sourceforge.jtds.jdbc.Semaphore r2 = r7.connectionLock     // Catch:{ all -> 0x014d }
            if (r2 != 0) goto L_0x0015
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r7.connection     // Catch:{ all -> 0x014d }
            net.sourceforge.jtds.jdbc.Semaphore r2 = r2.getMutex()     // Catch:{ all -> 0x014d }
            r7.connectionLock = r2     // Catch:{ all -> 0x014d }
        L_0x0015:
            r11.clearResponseQueue()     // Catch:{ all -> 0x014d }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r7.messages     // Catch:{ all -> 0x014d }
            r2.exceptions = r10     // Catch:{ all -> 0x014d }
            r2 = r17
            r3 = r18
            r11.setRowCountAndTextSize(r2, r3)     // Catch:{ all -> 0x014d }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r7.messages     // Catch:{ all -> 0x014d }
            r2.clearWarnings()     // Catch:{ all -> 0x014d }
            r7.returnStatus = r10     // Catch:{ all -> 0x014d }
            if (r1 == 0) goto L_0x0030
            int r2 = r1.length     // Catch:{ all -> 0x014d }
            if (r2 != 0) goto L_0x0030
            r1 = r10
        L_0x0030:
            r7.parameters = r1     // Catch:{ all -> 0x014d }
            if (r13 == 0) goto L_0x003c
            int r2 = r13.length()     // Catch:{ all -> 0x014d }
            if (r2 != 0) goto L_0x003c
            r3 = r10
            goto L_0x003d
        L_0x003c:
            r3 = r13
        L_0x003d:
            if (r1 == 0) goto L_0x004c
            r2 = r1[r8]     // Catch:{ all -> 0x014d }
            boolean r2 = r2.isRetVal     // Catch:{ all -> 0x014d }
            if (r2 == 0) goto L_0x004c
            r2 = r1[r8]     // Catch:{ all -> 0x014d }
            r7.returnParam = r2     // Catch:{ all -> 0x014d }
            r7.nextParam = r8     // Catch:{ all -> 0x014d }
            goto L_0x0051
        L_0x004c:
            r7.returnParam = r10     // Catch:{ all -> 0x014d }
            r2 = -1
            r7.nextParam = r2     // Catch:{ all -> 0x014d }
        L_0x0051:
            if (r1 == 0) goto L_0x00c0
            if (r3 != 0) goto L_0x008d
            java.lang.String r2 = "EXECUTE "
            boolean r2 = r12.startsWith(r2)     // Catch:{ all -> 0x014d }
            if (r2 == 0) goto L_0x008d
            r2 = 0
        L_0x005e:
            int r4 = r1.length     // Catch:{ all -> 0x014d }
            if (r2 >= r4) goto L_0x0084
            r4 = r1[r2]     // Catch:{ all -> 0x014d }
            boolean r4 = r4.isRetVal     // Catch:{ all -> 0x014d }
            if (r4 != 0) goto L_0x0081
            r4 = r1[r2]     // Catch:{ all -> 0x014d }
            boolean r4 = r4.isOutput     // Catch:{ all -> 0x014d }
            if (r4 != 0) goto L_0x006e
            goto L_0x0081
        L_0x006e:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ all -> 0x014d }
            java.lang.String r1 = "error.prepare.nooutparam"
            int r2 = r2 + r9
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch:{ all -> 0x014d }
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r1, r2)     // Catch:{ all -> 0x014d }
            java.lang.String r2 = "07000"
            r0.<init>(r1, r2)     // Catch:{ all -> 0x014d }
            throw r0     // Catch:{ all -> 0x014d }
        L_0x0081:
            int r2 = r2 + 1
            goto L_0x005e
        L_0x0084:
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r7.connection     // Catch:{ all -> 0x014d }
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r12, r1, r2)     // Catch:{ all -> 0x014d }
            r2 = r0
            r4 = r10
            goto L_0x00c2
        L_0x008d:
            r2 = 0
        L_0x008e:
            int r4 = r1.length     // Catch:{ all -> 0x014d }
            if (r2 >= r4) goto L_0x00c0
            r4 = r1[r2]     // Catch:{ all -> 0x014d }
            boolean r4 = r4.isSet     // Catch:{ all -> 0x014d }
            if (r4 != 0) goto L_0x00b1
            r4 = r1[r2]     // Catch:{ all -> 0x014d }
            boolean r4 = r4.isOutput     // Catch:{ all -> 0x014d }
            if (r4 == 0) goto L_0x009e
            goto L_0x00b1
        L_0x009e:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ all -> 0x014d }
            java.lang.String r1 = "error.prepare.paramnotset"
            int r2 = r2 + r9
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch:{ all -> 0x014d }
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r1, r2)     // Catch:{ all -> 0x014d }
            java.lang.String r2 = "07000"
            r0.<init>(r1, r2)     // Catch:{ all -> 0x014d }
            throw r0     // Catch:{ all -> 0x014d }
        L_0x00b1:
            r4 = r1[r2]     // Catch:{ all -> 0x014d }
            r4.clearOutValue()     // Catch:{ all -> 0x014d }
            net.sourceforge.jtds.jdbc.JtdsConnection r4 = r7.connection     // Catch:{ all -> 0x014d }
            r5 = r1[r2]     // Catch:{ all -> 0x014d }
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r4, r5)     // Catch:{ all -> 0x014d }
            int r2 = r2 + 1
            goto L_0x008e
        L_0x00c0:
            r2 = r0
            r4 = r1
        L_0x00c2:
            int r0 = r7.tdsVersion     // Catch:{ IOException -> 0x0131 }
            if (r0 == r9) goto L_0x00f8
            r1 = 2
            if (r0 == r1) goto L_0x00f4
            r1 = 3
            if (r0 == r1) goto L_0x00ec
            r1 = 4
            if (r0 == r1) goto L_0x00ec
            r1 = 5
            if (r0 != r1) goto L_0x00d3
            goto L_0x00ec
        L_0x00d3:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ IOException -> 0x0131 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0131 }
            r1.<init>()     // Catch:{ IOException -> 0x0131 }
            java.lang.String r2 = "Unknown TDS version "
            r1.append(r2)     // Catch:{ IOException -> 0x0131 }
            int r2 = r7.tdsVersion     // Catch:{ IOException -> 0x0131 }
            r1.append(r2)     // Catch:{ IOException -> 0x0131 }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0131 }
            r0.<init>(r1)     // Catch:{ IOException -> 0x0131 }
            throw r0     // Catch:{ IOException -> 0x0131 }
        L_0x00ec:
            r1 = r11
            r5 = r15
            r6 = r19
            r1.executeSQL70(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x0131 }
            goto L_0x00ff
        L_0x00f4:
            r11.executeSQL50(r2, r3, r4)     // Catch:{ IOException -> 0x0131 }
            goto L_0x00ff
        L_0x00f8:
            r1 = r11
            r5 = r15
            r6 = r19
            r1.executeSQL42(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x0131 }
        L_0x00ff:
            if (r19 == 0) goto L_0x011d
            net.sourceforge.jtds.jdbc.RequestStream r0 = r7.out     // Catch:{ IOException -> 0x0131 }
            r0.flush()     // Catch:{ IOException -> 0x0131 }
            net.sourceforge.jtds.jdbc.Semaphore r0 = r7.connectionLock     // Catch:{ IOException -> 0x0131 }
            r0.release()     // Catch:{ IOException -> 0x0131 }
            r7.connectionLock = r10     // Catch:{ IOException -> 0x0131 }
            r7.endOfResponse = r8     // Catch:{ IOException -> 0x011a, all -> 0x0117 }
            r7.endOfResults = r9     // Catch:{ IOException -> 0x011a, all -> 0x0117 }
            r0 = r16
            r11.wait(r0)     // Catch:{ IOException -> 0x011a, all -> 0x0117 }
            goto L_0x011d
        L_0x0117:
            r0 = move-exception
            r9 = 0
            goto L_0x014e
        L_0x011a:
            r0 = move-exception
            r9 = 0
            goto L_0x0132
        L_0x011d:
            if (r19 != 0) goto L_0x0120
            goto L_0x012b
        L_0x0120:
            net.sourceforge.jtds.jdbc.Semaphore r0 = r7.connectionLock     // Catch:{ all -> 0x0162 }
            if (r0 == 0) goto L_0x012b
            net.sourceforge.jtds.jdbc.Semaphore r0 = r7.connectionLock     // Catch:{ all -> 0x0162 }
            r0.release()     // Catch:{ all -> 0x0162 }
            r7.connectionLock = r10     // Catch:{ all -> 0x0162 }
        L_0x012b:
            if (r19 == 0) goto L_0x012f
            r7.inBatch = r8     // Catch:{ all -> 0x0162 }
        L_0x012f:
            monitor-exit(r11)
            return
        L_0x0131:
            r0 = move-exception
        L_0x0132:
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r7.connection     // Catch:{ all -> 0x014d }
            r1.setClosed()     // Catch:{ all -> 0x014d }
            java.sql.SQLException r1 = new java.sql.SQLException     // Catch:{ all -> 0x014d }
            java.lang.String r2 = "error.generic.ioerror"
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x014d }
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2, r3)     // Catch:{ all -> 0x014d }
            java.lang.String r3 = "08S01"
            r1.<init>(r2, r3)     // Catch:{ all -> 0x014d }
            java.sql.SQLException r0 = net.sourceforge.jtds.jdbc.Support.linkException(r1, r0)     // Catch:{ all -> 0x014d }
            throw r0     // Catch:{ all -> 0x014d }
        L_0x014d:
            r0 = move-exception
        L_0x014e:
            if (r19 != 0) goto L_0x0152
            if (r9 == 0) goto L_0x015d
        L_0x0152:
            net.sourceforge.jtds.jdbc.Semaphore r1 = r7.connectionLock     // Catch:{ all -> 0x0162 }
            if (r1 == 0) goto L_0x015d
            net.sourceforge.jtds.jdbc.Semaphore r1 = r7.connectionLock     // Catch:{ all -> 0x0162 }
            r1.release()     // Catch:{ all -> 0x0162 }
            r7.connectionLock = r10     // Catch:{ all -> 0x0162 }
        L_0x015d:
            if (r19 == 0) goto L_0x0161
            r7.inBatch = r8     // Catch:{ all -> 0x0162 }
        L_0x0161:
            throw r0     // Catch:{ all -> 0x0162 }
        L_0x0162:
            r0 = move-exception
            monitor-exit(r11)
            goto L_0x0166
        L_0x0165:
            throw r0
        L_0x0166:
            goto L_0x0165
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.executeSQL(java.lang.String, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[], boolean, int, int, int, boolean):void");
    }

    /* access modifiers changed from: 0000 */
    public String microsoftPrepare(String str, ParamInfo[] paramInfoArr, boolean z, int i, int i2) throws SQLException {
        ParamInfo[] paramInfoArr2 = paramInfoArr;
        checkOpen();
        this.messages.clearWarnings();
        int prepareSql = this.connection.getPrepareSql();
        String str2 = "error.prepare.prepfailed";
        String str3 = "08S01";
        int i3 = 0;
        if (prepareSql == 1) {
            StringBuilder sb = new StringBuilder(str.length() + 32 + (paramInfoArr2.length * 15));
            String procName = this.connection.getProcName();
            sb.append("create proc ");
            sb.append(procName);
            sb.append(' ');
            while (i3 < paramInfoArr2.length) {
                sb.append("@P");
                sb.append(i3);
                sb.append(' ');
                sb.append(paramInfoArr2[i3].sqlType);
                i3++;
                if (i3 < paramInfoArr2.length) {
                    sb.append(',');
                }
            }
            sb.append(" as ");
            sb.append(Support.substituteParamMarkers(str, paramInfoArr));
            try {
                submitSQL(sb.toString());
                return procName;
            } catch (SQLException e) {
                if (!str3.equals(e.getSQLState())) {
                    this.messages.addWarning(Support.linkException(new SQLWarning(Messages.get(str2, (Object) e.getMessage()), e.getSQLState(), e.getErrorCode()), (Throwable) e));
                } else {
                    throw e;
                }
            }
        } else {
            if (prepareSql == 3) {
                ParamInfo[] paramInfoArr3 = new ParamInfo[(z ? 6 : 4)];
                paramInfoArr3[0] = new ParamInfo(4, null, 1);
                paramInfoArr3[1] = new ParamInfo(-1, Support.getParameterDefinitions(paramInfoArr), 4);
                paramInfoArr3[2] = new ParamInfo(-1, Support.substituteParamMarkers(str, paramInfoArr), 4);
                paramInfoArr3[3] = new ParamInfo(4, new Integer(1), 0);
                if (z) {
                    int cursorScrollOpt = MSCursorResultSet.getCursorScrollOpt(i, i2, true);
                    int cursorConcurrencyOpt = MSCursorResultSet.getCursorConcurrencyOpt(i2);
                    paramInfoArr3[4] = new ParamInfo(4, new Integer(cursorScrollOpt), 1);
                    paramInfoArr3[5] = new ParamInfo(4, new Integer(cursorConcurrencyOpt), 1);
                }
                this.columns = null;
                ParamInfo[] paramInfoArr4 = paramInfoArr3;
                try {
                    executeSQL(null, z ? "sp_cursorprepare" : "sp_prepare", paramInfoArr3, false, 0, -1, -1, true);
                    int i4 = 0;
                    while (!this.endOfResponse) {
                        nextToken();
                        if (isResultSet()) {
                            i4++;
                        }
                    }
                    if (i4 != 1) {
                        this.columns = null;
                    }
                    Integer num = (Integer) paramInfoArr4[0].getOutValue();
                    if (num != null) {
                        return num.toString();
                    }
                    this.messages.checkErrors();
                } catch (SQLException e2) {
                    if (!str3.equals(e2.getSQLState())) {
                        this.messages.addWarning(Support.linkException(new SQLWarning(Messages.get(str2, (Object) e2.getMessage()), e2.getSQLState(), e2.getErrorCode()), (Throwable) e2));
                    } else {
                        throw e2;
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: used method not loaded: net.sourceforge.jtds.jdbc.RequestStream.write(byte):null, types can be incorrect */
    /* JADX INFO: used method not loaded: net.sourceforge.jtds.jdbc.RequestStream.write(short):null, types can be incorrect */
    /* JADX INFO: used method not loaded: net.sourceforge.jtds.jdbc.RequestStream.write(byte[]):null, types can be incorrect */
    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0050, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r8 = r6.connection.getMutex();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r6.out.setPacketType(SYBQUERY_PKT);
        r6.out.write((byte) TDS5_DYNAMIC_TOKEN);
        r7 = net.sourceforge.jtds.jdbc.Support.encodeString(r6.connection.getCharset(), r7);
        r6.out.write((short) (r7.length + 41));
        r6.out.write(1);
        r6.out.write(0);
        r6.out.write(10);
        r6.out.writeAscii(r0.substring(1));
        r6.out.write((short) (r7.length + 26));
        r6.out.writeAscii("create proc ");
        r6.out.writeAscii(r0.substring(1));
        r6.out.writeAscii(" as ");
        r6.out.write(r7);
        r6.out.flush();
        r6.endOfResponse = false;
        clearResponseQueue();
        r6.messages.checkErrors();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00c7, code lost:
        if (r8 == null) goto L_0x00cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r8.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00cd, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ce, code lost:
        r7 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00d0, code lost:
        r7 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d1, code lost:
        r4 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00e3, code lost:
        if (r8 != null) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r8.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e9, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        throw r7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ea A[SYNTHETIC, Splitter:B:46:0x00ea] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String sybasePrepare(java.lang.String r7, net.sourceforge.jtds.jdbc.ParamInfo[] r8) throws java.sql.SQLException {
        /*
            r6 = this;
            monitor-enter(r6)
            r6.checkOpen()     // Catch:{ all -> 0x0120 }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r0 = r6.messages     // Catch:{ all -> 0x0120 }
            r0.clearWarnings()     // Catch:{ all -> 0x0120 }
            if (r7 == 0) goto L_0x0118
            int r0 = r7.length()     // Catch:{ all -> 0x0120 }
            if (r0 == 0) goto L_0x0118
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r6.connection     // Catch:{ all -> 0x0120 }
            java.lang.String r0 = r0.getProcName()     // Catch:{ all -> 0x0120 }
            if (r0 == 0) goto L_0x0110
            int r1 = r0.length()     // Catch:{ all -> 0x0120 }
            r2 = 11
            if (r1 != r2) goto L_0x0110
            r1 = 0
            r2 = 0
        L_0x0023:
            int r3 = r8.length     // Catch:{ all -> 0x0120 }
            r4 = 0
            if (r2 >= r3) goto L_0x0051
            java.lang.String r3 = "text"
            r5 = r8[r2]     // Catch:{ all -> 0x0120 }
            java.lang.String r5 = r5.sqlType     // Catch:{ all -> 0x0120 }
            boolean r3 = r3.equals(r5)     // Catch:{ all -> 0x0120 }
            if (r3 != 0) goto L_0x004f
            java.lang.String r3 = "unitext"
            r5 = r8[r2]     // Catch:{ all -> 0x0120 }
            java.lang.String r5 = r5.sqlType     // Catch:{ all -> 0x0120 }
            boolean r3 = r3.equals(r5)     // Catch:{ all -> 0x0120 }
            if (r3 != 0) goto L_0x004f
            java.lang.String r3 = "image"
            r5 = r8[r2]     // Catch:{ all -> 0x0120 }
            java.lang.String r5 = r5.sqlType     // Catch:{ all -> 0x0120 }
            boolean r3 = r3.equals(r5)     // Catch:{ all -> 0x0120 }
            if (r3 == 0) goto L_0x004c
            goto L_0x004f
        L_0x004c:
            int r2 = r2 + 1
            goto L_0x0023
        L_0x004f:
            monitor-exit(r6)
            return r4
        L_0x0051:
            net.sourceforge.jtds.jdbc.JtdsConnection r8 = r6.connection     // Catch:{ IOException -> 0x00ee, SQLException -> 0x00d5 }
            net.sourceforge.jtds.jdbc.Semaphore r8 = r8.getMutex()     // Catch:{ IOException -> 0x00ee, SQLException -> 0x00d5 }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r3 = 15
            r2.setPacketType(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r3 = -25
            r2.write(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r6.connection     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            java.lang.String r2 = r2.getCharset()     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            byte[] r7 = net.sourceforge.jtds.jdbc.Support.encodeString(r2, r7)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            int r3 = r7.length     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            int r3 = r3 + 41
            short r3 = (short) r3     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.write(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r3 = 1
            r2.write(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.write(r1)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r5 = 10
            r2.write(r5)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            java.lang.String r5 = r0.substring(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.writeAscii(r5)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            int r5 = r7.length     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            int r5 = r5 + 26
            short r5 = (short) r5     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.write(r5)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            java.lang.String r5 = "create proc "
            r2.writeAscii(r5)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            java.lang.String r3 = r0.substring(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.writeAscii(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            java.lang.String r3 = " as "
            r2.writeAscii(r3)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r2.write(r7)     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.RequestStream r7 = r6.out     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r7.flush()     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r6.endOfResponse = r1     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r6.clearResponseQueue()     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r7 = r6.messages     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            r7.checkErrors()     // Catch:{ IOException -> 0x00d0, SQLException -> 0x00ce }
            if (r8 == 0) goto L_0x00cc
            r8.release()     // Catch:{ all -> 0x0120 }
        L_0x00cc:
            monitor-exit(r6)
            return r0
        L_0x00ce:
            r7 = move-exception
            goto L_0x00d7
        L_0x00d0:
            r7 = move-exception
            r4 = r8
            goto L_0x00ef
        L_0x00d3:
            r7 = move-exception
            goto L_0x010a
        L_0x00d5:
            r7 = move-exception
            r8 = r4
        L_0x00d7:
            java.lang.String r0 = "08S01"
            java.lang.String r1 = r7.getSQLState()     // Catch:{ all -> 0x00eb }
            boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x00eb }
            if (r0 != 0) goto L_0x00ea
            if (r8 == 0) goto L_0x00e8
            r8.release()     // Catch:{ all -> 0x0120 }
        L_0x00e8:
            monitor-exit(r6)
            return r4
        L_0x00ea:
            throw r7     // Catch:{ all -> 0x00eb }
        L_0x00eb:
            r7 = move-exception
            r4 = r8
            goto L_0x010a
        L_0x00ee:
            r7 = move-exception
        L_0x00ef:
            net.sourceforge.jtds.jdbc.JtdsConnection r8 = r6.connection     // Catch:{ all -> 0x00d3 }
            r8.setClosed()     // Catch:{ all -> 0x00d3 }
            java.sql.SQLException r8 = new java.sql.SQLException     // Catch:{ all -> 0x00d3 }
            java.lang.String r0 = "error.generic.ioerror"
            java.lang.String r1 = r7.getMessage()     // Catch:{ all -> 0x00d3 }
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Messages.get(r0, r1)     // Catch:{ all -> 0x00d3 }
            java.lang.String r1 = "08S01"
            r8.<init>(r0, r1)     // Catch:{ all -> 0x00d3 }
            java.sql.SQLException r7 = net.sourceforge.jtds.jdbc.Support.linkException(r8, r7)     // Catch:{ all -> 0x00d3 }
            throw r7     // Catch:{ all -> 0x00d3 }
        L_0x010a:
            if (r4 == 0) goto L_0x010f
            r4.release()     // Catch:{ all -> 0x0120 }
        L_0x010f:
            throw r7     // Catch:{ all -> 0x0120 }
        L_0x0110:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0120 }
            java.lang.String r8 = "procName parameter must be 11 characters long."
            r7.<init>(r8)     // Catch:{ all -> 0x0120 }
            throw r7     // Catch:{ all -> 0x0120 }
        L_0x0118:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0120 }
            java.lang.String r8 = "sql parameter must be at least 1 character long."
            r7.<init>(r8)     // Catch:{ all -> 0x0120 }
            throw r7     // Catch:{ all -> 0x0120 }
        L_0x0120:
            r7 = move-exception
            monitor-exit(r6)
            goto L_0x0124
        L_0x0123:
            throw r7
        L_0x0124:
            goto L_0x0123
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.sybasePrepare(java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[]):java.lang.String");
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x005e, code lost:
        if (r0 != null) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0073, code lost:
        if (r0 != null) goto L_0x0060;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void sybaseUnPrepare(java.lang.String r5) throws java.sql.SQLException {
        /*
            r4 = this;
            monitor-enter(r4)
            r4.checkOpen()     // Catch:{ all -> 0x00a3 }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r0 = r4.messages     // Catch:{ all -> 0x00a3 }
            r0.clearWarnings()     // Catch:{ all -> 0x00a3 }
            if (r5 == 0) goto L_0x009b
            int r0 = r5.length()     // Catch:{ all -> 0x00a3 }
            r1 = 11
            if (r0 != r1) goto L_0x009b
            r0 = 0
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r4.connection     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.Semaphore r0 = r1.getMutex()     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r2 = 15
            r1.setPacketType(r2)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r3 = -25
            r1.write(r3)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r1.write(r2)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r2 = 4
            r1.write(r2)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r2 = 0
            r1.write(r2)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r3 = 10
            r1.write(r3)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r1 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r3 = 1
            java.lang.String r5 = r5.substring(r3)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r1.writeAscii(r5)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r5 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r5.write(r2)     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.RequestStream r5 = r4.out     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r5.flush()     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r4.endOfResponse = r2     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r4.clearResponseQueue()     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r5 = r4.messages     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            r5.checkErrors()     // Catch:{ IOException -> 0x0079, SQLException -> 0x0066 }
            if (r0 == 0) goto L_0x0076
        L_0x0060:
            r0.release()     // Catch:{ all -> 0x00a3 }
            goto L_0x0076
        L_0x0064:
            r5 = move-exception
            goto L_0x0095
        L_0x0066:
            r5 = move-exception
            java.lang.String r1 = "08S01"
            java.lang.String r2 = r5.getSQLState()     // Catch:{ all -> 0x0064 }
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x0064 }
            if (r1 != 0) goto L_0x0078
            if (r0 == 0) goto L_0x0076
            goto L_0x0060
        L_0x0076:
            monitor-exit(r4)
            return
        L_0x0078:
            throw r5     // Catch:{ all -> 0x0064 }
        L_0x0079:
            r5 = move-exception
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r4.connection     // Catch:{ all -> 0x0064 }
            r1.setClosed()     // Catch:{ all -> 0x0064 }
            java.sql.SQLException r1 = new java.sql.SQLException     // Catch:{ all -> 0x0064 }
            java.lang.String r2 = "error.generic.ioerror"
            java.lang.String r3 = r5.getMessage()     // Catch:{ all -> 0x0064 }
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2, r3)     // Catch:{ all -> 0x0064 }
            java.lang.String r3 = "08S01"
            r1.<init>(r2, r3)     // Catch:{ all -> 0x0064 }
            java.sql.SQLException r5 = net.sourceforge.jtds.jdbc.Support.linkException(r1, r5)     // Catch:{ all -> 0x0064 }
            throw r5     // Catch:{ all -> 0x0064 }
        L_0x0095:
            if (r0 == 0) goto L_0x009a
            r0.release()     // Catch:{ all -> 0x00a3 }
        L_0x009a:
            throw r5     // Catch:{ all -> 0x00a3 }
        L_0x009b:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a3 }
            java.lang.String r0 = "procName parameter must be 11 characters long."
            r5.<init>(r0)     // Catch:{ all -> 0x00a3 }
            throw r5     // Catch:{ all -> 0x00a3 }
        L_0x00a3:
            r5 = move-exception
            monitor-exit(r4)
            goto L_0x00a7
        L_0x00a6:
            throw r5
        L_0x00a7:
            goto L_0x00a6
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.sybaseUnPrepare(java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [net.sourceforge.jtds.jdbc.Semaphore] */
    /* JADX WARNING: type inference failed for: r0v2 */
    /* JADX WARNING: type inference failed for: r1v3, types: [net.sourceforge.jtds.jdbc.Semaphore] */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v5, types: [byte[]] */
    /* JADX WARNING: type inference failed for: r0v7, types: [byte[]] */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r0v9 */
    /* JADX WARNING: type inference failed for: r0v10 */
    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v2
      assigns: []
      uses: []
      mth insns count: 74
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0092 A[SYNTHETIC, Splitter:B:39:0x0092] */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized byte[] enlistConnection(int r6, byte[] r7) throws java.sql.SQLException {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r5.connection     // Catch:{ IOException -> 0x0074 }
            net.sourceforge.jtds.jdbc.Semaphore r1 = r1.getMutex()     // Catch:{ IOException -> 0x0074 }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r3 = 14
            r2.setPacketType(r3)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            net.sourceforge.jtds.jdbc.RequestStream r2 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            short r3 = (short) r6     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r2.write(r3)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r2 = 1
            r3 = 0
            if (r6 == 0) goto L_0x0031
            if (r6 == r2) goto L_0x001c
            goto L_0x0036
        L_0x001c:
            if (r7 == 0) goto L_0x002b
            net.sourceforge.jtds.jdbc.RequestStream r6 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            int r4 = r7.length     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            short r4 = (short) r4     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r6.write(r4)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            net.sourceforge.jtds.jdbc.RequestStream r6 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r6.write(r7)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            goto L_0x0036
        L_0x002b:
            net.sourceforge.jtds.jdbc.RequestStream r6 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r6.write(r3)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            goto L_0x0036
        L_0x0031:
            net.sourceforge.jtds.jdbc.RequestStream r6 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r6.write(r3)     // Catch:{ IOException -> 0x006f, all -> 0x006c }
        L_0x0036:
            net.sourceforge.jtds.jdbc.RequestStream r6 = r5.out     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r6.flush()     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r5.endOfResponse = r3     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            r5.endOfResults = r2     // Catch:{ IOException -> 0x006f, all -> 0x006c }
            if (r1 == 0) goto L_0x0044
            r1.release()     // Catch:{ all -> 0x0096 }
        L_0x0044:
            boolean r6 = r5.getMoreResults()     // Catch:{ all -> 0x0096 }
            if (r6 == 0) goto L_0x0062
            boolean r6 = r5.getNextRow()     // Catch:{ all -> 0x0096 }
            if (r6 == 0) goto L_0x0062
            java.lang.Object[] r6 = r5.rowData     // Catch:{ all -> 0x0096 }
            int r6 = r6.length     // Catch:{ all -> 0x0096 }
            if (r6 != r2) goto L_0x0062
            java.lang.Object[] r6 = r5.rowData     // Catch:{ all -> 0x0096 }
            r6 = r6[r3]     // Catch:{ all -> 0x0096 }
            boolean r7 = r6 instanceof byte[]     // Catch:{ all -> 0x0096 }
            if (r7 == 0) goto L_0x0062
            byte[] r6 = (byte[]) r6     // Catch:{ all -> 0x0096 }
            r0 = r6
            byte[] r0 = (byte[]) r0     // Catch:{ all -> 0x0096 }
        L_0x0062:
            r5.clearResponseQueue()     // Catch:{ all -> 0x0096 }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r6 = r5.messages     // Catch:{ all -> 0x0096 }
            r6.checkErrors()     // Catch:{ all -> 0x0096 }
            monitor-exit(r5)
            return r0
        L_0x006c:
            r6 = move-exception
            r0 = r1
            goto L_0x0090
        L_0x006f:
            r6 = move-exception
            r0 = r1
            goto L_0x0075
        L_0x0072:
            r6 = move-exception
            goto L_0x0090
        L_0x0074:
            r6 = move-exception
        L_0x0075:
            net.sourceforge.jtds.jdbc.JtdsConnection r7 = r5.connection     // Catch:{ all -> 0x0072 }
            r7.setClosed()     // Catch:{ all -> 0x0072 }
            java.sql.SQLException r7 = new java.sql.SQLException     // Catch:{ all -> 0x0072 }
            java.lang.String r1 = "error.generic.ioerror"
            java.lang.String r2 = r6.getMessage()     // Catch:{ all -> 0x0072 }
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r1, r2)     // Catch:{ all -> 0x0072 }
            java.lang.String r2 = "08S01"
            r7.<init>(r1, r2)     // Catch:{ all -> 0x0072 }
            java.sql.SQLException r6 = net.sourceforge.jtds.jdbc.Support.linkException(r7, r6)     // Catch:{ all -> 0x0072 }
            throw r6     // Catch:{ all -> 0x0072 }
        L_0x0090:
            if (r0 == 0) goto L_0x0095
            r0.release()     // Catch:{ all -> 0x0096 }
        L_0x0095:
            throw r6     // Catch:{ all -> 0x0096 }
        L_0x0096:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.enlistConnection(int, byte[]):byte[]");
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Region traversal failed: Recursive call in traverseIterativeStepInternal method
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:29)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
        */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00d1 A[SYNTHETIC, Splitter:B:69:0x00d1] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x00df A[EDGE_INSN: B:97:0x00df->B:75:0x00df ?: BREAK  
    EDGE_INSN: B:97:0x00df->B:75:0x00df ?: BREAK  
    EDGE_INSN: B:97:0x00df->B:75:0x00df ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x00df A[EDGE_INSN: B:97:0x00df->B:75:0x00df ?: BREAK  
    EDGE_INSN: B:97:0x00df->B:75:0x00df ?: BREAK  , SYNTHETIC] */
    java.sql.SQLException getBatchCounts(java.util.ArrayList r4, java.sql.SQLException r5) throws java.sql.SQLException {
        /*
            r3 = this;
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.SUCCESS_NO_INFO
            r3.checkOpen()     // Catch:{ SQLException -> 0x00b0 }
        L_0x0005:
            boolean r1 = r3.endOfResponse     // Catch:{ SQLException -> 0x00b0 }
            if (r1 != 0) goto L_0x0095     // Catch:{ SQLException -> 0x00b0 }
            r3.nextToken()     // Catch:{ SQLException -> 0x00b0 }
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            boolean r1 = r1.isResultSet()     // Catch:{ SQLException -> 0x00b0 }
            if (r1 != 0) goto L_0x0087     // Catch:{ SQLException -> 0x00b0 }
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            byte r1 = r1.token     // Catch:{ SQLException -> 0x00b0 }
            r2 = -3     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == r2) goto L_0x0058     // Catch:{ SQLException -> 0x00b0 }
            r2 = -2     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == r2) goto L_0x003f     // Catch:{ SQLException -> 0x00b0 }
            r2 = -1     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == r2) goto L_0x0022     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
        L_0x0022:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            byte r1 = r1.status     // Catch:{ SQLException -> 0x00b0 }
            r1 = r1 & 2     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == 0) goto L_0x002d     // Catch:{ SQLException -> 0x00b0 }
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.EXECUTE_FAILED     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
        L_0x002d:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            boolean r1 = r1.isUpdateCount()     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == 0) goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ SQLException -> 0x00b0 }
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            int r1 = r1.updateCount     // Catch:{ SQLException -> 0x00b0 }
            r0.<init>(r1)     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
        L_0x003f:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            byte r1 = r1.status     // Catch:{ SQLException -> 0x00b0 }
            r1 = r1 & 2     // Catch:{ SQLException -> 0x00b0 }
            if (r1 != 0) goto L_0x0050     // Catch:{ SQLException -> 0x00b0 }
            java.lang.Integer r1 = net.sourceforge.jtds.jdbc.JtdsStatement.EXECUTE_FAILED     // Catch:{ SQLException -> 0x00b0 }
            if (r0 != r1) goto L_0x004c     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0050     // Catch:{ SQLException -> 0x00b0 }
        L_0x004c:
            r4.add(r0)     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0055     // Catch:{ SQLException -> 0x00b0 }
        L_0x0050:
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.EXECUTE_FAILED     // Catch:{ SQLException -> 0x00b0 }
            r4.add(r0)     // Catch:{ SQLException -> 0x00b0 }
        L_0x0055:
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.SUCCESS_NO_INFO     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
        L_0x0058:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            byte r1 = r1.status     // Catch:{ SQLException -> 0x00b0 }
            r1 = r1 & 2     // Catch:{ SQLException -> 0x00b0 }
            if (r1 != 0) goto L_0x007e     // Catch:{ SQLException -> 0x00b0 }
            java.lang.Integer r1 = net.sourceforge.jtds.jdbc.JtdsStatement.EXECUTE_FAILED     // Catch:{ SQLException -> 0x00b0 }
            if (r0 != r1) goto L_0x0065     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x007e     // Catch:{ SQLException -> 0x00b0 }
        L_0x0065:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            boolean r1 = r1.isUpdateCount()     // Catch:{ SQLException -> 0x00b0 }
            if (r1 == 0) goto L_0x007a     // Catch:{ SQLException -> 0x00b0 }
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ SQLException -> 0x00b0 }
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r1 = r3.currentToken     // Catch:{ SQLException -> 0x00b0 }
            int r1 = r1.updateCount     // Catch:{ SQLException -> 0x00b0 }
            r0.<init>(r1)     // Catch:{ SQLException -> 0x00b0 }
            r4.add(r0)     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0083     // Catch:{ SQLException -> 0x00b0 }
        L_0x007a:
            r4.add(r0)     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0083     // Catch:{ SQLException -> 0x00b0 }
        L_0x007e:
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.EXECUTE_FAILED     // Catch:{ SQLException -> 0x00b0 }
            r4.add(r0)     // Catch:{ SQLException -> 0x00b0 }
        L_0x0083:
            java.lang.Integer r0 = net.sourceforge.jtds.jdbc.JtdsStatement.SUCCESS_NO_INFO     // Catch:{ SQLException -> 0x00b0 }
            goto L_0x0005     // Catch:{ SQLException -> 0x00b0 }
        L_0x0087:
            java.sql.SQLException r4 = new java.sql.SQLException     // Catch:{ SQLException -> 0x00b0 }
            java.lang.String r0 = "error.statement.batchnocount"     // Catch:{ SQLException -> 0x00b0 }
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Messages.get(r0)     // Catch:{ SQLException -> 0x00b0 }
            java.lang.String r1 = "07000"     // Catch:{ SQLException -> 0x00b0 }
            r4.<init>(r0, r1)     // Catch:{ SQLException -> 0x00b0 }
            throw r4     // Catch:{ SQLException -> 0x00b0 }
        L_0x0095:
            net.sourceforge.jtds.jdbc.SQLDiagnostic r4 = r3.messages     // Catch:{ SQLException -> 0x00b0 }
            r4.checkErrors()     // Catch:{ SQLException -> 0x00b0 }
        L_0x009a:
            boolean r4 = r3.endOfResponse
            if (r4 != 0) goto L_0x00df
            r3.nextToken()     // Catch:{ SQLException -> 0x00a2 }
            goto L_0x009a
        L_0x00a2:
            r4 = move-exception
            r3.checkOpen()
            if (r5 == 0) goto L_0x00ac
            r5.setNextException(r4)
            goto L_0x009a
        L_0x00ac:
            r5 = r4
            goto L_0x009a
        L_0x00ae:
            r4 = move-exception
            goto L_0x00b7
        L_0x00b0:
            r4 = move-exception
            if (r5 == 0) goto L_0x00cc
            r5.setNextException(r4)     // Catch:{ all -> 0x00ae }
            goto L_0x00cd
        L_0x00b7:
            boolean r0 = r3.endOfResponse
            if (r0 != 0) goto L_0x00cb
            r3.nextToken()     // Catch:{ SQLException -> 0x00bf }
            goto L_0x00b7
        L_0x00bf:
            r0 = move-exception
            r3.checkOpen()
            if (r5 == 0) goto L_0x00c9
            r5.setNextException(r0)
            goto L_0x00b7
        L_0x00c9:
            r5 = r0
            goto L_0x00b7
        L_0x00cb:
            throw r4
        L_0x00cc:
            r5 = r4
        L_0x00cd:
            boolean r4 = r3.endOfResponse
            if (r4 != 0) goto L_0x00df
            r3.nextToken()     // Catch:{ SQLException -> 0x00d5 }
            goto L_0x00cd
        L_0x00d5:
            r4 = move-exception
            r3.checkOpen()
            if (r5 == 0) goto L_0x00cc
            r5.setNextException(r4)
            goto L_0x00cd
        L_0x00df:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.getBatchCounts(java.util.ArrayList, java.sql.SQLException):java.sql.SQLException");
    }

    /* access modifiers changed from: 0000 */
    public ColInfo[] getComputedColumns() {
        return this.computedColumns;
    }

    /* access modifiers changed from: 0000 */
    public Object[] getComputedRowData() {
        try {
            return this.computedRowData;
        } finally {
            this.computedRowData = null;
        }
    }

    private void putLoginString(String str, int i) throws IOException {
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str);
        this.out.write(encodeString, 0, i);
        RequestStream requestStream = this.out;
        if (encodeString.length < i) {
            i = encodeString.length;
        }
        requestStream.write((byte) i);
    }

    private void sendPreLoginPacket(String str, boolean z) throws IOException {
        this.out.setPacketType(PRELOGIN_PKT);
        this.out.write(0);
        this.out.write(21);
        this.out.write(6);
        this.out.write(1);
        this.out.write(27);
        this.out.write(1);
        this.out.write(2);
        this.out.write(28);
        this.out.write((byte) (str.length() + 1));
        this.out.write(3);
        this.out.write((short) (str.length() + 28 + 1));
        this.out.write(4);
        this.out.write((byte) TDS_DONEINPROC_TOKEN);
        this.out.write(new byte[]{8, 0, 1, 85, 0, 0});
        this.out.write(z ? (byte) 1 : 0);
        this.out.writeAscii(str);
        this.out.write(0);
        this.out.write(new byte[]{1, 2, 0, 0});
        this.out.flush();
    }

    private int readPreLoginPacket() throws IOException {
        byte[][] bArr = new byte[8][];
        byte[][] bArr2 = new byte[8][];
        byte[] bArr3 = new byte[5];
        bArr3[0] = (byte) this.f122in.read();
        int i = 0;
        while ((bArr3[0] & TDS_DONEINPROC_TOKEN) != 255) {
            if (i != 8) {
                this.f122in.read(bArr3, 1, 4);
                int i2 = i + 1;
                bArr[i] = bArr3;
                bArr3 = new byte[5];
                bArr3[0] = (byte) this.f122in.read();
                i = i2;
            } else {
                throw new IOException("Pre Login packet has more than 8 entries");
            }
        }
        for (int i3 = 0; i3 < i; i3++) {
            byte[] bArr4 = new byte[bArr[i3][4]];
            this.f122in.read(bArr4);
            bArr2[i3] = bArr4;
        }
        if (Logger.isActive()) {
            Logger.println("PreLogin server response");
            for (int i4 = 0; i4 < i; i4++) {
                StringBuilder sb = new StringBuilder();
                sb.append("Record ");
                sb.append(i4);
                sb.append(" = ");
                sb.append(Support.toHex(bArr2[i4]));
                Logger.println(sb.toString());
            }
        }
        if (i > 1) {
            return bArr2[1][0];
        }
        return 2;
    }

    private void send42LoginPkt(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i) throws IOException {
        String str9 = str3;
        byte[] bArr = new byte[0];
        this.out.setPacketType(2);
        putLoginString(str7, 30);
        String str10 = str2;
        putLoginString(str2, 30);
        putLoginString(str3, 30);
        putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write(3);
        this.out.write(1);
        this.out.write(6);
        this.out.write(10);
        this.out.write(9);
        this.out.write(1);
        this.out.write(1);
        this.out.write(0);
        this.out.write(0);
        this.out.write(bArr, 0, 7);
        putLoginString(str5, 30);
        String str11 = str;
        putLoginString(str, 30);
        this.out.write(0);
        this.out.write((byte) str3.length());
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str3);
        this.out.write(encodeString, 0, 253);
        this.out.write((byte) (encodeString.length + 2));
        this.out.write(4);
        this.out.write(2);
        this.out.write(0);
        this.out.write(0);
        putLoginString(str6, 10);
        this.out.write(6);
        this.out.write(0);
        this.out.write(0);
        this.out.write(0);
        this.out.write(0);
        this.out.write(13);
        this.out.write((byte) NTLMAUTH_PKT);
        putLoginString(str8, 30);
        this.out.write(1);
        this.out.write(0);
        this.out.write(0);
        this.out.write(bArr, 0, 8);
        this.out.write(0);
        String str12 = str4;
        putLoginString(str4, 30);
        this.out.write(1);
        putLoginString(String.valueOf(i), 6);
        this.out.write(bArr, 0, 8);
        this.out.flush();
        this.endOfResponse = false;
    }

    private void send50LoginPkt(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i) throws IOException {
        String str9 = str3;
        byte[] bArr = new byte[0];
        this.out.setPacketType(2);
        putLoginString(str7, 30);
        String str10 = str2;
        putLoginString(str2, 30);
        putLoginString(str3, 30);
        putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write(3);
        this.out.write(1);
        this.out.write(6);
        this.out.write(10);
        this.out.write(9);
        this.out.write(1);
        this.out.write(1);
        this.out.write(0);
        this.out.write(0);
        this.out.write(bArr, 0, 7);
        putLoginString(str5, 30);
        String str11 = str;
        putLoginString(str, 30);
        this.out.write(0);
        this.out.write((byte) str3.length());
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str3);
        this.out.write(encodeString, 0, 253);
        this.out.write((byte) (encodeString.length + 2));
        this.out.write((byte) TDS_ENV_LCID);
        this.out.write(0);
        this.out.write(0);
        this.out.write(0);
        putLoginString(str6, 10);
        this.out.write((byte) TDS_ENV_LCID);
        this.out.write(0);
        this.out.write(0);
        this.out.write(0);
        this.out.write(0);
        this.out.write(13);
        this.out.write((byte) NTLMAUTH_PKT);
        putLoginString(str8, 30);
        this.out.write(1);
        this.out.write(0);
        this.out.write(0);
        this.out.write(bArr, 0, 8);
        this.out.write(0);
        String str12 = str4;
        putLoginString(str4, 30);
        this.out.write(1);
        if (i > 0) {
            putLoginString(String.valueOf(i), 6);
        } else {
            putLoginString(String.valueOf(512), 6);
        }
        this.out.write(bArr, 0, 4);
        byte[] bArr2 = {1, 11, 79, TDS_DONEINPROC_TOKEN, -123, TDS_RESULT_TOKEN, -17, 101, Byte.MAX_VALUE, TDS_DONEINPROC_TOKEN, TDS_DONEINPROC_TOKEN, TDS_DONEINPROC_TOKEN, -42, 2, 10, 0, 2, 4, 6, DONE_END_OF_RESPONSE, 6, 72, 0, 0, 12};
        if (i == 0) {
            bArr2[17] = 0;
        }
        this.out.write((byte) TDS_CAP_TOKEN);
        this.out.write((short) 25);
        this.out.write(bArr2);
        this.out.flush();
        this.endOfResponse = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0069 A[SYNTHETIC, Splitter:B:23:0x0069] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0269  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMSLoginPkt(java.lang.String r15, java.lang.String r16, java.lang.String r17, java.lang.String r18, java.lang.String r19, java.lang.String r20, java.lang.String r21, java.lang.String r22, java.lang.String r23, java.lang.String r24, int r25) throws java.io.IOException, java.sql.SQLException {
        /*
            r14 = this;
            r1 = r14
            r0 = r17
            r2 = r19
            r3 = 0
            r1.ntlmMessage = r3
            r3 = 0
            byte[] r4 = new byte[r3]
            net.sourceforge.jtds.jdbc.JtdsConnection r5 = r1.connection
            boolean r5 = r5.getUseKerberos()
            r6 = 1
            if (r5 != 0) goto L_0x0028
            if (r0 == 0) goto L_0x0028
            int r7 = r17.length()
            if (r7 != 0) goto L_0x001d
            goto L_0x0028
        L_0x001d:
            if (r2 == 0) goto L_0x0026
            int r7 = r19.length()
            if (r7 <= 0) goto L_0x0026
            goto L_0x002a
        L_0x0026:
            r7 = 0
            goto L_0x002b
        L_0x0028:
            r1.ntlmAuthSSO = r6
        L_0x002a:
            r7 = 1
        L_0x002b:
            boolean r8 = r1.ntlmAuthSSO
            if (r8 == 0) goto L_0x0065
            boolean r8 = net.sourceforge.jtds.jdbc.Support.isWindowsOS()
            if (r8 == 0) goto L_0x0065
            if (r5 != 0) goto L_0x0065
            net.sourceforge.jtds.util.SSPIJNIClient r5 = net.sourceforge.jtds.util.SSPIJNIClient.getInstance()     // Catch:{ Exception -> 0x0049 }
            sspiJNIClient = r5     // Catch:{ Exception -> 0x0049 }
            byte[] r5 = r5.invokePrepareSSORequest()     // Catch:{ Exception -> 0x0049 }
            r1.ntlmMessage = r5     // Catch:{ Exception -> 0x0049 }
            java.lang.String r5 = "Using native SSO library for Windows Authentication."
            net.sourceforge.jtds.util.Logger.println(r5)     // Catch:{ Exception -> 0x0049 }
            goto L_0x0091
        L_0x0049:
            r0 = move-exception
            java.io.IOException r2 = new java.io.IOException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "SSO Failed: "
            r3.append(r4)
            java.lang.String r0 = r0.getMessage()
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r2.<init>(r0)
            throw r2
        L_0x0065:
            boolean r5 = r1.ntlmAuthSSO
            if (r5 == 0) goto L_0x0091
            byte[] r5 = r14.createGssToken()     // Catch:{ GSSException -> 0x0075 }
            r1.ntlmMessage = r5     // Catch:{ GSSException -> 0x0075 }
            java.lang.String r5 = "Using Kerberos GSS authentication."
            net.sourceforge.jtds.util.Logger.println(r5)     // Catch:{ GSSException -> 0x0075 }
            goto L_0x0091
        L_0x0075:
            r0 = move-exception
            java.io.IOException r2 = new java.io.IOException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "GSS Failed: "
            r3.append(r4)
            java.lang.String r0 = r0.getMessage()
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r2.<init>(r0)
            throw r2
        L_0x0091:
            int r5 = r22.length()
            int r8 = r20.length()
            int r5 = r5 + r8
            int r8 = r15.length()
            int r5 = r5 + r8
            int r8 = r21.length()
            int r5 = r5 + r8
            int r8 = r16.length()
            int r5 = r5 + r8
            int r8 = r23.length()
            int r5 = r5 + r8
            int r5 = r5 * 2
            r8 = 86
            int r5 = r5 + r8
            short r5 = (short) r5
            r9 = 32
            if (r7 == 0) goto L_0x00cb
            boolean r10 = r1.ntlmAuthSSO
            if (r10 == 0) goto L_0x00c2
            byte[] r10 = r1.ntlmMessage
            if (r10 == 0) goto L_0x00c2
            int r10 = r10.length
            goto L_0x00c7
        L_0x00c2:
            int r10 = r19.length()
            int r10 = r10 + r9
        L_0x00c7:
            short r10 = (short) r10
            int r5 = r5 + r10
            short r5 = (short) r5
            goto L_0x00d9
        L_0x00cb:
            int r10 = r17.length()
            int r11 = r18.length()
            int r10 = r10 + r11
            int r10 = r10 * 2
            int r5 = r5 + r10
            short r5 = (short) r5
            r10 = 0
        L_0x00d9:
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r12 = 16
            r11.setPacketType(r12)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r5)
            int r11 = r1.tdsVersion
            r12 = 3
            if (r11 != r12) goto L_0x00f2
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r13 = 1879048192(0x70000000, float:1.58456325E29)
            r11.write(r13)
            goto L_0x00fa
        L_0x00f2:
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r13 = 1895825409(0x71000001, float:6.338254E29)
            r11.write(r13)
        L_0x00fa:
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r13 = r25
            r11.write(r13)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r13 = 7
            r11.write(r13)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            net.sourceforge.jtds.jdbc.JtdsConnection r13 = r1.connection
            int r13 = r13.getProcessId()
            r11.write(r13)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r3)
            r11 = -32
            net.sourceforge.jtds.jdbc.RequestStream r13 = r1.out
            r13.write(r11)
            if (r7 == 0) goto L_0x0123
            r11 = 131(0x83, float:1.84E-43)
            byte r12 = (byte) r11
        L_0x0123:
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r12)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r12 = 4
            r11.write(r4, r3, r12)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r1.out
            r11.write(r4, r3, r12)
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            r4.write(r8)
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            int r11 = r22.length()
            short r11 = (short) r11
            r4.write(r11)
            int r4 = r22.length()
            int r4 = r4 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            if (r7 != 0) goto L_0x0185
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r17.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r17.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r18.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r18.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            goto L_0x0199
        L_0x0185:
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r3)
        L_0x0199:
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r20.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r20.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r15.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r15.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r21.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r21.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r23.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r23.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            int r11 = r16.length()
            short r11 = (short) r11
            r8.write(r11)
            int r8 = r16.length()
            int r8 = r8 * 2
            int r4 = r4 + r8
            short r4 = (short) r4
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            byte[] r11 = getMACAddress(r24)
            r8.write(r11)
            net.sourceforge.jtds.jdbc.RequestStream r8 = r1.out
            r8.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            r4.write(r10)
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            r4.write(r5)
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            r5 = r22
            r4.write(r5)
            if (r7 != 0) goto L_0x0245
            java.lang.String r4 = tds7CryptPass(r18)
            net.sourceforge.jtds.jdbc.RequestStream r5 = r1.out
            r5.write(r0)
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r0.write(r4)
        L_0x0245:
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r4 = r20
            r0.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r4 = r15
            r0.write(r15)
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r4 = r21
            r0.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r4 = r23
            r0.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r4 = r16
            r0.write(r4)
            if (r7 == 0) goto L_0x02cc
            boolean r0 = r1.ntlmAuthSSO
            if (r0 == 0) goto L_0x0275
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            byte[] r2 = r1.ntlmMessage
            r0.write(r2)
            goto L_0x02cc
        L_0x0275:
            java.lang.String r0 = "UTF8"
            byte[] r0 = r2.getBytes(r0)
            r2 = 8
            byte[] r2 = new byte[r2]
            r2 = {78, 84, 76, 77, 83, 83, 80, 0} // fill-array
            net.sourceforge.jtds.jdbc.RequestStream r4 = r1.out
            r4.write(r2)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r6)
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r1.connection
            boolean r2 = r2.getUseNTLMv2()
            if (r2 == 0) goto L_0x029d
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r4 = 569861(0x8b205, float:7.98545E-40)
            r2.write(r4)
            goto L_0x02a5
        L_0x029d:
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r4 = 45569(0xb201, float:6.3856E-41)
            r2.write(r4)
        L_0x02a5:
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            int r4 = r0.length
            short r4 = (short) r4
            r2.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            int r4 = r0.length
            short r4 = (short) r4
            r2.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r9)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r9)
            net.sourceforge.jtds.jdbc.RequestStream r2 = r1.out
            r2.write(r0)
        L_0x02cc:
            net.sourceforge.jtds.jdbc.RequestStream r0 = r1.out
            r0.flush()
            r1.endOfResponse = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.sendMSLoginPkt(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int):void");
    }

    private void tdsGssToken() throws IOException {
        byte[] bArr = new byte[this.f122in.readShort()];
        this.ntlmMessage = bArr;
        this.f122in.read(bArr);
        StringBuilder sb = new StringBuilder();
        sb.append("GSS: Received token (length: ");
        sb.append(this.ntlmMessage.length);
        sb.append(")");
        Logger.println(sb.toString());
    }

    private void sendGssToken() throws IOException {
        try {
            byte[] initSecContext = this._gssContext.initSecContext(this.ntlmMessage, 0, this.ntlmMessage.length);
            if (this._gssContext.isEstablished()) {
                Logger.println("GSS: Security context established.");
            }
            if (initSecContext != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("GSS: Sending token (length: ");
                sb.append(this.ntlmMessage.length);
                sb.append(")");
                Logger.println(sb.toString());
                this.out.setPacketType(NTLMAUTH_PKT);
                this.out.write(initSecContext);
                this.out.flush();
            }
        } catch (GSSException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("GSS failure: ");
            sb2.append(e.getMessage());
            throw new IOException(sb2.toString());
        }
    }

    private void sendNtlmChallengeResponse(String str, String str2, String str3) throws IOException {
        byte[] bArr;
        byte[] bArr2;
        this.out.setPacketType(NTLMAUTH_PKT);
        if (this.ntlmAuthSSO) {
            try {
                byte[] invokePrepareSSOSubmit = sspiJNIClient.invokePrepareSSOSubmit(this.ntlmMessage);
                this.ntlmMessage = invokePrepareSSOSubmit;
                this.out.write(invokePrepareSSOSubmit);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("SSO Failed: ");
                sb.append(e.getMessage());
                throw new IOException(sb.toString());
            }
        } else {
            if (this.connection.getUseNTLMv2()) {
                byte[] bArr3 = new byte[8];
                new Random().nextBytes(bArr3);
                bArr2 = NtlmAuth.answerLmv2Challenge(str3, str, str2, this.nonce, bArr3);
                bArr = NtlmAuth.answerNtlmv2Challenge(str3, str, str2, this.nonce, this.ntlmTarget, bArr3);
            } else {
                bArr2 = NtlmAuth.answerLmChallenge(str2, this.nonce);
                bArr = NtlmAuth.answerNtChallenge(str2, this.nonce);
            }
            this.out.write(new byte[]{78, 84, 76, 77, 83, 83, 80, 0});
            this.out.write(3);
            int length = str3.length() * 2;
            int length2 = str.length() * 2;
            int i = length + 64;
            int i2 = i + length2;
            int i3 = i2 + 0;
            this.out.write((short) bArr2.length);
            this.out.write((short) bArr2.length);
            this.out.write(i3);
            int length3 = bArr2.length + i3;
            this.out.write((short) bArr.length);
            this.out.write((short) bArr.length);
            this.out.write(length3);
            short s = (short) length;
            this.out.write(s);
            this.out.write(s);
            this.out.write(64);
            short s2 = (short) length2;
            this.out.write(s2);
            this.out.write(s2);
            this.out.write(i);
            this.out.write(0);
            this.out.write(0);
            this.out.write(i2);
            this.out.write(0);
            this.out.write(0);
            this.out.write(i3);
            if (this.connection.getUseNTLMv2()) {
                this.out.write(557569);
            } else {
                this.out.write(33281);
            }
            this.out.write(str3);
            this.out.write(str);
            this.out.write(bArr2);
            this.out.write(bArr);
        }
        this.out.flush();
    }

    private void nextToken() throws SQLException {
        String str = "08S01";
        checkOpen();
        if (this.endOfResponse) {
            this.currentToken.token = TDS_DONE_TOKEN;
            this.currentToken.status = 0;
            return;
        }
        try {
            if (this.computedColumns != null) {
                byte peek = (byte) this.f122in.peek();
                if (peek != -47) {
                    if (peek == -45) {
                        if (!this.endOfResults) {
                            this.endOfResults = true;
                            return;
                        }
                    }
                } else if (this.endOfResults) {
                    this.endOfResults = false;
                    return;
                }
            }
            this.currentToken.token = (byte) this.f122in.read();
            byte b = this.currentToken.token;
            if (b == -127) {
                tds7ResultToken();
            } else if (b == -120) {
                tdsComputedResultToken();
            } else if (b == -47) {
                tdsRowToken();
            } else if (b == -45) {
                tdsComputedRowToken();
            } else if (b == -41) {
                tds5ParamsToken();
            } else if (b == -27) {
                tds5ErrorToken();
            } else if (b == -25) {
                tds5DynamicToken();
            } else if (b == 97) {
                tds5WideResultToken();
            } else if (b == 113) {
                tdsInvalidToken();
            } else if (b == 124) {
                tdsProcIdToken();
            } else if (b == -96) {
                tds4ColNamesToken();
            } else if (b == -95) {
                tds4ColFormatToken();
            } else if (b == -92) {
                tdsTableNameToken();
            } else if (b == -91) {
                tdsColumnInfoToken();
            } else if (b == -30) {
                tdsCapabilityToken();
            } else if (b == -29) {
                tdsEnvChangeToken();
            } else if (b == -3 || b == -2 || b == -1) {
                tdsDoneToken();
            } else if (b == 32) {
                tds5ParamFmt2Token();
            } else if (b == 33) {
                tdsInvalidToken();
            } else if (b == 120) {
                tdsOffsetsToken();
            } else if (b != 121) {
                switch (b) {
                    case -89:
                        tdsInvalidToken();
                        break;
                    case -88:
                        tdsInvalidToken();
                        break;
                    case -87:
                        tdsOrderByToken();
                        break;
                    case -86:
                    case -85:
                        tdsErrorToken();
                        break;
                    case -84:
                        tdsOutputParamToken();
                        break;
                    case -83:
                        tdsLoginAckToken();
                        break;
                    case -82:
                        tdsControlToken();
                        break;
                    default:
                        switch (b) {
                            case -20:
                                tds5ParamFmtToken();
                                break;
                            case -19:
                                if (this._gssContext == null) {
                                    tdsNtlmAuthToken();
                                    break;
                                } else {
                                    tdsGssToken();
                                    break;
                                }
                            case -18:
                                tds5ResultToken();
                                break;
                            default:
                                StringBuilder sb = new StringBuilder();
                                sb.append("Invalid packet type 0x");
                                sb.append(Integer.toHexString(this.currentToken.token & TDS_DONEINPROC_TOKEN));
                                throw new ProtocolException(sb.toString());
                        }
                }
            } else {
                tdsReturnStatusToken();
            }
        } catch (IOException e) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), str), (Throwable) e);
        } catch (ProtocolException e2) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.tdserror", (Object) e2.getMessage()), str), (Throwable) e2);
        } catch (OutOfMemoryError e3) {
            this.f122in.skipToEnd();
            this.endOfResponse = true;
            this.endOfResults = true;
            this.cancelPending = false;
            throw e3;
        }
    }

    private void tdsInvalidToken() throws IOException, ProtocolException {
        ResponseStream responseStream = this.f122in;
        responseStream.skip(responseStream.readShort());
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported TDS token: 0x");
        sb.append(Integer.toHexString(this.currentToken.token & TDS_DONEINPROC_TOKEN));
        throw new ProtocolException(sb.toString());
    }

    private void tds5ParamFmt2Token() throws IOException, ProtocolException {
        this.f122in.readInt();
        short readShort = this.f122in.readShort();
        ColInfo[] colInfoArr = new ColInfo[readShort];
        for (int i = 0; i < readShort; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.f122in.readNonUnicodeString(this.f122in.read());
            int readInt = this.f122in.readInt();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (readInt & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (readInt & 16) != 0;
            colInfo.isIdentity = (readInt & 64) != 0;
            colInfo.isKey = (readInt & 2) != 0;
            colInfo.isHidden = (readInt & 1) != 0;
            colInfo.userType = this.f122in.readInt();
            TdsData.readType(this.f122in, colInfo);
            this.f122in.skip(1);
            colInfoArr[i] = colInfo;
        }
        this.currentToken.dynamParamInfo = colInfoArr;
        this.currentToken.dynamParamData = new Object[readShort];
    }

    private void tds5WideResultToken() throws IOException, ProtocolException {
        this.f122in.readInt();
        short readShort = this.f122in.readShort();
        this.columns = new ColInfo[readShort];
        this.rowData = new Object[readShort];
        this.tables = null;
        for (int i = 0; i < readShort; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.name = this.f122in.readNonUnicodeString(this.f122in.read());
            colInfo.catalog = this.f122in.readNonUnicodeString(this.f122in.read());
            colInfo.schema = this.f122in.readNonUnicodeString(this.f122in.read());
            colInfo.tableName = this.f122in.readNonUnicodeString(this.f122in.read());
            colInfo.realName = this.f122in.readNonUnicodeString(this.f122in.read());
            if (colInfo.name == null || colInfo.name.length() == 0) {
                colInfo.name = colInfo.realName;
            }
            int readInt = this.f122in.readInt();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (readInt & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (readInt & 16) != 0;
            colInfo.isIdentity = (readInt & 64) != 0;
            colInfo.isKey = (readInt & 2) != 0;
            colInfo.isHidden = (readInt & 1) != 0;
            colInfo.userType = this.f122in.readInt();
            TdsData.readType(this.f122in, colInfo);
            this.f122in.skip(1);
            this.columns[i] = colInfo;
        }
        this.endOfResults = false;
    }

    private void tdsReturnStatusToken() throws IOException, SQLException {
        Integer num = new Integer(this.f122in.readInt());
        this.returnStatus = num;
        ParamInfo paramInfo = this.returnParam;
        if (paramInfo != null) {
            paramInfo.setOutValue(Support.convert(this.connection, num, paramInfo.jdbcType, this.connection.getCharset()));
        }
    }

    private void tdsProcIdToken() throws IOException {
        this.f122in.skip(8);
    }

    private void tdsOffsetsToken() throws IOException {
        this.f122in.read();
        this.f122in.read();
        this.f122in.readShort();
    }

    private void tds7ResultToken() throws IOException, ProtocolException, SQLException {
        this.endOfResults = false;
        short readShort = this.f122in.readShort();
        if (readShort >= 0) {
            this.columns = new ColInfo[readShort];
            this.rowData = new Object[readShort];
            this.tables = null;
            for (int i = 0; i < readShort; i++) {
                ColInfo colInfo = new ColInfo();
                colInfo.userType = this.f122in.readShort();
                short readShort2 = this.f122in.readShort();
                boolean z = true;
                colInfo.nullable = (readShort2 & 1) != 0 ? 1 : 0;
                colInfo.isCaseSensitive = (readShort2 & 2) != 0;
                colInfo.isIdentity = (readShort2 & 16) != 0;
                if ((readShort2 & 12) == 0) {
                    z = false;
                }
                colInfo.isWriteable = z;
                TdsData.readType(this.f122in, colInfo);
                if (this.tdsVersion >= 4 && colInfo.collation != null) {
                    TdsData.setColumnCharset(colInfo, this.connection);
                }
                colInfo.realName = this.f122in.readUnicodeString(this.f122in.read());
                colInfo.name = colInfo.realName;
                this.columns[i] = colInfo;
            }
        }
    }

    private void tds4ColNamesToken() throws IOException {
        ArrayList arrayList = new ArrayList();
        short readShort = this.f122in.readShort();
        this.tables = null;
        int i = 0;
        while (i < readShort) {
            ColInfo colInfo = new ColInfo();
            int read = this.f122in.read();
            String readNonUnicodeString = this.f122in.readNonUnicodeString(read);
            i = i + 1 + read;
            colInfo.realName = readNonUnicodeString;
            colInfo.name = readNonUnicodeString;
            arrayList.add(colInfo);
        }
        int size = arrayList.size();
        this.columns = (ColInfo[]) arrayList.toArray(new ColInfo[size]);
        this.rowData = new Object[size];
    }

    private void tds4ColFormatToken() throws IOException, ProtocolException {
        short readShort = this.f122in.readShort();
        int i = 0;
        int i2 = 0;
        while (i < readShort) {
            ColInfo[] colInfoArr = this.columns;
            if (i2 <= colInfoArr.length) {
                ColInfo colInfo = colInfoArr[i2];
                boolean z = true;
                if (this.serverType == 1) {
                    colInfo.userType = this.f122in.readShort();
                    short readShort2 = this.f122in.readShort();
                    colInfo.nullable = (readShort2 & 1) != 0 ? 1 : 0;
                    colInfo.isCaseSensitive = (readShort2 & 2) != 0;
                    colInfo.isWriteable = (readShort2 & 12) != 0;
                    if ((readShort2 & 16) == 0) {
                        z = false;
                    }
                    colInfo.isIdentity = z;
                } else {
                    colInfo.isCaseSensitive = false;
                    colInfo.isWriteable = true;
                    if (colInfo.nullable == 0) {
                        colInfo.nullable = 2;
                    }
                    colInfo.userType = this.f122in.readInt();
                }
                i = i + 4 + TdsData.readType(this.f122in, colInfo);
                i2++;
            } else {
                throw new ProtocolException("Too many columns in TDS_COL_FMT packet");
            }
        }
        if (i2 == this.columns.length) {
            this.endOfResults = false;
            return;
        }
        throw new ProtocolException("Too few columns in TDS_COL_FMT packet");
    }

    private void tdsTableNameToken() throws IOException, ProtocolException {
        TableMetaData tableMetaData;
        String str;
        short readShort = this.f122in.readShort();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < readShort) {
            int i2 = this.tdsVersion;
            if (i2 >= 5) {
                tableMetaData = new TableMetaData();
                i++;
                int read = this.f122in.read();
                if (read != 0) {
                    if (read != 1) {
                        if (read != 2) {
                            if (read != 3) {
                                if (read == 4) {
                                    short readShort2 = this.f122in.readShort();
                                    i += (readShort2 * 2) + 2;
                                    this.f122in.readUnicodeString(readShort2);
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Invalid table TAB_NAME_TOKEN: ");
                                    sb.append(read);
                                    throw new ProtocolException(sb.toString());
                                }
                            }
                            short readShort3 = this.f122in.readShort();
                            i += (readShort3 * 2) + 2;
                            tableMetaData.catalog = this.f122in.readUnicodeString(readShort3);
                        }
                        short readShort4 = this.f122in.readShort();
                        i += (readShort4 * 2) + 2;
                        tableMetaData.schema = this.f122in.readUnicodeString(readShort4);
                    }
                    short readShort5 = this.f122in.readShort();
                    i += (readShort5 * 2) + 2;
                    tableMetaData.name = this.f122in.readUnicodeString(readShort5);
                }
            } else {
                if (i2 >= 3) {
                    short readShort6 = this.f122in.readShort();
                    i += (readShort6 * 2) + 2;
                    str = this.f122in.readUnicodeString(readShort6);
                } else {
                    int read2 = this.f122in.read();
                    i++;
                    if (read2 != 0) {
                        i += read2;
                        str = this.f122in.readNonUnicodeString(read2);
                    }
                }
                TableMetaData tableMetaData2 = new TableMetaData();
                int lastIndexOf = str.lastIndexOf(46);
                if (lastIndexOf > 0) {
                    tableMetaData2.name = str.substring(lastIndexOf + 1);
                    int lastIndexOf2 = str.lastIndexOf(46, lastIndexOf - 1);
                    int i3 = lastIndexOf2 + 1;
                    if (i3 < lastIndexOf) {
                        tableMetaData2.schema = str.substring(i3, lastIndexOf);
                    }
                    int lastIndexOf3 = str.lastIndexOf(46, lastIndexOf2 - 1) + 1;
                    if (lastIndexOf3 < lastIndexOf2) {
                        tableMetaData2.catalog = str.substring(lastIndexOf3, lastIndexOf2);
                    }
                } else {
                    tableMetaData2.name = str;
                }
                tableMetaData = tableMetaData2;
            }
            arrayList.add(tableMetaData);
        }
        if (arrayList.size() > 0) {
            this.tables = (TableMetaData[]) arrayList.toArray(new TableMetaData[arrayList.size()]);
        }
    }

    /* JADX WARNING: type inference failed for: r2v4 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r2v4
      assigns: []
      uses: []
      mth insns count: 76
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void tdsColumnInfoToken() throws java.io.IOException, net.sourceforge.jtds.jdbc.ProtocolException {
        /*
            r9 = this;
            net.sourceforge.jtds.jdbc.ResponseStream r0 = r9.f122in
            short r0 = r0.readShort()
            r1 = 0
            r2 = 0
            r3 = 0
        L_0x0009:
            if (r2 >= r0) goto L_0x00ae
            net.sourceforge.jtds.jdbc.ResponseStream r4 = r9.f122in
            r4.read()
            net.sourceforge.jtds.jdbc.ColInfo[] r4 = r9.columns
            int r5 = r4.length
            java.lang.String r6 = " invalid in TDS_COLINFO packet"
            r7 = 1
            if (r3 >= r5) goto L_0x0093
            int r5 = r3 + 1
            r3 = r4[r3]
            net.sourceforge.jtds.jdbc.ResponseStream r4 = r9.f122in
            int r4 = r4.read()
            net.sourceforge.jtds.jdbc.TdsCore$TableMetaData[] r8 = r9.tables
            if (r8 == 0) goto L_0x0044
            int r8 = r8.length
            if (r4 > r8) goto L_0x002a
            goto L_0x0044
        L_0x002a:
            net.sourceforge.jtds.jdbc.ProtocolException r0 = new net.sourceforge.jtds.jdbc.ProtocolException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Table index "
            r1.append(r2)
            r1.append(r4)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0044:
            net.sourceforge.jtds.jdbc.ResponseStream r6 = r9.f122in
            int r6 = r6.read()
            byte r6 = (byte) r6
            int r2 = r2 + 3
            if (r4 == 0) goto L_0x0063
            net.sourceforge.jtds.jdbc.TdsCore$TableMetaData[] r8 = r9.tables
            if (r8 == 0) goto L_0x0063
            int r4 = r4 + -1
            r4 = r8[r4]
            java.lang.String r8 = r4.catalog
            r3.catalog = r8
            java.lang.String r8 = r4.schema
            r3.schema = r8
            java.lang.String r4 = r4.name
            r3.tableName = r4
        L_0x0063:
            r4 = r6 & 8
            if (r4 == 0) goto L_0x0069
            r4 = 1
            goto L_0x006a
        L_0x0069:
            r4 = 0
        L_0x006a:
            r3.isKey = r4
            r4 = r6 & 16
            if (r4 == 0) goto L_0x0071
            goto L_0x0072
        L_0x0071:
            r7 = 0
        L_0x0072:
            r3.isHidden = r7
            r4 = r6 & 32
            if (r4 == 0) goto L_0x0090
            net.sourceforge.jtds.jdbc.ResponseStream r4 = r9.f122in
            int r4 = r4.read()
            int r2 = r2 + 1
            net.sourceforge.jtds.jdbc.ResponseStream r6 = r9.f122in
            java.lang.String r6 = r6.readString(r4)
            int r7 = r9.tdsVersion
            r8 = 3
            if (r7 < r8) goto L_0x008d
            int r4 = r4 * 2
        L_0x008d:
            int r2 = r2 + r4
            r3.realName = r6
        L_0x0090:
            r3 = r5
            goto L_0x0009
        L_0x0093:
            net.sourceforge.jtds.jdbc.ProtocolException r0 = new net.sourceforge.jtds.jdbc.ProtocolException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Column index "
            r1.append(r2)
            int r3 = r3 + r7
            r1.append(r3)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x00ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.tdsColumnInfoToken():void");
    }

    private void tdsOrderByToken() throws IOException {
        this.f122in.skip(this.f122in.readShort());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0081, code lost:
        if (r1 > 9) goto L_0x0085;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void tdsErrorToken() throws java.io.IOException {
        /*
            r12 = this;
            net.sourceforge.jtds.jdbc.ResponseStream r0 = r12.f122in
            short r0 = r0.readShort()
            net.sourceforge.jtds.jdbc.ResponseStream r1 = r12.f122in
            int r3 = r1.readInt()
            net.sourceforge.jtds.jdbc.ResponseStream r1 = r12.f122in
            int r4 = r1.read()
            net.sourceforge.jtds.jdbc.ResponseStream r1 = r12.f122in
            int r1 = r1.read()
            net.sourceforge.jtds.jdbc.ResponseStream r2 = r12.f122in
            short r2 = r2.readShort()
            net.sourceforge.jtds.jdbc.ResponseStream r5 = r12.f122in
            java.lang.String r6 = r5.readString(r2)
            int r5 = r12.tdsVersion
            r7 = 3
            if (r5 < r7) goto L_0x002b
            int r2 = r2 * 2
        L_0x002b:
            int r2 = r2 + 2
            int r2 = r2 + 6
            net.sourceforge.jtds.jdbc.ResponseStream r5 = r12.f122in
            int r5 = r5.read()
            net.sourceforge.jtds.jdbc.ResponseStream r8 = r12.f122in
            java.lang.String r8 = r8.readString(r5)
            int r9 = r12.tdsVersion
            if (r9 < r7) goto L_0x0041
            int r5 = r5 * 2
        L_0x0041:
            r9 = 1
            int r5 = r5 + r9
            int r2 = r2 + r5
            net.sourceforge.jtds.jdbc.ResponseStream r5 = r12.f122in
            int r5 = r5.read()
            net.sourceforge.jtds.jdbc.ResponseStream r10 = r12.f122in
            java.lang.String r10 = r10.readString(r5)
            int r11 = r12.tdsVersion
            if (r11 < r7) goto L_0x0056
            int r5 = r5 * 2
        L_0x0056:
            int r5 = r5 + r9
            int r2 = r2 + r5
            net.sourceforge.jtds.jdbc.ResponseStream r5 = r12.f122in
            short r11 = r5.readShort()
            int r2 = r2 + 2
            int r0 = r0 - r2
            if (r0 <= 0) goto L_0x0068
            net.sourceforge.jtds.jdbc.ResponseStream r2 = r12.f122in
            r2.skip(r0)
        L_0x0068:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r0 = r12.currentToken
            byte r0 = r0.token
            r2 = -86
            r5 = 9
            if (r0 != r2) goto L_0x0081
            r12._ErrorReceived = r9
            r0 = 10
            if (r1 >= r0) goto L_0x007a
            r1 = 11
        L_0x007a:
            r0 = 20
            if (r1 < r0) goto L_0x0084
            r12.fatalError = r9
            goto L_0x0084
        L_0x0081:
            if (r1 <= r5) goto L_0x0084
            goto L_0x0085
        L_0x0084:
            r5 = r1
        L_0x0085:
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r12.messages
            r7 = r8
            r8 = r10
            r9 = r11
            r2.addDiagnostic(r3, r4, r5, r6, r7, r8, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.tdsErrorToken():void");
    }

    private void tdsOutputParamToken() throws IOException, ProtocolException, SQLException {
        int i;
        ParamInfo[] paramInfoArr;
        this.f122in.readShort();
        ResponseStream responseStream = this.f122in;
        String readString = responseStream.readString(responseStream.read());
        boolean z = this.f122in.read() == 2;
        this.f122in.read();
        this.f122in.skip(3);
        ColInfo colInfo = new ColInfo();
        TdsData.readType(this.f122in, colInfo);
        if (this.tdsVersion >= 4 && colInfo.collation != null) {
            TdsData.setColumnCharset(colInfo, this.connection);
        }
        Object readData = TdsData.readData(this.connection, this.f122in, colInfo);
        if (this.parameters == null) {
            return;
        }
        if (readString.length() != 0 && !readString.startsWith("@")) {
            return;
        }
        if (this.tdsVersion < 4 || !z) {
            do {
                i = this.nextParam + 1;
                this.nextParam = i;
                paramInfoArr = this.parameters;
                if (i >= paramInfoArr.length) {
                    return;
                }
            } while (!paramInfoArr[i].isOutput);
            if (readData != null) {
                ParamInfo[] paramInfoArr2 = this.parameters;
                int i2 = this.nextParam;
                paramInfoArr2[i2].setOutValue(Support.convert(this.connection, readData, paramInfoArr2[i2].jdbcType, this.connection.getCharset()));
                this.parameters[this.nextParam].collation = colInfo.collation;
                this.parameters[this.nextParam].charsetInfo = colInfo.charsetInfo;
                return;
            }
            this.parameters[this.nextParam].setOutValue(null);
            return;
        }
        ParamInfo paramInfo = this.returnParam;
        if (paramInfo == null) {
            return;
        }
        if (readData != null) {
            paramInfo.setOutValue(Support.convert(this.connection, readData, paramInfo.jdbcType, this.connection.getCharset()));
            this.returnParam.collation = colInfo.collation;
            this.returnParam.charsetInfo = colInfo.charsetInfo;
            return;
        }
        paramInfo.setOutValue(null);
    }

    private void tdsLoginAckToken() throws IOException {
        int i;
        int i2;
        int i3;
        this.f122in.readShort();
        int read = this.f122in.read();
        int tdsVersion2 = TdsData.getTdsVersion((this.f122in.read() << 24) | (this.f122in.read() << 16) | (this.f122in.read() << 8) | this.f122in.read());
        this.tdsVersion = tdsVersion2;
        this.socket.setTdsVersion(tdsVersion2);
        ResponseStream responseStream = this.f122in;
        String readString = responseStream.readString(responseStream.read());
        if (this.tdsVersion >= 3) {
            i3 = this.f122in.read();
            i2 = this.f122in.read();
            i = (this.f122in.read() << 8) + this.f122in.read();
        } else {
            if (readString.toLowerCase().startsWith("microsoft")) {
                this.f122in.skip(1);
                i3 = this.f122in.read();
                i2 = this.f122in.read();
            } else {
                i3 = this.f122in.read();
                i2 = (this.f122in.read() * 10) + this.f122in.read();
            }
            this.f122in.skip(1);
            i = 0;
        }
        if (readString.length() > 1 && -1 != readString.indexOf(0)) {
            readString = readString.substring(0, readString.indexOf(0));
        }
        this.connection.setDBServerInfo(readString, i3, i2, i);
        if (this.tdsVersion != 2 || read == 5) {
            this.messages.clearWarnings();
            for (SQLException sQLException = this.messages.exceptions; sQLException != null; sQLException = sQLException.getNextException()) {
                this.messages.addWarning(new SQLWarning(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode()));
            }
            this.messages.exceptions = null;
            return;
        }
        this.messages.addDiagnostic(4002, 0, 14, "Login failed", "", "", 0);
        this.currentToken.token = TDS_ERROR_TOKEN;
    }

    private void tdsControlToken() throws IOException {
        this.f122in.skip(this.f122in.readShort());
    }

    private void tdsRowToken() throws IOException, ProtocolException {
        int i = 0;
        while (true) {
            ColInfo[] colInfoArr = this.columns;
            if (i < colInfoArr.length) {
                this.rowData[i] = TdsData.readData(this.connection, this.f122in, colInfoArr[i]);
                i++;
            } else {
                this.endOfResults = false;
                return;
            }
        }
    }

    private void tds5ParamsToken() throws IOException, ProtocolException, SQLException {
        if (this.currentToken.dynamParamInfo != null) {
            for (int i = 0; i < this.currentToken.dynamParamData.length; i++) {
                this.currentToken.dynamParamData[i] = TdsData.readData(this.connection, this.f122in, this.currentToken.dynamParamInfo[i]);
                String str = this.currentToken.dynamParamInfo[i].realName;
                if (this.parameters != null && (str.length() == 0 || str.startsWith("@"))) {
                    while (true) {
                        int i2 = this.nextParam + 1;
                        this.nextParam = i2;
                        ParamInfo[] paramInfoArr = this.parameters;
                        if (i2 >= paramInfoArr.length) {
                            break;
                        } else if (paramInfoArr[i2].isOutput) {
                            Object obj = this.currentToken.dynamParamData[i];
                            if (obj != null) {
                                ParamInfo[] paramInfoArr2 = this.parameters;
                                int i3 = this.nextParam;
                                paramInfoArr2[i3].setOutValue(Support.convert(this.connection, obj, paramInfoArr2[i3].jdbcType, this.connection.getCharset()));
                            } else {
                                this.parameters[this.nextParam].setOutValue(null);
                            }
                        }
                    }
                }
            }
            return;
        }
        throw new ProtocolException("TDS 5 Param results token (0xD7) not preceded by param format (0xEC or 0X20).");
    }

    private void tdsCapabilityToken() throws IOException, ProtocolException {
        this.f122in.readShort();
        if (this.f122in.read() == 1) {
            int read = this.f122in.read();
            if (read == 11 || read == 0) {
                byte[] bArr = new byte[11];
                if (read == 0) {
                    Logger.println("TDS_CAPABILITY: Invalid request length");
                } else {
                    this.f122in.read(bArr);
                }
                if (this.f122in.read() == 2) {
                    int read2 = this.f122in.read();
                    if (read2 == 10 || read2 == 0) {
                        byte[] bArr2 = new byte[10];
                        if (read2 == 0) {
                            Logger.println("TDS_CAPABILITY: Invalid response length");
                        } else {
                            this.f122in.read(bArr2);
                        }
                        int i = 0;
                        if ((bArr[0] & 2) == 2) {
                            i = 32;
                        }
                        if ((bArr[1] & 3) == 3) {
                            i |= 2;
                        }
                        if ((bArr[2] & DONE_END_OF_RESPONSE) == 128) {
                            i |= 16;
                        }
                        if ((bArr[3] & 2) == 2) {
                            i |= 8;
                        }
                        if ((bArr[2] & 1) == 1) {
                            i |= 64;
                        }
                        if ((bArr[4] & 4) == 4) {
                            i |= 4;
                        }
                        if ((bArr[7] & 48) == 48) {
                            i |= 1;
                        }
                        this.connection.setSybaseInfo(i);
                        return;
                    }
                    throw new ProtocolException("TDS_CAPABILITY: byte count not 10");
                }
                throw new ProtocolException("TDS_CAPABILITY: expected response string");
            }
            throw new ProtocolException("TDS_CAPABILITY: byte count not 11");
        }
        throw new ProtocolException("TDS_CAPABILITY: expected request string");
    }

    private void tdsEnvChangeToken() throws IOException, SQLException {
        short readShort = this.f122in.readShort();
        int read = this.f122in.read();
        if (read == 1) {
            this.connection.setDatabase(this.f122in.readString(this.f122in.read()), this.f122in.readString(this.f122in.read()));
        } else if (read == 2) {
            String readString = this.f122in.readString(this.f122in.read());
            String readString2 = this.f122in.readString(this.f122in.read());
            if (Logger.isActive()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Language changed from ");
                sb.append(readString2);
                sb.append(" to ");
                sb.append(readString);
                Logger.println(sb.toString());
            }
        } else if (read == 3) {
            int read2 = this.f122in.read();
            String readString3 = this.f122in.readString(read2);
            if (this.tdsVersion >= 3) {
                this.f122in.skip((readShort - 2) - (read2 * 2));
            } else {
                this.f122in.skip((readShort - 2) - read2);
            }
            this.connection.setServerCharset(readString3);
        } else if (read == 4) {
            int read3 = this.f122in.read();
            int parseInt = Integer.parseInt(this.f122in.readString(read3));
            if (this.tdsVersion >= 3) {
                this.f122in.skip((readShort - 2) - (read3 * 2));
            } else {
                this.f122in.skip((readShort - 2) - read3);
            }
            this.connection.setNetPacketSize(parseInt);
            this.out.setBufferSize(parseInt);
            if (Logger.isActive()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Changed blocksize to ");
                sb2.append(parseInt);
                Logger.println(sb2.toString());
            }
        } else if (read == 5) {
            this.f122in.skip(readShort - 1);
        } else if (read != 7) {
            if (Logger.isActive()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Unknown environment change type 0x");
                sb3.append(Integer.toHexString(read));
                Logger.println(sb3.toString());
            }
            this.f122in.skip(readShort - 1);
        } else {
            int read4 = this.f122in.read();
            byte[] bArr = new byte[5];
            if (read4 == 5) {
                this.f122in.read(bArr);
                this.connection.setCollation(bArr);
            } else {
                this.f122in.skip(read4);
            }
            this.f122in.skip(this.f122in.read());
        }
    }

    private void tds5ErrorToken() throws IOException {
        short readShort = this.f122in.readShort();
        int readInt = this.f122in.readInt();
        int read = this.f122in.read();
        int read2 = this.f122in.read();
        int read3 = this.f122in.read();
        this.f122in.readNonUnicodeString(read3);
        this.f122in.read();
        this.f122in.readShort();
        int i = read3 + 4 + 6;
        short readShort2 = this.f122in.readShort();
        String readNonUnicodeString = this.f122in.readNonUnicodeString(readShort2);
        int i2 = i + readShort2 + 2;
        int read4 = this.f122in.read();
        String readNonUnicodeString2 = this.f122in.readNonUnicodeString(read4);
        int i3 = i2 + read4 + 1;
        int read5 = this.f122in.read();
        String readNonUnicodeString3 = this.f122in.readNonUnicodeString(read5);
        int i4 = i3 + read5 + 1;
        short readShort3 = this.f122in.readShort();
        int i5 = readShort - (i4 + 2);
        if (i5 > 0) {
            this.f122in.skip(i5);
        }
        if (read2 > 10) {
            this.messages.addDiagnostic(readInt, read, read2, readNonUnicodeString, readNonUnicodeString2, readNonUnicodeString3, readShort3);
        } else {
            this.messages.addDiagnostic(readInt, read, read2, readNonUnicodeString, readNonUnicodeString2, readNonUnicodeString3, readShort3);
        }
    }

    private void tds5DynamicToken() throws IOException {
        short readShort = this.f122in.readShort();
        byte read = (byte) this.f122in.read();
        this.f122in.read();
        int i = readShort - 2;
        if (read == 32) {
            int read2 = this.f122in.read();
            this.f122in.skip(read2);
            i -= read2 + 1;
        }
        this.f122in.skip(i);
    }

    private void tds5ParamFmtToken() throws IOException, ProtocolException {
        this.f122in.readShort();
        short readShort = this.f122in.readShort();
        ColInfo[] colInfoArr = new ColInfo[readShort];
        for (int i = 0; i < readShort; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.f122in.readNonUnicodeString(this.f122in.read());
            int read = this.f122in.read();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (read & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (read & 16) != 0;
            colInfo.isIdentity = (read & 64) != 0;
            colInfo.isKey = (read & 2) != 0;
            colInfo.isHidden = (read & 1) != 0;
            colInfo.userType = this.f122in.readInt();
            if (((byte) this.f122in.peek()) == -3) {
                this.currentToken.dynamParamInfo = null;
                this.currentToken.dynamParamData = null;
                this.messages.addDiagnostic(9999, 0, 16, "Prepare failed", "", "", 0);
                return;
            }
            TdsData.readType(this.f122in, colInfo);
            this.f122in.skip(1);
            colInfoArr[i] = colInfo;
        }
        this.currentToken.dynamParamInfo = colInfoArr;
        this.currentToken.dynamParamData = new Object[readShort];
    }

    private void tdsNtlmAuthToken() throws IOException, ProtocolException {
        short readShort = this.f122in.readShort();
        if (readShort >= 40) {
            byte[] bArr = new byte[readShort];
            this.ntlmMessage = bArr;
            this.f122in.read(bArr);
            int intFromBuffer = getIntFromBuffer(this.ntlmMessage, 8);
            if (intFromBuffer == 2) {
                getIntFromBuffer(this.ntlmMessage, 20);
                int shortFromBuffer = getShortFromBuffer(this.ntlmMessage, 40);
                int intFromBuffer2 = getIntFromBuffer(this.ntlmMessage, 44);
                byte[] bArr2 = new byte[shortFromBuffer];
                this.ntlmTarget = bArr2;
                System.arraycopy(this.ntlmMessage, intFromBuffer2, bArr2, 0, shortFromBuffer);
                byte[] bArr3 = new byte[8];
                this.nonce = bArr3;
                System.arraycopy(this.ntlmMessage, 24, bArr3, 0, 8);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("NTLM challenge: got unexpected sequence number:");
            sb.append(intFromBuffer);
            throw new ProtocolException(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("NTLM challenge: packet is too small:");
        sb2.append(readShort);
        throw new ProtocolException(sb2.toString());
    }

    private static int getIntFromBuffer(byte[] bArr, int i) {
        return ((bArr[i + 3] & TDS_DONEINPROC_TOKEN) << 24) | ((bArr[i + 2] & TDS_DONEINPROC_TOKEN) << 16) | ((bArr[i + 1] & TDS_DONEINPROC_TOKEN) << 8) | (bArr[i] & TDS_DONEINPROC_TOKEN);
    }

    private static int getShortFromBuffer(byte[] bArr, int i) {
        return ((bArr[i + 1] & TDS_DONEINPROC_TOKEN) << 8) | (bArr[i] & TDS_DONEINPROC_TOKEN);
    }

    private void tds5ResultToken() throws IOException, ProtocolException {
        this.f122in.readShort();
        short readShort = this.f122in.readShort();
        this.columns = new ColInfo[readShort];
        this.rowData = new Object[readShort];
        this.tables = null;
        for (int i = 0; i < readShort; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.f122in.readNonUnicodeString(this.f122in.read());
            colInfo.name = colInfo.realName;
            int read = this.f122in.read();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (read & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (read & 16) != 0;
            colInfo.isIdentity = (read & 64) != 0;
            colInfo.isKey = (read & 2) != 0;
            colInfo.isHidden = (read & 1) != 0;
            colInfo.userType = this.f122in.readInt();
            TdsData.readType(this.f122in, colInfo);
            this.f122in.skip(1);
            this.columns[i] = colInfo;
        }
        this.endOfResults = false;
    }

    private void tdsDoneToken() throws IOException {
        this.currentToken.status = (byte) this.f122in.read();
        this.f122in.skip(1);
        this.currentToken.operation = (byte) this.f122in.read();
        this.f122in.skip(1);
        this.currentToken.updateCount = this.f122in.readInt();
        if (!this.endOfResults) {
            TdsToken tdsToken = this.currentToken;
            tdsToken.status = (byte) (tdsToken.status & -17);
            this.endOfResults = true;
        }
        if ((this.currentToken.status & 32) != 0) {
            synchronized (this.cancelMonitor) {
                this.cancelPending = false;
                if (this.cancelMonitor[0] == 0) {
                    this.messages.addException(new SQLException(Messages.get("error.generic.cancelled", (Object) "Statement"), "HY008"));
                }
            }
        } else if (!this._ErrorReceived && (this.currentToken.status & 2) != 0) {
            this.messages.addException(new SQLException(Messages.get("error.generic.unspecified"), "HY000"));
        }
        this._ErrorReceived = false;
        if ((this.currentToken.status & 1) == 0) {
            this.endOfResponse = !this.cancelPending;
            if (this.fatalError) {
                this.connection.setClosed();
            }
        }
        if (this.serverType == 1 && this.currentToken.operation == -63) {
            TdsToken tdsToken2 = this.currentToken;
            tdsToken2.status = (byte) (tdsToken2.status & -17);
        }
    }

    private void executeSQL42(String str, String str2, ParamInfo[] paramInfoArr, boolean z, boolean z2) throws IOException, SQLException {
        if (str2 != null) {
            this.out.setPacketType(3);
            byte[] encodeString = Support.encodeString(this.connection.getCharset(), str2);
            this.out.write((byte) encodeString.length);
            this.out.write(encodeString);
            this.out.write((short) (z ? 2 : 0));
            if (paramInfoArr != null) {
                for (int i = this.nextParam + 1; i < paramInfoArr.length; i++) {
                    if (paramInfoArr[i].name != null) {
                        byte[] encodeString2 = Support.encodeString(this.connection.getCharset(), paramInfoArr[i].name);
                        this.out.write((byte) encodeString2.length);
                        this.out.write(encodeString2);
                    } else {
                        this.out.write(0);
                    }
                    this.out.write(paramInfoArr[i].isOutput ? (byte) 1 : 0);
                    TdsData.writeParam(this.out, this.connection.getCharsetInfo(), null, paramInfoArr[i]);
                }
            }
            if (!z2) {
                this.out.write((byte) DONE_END_OF_RESPONSE);
            }
        } else if (str.length() > 0) {
            if (paramInfoArr != null) {
                str = Support.substituteParameters(str, paramInfoArr, this.connection);
            }
            this.out.setPacketType(1);
            this.out.write(str);
            if (!z2) {
                this.out.write(" ");
            }
        }
    }

    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r0v4, types: [int] */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v0
      assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], ?[int, float, short, byte, char], ?[boolean, int, float, short, byte, char]]
      uses: [int, boolean]
      mth insns count: 188
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeSQL50(java.lang.String r10, java.lang.String r11, net.sourceforge.jtds.jdbc.ParamInfo[] r12) throws java.io.IOException, java.sql.SQLException {
        /*
            r9 = this;
            r0 = 0
            r1 = 1
            if (r12 == 0) goto L_0x0006
            r2 = 1
            goto L_0x0007
        L_0x0006:
            r2 = 0
        L_0x0007:
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r3 = r9.currentToken
            r4 = 0
            r3.dynamParamInfo = r4
            net.sourceforge.jtds.jdbc.TdsCore$TdsToken r3 = r9.currentToken
            r3.dynamParamData = r4
            r3 = 0
        L_0x0011:
            if (r2 == 0) goto L_0x0085
            int r5 = r12.length
            if (r3 >= r5) goto L_0x0085
            r5 = r12[r3]
            java.lang.String r5 = r5.sqlType
            java.lang.String r6 = "text"
            boolean r5 = r6.equals(r5)
            java.lang.String r7 = "unitext"
            if (r5 != 0) goto L_0x003a
            r5 = r12[r3]
            java.lang.String r5 = r5.sqlType
            java.lang.String r8 = "image"
            boolean r5 = r8.equals(r5)
            if (r5 != 0) goto L_0x003a
            r5 = r12[r3]
            java.lang.String r5 = r5.sqlType
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x0082
        L_0x003a:
            if (r11 == 0) goto L_0x0071
            int r5 = r11.length()
            if (r5 <= 0) goto L_0x0071
            r10 = r12[r3]
            java.lang.String r10 = r10.sqlType
            boolean r10 = r6.equals(r10)
            java.lang.String r11 = "HY000"
            if (r10 != 0) goto L_0x0065
            r10 = r12[r3]
            java.lang.String r10 = r10.sqlType
            boolean r10 = r7.equals(r10)
            if (r10 == 0) goto L_0x0059
            goto L_0x0065
        L_0x0059:
            java.sql.SQLException r10 = new java.sql.SQLException
            java.lang.String r12 = "error.bintoolong"
            java.lang.String r12 = net.sourceforge.jtds.jdbc.Messages.get(r12)
            r10.<init>(r12, r11)
            throw r10
        L_0x0065:
            java.sql.SQLException r10 = new java.sql.SQLException
            java.lang.String r12 = "error.chartoolong"
            java.lang.String r12 = net.sourceforge.jtds.jdbc.Messages.get(r12)
            r10.<init>(r12, r11)
            throw r10
        L_0x0071:
            r5 = r12[r3]
            int r5 = r5.tdsType
            r6 = 36
            if (r5 == r6) goto L_0x0082
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r9.connection
            java.lang.String r10 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r10, r12, r11)
            r11 = r4
            r2 = 0
            goto L_0x0085
        L_0x0082:
            int r3 = r3 + 1
            goto L_0x0011
        L_0x0085:
            net.sourceforge.jtds.jdbc.RequestStream r3 = r9.out
            r4 = 15
            r3.setPacketType(r4)
            r3 = 2
            if (r11 != 0) goto L_0x00d8
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            r4 = 33
            r11.write(r4)
            if (r2 == 0) goto L_0x009c
            java.lang.String r10 = net.sourceforge.jtds.jdbc.Support.substituteParamMarkers(r10, r12)
        L_0x009c:
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r9.connection
            boolean r11 = r11.isWideChar()
            if (r11 == 0) goto L_0x00c2
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r9.connection
            java.lang.String r11 = r11.getCharset()
            byte[] r10 = net.sourceforge.jtds.jdbc.Support.encodeString(r11, r10)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            int r4 = r10.length
            int r4 = r4 + r1
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            byte r4 = (byte) r2
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            r11.write(r10)
            goto L_0x0148
        L_0x00c2:
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            int r4 = r10.length()
            int r4 = r4 + r1
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            byte r4 = (byte) r2
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            r11.write(r10)
            goto L_0x0148
        L_0x00d8:
            java.lang.String r10 = "#jtds"
            boolean r10 = r11.startsWith(r10)
            if (r10 == 0) goto L_0x0118
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            r4 = -25
            r10.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            int r4 = r11.length()
            int r4 = r4 + 4
            short r4 = (short) r4
            r10.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            r10.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            byte r4 = (byte) r2
            r10.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            int r4 = r11.length()
            int r4 = r4 - r1
            byte r4 = (byte) r4
            r10.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            java.lang.String r11 = r11.substring(r1)
            r10.write(r11)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            r10.write(r0)
            goto L_0x0148
        L_0x0118:
            net.sourceforge.jtds.jdbc.JtdsConnection r10 = r9.connection
            java.lang.String r10 = r10.getCharset()
            byte[] r10 = net.sourceforge.jtds.jdbc.Support.encodeString(r10, r11)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            r4 = -26
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            int r4 = r10.length
            int r4 = r4 + 3
            short r4 = (short) r4
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            int r4 = r10.length
            byte r4 = (byte) r4
            r11.write(r4)
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            r11.write(r10)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            if (r2 == 0) goto L_0x0143
            r0 = 2
        L_0x0143:
            short r11 = (short) r0
            r10.write(r11)
            r0 = 1
        L_0x0148:
            if (r2 == 0) goto L_0x01ba
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            r11 = -20
            r10.write(r11)
            int r10 = r9.nextParam
            int r10 = r10 + r1
        L_0x0154:
            int r11 = r12.length
            if (r10 >= r11) goto L_0x016d
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r9.connection
            java.lang.String r11 = r11.getCharset()
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r9.connection
            boolean r2 = r2.isWideChar()
            r4 = r12[r10]
            int r11 = net.sourceforge.jtds.jdbc.TdsData.getTds5ParamSize(r11, r2, r4, r0)
            int r3 = r3 + r11
            int r10 = r10 + 1
            goto L_0x0154
        L_0x016d:
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            short r11 = (short) r3
            r10.write(r11)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            int r11 = r9.nextParam
            if (r11 >= 0) goto L_0x017b
            int r11 = r12.length
            goto L_0x017d
        L_0x017b:
            int r11 = r12.length
            int r11 = r11 - r1
        L_0x017d:
            short r11 = (short) r11
            r10.write(r11)
            int r10 = r9.nextParam
            int r10 = r10 + r1
        L_0x0184:
            int r11 = r12.length
            if (r10 >= r11) goto L_0x019d
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r9.connection
            java.lang.String r2 = r2.getCharset()
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r9.connection
            boolean r3 = r3.isWideChar()
            r4 = r12[r10]
            net.sourceforge.jtds.jdbc.TdsData.writeTds5ParamFmt(r11, r2, r3, r4, r0)
            int r10 = r10 + 1
            goto L_0x0184
        L_0x019d:
            net.sourceforge.jtds.jdbc.RequestStream r10 = r9.out
            r11 = -41
            r10.write(r11)
            int r10 = r9.nextParam
            int r10 = r10 + r1
        L_0x01a7:
            int r11 = r12.length
            if (r10 >= r11) goto L_0x01ba
            net.sourceforge.jtds.jdbc.RequestStream r11 = r9.out
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r9.connection
            net.sourceforge.jtds.jdbc.CharsetInfo r0 = r0.getCharsetInfo()
            r1 = r12[r10]
            net.sourceforge.jtds.jdbc.TdsData.writeTds5Param(r11, r0, r1)
            int r10 = r10 + 1
            goto L_0x01a7
        L_0x01ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.executeSQL50(java.lang.String, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[]):void");
    }

    public static boolean isPreparedProcedureName(String str) {
        return str != null && str.length() > 0 && Character.isDigit(str.charAt(0));
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeSQL70(java.lang.String r9, java.lang.String r10, net.sourceforge.jtds.jdbc.ParamInfo[] r11, boolean r12, boolean r13) throws java.io.IOException, java.sql.SQLException {
        /*
            r8 = this;
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r8.connection
            int r0 = r0.getPrepareSql()
            r1 = 2
            r2 = 0
            if (r11 != 0) goto L_0x000d
            if (r0 != r1) goto L_0x000d
            r0 = 0
        L_0x000d:
            boolean r3 = r8.inBatch
            if (r3 == 0) goto L_0x0012
            r0 = 2
        L_0x0012:
            r3 = -1
            r4 = 4
            r5 = 1
            if (r10 != 0) goto L_0x0054
            if (r11 == 0) goto L_0x007d
            if (r0 != 0) goto L_0x0022
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r8.connection
            java.lang.String r9 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r9, r11, r0)
            goto L_0x007d
        L_0x0022:
            int r10 = r11.length
            int r10 = r10 + r1
            net.sourceforge.jtds.jdbc.ParamInfo[] r10 = new net.sourceforge.jtds.jdbc.ParamInfo[r10]
            int r0 = r11.length
            java.lang.System.arraycopy(r11, r2, r10, r1, r0)
            net.sourceforge.jtds.jdbc.ParamInfo r0 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.String r6 = net.sourceforge.jtds.jdbc.Support.substituteParamMarkers(r9, r11)
            r0.<init>(r3, r6, r4)
            r10[r2] = r0
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r8.connection
            r6 = r10[r2]
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r0, r6)
            net.sourceforge.jtds.jdbc.ParamInfo r0 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.String r11 = net.sourceforge.jtds.jdbc.Support.getParameterDefinitions(r11)
            r0.<init>(r3, r11, r4)
            r10[r5] = r0
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r8.connection
            r0 = r10[r5]
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r11, r0)
            java.lang.String r11 = "sp_executesql"
            r7 = r11
            r11 = r10
            r10 = r7
            goto L_0x007d
        L_0x0054:
            boolean r0 = isPreparedProcedureName(r10)
            if (r0 == 0) goto L_0x007d
            if (r11 == 0) goto L_0x0066
            int r0 = r11.length
            int r0 = r0 + r5
            net.sourceforge.jtds.jdbc.ParamInfo[] r0 = new net.sourceforge.jtds.jdbc.ParamInfo[r0]
            int r6 = r11.length
            java.lang.System.arraycopy(r11, r2, r0, r5, r6)
            r11 = r0
            goto L_0x0068
        L_0x0066:
            net.sourceforge.jtds.jdbc.ParamInfo[] r11 = new net.sourceforge.jtds.jdbc.ParamInfo[r5]
        L_0x0068:
            net.sourceforge.jtds.jdbc.ParamInfo r0 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.Integer r6 = new java.lang.Integer
            r6.<init>(r10)
            r0.<init>(r4, r6, r2)
            r11[r2] = r0
            net.sourceforge.jtds.jdbc.JtdsConnection r10 = r8.connection
            r0 = r11[r2]
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r10, r0)
            java.lang.String r10 = "sp_execute"
        L_0x007d:
            if (r10 == 0) goto L_0x0110
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            r0 = 3
            r9.setPacketType(r0)
            int r9 = r8.tdsVersion
            if (r9 < r4) goto L_0x00a2
            java.util.HashMap r9 = tds8SpNames
            java.lang.Object r9 = r9.get(r10)
            java.lang.Integer r9 = (java.lang.Integer) r9
            if (r9 == 0) goto L_0x00a2
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r10.write(r3)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            short r9 = r9.shortValue()
            r10.write(r9)
            goto L_0x00b1
        L_0x00a2:
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            int r0 = r10.length()
            short r0 = (short) r0
            r9.write(r0)
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            r9.write(r10)
        L_0x00b1:
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            if (r12 == 0) goto L_0x00b6
            goto L_0x00b7
        L_0x00b6:
            r1 = 0
        L_0x00b7:
            short r10 = (short) r1
            r9.write(r10)
            if (r11 == 0) goto L_0x0106
            int r9 = r8.nextParam
            int r9 = r9 + r5
        L_0x00c0:
            int r10 = r11.length
            if (r9 >= r10) goto L_0x0106
            r10 = r11[r9]
            java.lang.String r10 = r10.name
            if (r10 == 0) goto L_0x00e1
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r12 = r11[r9]
            java.lang.String r12 = r12.name
            int r12 = r12.length()
            byte r12 = (byte) r12
            r10.write(r12)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r12 = r11[r9]
            java.lang.String r12 = r12.name
            r10.write(r12)
            goto L_0x00e6
        L_0x00e1:
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r10.write(r2)
        L_0x00e6:
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r12 = r11[r9]
            boolean r12 = r12.isOutput
            byte r12 = (byte) r12
            r10.write(r12)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            net.sourceforge.jtds.jdbc.JtdsConnection r12 = r8.connection
            net.sourceforge.jtds.jdbc.CharsetInfo r12 = r12.getCharsetInfo()
            net.sourceforge.jtds.jdbc.JtdsConnection r0 = r8.connection
            byte[] r0 = r0.getCollation()
            r1 = r11[r9]
            net.sourceforge.jtds.jdbc.TdsData.writeParam(r10, r12, r0, r1)
            int r9 = r9 + 1
            goto L_0x00c0
        L_0x0106:
            if (r13 != 0) goto L_0x0129
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            r10 = -128(0xffffffffffffff80, float:NaN)
            r9.write(r10)
            goto L_0x0129
        L_0x0110:
            int r10 = r9.length()
            if (r10 <= 0) goto L_0x0129
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r10.setPacketType(r5)
            net.sourceforge.jtds.jdbc.RequestStream r10 = r8.out
            r10.write(r9)
            if (r13 != 0) goto L_0x0129
            net.sourceforge.jtds.jdbc.RequestStream r9 = r8.out
            java.lang.String r10 = " "
            r9.write(r10)
        L_0x0129:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.executeSQL70(java.lang.String, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[], boolean, boolean):void");
    }

    private void setRowCountAndTextSize(int i, int i2) throws SQLException {
        boolean z = i >= 0 && i != this.connection.getRowCount();
        boolean z2 = i2 >= 0 && i2 != this.connection.getTextSize();
        if (z || z2) {
            try {
                StringBuilder sb = new StringBuilder(64);
                if (z) {
                    sb.append("SET ROWCOUNT ");
                    sb.append(i);
                }
                if (z2) {
                    sb.append(" SET TEXTSIZE ");
                    sb.append(i2 == 0 ? ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED : i2);
                }
                this.out.setPacketType(1);
                this.out.write(sb.toString());
                this.out.flush();
                this.endOfResponse = false;
                this.endOfResults = true;
                wait(0);
                clearResponseQueue();
                this.messages.checkErrors();
                this.connection.setRowCount(i);
                this.connection.setTextSize(i2);
            } catch (IOException e) {
                throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "08S01");
            }
        }
    }

    private void wait(int i) throws IOException, SQLException {
        String str = "HYT00";
        String str2 = "error.generic.timeout";
        Object obj = null;
        if (i > 0) {
            try {
                obj = TimerThread.getInstance().setTimer(i * 1000, new TimerListener() {
                    public void timerExpired() {
                        TdsCore.this.cancel(true);
                    }
                });
            } catch (Throwable th) {
                if (0 == 0 || TimerThread.getInstance().cancelTimer(null)) {
                    throw th;
                }
                throw new SQLTimeoutException(Messages.get(str2), str);
            }
        }
        this.f122in.peek();
        if (obj != null && !TimerThread.getInstance().cancelTimer(obj)) {
            throw new SQLTimeoutException(Messages.get(str2), str);
        }
    }

    public void cleanUp() {
        if (this.endOfResponse) {
            this.returnParam = null;
            this.parameters = null;
            this.columns = null;
            this.rowData = null;
            this.tables = null;
            this.computedColumns = null;
            this.computedRowData = null;
            this.messages.clearWarnings();
        }
    }

    public SQLDiagnostic getMessages() {
        return this.messages;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] getMACAddress(java.lang.String r7) {
        /*
            r0 = 6
            byte[] r1 = new byte[r0]
            r2 = 0
            if (r7 == 0) goto L_0x0027
            int r3 = r7.length()
            r4 = 12
            if (r3 != r4) goto L_0x0027
            r3 = 0
            r4 = 0
        L_0x0010:
            if (r3 >= r0) goto L_0x0025
            int r5 = r4 + 2
            java.lang.String r4 = r7.substring(r4, r5)     // Catch:{ Exception -> 0x0027 }
            r6 = 16
            int r4 = java.lang.Integer.parseInt(r4, r6)     // Catch:{ Exception -> 0x0027 }
            byte r4 = (byte) r4     // Catch:{ Exception -> 0x0027 }
            r1[r3] = r4     // Catch:{ Exception -> 0x0027 }
            int r3 = r3 + 1
            r4 = r5
            goto L_0x0010
        L_0x0025:
            r7 = 1
            goto L_0x0028
        L_0x0027:
            r7 = 0
        L_0x0028:
            if (r7 != 0) goto L_0x002d
            java.util.Arrays.fill(r1, r2)
        L_0x002d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.getMACAddress(java.lang.String):byte[]");
    }

    private static String getHostName() {
        String str = "UNKNOWN";
        String str2 = hostName;
        if (str2 != null) {
            return str2;
        }
        try {
            String upperCase = InetAddress.getLocalHost().getHostName().toUpperCase();
            int indexOf = upperCase.indexOf(46);
            if (indexOf >= 0) {
                upperCase = upperCase.substring(0, indexOf);
            }
            if (upperCase.length() == 0) {
                hostName = str;
                return str;
            }
            try {
                Integer.parseInt(upperCase);
                hostName = str;
                return str;
            } catch (NumberFormatException unused) {
                hostName = upperCase;
                return upperCase;
            }
        } catch (UnknownHostException unused2) {
            hostName = str;
            return str;
        }
    }

    private static String tds7CryptPass(String str) {
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i) ^ 23130;
            cArr[i] = (char) (((charAt << 4) & 61680) | ((charAt >> 4) & 3855));
        }
        return new String(cArr);
    }

    private void tdsComputedResultToken() throws IOException, ProtocolException {
        short readShort = this.f122in.readShort();
        this.computedColumns = new ColInfo[readShort];
        this.f122in.readShort();
        this.f122in.skip(this.f122in.read() * 2);
        for (int i = 0; i < readShort; i++) {
            ColInfo colInfo = new ColInfo();
            this.computedColumns[i] = colInfo;
            int read = this.f122in.read();
            if (read == 9) {
                colInfo.name = "count_big";
            } else if (read == 75) {
                colInfo.name = "count";
            } else if (read == 77) {
                colInfo.name = "sum";
            } else if (read == 79) {
                colInfo.name = "avg";
            } else if (read == 81) {
                colInfo.name = "min";
            } else if (read != 82) {
                switch (read) {
                    case 48:
                        colInfo.name = "stdev";
                        break;
                    case 49:
                        colInfo.name = "stdevp";
                        break;
                    case 50:
                        colInfo.name = "var";
                        break;
                    case 51:
                        colInfo.name = "varp";
                        break;
                    default:
                        StringBuilder sb = new StringBuilder();
                        sb.append("unsupported aggregation type 0x");
                        sb.append(Integer.toHexString(read));
                        throw new ProtocolException(sb.toString());
                }
            } else {
                colInfo.name = "max";
            }
            boolean z = true;
            int readShort2 = this.f122in.readShort() - 1;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(colInfo.name);
            sb2.append("(");
            sb2.append(this.columns[readShort2].name);
            sb2.append(")");
            colInfo.name = sb2.toString();
            colInfo.realName = colInfo.name;
            colInfo.tableName = this.columns[readShort2].tableName;
            colInfo.catalog = this.columns[readShort2].catalog;
            colInfo.schema = this.columns[readShort2].schema;
            colInfo.userType = this.f122in.readShort();
            short readShort3 = this.f122in.readShort();
            colInfo.nullable = (readShort3 & 1) != 0 ? 1 : 0;
            colInfo.isCaseSensitive = (readShort3 & 2) != 0;
            colInfo.isIdentity = (readShort3 & 16) != 0;
            if ((readShort3 & 12) == 0) {
                z = false;
            }
            colInfo.isWriteable = z;
            TdsData.readType(this.f122in, colInfo);
            this.f122in.readString(this.f122in.read());
        }
    }

    private void tdsComputedRowToken() throws IOException, ProtocolException, SQLException {
        this.f122in.readShort();
        this.computedRowData = new Object[this.computedColumns.length];
        int i = 0;
        while (true) {
            Object[] objArr = this.computedRowData;
            if (i < objArr.length) {
                objArr[i] = TdsData.readData(this.connection, this.f122in, this.computedColumns[i]);
                i++;
            } else {
                return;
            }
        }
    }

    static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private byte[] createGssToken() throws GSSException, UnknownHostException {
        GSSManager instance = GSSManager.getInstance();
        Oid oid = new Oid("1.2.840.113554.1.2.2");
        Oid oid2 = new Oid("1.2.840.113554.1.2.2.1");
        String canonicalHostName = InetAddress.getByName(this.socket.getHost()).getCanonicalHostName();
        int port = this.socket.getPort();
        StringBuilder sb = new StringBuilder();
        sb.append("MSSQLSvc/");
        sb.append(canonicalHostName);
        sb.append(":");
        sb.append(port);
        GSSName createName = instance.createName(sb.toString(), oid2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("GSS: Using SPN ");
        sb2.append(createName);
        Logger.println(sb2.toString());
        GSSContext createContext = instance.createContext(createName, oid, null, 0);
        this._gssContext = createContext;
        createContext.requestMutualAuth(true);
        byte[] initSecContext = this._gssContext.initSecContext(new byte[0], 0, 0);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("GSS: Created GSS token (length: ");
        sb3.append(initSecContext.length);
        sb3.append(")");
        Logger.println(sb3.toString());
        return initSecContext;
    }
}
