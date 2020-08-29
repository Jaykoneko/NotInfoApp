package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.XASupport;
import net.sourceforge.jtds.util.Logger;

public class JtdsXAResource implements XAResource {
    private final Connection connection;
    private final String rmHost;
    private final JtdsXAConnection xaConnection;

    public JtdsXAResource(JtdsXAConnection jtdsXAConnection, Connection connection2) {
        this.xaConnection = jtdsXAConnection;
        this.connection = connection2;
        this.rmHost = ((JtdsConnection) connection2).getRmHost();
        Logger.println("JtdsXAResource created");
    }

    /* access modifiers changed from: protected */
    public JtdsXAConnection getResourceManager() {
        return this.xaConnection;
    }

    /* access modifiers changed from: protected */
    public String getRmHost() {
        return this.rmHost;
    }

    public int getTransactionTimeout() throws XAException {
        Logger.println("XAResource.getTransactionTimeout()");
        return 0;
    }

    public boolean setTransactionTimeout(int i) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.setTransactionTimeout(");
        sb.append(i);
        sb.append(')');
        Logger.println(sb.toString());
        return false;
    }

    public boolean isSameRM(XAResource xAResource) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.isSameRM(");
        sb.append(xAResource.toString());
        sb.append(')');
        Logger.println(sb.toString());
        return (xAResource instanceof JtdsXAResource) && ((JtdsXAResource) xAResource).getRmHost().equals(this.rmHost);
    }

    public Xid[] recover(int i) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.recover(");
        sb.append(i);
        sb.append(')');
        Logger.println(sb.toString());
        return XASupport.xa_recover(this.connection, this.xaConnection.getXAConnectionID(), i);
    }

    public int prepare(Xid xid) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.prepare(");
        sb.append(xid.toString());
        sb.append(')');
        Logger.println(sb.toString());
        return XASupport.xa_prepare(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void forget(Xid xid) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.forget(");
        sb.append(xid);
        sb.append(')');
        Logger.println(sb.toString());
        XASupport.xa_forget(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void rollback(Xid xid) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.rollback(");
        sb.append(xid.toString());
        sb.append(')');
        Logger.println(sb.toString());
        XASupport.xa_rollback(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void end(Xid xid, int i) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.end(");
        sb.append(xid.toString());
        sb.append(')');
        Logger.println(sb.toString());
        XASupport.xa_end(this.connection, this.xaConnection.getXAConnectionID(), xid, i);
    }

    public void start(Xid xid, int i) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.start(");
        sb.append(xid.toString());
        sb.append(',');
        sb.append(i);
        sb.append(')');
        Logger.println(sb.toString());
        XASupport.xa_start(this.connection, this.xaConnection.getXAConnectionID(), xid, i);
    }

    public void commit(Xid xid, boolean z) throws XAException {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResource.commit(");
        sb.append(xid.toString());
        sb.append(',');
        sb.append(z);
        sb.append(')');
        Logger.println(sb.toString());
        XASupport.xa_commit(this.connection, this.xaConnection.getXAConnectionID(), xid, z);
    }
}
