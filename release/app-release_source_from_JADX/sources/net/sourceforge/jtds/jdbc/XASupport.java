package net.sourceforge.jtds.jdbc;

import androidx.core.provider.FontsContractCompat.FontRequestCallback;
import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbcx.JtdsXid;
import net.sourceforge.jtds.util.Logger;

public class XASupport {
    private static final String TM_ID = "TM=JTDS,RmRecoveryGuid=434CDE1A-F747-4942-9584-04937455CAB4";
    private static final int XA_CLOSE = 2;
    private static final int XA_COMMIT = 7;
    private static final int XA_COMPLETE = 10;
    private static final int XA_END = 4;
    private static final int XA_FORGET = 9;
    private static final int XA_OPEN = 1;
    private static final int XA_PREPARE = 6;
    private static final int XA_RECOVER = 8;
    private static final int XA_RMID = 1;
    private static final int XA_ROLLBACK = 5;
    private static final int XA_START = 3;
    private static final int XA_TRACE = 0;

    public static int xa_open(Connection connection) throws SQLException {
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        String str = "HY000";
        if (jtdsConnection.isXaEmulation()) {
            Logger.println("xa_open: emulating distributed transaction support");
            if (jtdsConnection.getXid() == null) {
                jtdsConnection.setXaState(1);
                return 0;
            }
            throw new SQLException(Messages.get("error.xasupport.activetran", (Object) "xa_open"), str);
        } else if (jtdsConnection.getServerType() != 1 || jtdsConnection.getTdsVersion() < 4) {
            throw new SQLException(Messages.get("error.xasupport.nodist"), str);
        } else {
            Logger.println("xa_open: Using SQL2000 MSDTC to support distributed transactions");
            int[] iArr = new int[5];
            iArr[1] = 1;
            iArr[2] = 0;
            iArr[3] = 1;
            iArr[4] = 0;
            byte[][] sendXaPacket = jtdsConnection.sendXaPacket(iArr, TM_ID.getBytes());
            if (iArr[0] != 0 || sendXaPacket == null || sendXaPacket[0] == null || sendXaPacket[0].length != 4) {
                throw new SQLException(Messages.get("error.xasupport.badopen"), str);
            }
            return ((sendXaPacket[0][3] & 255) << 24) | (sendXaPacket[0][0] & 255) | ((sendXaPacket[0][1] & 255) << 8) | ((sendXaPacket[0][2] & 255) << TdsCore.MSLOGIN_PKT);
        }
    }

