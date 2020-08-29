package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;

public class StatementProxy implements Statement {
    private ConnectionProxy _connection;
    private JtdsStatement _statement;

    StatementProxy(ConnectionProxy connectionProxy, JtdsStatement jtdsStatement) {
        this._connection = connectionProxy;
        this._statement = jtdsStatement;
    }

    public ResultSet executeQuery(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeQuery(str);
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int executeUpdate(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void close() throws SQLException {
        validateConnection();
        try {
            this._statement.close();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMaxFieldSize();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setMaxFieldSize(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getMaxRows() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMaxRows();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxRows(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setMaxRows(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setEscapeProcessing(boolean z) throws SQLException {
        validateConnection();
        try {
            this._statement.setEscapeProcessing(z);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getQueryTimeout() throws SQLException {
        validateConnection();
        try {
            return this._statement.getQueryTimeout();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setQueryTimeout(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setQueryTimeout(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void cancel() throws SQLException {
        validateConnection();
        try {
            this._statement.cancel();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        validateConnection();
        try {
            return this._statement.getWarnings();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void clearWarnings() throws SQLException {
        validateConnection();
        try {
            this._statement.clearWarnings();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setCursorName(String str) throws SQLException {
        validateConnection();
        try {
            this._statement.setCursorName(str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public boolean execute(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public ResultSet getResultSet() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSet();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int getUpdateCount() throws SQLException {
        validateConnection();
        try {
            return this._statement.getUpdateCount();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public boolean getMoreResults() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMoreResults();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setFetchDirection(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getFetchDirection() throws SQLException {
        validateConnection();
        try {
            return this._statement.getFetchDirection();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setFetchSize(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setFetchSize(i);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getFetchSize() throws SQLException {
        validateConnection();
        try {
            return this._statement.getFetchSize();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetConcurrency();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getResultSetType() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetType();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void addBatch(String str) throws SQLException {
        validateConnection();
        try {
            this._statement.addBatch(str);
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void clearBatch() throws SQLException {
        validateConnection();
        try {
            this._statement.clearBatch();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        validateConnection();
        try {
            return this._statement.executeBatch();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Connection getConnection() throws SQLException {
        validateConnection();
        try {
            return this._statement.getConnection();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean getMoreResults(int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.getMoreResults(i);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        validateConnection();
        try {
            return this._statement.getGeneratedKeys();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int executeUpdate(String str, int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, i);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, iArr);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, strArr);
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public boolean execute(String str, int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, i);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, iArr);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, strArr);
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public int getResultSetHoldability() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetHoldability();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    /* access modifiers changed from: protected */
    public void validateConnection() throws SQLException {
        if (this._connection.isClosed()) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    /* access modifiers changed from: protected */
    public void processSQLException(SQLException sQLException) throws SQLException {
        this._connection.processSQLException(sQLException);
        throw sQLException;
    }

    public boolean isClosed() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isPoolable() throws SQLException {
        throw new AbstractMethodError();
    }

    public void setPoolable(boolean z) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }
}
