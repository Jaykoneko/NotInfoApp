package net.sourceforge.jtds.jdbc.cache;

import net.sourceforge.jtds.jdbc.JtdsConnection;

public class SQLCacheKey {
    private final int hashCode;
    private final int majorVersion;
    private final int minorVersion;
    private final int serverType;
    private final String sql;

    public SQLCacheKey(String str, JtdsConnection jtdsConnection) {
        this.sql = str;
        this.serverType = jtdsConnection.getServerType();
        this.majorVersion = jtdsConnection.getDatabaseMajorVersion();
        this.minorVersion = jtdsConnection.getDatabaseMinorVersion();
        this.hashCode = str.hashCode() ^ (((this.serverType << 24) | (this.majorVersion << 16)) | this.minorVersion);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        try {
            SQLCacheKey sQLCacheKey = (SQLCacheKey) obj;
            if (this.hashCode == sQLCacheKey.hashCode && this.majorVersion == sQLCacheKey.majorVersion && this.minorVersion == sQLCacheKey.minorVersion && this.serverType == sQLCacheKey.serverType && this.sql.equals(sQLCacheKey.sql)) {
                return true;
            }
            return false;
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
    }
}
