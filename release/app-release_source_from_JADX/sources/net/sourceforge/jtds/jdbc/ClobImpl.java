package net.sourceforge.jtds.jdbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import net.sourceforge.jtds.util.BlobBuffer;

public class ClobImpl implements Clob {
    private static final String EMPTY_CLOB = "";
    private final BlobBuffer blobBuffer;

    ClobImpl(JtdsConnection jtdsConnection) {
        this(jtdsConnection, "");
    }

    ClobImpl(JtdsConnection jtdsConnection, String str) {
        if (str != null) {
            this.blobBuffer = new BlobBuffer(jtdsConnection.getBufferDir(), jtdsConnection.getLobBuffer());
            try {
                this.blobBuffer.setBuffer(str.getBytes("UTF-16LE"), false);
            } catch (UnsupportedEncodingException unused) {
                throw new IllegalStateException("UTF-16LE encoding is not supported.");
            }
        } else {
            throw new IllegalArgumentException("str cannot be null");
        }
    }

    /* access modifiers changed from: 0000 */
    public BlobBuffer getBlobBuffer() {
        return this.blobBuffer;
    }

    public InputStream getAsciiStream() throws SQLException {
        return this.blobBuffer.getBinaryStream(true);
    }

    public Reader getCharacterStream() throws SQLException {
        try {
            return new BufferedReader(new InputStreamReader(this.blobBuffer.getBinaryStream(false), "UTF-16LE"));
        } catch (UnsupportedEncodingException unused) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    public String getSubString(long j, int i) throws SQLException {
        if (i == 0) {
            return "";
        }
        try {
            return new String(this.blobBuffer.getBytes(((j - 1) * 2) + 1, i * 2), "UTF-16LE");
        } catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", (Object) e.getMessage()), "HY000");
        }
    }

    public long length() throws SQLException {
        return this.blobBuffer.getLength() / 2;
    }

    public long position(String str, long j) throws SQLException {
        if (str != null) {
            try {
                int position = this.blobBuffer.position(str.getBytes("UTF-16LE"), ((j - 1) * 2) + 1);
                if (position >= 0) {
                    position = ((position - 1) / 2) + 1;
                }
                return (long) position;
            } catch (UnsupportedEncodingException unused) {
                throw new IllegalStateException("UTF-16LE encoding is not supported.");
            }
        } else {
            throw new SQLException(Messages.get("error.clob.searchnull"), "HY009");
        }
    }

    public long position(Clob clob, long j) throws SQLException {
        if (clob != null) {
            BlobBuffer blobBuffer2 = ((ClobImpl) clob).getBlobBuffer();
            int position = this.blobBuffer.position(blobBuffer2.getBytes(1, (int) blobBuffer2.getLength()), ((j - 1) * 2) + 1);
            if (position >= 0) {
                position = ((position - 1) / 2) + 1;
            }
            return (long) position;
        }
        throw new SQLException(Messages.get("error.clob.searchnull"), "HY009");
    }

    public OutputStream setAsciiStream(long j) throws SQLException {
        return this.blobBuffer.setBinaryStream(((j - 1) * 2) + 1, true);
    }

    public Writer setCharacterStream(long j) throws SQLException {
        try {
            return new BufferedWriter(new OutputStreamWriter(this.blobBuffer.setBinaryStream(((j - 1) * 2) + 1, false), "UTF-16LE"));
        } catch (UnsupportedEncodingException unused) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    public int setString(long j, String str) throws SQLException {
        if (str != null) {
            return setString(j, str, 0, str.length());
        }
        throw new SQLException(Messages.get("error.clob.strnull"), "HY009");
    }

    public int setString(long j, String str, int i, int i2) throws SQLException {
        String str2 = "HY090";
        if (i < 0 || i > str.length()) {
            throw new SQLException(Messages.get("error.blobclob.badoffset"), str2);
        }
        if (i2 >= 0) {
            int i3 = i2 + i;
            if (i3 <= str.length()) {
                try {
                    byte[] bytes = str.substring(i, i3).getBytes("UTF-16LE");
                    return this.blobBuffer.setBytes(((j - 1) * 2) + 1, bytes, 0, bytes.length, false);
                } catch (UnsupportedEncodingException unused) {
                    throw new IllegalStateException("UTF-16LE encoding is not supported.");
                }
            }
        }
        throw new SQLException(Messages.get("error.blobclob.badlen"), str2);
    }

    public void truncate(long j) throws SQLException {
        this.blobBuffer.truncate(j * 2);
    }

    public void free() throws SQLException {
        throw new AbstractMethodError();
    }

    public Reader getCharacterStream(long j, long j2) throws SQLException {
        throw new AbstractMethodError();
    }
}
