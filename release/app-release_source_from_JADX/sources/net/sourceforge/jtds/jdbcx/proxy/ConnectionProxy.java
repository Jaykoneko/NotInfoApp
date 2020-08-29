package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.PooledConnection;

public class ConnectionProxy implements Connection {
    private boolean _closed;
    private JtdsConnection _connection;
    private PooledConnection _pooledConnection;

    public ConnectionProxy(PooledConnection pooledConnection, Connection connection) {
        this._pooledConnection = pooledConnection;
        this._connection = (JtdsConnection) connection;
    }

    public void clearWarnings() throws SQLException {
        validateConnection();
        try {
            this._connection.clearWarnings();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void close() {
        if (!this._closed) {
            this._pooledConnection.fireConnectionEvent(true, null);
            this._closed = true;
        }
    }

    public void commit() throws SQLException {
        validateConnection();
        try {
            this._connection.commit();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public Statement createStatement() throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement());
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Statement createStatement(int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement(i, i2));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Statement createStatement(int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement(i, i2, i3));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        validateConnection();
        try {
            return this._connection.getAutoCommit();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String getCatalog() throws SQLException {
        validateConnection();
        try {
            return this._connection.getCatalog();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int getHoldability() throws SQLException {
        validateConnection();
        try {
            return this._connection.getHoldability();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        validateConnection();
        try {
            return this._connection.getTransactionIsolation();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public Map getTypeMap() throws SQLException {
        validateConnection();
        try {
            return this._connection.getTypeMap();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        validateConnection();
        try {
            return this._connection.getWarnings();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        validateConnection();
        try {
            return this._connection.getMetaData();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean isClosed() throws SQLException {
        if (this._closed) {
            return true;
        }
        try {
            return this._connection.isClosed();
        } catch (SQLException e) {
            processSQLException(e);
            return this._closed;
        }
    }

    public boolean isReadOnly() throws SQLException {
        validateConnection();
        try {
            return this._connection.isReadOnly();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String nativeSQL(String str) throws SQLException {
        validateConnection();
        try {
            return this._connection.nativeSQL(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public CallableStatement prepareCall(String str) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public CallableStatement prepareCall(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str, i, i2));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public CallableStatement prepareCall(String str, int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str, i, i2, i3));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, iArr));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, strArr));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i, i2));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i, i2, i3));
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        validateConnection();
        try {
            this._connection.releaseSavepoint(savepoint);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void rollback() throws SQLException {
        validateConnection();
        try {
            this._connection.rollback();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        validateConnection();
        try {
            this._connection.rollback(savepoint);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setAutoCommit(boolean z) throws SQLException {
        validateConnection();
        try {
            this._connection.setAutoCommit(z);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setCatalog(String str) throws SQLException {
        validateConnection();
        try {
            this._connection.setCatalog(str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setHoldability(int i) throws SQLException {
        validateConnection();
        try {
            this._connection.setHoldability(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setReadOnly(boolean z) throws SQLException {
        validateConnection();
        try {
            this._connection.setReadOnly(z);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        validateConnection();
        try {
            return this._connection.setSavepoint();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Savepoint setSavepoint(String str) throws SQLException {
        validateConnection();
        try {
            return this._connection.setSavepoint(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void setTransactionIsolation(int i) throws SQLException {
        validateConnection();
        try {
            this._connection.setTransactionIsolation(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setTypeMap(Map map) throws SQLException {
        validateConnection();
        try {
            this._connection.setTypeMap(map);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    private void validateConnection() throws SQLException {
        if (this._closed) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    /* access modifiers changed from: 0000 */
    public void processSQLException(SQLException sQLException) throws SQLException {
        this._pooledConnection.fireConnectionEvent(false, sQLException);
        throw sQLException;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        close();
    }

    public Array createArrayOf(String str, Object[] objArr) throws SQLException {
        throw new AbstractMethodError();
    }

    public Blob createBlob() throws SQLException {
        throw new AbstractMethodError();
    }

    public Clob createClob() throws SQLException {
        throw new AbstractMethodError();
    }

    public NClob createNClob() throws SQLException {
        throw new AbstractMethodError();
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new AbstractMethodError();
    }

    public Struct createStruct(String str, Object[] objArr) throws SQLException {
        throw new AbstractMethodError();
    }

    public Properties getClientInfo() throws SQLException {
        throw new AbstractMethodError();
    }

    public String getClientInfo(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isValid(int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    public void setClientInfo(String str, String str2) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setSchema(String str) throws SQLException {
        throw new AbstractMethodError();
    }

    public String getSchema() throws SQLException {
        throw new AbstractMethodError();
    }

    public void abort(Executor executor) throws SQLException {
        throw new AbstractMethodError();
    }

    public void setNetworkTimeout(Executor executor, int i) throws SQLException {
        throw new AbstractMethodError();
    }

    public int getNetworkTimeout() throws SQLException {
        throw new AbstractMethodError();
    }
}
