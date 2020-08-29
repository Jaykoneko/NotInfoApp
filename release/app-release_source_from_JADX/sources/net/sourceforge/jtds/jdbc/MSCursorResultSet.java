package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import java.math.BigDecimal;
import java.sql.SQLException;

public class MSCursorResultSet extends JtdsResultSet {
    private static final int CURSOR_CONCUR_OPTIMISTIC = 4;
    private static final int CURSOR_CONCUR_OPTIMISTIC_VALUES = 8;
    private static final int CURSOR_CONCUR_READ_ONLY = 1;
    private static final int CURSOR_CONCUR_SCROLL_LOCKS = 2;
    private static final Integer CURSOR_OP_DELETE = new Integer(34);
    private static final Integer CURSOR_OP_INSERT = new Integer(4);
    private static final Integer CURSOR_OP_UPDATE = new Integer(33);
    private static final int CURSOR_TYPE_AUTO_FETCH = 8192;
    private static final int CURSOR_TYPE_DYNAMIC = 2;
    private static final int CURSOR_TYPE_FASTFORWARDONLY = 16;
    private static final int CURSOR_TYPE_FORWARD = 4;
    private static final int CURSOR_TYPE_KEYSET = 1;
    private static final int CURSOR_TYPE_PARAMETERIZED = 4096;
    private static final int CURSOR_TYPE_STATIC = 8;
    private static final Integer FETCH_ABSOLUTE = new Integer(16);
    private static final Integer FETCH_FIRST = new Integer(1);
    private static final Integer FETCH_INFO = new Integer(256);
    private static final Integer FETCH_LAST = new Integer(8);
    private static final Integer FETCH_NEXT = new Integer(2);
    private static final Integer FETCH_PREVIOUS = new Integer(4);
    private static final Integer FETCH_RELATIVE = new Integer(32);
    private static final Integer FETCH_REPEAT = new Integer(128);
    private static final Integer SQL_ROW_DELETED = new Integer(2);
    private static final Integer SQL_ROW_DIRTY = new Integer(0);
    private static final Integer SQL_ROW_SUCCESS = new Integer(1);
    private final ParamInfo PARAM_CURSOR_HANDLE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_FETCHTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_NUMROWS_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_NUMROWS_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_OPTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM = new ParamInfo(4, new Integer(1), 0);
    private final ParamInfo PARAM_ROWNUM_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_TABLE = new ParamInfo(12, "", 4);
    private boolean asyncCursor;
    private int cursorPos;
    private ParamInfo[] insertRow;
    private boolean onInsertRow;
    private Object[][] rowCache = new Object[this.fetchSize][];
    private ParamInfo[] updateRow;

    static int getCursorConcurrencyOpt(int i) {
        switch (i) {
            case PointerIconCompat.TYPE_TEXT /*1008*/:
                return 4;
            case PointerIconCompat.TYPE_VERTICAL_TEXT /*1009*/:
                return 2;
            case PointerIconCompat.TYPE_ALIAS /*1010*/:
                return 8;
            default:
                return 1;
        }
    }

    static int getCursorScrollOpt(int i, int i2, boolean z) {
        int i3;
        switch (i) {
            case PointerIconCompat.TYPE_WAIT /*1004*/:
                i3 = 8;
                break;
            case 1005:
                i3 = 1;
                break;
            case PointerIconCompat.TYPE_CELL /*1006*/:
                i3 = 2;
                break;
            default:
                if (i2 != 1007) {
                    i3 = 4;
                    break;
                } else {
                    i3 = 8208;
                    break;
                }
        }
        return z ? i3 | 4096 : i3;
    }

    MSCursorResultSet(JtdsStatement jtdsStatement, String str, String str2, ParamInfo[] paramInfoArr, int i, int i2) throws SQLException {
        super(jtdsStatement, i, i2, null);
        cursorCreate(str, str2, paramInfoArr);
        if (this.asyncCursor) {
            cursorFetch(FETCH_REPEAT, 0);
        }
    }

    /* access modifiers changed from: protected */
    public Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        ParamInfo paramInfo;
        Object colValue = super.setColValue(i, i2, obj, i3);
        if (this.onInsertRow || getCurrentRow() != null) {
            int i4 = i - 1;
            ColInfo colInfo = this.columns[i4];
            if (this.onInsertRow) {
                paramInfo = this.insertRow[i4];
            } else {
                if (this.updateRow == null) {
                    this.updateRow = new ParamInfo[this.columnCount];
                }
                paramInfo = this.updateRow[i4];
            }
            if (paramInfo == null) {
                paramInfo = new ParamInfo(-1, TdsData.isUnicode(colInfo));
                StringBuilder sb = new StringBuilder();
                sb.append('@');
                sb.append(colInfo.realName);
                paramInfo.name = sb.toString();
                paramInfo.collation = colInfo.collation;
                paramInfo.charsetInfo = colInfo.charsetInfo;
                if (this.onInsertRow) {
                    this.insertRow[i4] = paramInfo;
                } else {
                    this.updateRow[i4] = paramInfo;
                }
            }
            boolean z = true;
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
                if (!"ntext".equals(colInfo.sqlType)) {
                    if (!"nchar".equals(colInfo.sqlType)) {
                        if (!"nvarchar".equals(colInfo.sqlType)) {
                            z = false;
                        }
                    }
                }
                paramInfo.isUnicode = z;
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

    /* access modifiers changed from: protected */
    public Object getColumn(int i) throws SQLException {
        checkOpen();
        boolean z = true;
        if (i < 1 || i > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", (Object) Integer.toString(i)), "07009");
        }
        if (!this.onInsertRow) {
            Object[] currentRow = getCurrentRow();
            if (currentRow != null) {
                if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
                    cursorFetch(FETCH_REPEAT, 0);
                    currentRow = getCurrentRow();
                }
                Object obj = currentRow[i - 1];
                if (obj != null) {
                    z = false;
                }
                this.wasNull = z;
                return obj;
            }
        }
        throw new SQLException(Messages.get("error.resultset.norow"), "24000");
    }

