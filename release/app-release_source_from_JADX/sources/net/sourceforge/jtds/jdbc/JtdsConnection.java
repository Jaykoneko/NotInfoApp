package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import net.sourceforge.jtds.jdbc.cache.ProcedureCache;
import net.sourceforge.jtds.jdbc.cache.StatementCache;
import net.sourceforge.jtds.util.Logger;

public class JtdsConnection implements Connection {
    private static final String SQL_SERVER_65_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select csid from master.dbo.syscharsets, master.dbo.sysconfigures where config=1123 and id = value)";
    private static final String SQL_SERVER_INITIAL_SQL = "SELECT @@MAX_PRECISION\r\nSET TRANSACTION ISOLATION LEVEL READ COMMITTED\r\nSET IMPLICIT_TRANSACTIONS OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    private static final String SYBASE_INITIAL_SQL = "SET TRANSACTION ISOLATION LEVEL 1\r\nSET CHAINED OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    private static final String SYBASE_SERVER_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select value from master.dbo.sysconfigures where config=131)";
    public static final int TRANSACTION_SNAPSHOT = 4096;
    private static int[] connections = new int[1];
    private static Integer processId;
    private String appName;
    private boolean autoCommit = true;
    private final TdsCore baseTds;
    private int batchSize;
    private String bindAddress;
    private File bufferDir;
    private int bufferMaxMemory;
    private int bufferMinPackets;
    private TdsCore cachedTds;
    private CharsetInfo charsetInfo;
    private boolean charsetSpecified;
    private boolean closed;
    private byte[] collation;
    private String currentDatabase;
    private int cursorSequenceNo = 1;
    private int databaseMajorVersion;
    private int databaseMinorVersion;
    private String databaseName;
    private String databaseProductName;
    private String databaseProductVersion;
    private String domainName;
    private String instanceName;
    private String language;
    private boolean lastUpdateCount;
    private long lobBuffer;
    private int loginTimeout;
    private String macAddress;
    private int maxPrecision = 38;
    private int maxStatements;
    private final SQLDiagnostic messages;
    private final Semaphore mutex = new Semaphore(1);
    private boolean namedPipe;
    private int netPacketSize = 512;
    private int packetSize;
    private String password;
    private int portNumber;
    private int prepareSql;
    private final ArrayList procInTran = new ArrayList();
    private String progName;
    private boolean readOnly;
    private int rowCount;
    private int savepointId;
    private Map savepointProcInTran;
    private ArrayList savepoints;
    private String serverCharset;
    private String serverName;
    private int serverType;
    /* access modifiers changed from: private */
    public final SharedSocket socket;
    private boolean socketKeepAlive;
    private int socketTimeout;
    private int spSequenceNo = 1;
    private String ssl;
    private StatementCache statementCache;
    private final ArrayList statements = new ArrayList();
    private int sybaseInfo;
    private boolean tcpNoDelay = true;
    private int tdsVersion;
    private int textSize;
    private int transactionIsolation = 2;
    private final String url;
    private boolean useCursors;
    private boolean useJCIFS;
    private boolean useKerberos = false;
    private boolean useLOBs;
    private boolean useMetadataCache;
    private boolean useNTLMv2 = false;
    private boolean useUnicode = true;
    private String user;
    private String wsid;
    private boolean xaEmulation = true;
    private int xaState;
    private boolean xaTransaction;
    private Object xid;

    private JtdsConnection() {
        synchronized (connections) {
            int[] iArr = connections;
            iArr[0] = iArr[0] + 1;
        }
        this.url = null;
        this.socket = null;
        this.baseTds = null;
        this.messages = null;
    }

    /* JADX INFO: used method not loaded: net.sourceforge.jtds.jdbc.Support.linkException(java.sql.SQLException, java.lang.Throwable):null, types can be incorrect */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x020b, code lost:
        throw net.sourceforge.jtds.jdbc.Support.linkException(new java.sql.SQLException(net.sourceforge.jtds.jdbc.Messages.get("error.connection.timeout"), "HYT01"), (java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x023e, code lost:
        if (r5 != null) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x0240, code lost:
        net.sourceforge.jtds.util.TimerThread.getInstance().cancelTimer(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0248, code lost:
        close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01b4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01b6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01b8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01ba, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01bc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01bd, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01e6, code lost:
        throw net.sourceforge.jtds.jdbc.Support.linkException(new java.sql.SQLException(net.sourceforge.jtds.jdbc.Messages.get("error.connection.timeout"), "HYT01"), (java.lang.Throwable) r0);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0248  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01bc A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v6 java.lang.Object) = (r5v2 java.lang.Object), (r5v7 java.lang.Object) binds: [B:20:0x009d, B:68:0x0198] A[DONT_GENERATE, DONT_INLINE], Splitter:B:20:0x009d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    JtdsConnection(java.lang.String r22, java.util.Properties r23) throws java.sql.SQLException {
        /*
            r21 = this;
            r1 = r21
            r21.<init>()
            r0 = 512(0x200, float:7.175E-43)
            r1.netPacketSize = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.statements = r0
            r2 = 2
            r1.transactionIsolation = r2
            r3 = 1
            r1.autoCommit = r3
            r0 = 38
            r1.maxPrecision = r0
            r1.spSequenceNo = r3
            r1.cursorSequenceNo = r3
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.procInTran = r0
            r1.useUnicode = r3
            r1.tcpNoDelay = r3
            r1.xaEmulation = r3
            net.sourceforge.jtds.jdbc.Semaphore r0 = new net.sourceforge.jtds.jdbc.Semaphore
            r4 = 1
            r0.<init>(r4)
            r1.mutex = r0
            r4 = 0
            r1.useNTLMv2 = r4
            r1.useKerberos = r4
            int[] r5 = connections
            monitor-enter(r5)
            int[] r0 = connections     // Catch:{ all -> 0x024c }
            r6 = r0[r4]     // Catch:{ all -> 0x024c }
            int r6 = r6 + r3
            r0[r4] = r6     // Catch:{ all -> 0x024c }
            monitor-exit(r5)     // Catch:{ all -> 0x024c }
            r0 = r22
            r1.url = r0
            r0 = r23
            r1.unpackProperties(r0)
            net.sourceforge.jtds.jdbc.SQLDiagnostic r0 = new net.sourceforge.jtds.jdbc.SQLDiagnostic
            int r5 = r1.serverType
            r0.<init>(r5)
            r1.messages = r0
            java.lang.String r0 = r1.instanceName
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0090
            boolean r0 = r1.namedPipe
            if (r0 != 0) goto L_0x0090
            net.sourceforge.jtds.jdbc.MSSqlServerInfo r0 = new net.sourceforge.jtds.jdbc.MSSqlServerInfo     // Catch:{ SQLException -> 0x0072 }
            java.lang.String r5 = r1.serverName     // Catch:{ SQLException -> 0x0072 }
            r0.<init>(r5)     // Catch:{ SQLException -> 0x0072 }
            java.lang.String r5 = r1.instanceName     // Catch:{ SQLException -> 0x0072 }
            int r0 = r0.getPortForInstance(r5)     // Catch:{ SQLException -> 0x0072 }
            r1.portNumber = r0     // Catch:{ SQLException -> 0x0072 }
            goto L_0x0077
        L_0x0072:
            r0 = move-exception
            int r5 = r1.portNumber
            if (r5 <= 0) goto L_0x008f
        L_0x0077:
            int r0 = r1.portNumber
            r5 = -1
            if (r0 == r5) goto L_0x007d
            goto L_0x0090
        L_0x007d:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r2 = "error.msinfo.badinst"
            java.lang.String r3 = r1.serverName
            java.lang.String r4 = r1.instanceName
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2, r3, r4)
            java.lang.String r3 = "08003"
            r0.<init>(r2, r3)
            throw r0
        L_0x008f:
            throw r0
        L_0x0090:
            int r0 = r1.bufferMaxMemory
            int r0 = r0 * 1024
            net.sourceforge.jtds.jdbc.SharedSocket.setMemoryBudget(r0)
            int r0 = r1.bufferMinPackets
            net.sourceforge.jtds.jdbc.SharedSocket.setMinMemPkts(r0)
            r5 = 0
            int r0 = r1.loginTimeout     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 <= 0) goto L_0x00b2
            net.sourceforge.jtds.util.TimerThread r0 = net.sourceforge.jtds.util.TimerThread.getInstance()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r6 = r1.loginTimeout     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r6 = r6 * 1000
            net.sourceforge.jtds.jdbc.JtdsConnection$1 r7 = new net.sourceforge.jtds.jdbc.JtdsConnection$1     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r7.<init>()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.Object r5 = r0.setTimer(r6, r7)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x00b2:
            boolean r0 = r1.namedPipe     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 == 0) goto L_0x00bd
            net.sourceforge.jtds.jdbc.SharedSocket r0 = r1.createNamedPipe(r1)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.socket = r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            goto L_0x00c4
        L_0x00bd:
            net.sourceforge.jtds.jdbc.SharedSocket r0 = new net.sourceforge.jtds.jdbc.SharedSocket     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r0.<init>(r1)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.socket = r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x00c4:
            java.lang.String r0 = r1.macAddress     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r6 = "000000000000"
            boolean r0 = r0.equals(r6)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 == 0) goto L_0x00db
            net.sourceforge.jtds.jdbc.SharedSocket r0 = r1.socket     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r0 = r0.getMAC()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 == 0) goto L_0x00d7
            goto L_0x00d9
        L_0x00d7:
            java.lang.String r0 = r1.macAddress     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x00d9:
            r1.macAddress = r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x00db:
            if (r5 == 0) goto L_0x00f5
            net.sourceforge.jtds.util.TimerThread r0 = net.sourceforge.jtds.util.TimerThread.getInstance()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            boolean r0 = r0.hasExpired(r5)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 != 0) goto L_0x00e8
            goto L_0x00f5
        L_0x00e8:
            net.sourceforge.jtds.jdbc.SharedSocket r0 = r1.socket     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r0.forceClose()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.io.IOException r0 = new java.io.IOException     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r2 = "Login timed out"
            r0.<init>(r2)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            throw r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x00f5:
            boolean r0 = r1.charsetSpecified     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r0 == 0) goto L_0x00ff
            java.lang.String r0 = r1.serverCharset     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.loadCharset(r0)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            goto L_0x0108
        L_0x00ff:
            java.lang.String r0 = "iso_1"
            r1.loadCharset(r0)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r0 = ""
            r1.serverCharset = r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x0108:
            net.sourceforge.jtds.jdbc.TdsCore r0 = new net.sourceforge.jtds.jdbc.TdsCore     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r6 = r1.messages     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r0.<init>(r1, r6)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.baseTds = r0     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r6 = r1.tdsVersion     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r7 = 4
            if (r6 < r7) goto L_0x0121
            boolean r6 = r1.namedPipe     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r6 != 0) goto L_0x0121
            java.lang.String r6 = r1.instanceName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r7 = r1.ssl     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r0.negotiateSSL(r6, r7)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x0121:
            net.sourceforge.jtds.jdbc.TdsCore r8 = r1.baseTds     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r9 = r1.serverName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r10 = r1.databaseName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r11 = r1.user     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r12 = r1.password     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r13 = r1.domainName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r14 = r1.serverCharset     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r15 = r1.appName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r0 = r1.progName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r6 = r1.wsid     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r7 = r1.language     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r4 = r1.macAddress     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r3 = r1.packetSize     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r16 = r0
            r17 = r6
            r18 = r7
            r19 = r4
            r20 = r3
            r8.login(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            net.sourceforge.jtds.jdbc.SQLDiagnostic r0 = r1.messages     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.sql.SQLWarning r0 = r0.warnings     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            net.sourceforge.jtds.jdbc.TdsCore r3 = r1.baseTds     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r3 = r3.getTdsVersion()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.tdsVersion = r3     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r4 = 3
            if (r3 >= r4) goto L_0x0164
            java.lang.String r3 = r1.databaseName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r3 = r3.length()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r3 <= 0) goto L_0x0164
            java.lang.String r3 = r1.databaseName     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.setCatalog(r3)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x0164:
            java.lang.String r3 = r1.serverCharset     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r3 == 0) goto L_0x0170
            java.lang.String r3 = r1.serverCharset     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            int r3 = r3.length()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r3 != 0) goto L_0x017b
        L_0x0170:
            byte[] r3 = r1.collation     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r3 != 0) goto L_0x017b
            java.lang.String r3 = r21.determineServerCharset()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            r1.loadCharset(r3)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
        L_0x017b:
            int r3 = r1.serverType     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r3 != r2) goto L_0x0187
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r3 = "SET TRANSACTION ISOLATION LEVEL 1\r\nSET CHAINED OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647"
            r2.submitSQL(r3)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            goto L_0x01a6
        L_0x0187:
            java.sql.Statement r2 = r21.createStatement()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            java.lang.String r3 = "SELECT @@MAX_PRECISION\r\nSET TRANSACTION ISOLATION LEVEL READ COMMITTED\r\nSET IMPLICIT_TRANSACTIONS OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647"
            java.sql.ResultSet r3 = r2.executeQuery(r3)     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            boolean r4 = r3.next()     // Catch:{ UnknownHostException -> 0x0222, IOException -> 0x01e8, SQLException -> 0x01c3, RuntimeException -> 0x01c0, all -> 0x01bc }
            if (r4 == 0) goto L_0x019f
            r4 = 1
            byte r6 = r3.getByte(r4)     // Catch:{ UnknownHostException -> 0x01ba, IOException -> 0x01b8, SQLException -> 0x01b6, RuntimeException -> 0x01b4, all -> 0x01bc }
            r1.maxPrecision = r6     // Catch:{ UnknownHostException -> 0x01ba, IOException -> 0x01b8, SQLException -> 0x01b6, RuntimeException -> 0x01b4, all -> 0x01bc }
            goto L_0x01a0
        L_0x019f:
            r4 = 1
        L_0x01a0:
            r3.close()     // Catch:{ UnknownHostException -> 0x01ba, IOException -> 0x01b8, SQLException -> 0x01b6, RuntimeException -> 0x01b4, all -> 0x01bc }
            r2.close()     // Catch:{ UnknownHostException -> 0x01ba, IOException -> 0x01b8, SQLException -> 0x01b6, RuntimeException -> 0x01b4, all -> 0x01bc }
        L_0x01a6:
            if (r5 == 0) goto L_0x01af
            net.sourceforge.jtds.util.TimerThread r2 = net.sourceforge.jtds.util.TimerThread.getInstance()
            r2.cancelTimer(r5)
        L_0x01af:
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r1.messages
            r2.warnings = r0
            return
        L_0x01b4:
            r0 = move-exception
            goto L_0x01c2
        L_0x01b6:
            r0 = move-exception
            goto L_0x01c5
        L_0x01b8:
            r0 = move-exception
            goto L_0x01ea
        L_0x01ba:
            r0 = move-exception
            goto L_0x0224
        L_0x01bc:
            r0 = move-exception
            r3 = 0
            goto L_0x023c
        L_0x01c0:
            r0 = move-exception
            r4 = 1
        L_0x01c2:
            throw r0     // Catch:{ all -> 0x023a }
        L_0x01c3:
            r0 = move-exception
            r4 = 1
        L_0x01c5:
            int r2 = r1.loginTimeout     // Catch:{ all -> 0x023a }
            if (r2 <= 0) goto L_0x01e7
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "socket closed"
            int r2 = r2.indexOf(r3)     // Catch:{ all -> 0x023a }
            if (r2 < 0) goto L_0x01e7
            java.sql.SQLException r2 = new java.sql.SQLException     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "error.connection.timeout"
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3)     // Catch:{ all -> 0x023a }
            java.lang.String r6 = "HYT01"
            r2.<init>(r3, r6)     // Catch:{ all -> 0x023a }
            java.sql.SQLException r0 = net.sourceforge.jtds.jdbc.Support.linkException(r2, r0)     // Catch:{ all -> 0x023a }
            throw r0     // Catch:{ all -> 0x023a }
        L_0x01e7:
            throw r0     // Catch:{ all -> 0x023a }
        L_0x01e8:
            r0 = move-exception
            r4 = 1
        L_0x01ea:
            int r2 = r1.loginTimeout     // Catch:{ all -> 0x023a }
            if (r2 <= 0) goto L_0x020c
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "timed out"
            int r2 = r2.indexOf(r3)     // Catch:{ all -> 0x023a }
            if (r2 < 0) goto L_0x020c
            java.sql.SQLException r2 = new java.sql.SQLException     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "error.connection.timeout"
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3)     // Catch:{ all -> 0x023a }
            java.lang.String r6 = "HYT01"
            r2.<init>(r3, r6)     // Catch:{ all -> 0x023a }
            java.sql.SQLException r0 = net.sourceforge.jtds.jdbc.Support.linkException(r2, r0)     // Catch:{ all -> 0x023a }
            throw r0     // Catch:{ all -> 0x023a }
        L_0x020c:
            java.sql.SQLException r2 = new java.sql.SQLException     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "error.connection.ioerror"
            java.lang.String r6 = r0.getMessage()     // Catch:{ all -> 0x023a }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3, r6)     // Catch:{ all -> 0x023a }
            java.lang.String r6 = "08S01"
            r2.<init>(r3, r6)     // Catch:{ all -> 0x023a }
            java.sql.SQLException r0 = net.sourceforge.jtds.jdbc.Support.linkException(r2, r0)     // Catch:{ all -> 0x023a }
            throw r0     // Catch:{ all -> 0x023a }
        L_0x0222:
            r0 = move-exception
            r4 = 1
        L_0x0224:
            java.sql.SQLException r2 = new java.sql.SQLException     // Catch:{ all -> 0x023a }
            java.lang.String r3 = "error.connection.badhost"
            java.lang.String r6 = r0.getMessage()     // Catch:{ all -> 0x023a }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3, r6)     // Catch:{ all -> 0x023a }
            java.lang.String r6 = "08S03"
            r2.<init>(r3, r6)     // Catch:{ all -> 0x023a }
            java.sql.SQLException r0 = net.sourceforge.jtds.jdbc.Support.linkException(r2, r0)     // Catch:{ all -> 0x023a }
            throw r0     // Catch:{ all -> 0x023a }
        L_0x023a:
            r0 = move-exception
            r3 = 1
        L_0x023c:
            if (r3 != 0) goto L_0x0248
            if (r5 == 0) goto L_0x024b
            net.sourceforge.jtds.util.TimerThread r2 = net.sourceforge.jtds.util.TimerThread.getInstance()
            r2.cancelTimer(r5)
            goto L_0x024b
        L_0x0248:
            r21.close()
        L_0x024b:
            throw r0
        L_0x024c:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x024c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.<init>(java.lang.String, java.util.Properties):void");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0091 A[EDGE_INSN: B:29:0x0091->B:24:0x0091 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private net.sourceforge.jtds.jdbc.SharedSocket createNamedPipe(net.sourceforge.jtds.jdbc.JtdsConnection r13) throws java.io.IOException {
        /*
            r12 = this;
            int r0 = r13.getLoginTimeout()
            long r0 = (long) r0
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x000c
            goto L_0x000e
        L_0x000c:
            r0 = 20
        L_0x000e:
            r2 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 * r2
            long r2 = java.lang.System.currentTimeMillis()
            java.util.Random r4 = new java.util.Random
            r4.<init>(r2)
            boolean r5 = net.sourceforge.jtds.jdbc.Support.isWindowsOS()
            r6 = 0
            r7 = 0
            r8 = r7
        L_0x0022:
            if (r5 == 0) goto L_0x0030
            boolean r9 = r13.getUseJCIFS()     // Catch:{ IOException -> 0x0037 }
            if (r9 != 0) goto L_0x0030
            net.sourceforge.jtds.jdbc.SharedLocalNamedPipe r9 = new net.sourceforge.jtds.jdbc.SharedLocalNamedPipe     // Catch:{ IOException -> 0x0037 }
            r9.<init>(r13)     // Catch:{ IOException -> 0x0037 }
            goto L_0x0035
        L_0x0030:
            net.sourceforge.jtds.jdbc.SharedNamedPipe r9 = new net.sourceforge.jtds.jdbc.SharedNamedPipe     // Catch:{ IOException -> 0x0037 }
            r9.<init>(r13)     // Catch:{ IOException -> 0x0037 }
        L_0x0035:
            r7 = r9
            goto L_0x0086
        L_0x0037:
            r8 = move-exception
            int r6 = r6 + 1
            java.lang.String r9 = r8.getMessage()
            java.lang.String r9 = r9.toLowerCase()
            java.lang.String r10 = "all pipe instances are busy"
            int r9 = r9.indexOf(r10)
            if (r9 < 0) goto L_0x009f
            r9 = 800(0x320, float:1.121E-42)
            int r9 = r4.nextInt(r9)
            int r9 = r9 + 200
            boolean r10 = net.sourceforge.jtds.util.Logger.isActive()
            if (r10 == 0) goto L_0x0080
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Retry #"
            r10.append(r11)
            r10.append(r6)
            java.lang.String r11 = " Wait "
            r10.append(r11)
            r10.append(r9)
            java.lang.String r11 = " ms: "
            r10.append(r11)
            java.lang.String r11 = r8.getMessage()
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            net.sourceforge.jtds.util.Logger.println(r10)
        L_0x0080:
            long r9 = (long) r9
            java.lang.Thread.sleep(r9)     // Catch:{ InterruptedException -> 0x0085 }
            goto L_0x0086
        L_0x0085:
        L_0x0086:
            if (r7 != 0) goto L_0x0091
            long r9 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r2
            int r11 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r11 < 0) goto L_0x0022
        L_0x0091:
            if (r7 == 0) goto L_0x0094
            return r7
        L_0x0094:
            java.io.IOException r13 = new java.io.IOException
            java.lang.String r0 = "Connection timed out to named pipe"
            r13.<init>(r0)
            net.sourceforge.jtds.jdbc.Support.linkException(r13, r8)
            throw r13
        L_0x009f:
            goto L_0x00a1
        L_0x00a0:
            throw r8
        L_0x00a1:
            goto L_0x00a0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.createNamedPipe(net.sourceforge.jtds.jdbc.JtdsConnection):net.sourceforge.jtds.jdbc.SharedSocket");
    }

    /* access modifiers changed from: 0000 */
    public SharedSocket getSocket() {
        return this.socket;
    }

    /* access modifiers changed from: 0000 */
    public int getTdsVersion() {
        return this.tdsVersion;
    }

    /* access modifiers changed from: 0000 */
    public String getProcName() {
        StringBuilder sb = new StringBuilder();
        sb.append("000000");
        int i = this.spSequenceNo;
        this.spSequenceNo = i + 1;
        sb.append(Integer.toHexString(i).toUpperCase());
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("#jtds");
        sb3.append(sb2.substring(sb2.length() - 6, sb2.length()));
        return sb3.toString();
    }

    /* access modifiers changed from: 0000 */
    public synchronized String getCursorName() {
        StringBuilder sb;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("000000");
        int i = this.cursorSequenceNo;
        this.cursorSequenceNo = i + 1;
        sb2.append(Integer.toHexString(i).toUpperCase());
        String sb3 = sb2.toString();
        sb = new StringBuilder();
        sb.append("_jtds");
        sb.append(sb3.substring(sb3.length() - 6, sb3.length()));
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0050, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0145, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String prepareSQL(net.sourceforge.jtds.jdbc.JtdsPreparedStatement r15, java.lang.String r16, net.sourceforge.jtds.jdbc.ParamInfo[] r17, boolean r18, boolean r19) throws java.sql.SQLException {
        /*
            r14 = this;
            r1 = r14
            r0 = r15
            r8 = r17
            monitor-enter(r14)
            int r2 = r1.prepareSql     // Catch:{ all -> 0x0146 }
            r3 = 0
            if (r2 == 0) goto L_0x0144
            int r2 = r1.prepareSql     // Catch:{ all -> 0x0146 }
            r9 = 2
            if (r2 != r9) goto L_0x0011
            goto L_0x0144
        L_0x0011:
            int r2 = r1.serverType     // Catch:{ all -> 0x0146 }
            if (r2 != r9) goto L_0x0023
            int r2 = r1.tdsVersion     // Catch:{ all -> 0x0146 }
            if (r2 == r9) goto L_0x001b
            monitor-exit(r14)
            return r3
        L_0x001b:
            if (r18 == 0) goto L_0x001f
            monitor-exit(r14)
            return r3
        L_0x001f:
            if (r19 == 0) goto L_0x0023
            monitor-exit(r14)
            return r3
        L_0x0023:
            r2 = 0
        L_0x0024:
            int r4 = r8.length     // Catch:{ all -> 0x0146 }
            r10 = 1
            if (r2 >= r4) goto L_0x0067
            r4 = r8[r2]     // Catch:{ all -> 0x0146 }
            boolean r4 = r4.isSet     // Catch:{ all -> 0x0146 }
            if (r4 == 0) goto L_0x0054
            r4 = r8[r2]     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r14, r4)     // Catch:{ all -> 0x0146 }
            int r4 = r1.serverType     // Catch:{ all -> 0x0146 }
            if (r4 != r9) goto L_0x0051
            java.lang.String r4 = "text"
            r5 = r8[r2]     // Catch:{ all -> 0x0146 }
            java.lang.String r5 = r5.sqlType     // Catch:{ all -> 0x0146 }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x0146 }
            if (r4 != 0) goto L_0x004f
            java.lang.String r4 = "image"
            r5 = r8[r2]     // Catch:{ all -> 0x0146 }
            java.lang.String r5 = r5.sqlType     // Catch:{ all -> 0x0146 }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x0146 }
            if (r4 == 0) goto L_0x0051
        L_0x004f:
            monitor-exit(r14)
            return r3
        L_0x0051:
            int r2 = r2 + 1
            goto L_0x0024
        L_0x0054:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ all -> 0x0146 }
            java.lang.String r3 = "error.prepare.paramnotset"
            int r2 = r2 + r10
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch:{ all -> 0x0146 }
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r3, r2)     // Catch:{ all -> 0x0146 }
            java.lang.String r3 = "07000"
            r0.<init>(r2, r3)     // Catch:{ all -> 0x0146 }
            throw r0     // Catch:{ all -> 0x0146 }
        L_0x0067:
            int r4 = r1.serverType     // Catch:{ all -> 0x0146 }
            java.lang.String r5 = r14.getCatalog()     // Catch:{ all -> 0x0146 }
            boolean r6 = r1.autoCommit     // Catch:{ all -> 0x0146 }
            r2 = r16
            r3 = r17
            r7 = r19
            java.lang.String r11 = net.sourceforge.jtds.jdbc.Support.getStatementKey(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.cache.StatementCache r2 = r1.statementCache     // Catch:{ all -> 0x0146 }
            java.lang.Object r2 = r2.get(r11)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ProcEntry r2 = (net.sourceforge.jtds.jdbc.ProcEntry) r2     // Catch:{ all -> 0x0146 }
            if (r2 == 0) goto L_0x00a6
            java.util.Collection r3 = r0.handles     // Catch:{ all -> 0x0146 }
            if (r3 == 0) goto L_0x0092
            java.util.Collection r3 = r0.handles     // Catch:{ all -> 0x0146 }
            boolean r3 = r3.contains(r2)     // Catch:{ all -> 0x0146 }
            if (r3 == 0) goto L_0x0092
            r2.release()     // Catch:{ all -> 0x0146 }
        L_0x0092:
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r2.getColMetaData()     // Catch:{ all -> 0x0146 }
            r15.setColMetaData(r3)     // Catch:{ all -> 0x0146 }
            int r3 = r1.serverType     // Catch:{ all -> 0x0146 }
            if (r3 != r9) goto L_0x012c
            net.sourceforge.jtds.jdbc.ParamInfo[] r3 = r2.getParamMetaData()     // Catch:{ all -> 0x0146 }
            r15.setParamMetaData(r3)     // Catch:{ all -> 0x0146 }
            goto L_0x012c
        L_0x00a6:
            net.sourceforge.jtds.jdbc.ProcEntry r12 = new net.sourceforge.jtds.jdbc.ProcEntry     // Catch:{ all -> 0x0146 }
            r12.<init>()     // Catch:{ all -> 0x0146 }
            int r2 = r1.serverType     // Catch:{ all -> 0x0146 }
            r13 = 4
            if (r2 != r10) goto L_0x00f0
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ all -> 0x0146 }
            int r6 = r15.getResultSetType()     // Catch:{ all -> 0x0146 }
            int r7 = r15.getResultSetConcurrency()     // Catch:{ all -> 0x0146 }
            r3 = r16
            r4 = r17
            r5 = r19
            java.lang.String r2 = r2.microsoftPrepare(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0146 }
            r12.setName(r2)     // Catch:{ all -> 0x0146 }
            java.lang.String r2 = r12.toString()     // Catch:{ all -> 0x0146 }
            if (r2 != 0) goto L_0x00d1
            r12.setType(r13)     // Catch:{ all -> 0x0146 }
            goto L_0x0128
        L_0x00d1:
            int r2 = r1.prepareSql     // Catch:{ all -> 0x0146 }
            if (r2 != r10) goto L_0x00d9
            r12.setType(r10)     // Catch:{ all -> 0x0146 }
            goto L_0x0128
        L_0x00d9:
            if (r19 == 0) goto L_0x00dc
            r9 = 3
        L_0x00dc:
            r12.setType(r9)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ColInfo[] r2 = r2.getColumns()     // Catch:{ all -> 0x0146 }
            r12.setColMetaData(r2)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ColInfo[] r2 = r12.getColMetaData()     // Catch:{ all -> 0x0146 }
            r15.setColMetaData(r2)     // Catch:{ all -> 0x0146 }
            goto L_0x0128
        L_0x00f0:
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ all -> 0x0146 }
            r3 = r16
            java.lang.String r2 = r2.sybasePrepare(r3, r8)     // Catch:{ all -> 0x0146 }
            r12.setName(r2)     // Catch:{ all -> 0x0146 }
            java.lang.String r2 = r12.toString()     // Catch:{ all -> 0x0146 }
            if (r2 != 0) goto L_0x0105
            r12.setType(r13)     // Catch:{ all -> 0x0146 }
            goto L_0x0108
        L_0x0105:
            r12.setType(r10)     // Catch:{ all -> 0x0146 }
        L_0x0108:
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ColInfo[] r2 = r2.getColumns()     // Catch:{ all -> 0x0146 }
            r12.setColMetaData(r2)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.TdsCore r2 = r1.baseTds     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ParamInfo[] r2 = r2.getParameters()     // Catch:{ all -> 0x0146 }
            r12.setParamMetaData(r2)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ColInfo[] r2 = r12.getColMetaData()     // Catch:{ all -> 0x0146 }
            r15.setColMetaData(r2)     // Catch:{ all -> 0x0146 }
            net.sourceforge.jtds.jdbc.ParamInfo[] r2 = r12.getParamMetaData()     // Catch:{ all -> 0x0146 }
            r15.setParamMetaData(r2)     // Catch:{ all -> 0x0146 }
        L_0x0128:
            r14.addCachedProcedure(r11, r12)     // Catch:{ all -> 0x0146 }
            r2 = r12
        L_0x012c:
            java.util.Collection r3 = r0.handles     // Catch:{ all -> 0x0146 }
            if (r3 != 0) goto L_0x0139
            java.util.HashSet r3 = new java.util.HashSet     // Catch:{ all -> 0x0146 }
            r4 = 10
            r3.<init>(r4)     // Catch:{ all -> 0x0146 }
            r0.handles = r3     // Catch:{ all -> 0x0146 }
        L_0x0139:
            java.util.Collection r0 = r0.handles     // Catch:{ all -> 0x0146 }
            r0.add(r2)     // Catch:{ all -> 0x0146 }
            java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x0146 }
            monitor-exit(r14)
            return r0
        L_0x0144:
            monitor-exit(r14)
            return r3
        L_0x0146:
            r0 = move-exception
            monitor-exit(r14)
            goto L_0x014a
        L_0x0149:
            throw r0
        L_0x014a:
            goto L_0x0149
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.prepareSQL(net.sourceforge.jtds.jdbc.JtdsPreparedStatement, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[], boolean, boolean):java.lang.String");
    }

    /* access modifiers changed from: 0000 */
    public void addCachedProcedure(String str, ProcEntry procEntry) {
        this.statementCache.put(str, procEntry);
        if (!this.autoCommit && procEntry.getType() == 1 && this.serverType == 1) {
            this.procInTran.add(str);
        }
        if (getServerType() == 1 && procEntry.getType() == 1) {
            addCachedProcedure(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public void removeCachedProcedure(String str) {
        this.statementCache.remove(str);
        if (!this.autoCommit) {
            this.procInTran.remove(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public int getMaxStatements() {
        return this.maxStatements;
    }

    public int getServerType() {
        return this.serverType;
    }

    /* access modifiers changed from: 0000 */
    public void setNetPacketSize(int i) {
        this.netPacketSize = i;
    }

    /* access modifiers changed from: 0000 */
    public int getNetPacketSize() {
        return this.netPacketSize;
    }

    /* access modifiers changed from: 0000 */
    public int getRowCount() {
        return this.rowCount;
    }

    /* access modifiers changed from: 0000 */
    public void setRowCount(int i) {
        this.rowCount = i;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int i) {
        this.textSize = i;
    }

    /* access modifiers changed from: 0000 */
    public boolean getLastUpdateCount() {
        return this.lastUpdateCount;
    }

    /* access modifiers changed from: 0000 */
    public int getMaxPrecision() {
        return this.maxPrecision;
    }

    /* access modifiers changed from: 0000 */
    public long getLobBuffer() {
        return this.lobBuffer;
    }

    /* access modifiers changed from: 0000 */
    public int getPrepareSql() {
        return this.prepareSql;
    }

    /* access modifiers changed from: 0000 */
    public int getBatchSize() {
        return this.batchSize;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseMetadataCache() {
        return this.useMetadataCache;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseCursors() {
        return this.useCursors;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseLOBs() {
        return this.useLOBs;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseNTLMv2() {
        return this.useNTLMv2;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseKerberos() {
        return this.useKerberos;
    }

    /* access modifiers changed from: 0000 */
    public String getAppName() {
        return this.appName;
    }

    /* access modifiers changed from: 0000 */
    public String getBindAddress() {
        return this.bindAddress;
    }

    /* access modifiers changed from: 0000 */
    public File getBufferDir() {
        return this.bufferDir;
    }

    /* access modifiers changed from: 0000 */
    public int getBufferMaxMemory() {
        return this.bufferMaxMemory;
    }

    /* access modifiers changed from: 0000 */
    public int getBufferMinPackets() {
        return this.bufferMinPackets;
    }

    /* access modifiers changed from: 0000 */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /* access modifiers changed from: 0000 */
    public String getDomainName() {
        return this.domainName;
    }

    /* access modifiers changed from: 0000 */
    public String getInstanceName() {
        return this.instanceName;
    }

    /* access modifiers changed from: 0000 */
    public int getLoginTimeout() {
        return this.loginTimeout;
    }

    /* access modifiers changed from: 0000 */
    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    /* access modifiers changed from: 0000 */
    public boolean getSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    /* access modifiers changed from: 0000 */
    public int getProcessId() {
        return processId.intValue();
    }

    /* access modifiers changed from: 0000 */
    public String getMacAddress() {
        return this.macAddress;
    }

    /* access modifiers changed from: 0000 */
    public boolean getNamedPipe() {
        return this.namedPipe;
    }

    /* access modifiers changed from: 0000 */
    public int getPacketSize() {
        return this.packetSize;
    }

    /* access modifiers changed from: 0000 */
    public String getPassword() {
        return this.password;
    }

    /* access modifiers changed from: 0000 */
    public int getPortNumber() {
        return this.portNumber;
    }

    /* access modifiers changed from: 0000 */
    public String getProgName() {
        return this.progName;
    }

    /* access modifiers changed from: 0000 */
    public String getServerName() {
        return this.serverName;
    }

    /* access modifiers changed from: 0000 */
    public boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    /* access modifiers changed from: 0000 */
    public boolean getUseJCIFS() {
        return this.useJCIFS;
    }

    /* access modifiers changed from: 0000 */
    public String getUser() {
        return this.user;
    }

    /* access modifiers changed from: 0000 */
    public String getWsid() {
        return this.wsid;
    }

    /* access modifiers changed from: protected */
    public void unpackProperties(Properties properties) throws SQLException {
        this.serverName = properties.getProperty(Messages.get(Driver.SERVERNAME));
        this.portNumber = parseIntegerProperty(properties, Driver.PORTNUMBER);
        this.serverType = parseIntegerProperty(properties, Driver.SERVERTYPE);
        this.databaseName = properties.getProperty(Messages.get(Driver.DATABASENAME));
        this.instanceName = properties.getProperty(Messages.get(Driver.INSTANCE));
        this.domainName = properties.getProperty(Messages.get(Driver.DOMAIN));
        this.user = properties.getProperty(Messages.get(Driver.USER));
        this.password = properties.getProperty(Messages.get(Driver.PASSWORD));
        this.macAddress = properties.getProperty(Messages.get(Driver.MACADDRESS));
        this.appName = properties.getProperty(Messages.get(Driver.APPNAME));
        this.progName = properties.getProperty(Messages.get(Driver.PROGNAME));
        this.wsid = properties.getProperty(Messages.get(Driver.WSID));
        this.serverCharset = properties.getProperty(Messages.get(Driver.CHARSET));
        this.language = properties.getProperty(Messages.get(Driver.LANGUAGE));
        this.bindAddress = properties.getProperty(Messages.get(Driver.BINDADDRESS));
        this.lastUpdateCount = parseBooleanProperty(properties, Driver.LASTUPDATECOUNT);
        this.useUnicode = parseBooleanProperty(properties, Driver.SENDSTRINGPARAMETERSASUNICODE);
        this.namedPipe = parseBooleanProperty(properties, Driver.NAMEDPIPE);
        this.tcpNoDelay = parseBooleanProperty(properties, Driver.TCPNODELAY);
        this.useCursors = this.serverType == 1 && parseBooleanProperty(properties, Driver.USECURSORS);
        this.useLOBs = parseBooleanProperty(properties, Driver.USELOBS);
        this.useMetadataCache = parseBooleanProperty(properties, Driver.CACHEMETA);
        this.xaEmulation = parseBooleanProperty(properties, Driver.XAEMULATION);
        this.useJCIFS = parseBooleanProperty(properties, Driver.USEJCIFS);
        this.charsetSpecified = this.serverCharset.length() > 0;
        this.useNTLMv2 = parseBooleanProperty(properties, Driver.USENTLMV2);
        this.useKerberos = parseBooleanProperty(properties, Driver.USEKERBEROS);
        String str = this.domainName;
        if (str != null) {
            this.domainName = str.toUpperCase();
        }
        String str2 = Driver.TDS;
        Integer tdsVersion2 = DefaultProperties.getTdsVersion(properties.getProperty(Messages.get(str2)));
        String str3 = "08001";
        String str4 = "error.connection.badprop";
        if (tdsVersion2 != null) {
            this.tdsVersion = tdsVersion2.intValue();
            int parseIntegerProperty = parseIntegerProperty(properties, Driver.PACKETSIZE);
            this.packetSize = parseIntegerProperty;
            if (parseIntegerProperty < 512) {
                int i = this.tdsVersion;
                if (i >= 3) {
                    this.packetSize = parseIntegerProperty == 0 ? 0 : 4096;
                } else if (i == 1) {
                    this.packetSize = 512;
                }
            }
            if (this.packetSize > 32768) {
                this.packetSize = 32768;
            }
            this.packetSize = (this.packetSize / 512) * 512;
            this.loginTimeout = parseIntegerProperty(properties, Driver.LOGINTIMEOUT);
            this.socketTimeout = parseIntegerProperty(properties, Driver.SOTIMEOUT);
            this.socketKeepAlive = parseBooleanProperty(properties, Driver.SOKEEPALIVE);
            this.autoCommit = parseBooleanProperty(properties, Driver.AUTOCOMMIT);
            String str5 = Driver.PROCESSID;
            String property = properties.getProperty(Messages.get(str5));
            if ("compute".equals(property)) {
                if (processId == null) {
                    processId = new Integer(new Random(System.currentTimeMillis()).nextInt(32768));
                }
            } else if (property.length() > 0) {
                processId = new Integer(parseIntegerProperty(properties, str5));
            }
            this.lobBuffer = parseLongProperty(properties, Driver.LOBBUFFER);
            this.maxStatements = parseIntegerProperty(properties, Driver.MAXSTATEMENTS);
            this.statementCache = new ProcedureCache(this.maxStatements);
            int parseIntegerProperty2 = parseIntegerProperty(properties, Driver.PREPARESQL);
            this.prepareSql = parseIntegerProperty2;
            if (parseIntegerProperty2 < 0) {
                this.prepareSql = 0;
            } else if (parseIntegerProperty2 > 3) {
                this.prepareSql = 3;
            }
            if (this.tdsVersion < 3 && this.prepareSql == 3) {
                this.prepareSql = 2;
            }
            if (this.tdsVersion < 2 && this.prepareSql == 2) {
                this.prepareSql = 1;
            }
            this.ssl = properties.getProperty(Messages.get(Driver.SSL));
            String str6 = Driver.BATCHSIZE;
            int parseIntegerProperty3 = parseIntegerProperty(properties, str6);
            this.batchSize = parseIntegerProperty3;
            if (parseIntegerProperty3 >= 0) {
                String str7 = Driver.BUFFERDIR;
                File file = new File(properties.getProperty(Messages.get(str7)));
                this.bufferDir = file;
                if (file.isDirectory() || this.bufferDir.mkdirs()) {
                    String str8 = Driver.BUFFERMAXMEMORY;
                    int parseIntegerProperty4 = parseIntegerProperty(properties, str8);
                    this.bufferMaxMemory = parseIntegerProperty4;
                    if (parseIntegerProperty4 >= 0) {
                        String str9 = Driver.BUFFERMINPACKETS;
                        int parseIntegerProperty5 = parseIntegerProperty(properties, str9);
                        this.bufferMinPackets = parseIntegerProperty5;
                        if (parseIntegerProperty5 < 1) {
                            throw new SQLException(Messages.get(str4, (Object) Messages.get(str9)), str3);
                        }
                        return;
                    }
                    throw new SQLException(Messages.get(str4, (Object) Messages.get(str8)), str3);
                }
                throw new SQLException(Messages.get(str4, (Object) Messages.get(str7)), str3);
            }
            throw new SQLException(Messages.get(str4, (Object) Messages.get(str6)), str3);
        }
        throw new SQLException(Messages.get(str4, (Object) Messages.get(str2)), str3);
    }

    private static boolean parseBooleanProperty(Properties properties, String str) throws SQLException {
        String str2 = Messages.get(str);
        String property = properties.getProperty(str2);
        String str3 = "true";
        if (property == null || str3.equalsIgnoreCase(property) || "false".equalsIgnoreCase(property)) {
            return str3.equalsIgnoreCase(property);
        }
        throw new SQLException(Messages.get("error.connection.badprop", (Object) str2), "08001");
    }

    private static int parseIntegerProperty(Properties properties, String str) throws SQLException {
        String str2 = Messages.get(str);
        try {
            return Integer.parseInt(properties.getProperty(str2));
        } catch (NumberFormatException unused) {
            throw new SQLException(Messages.get("error.connection.badprop", (Object) str2), "08001");
        }
    }

    private static long parseLongProperty(Properties properties, String str) throws SQLException {
        String str2 = Messages.get(str);
        try {
            return Long.parseLong(properties.getProperty(str2));
        } catch (NumberFormatException unused) {
            throw new SQLException(Messages.get("error.connection.badprop", (Object) str2), "08001");
        }
    }

    /* access modifiers changed from: protected */
    public String getCharset() {
        return this.charsetInfo.getCharset();
    }

    /* access modifiers changed from: protected */
    public boolean isWideChar() {
        return this.charsetInfo.isWideChars();
    }

    /* access modifiers changed from: protected */
    public CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    /* access modifiers changed from: protected */
    public boolean getUseUnicode() {
        return this.useUnicode;
    }

    /* access modifiers changed from: protected */
    public boolean getSybaseInfo(int i) {
        return (i & this.sybaseInfo) != 0;
    }

    /* access modifiers changed from: protected */
    public void setSybaseInfo(int i) {
        this.sybaseInfo = i;
    }

    /* access modifiers changed from: protected */
    public void setServerCharset(String str) throws SQLException {
        if (this.charsetSpecified) {
            StringBuilder sb = new StringBuilder();
            sb.append("Server charset ");
            sb.append(str);
            sb.append(". Ignoring as user requested ");
            sb.append(this.serverCharset);
            sb.append('.');
            Logger.println(sb.toString());
            return;
        }
        if (!str.equals(this.serverCharset)) {
            loadCharset(str);
            if (Logger.isActive()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Set charset to ");
                sb2.append(this.serverCharset);
                sb2.append('/');
                sb2.append(this.charsetInfo);
                Logger.println(sb2.toString());
            }
        }
    }

    private void loadCharset(String str) throws SQLException {
        if (getServerType() == 1 && str.equalsIgnoreCase("iso_1")) {
            str = "Cp1252";
        }
        CharsetInfo charset = CharsetInfo.getCharset(str);
        if (charset != null) {
            loadCharset(charset, str);
            this.serverCharset = str;
            return;
        }
        throw new SQLException(Messages.get("error.charset.nomapping", (Object) str), "2C000");
    }

    private void loadCharset(CharsetInfo charsetInfo2, String str) throws SQLException {
        try {
            "This is a test".getBytes(charsetInfo2.getCharset());
            this.charsetInfo = charsetInfo2;
            this.socket.setCharsetInfo(charsetInfo2);
        } catch (UnsupportedEncodingException unused) {
            throw new SQLException(Messages.get("error.charset.invalid", str, charsetInfo2.getCharset()), "2C000");
        }
    }

    private String determineServerCharset() throws SQLException {
        String str;
        int i = this.serverType;
        if (i != 1) {
            str = i != 2 ? null : SYBASE_SERVER_CHARSET_QUERY;
        } else if (this.databaseProductVersion.indexOf("6.5") >= 0) {
            str = SQL_SERVER_65_CHARSET_QUERY;
        } else {
            throw new SQLException("Please use TDS protocol version 7.0 or higher");
        }
        Statement createStatement = createStatement();
        ResultSet executeQuery = createStatement.executeQuery(str);
        executeQuery.next();
        String string = executeQuery.getString(1);
        executeQuery.close();
        createStatement.close();
        return string;
    }

    /* access modifiers changed from: 0000 */
    public void setCollation(byte[] bArr) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Support.toHex(bArr));
        String sb2 = sb.toString();
        if (this.charsetSpecified) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Server collation ");
            sb3.append(sb2);
            sb3.append(". Ignoring as user requested ");
            sb3.append(this.serverCharset);
            sb3.append('.');
            Logger.println(sb3.toString());
            return;
        }
        loadCharset(CharsetInfo.getCharset(bArr), sb2);
        this.collation = bArr;
        if (Logger.isActive()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Set collation to ");
            sb4.append(sb2);
            sb4.append('/');
            sb4.append(this.charsetInfo);
            Logger.println(sb4.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public byte[] getCollation() {
        return this.collation;
    }

    /* access modifiers changed from: 0000 */
    public boolean isCharsetSpecified() {
        return this.charsetSpecified;
    }

    /* access modifiers changed from: protected */
    public void setDatabase(String str, String str2) throws SQLException {
        String str3 = this.currentDatabase;
        if (str3 == null || str2.equalsIgnoreCase(str3)) {
            this.currentDatabase = str;
            if (Logger.isActive()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Changed database from ");
                sb.append(str2);
                sb.append(" to ");
                sb.append(str);
                Logger.println(sb.toString());
                return;
            }
            return;
        }
        throw new SQLException(Messages.get("error.connection.dbmismatch", str2, this.databaseName), "HY096");
    }

    /* access modifiers changed from: protected */
    public void setDBServerInfo(String str, int i, int i2, int i3) {
        this.databaseProductName = str;
        this.databaseMajorVersion = i;
        this.databaseMinorVersion = i2;
        if (this.tdsVersion >= 3) {
            StringBuilder sb = new StringBuilder(10);
            if (i < 10) {
                sb.append('0');
            }
            sb.append(i);
            sb.append('.');
            if (i2 < 10) {
                sb.append('0');
            }
            sb.append(i2);
            sb.append('.');
            sb.append(i3);
            while (sb.length() < 10) {
                sb.insert(6, '0');
            }
            this.databaseProductVersion = sb.toString();
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(i);
        sb2.append(".");
        sb2.append(i2);
        this.databaseProductVersion = sb2.toString();
    }

    /* access modifiers changed from: 0000 */
    public synchronized void removeStatement(JtdsStatement jtdsStatement) throws SQLException {
        synchronized (this.statements) {
            for (int i = 0; i < this.statements.size(); i++) {
                WeakReference weakReference = (WeakReference) this.statements.get(i);
                if (weakReference != null) {
                    Statement statement = (Statement) weakReference.get();
                    if (statement == null || statement == jtdsStatement) {
                        this.statements.set(i, null);
                    }
                }
            }
        }
        if (jtdsStatement instanceof JtdsPreparedStatement) {
            Collection<ProcEntry> obsoleteHandles = this.statementCache.getObsoleteHandles(((JtdsPreparedStatement) jtdsStatement).handles);
            if (obsoleteHandles != null) {
                if (this.serverType == 1) {
                    StringBuilder sb = new StringBuilder(obsoleteHandles.size() * 32);
                    for (ProcEntry appendDropSQL : obsoleteHandles) {
                        appendDropSQL.appendDropSQL(sb);
                    }
                    if (sb.length() > 0) {
                        this.baseTds.executeSQL(sb.toString(), null, null, true, 0, -1, -1, true);
                        this.baseTds.clearResponseQueue();
                    }
                } else {
                    for (ProcEntry procEntry : obsoleteHandles) {
                        if (procEntry.toString() != null) {
                            this.baseTds.sybaseUnPrepare(procEntry.toString());
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void addStatement(JtdsStatement jtdsStatement) {
        synchronized (this.statements) {
            int i = 0;
            while (i < this.statements.size()) {
                WeakReference weakReference = (WeakReference) this.statements.get(i);
                if (weakReference != null) {
                    if (weakReference.get() != null) {
                        i++;
                    }
                }
                this.statements.set(i, new WeakReference(jtdsStatement));
                return;
            }
            this.statements.add(new WeakReference(jtdsStatement));
        }
    }

    /* access modifiers changed from: 0000 */
    public void checkOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Connection"), "HY010");
        }
    }

    /* access modifiers changed from: 0000 */
    public void checkLocal(String str) throws SQLException {
        if (this.xaTransaction) {
            throw new SQLException(Messages.get("error.connection.badxaop", (Object) str), "HY010");
        }
    }

    static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    public int getDatabaseMajorVersion() {
        return this.databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return this.databaseMinorVersion;
    }

    /* access modifiers changed from: 0000 */
    public String getDatabaseProductName() {
        return this.databaseProductName;
    }

    /* access modifiers changed from: 0000 */
    public String getDatabaseProductVersion() {
        return this.databaseProductVersion;
    }

    /* access modifiers changed from: 0000 */
    public String getURL() {
        return this.url;
    }

    public String getRmHost() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.serverName);
        sb.append(':');
        sb.append(this.portNumber);
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public void setClosed() {
        if (!this.closed) {
            this.closed = true;
            try {
                this.socket.close();
            } catch (IOException unused) {
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized byte[][] sendXaPacket(int[] iArr, byte[] bArr) throws SQLException {
        ParamInfo[] paramInfoArr = {new ParamInfo(4, null, 2), new ParamInfo(4, new Integer(iArr[1]), 0), new ParamInfo(4, new Integer(iArr[2]), 0), new ParamInfo(4, new Integer(iArr[3]), 0), new ParamInfo(4, new Integer(iArr[4]), 0), new ParamInfo(-3, bArr, 1)};
        this.baseTds.executeSQL(null, "master..xp_jtdsxa", paramInfoArr, false, 0, -1, -1, true);
        ArrayList arrayList = new ArrayList();
        while (!this.baseTds.isEndOfResponse()) {
            if (this.baseTds.getMoreResults()) {
                while (this.baseTds.getNextRow()) {
                    Object[] rowData = this.baseTds.getRowData();
                    if (rowData.length == 1 && (rowData[0] instanceof byte[])) {
                        arrayList.add(rowData[0]);
                    }
                }
            }
        }
        this.messages.checkErrors();
        if (paramInfoArr[0].getOutValue() instanceof Integer) {
            iArr[0] = ((Integer) paramInfoArr[0].getOutValue()).intValue();
        } else {
            iArr[0] = -7;
        }
        if (arrayList.size() > 0) {
            byte[][] bArr2 = new byte[arrayList.size()][];
            for (int i = 0; i < arrayList.size(); i++) {
                bArr2[i] = (byte[]) arrayList.get(i);
            }
            return bArr2;
        } else if (paramInfoArr[5].getOutValue() instanceof byte[]) {
            return new byte[][]{(byte[]) paramInfoArr[5].getOutValue()};
        } else {
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized void enlistConnection(byte[] bArr) throws SQLException {
        if (bArr != null) {
            this.prepareSql = 2;
            this.baseTds.enlistConnection(1, bArr);
            this.xaTransaction = true;
        } else {
            this.baseTds.enlistConnection(1, null);
            this.xaTransaction = false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void setXid(Object obj) {
        this.xid = obj;
        this.xaTransaction = obj != null;
    }

    /* access modifiers changed from: 0000 */
    public Object getXid() {
        return this.xid;
    }

    /* access modifiers changed from: 0000 */
    public void setXaState(int i) {
        this.xaState = i;
    }

    /* access modifiers changed from: 0000 */
    public int getXaState() {
        return this.xaState;
    }

    /* access modifiers changed from: 0000 */
    public boolean isXaEmulation() {
        return this.xaEmulation;
    }

    /* access modifiers changed from: 0000 */
    public Semaphore getMutex() {
        boolean z = false;
        while (true) {
            try {
                this.mutex.acquire();
                break;
            } catch (InterruptedException unused) {
                z = true;
            }
        }
        if (z) {
            Thread.currentThread().interrupt();
        }
        return this.mutex;
    }

    /* access modifiers changed from: 0000 */
    public synchronized void releaseTds(TdsCore tdsCore) throws SQLException {
        if (this.cachedTds != null) {
            tdsCore.close();
        } else {
            tdsCore.clearResponseQueue();
            tdsCore.cleanUp();
            this.cachedTds = tdsCore;
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized TdsCore getCachedTds() {
        TdsCore tdsCore;
        tdsCore = this.cachedTds;
        this.cachedTds = null;
        return tdsCore;
    }

    public int getHoldability() throws SQLException {
        checkOpen();
        return 1;
    }

    public synchronized int getTransactionIsolation() throws SQLException {
        checkOpen();
        return this.transactionIsolation;
    }

    public synchronized void clearWarnings() throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:30:0x004e */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0052 A[Catch:{ IOException -> 0x0087, all -> 0x0070 }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x005c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void close() throws java.sql.SQLException {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.closed     // Catch:{ all -> 0x009f }
            if (r0 != 0) goto L_0x009d
            r0 = 0
            r1 = 1
            java.util.ArrayList r2 = r6.statements     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            monitor-enter(r2)     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x006d }
            java.util.ArrayList r4 = r6.statements     // Catch:{ all -> 0x006d }
            r3.<init>(r4)     // Catch:{ all -> 0x006d }
            java.util.ArrayList r4 = r6.statements     // Catch:{ all -> 0x006d }
            r4.clear()     // Catch:{ all -> 0x006d }
            monitor-exit(r2)     // Catch:{ all -> 0x006d }
            r2 = 0
        L_0x0018:
            int r4 = r3.size()     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            if (r2 >= r4) goto L_0x0034
            java.lang.Object r4 = r3.get(r2)     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            java.lang.ref.WeakReference r4 = (java.lang.ref.WeakReference) r4     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            if (r4 == 0) goto L_0x0031
            java.lang.Object r4 = r4.get()     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            java.sql.Statement r4 = (java.sql.Statement) r4     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            if (r4 == 0) goto L_0x0031
            r4.close()     // Catch:{ SQLException -> 0x0031 }
        L_0x0031:
            int r2 = r2 + 1
            goto L_0x0018
        L_0x0034:
            net.sourceforge.jtds.jdbc.TdsCore r2 = r6.baseTds     // Catch:{ SQLException -> 0x004e }
            if (r2 == 0) goto L_0x0042
            net.sourceforge.jtds.jdbc.TdsCore r2 = r6.baseTds     // Catch:{ SQLException -> 0x004e }
            r2.closeConnection()     // Catch:{ SQLException -> 0x004e }
            net.sourceforge.jtds.jdbc.TdsCore r2 = r6.baseTds     // Catch:{ SQLException -> 0x004e }
            r2.close()     // Catch:{ SQLException -> 0x004e }
        L_0x0042:
            net.sourceforge.jtds.jdbc.TdsCore r2 = r6.cachedTds     // Catch:{ SQLException -> 0x004e }
            if (r2 == 0) goto L_0x004e
            net.sourceforge.jtds.jdbc.TdsCore r2 = r6.cachedTds     // Catch:{ SQLException -> 0x004e }
            r2.close()     // Catch:{ SQLException -> 0x004e }
            r2 = 0
            r6.cachedTds = r2     // Catch:{ SQLException -> 0x004e }
        L_0x004e:
            net.sourceforge.jtds.jdbc.SharedSocket r2 = r6.socket     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            if (r2 == 0) goto L_0x0057
            net.sourceforge.jtds.jdbc.SharedSocket r2 = r6.socket     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
            r2.close()     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
        L_0x0057:
            r6.closed = r1     // Catch:{ all -> 0x009f }
            int[] r2 = connections     // Catch:{ all -> 0x009f }
            monitor-enter(r2)     // Catch:{ all -> 0x009f }
            int[] r3 = connections     // Catch:{ all -> 0x006a }
            r4 = r3[r0]     // Catch:{ all -> 0x006a }
            int r4 = r4 - r1
            r3[r0] = r4     // Catch:{ all -> 0x006a }
            if (r4 != 0) goto L_0x0068
            net.sourceforge.jtds.util.TimerThread.stopTimer()     // Catch:{ all -> 0x006a }
        L_0x0068:
            monitor-exit(r2)     // Catch:{ all -> 0x006a }
            goto L_0x009d
        L_0x006a:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x006a }
            throw r0     // Catch:{ all -> 0x009f }
        L_0x006d:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x006d }
            throw r3     // Catch:{ IOException -> 0x0087, all -> 0x0070 }
        L_0x0070:
            r2 = move-exception
            r6.closed = r1     // Catch:{ all -> 0x009f }
            int[] r3 = connections     // Catch:{ all -> 0x009f }
            monitor-enter(r3)     // Catch:{ all -> 0x009f }
            int[] r4 = connections     // Catch:{ all -> 0x0084 }
            r5 = r4[r0]     // Catch:{ all -> 0x0084 }
            int r5 = r5 - r1
            r4[r0] = r5     // Catch:{ all -> 0x0084 }
            if (r5 != 0) goto L_0x0082
            net.sourceforge.jtds.util.TimerThread.stopTimer()     // Catch:{ all -> 0x0084 }
        L_0x0082:
            monitor-exit(r3)     // Catch:{ all -> 0x0084 }
            throw r2     // Catch:{ all -> 0x009f }
        L_0x0084:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0084 }
            throw r0     // Catch:{ all -> 0x009f }
        L_0x0087:
            r6.closed = r1     // Catch:{ all -> 0x009f }
            int[] r2 = connections     // Catch:{ all -> 0x009f }
            monitor-enter(r2)     // Catch:{ all -> 0x009f }
            int[] r3 = connections     // Catch:{ all -> 0x009a }
            r4 = r3[r0]     // Catch:{ all -> 0x009a }
            int r4 = r4 - r1
            r3[r0] = r4     // Catch:{ all -> 0x009a }
            if (r4 != 0) goto L_0x0098
            net.sourceforge.jtds.util.TimerThread.stopTimer()     // Catch:{ all -> 0x009a }
        L_0x0098:
            monitor-exit(r2)     // Catch:{ all -> 0x009a }
            goto L_0x009d
        L_0x009a:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x009a }
            throw r0     // Catch:{ all -> 0x009f }
        L_0x009d:
            monitor-exit(r6)
            return
        L_0x009f:
            r0 = move-exception
            monitor-exit(r6)
            goto L_0x00a3
        L_0x00a2:
            throw r0
        L_0x00a3:
            goto L_0x00a2
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.close():void");
    }

    public synchronized void commit() throws SQLException {
        checkOpen();
        checkLocal("commit");
        if (!getAutoCommit()) {
            this.baseTds.submitSQL("IF @@TRANCOUNT > 0 COMMIT TRAN");
            this.procInTran.clear();
            clearSavepoints();
        } else {
            throw new SQLException(Messages.get("error.connection.autocommit", (Object) "commit"), "25000");
        }
    }

    public synchronized void rollback() throws SQLException {
        checkOpen();
        checkLocal("rollback");
        if (!getAutoCommit()) {
            this.baseTds.submitSQL("IF @@TRANCOUNT > 0 ROLLBACK TRAN");
            for (int i = 0; i < this.procInTran.size(); i++) {
                String str = (String) this.procInTran.get(i);
                if (str != null) {
                    this.statementCache.remove(str);
                }
            }
            this.procInTran.clear();
            clearSavepoints();
        } else {
            throw new SQLException(Messages.get("error.connection.autocommit", (Object) "rollback"), "25000");
        }
    }

    public synchronized boolean getAutoCommit() throws SQLException {
        checkOpen();
        return this.autoCommit;
    }

    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    public boolean isReadOnly() throws SQLException {
        checkOpen();
        return this.readOnly;
    }

    public void setHoldability(int i) throws SQLException {
        checkOpen();
        if (i != 1) {
            String str = "HY092";
            if (i != 2) {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "holdability"), str);
            }
            throw new SQLException(Messages.get("error.generic.optvalue", "CLOSE_CURSORS_AT_COMMIT", "setHoldability"), str);
        }
    }

    public synchronized void setTransactionIsolation(int i) throws SQLException {
        String str;
        checkOpen();
        if (this.transactionIsolation != i) {
            String str2 = "SET TRANSACTION ISOLATION LEVEL ";
            boolean z = this.serverType == 2;
            if (i != 0) {
                if (i == 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(z ? "0" : "READ UNCOMMITTED");
                    str = sb.toString();
                } else if (i == 2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(z ? "1" : "READ COMMITTED");
                    str = sb2.toString();
                } else if (i == 4) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str2);
                    sb3.append(z ? "2" : "REPEATABLE READ");
                    str = sb3.toString();
                } else if (i == 8) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(str2);
                    sb4.append(z ? "3" : "SERIALIZABLE");
                    str = sb4.toString();
                } else if (i != 4096) {
                    throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "level"), "HY092");
                } else if (!z) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(str2);
                    sb5.append("SNAPSHOT");
                    str = sb5.toString();
                } else {
                    throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_SNAPSHOT", "setTransactionIsolation"), "HY024");
                }
                this.transactionIsolation = i;
                this.baseTds.submitSQL(str);
                return;
            }
            throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_NONE", "setTransactionIsolation"), "HY024");
        }
    }

    public synchronized void setAutoCommit(boolean z) throws SQLException {
        checkOpen();
        checkLocal("setAutoCommit");
        if (this.autoCommit != z) {
            StringBuilder sb = new StringBuilder(70);
            if (!this.autoCommit) {
                sb.append("IF @@TRANCOUNT > 0 COMMIT TRAN\r\n");
            }
            if (this.serverType == 2) {
                if (z) {
                    sb.append("SET CHAINED OFF");
                } else {
                    sb.append("SET CHAINED ON");
                }
            } else if (z) {
                sb.append("SET IMPLICIT_TRANSACTIONS OFF");
            } else {
                sb.append("SET IMPLICIT_TRANSACTIONS ON");
            }
            this.baseTds.submitSQL(sb.toString());
            this.autoCommit = z;
        }
    }

    public void setReadOnly(boolean z) throws SQLException {
        checkOpen();
        this.readOnly = z;
    }

    public synchronized String getCatalog() throws SQLException {
        checkOpen();
        return this.currentDatabase;
    }

    public synchronized void setCatalog(String str) throws SQLException {
        StringBuilder sb;
        checkOpen();
        if (this.currentDatabase == null || !this.currentDatabase.equals(str)) {
            if (str.length() > (this.tdsVersion >= 3 ? 128 : 30) || str.length() < 1) {
                throw new SQLException(Messages.get("error.generic.badparam", str, "catalog"), "3D000");
            }
            if (this.tdsVersion >= 3) {
                sb = new StringBuilder();
                sb.append("use [");
                sb.append(str);
                sb.append(']');
            } else {
                sb = new StringBuilder();
                sb.append("use ");
                sb.append(str);
            }
            this.baseTds.submitSQL(sb.toString());
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen();
        return new JtdsDatabaseMetaData(this);
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.messages.getWarnings();
    }

    public Statement createStatement() throws SQLException {
        checkOpen();
        return createStatement(PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public synchronized Statement createStatement(int i, int i2) throws SQLException {
        JtdsStatement jtdsStatement;
        checkOpen();
        jtdsStatement = new JtdsStatement(this, i, i2);
        addStatement(jtdsStatement);
        return jtdsStatement;
    }

    public Statement createStatement(int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return createStatement(i, i2);
    }

    public Map getTypeMap() throws SQLException {
        checkOpen();
        return new HashMap();
    }

    public void setTypeMap(Map map) throws SQLException {
        checkOpen();
        notImplemented("Connection.setTypeMap(Map)");
    }

    public String nativeSQL(String str) throws SQLException {
        checkOpen();
        if (str != null && str.length() != 0) {
            return SQLParser.parse(str, new ArrayList(), this, false)[0];
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
    }

    public CallableStatement prepareCall(String str) throws SQLException {
        checkOpen();
        return prepareCall(str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public synchronized CallableStatement prepareCall(String str, int i, int i2) throws SQLException {
        JtdsCallableStatement jtdsCallableStatement;
        checkOpen();
        if (str == null || str.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        jtdsCallableStatement = new JtdsCallableStatement(this, str, i, i2);
        addStatement(jtdsCallableStatement);
        return jtdsCallableStatement;
    }

    public CallableStatement prepareCall(String str, int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return prepareCall(str, i, i2);
    }

    public PreparedStatement prepareStatement(String str) throws SQLException {
        checkOpen();
        return prepareStatement(str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public PreparedStatement prepareStatement(String str, int i) throws SQLException {
        checkOpen();
        if (str == null || str.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        } else if (i == 1 || i == 2) {
            JtdsPreparedStatement jtdsPreparedStatement = new JtdsPreparedStatement(this, str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, i == 1);
            addStatement(jtdsPreparedStatement);
            return jtdsPreparedStatement;
        } else {
            throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "autoGeneratedKeys"), "HY092");
        }
    }

    public synchronized PreparedStatement prepareStatement(String str, int i, int i2) throws SQLException {
        JtdsPreparedStatement jtdsPreparedStatement;
        checkOpen();
        if (str == null || str.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        jtdsPreparedStatement = new JtdsPreparedStatement(this, str, i, i2, false);
        addStatement(jtdsPreparedStatement);
        return jtdsPreparedStatement;
    }

    public PreparedStatement prepareStatement(String str, int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return prepareStatement(str, i, i2);
    }

    public PreparedStatement prepareStatement(String str, int[] iArr) throws SQLException {
        String str2 = "HY092";
        String str3 = "prepareStatement";
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (iArr.length == 1) {
            return prepareStatement(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) str3), str2);
        }
    }

    public PreparedStatement prepareStatement(String str, String[] strArr) throws SQLException {
        String str2 = "HY092";
        String str3 = "prepareStatement";
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) str3), str2);
        } else if (strArr.length == 1) {
            return prepareStatement(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) str3), str2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x003d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSavepoint(net.sourceforge.jtds.jdbc.SavepointImpl r4) throws java.sql.SQLException {
        /*
            r3 = this;
            java.sql.Statement r0 = r3.createStatement()     // Catch:{ all -> 0x0039 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0037 }
            r1.<init>()     // Catch:{ all -> 0x0037 }
            java.lang.String r2 = "IF @@TRANCOUNT=0 BEGIN SET IMPLICIT_TRANSACTIONS OFF; BEGIN TRAN; SET IMPLICIT_TRANSACTIONS ON; END SAVE TRAN jtds"
            r1.append(r2)     // Catch:{ all -> 0x0037 }
            int r2 = r4.getId()     // Catch:{ all -> 0x0037 }
            r1.append(r2)     // Catch:{ all -> 0x0037 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0037 }
            r0.execute(r1)     // Catch:{ all -> 0x0037 }
            if (r0 == 0) goto L_0x0021
            r0.close()
        L_0x0021:
            monitor-enter(r3)
            java.util.ArrayList r0 = r3.savepoints     // Catch:{ all -> 0x0034 }
            if (r0 != 0) goto L_0x002d
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0034 }
            r0.<init>()     // Catch:{ all -> 0x0034 }
            r3.savepoints = r0     // Catch:{ all -> 0x0034 }
        L_0x002d:
            java.util.ArrayList r0 = r3.savepoints     // Catch:{ all -> 0x0034 }
            r0.add(r4)     // Catch:{ all -> 0x0034 }
            monitor-exit(r3)     // Catch:{ all -> 0x0034 }
            return
        L_0x0034:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0034 }
            throw r4
        L_0x0037:
            r4 = move-exception
            goto L_0x003b
        L_0x0039:
            r4 = move-exception
            r0 = 0
        L_0x003b:
            if (r0 == 0) goto L_0x0040
            r0.close()
        L_0x0040:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.setSavepoint(net.sourceforge.jtds.jdbc.SavepointImpl):void");
    }

    private synchronized void clearSavepoints() {
        if (this.savepoints != null) {
            this.savepoints.clear();
        }
        if (this.savepointProcInTran != null) {
            this.savepointProcInTran.clear();
        }
        this.savepointId = 0;
    }

    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkOpen();
        if (this.savepoints != null) {
            int indexOf = this.savepoints.indexOf(savepoint);
            if (indexOf != -1) {
                Object remove = this.savepoints.remove(indexOf);
                if (this.savepointProcInTran != null) {
                    if (indexOf != 0) {
                        List list = (List) this.savepointProcInTran.get(savepoint);
                        if (list != null) {
                            Savepoint savepoint2 = (Savepoint) this.savepoints.get(indexOf - 1);
                            List list2 = (List) this.savepointProcInTran.get(savepoint2);
                            if (list2 == null) {
                                list2 = new ArrayList();
                            }
                            list2.addAll(list);
                            this.savepointProcInTran.put(savepoint2, list2);
                        }
                    }
                    this.savepointProcInTran.remove(remove);
                }
            } else {
                throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
            }
        } else {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
    }

    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        checkOpen();
        checkLocal("rollback");
        if (this.savepoints != null) {
            int indexOf = this.savepoints.indexOf(savepoint);
            if (indexOf == -1) {
                throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
            } else if (!getAutoCommit()) {
                Statement statement = null;
                try {
                    statement = createStatement();
                    StringBuilder sb = new StringBuilder();
                    sb.append("ROLLBACK TRAN jtds");
                    sb.append(((SavepointImpl) savepoint).getId());
                    statement.execute(sb.toString());
                    if (statement != null) {
                        statement.close();
                    }
                    for (int size = this.savepoints.size() - 1; size >= indexOf; size--) {
                        Object remove = this.savepoints.remove(size);
                        if (this.savepointProcInTran != null) {
                            List<String> list = (List) this.savepointProcInTran.get(remove);
                            if (list != null) {
                                for (String removeCachedProcedure : list) {
                                    removeCachedProcedure(removeCachedProcedure);
                                }
                            }
                        }
                    }
                    setSavepoint((SavepointImpl) savepoint);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            } else {
                throw new SQLException(Messages.get("error.connection.savenorollback"), "25000");
            }
        } else {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
    }

    public synchronized Savepoint setSavepoint() throws SQLException {
        SavepointImpl savepointImpl;
        checkOpen();
        checkLocal("setSavepoint");
        if (!getAutoCommit()) {
            savepointImpl = new SavepointImpl(getNextSavepointId());
            setSavepoint(savepointImpl);
        } else {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        }
        return savepointImpl;
    }

    public synchronized Savepoint setSavepoint(String str) throws SQLException {
        SavepointImpl savepointImpl;
        checkOpen();
        checkLocal("setSavepoint");
        if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        } else if (str != null) {
            savepointImpl = new SavepointImpl(getNextSavepointId(), str);
            setSavepoint(savepointImpl);
        } else {
            throw new SQLException(Messages.get("error.connection.savenullname", (Object) "savepoint"), "25000");
        }
        return savepointImpl;
    }

    private int getNextSavepointId() {
        int i = this.savepointId + 1;
        this.savepointId = i;
        return i;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0041, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void addCachedProcedure(java.lang.String r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            java.util.ArrayList r0 = r2.savepoints     // Catch:{ all -> 0x0042 }
            if (r0 == 0) goto L_0x0040
            java.util.ArrayList r0 = r2.savepoints     // Catch:{ all -> 0x0042 }
            int r0 = r0.size()     // Catch:{ all -> 0x0042 }
            if (r0 != 0) goto L_0x000e
            goto L_0x0040
        L_0x000e:
            java.util.Map r0 = r2.savepointProcInTran     // Catch:{ all -> 0x0042 }
            if (r0 != 0) goto L_0x0019
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0042 }
            r0.<init>()     // Catch:{ all -> 0x0042 }
            r2.savepointProcInTran = r0     // Catch:{ all -> 0x0042 }
        L_0x0019:
            java.util.ArrayList r0 = r2.savepoints     // Catch:{ all -> 0x0042 }
            java.util.ArrayList r1 = r2.savepoints     // Catch:{ all -> 0x0042 }
            int r1 = r1.size()     // Catch:{ all -> 0x0042 }
            int r1 = r1 + -1
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x0042 }
            java.util.Map r1 = r2.savepointProcInTran     // Catch:{ all -> 0x0042 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x0042 }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x0042 }
            if (r1 != 0) goto L_0x0036
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0042 }
            r1.<init>()     // Catch:{ all -> 0x0042 }
        L_0x0036:
            r1.add(r3)     // Catch:{ all -> 0x0042 }
            java.util.Map r3 = r2.savepointProcInTran     // Catch:{ all -> 0x0042 }
            r3.put(r0, r1)     // Catch:{ all -> 0x0042 }
            monitor-exit(r2)
            return
        L_0x0040:
            monitor-exit(r2)
            return
        L_0x0042:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsConnection.addCachedProcedure(java.lang.String):void");
    }

    public Array createArrayOf(String str, Object[] objArr) throws SQLException {
        throw new AbstractMethodError();
    }

    public Blob createBlob() throws SQLException {
        throw new AbstractMethodError();
    }

    public Clob createClob() throws SQLException {
        throw new AbstractMethodError();
    }

    public NClob createNClob() throws SQLException {
        throw new AbstractMethodError();
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new AbstractMethodError();
    }

    public Struct createStruct(String str, Object[] objArr) throws SQLException {
        throw new AbstractMethodError();
    }

    public Properties getClientInfo() throws SQLException {
        throw new AbstractMethodError();
    }

    public String getClientInfo(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isValid(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    public void setClientInfo(String str, String str2) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setSchema(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public String getSchema() throws SQLException {
        throw new AbstractMethodError();
    }

    public void abort(Executor executor) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNetworkTimeout(Executor executor, int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public int getNetworkTimeout() throws SQLException {
        throw new AbstractMethodError();
    }
}
