package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import net.sourceforge.jtds.util.Logger;

public final class CharsetInfo {
    private static final String CHARSETS_RESOURCE_NAME = "net/sourceforge/jtds/jdbc/Charsets.properties";
    private static final HashMap charsets = new HashMap();
    private static final HashMap lcidToCharsetMap = new HashMap();
    private static final CharsetInfo[] sortToCharsetMap = new CharsetInfo[256];
    private final String charset;
    private final boolean wideChars;

    static {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                classLoader = classLoader.getResourceAsStream(CHARSETS_RESOURCE_NAME);
            }
            if (classLoader == null) {
                classLoader = (InputStream) AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        ClassLoader classLoader = CharsetInfo.class.getClassLoader();
                        if (classLoader == null) {
                            classLoader = ClassLoader.getSystemClassLoader();
                        }
                        return classLoader.getResourceAsStream(CharsetInfo.CHARSETS_RESOURCE_NAME);
                    }
                });
            }
            if (classLoader != null) {
                Properties properties = new Properties();
                properties.load(classLoader);
                HashMap hashMap = new HashMap();
                Enumeration propertyNames = properties.propertyNames();
                while (propertyNames.hasMoreElements()) {
                    String str = (String) propertyNames.nextElement();
                    CharsetInfo charsetInfo = new CharsetInfo(properties.getProperty(str));
                    CharsetInfo charsetInfo2 = (CharsetInfo) hashMap.get(charsetInfo.getCharset());
                    if (charsetInfo2 != null) {
                        if (charsetInfo2.isWideChars() == charsetInfo.isWideChars()) {
                            charsetInfo = charsetInfo2;
                        } else {
                            throw new IllegalStateException("Inconsistent Charsets.properties");
                        }
                    }
                    if (str.startsWith("LCID_")) {
                        lcidToCharsetMap.put(new Integer(str.substring(5)), charsetInfo);
                    } else if (str.startsWith("SORT_")) {
                        sortToCharsetMap[Integer.parseInt(str.substring(5))] = charsetInfo;
                    } else {
                        charsets.put(str, charsetInfo);
                    }
                }
            } else {
                Logger.println("Can't load Charsets.properties");
            }
            if (classLoader == null) {
                return;
            }
        } catch (IOException e) {
            Logger.logException(e);
            if (classLoader == null) {
                return;
            }
        } finally {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (Exception unused) {
                }
            }
        }
        try {
            classLoader.close();
        } catch (Exception unused2) {
        }
    }

    public static CharsetInfo getCharset(String str) {
        return (CharsetInfo) charsets.get(str.toUpperCase());
    }

    public static CharsetInfo getCharsetForLCID(int i) {
        return (CharsetInfo) lcidToCharsetMap.get(new Integer(i));
    }

    public static CharsetInfo getCharsetForSortOrder(int i) {
        return sortToCharsetMap[i];
    }

    public static CharsetInfo getCharset(byte[] bArr) throws SQLException {
        CharsetInfo charsetInfo;
        if (bArr[4] != 0) {
            charsetInfo = getCharsetForSortOrder(bArr[4] & 255);
        } else {
            charsetInfo = getCharsetForLCID(((bArr[2] & TdsCore.SYBQUERY_PKT) << TdsCore.MSLOGIN_PKT) | ((bArr[1] & 255) << 8) | (bArr[0] & 255));
        }
        if (charsetInfo != null) {
            return charsetInfo;
        }
        throw new SQLException(Messages.get("error.charset.nocollation", (Object) Support.toHex(bArr)), "2C000");
    }

    public CharsetInfo(String str) {
        this.wideChars = !"1".equals(str.substring(0, 1));
        this.charset = str.substring(2);
    }

    public String getCharset() {
        return this.charset;
    }

    public boolean isWideChars() {
        return this.wideChars;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharsetInfo)) {
            return false;
        }
        return this.charset.equals(((CharsetInfo) obj).charset);
    }

    public int hashCode() {
        return this.charset.hashCode();
    }

    public String toString() {
        return this.charset;
    }
}
