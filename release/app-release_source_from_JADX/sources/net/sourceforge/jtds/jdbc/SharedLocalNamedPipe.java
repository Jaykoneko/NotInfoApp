package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SharedLocalNamedPipe extends SharedSocket {
    RandomAccessFile pipe;

    /* access modifiers changed from: protected */
    public void setTimeout(int i) {
    }

    public SharedLocalNamedPipe(JtdsConnection jtdsConnection) throws IOException {
        super(jtdsConnection.getBufferDir(), jtdsConnection.getTdsVersion(), jtdsConnection.getServerType());
        String serverName = jtdsConnection.getServerName();
        String instanceName = jtdsConnection.getInstanceName();
        StringBuilder sb = new StringBuilder(64);
        sb.append("\\\\");
        if (serverName == null || serverName.length() == 0) {
            sb.append('.');
        } else {
            sb.append(serverName);
        }
        sb.append("\\pipe");
        if (!(instanceName == null || instanceName.length() == 0)) {
            sb.append("\\MSSQL$");
            sb.append(instanceName);
        }
        sb.append(DefaultProperties.getNamedPipePath(jtdsConnection.getServerType()).replace('/', '\\'));
        this.pipe = new RandomAccessFile(sb.toString(), "rw");
        int calculateNamedPipeBufferSize = Support.calculateNamedPipeBufferSize(jtdsConnection.getTdsVersion(), jtdsConnection.getPacketSize());
        setOut(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.pipe.getFD()), calculateNamedPipeBufferSize)));
        setIn(new DataInputStream(new BufferedInputStream(new FileInputStream(this.pipe.getFD()), calculateNamedPipeBufferSize)));
    }

    /* access modifiers changed from: 0000 */
    public String getMAC() {
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                try {
                    if (!networkInterface.isLoopback() && !networkInterface.isVirtual()) {
                        byte[] hardwareAddress = networkInterface.getHardwareAddress();
                        if (hardwareAddress != null) {
                            String str = "";
                            for (byte valueOf : hardwareAddress) {
                                String format = String.format("%02X", new Object[]{Byte.valueOf(valueOf)});
                                StringBuilder sb = new StringBuilder();
                                sb.append(str);
                                sb.append(format);
                                str = sb.toString();
                            }
                            return str;
                        }
                    }
                } catch (SocketException unused) {
                }
            }
        } catch (SocketException unused2) {
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public boolean isConnected() {
        return this.pipe != null;
    }

    /* access modifiers changed from: 0000 */
    public byte[] sendNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        byte[] sendNetPacket = super.sendNetPacket(virtualSocket, bArr);
        getOut().flush();
        return sendNetPacket;
    }

    /* access modifiers changed from: 0000 */
    public void close() throws IOException {
        try {
            super.close();
            getOut().close();
            setOut(null);
            getIn().close();
            setIn(null);
            if (this.pipe != null) {
                this.pipe.close();
            }
        } finally {
            this.pipe = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void forceClose() {
        try {
            getOut().close();
        } catch (Exception unused) {
        } catch (Throwable th) {
            setOut(null);
            throw th;
        }
        setOut(null);
        try {
            getIn().close();
        } catch (Exception unused2) {
        } catch (Throwable th2) {
            setIn(null);
            throw th2;
        }
        setIn(null);
        try {
            if (this.pipe != null) {
                this.pipe.close();
            }
        } catch (IOException unused3) {
        } catch (Throwable th3) {
            this.pipe = null;
            throw th3;
        }
        this.pipe = null;
    }
}
