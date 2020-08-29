package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

class SavepointImpl implements Savepoint {

    /* renamed from: id */
    private final int f119id;
    private final String name;

    SavepointImpl(int i) {
        this(i, null);
    }

    SavepointImpl(int i, String str) {
        this.f119id = i;
        this.name = str;
    }

    public int getSavepointId() throws SQLException {
        if (this.name == null) {
            return this.f119id;
        }
        throw new SQLException(Messages.get("error.savepoint.named"), "HY024");
    }

    public String getSavepointName() throws SQLException {
        String str = this.name;
        if (str != null) {
            return str;
        }
        throw new SQLException(Messages.get("error.savepoint.unnamed"), "HY024");
    }

    /* access modifiers changed from: 0000 */
    public int getId() {
        return this.f119id;
    }
}
