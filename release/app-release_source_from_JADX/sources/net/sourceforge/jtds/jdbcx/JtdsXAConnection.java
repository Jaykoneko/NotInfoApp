package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import net.sourceforge.jtds.jdbc.XASupport;

public class JtdsXAConnection extends PooledConnection implements XAConnection {
    private final JtdsDataSource dataSource;
    private final XAResource resource;
    private final int xaConnectionId;

    public JtdsXAConnection(JtdsDataSource jtdsDataSource, Connection connection) throws SQLException {
        super(connection);
        this.resource = new JtdsXAResource(this, connection);
        this.dataSource = jtdsDataSource;
        this.xaConnectionId = XASupport.xa_open(connection);
    }

    /* access modifiers changed from: 0000 */
    public int getXAConnectionID() {
        return this.xaConnectionId;
    }

    public XAResource getXAResource() throws SQLException {
        return this.resource;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void close() throws java.sql.SQLException {
        /*
            r2 = this;
            monitor-enter(r2)
            java.sql.Connection r0 = r2.connection     // Catch:{ SQLException -> 0x000b }
            int r1 = r2.xaConnectionId     // Catch:{ SQLException -> 0x000b }
            net.sourceforge.jtds.jdbc.XASupport.xa_close(r0, r1)     // Catch:{ SQLException -> 0x000b }
            goto L_0x000b
        L_0x0009:
            r0 = move-exception
            goto L_0x0010
        L_0x000b:
            super.close()     // Catch:{ all -> 0x0009 }
            monitor-exit(r2)
            return
        L_0x0010:
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbcx.JtdsXAConnection.close():void");
    }

    /* access modifiers changed from: protected */
    public JtdsDataSource getXADataSource() {
        return this.dataSource;
    }
}
