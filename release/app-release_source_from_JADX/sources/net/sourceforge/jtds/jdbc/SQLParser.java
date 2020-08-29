package net.sourceforge.jtds.jdbc;

import androidx.core.app.NotificationCompat;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.cache.SQLCacheKey;
import net.sourceforge.jtds.jdbc.cache.SimpleLRUCache;

class SQLParser {
    private static final SimpleLRUCache<SQLCacheKey, CachedSQLQuery> _Cache = new SimpleLRUCache<>(1000);
    private static HashMap cvMap = new HashMap();
    private static final byte[] dateMask = {35, 35, 35, 35, 45, 35, 35, 45, 35, 35};
    private static HashMap fnMap = new HashMap();
    private static boolean[] identifierChar = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false};
    private static HashMap msFnMap = new HashMap();
    private static final byte[] timeMask = {35, 35, 58, 35, 35, 58, 35, 35};
    static final byte[] timestampMask = {35, 35, 35, 35, 45, 35, 35, 45, 35, 35, 32, 35, 35, 58, 35, 35, 58, 35, 35};
    private final JtdsConnection connection;

    /* renamed from: d */
    private int f116d;

    /* renamed from: in */
    private final char[] f117in;
    private String keyWord;
    private final int len;
    private char[] out;
    private final ArrayList params;
    private String procName = "";

    /* renamed from: s */
    private int f118s;
    private final String sql;
    private String tableName;
    private char terminator;

    private static class CachedSQLQuery {
        final boolean[] paramIsRetVal;
        final boolean[] paramIsUnicode;
        final int[] paramMarkerPos;
        final String[] paramNames;
        final String[] parsedSql;

        CachedSQLQuery(String[] strArr, ArrayList arrayList) {
            this.parsedSql = strArr;
            if (arrayList != null) {
                int size = arrayList.size();
                this.paramNames = new String[size];
                this.paramMarkerPos = new int[size];
                this.paramIsRetVal = new boolean[size];
                this.paramIsUnicode = new boolean[size];
                for (int i = 0; i < size; i++) {
                    ParamInfo paramInfo = (ParamInfo) arrayList.get(i);
                    this.paramNames[i] = paramInfo.name;
                    this.paramMarkerPos[i] = paramInfo.markerPos;
                    this.paramIsRetVal[i] = paramInfo.isRetVal;
                    this.paramIsUnicode[i] = paramInfo.isUnicode;
                }
                return;
            }
            this.paramNames = null;
            this.paramMarkerPos = null;
            this.paramIsRetVal = null;
            this.paramIsUnicode = null;
        }
    }

    static {
        String str = "length";
        msFnMap.put(str, "len($)");
        msFnMap.put("truncate", "round($, 1)");
        fnMap.put("user", "user_name($)");
        fnMap.put("database", "db_name($)");
        fnMap.put("ifnull", "isnull($)");
        fnMap.put("now", "getdate($)");
        fnMap.put("atan2", "atn2($)");
        String str2 = "($)";
        fnMap.put("mod", str2);
        fnMap.put(str, "char_length($)");
        fnMap.put("locate", "charindex($)");
        fnMap.put("repeat", "replicate($)");
        fnMap.put("insert", "stuff($)");
        fnMap.put("lcase", "lower($)");
        fnMap.put("ucase", "upper($)");
        fnMap.put("concat", str2);
        fnMap.put("curdate", "convert(datetime, convert(varchar, getdate(), 112))");
        fnMap.put("curtime", "convert(datetime, convert(varchar, getdate(), 108))");
        fnMap.put("dayname", "datename(weekday,$)");
        fnMap.put("dayofmonth", "datepart(day,$)");
        fnMap.put("dayofweek", "((datepart(weekday,$)+@@DATEFIRST-1)%7+1)");
        fnMap.put("dayofyear", "datepart(dayofyear,$)");
        fnMap.put("hour", "datepart(hour,$)");
        fnMap.put("minute", "datepart(minute,$)");
        fnMap.put("second", "datepart(second,$)");
        fnMap.put("year", "datepart(year,$)");
        fnMap.put("quarter", "datepart(quarter,$)");
        fnMap.put("month", "datepart(month,$)");
        fnMap.put("week", "datepart(week,$)");
        fnMap.put("monthname", "datename(month,$)");
        fnMap.put("timestampadd", "dateadd($)");
        fnMap.put("timestampdiff", "datediff($)");
        cvMap.put("binary", "varbinary");
        cvMap.put("char", "varchar");
        String str3 = "datetime";
        cvMap.put("date", str3);
        cvMap.put("double", "float");
        cvMap.put("longvarbinary", "image");
        cvMap.put("longvarchar", "text");
        cvMap.put("time", str3);
        String str4 = "timestamp";
        cvMap.put(str4, str4);
    }

    static String[] parse(String str, ArrayList arrayList, JtdsConnection jtdsConnection, boolean z) throws SQLException {
        if (z) {
            return new SQLParser(str, arrayList, jtdsConnection).parse(z);
        }
        SQLCacheKey sQLCacheKey = new SQLCacheKey(str, jtdsConnection);
        CachedSQLQuery cachedSQLQuery = (CachedSQLQuery) _Cache.get(sQLCacheKey);
        if (cachedSQLQuery == null) {
            String[] parse = new SQLParser(str, arrayList, jtdsConnection).parse(z);
            _Cache.put(sQLCacheKey, new CachedSQLQuery(parse, arrayList));
            return parse;
        }
        String[] strArr = cachedSQLQuery.parsedSql;
        int length = cachedSQLQuery.paramNames == null ? 0 : cachedSQLQuery.paramNames.length;
        for (int i = 0; i < length; i++) {
            arrayList.add(new ParamInfo(cachedSQLQuery.paramNames[i], cachedSQLQuery.paramMarkerPos[i], cachedSQLQuery.paramIsRetVal[i], cachedSQLQuery.paramIsUnicode[i]));
        }
        return strArr;
    }

    private static boolean isIdentifier(int i) {
        return i > 127 || identifierChar[i];
    }

    private SQLParser(String str, ArrayList arrayList, JtdsConnection jtdsConnection) {
        this.sql = str;
        char[] charArray = str.toCharArray();
        this.f117in = charArray;
        int length = charArray.length;
        this.len = length;
        this.out = new char[length];
        this.params = arrayList;
        this.connection = jtdsConnection;
    }

    private void copyLiteral(String str) throws SQLException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == '?') {
                if (this.params != null) {
                    this.params.add(new ParamInfo(this.f116d, this.connection.getUseUnicode()));
                } else {
                    throw new SQLException(Messages.get("error.parsesql.unexpectedparam", (Object) String.valueOf(this.f118s)), "2A000");
                }
            }
            append(charAt);
        }
    }

    private void copyString() {
        char c = this.terminator;
        char c2 = this.f117in[this.f118s];
        if (c2 == '[') {
            c2 = ']';
        }
        this.terminator = c2;
        char[] cArr = this.f117in;
        int i = this.f118s;
        this.f118s = i + 1;
        append(cArr[i]);
        while (true) {
            char[] cArr2 = this.f117in;
            int i2 = this.f118s;
            if (cArr2[i2] != c2) {
                this.f118s = i2 + 1;
                append(cArr2[i2]);
            } else {
                this.f118s = i2 + 1;
                append(cArr2[i2]);
                this.terminator = c;
                return;
            }
        }
    }

    private String copyKeyWord() {
        int i = this.f116d;
        while (true) {
            int i2 = this.f118s;
            if (i2 < this.len && isIdentifier(this.f117in[i2])) {
                char[] cArr = this.f117in;
                int i3 = this.f118s;
                this.f118s = i3 + 1;
                append(cArr[i3]);
            }
        }
        return String.valueOf(this.out, i, this.f116d - i).toLowerCase();
    }

    private void copyParam(String str, int i) throws SQLException {
        if (this.params != null) {
            ParamInfo paramInfo = new ParamInfo(i, this.connection.getUseUnicode());
            paramInfo.name = str;
            if (i >= 0) {
                char[] cArr = this.f117in;
                int i2 = this.f118s;
                this.f118s = i2 + 1;
                append(cArr[i2]);
            } else {
                paramInfo.isRetVal = true;
                this.f118s++;
            }
            this.params.add(paramInfo);
            return;
        }
        throw new SQLException(Messages.get("error.parsesql.unexpectedparam", (Object) String.valueOf(this.f118s)), "2A000");
    }

    private String copyProcName() throws SQLException {
        int i = this.f116d;
        while (true) {
            char[] cArr = this.f117in;
            int i2 = this.f118s;
            if (cArr[i2] == '\"' || cArr[i2] == '[') {
                copyString();
            } else {
                this.f118s = i2 + 1;
                char c = cArr[i2];
                while (true) {
                    if (!isIdentifier(c) && c != ';') {
                        break;
                    }
                    append(c);
                    char[] cArr2 = this.f117in;
                    int i3 = this.f118s;
                    this.f118s = i3 + 1;
                    c = cArr2[i3];
                }
                this.f118s--;
            }
            if (this.f117in[this.f118s] != '.') {
                break;
            }
            while (true) {
                char[] cArr3 = this.f117in;
                int i4 = this.f118s;
                if (cArr3[i4] == '.') {
                    this.f118s = i4 + 1;
                    append(cArr3[i4]);
                }
            }
        }
        if (this.f116d != i) {
            return new String(this.out, i, this.f116d - i);
        }
        throw new SQLException(Messages.get("error.parsesql.syntax", NotificationCompat.CATEGORY_CALL, String.valueOf(this.f118s)), "22025");
    }

    private String copyParamName() {
        int i = this.f116d;
        char[] cArr = this.f117in;
        int i2 = this.f118s;
        this.f118s = i2 + 1;
        char c = cArr[i2];
        while (isIdentifier(c)) {
            append(c);
            char[] cArr2 = this.f117in;
            int i3 = this.f118s;
            this.f118s = i3 + 1;
            c = cArr2[i3];
        }
        this.f118s--;
        return new String(this.out, i, this.f116d - i);
    }

    private void copyWhiteSpace() {
        while (true) {
            int i = this.f118s;
            char[] cArr = this.f117in;
            if (i < cArr.length && Character.isWhitespace(cArr[i])) {
                char[] cArr2 = this.f117in;
                int i2 = this.f118s;
                this.f118s = i2 + 1;
                append(cArr2[i2]);
            } else {
                return;
            }
        }
    }

    private void mustbe(char c, boolean z) throws SQLException {
        char[] cArr = this.f117in;
        int i = this.f118s;
        if (cArr[i] != c) {
            throw new SQLException(Messages.get("error.parsesql.mustbe", String.valueOf(this.f118s), String.valueOf(c)), "22019");
        } else if (z) {
            this.f118s = i + 1;
            append(cArr[i]);
        } else {
            this.f118s = i + 1;
        }
    }

    private void skipWhiteSpace() throws SQLException {
        int i;
        while (this.f118s < this.len) {
            while (true) {
                i = 1;
                if (!Character.isWhitespace(this.sql.charAt(this.f118s))) {
                    break;
                }
                this.f118s++;
            }
            char charAt = this.sql.charAt(this.f118s);
            if (charAt == '-') {
                int i2 = this.f118s;
                if (i2 + 1 < this.len) {
                    char[] cArr = this.f117in;
                    if (cArr[i2 + 1] == '-') {
                        this.f118s = i2 + 1;
                        append(cArr[i2]);
                        char[] cArr2 = this.f117in;
                        int i3 = this.f118s;
                        this.f118s = i3 + 1;
                        append(cArr2[i3]);
                        while (true) {
                            int i4 = this.f118s;
                            if (i4 >= this.len) {
                                break;
                            }
                            char[] cArr3 = this.f117in;
                            if (cArr3[i4] == 10 || cArr3[i4] == 13) {
                                break;
                            }
                            this.f118s = i4 + 1;
                            append(cArr3[i4]);
                        }
                    }
                }
            } else if (charAt == '/') {
                int i5 = this.f118s;
                if (i5 + 1 < this.len) {
                    char[] cArr4 = this.f117in;
                    if (cArr4[i5 + 1] == '*') {
                        this.f118s = i5 + 1;
                        append(cArr4[i5]);
                        char[] cArr5 = this.f117in;
                        int i6 = this.f118s;
                        this.f118s = i6 + 1;
                        append(cArr5[i6]);
                        do {
                            int i7 = this.f118s;
                            int i8 = this.len;
                            if (i7 < i8 - 1) {
                                char[] cArr6 = this.f117in;
                                if (cArr6[i7] == '/' && i7 + 1 < i8 && cArr6[i7 + 1] == '*') {
                                    this.f118s = i7 + 1;
                                    append(cArr6[i7]);
                                    i++;
                                } else {
                                    char[] cArr7 = this.f117in;
                                    int i9 = this.f118s;
                                    if (cArr7[i9] == '*' && i9 + 1 < this.len && cArr7[i9 + 1] == '/') {
                                        this.f118s = i9 + 1;
                                        append(cArr7[i9]);
                                        i--;
                                    }
                                }
                                char[] cArr8 = this.f117in;
                                int i10 = this.f118s;
                                this.f118s = i10 + 1;
                                append(cArr8[i10]);
                            } else {
                                throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
                            }
                        } while (i > 0);
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else {
                return;
            }
        }
    }

    private void skipSingleComments() {
        while (true) {
            int i = this.f118s;
            if (i < this.len) {
                char[] cArr = this.f117in;
                if (cArr[i] != 10 && cArr[i] != 13) {
                    this.f118s = i + 1;
                    append(cArr[i]);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private void skipMultiComments() throws SQLException {
        int i = 0;
        do {
            int i2 = this.f118s;
            if (i2 < this.len - 1) {
                char[] cArr = this.f117in;
                if (cArr[i2] == '/' && cArr[i2 + 1] == '*') {
                    this.f118s = i2 + 1;
                    append(cArr[i2]);
                    i++;
                } else {
                    char[] cArr2 = this.f117in;
                    int i3 = this.f118s;
                    if (cArr2[i3] == '*' && cArr2[i3 + 1] == '/') {
                        this.f118s = i3 + 1;
                        append(cArr2[i3]);
                        i--;
                    }
                }
                char[] cArr3 = this.f117in;
                int i4 = this.f118s;
                this.f118s = i4 + 1;
                append(cArr3[i4]);
            } else {
                throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
            }
        } while (i > 0);
    }

    private void callEscape() throws SQLException {
        int i;
        char c;
        copyLiteral("EXECUTE ");
        this.keyWord = "execute";
        this.procName = copyProcName();
        skipWhiteSpace();
        char[] cArr = this.f117in;
        int i2 = this.f118s;
        if (cArr[i2] == '(') {
            this.f118s = i2 + 1;
            this.terminator = ')';
            skipWhiteSpace();
        } else {
            this.terminator = '}';
        }
        append(' ');
        while (true) {
            char[] cArr2 = this.f117in;
            i = this.f118s;
            char c2 = cArr2[i];
            c = this.terminator;
            if (c2 == c) {
                break;
            }
            String str = "";
            if (cArr2[i] == '@') {
                String copyParamName = copyParamName();
                skipWhiteSpace();
                mustbe('=', true);
                skipWhiteSpace();
                if (this.f117in[this.f118s] == '?') {
                    copyParam(copyParamName, this.f116d);
                } else {
                    this.procName = str;
                }
            } else if (cArr2[i] == '?') {
                copyParam(null, this.f116d);
            } else {
                this.procName = str;
            }
            skipWhiteSpace();
            while (true) {
                char[] cArr3 = this.f117in;
                int i3 = this.f118s;
                if (cArr3[i3] == this.terminator || cArr3[i3] == ',') {
                    char[] cArr4 = this.f117in;
                    int i4 = this.f118s;
                } else if (cArr3[i3] == '{') {
                    escape();
                } else if (cArr3[i3] == '\'' || cArr3[i3] == '[' || cArr3[i3] == '\"') {
                    copyString();
                } else {
                    this.f118s = i3 + 1;
                    append(cArr3[i3]);
                }
            }
            char[] cArr42 = this.f117in;
            int i42 = this.f118s;
            if (cArr42[i42] == ',') {
                this.f118s = i42 + 1;
                append(cArr42[i42]);
            }
            skipWhiteSpace();
        }
        if (c == ')') {
            this.f118s = i + 1;
        }
        this.terminator = '}';
        skipWhiteSpace();
    }

    private boolean getDateTimeField(byte[] bArr) throws SQLException {
        char c;
        int i;
        skipWhiteSpace();
        boolean z = false;
        if (this.f117in[this.f118s] == '?') {
            copyParam(null, this.f116d);
            skipWhiteSpace();
            if (this.f117in[this.f118s] == this.terminator) {
                z = true;
            }
            return z;
        }
        boolean equals = this.keyWord.equals("select");
        if (equals) {
            append("convert(datetime,".toCharArray());
        }
        append('\'');
        char[] cArr = this.f117in;
        int i2 = this.f118s;
        if (cArr[i2] == '\'' || cArr[i2] == '\"') {
            char[] cArr2 = this.f117in;
            int i3 = this.f118s;
            this.f118s = i3 + 1;
            c = cArr2[i3];
        } else {
            c = '}';
        }
        this.terminator = c;
        skipWhiteSpace();
        int i4 = 0;
        while (i4 < bArr.length) {
            char[] cArr3 = this.f117in;
            int i5 = this.f118s;
            this.f118s = i5 + 1;
            char c2 = cArr3[i5];
            if (c2 != ' ' || this.out[this.f116d - 1] != ' ') {
                if (bArr[i4] == 35) {
                    if (!Character.isDigit(c2)) {
                        return false;
                    }
                } else if (bArr[i4] != c2) {
                    return false;
                }
                if (c2 != '-') {
                    append(c2);
                }
                i4++;
            }
        }
        if (bArr.length == 19) {
            char[] cArr4 = this.f117in;
            int i6 = this.f118s;
            if (cArr4[i6] == '.') {
                this.f118s = i6 + 1;
                append(cArr4[i6]);
                i = 0;
                while (Character.isDigit(this.f117in[this.f118s])) {
                    if (i < 3) {
                        char[] cArr5 = this.f117in;
                        int i7 = this.f118s;
                        this.f118s = i7 + 1;
                        append(cArr5[i7]);
                        i++;
                    } else {
                        this.f118s++;
                    }
                }
            } else {
                append('.');
                i = 0;
            }
            while (i < 3) {
                append('0');
                i++;
            }
        }
        skipWhiteSpace();
        char[] cArr6 = this.f117in;
        int i8 = this.f118s;
        char c3 = cArr6[i8];
        char c4 = this.terminator;
        if (c3 != c4) {
            return false;
        }
        if (c4 != '}') {
            this.f118s = i8 + 1;
        }
        skipWhiteSpace();
        append('\'');
        if (equals) {
            append(')');
        }
        return true;
    }

    private void outerJoinEscape() throws SQLException {
        while (true) {
            char[] cArr = this.f117in;
            int i = this.f118s;
            if (cArr[i] != '}') {
                char c = cArr[i];
                if (!(c == '\"' || c == '\'')) {
                    if (c == '?') {
                        copyParam(null, this.f116d);
                    } else if (c != '[') {
                        if (c != '{') {
                            append(c);
                            this.f118s++;
                        } else {
                            escape();
                        }
                    }
                }
                copyString();
            } else {
                return;
            }
        }
    }

    private void functionEscape() throws SQLException {
        String str;
        char c = this.terminator;
        skipWhiteSpace();
        StringBuilder sb = new StringBuilder();
        while (isIdentifier(this.f117in[this.f118s])) {
            char[] cArr = this.f117in;
            int i = this.f118s;
            this.f118s = i + 1;
            sb.append(cArr[i]);
        }
        String lowerCase = sb.toString().toLowerCase();
        skipWhiteSpace();
        mustbe('(', false);
        int i2 = this.f116d;
        this.terminator = ')';
        int i3 = 1;
        int i4 = 0;
        while (true) {
            if (this.f117in[this.f118s] != ')' || i3 > 1) {
                char c2 = this.f117in[this.f118s];
                if (c2 != '\"') {
                    if (c2 != ',') {
                        if (c2 != '[') {
                            if (c2 != '{') {
                                switch (c2) {
                                    case '\'':
                                        break;
                                    case '(':
                                        i3++;
                                        append(c2);
                                        this.f118s++;
                                        continue;
                                    case ')':
                                        i3--;
                                        append(c2);
                                        this.f118s++;
                                        continue;
                                    default:
                                        append(c2);
                                        this.f118s++;
                                        continue;
                                }
                            } else {
                                escape();
                            }
                        }
                    } else if (i3 == 1) {
                        if (i4 == 0) {
                            i4 = this.f116d - i2;
                        }
                        if ("concat".equals(lowerCase)) {
                            append('+');
                            this.f118s++;
                        } else if ("mod".equals(lowerCase)) {
                            append('%');
                            this.f118s++;
                        } else {
                            append(c2);
                            this.f118s++;
                        }
                    } else {
                        append(c2);
                        this.f118s++;
                    }
                }
                copyString();
            } else {
                String trim = String.valueOf(this.out, i2, this.f116d - i2).trim();
                this.f116d = i2;
                mustbe(')', false);
                this.terminator = c;
                skipWhiteSpace();
                if (!"convert".equals(lowerCase) || i4 >= trim.length() - 1) {
                    if (this.connection.getServerType() == 1) {
                        str = (String) msFnMap.get(lowerCase);
                        if (str == null) {
                            str = (String) fnMap.get(lowerCase);
                        }
                    } else {
                        str = (String) fnMap.get(lowerCase);
                    }
                    if (str == null) {
                        copyLiteral(lowerCase);
                        append('(');
                        copyLiteral(trim);
                        append(')');
                        return;
                    }
                    if (trim.length() > 8 && trim.substring(0, 8).equalsIgnoreCase("sql_tsi_")) {
                        trim = trim.substring(8);
                        if (trim.length() > 11 && trim.substring(0, 11).equalsIgnoreCase("frac_second")) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("millisecond");
                            sb2.append(trim.substring(11));
                            trim = sb2.toString();
                        }
                    }
                    int length = str.length();
                    for (int i5 = 0; i5 < length; i5++) {
                        char charAt = str.charAt(i5);
                        if (charAt == '$') {
                            copyLiteral(trim);
                        } else {
                            append(charAt);
                        }
                    }
                    return;
                }
                String lowerCase2 = trim.substring(i4 + 1).trim().toLowerCase();
                String str2 = (String) cvMap.get(lowerCase2);
                if (str2 != null) {
                    lowerCase2 = str2;
                }
                copyLiteral("convert(");
                copyLiteral(lowerCase2);
                append(',');
                copyLiteral(trim.substring(0, i4));
                append(')');
                return;
            }
        }
    }

    private void likeEscape() throws SQLException {
        copyLiteral("escape ");
        skipWhiteSpace();
        char[] cArr = this.f117in;
        int i = this.f118s;
        if (cArr[i] == '\'' || cArr[i] == '\"') {
            copyString();
        } else {
            mustbe('\'', true);
        }
        skipWhiteSpace();
    }

    private void escape() throws SQLException {
        char c = this.terminator;
        this.terminator = '}';
        StringBuilder sb = new StringBuilder();
        this.f118s++;
        skipWhiteSpace();
        char c2 = this.f117in[this.f118s];
        String str = NotificationCompat.CATEGORY_CALL;
        String str2 = "error.parsesql.syntax";
        String str3 = "22019";
        if (c2 == '?') {
            copyParam("@return_status", -1);
            skipWhiteSpace();
            mustbe('=', false);
            skipWhiteSpace();
            while (Character.isLetter(this.f117in[this.f118s])) {
                char[] cArr = this.f117in;
                int i = this.f118s;
                this.f118s = i + 1;
                sb.append(Character.toLowerCase(cArr[i]));
            }
            skipWhiteSpace();
            if (str.equals(sb.toString())) {
                callEscape();
            } else {
                throw new SQLException(Messages.get(str2, str, String.valueOf(this.f118s)), str3);
            }
        } else {
            while (Character.isLetter(this.f117in[this.f118s])) {
                char[] cArr2 = this.f117in;
                int i2 = this.f118s;
                this.f118s = i2 + 1;
                sb.append(Character.toLowerCase(cArr2[i2]));
            }
            skipWhiteSpace();
            String sb2 = sb.toString();
            if (str.equals(sb2)) {
                callEscape();
            } else if ("t".equals(sb2)) {
                if (!getDateTimeField(timeMask)) {
                    throw new SQLException(Messages.get(str2, "time", String.valueOf(this.f118s)), str3);
                }
            } else if ("d".equals(sb2)) {
                if (!getDateTimeField(dateMask)) {
                    throw new SQLException(Messages.get(str2, "date", String.valueOf(this.f118s)), str3);
                }
            } else if ("ts".equals(sb2)) {
                if (!getDateTimeField(timestampMask)) {
                    throw new SQLException(Messages.get(str2, "timestamp", String.valueOf(this.f118s)), str3);
                }
            } else if ("oj".equals(sb2)) {
                outerJoinEscape();
            } else if ("fn".equals(sb2)) {
                functionEscape();
            } else if ("escape".equals(sb2)) {
                likeEscape();
            } else {
                throw new SQLException(Messages.get("error.parsesql.badesc", sb2, String.valueOf(this.f118s)), str3);
            }
        }
        mustbe('}', false);
        this.terminator = c;
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0104 A[EDGE_INSN: B:65:0x0104->B:55:0x0104 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getTableName() throws java.sql.SQLException {
        /*
            r9 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = 128(0x80, float:1.794E-43)
            r0.<init>(r1)
            r9.copyWhiteSpace()
            int r1 = r9.f118s
            int r2 = r9.len
            r3 = 32
            if (r1 >= r2) goto L_0x0017
            char[] r2 = r9.f117in
            char r1 = r2[r1]
            goto L_0x0019
        L_0x0017:
            r1 = 32
        L_0x0019:
            java.lang.String r2 = ""
            r4 = 123(0x7b, float:1.72E-43)
            if (r1 != r4) goto L_0x0020
            return r2
        L_0x0020:
            r5 = 45
            r6 = 47
            if (r1 == r6) goto L_0x0030
            if (r1 != r5) goto L_0x0060
            int r7 = r9.f118s
            int r7 = r7 + 1
            int r8 = r9.len
            if (r7 >= r8) goto L_0x0060
        L_0x0030:
            if (r1 != r6) goto L_0x0042
            char[] r5 = r9.f117in
            int r6 = r9.f118s
            int r6 = r6 + 1
            char r5 = r5[r6]
            r6 = 42
            if (r5 != r6) goto L_0x0060
            r9.skipMultiComments()
            goto L_0x004f
        L_0x0042:
            char[] r6 = r9.f117in
            int r7 = r9.f118s
            int r7 = r7 + 1
            char r6 = r6[r7]
            if (r6 != r5) goto L_0x0060
            r9.skipSingleComments()
        L_0x004f:
            r9.copyWhiteSpace()
            int r1 = r9.f118s
            int r5 = r9.len
            if (r1 >= r5) goto L_0x005d
            char[] r5 = r9.f117in
            char r1 = r5[r1]
            goto L_0x0020
        L_0x005d:
            r1 = 32
            goto L_0x0020
        L_0x0060:
            if (r1 != r4) goto L_0x0063
            return r2
        L_0x0063:
            int r2 = r9.f118s
            int r4 = r9.len
            if (r2 >= r4) goto L_0x0104
            r5 = 91
            r6 = 46
            if (r1 == r5) goto L_0x00c4
            r5 = 34
            if (r1 != r5) goto L_0x0074
            goto L_0x00c4
        L_0x0074:
            int r1 = r9.f116d
            if (r2 >= r4) goto L_0x0081
            char[] r4 = r9.f117in
            int r5 = r2 + 1
            r9.f118s = r5
            char r2 = r4[r2]
            goto L_0x0083
        L_0x0081:
            r2 = 32
        L_0x0083:
            boolean r4 = isIdentifier(r2)
            if (r4 == 0) goto L_0x00a1
            if (r2 == r6) goto L_0x00a1
            r4 = 44
            if (r2 == r4) goto L_0x00a1
            r9.append(r2)
            int r2 = r9.f118s
            int r4 = r9.len
            if (r2 >= r4) goto L_0x0081
            char[] r4 = r9.f117in
            int r5 = r2 + 1
            r9.f118s = r5
            char r2 = r4[r2]
            goto L_0x0083
        L_0x00a1:
            char[] r2 = r9.out
            int r4 = r9.f116d
            int r4 = r4 - r1
            java.lang.String r1 = java.lang.String.valueOf(r2, r1, r4)
            r0.append(r1)
            int r1 = r9.f118s
            int r1 = r1 + -1
            r9.f118s = r1
            r9.copyWhiteSpace()
            int r1 = r9.f118s
            int r2 = r9.len
            if (r1 >= r2) goto L_0x00c1
            char[] r2 = r9.f117in
            char r1 = r2[r1]
            goto L_0x00e2
        L_0x00c1:
            r1 = 32
            goto L_0x00e2
        L_0x00c4:
            int r1 = r9.f116d
            r9.copyString()
            char[] r2 = r9.out
            int r4 = r9.f116d
            int r4 = r4 - r1
            java.lang.String r1 = java.lang.String.valueOf(r2, r1, r4)
            r0.append(r1)
            r9.copyWhiteSpace()
            int r1 = r9.f118s
            int r2 = r9.len
            if (r1 >= r2) goto L_0x00c1
            char[] r2 = r9.f117in
            char r1 = r2[r1]
        L_0x00e2:
            if (r1 == r6) goto L_0x00e5
            goto L_0x0104
        L_0x00e5:
            r0.append(r1)
            r9.append(r1)
            int r1 = r9.f118s
            int r1 = r1 + 1
            r9.f118s = r1
            r9.copyWhiteSpace()
            int r1 = r9.f118s
            int r2 = r9.len
            if (r1 >= r2) goto L_0x0100
            char[] r2 = r9.f117in
            char r1 = r2[r1]
            goto L_0x0063
        L_0x0100:
            r1 = 32
            goto L_0x0063
        L_0x0104:
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SQLParser.getTableName():java.lang.String");
    }

    private final void append(char[] cArr) {
        for (char append : cArr) {
            append(append);
        }
    }

    private final void append(char c) {
        try {
            char[] cArr = this.out;
            int i = this.f116d;
            this.f116d = i + 1;
            cArr[i] = c;
        } catch (ArrayIndexOutOfBoundsException unused) {
            char[] cArr2 = this.out;
            char[] cArr3 = new char[(cArr2.length + 256)];
            System.arraycopy(cArr2, 0, cArr3, 0, cArr2.length);
            this.out = cArr3;
            cArr3[this.f116d - 1] = c;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0102, code lost:
        if (r10.connection.getDatabaseMinorVersion() < 50) goto L_0x011c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0124 A[Catch:{ IndexOutOfBoundsException -> 0x015d }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0125 A[Catch:{ IndexOutOfBoundsException -> 0x015d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] parse(boolean r11) throws java.sql.SQLException {
        /*
            r10 = this;
            java.lang.String r0 = "22025"
            r1 = 0
            r2 = 1
            r3 = 1
            r4 = 0
            r5 = 0
        L_0x0007:
            int r6 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r7 = r10.len     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r6 >= r7) goto L_0x00c3
            char[] r6 = r10.f117in     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r7 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            char r6 = r6[r7]     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r7 = 34
            if (r6 == r7) goto L_0x00be
            r7 = 39
            if (r6 == r7) goto L_0x00be
            r7 = 45
            if (r6 == r7) goto L_0x009f
            r7 = 47
            if (r6 == r7) goto L_0x007e
            r7 = 63
            if (r6 == r7) goto L_0x0077
            r7 = 91
            if (r6 == r7) goto L_0x00be
            r7 = 123(0x7b, float:1.72E-43)
            if (r6 == r7) goto L_0x0072
            if (r3 == 0) goto L_0x0069
            boolean r7 = java.lang.Character.isLetter(r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r7 == 0) goto L_0x0069
            java.lang.String r7 = r10.keyWord     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r7 != 0) goto L_0x0052
            java.lang.String r3 = r10.copyKeyWord()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r10.keyWord = r3     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r6 = "select"
            boolean r3 = r6.equals(r3)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r3 == 0) goto L_0x004a
            r4 = 1
        L_0x004a:
            if (r11 == 0) goto L_0x0050
            if (r4 == 0) goto L_0x0050
            r3 = 1
            goto L_0x0007
        L_0x0050:
            r3 = 0
            goto L_0x0007
        L_0x0052:
            if (r11 == 0) goto L_0x0069
            if (r4 == 0) goto L_0x0069
            java.lang.String r6 = r10.copyKeyWord()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r7 = "from"
            boolean r6 = r7.equals(r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r6 == 0) goto L_0x0007
            java.lang.String r3 = r10.getTableName()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r10.tableName = r3     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0050
        L_0x0069:
            r10.append(r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r6 + r2
            r10.f118s = r6     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x0072:
            r10.escape()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r5 = 1
            goto L_0x0007
        L_0x0077:
            r6 = 0
            int r7 = r10.f116d     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r10.copyParam(r6, r7)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x007e:
            int r7 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r7 = r7 + r2
            int r8 = r10.len     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r7 >= r8) goto L_0x0095
            char[] r7 = r10.f117in     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r8 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r8 = r8 + r2
            char r7 = r7[r8]     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r8 = 42
            if (r7 != r8) goto L_0x0095
            r10.skipMultiComments()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x0095:
            r10.append(r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r6 + r2
            r10.f118s = r6     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x009f:
            int r8 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r8 = r8 + r2
            int r9 = r10.len     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r8 >= r9) goto L_0x00b4
            char[] r8 = r10.f117in     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r9 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r9 = r9 + r2
            char r8 = r8[r9]     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r8 != r7) goto L_0x00b4
            r10.skipSingleComments()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x00b4:
            r10.append(r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r10.f118s     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r6 + r2
            r10.f118s = r6     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x00be:
            r10.copyString()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0007
        L_0x00c3:
            java.util.ArrayList r11 = r10.params     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r3 = 2
            if (r11 == 0) goto L_0x0135
            java.util.ArrayList r11 = r10.params     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.size()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r4 = 255(0xff, float:3.57E-43)
            if (r11 <= r4) goto L_0x0135
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getPrepareSql()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r11 == 0) goto L_0x0135
            java.lang.String r11 = r10.procName     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r11 == 0) goto L_0x0135
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getServerType()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r6 = 2000(0x7d0, float:2.803E-42)
            if (r11 != r3) goto L_0x0107
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getDatabaseMajorVersion()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r7 = 12
            if (r11 > r7) goto L_0x0104
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getDatabaseMajorVersion()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r11 != r7) goto L_0x011c
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getDatabaseMinorVersion()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r7 = 50
            if (r11 < r7) goto L_0x011c
        L_0x0104:
            r4 = 2000(0x7d0, float:2.803E-42)
            goto L_0x011c
        L_0x0107:
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getDatabaseMajorVersion()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r7 = 7
            if (r11 != r7) goto L_0x0113
            r4 = 1000(0x3e8, float:1.401E-42)
            goto L_0x011c
        L_0x0113:
            net.sourceforge.jtds.jdbc.JtdsConnection r11 = r10.connection     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.getDatabaseMajorVersion()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r11 <= r7) goto L_0x011c
            goto L_0x0104
        L_0x011c:
            java.util.ArrayList r11 = r10.params     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r11 = r11.size()     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r11 > r4) goto L_0x0125
            goto L_0x0135
        L_0x0125:
            java.sql.SQLException r11 = new java.sql.SQLException     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r1 = "error.parsesql.toomanyparams"
            java.lang.String r2 = java.lang.Integer.toString(r4)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r1, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r11.<init>(r1, r0)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            throw r11     // Catch:{ IndexOutOfBoundsException -> 0x015d }
        L_0x0135:
            r11 = 4
            java.lang.String[] r11 = new java.lang.String[r11]     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r5 == 0) goto L_0x0144
            java.lang.String r4 = new java.lang.String     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            char[] r5 = r10.out     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            int r6 = r10.f116d     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r4.<init>(r5, r1, r6)     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            goto L_0x0146
        L_0x0144:
            java.lang.String r4 = r10.sql     // Catch:{ IndexOutOfBoundsException -> 0x015d }
        L_0x0146:
            r11[r1] = r4     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r1 = r10.procName     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r11[r2] = r1     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            java.lang.String r1 = r10.keyWord     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            if (r1 != 0) goto L_0x0153
            java.lang.String r1 = ""
            goto L_0x0155
        L_0x0153:
            java.lang.String r1 = r10.keyWord     // Catch:{ IndexOutOfBoundsException -> 0x015d }
        L_0x0155:
            r11[r3] = r1     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r1 = 3
            java.lang.String r2 = r10.tableName     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            r11[r1] = r2     // Catch:{ IndexOutOfBoundsException -> 0x015d }
            return r11
        L_0x015d:
            java.sql.SQLException r11 = new java.sql.SQLException
            char r1 = r10.terminator
            java.lang.String r1 = java.lang.String.valueOf(r1)
            java.lang.String r2 = "error.parsesql.missing"
            java.lang.String r1 = net.sourceforge.jtds.jdbc.Messages.get(r2, r1)
            r11.<init>(r1, r0)
            goto L_0x0170
        L_0x016f:
            throw r11
        L_0x0170:
            goto L_0x016f
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SQLParser.parse(boolean):java.lang.String[]");
    }
}
