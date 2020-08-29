package net.sourceforge.jtds.jdbcx;

import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsCore;

public class JtdsXid implements Xid {
    public static final int XID_SIZE = 140;
    private final byte[] bqual;
    public final int fmtId;
    private final byte[] gtran;
    public int hash;

    public JtdsXid(byte[] bArr, int i) {
        this.fmtId = (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << TdsCore.MSLOGIN_PKT) | ((bArr[i + 3] & 255) << 24);
        byte b = bArr[i + 4];
        byte b2 = bArr[i + 8];
        byte[] bArr2 = new byte[b];
        this.gtran = bArr2;
        this.bqual = new byte[b2];
        System.arraycopy(bArr, i + 12, bArr2, 0, b);
        System.arraycopy(bArr, b + 12 + i, this.bqual, 0, b2);
        calculateHash();
    }

    public JtdsXid(byte[] bArr, byte[] bArr2) {
        this.fmtId = 0;
        this.gtran = bArr;
        this.bqual = bArr2;
        calculateHash();
    }

    public JtdsXid(Xid xid) {
        this.fmtId = xid.getFormatId();
        this.gtran = new byte[xid.getGlobalTransactionId().length];
        byte[] globalTransactionId = xid.getGlobalTransactionId();
        byte[] bArr = this.gtran;
        System.arraycopy(globalTransactionId, 0, bArr, 0, bArr.length);
        this.bqual = new byte[xid.getBranchQualifier().length];
        byte[] branchQualifier = xid.getBranchQualifier();
        byte[] bArr2 = this.bqual;
        System.arraycopy(branchQualifier, 0, bArr2, 0, bArr2.length);
        calculateHash();
    }

    private void calculateHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(this.fmtId));
        sb.append(new String(this.gtran));
        sb.append(new String(this.bqual));
        this.hash = sb.toString().hashCode();
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JtdsXid) {
            JtdsXid jtdsXid = (JtdsXid) obj;
            if (this.gtran.length + this.bqual.length == jtdsXid.gtran.length + jtdsXid.bqual.length && this.fmtId == jtdsXid.fmtId) {
                int i = 0;
                while (true) {
                    byte[] bArr = this.gtran;
                    if (i >= bArr.length) {
                        int i2 = 0;
                        while (true) {
                            byte[] bArr2 = this.bqual;
                            if (i2 >= bArr2.length) {
                                return true;
                            }
                            if (bArr2[i2] != jtdsXid.bqual[i2]) {
                                return false;
                            }
                            i2++;
                        }
                    } else if (bArr[i] != jtdsXid.gtran[i]) {
                        return false;
                    } else {
                        i++;
                    }
                }
            }
        }
        return false;
    }

    public int getFormatId() {
        return this.fmtId;
    }

    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    public byte[] getGlobalTransactionId() {
        return this.gtran;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("XID[Format=");
        sb.append(this.fmtId);
        sb.append(", Global=0x");
        sb.append(Support.toHex(this.gtran));
        sb.append(", Branch=0x");
        sb.append(Support.toHex(this.bqual));
        sb.append(']');
        return sb.toString();
    }
}