    public static void xa_close(Connection connection, int i) throws SQLException {
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            jtdsConnection.setXaState(0);
            if (jtdsConnection.getXid() != null) {
                jtdsConnection.setXid(null);
                try {
                    jtdsConnection.rollback();
                } catch (SQLException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("xa_close: rollback() returned ");
                    sb.append(e);
                    Logger.println(sb.toString());
                }
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("xa_close: setAutoCommit() returned ");
                    sb2.append(e2);
                    Logger.println(sb2.toString());
                }
                throw new SQLException(Messages.get("error.xasupport.activetran", (Object) "xa_close"), "HY000");
            }
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 2;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        jtdsConnection.sendXaPacket(iArr, TM_ID.getBytes());
    }

    public static void xa_start(Connection connection, int i, Xid xid, int i2) throws XAException {
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = new JtdsXid(xid);
            if (jtdsConnection.getXaState() == 0) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid2 = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid2 != null) {
                if (jtdsXid2.equals(jtdsXid)) {
                    raiseXAException(-8);
                } else {
                    raiseXAException(-6);
                }
            }
            if (i2 != 0) {
                raiseXAException(-5);
            }
            try {
                connection.setAutoCommit(false);
            } catch (SQLException unused) {
                raiseXAException(-3);
            }
            jtdsConnection.setXid(jtdsXid);
            jtdsConnection.setXaState(3);
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 3;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = i2;
        try {
            byte[][] sendXaPacket = ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
            if (iArr[0] == 0 && sendXaPacket != null) {
                ((JtdsConnection) connection).enlistConnection(sendXaPacket[0]);
            }
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
    }

    public static void xa_end(Connection connection, int i, Xid xid, int i2) throws XAException {
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = new JtdsXid(xid);
            if (jtdsConnection.getXaState() != 3) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid2 = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid2 == null || !jtdsXid2.equals(jtdsXid)) {
                raiseXAException(-4);
            }
            if (!(i2 == 67108864 || i2 == 536870912)) {
                raiseXAException(-5);
            }
            jtdsConnection.setXaState(4);
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 4;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = i2;
        try {
            ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
            ((JtdsConnection) connection).enlistConnection(null);
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
    }

    public static int xa_prepare(Connection connection, int i, Xid xid) throws XAException {
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = new JtdsXid(xid);
            if (jtdsConnection.getXaState() != 4) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid2 = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid2 == null || !jtdsXid2.equals(jtdsXid)) {
                raiseXAException(-4);
            }
            jtdsConnection.setXaState(6);
            Logger.println("xa_prepare: Warning: Two phase commit not available in XA emulation mode.");
            return 0;
        }
        int[] iArr = new int[5];
        iArr[1] = 6;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (!(iArr[0] == 0 || iArr[0] == 3)) {
            raiseXAException(iArr[0]);
        }
        return iArr[0];
    }

    public static void xa_commit(Connection connection, int i, Xid xid, boolean z) throws XAException {
        StringBuilder sb;
        String str = "xa_close: setAutoCommit() returned ";
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid == null) {
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException unused) {
                    raiseXAException(-3);
                }
            } else {
                if (!(jtdsConnection.getXaState() == 4 || jtdsConnection.getXaState() == 6)) {
                    raiseXAException(-6);
                }
                if (!jtdsXid.equals(new JtdsXid(xid))) {
                    raiseXAException(-4);
                }
            }
            jtdsConnection.setXid(null);
            try {
                jtdsConnection.commit();
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e) {
                    e = e;
                    sb = new StringBuilder();
                }
            } catch (SQLException e2) {
                raiseXAException(e2);
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e3) {
                    e = e3;
                    sb = new StringBuilder();
                }
            } catch (Throwable th) {
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e4) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(e4);
                    Logger.println(sb2.toString());
                }
                jtdsConnection.setXaState(1);
                throw th;
            }
            jtdsConnection.setXaState(1);
        }
        int[] iArr = new int[5];
        iArr[1] = 7;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = z ? 1073741824 : 0;
        try {
            ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e5) {
            raiseXAException(e5);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
        return;
        sb.append(str);
        sb.append(e);
        Logger.println(sb.toString());
        jtdsConnection.setXaState(1);
    }

    public static void xa_rollback(Connection connection, int i, Xid xid) throws XAException {
        StringBuilder sb;
        String str = "xa_close: setAutoCommit() returned ";
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = new JtdsXid(xid);
            if (!(jtdsConnection.getXaState() == 4 || jtdsConnection.getXaState() == 6)) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid2 = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid2 == null || !jtdsXid2.equals(jtdsXid)) {
                raiseXAException(-4);
            }
            jtdsConnection.setXid(null);
            try {
                jtdsConnection.rollback();
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e) {
                    e = e;
                    sb = new StringBuilder();
                }
            } catch (SQLException e2) {
                raiseXAException(e2);
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e3) {
                    e = e3;
                    sb = new StringBuilder();
                }
            } catch (Throwable th) {
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e4) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(e4);
                    Logger.println(sb2.toString());
                }
                jtdsConnection.setXaState(1);
                throw th;
            }
            jtdsConnection.setXaState(1);
        }
        int[] iArr = new int[5];
        iArr[1] = 5;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e5) {
            raiseXAException(e5);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
        return;
        sb.append(str);
        sb.append(e);
        Logger.println(sb.toString());
        jtdsConnection.setXaState(1);
    }

    public static Xid[] xa_recover(Connection connection, int i, int i2) throws XAException {
        if (((JtdsConnection) connection).isXaEmulation()) {
            if (!(i2 == 16777216 || i2 == 8388608 || i2 == 25165824 || i2 == 0)) {
                raiseXAException(-5);
            }
            return new JtdsXid[0];
        }
        int[] iArr = new int[5];
        iArr[1] = 8;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        if (i2 != 16777216) {
            return new JtdsXid[0];
        }
        JtdsXid[] jtdsXidArr = null;
        try {
            byte[][] sendXaPacket = ((JtdsConnection) connection).sendXaPacket(iArr, null);
            if (iArr[0] >= 0) {
                int length = sendXaPacket.length;
                jtdsXidArr = new JtdsXid[length];
                for (int i3 = 0; i3 < length; i3++) {
                    jtdsXidArr[i3] = new JtdsXid(sendXaPacket[i3], 0);
                }
            }
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (iArr[0] < 0) {
            raiseXAException(iArr[0]);
        }
        if (jtdsXidArr == null) {
            jtdsXidArr = new JtdsXid[0];
        }
        return jtdsXidArr;
    }

    public static void xa_forget(Connection connection, int i, Xid xid) throws XAException {
        StringBuilder sb;
        String str = "xa_close: setAutoCommit() returned ";
        JtdsConnection jtdsConnection = (JtdsConnection) connection;
        if (jtdsConnection.isXaEmulation()) {
            JtdsXid jtdsXid = new JtdsXid(xid);
            JtdsXid jtdsXid2 = (JtdsXid) jtdsConnection.getXid();
            if (jtdsXid2 != null && !jtdsXid2.equals(jtdsXid)) {
                raiseXAException(-4);
            }
            if (!(jtdsConnection.getXaState() == 4 || jtdsConnection.getXaState() == 6)) {
                raiseXAException(-6);
            }
            jtdsConnection.setXid(null);
            try {
                jtdsConnection.rollback();
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e) {
                    e = e;
                    sb = new StringBuilder();
                }
            } catch (SQLException e2) {
                raiseXAException(e2);
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e3) {
                    e = e3;
                    sb = new StringBuilder();
                }
            } catch (Throwable th) {
                try {
                    jtdsConnection.setAutoCommit(true);
                } catch (SQLException e4) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(e4);
                    Logger.println(sb2.toString());
                }
                jtdsConnection.setXaState(1);
                throw th;
            }
            jtdsConnection.setXaState(1);
        }
        int[] iArr = new int[5];
        iArr[1] = 9;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((JtdsConnection) connection).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e5) {
            raiseXAException(e5);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
        return;
        sb.append(str);
        sb.append(e);
        Logger.println(sb.toString());
        jtdsConnection.setXaState(1);
    }

    public static void raiseXAException(SQLException sQLException) throws XAException {
        XAException xAException = new XAException(sQLException.getMessage());
        xAException.errorCode = -7;
        StringBuilder sb = new StringBuilder();
        sb.append("XAException: ");
        sb.append(xAException.getMessage());
        Logger.println(sb.toString());
        throw xAException;
    }

    public static void raiseXAException(int i) throws XAException {
        String str;
        switch (i) {
            case -9:
                str = "xaeroutside";
                break;
            case -8:
                str = "xaerdupid";
                break;
            case -7:
                str = "xaerrmfail";
                break;
            case -6:
                str = "xaerproto";
                break;
            case -5:
                str = "xaerinval";
                break;
            case FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /*-4*/:
                str = "xaernota";
                break;
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                str = "xaerrmerr";
                break;
            case -2:
                str = "xaerasync";
                break;
            default:
                switch (i) {
                    case 3:
                        str = "xardonly";
                        break;
                    case 4:
                        str = "xaretry";
                        break;
                    case 5:
                        str = "xaheurmix";
                        break;
                    case 6:
                        str = "xaheurrb";
                        break;
                    case 7:
                        str = "xaheurcom";
                        break;
                    case 8:
                        str = "xaheurhaz";
                        break;
                    case 9:
                        str = "xanomigrate";
                        break;
                    default:
                        switch (i) {
                            case 100:
                                str = "xarbrollback";
                                break;
                            case 101:
                                str = "xarbcommfail";
                                break;
                            case 102:
                                str = "xarbdeadlock";
                                break;
                            case 103:
                                str = "xarbintegrity";
                                break;
                            case 104:
                                str = "xarbother";
                                break;
                            case 105:
                                str = "xarbproto";
                                break;
                            case 106:
                                str = "xarbtimeout";
                                break;
                            case 107:
                                str = "xarbtransient";
                                break;
                            default:
                                str = "xaerunknown";
                                break;
                        }
                }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("error.xaexception.");
        sb.append(str);
        XAException xAException = new XAException(Messages.get(sb.toString()));
        xAException.errorCode = i;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("XAException: ");
        sb2.append(xAException.getMessage());
        Logger.println(sb2.toString());
        throw xAException;
    }

    private static byte[] toBytesXid(Xid xid) {
        byte[] bArr = new byte[(xid.getGlobalTransactionId().length + 12 + xid.getBranchQualifier().length)];
        int formatId = xid.getFormatId();
        bArr[0] = (byte) formatId;
        bArr[1] = (byte) (formatId >> 8);
        bArr[2] = (byte) (formatId >> 16);
        bArr[3] = (byte) (formatId >> 24);
        bArr[4] = (byte) xid.getGlobalTransactionId().length;
        bArr[8] = (byte) xid.getBranchQualifier().length;
        System.arraycopy(xid.getGlobalTransactionId(), 0, bArr, 12, bArr[4]);
        System.arraycopy(xid.getBranchQualifier(), 0, bArr, bArr[4] + 12, bArr[8]);
        return bArr;
    }

    private XASupport() {
    }
}
