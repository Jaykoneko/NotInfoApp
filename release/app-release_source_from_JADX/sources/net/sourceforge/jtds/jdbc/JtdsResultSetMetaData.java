package net.sourceforge.jtds.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JtdsResultSetMetaData implements ResultSetMetaData {
    private final int columnCount;
    private final ColInfo[] columns;
    private final boolean useLOBs;

    JtdsResultSetMetaData(ColInfo[] colInfoArr, int i, boolean z) {
        this.columns = colInfoArr;
        this.columnCount = i;
        this.useLOBs = z;
    }

    /* access modifiers changed from: 0000 */
    public ColInfo getColumn(int i) throws SQLException {
        if (i >= 1 && i <= this.columnCount) {
            return this.columns[i - 1];
        }
        throw new SQLException(Messages.get("error.resultset.colindex", (Object) Integer.toString(i)), "07009");
    }

    public int getColumnCount() throws SQLException {
        return this.columnCount;
    }

    public int getColumnDisplaySize(int i) throws SQLException {
        return getColumn(i).displaySize;
    }

    public int getColumnType(int i) throws SQLException {
        if (this.useLOBs) {
            return getColumn(i).jdbcType;
        }
        return Support.convertLOBType(getColumn(i).jdbcType);
    }

    public int getPrecision(int i) throws SQLException {
        return getColumn(i).precision;
    }

    public int getScale(int i) throws SQLException {
        return getColumn(i).scale;
    }

    public int isNullable(int i) throws SQLException {
        return getColumn(i).nullable;
    }

    public boolean isAutoIncrement(int i) throws SQLException {
        return getColumn(i).isIdentity;
    }

    public boolean isCaseSensitive(int i) throws SQLException {
        return getColumn(i).isCaseSensitive;
    }

    public boolean isCurrency(int i) throws SQLException {
        return TdsData.isCurrency(getColumn(i));
    }

    public boolean isDefinitelyWritable(int i) throws SQLException {
        getColumn(i);
        return false;
    }

    public boolean isReadOnly(int i) throws SQLException {
        return !getColumn(i).isWriteable;
    }

    public boolean isSearchable(int i) throws SQLException {
        return TdsData.isSearchable(getColumn(i));
    }

    public boolean isSigned(int i) throws SQLException {
        return TdsData.isSigned(getColumn(i));
    }

    public boolean isWritable(int i) throws SQLException {
        return getColumn(i).isWriteable;
    }

    public String getCatalogName(int i) throws SQLException {
        ColInfo column = getColumn(i);
        return column.catalog == null ? "" : column.catalog;
    }

    public String getColumnClassName(int i) throws SQLException {
        String className = Support.getClassName(getColumnType(i));
        if (!this.useLOBs) {
            if ("java.sql.Clob".equals(className)) {
                return "java.lang.String";
            }
            if ("java.sql.Blob".equals(className)) {
                className = "[B";
            }
        }
        return className;
    }

    public String getColumnLabel(int i) throws SQLException {
        return getColumn(i).name;
    }

    public String getColumnName(int i) throws SQLException {
        return getColumn(i).name;
    }

    public String getColumnTypeName(int i) throws SQLException {
        return getColumn(i).sqlType;
    }

    public String getSchemaName(int i) throws SQLException {
        ColInfo column = getColumn(i);
        return column.schema == null ? "" : column.schema;
    }

    public String getTableName(int i) throws SQLException {
        ColInfo column = getColumn(i);
        return column.tableName == null ? "" : column.tableName;
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }
}
