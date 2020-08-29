package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

class ParamInfo implements Cloneable {
    static final int INPUT = 0;
    static final int OUTPUT = 1;
    static final int RETVAL = 2;
    static final int UNICODE = 4;
    CharsetInfo charsetInfo;
    byte[] collation;
    boolean isOutput;
    boolean isRetVal;
    boolean isSet;
    boolean isSetOut;
    boolean isUnicode;
    int jdbcType;
    int length = -1;
    int markerPos = -1;
    String name;
    Object outValue;
    int precision = -1;
    int scale = -1;
    String sqlType;
    int tdsType;
    Object value;

    ParamInfo(int i, boolean z) {
        this.markerPos = i;
        this.isUnicode = z;
    }

    ParamInfo(String str, int i, boolean z, boolean z2) {
        this.name = str;
        this.markerPos = i;
        this.isRetVal = z;
        this.isUnicode = z2;
    }

    ParamInfo(int i, Object obj, int i2) {
        this.jdbcType = i;
        this.value = obj;
        boolean z = true;
        this.isSet = true;
        this.isOutput = (i2 & 1) > 0 || (i2 & 2) > 0;
        this.isRetVal = (i2 & 2) > 0;
        if ((i2 & 4) <= 0) {
            z = false;
        }
        this.isUnicode = z;
        if (obj instanceof String) {
            this.length = ((String) obj).length();
        } else if (obj instanceof byte[]) {
            this.length = ((byte[]) obj).length;
        }
    }

    ParamInfo(ColInfo colInfo, String str, Object obj, int i) {
        this.name = str;
        this.tdsType = colInfo.tdsType;
        this.scale = colInfo.scale;
        this.precision = colInfo.precision;
        this.jdbcType = colInfo.jdbcType;
        this.sqlType = colInfo.sqlType;
        this.collation = colInfo.collation;
        this.charsetInfo = colInfo.charsetInfo;
        this.isUnicode = TdsData.isUnicode(colInfo);
        this.isSet = true;
        this.value = obj;
        this.length = i;
    }

    /* access modifiers changed from: 0000 */
    public Object getOutValue() throws SQLException {
        if (this.isSetOut) {
            return this.outValue;
        }
        throw new SQLException(Messages.get("error.callable.outparamnotset"), "HY010");
    }

    /* access modifiers changed from: 0000 */
    public void setOutValue(Object obj) {
        this.outValue = obj;
        this.isSetOut = true;
    }

    /* access modifiers changed from: 0000 */
    public void clearOutValue() {
        this.outValue = null;
        this.isSetOut = false;
    }

    /* access modifiers changed from: 0000 */
    public void clearInValue() {
        this.value = null;
        this.isSet = false;
    }

    /* access modifiers changed from: 0000 */
    public String getString(String str) throws IOException {
        Object obj = this.value;
        if (obj == null || (obj instanceof String)) {
            return (String) this.value;
        }
        if (obj instanceof InputStream) {
            try {
                String loadFromReader = loadFromReader(new InputStreamReader((InputStream) this.value, str), this.length);
                this.value = loadFromReader;
                this.length = loadFromReader.length();
                return (String) this.value;
            } catch (UnsupportedEncodingException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("I/O Error: UnsupportedEncodingException: ");
                sb.append(e.getMessage());
                throw new IOException(sb.toString());
            }
        } else if (!(obj instanceof Reader)) {
            return obj.toString();
        } else {
            String loadFromReader2 = loadFromReader((Reader) obj, this.length);
            this.value = loadFromReader2;
            return loadFromReader2;
        }
    }

    /* access modifiers changed from: 0000 */
    public byte[] getBytes(String str) throws IOException {
        Object obj = this.value;
        if (obj == null || (obj instanceof byte[])) {
            return (byte[]) this.value;
        }
        if (obj instanceof InputStream) {
            byte[] loadFromStream = loadFromStream((InputStream) obj, this.length);
            this.value = loadFromStream;
            return loadFromStream;
        } else if (!(obj instanceof Reader)) {
            return obj instanceof String ? Support.encodeString(str, (String) obj) : new byte[0];
        } else {
            byte[] encodeString = Support.encodeString(str, loadFromReader((Reader) obj, this.length));
            this.value = encodeString;
            return encodeString;
        }
    }

    private static byte[] loadFromStream(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 != i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 != i) {
            throw new IOException("Data in stream less than specified by length");
        } else if (inputStream.read() < 0) {
            return bArr;
        } else {
            throw new IOException("More data in stream than specified by length");
        }
    }

    private static String loadFromReader(Reader reader, int i) throws IOException {
        char[] cArr = new char[i];
        int i2 = 0;
        while (i2 != i) {
            int read = reader.read(cArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 != i) {
            throw new IOException("Data in stream less than specified by length");
        } else if (reader.read() < 0) {
            return new String(cArr);
        } else {
            throw new IOException("More data in stream than specified by length");
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException unused) {
            return null;
        }
    }
}
