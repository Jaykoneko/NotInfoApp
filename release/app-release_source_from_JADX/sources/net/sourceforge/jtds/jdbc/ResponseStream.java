package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import net.sourceforge.jtds.util.Logger;

public class ResponseStream {
    private final VirtualSocket _VirtualSocket;
    private byte[] buffer;
    private int bufferLen;
    private int bufferPtr;
    private final byte[] byteBuffer = new byte[255];
    private final char[] charBuffer = new char[255];
    private boolean isClosed;
    private final SharedSocket socket;

    private static class TdsInputStream extends InputStream {
        int maxLen;
        ResponseStream tds;

        public TdsInputStream(ResponseStream responseStream, int i) {
            this.tds = responseStream;
            this.maxLen = i;
        }

        public int read() throws IOException {
            int i = this.maxLen;
            this.maxLen = i - 1;
            if (i > 0) {
                return this.tds.read();
            }
            return -1;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3 = this.maxLen;
            if (i3 < 1) {
                return -1;
            }
            int min = Math.min(i3, i2);
            if (min > 0) {
                min = this.tds.read(bArr, i, min);
                this.maxLen -= min == -1 ? 0 : min;
            }
            return min;
        }
    }

    ResponseStream(SharedSocket sharedSocket, VirtualSocket virtualSocket, int i) {
        this._VirtualSocket = virtualSocket;
        this.socket = sharedSocket;
        this.buffer = new byte[i];
        this.bufferLen = i;
        this.bufferPtr = i;
    }

    /* access modifiers changed from: 0000 */
    public VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    /* access modifiers changed from: 0000 */
    public int peek() throws IOException {
        int read = read();
        this.bufferPtr--;
        return read;
    }

    /* access modifiers changed from: 0000 */
    public int read() throws IOException {
        if (this.bufferPtr >= this.bufferLen) {
            getPacket();
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPtr;
        this.bufferPtr = i + 1;
        return bArr[i] & 255;
    }

    /* access modifiers changed from: 0000 */
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    /* access modifiers changed from: 0000 */
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = i2;
        while (i3 > 0) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            int i4 = this.bufferLen - this.bufferPtr;
            if (i4 > i3) {
                i4 = i3;
            }
            System.arraycopy(this.buffer, this.bufferPtr, bArr, i, i4);
            i += i4;
            i3 -= i4;
            this.bufferPtr += i4;
        }
        return i2;
    }

    /* access modifiers changed from: 0000 */
    public int read(char[] cArr) throws IOException {
        for (int i = 0; i < cArr.length; i++) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr = this.buffer;
            int i2 = this.bufferPtr;
            int i3 = i2 + 1;
            this.bufferPtr = i3;
            byte b = bArr[i2] & 255;
            if (i3 >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr2 = this.buffer;
            int i4 = this.bufferPtr;
            this.bufferPtr = i4 + 1;
            cArr[i] = (char) (b | (bArr2[i4] << 8));
        }
        return cArr.length;
    }

    /* access modifiers changed from: 0000 */
    public String readString(int i) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            return readUnicodeString(i);
        }
        return readNonUnicodeString(i);
    }

    /* access modifiers changed from: 0000 */
    public void skipString(int i) throws IOException {
        if (i > 0) {
            if (this.socket.getTdsVersion() >= 3) {
                skip(i * 2);
            } else {
                skip(i);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public String readUnicodeString(int i) throws IOException {
        char[] cArr = this.charBuffer;
        if (i > cArr.length) {
            cArr = new char[i];
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr = this.buffer;
            int i3 = this.bufferPtr;
            int i4 = i3 + 1;
            this.bufferPtr = i4;
            byte b = bArr[i3] & 255;
            if (i4 >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr2 = this.buffer;
            int i5 = this.bufferPtr;
            this.bufferPtr = i5 + 1;
            cArr[i2] = (char) (b | (bArr2[i5] << 8));
        }
        return new String(cArr, 0, i);
    }

    /* access modifiers changed from: 0000 */
    public String readNonUnicodeString(int i) throws IOException {
        return readString(i, this.socket.getCharsetInfo());
    }

    /* access modifiers changed from: 0000 */
    public String readNonUnicodeString(int i, CharsetInfo charsetInfo) throws IOException {
        return readString(i, charsetInfo);
    }

    /* access modifiers changed from: 0000 */
    public String readString(int i, CharsetInfo charsetInfo) throws IOException {
        String charset = charsetInfo.getCharset();
        byte[] bArr = this.byteBuffer;
        if (i > bArr.length) {
            bArr = new byte[i];
        }
        read(bArr, 0, i);
        try {
            return new String(bArr, 0, i, charset);
        } catch (UnsupportedEncodingException unused) {
            return new String(bArr, 0, i);
        }
    }

    /* access modifiers changed from: 0000 */
    public short readShort() throws IOException {
        return (short) (read() | (read() << 8));
    }

    /* access modifiers changed from: 0000 */
    public int readInt() throws IOException {
        return read() | (read() << 8) | (read() << 16) | (read() << 24);
    }

    /* access modifiers changed from: 0000 */
    public long readLong() throws IOException {
        long read = ((long) read()) << 16;
        long read2 = ((long) read()) << 24;
        long read3 = ((long) read()) << 32;
        long read4 = ((long) read()) << 40;
        long read5 = ((long) read()) << 48;
        return ((long) read()) | (((long) read()) << 8) | read | read2 | read3 | read4 | read5 | (((long) read()) << 56);
    }

    /* access modifiers changed from: 0000 */
    public BigDecimal readUnsignedLong() throws IOException {
        long read = ((long) read()) << 16;
        long read2 = ((long) read()) << 24;
        long read3 = ((long) read()) << 32;
        long read4 = ((long) read()) << 40;
        return new BigDecimal(Long.toString(((long) read()) | (((long) read()) << 8) | read | read2 | read3 | read4 | (((long) read()) << 48))).multiply(new BigDecimal(256)).add(new BigDecimal(read() & 255));
    }

    /* access modifiers changed from: 0000 */
    public int skip(int i) throws IOException {
        int i2 = i;
        while (i2 > 0) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            int i3 = this.bufferLen;
            int i4 = this.bufferPtr;
            int i5 = i3 - i4;
            if (i2 > i5) {
                i2 -= i5;
                this.bufferPtr = i3;
            } else {
                this.bufferPtr = i4 + i2;
                i2 = 0;
            }
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public void skipToEnd() {
        try {
            this.bufferPtr = this.bufferLen;
            while (true) {
                this.buffer = this.socket.getNetPacket(this._VirtualSocket, this.buffer);
            }
        } catch (IOException unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public void close() {
        this.isClosed = true;
        this.socket.closeStream(this._VirtualSocket);
    }

    /* access modifiers changed from: 0000 */
    public int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    /* access modifiers changed from: 0000 */
    public int getServerType() {
        return this.socket.serverType;
    }

    /* access modifiers changed from: 0000 */
    public InputStream getInputStream(int i) {
        return new TdsInputStream(this, i);
    }

    private void getPacket() throws IOException {
        while (this.bufferPtr >= this.bufferLen) {
            if (!this.isClosed) {
                byte[] netPacket = this.socket.getNetPacket(this._VirtualSocket, this.buffer);
                this.buffer = netPacket;
                this.bufferLen = (netPacket[3] & 255) | ((netPacket[2] & 255) << 8);
                this.bufferPtr = 8;
                if (Logger.isActive()) {
                    Logger.logPacket(this._VirtualSocket.f121id, true, this.buffer);
                }
            } else {
                throw new IOException("ResponseStream is closed");
            }
        }
    }
}
