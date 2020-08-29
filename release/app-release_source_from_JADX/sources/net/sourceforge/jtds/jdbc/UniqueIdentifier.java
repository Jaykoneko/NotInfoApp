package net.sourceforge.jtds.jdbc;

public class UniqueIdentifier {
    private final byte[] bytes;

    public UniqueIdentifier(byte[] bArr) {
        this.bytes = bArr;
    }

    public byte[] getBytes() {
        return (byte[]) this.bytes.clone();
    }

    public String toString() {
        byte[] bArr = this.bytes;
        if (bArr.length == 16) {
            byte[] bArr2 = new byte[bArr.length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            byte[] bArr3 = this.bytes;
            bArr2[0] = bArr3[3];
            bArr2[1] = bArr3[2];
            bArr2[2] = bArr3[1];
            bArr2[3] = bArr3[0];
            bArr2[4] = bArr3[5];
            bArr2[5] = bArr3[4];
            bArr2[6] = bArr3[7];
            bArr2[7] = bArr3[6];
            bArr = bArr2;
        }
        byte[] bArr4 = new byte[1];
        StringBuilder sb = new StringBuilder(36);
        for (int i = 0; i < this.bytes.length; i++) {
            bArr4[0] = bArr[i];
            sb.append(Support.toHex(bArr4));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                sb.append('-');
            }
        }
        return sb.toString();
    }
}
