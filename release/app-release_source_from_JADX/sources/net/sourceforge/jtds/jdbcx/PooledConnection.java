package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;

public class PooledConnection implements javax.sql.PooledConnection {
    protected Connection connection;
    private ArrayList listeners = new ArrayList();

    public PooledConnection(Connection connection2) {
        this.connection = connection2;
    }

    public synchronized void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        ArrayList arrayList = (ArrayList) this.listeners.clone();
        this.listeners = arrayList;
        arrayList.add(connectionEventListener);
    }

    public synchronized void close() throws SQLException {
        this.connection.close();
        this.connection = null;
    }

    public synchronized void fireConnectionEvent(boolean z, SQLException sQLException) {
        if (this.listeners.size() > 0) {
            ConnectionEvent connectionEvent = new ConnectionEvent(this, sQLException);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ConnectionEventListener connectionEventListener = (ConnectionEventListener) it.next();
                if (z) {
                    connectionEventListener.connectionClosed(connectionEvent);
                } else {
                    try {
                        if (this.connection == null || this.connection.isClosed()) {
                            connectionEventListener.connectionErrorOccurred(connectionEvent);
                        }
                    } catch (SQLException unused) {
                    }
                }
            }
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (this.connection == null) {
            fireConnectionEvent(false, new SQLException(Messages.get("error.jdbcx.conclosed"), "08003"));
            return null;
        }
        return new ConnectionProxy(this, this.connection);
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        ArrayList arrayList = (ArrayList) this.listeners.clone();
        this.listeners = arrayList;
        arrayList.remove(connectionEventListener);
    }

    public void addStatementEventListener(StatementEventListener statementEventListener) {
        throw new AbstractMethodError();
    }

    public void removeStatementEventListener(StatementEventListener statementEventListener) {
        throw new AbstractMethodError();
    }
}
