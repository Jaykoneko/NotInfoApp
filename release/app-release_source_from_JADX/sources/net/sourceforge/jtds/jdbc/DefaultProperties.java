package net.sourceforge.jtds.jdbc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class DefaultProperties {
    public static final String APP_NAME = "jTDS";
    public static final String AUTO_COMMIT = "true";
    public static final String BATCH_SIZE_SQLSERVER = "0";
    public static final String BATCH_SIZE_SYBASE = "1000";
    public static final String BIND_ADDRESS = "";
    public static final String BUFFER_DIR = new File(System.getProperty("java.io.tmpdir")).toString();
    public static final String BUFFER_MAX_MEMORY = "1024";
    public static final String BUFFER_MIN_PACKETS = "8";
    public static final String CACHEMETA = "false";
    public static final String CHARSET = "";
    public static final String DATABASE_NAME = "";
    public static final String DOMAIN = "";
    public static final String INSTANCE = "";
    public static final String LANGUAGE = "";
    public static final String LAST_UPDATE_COUNT = "true";
    public static final String LOB_BUFFER_SIZE = "32768";
    public static final String LOGFILE = "";
    public static final String LOGIN_TIMEOUT = "0";
    public static final String MAC_ADDRESS = "000000000000";
    public static final String MAX_STATEMENTS = "500";
    public static final String NAMED_PIPE = "false";
    public static final String NAMED_PIPE_PATH_SQLSERVER = "/sql/query";
    public static final String NAMED_PIPE_PATH_SYBASE = "/sybase/query";
    public static final String PACKET_SIZE_42 = String.valueOf(512);
    public static final String PACKET_SIZE_50 = "0";
    public static final String PACKET_SIZE_70_80 = "0";
    public static final String PASSWORD = "";
    public static final String PORT_NUMBER_SQLSERVER = "1433";
    public static final String PORT_NUMBER_SYBASE = "7100";
    public static final String PREPARE_SQLSERVER = String.valueOf(3);
    public static final String PREPARE_SYBASE = String.valueOf(1);
    public static final String PROCESS_ID = "123";
    public static final String PROG_NAME = "jTDS";
    public static final String SERVER_TYPE_SQLSERVER = "sqlserver";
    public static final String SERVER_TYPE_SYBASE = "sybase";
    public static final String SOCKET_KEEPALIVE = "false";
    public static final String SOCKET_TIMEOUT = "0";
    public static final String SSL = "off";
    public static final String TCP_NODELAY = "true";
    public static final String TDS_VERSION_42 = "4.2";
    public static final String TDS_VERSION_50 = "5.0";
    public static final String TDS_VERSION_70 = "7.0";
    public static final String TDS_VERSION_80 = "8.0";
    public static final String TDS_VERSION_90 = "9.0";
    public static final String USECURSORS = "false";
    public static final String USEJCIFS = "false";
    public static final String USEKERBEROS = "false";
    public static final String USELOBS = "true";
    public static final String USENTLMV2 = "false";
    public static final String USER = "";
    public static final String USE_UNICODE = "true";
    public static final String WSID = "";
    public static final String XAEMULATION = "true";
    private static final HashMap batchSizeDefaults;
    private static final HashMap packetSizeDefaults;
    private static final HashMap portNumberDefaults;
    private static final HashMap prepareSQLDefaults;
    private static final HashMap tdsDefaults;

    public static String getServerType(int i) {
        if (i == 1) {
            return SERVER_TYPE_SQLSERVER;
        }
        if (i == 2) {
            return SERVER_TYPE_SYBASE;
        }
        return null;
    }

    static {
        HashMap hashMap = new HashMap(2);
        tdsDefaults = hashMap;
        String valueOf = String.valueOf(1);
        String str = TDS_VERSION_80;
        hashMap.put(valueOf, str);
        HashMap hashMap2 = tdsDefaults;
        String valueOf2 = String.valueOf(2);
        String str2 = TDS_VERSION_50;
        hashMap2.put(valueOf2, str2);
        HashMap hashMap3 = new HashMap(2);
        portNumberDefaults = hashMap3;
        hashMap3.put(String.valueOf(1), PORT_NUMBER_SQLSERVER);
        portNumberDefaults.put(String.valueOf(2), PORT_NUMBER_SYBASE);
        HashMap hashMap4 = new HashMap(5);
        packetSizeDefaults = hashMap4;
        hashMap4.put(TDS_VERSION_42, PACKET_SIZE_42);
        String str3 = "0";
        packetSizeDefaults.put(str2, str3);
        packetSizeDefaults.put(TDS_VERSION_70, str3);
        packetSizeDefaults.put(str, str3);
        packetSizeDefaults.put(TDS_VERSION_90, str3);
        HashMap hashMap5 = new HashMap(2);
        batchSizeDefaults = hashMap5;
        hashMap5.put(String.valueOf(1), str3);
        batchSizeDefaults.put(String.valueOf(2), BATCH_SIZE_SYBASE);
        HashMap hashMap6 = new HashMap(2);
        prepareSQLDefaults = hashMap6;
        hashMap6.put(String.valueOf(1), PREPARE_SQLSERVER);
        prepareSQLDefaults.put(String.valueOf(2), PREPARE_SYBASE);
    }

    public static Properties addDefaultProperties(Properties properties) {
        String str = Driver.SERVERTYPE;
        if (properties.getProperty(Messages.get(str)) == null) {
            return null;
        }
        HashMap hashMap = tdsDefaults;
        String str2 = Driver.TDS;
        addDefaultPropertyIfNotSet(properties, str2, str, hashMap);
        addDefaultPropertyIfNotSet(properties, Driver.PORTNUMBER, str, portNumberDefaults);
        String str3 = "";
        addDefaultPropertyIfNotSet(properties, Driver.USER, str3);
        addDefaultPropertyIfNotSet(properties, Driver.PASSWORD, str3);
        addDefaultPropertyIfNotSet(properties, Driver.DATABASENAME, str3);
        addDefaultPropertyIfNotSet(properties, Driver.INSTANCE, str3);
        addDefaultPropertyIfNotSet(properties, Driver.DOMAIN, str3);
        String str4 = "jTDS";
        addDefaultPropertyIfNotSet(properties, Driver.APPNAME, str4);
        String str5 = "true";
        addDefaultPropertyIfNotSet(properties, Driver.AUTOCOMMIT, str5);
        addDefaultPropertyIfNotSet(properties, Driver.PROGNAME, str4);
        addDefaultPropertyIfNotSet(properties, Driver.WSID, str3);
        addDefaultPropertyIfNotSet(properties, Driver.BATCHSIZE, str, batchSizeDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.LASTUPDATECOUNT, str5);
        addDefaultPropertyIfNotSet(properties, Driver.LOBBUFFER, LOB_BUFFER_SIZE);
        String str6 = "0";
        addDefaultPropertyIfNotSet(properties, Driver.LOGINTIMEOUT, str6);
        addDefaultPropertyIfNotSet(properties, Driver.SOTIMEOUT, str6);
        String str7 = "false";
        addDefaultPropertyIfNotSet(properties, Driver.SOKEEPALIVE, str7);
        addDefaultPropertyIfNotSet(properties, Driver.PROCESSID, PROCESS_ID);
        addDefaultPropertyIfNotSet(properties, Driver.MACADDRESS, MAC_ADDRESS);
        addDefaultPropertyIfNotSet(properties, Driver.MAXSTATEMENTS, MAX_STATEMENTS);
        addDefaultPropertyIfNotSet(properties, Driver.NAMEDPIPE, str7);
        addDefaultPropertyIfNotSet(properties, Driver.PACKETSIZE, str2, packetSizeDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.CACHEMETA, str7);
        addDefaultPropertyIfNotSet(properties, Driver.CHARSET, str3);
        addDefaultPropertyIfNotSet(properties, Driver.LANGUAGE, str3);
        addDefaultPropertyIfNotSet(properties, Driver.PREPARESQL, str, prepareSQLDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.SENDSTRINGPARAMETERSASUNICODE, str5);
        addDefaultPropertyIfNotSet(properties, Driver.TCPNODELAY, str5);
        addDefaultPropertyIfNotSet(properties, Driver.XAEMULATION, str5);
        addDefaultPropertyIfNotSet(properties, Driver.LOGFILE, str3);
        addDefaultPropertyIfNotSet(properties, Driver.SSL, "off");
        addDefaultPropertyIfNotSet(properties, Driver.USECURSORS, str7);
        addDefaultPropertyIfNotSet(properties, Driver.USENTLMV2, str7);
        addDefaultPropertyIfNotSet(properties, Driver.USEKERBEROS, str7);
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERMAXMEMORY, BUFFER_MAX_MEMORY);
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERMINPACKETS, BUFFER_MIN_PACKETS);
        addDefaultPropertyIfNotSet(properties, Driver.USELOBS, str5);
        addDefaultPropertyIfNotSet(properties, Driver.BINDADDRESS, str3);
        addDefaultPropertyIfNotSet(properties, Driver.USEJCIFS, str7);
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERDIR, BUFFER_DIR);
        return properties;
    }

    private static void addDefaultPropertyIfNotSet(Properties properties, String str, String str2) {
        String str3 = Messages.get(str);
        if (properties.getProperty(str3) == null) {
            properties.setProperty(str3, str2);
        }
    }

    private static void addDefaultPropertyIfNotSet(Properties properties, String str, String str2, Map map) {
        String property = properties.getProperty(Messages.get(str2));
        if (property != null) {
            String str3 = Messages.get(str);
            if (properties.getProperty(str3) == null) {
                Object obj = map.get(property);
                if (obj != null) {
                    properties.setProperty(str3, String.valueOf(obj));
                }
            }
        }
    }

    public static String getNamedPipePath(int i) {
        if (i == 0 || i == 1) {
            return NAMED_PIPE_PATH_SQLSERVER;
        }
        if (i == 2) {
            return NAMED_PIPE_PATH_SYBASE;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown serverType: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public static Integer getServerType(String str) {
        if (SERVER_TYPE_SQLSERVER.equals(str)) {
            return new Integer(1);
        }
        if (SERVER_TYPE_SYBASE.equals(str)) {
            return new Integer(2);
        }
        return null;
    }

    public static String getServerTypeWithDefault(int i) {
        if (i == 0) {
            return SERVER_TYPE_SQLSERVER;
        }
        if (i == 1 || i == 2) {
            return getServerType(i);
        }
        throw new IllegalArgumentException("Only 0, 1 and 2 accepted for serverType");
    }

    public static Integer getTdsVersion(String str) {
        if (TDS_VERSION_42.equals(str)) {
            return new Integer(1);
        }
        if (TDS_VERSION_50.equals(str)) {
            return new Integer(2);
        }
        if (TDS_VERSION_70.equals(str)) {
            return new Integer(3);
        }
        if (TDS_VERSION_80.equals(str)) {
            return new Integer(4);
        }
        return null;
    }
}