    /* JADX WARNING: type inference failed for: r9v15, types: [boolean] */
    /* JADX WARNING: type inference failed for: r9v16 */
    /* JADX WARNING: type inference failed for: r9v17 */
    /* JADX WARNING: type inference failed for: r9v24 */
    /* JADX WARNING: type inference failed for: r9v25 */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01e7, code lost:
        if (r3.intValue() == 2) goto L_0x01eb;
     */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r9v15, types: [boolean]
      assigns: []
      uses: [boolean, ?[int, short, byte, char]]
      mth insns count: 319
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
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01dc  */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void cursorCreate(java.lang.String r22, java.lang.String r23, net.sourceforge.jtds.jdbc.ParamInfo[] r24) throws java.sql.SQLException {
        /*
            r21 = this;
            r0 = r21
            r1 = r24
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.TdsCore r2 = r2.getTds()
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = r3.connection
            int r3 = r3.getPrepareSql()
            java.lang.String r4 = r0.cursorName
            r12 = 1008(0x3f0, float:1.413E-42)
            r13 = 1007(0x3ef, float:1.411E-42)
            r14 = 1003(0x3eb, float:1.406E-42)
            if (r4 == 0) goto L_0x0026
            int r4 = r0.resultSetType
            if (r4 != r14) goto L_0x0026
            int r4 = r0.concurrency
            if (r4 != r13) goto L_0x0026
            r0.concurrency = r12
        L_0x0026:
            r4 = 0
            if (r1 == 0) goto L_0x002d
            int r5 = r1.length
            if (r5 != 0) goto L_0x002d
            r1 = r4
        L_0x002d:
            int r5 = r2.getTdsVersion()
            r11 = 1
            if (r5 != r11) goto L_0x003c
            if (r1 == 0) goto L_0x0038
            r3 = r4
            goto L_0x003a
        L_0x0038:
            r3 = r23
        L_0x003a:
            r5 = 0
            goto L_0x003f
        L_0x003c:
            r5 = r3
            r3 = r23
        L_0x003f:
            if (r1 == 0) goto L_0x004f
            if (r5 != 0) goto L_0x004f
            net.sourceforge.jtds.jdbc.JtdsStatement r6 = r0.statement
            net.sourceforge.jtds.jdbc.JtdsConnection r6 = r6.connection
            r7 = r22
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r7, r1, r6)
            r6 = r4
            goto L_0x0053
        L_0x004f:
            r7 = r22
            r6 = r1
            r1 = r7
        L_0x0053:
            java.lang.String r7 = "#jtds"
            if (r6 == 0) goto L_0x0063
            if (r3 == 0) goto L_0x005f
            boolean r8 = r3.startsWith(r7)
            if (r8 != 0) goto L_0x0063
        L_0x005f:
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Support.substituteParamMarkers(r1, r6)
        L_0x0063:
            r10 = 16
            r8 = 5
            if (r3 == 0) goto L_0x00db
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x00b8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            int r7 = r3.length()
            int r7 = r7 + r10
            if (r6 == 0) goto L_0x007b
            int r9 = r6.length
            int r9 = r9 * 5
            goto L_0x007c
        L_0x007b:
            r9 = 0
        L_0x007c:
            int r7 = r7 + r9
            r1.<init>(r7)
            java.lang.String r7 = "EXEC "
            r1.append(r7)
            r1.append(r3)
            r3 = 32
            r1.append(r3)
            r3 = 0
        L_0x008e:
            if (r6 == 0) goto L_0x00b3
            int r7 = r6.length
            if (r3 >= r7) goto L_0x00b3
            if (r3 == 0) goto L_0x009a
            r7 = 44
            r1.append(r7)
        L_0x009a:
            r7 = r6[r3]
            java.lang.String r7 = r7.name
            if (r7 == 0) goto L_0x00a8
            r7 = r6[r3]
            java.lang.String r7 = r7.name
            r1.append(r7)
            goto L_0x00b0
        L_0x00a8:
            java.lang.String r7 = "@P"
            r1.append(r7)
            r1.append(r3)
        L_0x00b0:
            int r3 = r3 + 1
            goto L_0x008e
        L_0x00b3:
            java.lang.String r1 = r1.toString()
            goto L_0x00db
        L_0x00b8:
            boolean r7 = net.sourceforge.jtds.jdbc.TdsCore.isPreparedProcedureName(r3)
            if (r7 == 0) goto L_0x00db
            java.lang.Integer r7 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x00c4 }
            r7.<init>(r3)     // Catch:{ NumberFormatException -> 0x00c4 }
            goto L_0x00dc
        L_0x00c4:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Invalid prepared statement handle: "
            r2.append(r4)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x00db:
            r7 = r4
        L_0x00dc:
            int r3 = r0.resultSetType
            int r9 = r0.concurrency
            if (r6 == 0) goto L_0x00e4
            r10 = 1
            goto L_0x00e5
        L_0x00e4:
            r10 = 0
        L_0x00e5:
            int r10 = getCursorScrollOpt(r3, r9, r10)
            int r3 = r0.concurrency
            int r9 = getCursorConcurrencyOpt(r3)
            net.sourceforge.jtds.jdbc.ParamInfo r3 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.Integer r12 = new java.lang.Integer
            r12.<init>(r10)
            r13 = 4
            r3.<init>(r13, r12, r11)
            net.sourceforge.jtds.jdbc.ParamInfo r12 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.Integer r14 = new java.lang.Integer
            r14.<init>(r9)
            r12.<init>(r13, r14, r11)
            net.sourceforge.jtds.jdbc.ParamInfo r14 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.Integer r15 = new java.lang.Integer
            int r8 = r0.fetchSize
            r15.<init>(r8)
            r14.<init>(r13, r15, r11)
            net.sourceforge.jtds.jdbc.ParamInfo r15 = new net.sourceforge.jtds.jdbc.ParamInfo
            r15.<init>(r13, r4, r11)
            r8 = 3
            if (r5 != r8) goto L_0x011e
            net.sourceforge.jtds.jdbc.ParamInfo r4 = new net.sourceforge.jtds.jdbc.ParamInfo
            r4.<init>(r13, r7, r11)
            goto L_0x011f
        L_0x011e:
            r4 = 0
        L_0x011f:
            if (r6 == 0) goto L_0x0142
            r8 = 0
        L_0x0122:
            int r11 = r6.length
            if (r8 >= r11) goto L_0x0132
            net.sourceforge.jtds.jdbc.JtdsStatement r11 = r0.statement
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r11.connection
            r13 = r6[r8]
            net.sourceforge.jtds.jdbc.TdsData.getNativeType(r11, r13)
            int r8 = r8 + 1
            r13 = 4
            goto L_0x0122
        L_0x0132:
            net.sourceforge.jtds.jdbc.ParamInfo r8 = new net.sourceforge.jtds.jdbc.ParamInfo
            java.lang.String r11 = net.sourceforge.jtds.jdbc.Support.getParameterDefinitions(r6)
            r18 = r9
            r9 = 4
            r13 = -1
            r8.<init>(r13, r11, r9)
            r16 = r8
            goto L_0x0148
        L_0x0142:
            r18 = r9
            r9 = 4
            r13 = -1
            r16 = 0
        L_0x0148:
            net.sourceforge.jtds.jdbc.ParamInfo r8 = new net.sourceforge.jtds.jdbc.ParamInfo
            r8.<init>(r13, r1, r9)
            r1 = 3
            if (r5 != r1) goto L_0x017c
            if (r7 == 0) goto L_0x017c
            if (r6 != 0) goto L_0x0159
            r1 = 5
            net.sourceforge.jtds.jdbc.ParamInfo[] r1 = new net.sourceforge.jtds.jdbc.ParamInfo[r1]
            r9 = 0
            goto L_0x0164
        L_0x0159:
            r1 = 5
            int r5 = r6.length
            int r5 = r5 + r1
            net.sourceforge.jtds.jdbc.ParamInfo[] r5 = new net.sourceforge.jtds.jdbc.ParamInfo[r5]
            int r8 = r6.length
            r9 = 0
            java.lang.System.arraycopy(r6, r9, r5, r1, r8)
            r1 = r5
        L_0x0164:
            r4.isOutput = r9
            r4.value = r7
            r1[r9] = r4
            r4 = 1
            r1[r4] = r15
            java.lang.Integer r4 = new java.lang.Integer
            r5 = r10 & -4097(0xffffffffffffefff, float:NaN)
            r4.<init>(r5)
            r3.value = r4
            java.lang.String r4 = "sp_cursorexecute"
            r6 = r1
            r5 = r4
            r11 = 1
            goto L_0x019a
        L_0x017c:
            if (r6 != 0) goto L_0x0183
            r1 = 5
            net.sourceforge.jtds.jdbc.ParamInfo[] r1 = new net.sourceforge.jtds.jdbc.ParamInfo[r1]
            r9 = 0
            goto L_0x0191
        L_0x0183:
            r1 = 5
            int r4 = r6.length
            r5 = 6
            int r4 = r4 + r5
            net.sourceforge.jtds.jdbc.ParamInfo[] r4 = new net.sourceforge.jtds.jdbc.ParamInfo[r4]
            int r7 = r6.length
            r9 = 0
            java.lang.System.arraycopy(r6, r9, r4, r5, r7)
            r4[r1] = r16
            r1 = r4
        L_0x0191:
            r1[r9] = r15
            r11 = 1
            r1[r11] = r8
            java.lang.String r4 = "sp_cursoropen"
            r6 = r1
            r5 = r4
        L_0x019a:
            r1 = 2
            r6[r1] = r3
            r8 = 3
            r6[r8] = r12
            r4 = 4
            r6[r4] = r14
            r4 = 0
            r7 = 0
            net.sourceforge.jtds.jdbc.JtdsStatement r9 = r0.statement
            int r9 = r9.getQueryTimeout()
            net.sourceforge.jtds.jdbc.JtdsStatement r13 = r0.statement
            int r13 = r13.getMaxRows()
            net.sourceforge.jtds.jdbc.JtdsStatement r8 = r0.statement
            int r16 = r8.getMaxFieldSize()
            r17 = 1
            r19 = r3
            r3 = r2
            r8 = r9
            r20 = r18
            r9 = r13
            r13 = r10
            r10 = r16
            r1 = 1
            r11 = r17
            r3.executeSQL(r4, r5, r6, r7, r8, r9, r10, r11)
            r0.processOutput(r2, r1)
            r3 = r13 & 8192(0x2000, float:1.14794E-41)
            if (r3 == 0) goto L_0x01d2
            r0.cursorPos = r1
        L_0x01d2:
            java.lang.Integer r3 = r2.getReturnStatus()
            java.lang.String r11 = "24000"
            java.lang.String r16 = "error.resultset.openfail"
            if (r3 == 0) goto L_0x0335
            int r4 = r3.intValue()
            if (r4 == 0) goto L_0x01ea
            int r4 = r3.intValue()
            r5 = 2
            if (r4 != r5) goto L_0x0335
            goto L_0x01eb
        L_0x01ea:
            r5 = 2
        L_0x01eb:
            int r3 = r3.intValue()
            if (r3 != r5) goto L_0x01f3
            r3 = 1
            goto L_0x01f4
        L_0x01f3:
            r3 = 0
        L_0x01f4:
            r0.asyncCursor = r3
            net.sourceforge.jtds.jdbc.ParamInfo r3 = r0.PARAM_CURSOR_HANDLE
            java.lang.Object r4 = r15.getOutValue()
            r3.value = r4
            java.lang.Object r3 = r19.getOutValue()
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r15 = r3.intValue()
            java.lang.Object r3 = r12.getOutValue()
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r12 = r3.intValue()
            java.lang.Object r3 = r14.getOutValue()
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r0.rowsInResult = r3
            java.lang.String r3 = r0.cursorName
            if (r3 == 0) goto L_0x027c
            r3 = 3
            net.sourceforge.jtds.jdbc.ParamInfo[] r6 = new net.sourceforge.jtds.jdbc.ParamInfo[r3]
            net.sourceforge.jtds.jdbc.ParamInfo r3 = r0.PARAM_CURSOR_HANDLE
            r14 = 0
            r6[r14] = r3
            net.sourceforge.jtds.jdbc.ParamInfo r3 = r0.PARAM_OPTYPE
            java.lang.Integer r4 = new java.lang.Integer
            r5 = 2
            r4.<init>(r5)
            r3.value = r4
            net.sourceforge.jtds.jdbc.ParamInfo r3 = r0.PARAM_OPTYPE
            r6[r1] = r3
            net.sourceforge.jtds.jdbc.ParamInfo r3 = new net.sourceforge.jtds.jdbc.ParamInfo
            r4 = 12
            java.lang.String r7 = r0.cursorName
            r8 = 4
            r3.<init>(r4, r7, r8)
            r6[r5] = r3
            r4 = 0
            r7 = 1
            r8 = 0
            r9 = -1
            r10 = -1
            r17 = 1
            java.lang.String r5 = "sp_cursoroption"
            r3 = r2
            r14 = r11
            r11 = r17
            r3.executeSQL(r4, r5, r6, r7, r8, r9, r10, r11)
            r2.clearResponseQueue()
            java.lang.Integer r2 = r2.getReturnStatus()
            int r2 = r2.intValue()
            if (r2 == 0) goto L_0x0273
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            java.sql.SQLException r3 = new java.sql.SQLException
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r16)
            r3.<init>(r4, r14)
            r2.addException(r3)
        L_0x0273:
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            r2.checkErrors()
        L_0x027c:
            r2 = r13 & 4095(0xfff, float:5.738E-42)
            if (r15 != r2) goto L_0x0285
            r2 = r20
            if (r12 == r2) goto L_0x0334
            goto L_0x0287
        L_0x0285:
            r2 = r20
        L_0x0287:
            r3 = 8
            java.lang.String r4 = "01000"
            if (r15 == r13) goto L_0x02cc
            if (r15 == r1) goto L_0x02bf
            r5 = 2
            if (r15 == r5) goto L_0x02bc
            r5 = 4
            if (r15 == r5) goto L_0x02b9
            if (r15 == r3) goto L_0x02b6
            r5 = 16
            if (r15 == r5) goto L_0x02b9
            int r14 = r0.resultSetType
            net.sourceforge.jtds.jdbc.JtdsStatement r5 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r5 = r5.getMessages()
            java.sql.SQLWarning r6 = new java.sql.SQLWarning
            java.lang.String r7 = java.lang.Integer.toString(r15)
            java.lang.String r8 = "warning.cursortype"
            java.lang.String r7 = net.sourceforge.jtds.jdbc.Messages.get(r8, r7)
            r6.<init>(r7, r4)
            r5.addWarning(r6)
            goto L_0x02c1
        L_0x02b6:
            r14 = 1004(0x3ec, float:1.407E-42)
            goto L_0x02c1
        L_0x02b9:
            r14 = 1003(0x3eb, float:1.406E-42)
            goto L_0x02c1
        L_0x02bc:
            r14 = 1006(0x3ee, float:1.41E-42)
            goto L_0x02c1
        L_0x02bf:
            r14 = 1005(0x3ed, float:1.408E-42)
        L_0x02c1:
            int r5 = r0.resultSetType
            if (r14 >= r5) goto L_0x02c7
            r11 = 1
            goto L_0x02c8
        L_0x02c7:
            r11 = 0
        L_0x02c8:
            r0.resultSetType = r14
            r9 = r11
            goto L_0x02cd
        L_0x02cc:
            r9 = 0
        L_0x02cd:
            if (r12 == r2) goto L_0x030a
            if (r12 == r1) goto L_0x02fe
            r2 = 2
            if (r12 == r2) goto L_0x02fb
            r2 = 4
            if (r12 == r2) goto L_0x02f8
            if (r12 == r3) goto L_0x02f5
            int r2 = r0.concurrency
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r0.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r3 = r3.getMessages()
            java.sql.SQLWarning r5 = new java.sql.SQLWarning
            java.lang.String r6 = java.lang.Integer.toString(r12)
            java.lang.String r7 = "warning.concurrtype"
            java.lang.String r6 = net.sourceforge.jtds.jdbc.Messages.get(r7, r6)
            r5.<init>(r6, r4)
            r3.addWarning(r5)
            r12 = r2
            goto L_0x0300
        L_0x02f5:
            r12 = 1010(0x3f2, float:1.415E-42)
            goto L_0x0300
        L_0x02f8:
            r12 = 1008(0x3f0, float:1.413E-42)
            goto L_0x0300
        L_0x02fb:
            r12 = 1009(0x3f1, float:1.414E-42)
            goto L_0x0300
        L_0x02fe:
            r12 = 1007(0x3ef, float:1.411E-42)
        L_0x0300:
            int r2 = r0.concurrency
            if (r12 >= r2) goto L_0x0306
            r15 = 1
            goto L_0x0307
        L_0x0306:
            r15 = 0
        L_0x0307:
            r0.concurrency = r12
            r9 = r15
        L_0x030a:
            if (r9 == 0) goto L_0x0334
            net.sourceforge.jtds.jdbc.JtdsStatement r1 = r0.statement
            java.sql.SQLWarning r2 = new java.sql.SQLWarning
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r5 = r0.resultSetType
            r3.append(r5)
            java.lang.String r5 = "/"
            r3.append(r5)
            int r5 = r0.concurrency
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            java.lang.String r5 = "warning.cursordowngraded"
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r5, r3)
            r2.<init>(r3, r4)
            r1.addWarning(r2)
        L_0x0334:
            return
        L_0x0335:
            r14 = r11
            java.sql.SQLException r1 = new java.sql.SQLException
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r16)
            r1.<init>(r2, r14)
            goto L_0x0341
        L_0x0340:
            throw r1
        L_0x0341:
            goto L_0x0340
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.MSCursorResultSet.cursorCreate(java.lang.String, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[]):void");
    }

    private boolean cursorFetch(Integer num, int i) throws SQLException {
        Integer num2 = num;
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        int i2 = (num2 == FETCH_ABSOLUTE || num2 == FETCH_RELATIVE) ? i : 1;
        ParamInfo[] paramInfoArr = new ParamInfo[4];
        paramInfoArr[0] = this.PARAM_CURSOR_HANDLE;
        this.PARAM_FETCHTYPE.value = num2;
        paramInfoArr[1] = this.PARAM_FETCHTYPE;
        this.PARAM_ROWNUM_IN.value = new Integer(i2);
        paramInfoArr[2] = this.PARAM_ROWNUM_IN;
        if (((Integer) this.PARAM_NUMROWS_IN.value).intValue() != this.fetchSize) {
            this.PARAM_NUMROWS_IN.value = new Integer(this.fetchSize);
            this.rowCache = new Object[this.fetchSize][];
        }
        paramInfoArr[3] = this.PARAM_NUMROWS_IN;
        synchronized (tds) {
            tds.executeSQL(null, "sp_cursorfetch", paramInfoArr, true, 0, 0, this.statement.getMaxFieldSize(), false);
            this.PARAM_FETCHTYPE.value = FETCH_INFO;
            paramInfoArr[1] = this.PARAM_FETCHTYPE;
            this.PARAM_ROWNUM_OUT.clearOutValue();
            paramInfoArr[2] = this.PARAM_ROWNUM_OUT;
            this.PARAM_NUMROWS_OUT.clearOutValue();
            paramInfoArr[3] = this.PARAM_NUMROWS_OUT;
            tds.executeSQL(null, "sp_cursorfetch", paramInfoArr, true, this.statement.getQueryTimeout(), -1, -1, true);
        }
        processOutput(tds, false);
        int intValue = ((Integer) this.PARAM_ROWNUM_OUT.getOutValue()).intValue();
        this.cursorPos = intValue;
        if (num2 != FETCH_REPEAT) {
            this.pos = intValue;
        }
        this.rowsInResult = ((Integer) this.PARAM_NUMROWS_OUT.getOutValue()).intValue();
        if (this.rowsInResult < 0) {
            this.rowsInResult = 0 - this.rowsInResult;
        }
        if (getCurrentRow() != null) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void cursor(java.lang.Integer r20, net.sourceforge.jtds.jdbc.ParamInfo[] r21) throws java.sql.SQLException {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            r2 = r21
            net.sourceforge.jtds.jdbc.JtdsStatement r3 = r1.statement
            net.sourceforge.jtds.jdbc.TdsCore r3 = r3.getTds()
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r1.statement
            r4.clearWarnings()
            java.lang.Integer r4 = CURSOR_OP_DELETE
            r13 = 3
            r14 = 4
            if (r0 != r4) goto L_0x001a
            net.sourceforge.jtds.jdbc.ParamInfo[] r4 = new net.sourceforge.jtds.jdbc.ParamInfo[r13]
            goto L_0x0021
        L_0x001a:
            if (r2 == 0) goto L_0x01d0
            int r4 = r1.columnCount
            int r4 = r4 + r14
            net.sourceforge.jtds.jdbc.ParamInfo[] r4 = new net.sourceforge.jtds.jdbc.ParamInfo[r4]
        L_0x0021:
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_CURSOR_HANDLE
            r15 = 0
            r4[r15] = r5
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_OPTYPE
            r5.value = r0
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_OPTYPE
            r16 = 1
            r4[r16] = r5
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_ROWNUM
            java.lang.Integer r6 = new java.lang.Integer
            int r7 = r1.pos
            int r8 = r1.cursorPos
            int r7 = r7 - r8
            int r7 = r7 + 1
            r6.<init>(r7)
            r5.value = r6
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_ROWNUM
            r17 = 2
            r4[r17] = r5
            if (r2 == 0) goto L_0x00f5
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_TABLE
            r4[r13] = r5
            int r5 = r1.columnCount
            r6 = 0
            r7 = 0
            r8 = 4
        L_0x0051:
            if (r7 >= r5) goto L_0x00c2
            r9 = r2[r7]
            net.sourceforge.jtds.jdbc.ColInfo[] r10 = r1.columns
            r10 = r10[r7]
            if (r9 == 0) goto L_0x007f
            boolean r11 = r9.isSet
            if (r11 == 0) goto L_0x007f
            boolean r11 = r10.isWriteable
            if (r11 == 0) goto L_0x0069
            int r11 = r8 + 1
            r4[r8] = r9
            r8 = r11
            goto L_0x007f
        L_0x0069:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r2 = "error.resultset.insert"
            int r7 = r7 + 1
            java.lang.String r3 = java.lang.Integer.toString(r7)
            java.lang.String r4 = r10.realName
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2, r3, r4)
            java.lang.String r3 = "24000"
            r0.<init>(r2, r3)
            throw r0
        L_0x007f:
            if (r6 != 0) goto L_0x00bf
            java.lang.String r9 = r10.tableName
            if (r9 == 0) goto L_0x00bf
            java.lang.String r6 = r10.catalog
            if (r6 != 0) goto L_0x0091
            java.lang.String r6 = r10.schema
            if (r6 == 0) goto L_0x008e
            goto L_0x0091
        L_0x008e:
            java.lang.String r6 = r10.tableName
            goto L_0x00bf
        L_0x0091:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r9 = r10.catalog
            if (r9 == 0) goto L_0x009d
            java.lang.String r9 = r10.catalog
            goto L_0x009f
        L_0x009d:
            java.lang.String r9 = ""
        L_0x009f:
            r6.append(r9)
            r9 = 46
            r6.append(r9)
            java.lang.String r11 = r10.schema
            if (r11 == 0) goto L_0x00ae
            java.lang.String r11 = r10.schema
            goto L_0x00b0
        L_0x00ae:
            java.lang.String r11 = ""
        L_0x00b0:
            r6.append(r11)
            r6.append(r9)
            java.lang.String r9 = r10.tableName
            r6.append(r9)
            java.lang.String r6 = r6.toString()
        L_0x00bf:
            int r7 = r7 + 1
            goto L_0x0051
        L_0x00c2:
            if (r8 != r14) goto L_0x00eb
            java.lang.Integer r7 = CURSOR_OP_INSERT
            if (r0 != r7) goto L_0x00ea
            net.sourceforge.jtds.jdbc.ParamInfo r7 = new net.sourceforge.jtds.jdbc.ParamInfo
            r9 = 12
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "insert "
            r10.append(r11)
            r10.append(r6)
            java.lang.String r6 = " default values"
            r10.append(r6)
            java.lang.String r6 = r10.toString()
            r7.<init>(r9, r6, r14)
            r4[r8] = r7
            int r8 = r8 + 1
            goto L_0x00eb
        L_0x00ea:
            return
        L_0x00eb:
            int r5 = r5 + r14
            if (r8 == r5) goto L_0x00f5
            net.sourceforge.jtds.jdbc.ParamInfo[] r5 = new net.sourceforge.jtds.jdbc.ParamInfo[r8]
            java.lang.System.arraycopy(r4, r15, r5, r15, r8)
            r12 = r5
            goto L_0x00f6
        L_0x00f5:
            r12 = r4
        L_0x00f6:
            monitor-enter(r3)
            r5 = 0
            java.lang.String r6 = "sp_cursor"
            r8 = 0
            r9 = 0
            r10 = -1
            r11 = -1
            r18 = 0
            r4 = r3
            r7 = r12
            r13 = r12
            r12 = r18
            r4.executeSQL(r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x01cd }
            int r4 = r13.length     // Catch:{ all -> 0x01cd }
            if (r4 == r14) goto L_0x0113
            net.sourceforge.jtds.jdbc.ParamInfo[] r4 = new net.sourceforge.jtds.jdbc.ParamInfo[r14]     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r5 = r1.PARAM_CURSOR_HANDLE     // Catch:{ all -> 0x01cd }
            r4[r15] = r5     // Catch:{ all -> 0x01cd }
            r7 = r4
            goto L_0x0114
        L_0x0113:
            r7 = r13
        L_0x0114:
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_FETCHTYPE     // Catch:{ all -> 0x01cd }
            java.lang.Integer r5 = FETCH_INFO     // Catch:{ all -> 0x01cd }
            r4.value = r5     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_FETCHTYPE     // Catch:{ all -> 0x01cd }
            r7[r16] = r4     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_ROWNUM_OUT     // Catch:{ all -> 0x01cd }
            r4.clearOutValue()     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_ROWNUM_OUT     // Catch:{ all -> 0x01cd }
            r7[r17] = r4     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_NUMROWS_OUT     // Catch:{ all -> 0x01cd }
            r4.clearOutValue()     // Catch:{ all -> 0x01cd }
            net.sourceforge.jtds.jdbc.ParamInfo r4 = r1.PARAM_NUMROWS_OUT     // Catch:{ all -> 0x01cd }
            r5 = 3
            r7[r5] = r4     // Catch:{ all -> 0x01cd }
            r5 = 0
            java.lang.String r6 = "sp_cursorfetch"
            r8 = 1
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r1.statement     // Catch:{ all -> 0x01cd }
            int r9 = r4.getQueryTimeout()     // Catch:{ all -> 0x01cd }
            r10 = -1
            r11 = -1
            r12 = 1
            r4 = r3
            r4.executeSQL(r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x01cd }
            monitor-exit(r3)     // Catch:{ all -> 0x01cd }
            r3.consumeOneResponse()
            net.sourceforge.jtds.jdbc.JtdsStatement r4 = r1.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r4 = r4.getMessages()
            r4.checkErrors()
            java.lang.Integer r4 = r3.getReturnStatus()
            int r4 = r4.intValue()
            if (r4 != 0) goto L_0x01bf
            if (r2 == 0) goto L_0x016a
        L_0x015b:
            int r4 = r2.length
            if (r15 >= r4) goto L_0x016a
            r4 = r2[r15]
            if (r4 == 0) goto L_0x0167
            r4 = r2[r15]
            r4.clearInValue()
        L_0x0167:
            int r15 = r15 + 1
            goto L_0x015b
        L_0x016a:
            r3.clearResponseQueue()
            net.sourceforge.jtds.jdbc.JtdsStatement r2 = r1.statement
            net.sourceforge.jtds.jdbc.SQLDiagnostic r2 = r2.getMessages()
            r2.checkErrors()
            net.sourceforge.jtds.jdbc.ParamInfo r2 = r1.PARAM_ROWNUM_OUT
            java.lang.Object r2 = r2.getOutValue()
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r1.cursorPos = r2
            net.sourceforge.jtds.jdbc.ParamInfo r2 = r1.PARAM_NUMROWS_OUT
            java.lang.Object r2 = r2.getOutValue()
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r1.rowsInResult = r2
            java.lang.Integer r2 = CURSOR_OP_DELETE
            if (r0 == r2) goto L_0x019a
            java.lang.Integer r2 = CURSOR_OP_UPDATE
            if (r0 != r2) goto L_0x01b0
        L_0x019a:
            java.lang.Object[] r2 = r19.getCurrentRow()
            if (r2 == 0) goto L_0x01b1
            net.sourceforge.jtds.jdbc.ColInfo[] r3 = r1.columns
            int r3 = r3.length
            int r3 = r3 + -1
            java.lang.Integer r4 = CURSOR_OP_DELETE
            if (r0 != r4) goto L_0x01ac
            java.lang.Integer r0 = SQL_ROW_DELETED
            goto L_0x01ae
        L_0x01ac:
            java.lang.Integer r0 = SQL_ROW_DIRTY
        L_0x01ae:
            r2[r3] = r0
        L_0x01b0:
            return
        L_0x01b1:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r2 = "error.resultset.updatefail"
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2)
            java.lang.String r3 = "24000"
            r0.<init>(r2, r3)
            throw r0
        L_0x01bf:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r2 = "error.resultset.cursorfail"
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2)
            java.lang.String r3 = "24000"
            r0.<init>(r2, r3)
            throw r0
        L_0x01cd:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x01cd }
            throw r0
        L_0x01d0:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r2 = "error.resultset.update"
            java.lang.String r2 = net.sourceforge.jtds.jdbc.Messages.get(r2)
            java.lang.String r3 = "24000"
            r0.<init>(r2, r3)
            goto L_0x01df
        L_0x01de:
            throw r0
        L_0x01df:
            goto L_0x01de
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.MSCursorResultSet.cursor(java.lang.Integer, net.sourceforge.jtds.jdbc.ParamInfo[]):void");
    }

    private void cursorClose() throws SQLException {
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        tds.clearResponseQueue();
        SQLException sQLException = this.statement.getMessages().exceptions;
        TdsCore tdsCore = tds;
        tdsCore.executeSQL(null, "sp_cursorclose", new ParamInfo[]{this.PARAM_CURSOR_HANDLE}, false, this.statement.getQueryTimeout(), -1, -1, true);
        tds.clearResponseQueue();
        if (sQLException == null) {
            this.statement.getMessages().checkErrors();
        } else {
            sQLException.setNextException(this.statement.getMessages().exceptions);
            throw sQLException;
        }
    }

    private void processOutput(TdsCore tdsCore, boolean z) throws SQLException {
        while (!tdsCore.getMoreResults()) {
            if (tdsCore.isEndOfResponse()) {
                break;
            }
        }
        int i = 0;
        if (tdsCore.isResultSet()) {
            if (z) {
                this.columns = copyInfo(tdsCore.getColumns());
                this.columnCount = getColumnCount(this.columns);
            }
            if (tdsCore.isRowData() || tdsCore.getNextRow()) {
                do {
                    int i2 = i + 1;
                    this.rowCache[i] = copyRow(tdsCore.getRowData());
                    i = i2;
                } while (tdsCore.getNextRow());
            }
        } else if (z) {
            this.statement.getMessages().addException(new SQLException(Messages.get("error.statement.noresult"), "24000"));
        }
        while (true) {
            Object[][] objArr = this.rowCache;
            if (i < objArr.length) {
                objArr[i] = null;
                i++;
            } else {
                tdsCore.clearResponseQueue();
                this.statement.messages.checkErrors();
                return;
            }
        }
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != -1) {
            cursorFetch(FETCH_ABSOLUTE, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        }
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != 0) {
            cursorFetch(FETCH_ABSOLUTE, 0);
        }
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (!this.onInsertRow) {
            int i = 0;
            while (true) {
                ParamInfo[] paramInfoArr = this.updateRow;
                if (paramInfoArr != null && i < paramInfoArr.length) {
                    if (paramInfoArr[i] != null) {
                        paramInfoArr[i].clearInValue();
                    }
                    i++;
                } else {
                    return;
                }
            }
        } else {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!this.statement.getConnection().isClosed()) {
                    cursorClose();
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
        String str = "24000";
        if (getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), str);
        } else if (!this.onInsertRow) {
            cursor(CURSOR_OP_DELETE, null);
        } else {
            throw new SQLException(Messages.get("error.resultset.insrow"), str);
        }
    }

    public void insertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            cursor(CURSOR_OP_INSERT, this.insertRow);
            return;
        }
        throw new SQLException(Messages.get("error.resultset.notinsrow"), "24000");
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.onInsertRow = false;
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.insertRow == null) {
            this.insertRow = new ParamInfo[this.columnCount];
        }
        this.onInsertRow = true;
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        if (!this.onInsertRow) {
            cursorFetch(FETCH_REPEAT, 0);
            return;
        }
        throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
    }

    public void updateRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        String str = "24000";
        if (getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), str);
        } else if (!this.onInsertRow) {
            ParamInfo[] paramInfoArr = this.updateRow;
            if (paramInfoArr != null) {
                cursor(CURSOR_OP_UPDATE, paramInfoArr);
            }
        } else {
            throw new SQLException(Messages.get("error.resultset.insrow"), str);
        }
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = 1;
        if (getCurrentRow() == null) {
            return cursorFetch(FETCH_FIRST, 0);
        }
        return true;
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = this.rowsInResult;
        if (!this.asyncCursor && getCurrentRow() != null) {
            return true;
        }
        if (!cursorFetch(FETCH_LAST, 0)) {
            return false;
        }
        this.pos = this.rowsInResult;
        return true;
    }

    public boolean next() throws SQLException {
        checkOpen();
        this.pos++;
        if (getCurrentRow() == null) {
            return cursorFetch(FETCH_NEXT, 0);
        }
        return true;
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos == 0) {
            return false;
        }
        int i = this.pos;
        this.pos--;
        if (i != -1 && getCurrentRow() != null) {
            return true;
        }
        boolean cursorFetch = cursorFetch(FETCH_PREVIOUS, 0);
        this.pos = i == -1 ? this.rowsInResult : i - 1;
        return cursorFetch;
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        Object[] currentRow = getCurrentRow();
        if (currentRow == null) {
            return false;
        }
        if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
            cursorFetch(FETCH_REPEAT, 0);
            currentRow = getCurrentRow();
        }
        return SQL_ROW_DELETED.equals(currentRow[this.columns.length - 1]);
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
        int i2;
        checkOpen();
        checkScrollable();
        if (i >= 0) {
            i2 = i;
        } else {
            i2 = (this.rowsInResult - i) + 1;
        }
        this.pos = i2;
        if (getCurrentRow() != null) {
            return true;
        }
        boolean cursorFetch = cursorFetch(FETCH_ABSOLUTE, i);
        if (this.cursorPos == 1 && i + this.rowsInResult < 0) {
            this.pos = 0;
            cursorFetch = false;
        }
        return cursorFetch;
    }

    public boolean relative(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = (this.pos == -1 ? this.rowsInResult + 1 : this.pos) + i;
        if (getCurrentRow() != null) {
            return true;
        }
        if (this.pos >= this.cursorPos) {
            return cursorFetch(FETCH_RELATIVE, this.pos - this.cursorPos);
        }
        int i2 = this.pos;
        boolean cursorFetch = cursorFetch(FETCH_RELATIVE, ((this.pos - this.cursorPos) - this.fetchSize) + 1);
        if (cursorFetch) {
            this.pos = i2;
        } else {
            this.pos = 0;
        }
        return cursorFetch;
    }

    /* access modifiers changed from: protected */
    public Object[] getCurrentRow() {
        if (this.pos >= this.cursorPos) {
            int i = this.pos;
            int i2 = this.cursorPos;
            Object[][] objArr = this.rowCache;
            if (i < i2 + objArr.length) {
                return objArr[this.pos - this.cursorPos];
            }
        }
        return null;
    }
}
