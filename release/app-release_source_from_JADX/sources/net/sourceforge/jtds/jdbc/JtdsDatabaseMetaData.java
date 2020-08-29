package net.sourceforge.jtds.jdbc;

import androidx.core.view.PointerIconCompat;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JtdsDatabaseMetaData implements DatabaseMetaData {
    static final int sqlStateXOpen = 1;
    Boolean caseSensitive;
    private final JtdsConnection connection;
    private final int serverType;
    int sysnameLength = 30;
    private final int tdsVersion;

    public boolean allProceduresAreCallable() throws SQLException {
        return true;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int i) throws SQLException {
        return true;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        return 2;
    }

    public int getDriverMajorVersion() {
        return 1;
    }

    public int getDriverMinorVersion() {
        return 3;
    }

    public String getDriverName() throws SQLException {
        return "jTDS Type 4 JDBC Driver for MS SQL Server and Sybase";
    }

    public String getExtraNameCharacters() throws SQLException {
        return "$#@";
    }

    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 3;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 131072;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 131072;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 4096;
    }

    public int getMaxConnections() throws SQLException {
        return 32767;
    }

    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    public int getMaxStatements() throws SQLException {
        return 0;
    }

    public String getNumericFunctions() throws SQLException {
        return "abs,acos,asin,atan,atan2,ceiling,cos,cot,degrees,exp,floor,log,log10,mod,pi,power,radians,rand,round,sign,sin,sqrt,tan";
    }

    public String getProcedureTerm() throws SQLException {
        return "stored procedure";
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    public String getSQLKeywords() throws SQLException {
        return "ARITH_OVERFLOW,BREAK,BROWSE,BULK,CHAR_CONVERT,CHECKPOINT,CLUSTERED,COMPUTE,CONFIRM,CONTROLROW,DATA_PGS,DATABASE,DBCC,DISK,DUMMY,DUMP,ENDTRAN,ERRLVL,ERRORDATA,ERROREXIT,EXIT,FILLFACTOR,HOLDLOCK,IDENTITY_INSERT,IF,INDEX,KILL,LINENO,LOAD,MAX_ROWS_PER_PAGE,MIRROR,MIRROREXIT,NOHOLDLOCK,NONCLUSTERED,NUMERIC_TRUNCATION,OFF,OFFSETS,ONCE,ONLINE,OVER,PARTITION,PERM,PERMANENT,PLAN,PRINT,PROC,PROCESSEXIT,RAISERROR,READ,READTEXT,RECONFIGURE,REPLACE,RESERVED_PGS,RETURN,ROLE,ROWCNT,ROWCOUNT,RULE,SAVE,SETUSER,SHARED,SHUTDOWN,SOME,STATISTICS,STRIPE,SYB_IDENTITY,SYB_RESTREE,SYB_TERMINATE,TEMP,TEXTSIZE,TRAN,TRIGGER,TRUNCATE,TSEQUAL,UNPARTITION,USE,USED_PGS,USER_OPTION,WAITFOR,WHILE,WRITETEXT";
    }

    public int getSQLStateType() throws SQLException {
        return 1;
    }

    public String getSchemaTerm() throws SQLException {
        return "owner";
    }

    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    public String getSystemFunctions() throws SQLException {
        return "database,ifnull,user,convert";
    }

    public String getTimeDateFunctions() throws SQLException {
        return "curdate,curtime,dayname,dayofmonth,dayofweek,dayofyear,hour,minute,month,monthname,now,quarter,timestampadd,timestampdiff,second,week,year";
    }

    public boolean insertsAreDetected(int i) throws SQLException {
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    public boolean isReadOnly() throws SQLException {
        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    public boolean othersDeletesAreVisible(int i) throws SQLException {
        return i >= 1005;
    }

    public boolean othersInsertsAreVisible(int i) throws SQLException {
        return i == 1006;
    }

    public boolean othersUpdatesAreVisible(int i) throws SQLException {
        return i >= 1005;
    }

    public boolean ownDeletesAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean ownInsertsAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean ownUpdatesAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    public boolean supportsConvert() throws SQLException {
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0027, code lost:
        if (r9 == -1) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0029, code lost:
        if (r9 == -4) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002b, code lost:
        if (r9 == 2004) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002d, code lost:
        if (r9 == 2005) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0030, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0031, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean supportsConvert(int r8, int r9) throws java.sql.SQLException {
        /*
            r7 = this;
            r0 = 1
            if (r8 != r9) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 12
            if (r8 == r1) goto L_0x0049
            r2 = -4
            r3 = 2004(0x7d4, float:2.808E-42)
            r4 = 0
            if (r8 == r3) goto L_0x003d
            r5 = -1
            r6 = 2005(0x7d5, float:2.81E-42)
            if (r8 == r6) goto L_0x0032
            switch(r8) {
                case -7: goto L_0x0027;
                case -6: goto L_0x0027;
                case -5: goto L_0x0027;
                case -4: goto L_0x003d;
                case -3: goto L_0x001a;
                case -2: goto L_0x001a;
                case -1: goto L_0x0032;
                case 0: goto L_0x0049;
                case 1: goto L_0x0049;
                case 2: goto L_0x0027;
                case 3: goto L_0x0027;
                case 4: goto L_0x0027;
                case 5: goto L_0x0027;
                case 6: goto L_0x0027;
                case 7: goto L_0x0027;
                case 8: goto L_0x0027;
                default: goto L_0x0016;
            }
        L_0x0016:
            switch(r8) {
                case 91: goto L_0x0027;
                case 92: goto L_0x0027;
                case 93: goto L_0x0027;
                default: goto L_0x0019;
            }
        L_0x0019:
            return r4
        L_0x001a:
            r8 = 6
            if (r9 == r8) goto L_0x0025
            r8 = 7
            if (r9 == r8) goto L_0x0025
            r8 = 8
            if (r9 == r8) goto L_0x0025
            goto L_0x0026
        L_0x0025:
            r0 = 0
        L_0x0026:
            return r0
        L_0x0027:
            if (r9 == r5) goto L_0x0030
            if (r9 == r2) goto L_0x0030
            if (r9 == r3) goto L_0x0030
            if (r9 == r6) goto L_0x0030
            goto L_0x0031
        L_0x0030:
            r0 = 0
        L_0x0031:
            return r0
        L_0x0032:
            if (r9 == r0) goto L_0x003c
            if (r9 == r1) goto L_0x003c
            if (r9 == r6) goto L_0x003c
            if (r9 != r5) goto L_0x003b
            goto L_0x003c
        L_0x003b:
            r0 = 0
        L_0x003c:
            return r0
        L_0x003d:
            r8 = -2
            if (r9 == r8) goto L_0x0049
            r8 = -3
            if (r9 == r8) goto L_0x0049
            if (r9 == r3) goto L_0x0049
            if (r9 != r2) goto L_0x0048
            goto L_0x0049
        L_0x0048:
            r0 = 0
        L_0x0049:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsDatabaseMetaData.supportsConvert(int, int):boolean");
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return true;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return true;
    }

    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return true;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return true;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return true;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return true;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return true;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return true;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return true;
    }

    public boolean supportsResultSetHoldability(int i) throws SQLException {
        return false;
    }

    public boolean supportsResultSetType(int i) throws SQLException {
        return i >= 1003 && i <= 1006;
    }

    public boolean supportsSavepoints() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return true;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    public boolean supportsTransactionIsolationLevel(int i) throws SQLException {
        return i == 1 || i == 2 || i == 4 || i == 8;
    }

    public boolean supportsTransactions() throws SQLException {
        return true;
    }

    public boolean supportsUnion() throws SQLException {
        return true;
    }

    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    public boolean updatesAreDetected(int i) throws SQLException {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    public JtdsDatabaseMetaData(JtdsConnection jtdsConnection) {
        this.connection = jtdsConnection;
        this.tdsVersion = jtdsConnection.getTdsVersion();
        this.serverType = jtdsConnection.getServerType();
        if (this.tdsVersion >= 3) {
            this.sysnameLength = 128;
        }
    }

    public boolean allTablesAreSelectable() throws SQLException {
        return this.connection.getServerType() == 1;
    }

    public ResultSet getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) throws SQLException {
        String[] strArr = {"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] iArr = {5, 12, 4, 12, 4, 4, 5, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_special_columns ?, ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, "R");
        prepareCall.setString(5, "T");
        prepareCall.setString(6, "U");
        prepareCall.setInt(7, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i2 = 1; i2 <= columnCount; i2++) {
                if (i2 == 3) {
                    cachedResultSet.updateInt(i2, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i2), this.connection.getUseLOBs()));
                } else {
                    cachedResultSet.updateObject(i2, jtdsResultSet.getObject(i2));
                }
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getCatalogs() throws SQLException {
        JtdsResultSet jtdsResultSet = (JtdsResultSet) this.connection.createStatement().executeQuery("exec sp_tables '', '', '%', NULL");
        jtdsResultSet.setColumnCount(1);
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getColumnPrivileges(String str, String str2, String str3, String str4) throws SQLException {
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_column_privileges ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, processEscapes(str4));
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        jtdsResultSet.setColLabel(2, "TABLE_SCHEM");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getColumns(String str, String str2, String str3, String str4) throws SQLException {
        String str5 = str;
        String[] strArr = {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT"};
        int[] iArr = {12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str5, "sp_columns ?, ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str5);
        prepareCall.setString(4, processEscapes(str4));
        int i = 5;
        prepareCall.setInt(5, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            String string = jtdsResultSet.getString(6);
            String str6 = "YES";
            String str7 = "NO";
            String str8 = "identity";
            if (this.serverType == 2) {
                for (int i2 = 1; i2 <= 4; i2++) {
                    cachedResultSet.updateObject(i2, jtdsResultSet.getObject(i2));
                }
                cachedResultSet.updateInt(i, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i), this.connection.getUseLOBs()));
                cachedResultSet.updateString(6, string);
                for (int i3 = 8; i3 <= 12; i3++) {
                    cachedResultSet.updateObject(i3, jtdsResultSet.getObject(i3));
                }
                if (columnCount >= 20) {
                    for (int i4 = 13; i4 <= 18; i4++) {
                        cachedResultSet.updateObject(i4, jtdsResultSet.getObject(i4 + 2));
                    }
                } else {
                    cachedResultSet.updateObject(16, jtdsResultSet.getObject(8));
                    cachedResultSet.updateObject(17, jtdsResultSet.getObject(14));
                }
                if ("image".equals(string) || "text".equals(string)) {
                    cachedResultSet.updateInt(7, (int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    cachedResultSet.updateInt(16, (int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                } else if ("univarchar".equals(string) || "unichar".equals(string)) {
                    cachedResultSet.updateInt(7, jtdsResultSet.getInt(7) / 2);
                    cachedResultSet.updateObject(16, jtdsResultSet.getObject(7));
                } else {
                    cachedResultSet.updateInt(7, jtdsResultSet.getInt(7));
                }
                if (!string.toLowerCase().contains(str8)) {
                    str6 = str7;
                }
                cachedResultSet.updateString(23, str6);
            } else {
                int i5 = 1;
                while (i5 <= columnCount) {
                    if (i5 == i) {
                        cachedResultSet.updateInt(i5, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i5), this.connection.getUseLOBs()));
                    } else if (i5 == 19) {
                        cachedResultSet.updateString(6, TdsData.getMSTypeName(jtdsResultSet.getString(6), jtdsResultSet.getInt(19)));
                    } else {
                        cachedResultSet.updateObject(i5, jtdsResultSet.getObject(i5));
                    }
                    i5++;
                    i = 5;
                }
                if (!string.toLowerCase().contains(str8)) {
                    str6 = str7;
                }
                cachedResultSet.updateString(23, str6);
            }
            cachedResultSet.insertRow();
            i = 5;
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) throws SQLException {
        String str7;
        String str8 = str;
        String str9 = str4;
        String[] strArr = {"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"};
        int[] iArr = {12, 12, 12, 12, 12, 12, 12, 12, 5, 5, 5, 12, 12, 5};
        String str10 = "sp_fkeys ?, ?, ?, ?, ?, ?";
        if (str8 != null) {
            str7 = syscall(str8, str10);
        } else if (str9 != null) {
            str7 = syscall(str9, str10);
        } else {
            str7 = syscall(null, str10);
        }
        CallableStatement prepareCall = this.connection.prepareCall(str7);
        prepareCall.setString(1, str3);
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str8);
        prepareCall.setString(4, str6);
        prepareCall.setString(5, processEscapes(str5));
        prepareCall.setString(6, str9);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            if (columnCount < 14) {
                cachedResultSet.updateShort(14, 7);
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public String getDatabaseProductName() throws SQLException {
        return this.connection.getDatabaseProductName();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return this.connection.getDatabaseProductVersion();
    }

    public String getDriverVersion() throws SQLException {
        return Driver.getVersion();
    }

    public ResultSet getExportedKeys(String str, String str2, String str3) throws SQLException {
        return getCrossReference(str, str2, str3, null, null, null);
    }

    public ResultSet getImportedKeys(String str, String str2, String str3) throws SQLException {
        return getCrossReference(null, null, null, str, str2, str3);
    }

    public ResultSet getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) throws SQLException {
        String str4 = str;
        String[] strArr = {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION"};
        int[] iArr = {12, 12, 12, -7, 12, 12, 5, 5, 12, 12, 4, 4, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str4, "sp_statistics ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str4);
        prepareCall.setString(4, "%");
        prepareCall.setString(5, z ? "Y" : "N");
        prepareCall.setString(6, z2 ? "Q" : "E");
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxColumnNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    public int getMaxColumnsInTable() throws SQLException {
        if (this.tdsVersion >= 3) {
            return 1024;
        }
        return Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxIndexLength() throws SQLException {
        return this.tdsVersion >= 3 ? 900 : 255;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxRowSize() throws SQLException {
        return this.tdsVersion >= 3 ? 8060 : 1962;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxTableNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return this.tdsVersion > 2 ? 256 : 16;
    }

    public int getMaxUserNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public ResultSet getPrimaryKeys(String str, String str2, String str3) throws SQLException {
        String[] strArr = {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"};
        int[] iArr = {12, 12, 12, 12, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_pkeys ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getProcedureColumns(String str, String str2, String str3, String str4) throws SQLException {
        String str5;
        String str6 = str;
        String[] strArr = {"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS"};
        int[] iArr = {12, 12, 12, 12, 5, 4, 12, 4, 4, 5, 5, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str6, "sp_sproc_columns ?, ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str6);
        prepareCall.setString(4, processEscapes(str4));
        prepareCall.setInt(5, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        ResultSetMetaData metaData = jtdsResultSet.getMetaData();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        while (jtdsResultSet.next()) {
            int i = 1;
            int i2 = 0;
            while (true) {
                str5 = "RETURN_VALUE";
                if (i + i2 > 13) {
                    break;
                }
                if (i == 5) {
                    if (!"column_type".equalsIgnoreCase(metaData.getColumnName(i))) {
                        if (str5.equals(jtdsResultSet.getString(4))) {
                            cachedResultSet.updateInt(i, 5);
                        } else {
                            cachedResultSet.updateInt(i, 0);
                        }
                        i2 = 1;
                    }
                }
                if (i == 3) {
                    String string = jtdsResultSet.getString(i);
                    if (string != null && string.length() > 0) {
                        int lastIndexOf = string.lastIndexOf(59);
                        if (lastIndexOf >= 0) {
                            string = string.substring(0, lastIndexOf);
                        }
                    }
                    cachedResultSet.updateString(i + i2, string);
                } else {
                    if ("data_type".equalsIgnoreCase(metaData.getColumnName(i))) {
                        cachedResultSet.updateInt(i + i2, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i), this.connection.getUseLOBs()));
                    } else {
                        cachedResultSet.updateObject(i + i2, jtdsResultSet.getObject(i));
                    }
                }
                i++;
            }
            if (this.serverType == 2 && metaData.getColumnCount() >= 22) {
                String string2 = jtdsResultSet.getString(22);
                if (string2 != null) {
                    if (string2.equalsIgnoreCase("in")) {
                        cachedResultSet.updateInt(5, 1);
                    } else if (string2.equalsIgnoreCase("out")) {
                        cachedResultSet.updateInt(5, 2);
                    }
                }
            }
            if (this.serverType != 2) {
                int i3 = this.tdsVersion;
                if (!(i3 == 1 || i3 == 3)) {
                    cachedResultSet.insertRow();
                }
            }
            if (str5.equals(jtdsResultSet.getString(4))) {
                cachedResultSet.updateString(4, "@RETURN_VALUE");
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getProcedures(String str, String str2, String str3) throws SQLException {
        String[] strArr = {"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "RESERVED_1", "RESERVED_2", "RESERVED_3", "REMARKS", "PROCEDURE_TYPE"};
        int[] iArr = {12, 12, 12, 4, 4, 4, 12, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_stored_procedures ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            cachedResultSet.updateString(1, jtdsResultSet.getString(1));
            cachedResultSet.updateString(2, jtdsResultSet.getString(2));
            String string = jtdsResultSet.getString(3);
            if (string != null && string.endsWith(";1")) {
                string = string.substring(0, string.length() - 2);
            }
            cachedResultSet.updateString(3, string);
            for (int i = 4; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            if (columnCount < 8) {
                cachedResultSet.updateShort(8, 2);
            }
            cachedResultSet.insertRow();
        }
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return cachedResultSet;
    }

    public ResultSet getSchemas() throws SQLException {
        String str;
        Statement createStatement = this.connection.createStatement();
        if (this.connection.getServerType() != 1 || this.connection.getDatabaseMajorVersion() < 9) {
            String str2 = "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM dbo.sysusers";
            if (this.tdsVersion >= 3) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(" WHERE islogin=1");
                str = sb.toString();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(" WHERE uid>0");
                str = sb2.toString();
            }
        } else {
            str = "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM sys.schemas";
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(" ORDER BY TABLE_SCHEM");
        return createStatement.executeQuery(sb3.toString());
    }

    public String getStringFunctions() throws SQLException {
        return this.connection.getServerType() == 1 ? "ascii,char,concat,difference,insert,lcase,left,length,locate,ltrim,repeat,replace,right,rtrim,soundex,space,substring,ucase" : "ascii,char,concat,difference,insert,lcase,length,ltrim,repeat,right,rtrim,soundex,space,substring,ucase";
    }

    public ResultSet getTablePrivileges(String str, String str2, String str3) throws SQLException {
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_table_privileges ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        jtdsResultSet.setColLabel(2, "TABLE_SCHEM");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getTables(String str, String str2, String str3, String[] strArr) throws SQLException {
        String[] strArr2 = {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"};
        int[] iArr = {12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_tables ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        if (strArr == null) {
            prepareCall.setString(4, null);
        } else {
            StringBuilder sb = new StringBuilder(64);
            sb.append('\"');
            for (String append : strArr) {
                sb.append('\'');
                sb.append(append);
                sb.append("',");
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 1);
            }
            sb.append('\"');
            prepareCall.setString(4, sb.toString());
        }
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr2, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            cachedResultSet.insertRow();
        }
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return cachedResultSet;
    }

    public ResultSet getTableTypes() throws SQLException {
        return this.connection.createStatement().executeQuery("select 'SYSTEM TABLE' TABLE_TYPE union select 'TABLE' TABLE_TYPE union select 'VIEW' TABLE_TYPE order by TABLE_TYPE");
    }

    public ResultSet getTypeInfo() throws SQLException {
        Statement createStatement = this.connection.createStatement();
        try {
            JtdsResultSet jtdsResultSet = (JtdsResultSet) createStatement.executeQuery("exec sp_datatype_info @ODBCVer=3");
            try {
                return createTypeInfoResultSet(jtdsResultSet, this.connection.getUseLOBs());
            } finally {
                jtdsResultSet.close();
            }
        } catch (SQLException e) {
            createStatement.close();
            throw e;
        }
    }

    public ResultSet getUDTs(String str, String str2, String str3, int[] iArr) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "CLASS_NAME", "DATA_TYPE", "REMARKS", "BASE_TYPE"}, new int[]{12, 12, 12, 12, 4, 12, 5});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public String getURL() throws SQLException {
        return this.connection.getURL();
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getUserName() throws java.sql.SQLException {
        /*
            r5 = this;
            r0 = 0
            net.sourceforge.jtds.jdbc.JtdsConnection r1 = r5.connection     // Catch:{ all -> 0x0043 }
            java.sql.Statement r1 = r1.createStatement()     // Catch:{ all -> 0x0043 }
            net.sourceforge.jtds.jdbc.JtdsConnection r2 = r5.connection     // Catch:{ all -> 0x0041 }
            int r2 = r2.getServerType()     // Catch:{ all -> 0x0041 }
            r3 = 2
            if (r2 != r3) goto L_0x0017
            java.lang.String r2 = "select suser_name()"
            java.sql.ResultSet r0 = r1.executeQuery(r2)     // Catch:{ all -> 0x0041 }
            goto L_0x001d
        L_0x0017:
            java.lang.String r2 = "select system_user"
            java.sql.ResultSet r0 = r1.executeQuery(r2)     // Catch:{ all -> 0x0041 }
        L_0x001d:
            boolean r2 = r0.next()     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x0033
            r2 = 1
            java.lang.String r2 = r0.getString(r2)     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x002d
            r0.close()
        L_0x002d:
            if (r1 == 0) goto L_0x0032
            r1.close()
        L_0x0032:
            return r2
        L_0x0033:
            java.sql.SQLException r2 = new java.sql.SQLException     // Catch:{ all -> 0x0041 }
            java.lang.String r3 = "error.dbmeta.nouser"
            java.lang.String r3 = net.sourceforge.jtds.jdbc.Messages.get(r3)     // Catch:{ all -> 0x0041 }
            java.lang.String r4 = "HY000"
            r2.<init>(r3, r4)     // Catch:{ all -> 0x0041 }
            throw r2     // Catch:{ all -> 0x0041 }
        L_0x0041:
            r2 = move-exception
            goto L_0x0045
        L_0x0043:
            r2 = move-exception
            r1 = r0
        L_0x0045:
            if (r0 == 0) goto L_0x004a
            r0.close()
        L_0x004a:
            if (r1 == 0) goto L_0x004f
            r1.close()
        L_0x004f:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsDatabaseMetaData.getUserName():java.lang.String");
    }

    public ResultSet getVersionColumns(String str, String str2, String str3) throws SQLException {
        String[] strArr = {"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] iArr = {5, 12, 4, 12, 4, 4, 5, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_special_columns ?, ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, "V");
        prepareCall.setString(5, "C");
        prepareCall.setString(6, "O");
        prepareCall.setInt(7, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            cachedResultSet.insertRow();
        }
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return cachedResultSet;
    }

    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return !this.caseSensitive.booleanValue();
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return !this.caseSensitive.booleanValue();
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        if (this.connection.getServerType() != 2 || getDatabaseMajorVersion() >= 12) {
            return true;
        }
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue();
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return this.connection.getServerType() == 2;
    }

    public boolean supportsResultSetConcurrency(int i, int i2) throws SQLException {
        boolean z = false;
        if (!supportsResultSetType(i)) {
            return false;
        }
        if (i2 >= 1007 && i2 <= 1010 && (i != 1004 || i2 == 1007)) {
            z = true;
        }
        return z;
    }

    private void setCaseSensitiveFlag() throws SQLException {
        if (this.caseSensitive == null) {
            Statement createStatement = this.connection.createStatement();
            ResultSet executeQuery = createStatement.executeQuery("sp_server_info 16");
            executeQuery.next();
            this.caseSensitive = "MIXED".equalsIgnoreCase(executeQuery.getString(3)) ? Boolean.FALSE : Boolean.TRUE;
            createStatement.close();
        }
    }

    public ResultSet getAttributes(String str, String str2, String str3, String str4) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "ATTR_NAME", "DATA_TYPE", "ATTR_TYPE_NAME", "ATTR_SIZE", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "ATTR_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE"}, new int[]{12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return this.connection.getDatabaseMajorVersion();
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return this.connection.getDatabaseMinorVersion();
    }

    public ResultSet getSuperTables(String str, String str2, String str3) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME"}, new int[]{12, 12, 12, 12});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getSuperTypes(String str, String str2, String str3) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SUPERTYPE_CAT", "SUPERTYPE_SCHEM", "SUPERTYPE_NAME"}, new int[]{12, 12, 12, 12, 12, 12});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    private static String processEscapes(String str) {
        if (str == null || str.indexOf(92) == -1) {
            return str;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length + 10);
        int i = 0;
        while (i < length) {
            if (str.charAt(i) != '\\') {
                sb.append(str.charAt(i));
            } else if (i < length - 1) {
                sb.append('[');
                i++;
                sb.append(str.charAt(i));
                sb.append(']');
            }
            i++;
        }
        return sb.toString();
    }

    private String syscall(String str, String str2) {
        StringBuilder sb = new StringBuilder(str2.length() + 30);
        sb.append("{call ");
        if (str != null) {
            if (this.tdsVersion >= 3) {
                sb.append('[');
                sb.append(str);
                sb.append(']');
            } else {
                sb.append(str);
            }
            sb.append("..");
        }
        sb.append(str2);
        sb.append('}');
        return sb.toString();
    }

    private static void upperCaseColumnNames(JtdsResultSet jtdsResultSet) throws SQLException {
        ResultSetMetaData metaData = jtdsResultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            if (columnLabel != null && columnLabel.length() > 0) {
                jtdsResultSet.setColLabel(i, columnLabel.toUpperCase());
            }
        }
    }

    private static CachedResultSet createTypeInfoResultSet(JtdsResultSet jtdsResultSet, boolean z) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet(jtdsResultSet, false);
        if (cachedResultSet.getMetaData().getColumnCount() > 18) {
            cachedResultSet.setColumnCount(18);
        }
        cachedResultSet.setColLabel(3, "PRECISION");
        cachedResultSet.setColLabel(11, "FIXED_PREC_SCALE");
        upperCaseColumnNames(cachedResultSet);
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_TEXT);
        cachedResultSet.moveToInsertRow();
        for (TypeInfo update : getSortedTypes(jtdsResultSet, z)) {
            update.update(cachedResultSet);
            cachedResultSet.insertRow();
        }
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    private static Collection getSortedTypes(ResultSet resultSet, boolean z) throws SQLException {
        ArrayList arrayList = new ArrayList(40);
        while (resultSet.next()) {
            arrayList.add(new TypeInfo(resultSet, z));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new AbstractMethodError();
    }

    public ResultSet getClientInfoProperties() throws SQLException {
        throw new AbstractMethodError();
    }

    public ResultSet getFunctionColumns(String str, String str2, String str3, String str4) throws SQLException {
        throw new AbstractMethodError();
    }

    public ResultSet getFunctions(String str, String str2, String str3) throws SQLException {
        throw new AbstractMethodError();
    }

    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new AbstractMethodError();
    }

    public ResultSet getSchemas(String str, String str2) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public ResultSet getPseudoColumns(String str, String str2, String str3, String str4) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new AbstractMethodError();
    }
}
