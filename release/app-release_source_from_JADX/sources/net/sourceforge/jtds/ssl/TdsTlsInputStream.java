package net.sourceforge.jtds.ssl;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class TdsTlsInputStream extends FilterInputStream {
    InputStream bufferStream;
    int bytesOutstanding;
    boolean pureSSL;
    final byte[] readBuffer = new byte[6144];

    public TdsTlsInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.pureSSL && this.bufferStream == null) {
            return this.in.read(bArr, i, i2);
        }
        if (!this.pureSSL && this.bufferStream == null) {
            primeBuffer();
        }
        int read = this.bufferStream.read(bArr, i, i2);
        int i3 = this.bytesOutstanding - (read < 0 ? 0 : read);
        this.bytesOutstanding = i3;
        if (i3 == 0) {
            this.bufferStream = null;
        }
        return read;
    }

    private void primeBuffer() throws IOException {
        int i;
        readFully(this.readBuffer, 0, 5);
        byte[] bArr = this.readBuffer;
        if (bArr[0] == 4 || bArr[0] == 18) {
            byte[] bArr2 = this.readBuffer;
            byte b = ((bArr2[2] & 255) << 8) | (bArr2[3] & 255);
            readFully(bArr2, 5, 3);
            i = b - 8;
            readFully(this.readBuffer, 0, i);
        } else {
            i = ((bArr[3] & 255) << 8) | (bArr[4] & 255);
            readFully(bArr, 5, i - 5);
            this.pureSSL = true;
        }
        this.bufferStream = new ByteArrayInputStream(this.readBuffer, 0, i);
        this.bytesOutstanding = i;
    }

    private void readFully(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        while (i2 > 0) {
            i3 = this.in.read(bArr, i, i2);
            if (i3 < 0) {
                break;
            }
            i += i3;
            i2 -= i3;
        }
        if (i3 < 0) {
            throw new IOException();
        }
    }
}
