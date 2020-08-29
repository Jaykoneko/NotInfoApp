package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import net.sourceforge.jtds.util.Logger;

public class RequestStream {
    private final VirtualSocket _VirtualSocket;
    private byte[] buffer;
    private int bufferPtr = 8;
    private final int bufferSize;
    private boolean isClosed;
    private final int maxPrecision;
    private byte pktType;
    private final SharedSocket socket;

    RequestStream(SharedSocket sharedSocket, VirtualSocket virtualSocket, int i, int i2) {
        this._VirtualSocket = virtualSocket;
        this.socket = sharedSocket;
        this.bufferSize = i;
        this.buffer = new byte[i];
        this.maxPrecision = i2;
    }

    /* access modifiers changed from: 0000 */
    public void setBufferSize(int i) {
        int i2 = this.bufferPtr;
        if (i >= i2 && i != this.bufferSize) {
            if (i < 512 || i > 32768) {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid buffer size parameter ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            byte[] bArr = new byte[i];
            System.arraycopy(this.buffer, 0, bArr, 0, i2);
            this.buffer = bArr;
        }
    }

    /* access modifiers changed from: 0000 */
    public int getBufferSize() {
        return this.bufferSize;
    }

    /* access modifiers changed from: 0000 */
    public int getMaxPrecision() {
        return this.maxPrecision;
    }

    /* access modifiers changed from: 0000 */
    public byte getMaxDecimalBytes() {
        return (byte) (this.maxPrecision <= 28 ? 13 : 17);
    }

    /* access modifiers changed from: 0000 */
    public VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    /* access modifiers changed from: 0000 */
    public void setPacketType(byte b) {
        this.pktType = b;
    }

    /* access modifiers changed from: 0000 */
    public void write(byte b) throws IOException {
        if (this.bufferPtr == this.buffer.length) {
            putPacket(0);
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPtr;
        this.bufferPtr = i + 1;
        bArr[i] = b;
    }

    /* access modifiers changed from: 0000 */
    public void write(byte[] bArr) throws IOException {
        int length = bArr.length;
        int i = 0;
        while (length > 0) {
            int length2 = this.buffer.length - this.bufferPtr;
            if (length2 == 0) {
                putPacket(0);
            } else {
                if (length2 > length) {
                    length2 = length;
                }
                System.arraycopy(bArr, i, this.buffer, this.bufferPtr, length2);
                i += length2;
                this.bufferPtr += length2;
                length -= length2;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void write(byte[] bArr, int i, int i2) throws IOException {
        int i3 = i + i2;
        if (i3 > bArr.length) {
            i3 = bArr.length;
        }
        int i4 = i3 - i;
        while (i4 > 0) {
            int length = this.buffer.length - this.bufferPtr;
            if (length == 0) {
                putPacket(0);
            } else {
                if (length > i4) {
                    length = i4;
                }
                System.arraycopy(bArr, i, this.buffer, this.bufferPtr, length);
                i += length;
                this.bufferPtr += length;
                i4 -= length;
            }
        }
        for (int i5 = i2 - i4; i5 > 0; i5--) {
            write(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void write(int i) throws IOException {
        write((byte) i);
        write((byte) (i >> 8));
        write((byte) (i >> 16));
        write((byte) (i >> 24));
    }

    /* access modifiers changed from: 0000 */
    public void write(short s) throws IOException {
        write((byte) s);
        write((byte) (s >> 8));
    }

    /* access modifiers changed from: 0000 */
    public void write(long j) throws IOException {
        write((byte) ((int) j));
        write((byte) ((int) (j >> 8)));
        write((byte) ((int) (j >> 16)));
        write((byte) ((int) (j >> 24)));
        write((byte) ((int) (j >> 32)));
        write((byte) ((int) (j >> 40)));
        write((byte) ((int) (j >> 48)));
        write((byte) ((int) (j >> 56)));
    }

    /* access modifiers changed from: 0000 */
    public void write(double d) throws IOException {
        long doubleToLongBits = Double.doubleToLongBits(d);
        write((byte) ((int) doubleToLongBits));
        write((byte) ((int) (doubleToLongBits >> 8)));
        write((byte) ((int) (doubleToLongBits >> 16)));
        write((byte) ((int) (doubleToLongBits >> 24)));
        write((byte) ((int) (doubleToLongBits >> 32)));
        write((byte) ((int) (doubleToLongBits >> 40)));
        write((byte) ((int) (doubleToLongBits >> 48)));
        write((byte) ((int) (doubleToLongBits >> 56)));
    }

    /* access modifiers changed from: 0000 */
    public void write(float f) throws IOException {
        int floatToIntBits = Float.floatToIntBits(f);
        write((byte) floatToIntBits);
        write((byte) (floatToIntBits >> 8));
        write((byte) (floatToIntBits >> 16));
        write((byte) (floatToIntBits >> 24));
    }

    /* access modifiers changed from: 0000 */
    public void write(String str) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if (this.bufferPtr == this.buffer.length) {
                    putPacket(0);
                }
                byte[] bArr = this.buffer;
                int i2 = this.bufferPtr;
                int i3 = i2 + 1;
                this.bufferPtr = i3;
                bArr[i2] = (byte) charAt;
                if (i3 == bArr.length) {
                    putPacket(0);
                }
                byte[] bArr2 = this.buffer;
                int i4 = this.bufferPtr;
                this.bufferPtr = i4 + 1;
                bArr2[i4] = (byte) (charAt >> 8);
            }
            return;
        }
        writeAscii(str);
    }

    /* access modifiers changed from: 0000 */
    public void write(char[] cArr, int i, int i2) throws IOException {
        int i3 = i2 + i;
        if (i3 > cArr.length) {
            i3 = cArr.length;
        }
        while (i < i3) {
            char c = cArr[i];
            if (this.bufferPtr == this.buffer.length) {
                putPacket(0);
            }
            byte[] bArr = this.buffer;
            int i4 = this.bufferPtr;
            int i5 = i4 + 1;
            this.bufferPtr = i5;
            bArr[i4] = (byte) c;
            if (i5 == bArr.length) {
                putPacket(0);
            }
            byte[] bArr2 = this.buffer;
            int i6 = this.bufferPtr;
            this.bufferPtr = i6 + 1;
            bArr2[i6] = (byte) (c >> 8);
            i++;
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeAscii(String str) throws IOException {
        String charset = this.socket.getCharset();
        if (charset != null) {
            try {
                write(str.getBytes(charset));
            } catch (UnsupportedEncodingException unused) {
                write(str.getBytes());
            }
        } else {
            write(str.getBytes());
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeStreamBytes(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[1024];
        while (i > 0) {
            int read = inputStream.read(bArr);
            if (read >= 0) {
                write(bArr, 0, read);
                i -= read;
            } else {
                throw new IOException("Data in stream less than specified by length");
            }
        }
        if (i < 0 || inputStream.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeReaderChars(Reader reader, int i) throws IOException {
        char[] cArr = new char[512];
        byte[] bArr = new byte[1024];
        while (i > 0) {
            int read = reader.read(cArr);
            if (read >= 0) {
                int i2 = -1;
                for (int i3 = 0; i3 < read; i3++) {
                    int i4 = i2 + 1;
                    bArr[i4] = (byte) cArr[i3];
                    i2 = i4 + 1;
                    bArr[i2] = (byte) (cArr[i3] >> 8);
                }
                write(bArr, 0, read * 2);
                i -= read;
            } else {
                throw new IOException("Data in stream less than specified by length");
            }
        }
        if (i < 0 || reader.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeReaderBytes(Reader reader, int i) throws IOException {
        char[] cArr = new char[1024];
        int i2 = 0;
        while (i2 < i) {
            int read = reader.read(cArr);
            if (read != -1) {
                i2 += read;
                if (i2 <= i) {
                    write(Support.encodeString(this.socket.getCharset(), new String(cArr, 0, read)));
                } else {
                    throw new IOException("More data in stream than specified by length");
                }
            } else {
                throw new IOException("Data in stream less than specified by length");
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void write(BigDecimal bigDecimal) throws IOException {
        if (bigDecimal == null) {
            write(0);
            return;
        }
        byte b = (byte) (bigDecimal.signum() < 0 ? 0 : 1);
        byte[] byteArray = bigDecimal.unscaledValue().abs().toByteArray();
        byte length = (byte) (byteArray.length + 1);
        if (length > getMaxDecimalBytes()) {
            throw new IOException("BigDecimal to big to send");
        } else if (this.socket.serverType == 2) {
            write(length);
            write((byte) (b ^ 1));
            for (byte write : byteArray) {
                write(write);
            }
        } else {
            write(length);
            write(b);
            for (int length2 = byteArray.length - 1; length2 >= 0; length2--) {
                write(byteArray[length2]);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void flush() throws IOException {
        putPacket(1);
    }

    /* access modifiers changed from: 0000 */
    public void close() {
        this.isClosed = true;
    }

    /* access modifiers changed from: 0000 */
    public int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    /* access modifiers changed from: 0000 */
    public int getServerType() {
        return this.socket.serverType;
    }

    private void putPacket(int i) throws IOException {
        if (!this.isClosed) {
            byte[] bArr = this.buffer;
            bArr[0] = this.pktType;
            int i2 = 1;
            bArr[1] = (byte) i;
            int i3 = this.bufferPtr;
            bArr[2] = (byte) (i3 >> 8);
            bArr[3] = (byte) i3;
            bArr[4] = 0;
            bArr[5] = 0;
            if (this.socket.getTdsVersion() < 3) {
                i2 = 0;
            }
            bArr[6] = (byte) i2;
            this.buffer[7] = 0;
            if (Logger.isActive()) {
                Logger.logPacket(this._VirtualSocket.f121id, false, this.buffer);
            }
            this.buffer = this.socket.sendNetPacket(this._VirtualSocket, this.buffer);
            this.bufferPtr = 8;
            return;
        }
        throw new IOException("RequestStream is closed");
    }
}
