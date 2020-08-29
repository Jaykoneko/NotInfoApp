package net.sourceforge.jtds.jdbc;

import androidx.core.provider.FontsContractCompat.FontRequestCallback;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import net.sourceforge.jtds.util.Logger;

public class Support {
    private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal(1.0d);
    private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal(0.0d);
    private static final Date DATE_ZERO = new Date(0);
    private static final Double DOUBLE_ONE = new Double(1.0d);
    private static final Double DOUBLE_ZERO = new Double(0.0d);
    private static final Float FLOAT_ONE = new Float(1.0d);
    private static final Float FLOAT_ZERO = new Float(0.0d);
    private static final Integer INTEGER_ONE = new Integer(1);
    private static final Integer INTEGER_ZERO = new Integer(0);
    private static final Long LONG_ONE = new Long(1);
    private static final Long LONG_ZERO = new Long(0);
    private static final BigInteger MAX_VALUE_28 = new BigInteger("9999999999999999999999999999");
    private static final BigInteger MAX_VALUE_38 = new BigInteger("99999999999999999999999999999999999999");
    private static final BigDecimal MAX_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MAX_VALUE));
    private static final BigInteger MAX_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MAX_VALUE));
    private static final BigDecimal MIN_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MIN_VALUE));
    private static final BigInteger MIN_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MIN_VALUE));
    private static final Time TIME_ZERO = new Time(0);
    private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final HashMap typeMap;

    static int calculateNamedPipeBufferSize(int i, int i2) {
        return i2 == 0 ? i >= 3 ? 4096 : 512 : i2;
    }

    static Object castNumeric(Object obj, int i, int i2) {
        return null;
    }

    public static int convertLOBType(int i) {
        if (i == 2004) {
            return -4;
        }
        if (i != 2005) {
            return i;
        }
        return -1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0036, code lost:
        return "java.lang.Integer";
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.String getClassName(int r1) {
        /*
            r0 = 12
            if (r1 == r0) goto L_0x0040
            r0 = 16
            if (r1 == r0) goto L_0x003d
            r0 = 2004(0x7d4, float:2.808E-42)
            if (r1 == r0) goto L_0x003a
            r0 = 2005(0x7d5, float:2.81E-42)
            if (r1 == r0) goto L_0x0037
            switch(r1) {
                case -7: goto L_0x003d;
                case -6: goto L_0x0034;
                case -5: goto L_0x0031;
                case -4: goto L_0x003a;
                case -3: goto L_0x002e;
                case -2: goto L_0x002e;
                case -1: goto L_0x0037;
                default: goto L_0x0013;
            }
        L_0x0013:
            switch(r1) {
                case 1: goto L_0x0040;
                case 2: goto L_0x002b;
                case 3: goto L_0x002b;
                case 4: goto L_0x0034;
                case 5: goto L_0x0034;
                case 6: goto L_0x0028;
                case 7: goto L_0x0025;
                case 8: goto L_0x0028;
                default: goto L_0x0016;
            }
        L_0x0016:
            switch(r1) {
                case 91: goto L_0x0022;
                case 92: goto L_0x001f;
                case 93: goto L_0x001c;
                default: goto L_0x0019;
            }
        L_0x0019:
            java.lang.String r1 = "java.lang.Object"
            return r1
        L_0x001c:
            java.lang.String r1 = "java.sql.Timestamp"
            return r1
        L_0x001f:
            java.lang.String r1 = "java.sql.Time"
            return r1
        L_0x0022:
            java.lang.String r1 = "java.sql.Date"
            return r1
        L_0x0025:
            java.lang.String r1 = "java.lang.Float"
            return r1
        L_0x0028:
            java.lang.String r1 = "java.lang.Double"
            return r1
        L_0x002b:
            java.lang.String r1 = "java.math.BigDecimal"
            return r1
        L_0x002e:
            java.lang.String r1 = "[B"
            return r1
        L_0x0031:
            java.lang.String r1 = "java.lang.Long"
            return r1
        L_0x0034:
            java.lang.String r1 = "java.lang.Integer"
            return r1
        L_0x0037:
            java.lang.String r1 = "java.sql.Clob"
            return r1
        L_0x003a:
            java.lang.String r1 = "java.sql.Blob"
            return r1
        L_0x003d:
            java.lang.String r1 = "java.lang.Boolean"
            return r1
        L_0x0040:
            java.lang.String r1 = "java.lang.String"
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Support.getClassName(int):java.lang.String");
    }

    static String getJdbcTypeName(int i) {
        if (i == 12) {
            return "VARCHAR";
        }
        if (i == 16) {
            return "BOOLEAN";
        }
        if (i == 70) {
            return "DATALINK";
        }
        if (i == 1111) {
            return "OTHER";
        }
        if (i == 2009) {
            return "XML";
        }
        switch (i) {
            case -7:
                return "BIT";
            case -6:
                return "TINYINT";
            case -5:
                return "BIGINT";
            case FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /*-4*/:
                return "LONGVARBINARY";
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                return "VARBINARY";
            case -2:
                return "BINARY";
            case -1:
                return "LONGVARCHAR";
            case 0:
                return "NULL";
            case 1:
                return "CHAR";
            case 2:
                return "NUMERIC";
            case 3:
                return "DECIMAL";
            case 4:
                return "INTEGER";
            case 5:
                return "SMALLINT";
            case 6:
                return "FLOAT";
            case 7:
                return "REAL";
            case 8:
                return "DOUBLE";
            default:
                switch (i) {
                    case 91:
                        return "DATE";
                    case 92:
                        return "TIME";
                    case 93:
                        return "TIMESTAMP";
                    default:
                        switch (i) {
                            case 2000:
                                return "JAVA_OBJECT";
                            case 2001:
                                return "DISTINCT";
                            case 2002:
                                return "STRUCT";
                            case 2003:
                                return "ARRAY";
                            case 2004:
                                return "BLOB";
                            case 2005:
                                return "CLOB";
                            case 2006:
                                return "REF";
                            default:
                                return "ERROR";
                        }
                }
        }
    }

    static {
        HashMap hashMap = new HashMap();
        typeMap = hashMap;
        hashMap.put(Byte.class, new Integer(-6));
        typeMap.put(Short.class, new Integer(5));
        typeMap.put(Integer.class, new Integer(4));
        typeMap.put(Long.class, new Integer(-5));
        typeMap.put(Float.class, new Integer(7));
        typeMap.put(Double.class, new Integer(8));
        typeMap.put(BigDecimal.class, new Integer(3));
        typeMap.put(Boolean.class, new Integer(16));
        typeMap.put(byte[].class, new Integer(-3));
        typeMap.put(Date.class, new Integer(91));
        typeMap.put(Time.class, new Integer(92));
        typeMap.put(Timestamp.class, new Integer(93));
        typeMap.put(BlobImpl.class, new Integer(-4));
        typeMap.put(ClobImpl.class, new Integer(-1));
        typeMap.put(String.class, new Integer(12));
        typeMap.put(Blob.class, new Integer(-4));
        typeMap.put(Clob.class, new Integer(-1));
        typeMap.put(BigInteger.class, new Integer(-5));
    }

    public static String toHex(byte[] bArr) {
        if (r0 <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(r0 * 2);
        for (byte b : bArr) {
            byte b2 = b & 255;
            sb.append(hex[b2 >> 4]);
            sb.append(hex[b2 & TdsCore.SYBQUERY_PKT]);
        }
        return sb.toString();
    }

    static BigDecimal normalizeBigDecimal(BigDecimal bigDecimal, int i) throws SQLException {
        if (bigDecimal == null) {
            return null;
        }
        if (bigDecimal.scale() < 0) {
            bigDecimal = bigDecimal.setScale(0);
        }
        if (bigDecimal.scale() > i) {
            bigDecimal = bigDecimal.setScale(i, 4);
        }
        BigInteger bigInteger = i == 28 ? MAX_VALUE_28 : MAX_VALUE_38;
        while (bigDecimal.abs().unscaledValue().compareTo(bigInteger) > 0) {
            int scale = bigDecimal.scale() - 1;
            if (scale >= 0) {
                bigDecimal = bigDecimal.setScale(scale, 4);
            } else {
                throw new SQLException(Messages.get("error.normalize.numtoobig", (Object) String.valueOf(i)), "22000");
            }
        }
        return bigDecimal;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:(2:114|115)|116|117|118) */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:(2:153|154)|155|156|157) */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:(2:74|75)|76|77|78) */
    /* JADX WARNING: Can't wrap try/catch for region: R(7:449|(1:451)|452|453|454|455|456) */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x05df, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x05f1, code lost:
        throw new java.sql.SQLException(net.sourceforge.jtds.jdbc.Messages.get("error.generic.ioerror", (java.lang.Object) r0.getMessage()), "HY000");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x068b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x069d, code lost:
        throw new java.sql.SQLException(net.sourceforge.jtds.jdbc.Messages.get("error.generic.ioerror", (java.lang.Object) r0.getMessage()), "HY000");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x069e, code lost:
        r0 = r0.getSubString(1, (int) r0.length());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:?, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:39:0x0067, B:388:0x05ae, B:432:0x065f] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:116:0x01a4 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:155:0x024a */
    /* JADX WARNING: Missing exception handler attribute for start block: B:454:0x06c8 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:76:0x0102 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:296:0x0462=Splitter:B:296:0x0462, B:482:0x0722=Splitter:B:482:0x0722, B:454:0x06c8=Splitter:B:454:0x06c8, B:446:0x06ac=Splitter:B:446:0x06ac, B:518:0x079c=Splitter:B:518:0x079c, B:409:0x060f=Splitter:B:409:0x060f} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:76:0x0102=Splitter:B:76:0x0102, B:116:0x01a4=Splitter:B:116:0x01a4, B:155:0x024a=Splitter:B:155:0x024a, B:418:0x063a=Splitter:B:418:0x063a, B:487:0x073d=Splitter:B:487:0x073d} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Object convert(java.lang.Object r16, java.lang.Object r17, int r18, java.lang.String r19) throws java.sql.SQLException {
        /*
            r1 = r17
            r2 = r18
            r0 = 16
            if (r1 != 0) goto L_0x0027
            r1 = -7
            if (r2 == r1) goto L_0x0024
            r1 = -6
            if (r2 == r1) goto L_0x0021
            r1 = -5
            if (r2 == r1) goto L_0x001e
            if (r2 == r0) goto L_0x0024
            switch(r2) {
                case 4: goto L_0x0021;
                case 5: goto L_0x0021;
                case 6: goto L_0x001b;
                case 7: goto L_0x0018;
                case 8: goto L_0x001b;
                default: goto L_0x0016;
            }
        L_0x0016:
            r0 = 0
            return r0
        L_0x0018:
            java.lang.Float r0 = FLOAT_ZERO
            return r0
        L_0x001b:
            java.lang.Double r0 = DOUBLE_ZERO
            return r0
        L_0x001e:
            java.lang.Long r0 = LONG_ZERO
            return r0
        L_0x0021:
            java.lang.Integer r0 = INTEGER_ZERO
            return r0
        L_0x0024:
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            return r0
        L_0x0027:
            r3 = 12
            java.lang.String r4 = "error.normalize.lobtoobig"
            java.lang.String r5 = "1"
            r6 = 2147483647(0x7fffffff, double:1.060997895E-314)
            java.lang.String r8 = "error.convert.badnumber"
            java.lang.String r9 = "22000"
            r10 = 1
            if (r2 == r3) goto L_0x073d
            java.lang.String r12 = "error.convert.badtypes"
            if (r2 == r0) goto L_0x06e9
            r0 = 1111(0x457, float:1.557E-42)
            if (r2 == r0) goto L_0x06e8
            r0 = 2000(0x7d0, float:2.803E-42)
            if (r2 == r0) goto L_0x06d0
            r0 = 2004(0x7d4, float:2.808E-42)
            java.lang.String r13 = "ISO-8859-1"
            if (r2 == r0) goto L_0x063a
            r0 = 2005(0x7d5, float:2.81E-42)
            if (r2 == r0) goto L_0x05a2
            java.lang.String r0 = "22003"
            java.lang.String r14 = "error.convert.numericoverflow"
            switch(r2) {
                case -7: goto L_0x06e9;
                case -6: goto L_0x0533;
                case -5: goto L_0x0487;
                case -4: goto L_0x063a;
                case -3: goto L_0x042b;
                case -2: goto L_0x042b;
                case -1: goto L_0x05a2;
                default: goto L_0x0055;
            }
        L_0x0055:
            switch(r2) {
                case 1: goto L_0x073d;
                case 2: goto L_0x03f8;
                case 3: goto L_0x03f8;
                case 4: goto L_0x0374;
                case 5: goto L_0x02f4;
                case 6: goto L_0x02a6;
                case 7: goto L_0x0258;
                case 8: goto L_0x02a6;
                default: goto L_0x0058;
            }
        L_0x0058:
            r0 = 58
            r4 = 10
            java.lang.String r5 = "\\."
            r6 = 2
            r7 = 7
            r10 = 45
            r11 = 4
            r13 = 0
            switch(r2) {
                case 91: goto L_0x01b2;
                case 92: goto L_0x0110;
                case 93: goto L_0x007d;
                default: goto L_0x0067;
            }
        L_0x0067:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = "error.convert.badtypeconst"
            java.lang.String r4 = java.lang.String.valueOf(r17)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r5 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3, r4, r5)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "HY004"
            r0.<init>(r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x007d:
            boolean r3 = r1 instanceof net.sourceforge.jtds.jdbc.DateTime     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0089
            r0 = r1
            net.sourceforge.jtds.jdbc.DateTime r0 = (net.sourceforge.jtds.jdbc.DateTime) r0     // Catch:{ NumberFormatException -> 0x07af }
            java.sql.Timestamp r0 = r0.toTimestamp()     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0089:
            boolean r3 = r1 instanceof java.sql.Timestamp     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x008e
            return r1
        L_0x008e:
            boolean r3 = r1 instanceof java.sql.Date     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x009f
            java.sql.Timestamp r0 = new java.sql.Timestamp     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.sql.Date r3 = (java.sql.Date) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x009f:
            boolean r3 = r1 instanceof java.sql.Time     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x00b0
            java.sql.Timestamp r0 = new java.sql.Timestamp     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.sql.Time r3 = (java.sql.Time) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x00b0:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            int r12 = r3.length()     // Catch:{ NumberFormatException -> 0x07af }
            if (r12 <= r4) goto L_0x00cc
            char r4 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x0102 }
            if (r4 != r10) goto L_0x00cc
            java.sql.Timestamp r0 = java.sql.Timestamp.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x0102 }
            return r0
        L_0x00cc:
            if (r12 <= r7) goto L_0x00e2
            char r4 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x0102 }
            if (r4 != r10) goto L_0x00e2
            java.sql.Timestamp r0 = new java.sql.Timestamp     // Catch:{ IllegalArgumentException -> 0x0102 }
            java.sql.Date r4 = java.sql.Date.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x0102 }
            long r4 = r4.getTime()     // Catch:{ IllegalArgumentException -> 0x0102 }
            r0.<init>(r4)     // Catch:{ IllegalArgumentException -> 0x0102 }
            return r0
        L_0x00e2:
            if (r12 <= r7) goto L_0x0102
            char r4 = r3.charAt(r6)     // Catch:{ IllegalArgumentException -> 0x0102 }
            if (r4 != r0) goto L_0x0102
            java.sql.Timestamp r0 = new java.sql.Timestamp     // Catch:{ IllegalArgumentException -> 0x0102 }
            java.lang.String[] r4 = r3.split(r5)     // Catch:{ IllegalArgumentException -> 0x0102 }
            r4 = r4[r13]     // Catch:{ IllegalArgumentException -> 0x0102 }
            java.lang.String r4 = r4.trim()     // Catch:{ IllegalArgumentException -> 0x0102 }
            java.sql.Time r4 = java.sql.Time.valueOf(r4)     // Catch:{ IllegalArgumentException -> 0x0102 }
            long r4 = r4.getTime()     // Catch:{ IllegalArgumentException -> 0x0102 }
            r0.<init>(r4)     // Catch:{ IllegalArgumentException -> 0x0102 }
            return r0
        L_0x0102:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r8, r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0110:
            boolean r3 = r1 instanceof net.sourceforge.jtds.jdbc.DateTime     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x011c
            r0 = r1
            net.sourceforge.jtds.jdbc.DateTime r0 = (net.sourceforge.jtds.jdbc.DateTime) r0     // Catch:{ NumberFormatException -> 0x07af }
            java.sql.Time r0 = r0.toTime()     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x011c:
            boolean r3 = r1 instanceof java.sql.Time     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0121
            return r1
        L_0x0121:
            boolean r3 = r1 instanceof java.sql.Date     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0128
            java.sql.Time r0 = TIME_ZERO     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0128:
            boolean r3 = r1 instanceof java.sql.Timestamp     // Catch:{ NumberFormatException -> 0x07af }
            r14 = 1
            if (r3 == 0) goto L_0x0152
            java.util.GregorianCalendar r0 = new java.util.GregorianCalendar     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.util.Date r3 = (java.util.Date) r3     // Catch:{ NumberFormatException -> 0x07af }
            r0.setTime(r3)     // Catch:{ NumberFormatException -> 0x07af }
            r3 = 1970(0x7b2, float:2.76E-42)
            r0.set(r14, r3)     // Catch:{ NumberFormatException -> 0x07af }
            r0.set(r6, r13)     // Catch:{ NumberFormatException -> 0x07af }
            r3 = 5
            r0.set(r3, r14)     // Catch:{ NumberFormatException -> 0x07af }
            java.sql.Time r3 = new java.sql.Time     // Catch:{ NumberFormatException -> 0x07af }
            java.util.Date r0 = r0.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            long r4 = r0.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x07af }
            return r3
        L_0x0152:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String[] r3 = r3.split(r5)     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3[r13]     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            int r5 = r3.length()     // Catch:{ NumberFormatException -> 0x07af }
            r12 = 8
            if (r5 != r12) goto L_0x017a
            char r6 = r3.charAt(r6)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            if (r6 != r0) goto L_0x017a
            java.sql.Time r0 = java.sql.Time.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            return r0
        L_0x017a:
            if (r5 <= r4) goto L_0x0196
            char r0 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            if (r0 != r10) goto L_0x0196
            java.lang.String r0 = " "
            java.lang.String[] r0 = r3.split(r0)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            int r4 = r0.length     // Catch:{ IllegalArgumentException -> 0x01a4 }
            if (r4 <= r14) goto L_0x01a4
            r0 = r0[r14]     // Catch:{ IllegalArgumentException -> 0x01a4 }
            java.lang.String r0 = r0.trim()     // Catch:{ IllegalArgumentException -> 0x01a4 }
            java.sql.Time r0 = java.sql.Time.valueOf(r0)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            return r0
        L_0x0196:
            if (r5 <= r7) goto L_0x01a4
            char r0 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            if (r0 != r10) goto L_0x01a4
            java.sql.Date.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x01a4 }
            java.sql.Time r0 = TIME_ZERO     // Catch:{ IllegalArgumentException -> 0x01a4 }
            return r0
        L_0x01a4:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r8, r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x01b2:
            boolean r14 = r1 instanceof net.sourceforge.jtds.jdbc.DateTime     // Catch:{ NumberFormatException -> 0x07af }
            if (r14 == 0) goto L_0x01be
            r0 = r1
            net.sourceforge.jtds.jdbc.DateTime r0 = (net.sourceforge.jtds.jdbc.DateTime) r0     // Catch:{ NumberFormatException -> 0x07af }
            java.sql.Date r0 = r0.toDate()     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x01be:
            boolean r14 = r1 instanceof java.sql.Date     // Catch:{ NumberFormatException -> 0x07af }
            if (r14 == 0) goto L_0x01c3
            return r1
        L_0x01c3:
            boolean r14 = r1 instanceof java.sql.Time     // Catch:{ NumberFormatException -> 0x07af }
            if (r14 == 0) goto L_0x01ca
            java.sql.Date r0 = DATE_ZERO     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x01ca:
            boolean r14 = r1 instanceof java.sql.Timestamp     // Catch:{ NumberFormatException -> 0x07af }
            if (r14 == 0) goto L_0x01f9
            java.util.GregorianCalendar r0 = new java.util.GregorianCalendar     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>()     // Catch:{ NumberFormatException -> 0x07af }
            r4 = r1
            java.util.Date r4 = (java.util.Date) r4     // Catch:{ NumberFormatException -> 0x07af }
            r0.setTime(r4)     // Catch:{ NumberFormatException -> 0x07af }
            r4 = 11
            r0.set(r4, r13)     // Catch:{ NumberFormatException -> 0x07af }
            r0.set(r3, r13)     // Catch:{ NumberFormatException -> 0x07af }
            r3 = 13
            r0.set(r3, r13)     // Catch:{ NumberFormatException -> 0x07af }
            r3 = 14
            r0.set(r3, r13)     // Catch:{ NumberFormatException -> 0x07af }
            java.sql.Date r3 = new java.sql.Date     // Catch:{ NumberFormatException -> 0x07af }
            java.util.Date r0 = r0.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            long r4 = r0.getTime()     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x07af }
            return r3
        L_0x01f9:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            int r12 = r3.length()     // Catch:{ NumberFormatException -> 0x07af }
            if (r12 <= r7) goto L_0x0219
            r14 = 11
            if (r12 >= r14) goto L_0x0219
            char r14 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x024a }
            if (r14 != r10) goto L_0x0219
            java.sql.Date r0 = java.sql.Date.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x024a }
            return r0
        L_0x0219:
            if (r12 <= r4) goto L_0x0232
            char r4 = r3.charAt(r11)     // Catch:{ IllegalArgumentException -> 0x024a }
            if (r4 != r10) goto L_0x0232
            java.lang.String r0 = " "
            java.lang.String[] r0 = r3.split(r0)     // Catch:{ IllegalArgumentException -> 0x024a }
            r0 = r0[r13]     // Catch:{ IllegalArgumentException -> 0x024a }
            java.lang.String r0 = r0.trim()     // Catch:{ IllegalArgumentException -> 0x024a }
            java.sql.Date r0 = java.sql.Date.valueOf(r0)     // Catch:{ IllegalArgumentException -> 0x024a }
            return r0
        L_0x0232:
            if (r12 <= r7) goto L_0x024a
            char r4 = r3.charAt(r6)     // Catch:{ IllegalArgumentException -> 0x024a }
            if (r4 != r0) goto L_0x024a
            java.lang.String[] r0 = r3.split(r5)     // Catch:{ IllegalArgumentException -> 0x024a }
            r0 = r0[r13]     // Catch:{ IllegalArgumentException -> 0x024a }
            java.lang.String r0 = r0.trim()     // Catch:{ IllegalArgumentException -> 0x024a }
            java.sql.Time.valueOf(r0)     // Catch:{ IllegalArgumentException -> 0x024a }
            java.sql.Date r0 = DATE_ZERO     // Catch:{ IllegalArgumentException -> 0x024a }
            return r0
        L_0x024a:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r8, r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0258:
            boolean r0 = r1 instanceof java.lang.Float     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x025d
            return r1
        L_0x025d:
            boolean r0 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0271
            java.lang.Float r0 = new java.lang.Float     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            float r3 = (float) r3     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0271:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0282
            java.lang.Float r0 = new java.lang.Float     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            float r3 = r3.floatValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0282:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0293
            java.lang.Float r0 = new java.lang.Float     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0293:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0721
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02a3
            java.lang.Float r0 = FLOAT_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x02a5
        L_0x02a3:
            java.lang.Float r0 = FLOAT_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x02a5:
            return r0
        L_0x02a6:
            boolean r0 = r1 instanceof java.lang.Double     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02ab
            return r1
        L_0x02ab:
            boolean r0 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02bf
            java.lang.Double r0 = new java.lang.Double     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            double r3 = (double) r3     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x02bf:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02d0
            java.lang.Double r0 = new java.lang.Double     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            double r3 = r3.doubleValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x02d0:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02e1
            java.lang.Double r0 = new java.lang.Double     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x02e1:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0721
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x02f1
            java.lang.Double r0 = DOUBLE_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x02f3
        L_0x02f1:
            java.lang.Double r0 = DOUBLE_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x02f3:
            return r0
        L_0x02f4:
            boolean r3 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0307
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0304
            java.lang.Integer r0 = INTEGER_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0306
        L_0x0304:
            java.lang.Integer r0 = INTEGER_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0306:
            return r0
        L_0x0307:
            boolean r3 = r1 instanceof java.lang.Short     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0318
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Short r3 = (java.lang.Short) r3     // Catch:{ NumberFormatException -> 0x07af }
            short r3 = r3.shortValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0318:
            boolean r3 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x032b
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x032b:
            boolean r3 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0337
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x034b
        L_0x0337:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            java.lang.Long r3 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r4 = r1
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = r4.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
        L_0x034b:
            r5 = -32768(0xffffffffffff8000, double:NaN)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x0366
            r5 = 32767(0x7fff, double:1.6189E-319)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 > 0) goto L_0x0366
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.Long r5 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r5.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = r5.intValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0366:
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r14, r1, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0374:
            boolean r3 = r1 instanceof java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0379
            return r1
        L_0x0379:
            boolean r3 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x038c
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0389
            java.lang.Integer r0 = INTEGER_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x038b
        L_0x0389:
            java.lang.Integer r0 = INTEGER_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x038b:
            return r0
        L_0x038c:
            boolean r3 = r1 instanceof java.lang.Short     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x039d
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Short r3 = (java.lang.Short) r3     // Catch:{ NumberFormatException -> 0x07af }
            short r3 = r3.shortValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x039d:
            boolean r3 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x03b0
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x03b0:
            boolean r3 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x03bc
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x03d0
        L_0x03bc:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            java.lang.Long r3 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r4 = r1
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = r4.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
        L_0x03d0:
            r10 = -2147483648(0xffffffff80000000, double:NaN)
            int r5 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r5 < 0) goto L_0x03ea
            int r5 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r5 > 0) goto L_0x03ea
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.Long r5 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r5.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = r5.intValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x03ea:
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r14, r1, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x03f8:
            boolean r0 = r1 instanceof java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x03fd
            return r1
        L_0x03fd:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x040b
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r17.toString()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x040b:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0418
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0418:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0721
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0428
            java.math.BigDecimal r0 = BIG_DECIMAL_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x042a
        L_0x0428:
            java.math.BigDecimal r0 = BIG_DECIMAL_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x042a:
            return r0
        L_0x042b:
            boolean r0 = r1 instanceof byte[]     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0430
            return r1
        L_0x0430:
            boolean r0 = r1 instanceof java.sql.Blob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0441
            r0 = r1
            java.sql.Blob r0 = (java.sql.Blob) r0     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r0.length()     // Catch:{ NumberFormatException -> 0x07af }
            int r4 = (int) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte[] r0 = r0.getBytes(r10, r4)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0441:
            boolean r0 = r1 instanceof java.sql.Clob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0460
            r0 = r1
            java.sql.Clob r0 = (java.sql.Clob) r0     // Catch:{ NumberFormatException -> 0x07af }
            long r14 = r0.length()     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r3 > 0) goto L_0x0456
            int r3 = (int) r14     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = r0.getSubString(r10, r3)     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0461
        L_0x0456:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0460:
            r0 = r1
        L_0x0461:
            boolean r1 = r0 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x047b
            if (r19 != 0) goto L_0x0469
            goto L_0x046b
        L_0x0469:
            r13 = r19
        L_0x046b:
            r1 = r0
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ UnsupportedEncodingException -> 0x0473 }
            byte[] r0 = r1.getBytes(r13)     // Catch:{ UnsupportedEncodingException -> 0x0473 }
            return r0
        L_0x0473:
            r1 = r0
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ NumberFormatException -> 0x073a }
            byte[] r0 = r1.getBytes()     // Catch:{ NumberFormatException -> 0x073a }
            return r0
        L_0x047b:
            boolean r1 = r0 instanceof net.sourceforge.jtds.jdbc.UniqueIdentifier     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x0722
            r1 = r0
            net.sourceforge.jtds.jdbc.UniqueIdentifier r1 = (net.sourceforge.jtds.jdbc.UniqueIdentifier) r1     // Catch:{ NumberFormatException -> 0x073a }
            byte[] r0 = r1.getBytes()     // Catch:{ NumberFormatException -> 0x073a }
            return r0
        L_0x0487:
            boolean r3 = r1 instanceof java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x04b6
            r3 = r1
            java.math.BigDecimal r3 = (java.math.BigDecimal) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.math.BigDecimal r4 = MIN_VALUE_LONG_BD     // Catch:{ NumberFormatException -> 0x07af }
            int r4 = r3.compareTo(r4)     // Catch:{ NumberFormatException -> 0x07af }
            if (r4 < 0) goto L_0x04a8
            java.math.BigDecimal r4 = MAX_VALUE_LONG_BD     // Catch:{ NumberFormatException -> 0x07af }
            int r4 = r3.compareTo(r4)     // Catch:{ NumberFormatException -> 0x07af }
            if (r4 > 0) goto L_0x04a8
            java.lang.Long r0 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x04a8:
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r14, r1, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x04b6:
            boolean r3 = r1 instanceof java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x04bb
            return r1
        L_0x04bb:
            boolean r3 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x04ce
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x04cb
            java.lang.Long r0 = LONG_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x04cd
        L_0x04cb:
            java.lang.Long r0 = LONG_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x04cd:
            return r0
        L_0x04ce:
            boolean r3 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x04e2
            java.lang.Long r0 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            long r3 = (long) r3     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x04e2:
            boolean r3 = r1 instanceof java.math.BigInteger     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0511
            r3 = r1
            java.math.BigInteger r3 = (java.math.BigInteger) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.math.BigInteger r4 = MIN_VALUE_LONG_BI     // Catch:{ NumberFormatException -> 0x07af }
            int r4 = r3.compareTo(r4)     // Catch:{ NumberFormatException -> 0x07af }
            if (r4 < 0) goto L_0x0503
            java.math.BigInteger r4 = MAX_VALUE_LONG_BI     // Catch:{ NumberFormatException -> 0x07af }
            int r4 = r3.compareTo(r4)     // Catch:{ NumberFormatException -> 0x07af }
            if (r4 > 0) goto L_0x0503
            java.lang.Long r0 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0503:
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r14, r1, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0511:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0522
            java.lang.Long r0 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0522:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0721
            java.lang.Long r0 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0533:
            boolean r3 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0546
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0543
            java.lang.Integer r0 = INTEGER_ONE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0545
        L_0x0543:
            java.lang.Integer r0 = INTEGER_ZERO     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0545:
            return r0
        L_0x0546:
            boolean r3 = r1 instanceof java.lang.Byte     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0559
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r1
            java.lang.Byte r3 = (java.lang.Byte) r3     // Catch:{ NumberFormatException -> 0x07af }
            byte r3 = r3.byteValue()     // Catch:{ NumberFormatException -> 0x07af }
            r3 = r3 & 255(0xff, float:3.57E-43)
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0559:
            boolean r3 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0565
            r3 = r1
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0579
        L_0x0565:
            boolean r3 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 == 0) goto L_0x0721
            java.lang.Long r3 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r4 = r1
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = r4.trim()     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x07af }
            long r3 = r3.longValue()     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0579:
            r5 = -128(0xffffffffffffff80, double:NaN)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x0594
            r5 = 127(0x7f, double:6.27E-322)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 > 0) goto L_0x0594
            java.lang.Integer r0 = new java.lang.Integer     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.Long r5 = new java.lang.Long     // Catch:{ NumberFormatException -> 0x07af }
            r5.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = r5.intValue()     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0594:
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = net.sourceforge.jtds.jdbc.Messages.get(r14, r1, r4)     // Catch:{ NumberFormatException -> 0x07af }
            r3.<init>(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x05a2:
            boolean r0 = r1 instanceof java.sql.Clob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x05a7
            return r1
        L_0x05a7:
            boolean r0 = r1 instanceof java.sql.Blob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x05f2
            r0 = r1
            java.sql.Blob r0 = (java.sql.Blob) r0     // Catch:{ NumberFormatException -> 0x07af }
            java.io.InputStream r0 = r0.getBinaryStream()     // Catch:{ IOException -> 0x05df }
            net.sourceforge.jtds.jdbc.ClobImpl r3 = new net.sourceforge.jtds.jdbc.ClobImpl     // Catch:{ IOException -> 0x05df }
            net.sourceforge.jtds.jdbc.JtdsConnection r4 = getConnection(r16)     // Catch:{ IOException -> 0x05df }
            r3.<init>(r4)     // Catch:{ IOException -> 0x05df }
            java.io.Writer r4 = r3.setCharacterStream(r10)     // Catch:{ IOException -> 0x05df }
        L_0x05bf:
            int r5 = r0.read()     // Catch:{ IOException -> 0x05df }
            if (r5 < 0) goto L_0x05d8
            char[] r6 = hex     // Catch:{ IOException -> 0x05df }
            int r7 = r5 >> 4
            char r6 = r6[r7]     // Catch:{ IOException -> 0x05df }
            r4.write(r6)     // Catch:{ IOException -> 0x05df }
            char[] r6 = hex     // Catch:{ IOException -> 0x05df }
            r5 = r5 & 15
            char r5 = r6[r5]     // Catch:{ IOException -> 0x05df }
            r4.write(r5)     // Catch:{ IOException -> 0x05df }
            goto L_0x05bf
        L_0x05d8:
            r4.close()     // Catch:{ IOException -> 0x05df }
            r0.close()     // Catch:{ IOException -> 0x05df }
            return r3
        L_0x05df:
            r0 = move-exception
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "error.generic.ioerror"
            java.lang.String r0 = r0.getMessage()     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Messages.get(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "HY000"
            r3.<init>(r0, r4)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x05f2:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0604
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0601
            r0 = r5
            goto L_0x060e
        L_0x0601:
            java.lang.String r0 = "0"
            goto L_0x060e
        L_0x0604:
            boolean r0 = r1 instanceof byte[]     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 != 0) goto L_0x060d
            java.lang.String r0 = r17.toString()     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x060e
        L_0x060d:
            r0 = r1
        L_0x060e:
            boolean r1 = r0 instanceof byte[]     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x0629
            net.sourceforge.jtds.jdbc.ClobImpl r1 = new net.sourceforge.jtds.jdbc.ClobImpl     // Catch:{ NumberFormatException -> 0x073a }
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = getConnection(r16)     // Catch:{ NumberFormatException -> 0x073a }
            r1.<init>(r3)     // Catch:{ NumberFormatException -> 0x073a }
            r3 = r0
            byte[] r3 = (byte[]) r3     // Catch:{ NumberFormatException -> 0x073a }
            byte[] r3 = (byte[]) r3     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r3 = toHex(r3)     // Catch:{ NumberFormatException -> 0x073a }
            r1.setString(r10, r3)     // Catch:{ NumberFormatException -> 0x073a }
            return r1
        L_0x0629:
            boolean r1 = r0 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x0722
            net.sourceforge.jtds.jdbc.ClobImpl r1 = new net.sourceforge.jtds.jdbc.ClobImpl     // Catch:{ NumberFormatException -> 0x073a }
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = getConnection(r16)     // Catch:{ NumberFormatException -> 0x073a }
            r4 = r0
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x073a }
            r1.<init>(r3, r4)     // Catch:{ NumberFormatException -> 0x073a }
            return r1
        L_0x063a:
            boolean r0 = r1 instanceof java.sql.Blob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x063f
            return r1
        L_0x063f:
            boolean r0 = r1 instanceof byte[]     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0652
            net.sourceforge.jtds.jdbc.BlobImpl r0 = new net.sourceforge.jtds.jdbc.BlobImpl     // Catch:{ NumberFormatException -> 0x07af }
            net.sourceforge.jtds.jdbc.JtdsConnection r3 = getConnection(r16)     // Catch:{ NumberFormatException -> 0x07af }
            r4 = r1
            byte[] r4 = (byte[]) r4     // Catch:{ NumberFormatException -> 0x07af }
            byte[] r4 = (byte[]) r4     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0652:
            boolean r0 = r1 instanceof java.sql.Clob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x06a8
            r0 = r1
            java.sql.Clob r0 = (java.sql.Clob) r0     // Catch:{ NumberFormatException -> 0x07af }
            if (r19 != 0) goto L_0x065d
            r3 = r13
            goto L_0x065f
        L_0x065d:
            r3 = r19
        L_0x065f:
            java.io.Reader r4 = r0.getCharacterStream()     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            net.sourceforge.jtds.jdbc.BlobImpl r5 = new net.sourceforge.jtds.jdbc.BlobImpl     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            net.sourceforge.jtds.jdbc.JtdsConnection r6 = getConnection(r16)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            r5.<init>(r6)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            java.io.BufferedWriter r6 = new java.io.BufferedWriter     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            java.io.OutputStreamWriter r7 = new java.io.OutputStreamWriter     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            java.io.OutputStream r14 = r5.setBinaryStream(r10)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            r7.<init>(r14, r3)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            r6.<init>(r7)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
        L_0x067a:
            int r7 = r4.read()     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            if (r7 < 0) goto L_0x0684
            r6.write(r7)     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            goto L_0x067a
        L_0x0684:
            r6.close()     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            r4.close()     // Catch:{ UnsupportedEncodingException -> 0x069e, IOException -> 0x068b }
            return r5
        L_0x068b:
            r0 = move-exception
            java.sql.SQLException r3 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "error.generic.ioerror"
            java.lang.String r0 = r0.getMessage()     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = net.sourceforge.jtds.jdbc.Messages.get(r4, r0)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "HY000"
            r3.<init>(r0, r4)     // Catch:{ NumberFormatException -> 0x07af }
            throw r3     // Catch:{ NumberFormatException -> 0x07af }
        L_0x069e:
            long r4 = r0.length()     // Catch:{ NumberFormatException -> 0x07af }
            int r5 = (int) r4     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = r0.getSubString(r10, r5)     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x06ab
        L_0x06a8:
            r3 = r19
            r0 = r1
        L_0x06ab:
            boolean r1 = r0 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x0722
            net.sourceforge.jtds.jdbc.BlobImpl r1 = new net.sourceforge.jtds.jdbc.BlobImpl     // Catch:{ NumberFormatException -> 0x073a }
            net.sourceforge.jtds.jdbc.JtdsConnection r4 = getConnection(r16)     // Catch:{ NumberFormatException -> 0x073a }
            r1.<init>(r4)     // Catch:{ NumberFormatException -> 0x073a }
            r4 = r0
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x073a }
            if (r3 != 0) goto L_0x06bf
            goto L_0x06c0
        L_0x06bf:
            r13 = r3
        L_0x06c0:
            byte[] r3 = r4.getBytes(r13)     // Catch:{ UnsupportedEncodingException -> 0x06c8 }
            r1.setBytes(r10, r3)     // Catch:{ UnsupportedEncodingException -> 0x06c8 }
            goto L_0x06cf
        L_0x06c8:
            byte[] r3 = r4.getBytes()     // Catch:{ NumberFormatException -> 0x073a }
            r1.setBytes(r10, r3)     // Catch:{ NumberFormatException -> 0x073a }
        L_0x06cf:
            return r1
        L_0x06d0:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.Class r3 = r17.getClass()     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = r3.getName()     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r12, r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r4 = "22005"
            r0.<init>(r3, r4)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x06e8:
            return r1
        L_0x06e9:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x06ee
            return r1
        L_0x06ee:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0701
            r0 = r1
            java.lang.Number r0 = (java.lang.Number) r0     // Catch:{ NumberFormatException -> 0x07af }
            int r0 = r0.intValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 != 0) goto L_0x06fe
            java.lang.Boolean r0 = java.lang.Boolean.FALSE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0700
        L_0x06fe:
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0700:
            return r0
        L_0x0701:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0721
            r0 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = r0.trim()     // Catch:{ NumberFormatException -> 0x07af }
            boolean r3 = r5.equals(r0)     // Catch:{ NumberFormatException -> 0x07af }
            if (r3 != 0) goto L_0x071e
            java.lang.String r3 = "true"
            boolean r0 = r3.equalsIgnoreCase(r0)     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x071b
            goto L_0x071e
        L_0x071b:
            java.lang.Boolean r0 = java.lang.Boolean.FALSE     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x0720
        L_0x071e:
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ NumberFormatException -> 0x07af }
        L_0x0720:
            return r0
        L_0x0721:
            r0 = r1
        L_0x0722:
            java.sql.SQLException r1 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.Class r3 = r0.getClass()     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r3 = r3.getName()     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r4 = getJdbcTypeName(r18)     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r12, r3, r4)     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r4 = "22005"
            r1.<init>(r3, r4)     // Catch:{ NumberFormatException -> 0x073a }
            throw r1     // Catch:{ NumberFormatException -> 0x073a }
        L_0x073a:
            r1 = r0
            goto L_0x07af
        L_0x073d:
            boolean r0 = r1 instanceof java.lang.String     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0742
            return r1
        L_0x0742:
            boolean r0 = r1 instanceof java.lang.Number     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x074b
            java.lang.String r0 = r17.toString()     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x074b:
            boolean r0 = r1 instanceof java.lang.Boolean     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x075c
            r0 = r1
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ NumberFormatException -> 0x07af }
            boolean r0 = r0.booleanValue()     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x0759
            goto L_0x075b
        L_0x0759:
            java.lang.String r5 = "0"
        L_0x075b:
            return r5
        L_0x075c:
            boolean r0 = r1 instanceof java.sql.Clob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x077b
            r0 = r1
            java.sql.Clob r0 = (java.sql.Clob) r0     // Catch:{ NumberFormatException -> 0x07af }
            long r12 = r0.length()     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r3 > 0) goto L_0x0771
            int r3 = (int) r12     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r0 = r0.getSubString(r10, r3)     // Catch:{ NumberFormatException -> 0x07af }
            return r0
        L_0x0771:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x077b:
            boolean r0 = r1 instanceof java.sql.Blob     // Catch:{ NumberFormatException -> 0x07af }
            if (r0 == 0) goto L_0x079a
            r0 = r1
            java.sql.Blob r0 = (java.sql.Blob) r0     // Catch:{ NumberFormatException -> 0x07af }
            long r12 = r0.length()     // Catch:{ NumberFormatException -> 0x07af }
            int r3 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r3 > 0) goto L_0x0790
            int r3 = (int) r12     // Catch:{ NumberFormatException -> 0x07af }
            byte[] r0 = r0.getBytes(r10, r3)     // Catch:{ NumberFormatException -> 0x07af }
            goto L_0x079b
        L_0x0790:
            java.sql.SQLException r0 = new java.sql.SQLException     // Catch:{ NumberFormatException -> 0x07af }
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r4)     // Catch:{ NumberFormatException -> 0x07af }
            r0.<init>(r3, r9)     // Catch:{ NumberFormatException -> 0x07af }
            throw r0     // Catch:{ NumberFormatException -> 0x07af }
        L_0x079a:
            r0 = r1
        L_0x079b:
            boolean r1 = r0 instanceof byte[]     // Catch:{ NumberFormatException -> 0x073a }
            if (r1 == 0) goto L_0x07aa
            r1 = r0
            byte[] r1 = (byte[]) r1     // Catch:{ NumberFormatException -> 0x073a }
            byte[] r1 = (byte[]) r1     // Catch:{ NumberFormatException -> 0x073a }
            java.lang.String r0 = toHex(r1)     // Catch:{ NumberFormatException -> 0x073a }
            return r0
        L_0x07aa:
            java.lang.String r0 = r0.toString()     // Catch:{ NumberFormatException -> 0x073a }
            return r0
        L_0x07af:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r1 = java.lang.String.valueOf(r1)
            java.lang.String r2 = getJdbcTypeName(r18)
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r8, r1, r2)
            r0.<init>(r1, r9)
            goto L_0x07c2
        L_0x07c1:
            throw r0
        L_0x07c2:
            goto L_0x07c1
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Support.convert(java.lang.Object, java.lang.Object, int, java.lang.String):java.lang.Object");
    }

    static int getJdbcType(Object obj) {
        if (obj == null) {
            return 0;
        }
        return getJdbcType(obj.getClass());
    }

    static int getJdbcType(Class cls) {
        if (cls == null) {
            return 2000;
        }
        Object obj = typeMap.get(cls);
        if (obj == null) {
            return getJdbcType(cls.getSuperclass());
        }
        return ((Integer) obj).intValue();
    }

    static void embedData(StringBuilder sb, Object obj, boolean z, JtdsConnection jtdsConnection) throws SQLException {
        sb.append(' ');
        if (obj == null) {
            sb.append("NULL ");
            return;
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        }
        if (obj instanceof DateTime) {
            sb.append('\'');
            sb.append(obj);
            sb.append('\'');
        } else {
            int i = 0;
            char c = '0';
            if (obj instanceof byte[]) {
                byte[] bArr = (byte[]) obj;
                int length = bArr.length;
                if (length >= 0) {
                    sb.append('0');
                    sb.append('x');
                    if (length != 0 || jtdsConnection.getTdsVersion() >= 3) {
                        while (i < length) {
                            byte b = bArr[i] & 255;
                            sb.append(hex[b >> 4]);
                            sb.append(hex[b & TdsCore.SYBQUERY_PKT]);
                            i++;
                        }
                    } else {
                        sb.append('0');
                        sb.append('0');
                    }
                }
            } else if (obj instanceof String) {
                String str = (String) obj;
                int length2 = str.length();
                if (z) {
                    sb.append('N');
                }
                sb.append('\'');
                while (i < length2) {
                    char charAt = str.charAt(i);
                    if (charAt == '\'') {
                        sb.append('\'');
                    }
                    sb.append(charAt);
                    i++;
                }
                sb.append('\'');
            } else if (obj instanceof Date) {
                DateTime dateTime = new DateTime((Date) obj);
                sb.append('\'');
                sb.append(dateTime);
                sb.append('\'');
            } else if (obj instanceof Time) {
                DateTime dateTime2 = new DateTime((Time) obj);
                sb.append('\'');
                sb.append(dateTime2);
                sb.append('\'');
            } else if (obj instanceof Timestamp) {
                DateTime dateTime3 = new DateTime((Timestamp) obj);
                sb.append('\'');
                sb.append(dateTime3);
                sb.append('\'');
            } else if (obj instanceof Boolean) {
                if (((Boolean) obj).booleanValue()) {
                    c = '1';
                }
                sb.append(c);
            } else if (obj instanceof BigDecimal) {
                String obj2 = obj.toString();
                int maxPrecision = jtdsConnection.getMaxPrecision();
                if (obj2.charAt(0) == '-') {
                    maxPrecision++;
                }
                if (obj2.indexOf(46) >= 0) {
                    maxPrecision++;
                }
                if (obj2.length() > maxPrecision) {
                    sb.append(obj2.substring(0, maxPrecision));
                } else {
                    sb.append(obj2);
                }
            } else {
                sb.append(obj.toString());
            }
        }
        sb.append(' ');
    }

    static String getStatementKey(String str, ParamInfo[] paramInfoArr, int i, String str2, boolean z, boolean z2) {
        StringBuilder sb;
        if (i == 1) {
            sb = new StringBuilder(str2.length() + 1 + str.length() + (paramInfoArr.length * 11));
            sb.append(z2 ? 'C' : 'X');
            sb.append(str2);
            sb.append(str);
            for (ParamInfo paramInfo : paramInfoArr) {
                sb.append(paramInfo.sqlType);
            }
        } else {
            sb = new StringBuilder(str.length() + 2);
            sb.append(z ? 'T' : 'F');
            sb.append(str);
        }
        return sb.toString();
    }

    static String getParameterDefinitions(ParamInfo[] paramInfoArr) {
        StringBuilder sb = new StringBuilder(paramInfoArr.length * 15);
        int i = 0;
        while (i < paramInfoArr.length) {
            if (paramInfoArr[i].name == null) {
                sb.append("@P");
                sb.append(i);
            } else {
                sb.append(paramInfoArr[i].name);
            }
            sb.append(' ');
            sb.append(paramInfoArr[i].sqlType);
            i++;
            if (i < paramInfoArr.length) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    static String substituteParamMarkers(String str, ParamInfo[] paramInfoArr) {
        char[] cArr = new char[(str.length() + (paramInfoArr.length * 7))];
        StringBuilder sb = new StringBuilder(4);
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < paramInfoArr.length; i3++) {
            int i4 = paramInfoArr[i3].markerPos;
            if (i4 > 0) {
                str.getChars(i, i4, cArr, i2);
                int i5 = i2 + (i4 - i);
                i = i4 + 1;
                int i6 = i5 + 1;
                cArr[i5] = ' ';
                int i7 = i6 + 1;
                cArr[i6] = '@';
                int i8 = i7 + 1;
                cArr[i7] = 'P';
                sb.setLength(0);
                sb.append(i3);
                sb.getChars(0, sb.length(), cArr, i8);
                int length = i8 + sb.length();
                i2 = length + 1;
                cArr[length] = ' ';
            }
        }
        if (i < str.length()) {
            str.getChars(i, str.length(), cArr, i2);
            i2 += str.length() - i;
        }
        return new String(cArr, 0, i2);
    }

    static String substituteParameters(String str, ParamInfo[] paramInfoArr, JtdsConnection jtdsConnection) throws SQLException {
        int length;
        int length2 = str.length();
        int i = 0;
        while (i < paramInfoArr.length) {
            if (paramInfoArr[i].isRetVal || paramInfoArr[i].isSet || paramInfoArr[i].isOutput) {
                Object obj = paramInfoArr[i].value;
                if ((obj instanceof InputStream) || (obj instanceof Reader)) {
                    try {
                        String str2 = "US-ASCII";
                        if (paramInfoArr[i].jdbcType != -1) {
                            if (paramInfoArr[i].jdbcType != 2005) {
                                if (paramInfoArr[i].jdbcType != 12) {
                                    obj = paramInfoArr[i].getBytes(str2);
                                    paramInfoArr[i].value = obj;
                                }
                            }
                        }
                        obj = paramInfoArr[i].getString(str2);
                        paramInfoArr[i].value = obj;
                    } catch (IOException e) {
                        throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
                    }
                }
                if (obj instanceof String) {
                    length = ((String) obj).length() + 5;
                } else if (obj instanceof byte[]) {
                    length = (((byte[]) obj).length * 2) + 4;
                } else {
                    length2 += 32;
                    i++;
                }
                length2 += length;
                i++;
            } else {
                throw new SQLException(Messages.get("error.prepare.paramnotset", (Object) Integer.toString(i + 1)), "07000");
            }
        }
        StringBuilder sb = new StringBuilder(length2 + 16);
        int i2 = 0;
        for (int i3 = 0; i3 < paramInfoArr.length; i3++) {
            int i4 = paramInfoArr[i3].markerPos;
            if (i4 > 0) {
                sb.append(str.substring(i2, paramInfoArr[i3].markerPos));
                i2 = i4 + 1;
                embedData(sb, paramInfoArr[i3].value, jtdsConnection.getTdsVersion() >= 3 && paramInfoArr[i3].isUnicode, jtdsConnection);
            }
        }
        if (i2 < str.length()) {
            sb.append(str.substring(i2));
        }
        return sb.toString();
    }

    static byte[] encodeString(String str, String str2) {
        try {
            return str2.getBytes(str);
        } catch (UnsupportedEncodingException unused) {
            return str2.getBytes();
        }
    }

    public static SQLWarning linkException(SQLWarning sQLWarning, Throwable th) {
        return (SQLWarning) linkException((Exception) sQLWarning, th);
    }

    public static SQLException linkException(SQLException sQLException, Throwable th) {
        return (SQLException) linkException((Exception) sQLException, th);
    }

    public static Throwable linkException(Exception exc, Throwable th) {
        try {
            exc.getClass().getMethod("initCause", new Class[]{Throwable.class}).invoke(exc, new Object[]{th});
        } catch (NoSuchMethodException unused) {
            if (Logger.isActive()) {
                Logger.logException((Exception) th);
            }
        } catch (Exception unused2) {
        }
        return exc;
    }

    public static long timeToZone(java.util.Date date, Calendar calendar) {
        java.util.Date time = calendar.getTime();
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            calendar.set(11, gregorianCalendar.get(11));
            calendar.set(12, gregorianCalendar.get(12));
            calendar.set(13, gregorianCalendar.get(13));
            calendar.set(14, gregorianCalendar.get(14));
            calendar.set(1, gregorianCalendar.get(1));
            calendar.set(2, gregorianCalendar.get(2));
            calendar.set(5, gregorianCalendar.get(5));
            return calendar.getTime().getTime();
        } finally {
            calendar.setTime(time);
        }
    }

    public static long timeFromZone(java.util.Date date, Calendar calendar) {
        java.util.Date time = calendar.getTime();
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            calendar.setTime(date);
            gregorianCalendar.set(11, calendar.get(11));
            gregorianCalendar.set(12, calendar.get(12));
            gregorianCalendar.set(13, calendar.get(13));
            gregorianCalendar.set(14, calendar.get(14));
            gregorianCalendar.set(1, calendar.get(1));
            gregorianCalendar.set(2, calendar.get(2));
            gregorianCalendar.set(5, calendar.get(5));
            return gregorianCalendar.getTime().getTime();
        } finally {
            calendar.setTime(time);
        }
    }

    public static Object convertLOB(Object obj) throws SQLException {
        if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            return clob.getSubString(1, (int) clob.length());
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        }
        return obj;
    }

    public static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    private static JtdsConnection getConnection(Object obj) {
        Connection connection;
        if (obj != null) {
            try {
                if (obj instanceof Connection) {
                    connection = (Connection) obj;
                } else if (obj instanceof Statement) {
                    connection = ((Statement) obj).getConnection();
                } else if (obj instanceof ResultSet) {
                    connection = ((ResultSet) obj).getStatement().getConnection();
                } else {
                    throw new IllegalArgumentException("callerReference is invalid.");
                }
                return (JtdsConnection) connection;
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("callerReference cannot be null.");
        }
    }

    private Support() {
    }
}
