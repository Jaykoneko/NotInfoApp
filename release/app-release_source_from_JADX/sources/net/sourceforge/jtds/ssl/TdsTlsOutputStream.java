package net.sourceforge.jtds.ssl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.jtds.jdbc.TdsCore;

class TdsTlsOutputStream extends FilterOutputStream {
    private final List bufferedRecords = new ArrayList();
    private int totalSize;

    TdsTlsOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    private void deferRecord(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i);
        this.bufferedRecords.add(bArr2);
        this.totalSize += i;
    }

    private void flushBufferedRecords() throws IOException {
        byte[] bArr = new byte[this.totalSize];
        int i = 0;
        for (int i2 = 0; i2 < this.bufferedRecords.size(); i2++) {
            byte[] bArr2 = (byte[]) this.bufferedRecords.get(i2);
            System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
            i += bArr2.length;
        }
        putTdsPacket(bArr, i);
        this.bufferedRecords.clear();
        this.totalSize = 0;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(byte[] r6, int r7, int r8) throws java.io.IOException {
        /*
            r5 = this;
            r0 = 5
            if (r8 < r0) goto L_0x006e
            if (r7 <= 0) goto L_0x0006
            goto L_0x006e
        L_0x0006:
            r1 = 0
            byte r1 = r6[r1]
            r1 = r1 & 255(0xff, float:3.57E-43)
            r2 = 3
            byte r2 = r6[r2]
            r2 = r2 & 255(0xff, float:3.57E-43)
            r3 = 8
            int r2 = r2 << r3
            r4 = 4
            byte r4 = r6[r4]
            r4 = r4 & 255(0xff, float:3.57E-43)
            r2 = r2 | r4
            r4 = 20
            if (r1 < r4) goto L_0x006a
            r4 = 23
            if (r1 > r4) goto L_0x006a
            int r4 = r8 + -5
            if (r2 == r4) goto L_0x0026
            goto L_0x006a
        L_0x0026:
            switch(r1) {
                case 20: goto L_0x0060;
                case 21: goto L_0x0069;
                case 22: goto L_0x0030;
                case 23: goto L_0x002a;
                default: goto L_0x0029;
            }
        L_0x0029:
            goto L_0x0064
        L_0x002a:
            java.io.OutputStream r0 = r5.out
            r0.write(r6, r7, r8)
            goto L_0x0069
        L_0x0030:
            r1 = 9
            if (r8 < r1) goto L_0x0064
            byte r7 = r6[r0]
            r0 = 6
            byte r0 = r6[r0]
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 16
            int r0 = r0 << r1
            r2 = 7
            byte r2 = r6[r2]
            r2 = r2 & 255(0xff, float:3.57E-43)
            int r2 = r2 << r3
            r0 = r0 | r2
            byte r2 = r6[r3]
            r2 = r2 & 255(0xff, float:3.57E-43)
            r0 = r0 | r2
            int r2 = r8 + -9
            if (r0 != r2) goto L_0x0055
            r3 = 1
            if (r7 != r3) goto L_0x0055
            r5.putTdsPacket(r6, r8)
            goto L_0x0069
        L_0x0055:
            r5.deferRecord(r6, r8)
            if (r0 != r2) goto L_0x005c
            if (r7 == r1) goto L_0x0069
        L_0x005c:
            r5.flushBufferedRecords()
            goto L_0x0069
        L_0x0060:
            r5.deferRecord(r6, r8)
            goto L_0x0069
        L_0x0064:
            java.io.OutputStream r0 = r5.out
            r0.write(r6, r7, r8)
        L_0x0069:
            return
        L_0x006a:
            r5.putTdsPacket(r6, r8)
            return
        L_0x006e:
            java.io.OutputStream r0 = r5.out
            r0.write(r6, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.ssl.TdsTlsOutputStream.write(byte[], int, int):void");
    }

    /* access modifiers changed from: 0000 */
    public void putTdsPacket(byte[] bArr, int i) throws IOException {
        byte[] bArr2 = new byte[8];
        bArr2[0] = TdsCore.PRELOGIN_PKT;
        bArr2[1] = 1;
        int i2 = i + 8;
        bArr2[2] = (byte) (i2 >> 8);
        bArr2[3] = (byte) i2;
        this.out.write(bArr2, 0, 8);
        this.out.write(bArr, 0, i);
    }

    public void flush() throws IOException {
        super.flush();
    }
}
