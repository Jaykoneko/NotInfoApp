package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import net.sourceforge.jtds.util.BlobBuffer;

public class BlobImpl implements Blob {
    private static final byte[] EMPTY_BLOB = new byte[0];
    private final BlobBuffer blobBuffer;

    BlobImpl(JtdsConnection jtdsConnection) {
        this(jtdsConnection, EMPTY_BLOB);
    }

    BlobImpl(JtdsConnection jtdsConnection, byte[] bArr) {
        if (bArr != null) {
            BlobBuffer blobBuffer2 = new BlobBuffer(jtdsConnection.getBufferDir(), jtdsConnection.getLobBuffer());
            this.blobBuffer = blobBuffer2;
            blobBuffer2.setBuffer(bArr, false);
            return;
        }
        throw new IllegalArgumentException("bytes cannot be null");
    }

    public InputStream getBinaryStream() throws SQLException {
        return this.blobBuffer.getBinaryStream(false);
    }

    public byte[] getBytes(long j, int i) throws SQLException {
        return this.blobBuffer.getBytes(j, i);
    }

    public long length() throws SQLException {
        return this.blobBuffer.getLength();
    }

    public long position(byte[] bArr, long j) throws SQLException {
        return (long) this.blobBuffer.position(bArr, j);
    }

    public long position(Blob blob, long j) throws SQLException {
        if (blob != null) {
            return (long) this.blobBuffer.position(blob.getBytes(1, (int) blob.length()), j);
        }
        throw new SQLException(Messages.get("error.blob.badpattern"), "HY009");
    }

    public OutputStream setBinaryStream(long j) throws SQLException {
        return this.blobBuffer.setBinaryStream(j, false);
    }

    public int setBytes(long j, byte[] bArr) throws SQLException {
        if (bArr != null) {
            return setBytes(j, bArr, 0, bArr.length);
        }
        throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
    }

    public int setBytes(long j, byte[] bArr, int i, int i2) throws SQLException {
        if (bArr != null) {
            return this.blobBuffer.setBytes(j, bArr, i, i2, true);
        }
        throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
    }

    public void truncate(long j) throws SQLException {
        this.blobBuffer.truncate(j);
    }

    public void free() throws SQLException {
        throw new AbstractMethodError();
    }

    public InputStream getBinaryStream(long j, long j2) throws SQLException {
        throw new AbstractMethodError();
    }
}
