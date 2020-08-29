package net.sourceforge.jtds.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Messages;

public class BlobBuffer {
    private static final int BYTE_MASK = 1023;
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final int INVALID_PAGE = -1;
    private static final int MAX_BUF_INC = 16384;
    private static final int PAGE_MASK = -1024;
    private static final int PAGE_SIZE = 1024;
    private File blobFile;
    private byte[] buffer;
    private final File bufferDir;
    private boolean bufferDirty;
    private int currentPage;
    private boolean isMemOnly;
    private int length;
    private final int maxMemSize;
    private int openCount;
    private RandomAccessFile raFile;

    private class AsciiInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public AsciiInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            if (this.open) {
                try {
                    close();
                } catch (IOException unused) {
                } catch (Throwable th) {
                    super.finalize();
                    throw th;
                }
                super.finalize();
            }
        }

        public int available() throws IOException {
            return (((int) BlobBuffer.this.getLength()) - this.readPtr) / 2;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr);
            if (read >= 0) {
                int i = this.readPtr + 1;
                this.readPtr = i;
                int read2 = BlobBuffer.this.read(i);
                if (read2 >= 0) {
                    this.readPtr++;
                    if (read2 != 0 || read > 127) {
                        read = 63;
                    }
                    return read;
                }
            }
            return -1;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class AsciiOutputStream extends OutputStream {
        private boolean open = true;
        private int writePtr;

        AsciiOutputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.writePtr = (int) j;
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            if (this.open) {
                try {
                    close();
                } catch (IOException unused) {
                } catch (Throwable th) {
                    super.finalize();
                    throw th;
                }
                super.finalize();
            }
        }

        public void write(int i) throws IOException {
            BlobBuffer blobBuffer = BlobBuffer.this;
            int i2 = this.writePtr;
            this.writePtr = i2 + 1;
            blobBuffer.write(i2, i);
            BlobBuffer blobBuffer2 = BlobBuffer.this;
            int i3 = this.writePtr;
            this.writePtr = i3 + 1;
            blobBuffer2.write(i3, 0);
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public BlobInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            if (this.open) {
                try {
                    close();
                } catch (IOException unused) {
                } catch (Throwable th) {
                    super.finalize();
                    throw th;
                }
                super.finalize();
            }
        }

        public int available() throws IOException {
            return ((int) BlobBuffer.this.getLength()) - this.readPtr;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr);
            if (read >= 0) {
                this.readPtr++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            int read = BlobBuffer.this.read(this.readPtr, bArr, i, i2);
            if (read > 0) {
                this.readPtr += read;
            }
            return read;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobOutputStream extends OutputStream {
        private boolean open = true;
        private int writePtr;

        BlobOutputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.writePtr = (int) j;
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            if (this.open) {
                try {
                    close();
                } catch (IOException unused) {
                } catch (Throwable th) {
                    super.finalize();
                    throw th;
                }
                super.finalize();
            }
        }

        public void write(int i) throws IOException {
            BlobBuffer blobBuffer = BlobBuffer.this;
            int i2 = this.writePtr;
            this.writePtr = i2 + 1;
            blobBuffer.write(i2, i);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            BlobBuffer.this.write(this.writePtr, bArr, i, i2);
            this.writePtr += i2;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class UnicodeInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public UnicodeInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            if (this.open) {
                try {
                    close();
                } catch (IOException unused) {
                } catch (Throwable th) {
                    super.finalize();
                    throw th;
                }
                super.finalize();
            }
        }

        public int available() throws IOException {
            return ((int) BlobBuffer.this.getLength()) - this.readPtr;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr ^ 1);
            if (read >= 0) {
                this.readPtr++;
            }
            return read;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    public BlobBuffer(File file, long j) {
        if (j <= 2147483647L) {
            this.bufferDir = file;
            this.maxMemSize = (int) j;
            this.buffer = EMPTY_BUFFER;
            return;
        }
        throw new IllegalArgumentException("The maximum in-memory buffer size of a blob buffer cannot exceed 2GB");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        File file;
        try {
            if (this.raFile != null) {
                this.raFile.close();
            }
            file = this.blobFile;
            if (file == null) {
                return;
            }
        } catch (IOException unused) {
            file = this.blobFile;
            if (file == null) {
                return;
            }
        } catch (Throwable th) {
            File file2 = this.blobFile;
            if (file2 != null) {
                file2.delete();
            }
            throw th;
        }
        file.delete();
    }

    public void createBlobFile() {
        File file = this.bufferDir;
        if (file == null) {
            this.isMemOnly = true;
            return;
        }
        try {
            this.blobFile = File.createTempFile("jtds", ".tmp", file);
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.blobFile, "rw");
            this.raFile = randomAccessFile;
            if (this.length > 0) {
                randomAccessFile.write(this.buffer, 0, this.length);
            }
            this.buffer = new byte[1024];
            this.currentPage = -1;
            this.openCount = 0;
        } catch (SecurityException e) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("SecurityException creating BLOB file:");
            Logger.logException(e);
        } catch (IOException e2) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("IOException creating BLOB file:");
            Logger.logException(e2);
        }
    }

    public void open() throws IOException {
        if (this.raFile != null || this.blobFile == null) {
            if (this.raFile != null) {
                this.openCount++;
            }
            return;
        }
        this.raFile = new RandomAccessFile(this.blobFile, "rw");
        this.openCount = 1;
        this.currentPage = -1;
        this.buffer = new byte[1024];
    }

    public int read(int i) throws IOException {
        byte b;
        if (i >= this.length) {
            return -1;
        }
        if (this.raFile != null) {
            if (this.currentPage != (i & PAGE_MASK)) {
                readPage(i);
            }
            b = this.buffer[i & BYTE_MASK];
        } else {
            b = this.buffer[i];
        }
        return b & 255;
    }

    public int read(int i, byte[] bArr, int i2, int i3) throws IOException {
        int i4;
        if (bArr != null) {
            if (i2 >= 0 && i2 <= bArr.length && i3 >= 0) {
                int i5 = i2 + i3;
                if (i5 <= bArr.length && i5 >= 0) {
                    if (i3 == 0) {
                        return 0;
                    }
                    int i6 = this.length;
                    if (i >= i6) {
                        return -1;
                    }
                    if (this.raFile != null) {
                        i4 = Math.min(i6 - i, i3);
                        if (i4 >= 1024) {
                            if (this.bufferDirty) {
                                writePage(this.currentPage);
                            }
                            this.currentPage = -1;
                            this.raFile.seek((long) i);
                            this.raFile.readFully(bArr, i2, i4);
                        } else {
                            int i7 = i4;
                            while (i7 > 0) {
                                if (this.currentPage != (i & PAGE_MASK)) {
                                    readPage(i);
                                }
                                int i8 = i & BYTE_MASK;
                                int min = Math.min(1024 - i8, i7);
                                System.arraycopy(this.buffer, i8, bArr, i2, min);
                                i2 += min;
                                i += min;
                                i7 -= min;
                            }
                        }
                    } else {
                        i4 = Math.min(i6 - i, i3);
                        System.arraycopy(this.buffer, i, bArr, i2, i4);
                    }
                    return i4;
                }
            }
            throw new IndexOutOfBoundsException();
        }
        throw null;
    }

    public void write(int i, int i2) throws IOException {
        int i3 = this.length;
        if (i >= i3) {
            if (i <= i3) {
                int i4 = i3 + 1;
                this.length = i4;
                if (i4 < 0) {
                    throw new IOException("BLOB may not exceed 2GB in size");
                }
            } else {
                throw new IOException("BLOB buffer has been truncated");
            }
        }
        if (this.raFile != null) {
            if (this.currentPage != (i & PAGE_MASK)) {
                readPage(i);
            }
            this.buffer[i & BYTE_MASK] = (byte) i2;
            this.bufferDirty = true;
            return;
        }
        if (i >= this.buffer.length) {
            growBuffer(i + 1);
        }
        this.buffer[i] = (byte) i2;
    }

    /* access modifiers changed from: 0000 */
    public void write(int i, byte[] bArr, int i2, int i3) throws IOException {
        if (bArr != null) {
            if (i2 >= 0 && i2 <= bArr.length && i3 >= 0) {
                int i4 = i2 + i3;
                if (i4 <= bArr.length && i4 >= 0) {
                    if (i3 != 0) {
                        long j = (long) i;
                        if (((long) i3) + j > 2147483647L) {
                            throw new IOException("BLOB may not exceed 2GB in size");
                        } else if (i <= this.length) {
                            if (this.raFile == null) {
                                int i5 = i + i3;
                                if (i5 > this.buffer.length) {
                                    growBuffer(i5);
                                }
                                System.arraycopy(bArr, i2, this.buffer, i, i3);
                                i = i5;
                            } else if (i3 >= 1024) {
                                if (this.bufferDirty) {
                                    writePage(this.currentPage);
                                }
                                this.currentPage = -1;
                                this.raFile.seek(j);
                                this.raFile.write(bArr, i2, i3);
                                i += i3;
                            } else {
                                while (i3 > 0) {
                                    if (this.currentPage != (i & PAGE_MASK)) {
                                        readPage(i);
                                    }
                                    int i6 = i & BYTE_MASK;
                                    int min = Math.min(1024 - i6, i3);
                                    System.arraycopy(bArr, i2, this.buffer, i6, min);
                                    this.bufferDirty = true;
                                    i2 += min;
                                    i += min;
                                    i3 -= min;
                                }
                            }
                            if (i > this.length) {
                                this.length = i;
                            }
                            return;
                        } else {
                            throw new IOException("BLOB buffer has been truncated");
                        }
                    } else {
                        return;
                    }
                }
            }
            throw new IndexOutOfBoundsException();
        }
        throw null;
    }

    public void readPage(int i) throws IOException {
        int read;
        int i2 = i & PAGE_MASK;
        if (this.bufferDirty) {
            writePage(this.currentPage);
        }
        if (((long) i2) <= this.raFile.length()) {
            this.currentPage = i2;
            this.raFile.seek((long) i2);
            int i3 = 0;
            do {
                RandomAccessFile randomAccessFile = this.raFile;
                byte[] bArr = this.buffer;
                read = randomAccessFile.read(bArr, i3, bArr.length - i3);
                i3 += read == -1 ? 0 : read;
                if (i3 >= 1024) {
                    return;
                }
            } while (read != -1);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("readPage: Invalid page number ");
        sb.append(i2);
        throw new IOException(sb.toString());
    }

    public void writePage(int i) throws IOException {
        int i2 = i & PAGE_MASK;
        long j = (long) i2;
        if (j > this.raFile.length()) {
            StringBuilder sb = new StringBuilder();
            sb.append("writePage: Invalid page number ");
            sb.append(i2);
            throw new IOException(sb.toString());
        } else if (this.buffer.length == 1024) {
            this.raFile.seek(j);
            this.raFile.write(this.buffer);
            this.bufferDirty = false;
        } else {
            throw new IllegalStateException("writePage: buffer size invalid");
        }
    }

    public void close() throws IOException {
        int i = this.openCount;
        if (i > 0) {
            int i2 = i - 1;
            this.openCount = i2;
            if (i2 == 0 && this.raFile != null) {
                if (this.bufferDirty) {
                    writePage(this.currentPage);
                }
                this.raFile.close();
                this.raFile = null;
                this.buffer = EMPTY_BUFFER;
                this.currentPage = -1;
            }
        }
    }

    public void growBuffer(int i) {
        byte[] bArr;
        byte[] bArr2 = this.buffer;
        if (bArr2.length == 0) {
            this.buffer = new byte[Math.max(1024, i)];
            return;
        }
        if (bArr2.length * 2 <= i || bArr2.length > 16384) {
            bArr = new byte[(i + 16384)];
        } else {
            bArr = new byte[(bArr2.length * 2)];
        }
        byte[] bArr3 = this.buffer;
        System.arraycopy(bArr3, 0, bArr, 0, bArr3.length);
        this.buffer = bArr;
    }

    public void setBuffer(byte[] bArr, boolean z) {
        if (z) {
            byte[] bArr2 = new byte[bArr.length];
            this.buffer = bArr2;
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        } else {
            this.buffer = bArr;
        }
        this.length = this.buffer.length;
    }

    public byte[] getBytes(long j, int i) throws SQLException {
        long j2 = j - 1;
        String str = "HY090";
        if (j2 >= 0) {
            int i2 = this.length;
            if (j2 > ((long) i2)) {
                throw new SQLException(Messages.get("error.blobclob.badposlen"), str);
            } else if (i >= 0) {
                if (((long) i) + j2 > ((long) i2)) {
                    i = (int) (((long) i2) - j2);
                }
                try {
                    byte[] bArr = new byte[i];
                    if (this.blobFile == null) {
                        System.arraycopy(this.buffer, (int) j2, bArr, 0, i);
                    } else {
                        BlobInputStream blobInputStream = new BlobInputStream(j2);
                        int read = blobInputStream.read(bArr);
                        blobInputStream.close();
                        if (read != i) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Unexpected EOF on BLOB data file bc=");
                            sb.append(read);
                            sb.append(" data.len=");
                            sb.append(i);
                            throw new IOException(sb.toString());
                        }
                    }
                    return bArr;
                } catch (IOException e) {
                    throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
                }
            } else {
                throw new SQLException(Messages.get("error.blobclob.badlen"), str);
            }
        } else {
            throw new SQLException(Messages.get("error.blobclob.badpos"), str);
        }
    }

    public InputStream getBinaryStream(boolean z) throws SQLException {
        if (!z) {
            return new BlobInputStream(0);
        }
        try {
            return new AsciiInputStream(0);
        } catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
        }
    }

    public InputStream getUnicodeStream() throws SQLException {
        try {
            return new UnicodeInputStream(0);
        } catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
        }
    }

    public OutputStream setBinaryStream(long j, boolean z) throws SQLException {
        long j2 = j - 1;
        String str = "HY090";
        if (j2 < 0) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), str);
        } else if (j2 <= ((long) this.length)) {
            try {
                if (!this.isMemOnly && this.blobFile == null) {
                    createBlobFile();
                }
                if (z) {
                    return new AsciiOutputStream(j2);
                }
                return new BlobOutputStream(j2);
            } catch (IOException e) {
                throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
            }
        } else {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), str);
        }
    }

    public int setBytes(long j, byte[] bArr, int i, int i2, boolean z) throws SQLException {
        long j2 = j - 1;
        String str = "HY090";
        if (j2 >= 0) {
            int i3 = this.length;
            if (j2 > ((long) i3)) {
                throw new SQLException(Messages.get("error.blobclob.badposlen"), str);
            } else if (bArr == null) {
                throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
            } else if (i < 0 || i > bArr.length) {
                throw new SQLException(Messages.get("error.blobclob.badoffset"), str);
            } else if (i2 < 0 || ((long) i2) + j2 > 2147483647L || i + i2 > bArr.length) {
                throw new SQLException(Messages.get("error.blobclob.badlen"), str);
            } else if (this.blobFile != null || j2 != 0 || i2 < i3 || i2 > this.maxMemSize) {
                try {
                    if (!this.isMemOnly && this.blobFile == null) {
                        createBlobFile();
                    }
                    open();
                    write((int) j2, bArr, i, i2);
                    close();
                    return i2;
                } catch (IOException e) {
                    throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
                }
            } else {
                if (z) {
                    byte[] bArr2 = new byte[i2];
                    this.buffer = bArr2;
                    System.arraycopy(bArr, i, bArr2, 0, i2);
                } else {
                    this.buffer = bArr;
                }
                this.length = i2;
                return i2;
            }
        } else {
            throw new SQLException(Messages.get("error.blobclob.badpos"), str);
        }
    }

    public long getLength() {
        return (long) this.length;
    }

    public void setLength(long j) {
        this.length = (int) j;
    }

    public void truncate(long j) throws SQLException {
        String str = "HY090";
        if (j < 0) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), str);
        } else if (j <= ((long) this.length)) {
            this.length = (int) j;
            if (j == 0) {
                try {
                    if (this.blobFile != null) {
                        if (this.raFile != null) {
                            this.raFile.close();
                        }
                        this.blobFile.delete();
                    }
                    this.buffer = EMPTY_BUFFER;
                    this.blobFile = null;
                    this.raFile = null;
                    this.openCount = 0;
                    this.currentPage = -1;
                } catch (IOException e) {
                    throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
                } catch (Throwable th) {
                    this.buffer = EMPTY_BUFFER;
                    this.blobFile = null;
                    this.raFile = null;
                    this.openCount = 0;
                    this.currentPage = -1;
                    throw th;
                }
            }
        } else {
            throw new SQLException(Messages.get("error.blobclob.lentoolong"), str);
        }
    }

    public int position(byte[] bArr, long j) throws SQLException {
        long j2 = j - 1;
        String str = "HY090";
        if (j2 >= 0) {
            try {
                if (j2 >= ((long) this.length)) {
                    throw new SQLException(Messages.get("error.blobclob.badposlen"), str);
                } else if (bArr != null) {
                    if (!(bArr.length == 0 || this.length == 0)) {
                        if (bArr.length <= this.length) {
                            int length2 = this.length - bArr.length;
                            if (this.blobFile == null) {
                                int i = (int) j2;
                                while (i <= length2) {
                                    int i2 = 0;
                                    while (i2 < bArr.length && this.buffer[i + i2] == bArr[i2]) {
                                        i2++;
                                    }
                                    if (i2 == bArr.length) {
                                        return i + 1;
                                    }
                                    i++;
                                }
                            } else {
                                open();
                                int i3 = (int) j2;
                                while (i3 <= length2) {
                                    int i4 = 0;
                                    while (i4 < bArr.length && read(i3 + i4) == (bArr[i4] & 255)) {
                                        i4++;
                                    }
                                    if (i4 == bArr.length) {
                                        close();
                                        return i3 + 1;
                                    }
                                    i3++;
                                }
                                close();
                            }
                        }
                    }
                    return -1;
                } else {
                    throw new SQLException(Messages.get("error.blob.badpattern"), "HY009");
                }
            } catch (IOException e) {
                throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
            }
        } else {
            throw new SQLException(Messages.get("error.blobclob.badpos"), str);
        }
    }
}
