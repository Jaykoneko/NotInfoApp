package net.sourceforge.jtds.jdbc;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;
import net.sourceforge.jtds.ssl.Ssl;

public class Driver implements java.sql.Driver {
    public static final String APPNAME = "prop.appname";
    public static final String AUTOCOMMIT = "prop.autocommit";
    public static final String BATCHSIZE = "prop.batchsize";
    public static final String BINDADDRESS = "prop.bindaddress";
    public static final String BUFFERDIR = "prop.bufferdir";
    public static final String BUFFERMAXMEMORY = "prop.buffermaxmemory";
    public static final String BUFFERMINPACKETS = "prop.bufferminpackets";
    public static final String CACHEMETA = "prop.cachemetadata";
    public static final String CHARSET = "prop.charset";
    public static final String DATABASENAME = "prop.databasename";
    public static final String DOMAIN = "prop.domain";
    public static final String INSTANCE = "prop.instance";
    public static final String LANGUAGE = "prop.language";
    public static final String LASTUPDATECOUNT = "prop.lastupdatecount";
    public static final String LOBBUFFER = "prop.lobbuffer";
    public static final String LOGFILE = "prop.logfile";
    public static final String LOGINTIMEOUT = "prop.logintimeout";
    public static final String MACADDRESS = "prop.macaddress";
    static final int MAJOR_VERSION = 1;
    public static final String MAXSTATEMENTS = "prop.maxstatements";
    static final int MINOR_VERSION = 3;
    static final String MISC_VERSION = ".1";
    public static final String NAMEDPIPE = "prop.namedpipe";
    public static final String PACKETSIZE = "prop.packetsize";
    public static final String PASSWORD = "prop.password";
    public static final String PORTNUMBER = "prop.portnumber";
    public static final String PREPARESQL = "prop.preparesql";
    public static final String PROCESSID = "prop.processid";
    public static final String PROGNAME = "prop.progname";
    public static final String SENDSTRINGPARAMETERSASUNICODE = "prop.useunicode";
    public static final String SERVERNAME = "prop.servername";
    public static final String SERVERTYPE = "prop.servertype";
    public static final String SOKEEPALIVE = "prop.sokeepalive";
    public static final String SOTIMEOUT = "prop.sotimeout";
    public static final int SQLSERVER = 1;
    public static final String SSL = "prop.ssl";
    public static final int SYBASE = 2;
    public static final String TCPNODELAY = "prop.tcpnodelay";
    public static final String TDS = "prop.tds";
    public static final int TDS42 = 1;
    public static final int TDS50 = 2;
    public static final int TDS70 = 3;
    public static final int TDS80 = 4;
    public static final int TDS81 = 5;
    public static final int TDS90 = 6;
    public static final String USECURSORS = "prop.usecursors";
    public static final String USEJCIFS = "prop.usejcifs";
    public static final String USEKERBEROS = "prop.usekerberos";
    public static final String USELOBS = "prop.uselobs";
    public static final String USENTLMV2 = "prop.usentlmv2";
    public static final String USER = "prop.user";
    public static final String WSID = "prop.wsid";
    public static final String XAEMULATION = "prop.xaemulation";
    private static String driverPrefix = "jdbc:jtds:";

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 3;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException unused) {
        }
    }

    public static final String getVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append("1.3");
        sb.append(MISC_VERSION);
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("jTDS ");
        sb.append(getVersion());
        return sb.toString();
    }

    public boolean acceptsURL(String str) throws SQLException {
        if (str == null) {
            return false;
        }
        return str.toLowerCase().startsWith(driverPrefix);
    }

    public Connection connect(String str, Properties properties) throws SQLException {
        if (str == null || !str.toLowerCase().startsWith(driverPrefix)) {
            return null;
        }
        return new JtdsConnection(str, setupConnectProperties(str, properties));
    }

    public DriverPropertyInfo[] getPropertyInfo(String str, Properties properties) throws SQLException {
        if (properties == null) {
            properties = new Properties();
        }
        Properties parseURL = parseURL(str, properties);
        if (parseURL != null) {
            Properties addDefaultProperties = DefaultProperties.addDefaultProperties(parseURL);
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            Messages.loadDriverProperties(hashMap, hashMap2);
            Map createChoicesMap = createChoicesMap();
            Map createRequiredTrueMap = createRequiredTrueMap();
            DriverPropertyInfo[] driverPropertyInfoArr = new DriverPropertyInfo[hashMap.size()];
            int i = 0;
            for (Entry entry : hashMap.entrySet()) {
                String str2 = (String) entry.getKey();
                String str3 = (String) entry.getValue();
                DriverPropertyInfo driverPropertyInfo = new DriverPropertyInfo(str3, addDefaultProperties.getProperty(str3));
                driverPropertyInfo.description = (String) hashMap2.get(str2);
                driverPropertyInfo.required = createRequiredTrueMap.containsKey(str3);
                if (createChoicesMap.containsKey(str3)) {
                    driverPropertyInfo.choices = (String[]) createChoicesMap.get(str3);
                }
                driverPropertyInfoArr[i] = driverPropertyInfo;
                i++;
            }
            return driverPropertyInfoArr;
        }
        throw new SQLException(Messages.get("error.driver.badurl", (Object) str), "08001");
    }

    private Properties setupConnectProperties(String str, Properties properties) throws SQLException {
        Properties parseURL = parseURL(str, properties);
        if (parseURL != null) {
            String str2 = LOGINTIMEOUT;
            if (parseURL.getProperty(Messages.get(str2)) == null) {
                parseURL.setProperty(Messages.get(str2), Integer.toString(DriverManager.getLoginTimeout()));
            }
            return DefaultProperties.addDefaultProperties(parseURL);
        }
        throw new SQLException(Messages.get("error.driver.badurl", (Object) str), "08001");
    }

    private static Map createChoicesMap() {
        HashMap hashMap = new HashMap();
        String[] strArr = {"true", "false"};
        hashMap.put(Messages.get(CACHEMETA), strArr);
        hashMap.put(Messages.get(LASTUPDATECOUNT), strArr);
        hashMap.put(Messages.get(NAMEDPIPE), strArr);
        hashMap.put(Messages.get(TCPNODELAY), strArr);
        hashMap.put(Messages.get(SENDSTRINGPARAMETERSASUNICODE), strArr);
        hashMap.put(Messages.get(USECURSORS), strArr);
        hashMap.put(Messages.get(USELOBS), strArr);
        hashMap.put(Messages.get(XAEMULATION), strArr);
        hashMap.put(Messages.get(PREPARESQL), new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(2), String.valueOf(3)});
        hashMap.put(Messages.get(SERVERTYPE), new String[]{String.valueOf(1), String.valueOf(2)});
        hashMap.put(Messages.get(TDS), new String[]{DefaultProperties.TDS_VERSION_42, DefaultProperties.TDS_VERSION_50, DefaultProperties.TDS_VERSION_70, DefaultProperties.TDS_VERSION_80});
        hashMap.put(Messages.get(SSL), new String[]{"off", Ssl.SSL_REQUEST, Ssl.SSL_REQUIRE, Ssl.SSL_AUTHENTICATE});
        return hashMap;
    }

    private static Map createRequiredTrueMap() {
        HashMap hashMap = new HashMap();
        hashMap.put(Messages.get(SERVERNAME), null);
        hashMap.put(Messages.get(SERVERTYPE), null);
        return hashMap;
    }

    private static Properties parseURL(String str, Properties properties) {
        Properties properties2 = new Properties();
        Enumeration propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String str2 = (String) propertyNames.nextElement();
            String property = properties.getProperty(str2);
            if (property != null) {
                properties2.setProperty(str2.toUpperCase(), property);
            }
        }
        StringBuilder sb = new StringBuilder(16);
        int nextToken = nextToken(str, 0, sb);
        if (!"jdbc".equalsIgnoreCase(sb.toString())) {
            return null;
        }
        int nextToken2 = nextToken(str, nextToken, sb);
        if (!"jtds".equalsIgnoreCase(sb.toString())) {
            return null;
        }
        int nextToken3 = nextToken(str, nextToken2, sb);
        Integer serverType = DefaultProperties.getServerType(sb.toString().toLowerCase());
        if (serverType == null) {
            return null;
        }
        properties2.setProperty(Messages.get(SERVERTYPE), String.valueOf(serverType));
        int nextToken4 = nextToken(str, nextToken3, sb);
        if (sb.length() > 0) {
            return null;
        }
        int nextToken5 = nextToken(str, nextToken4, sb);
        String sb2 = sb.toString();
        int length = sb2.length();
        String str3 = SERVERNAME;
        if (length == 0) {
            sb2 = properties2.getProperty(Messages.get(str3));
            if (sb2 == null || sb2.length() == 0) {
                return null;
            }
        }
        properties2.setProperty(Messages.get(str3), sb2);
        if (str.charAt(nextToken5 - 1) == ':' && nextToken5 < str.length()) {
            nextToken5 = nextToken(str, nextToken5, sb);
            try {
                properties2.setProperty(Messages.get(PORTNUMBER), Integer.toString(Integer.parseInt(sb.toString())));
            } catch (NumberFormatException unused) {
                return null;
            }
        }
        if (str.charAt(nextToken5 - 1) == '/' && nextToken5 < str.length()) {
            nextToken5 = nextToken(str, nextToken5, sb);
            properties2.setProperty(Messages.get(DATABASENAME), sb.toString());
        }
        while (str.charAt(nextToken5 - 1) == ';' && nextToken5 < str.length()) {
            nextToken5 = nextToken(str, nextToken5, sb);
            String sb3 = sb.toString();
            int indexOf = sb3.indexOf(61);
            if (indexOf <= 0 || indexOf >= sb3.length() - 1) {
                properties2.setProperty(sb3.toUpperCase(), "");
            } else {
                properties2.setProperty(sb3.substring(0, indexOf).toUpperCase(), sb3.substring(indexOf + 1));
            }
        }
        return properties2;
    }

    private static int nextToken(String str, int i, StringBuilder sb) {
        int i2;
        sb.setLength(0);
        loop0:
        while (true) {
            boolean z = false;
            while (i < str.length()) {
                i2 = i + 1;
                char charAt = str.charAt(i);
                if (!z) {
                    if (charAt == ':' || charAt == ';') {
                        break loop0;
                    } else if (charAt == '/') {
                        if (i2 < str.length() && str.charAt(i2) == '/') {
                            return i2 + 1;
                        }
                    }
                }
                if (charAt == '[') {
                    z = true;
                } else if (charAt == ']') {
                    i = i2;
                } else {
                    sb.append(charAt);
                }
                i = i2;
            }
            return i;
        }
        return i2;
    }

    public static void main(String[] strArr) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("jTDS ");
        sb.append(getVersion());
        printStream.println(sb.toString());
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new AbstractMethodError();
    }
}
