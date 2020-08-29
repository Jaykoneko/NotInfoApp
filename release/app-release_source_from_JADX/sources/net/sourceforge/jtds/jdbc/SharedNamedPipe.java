package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbNamedPipe;

public class SharedNamedPipe extends SharedSocket {
    private SmbNamedPipe pipe;

    /* access modifiers changed from: protected */
    public void setTimeout(int i) {
    }

    public SharedNamedPipe(JtdsConnection jtdsConnection) throws IOException {
        super(jtdsConnection.getBufferDir(), jtdsConnection.getTdsVersion(), jtdsConnection.getServerType());
        int socketTimeout = jtdsConnection.getSocketTimeout() * 1000;
        if (socketTimeout <= 0) {
            socketTimeout = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
        String valueOf = String.valueOf(socketTimeout);
        Config.setProperty("jcifs.smb.client.responseTimeout", valueOf);
        Config.setProperty("jcifs.smb.client.soTimeout", valueOf);
        NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(jtdsConnection.getDomainName(), jtdsConnection.getUser(), jtdsConnection.getPassword());
        StringBuilder sb = new StringBuilder(32);
        sb.append("smb://");
        sb.append(jtdsConnection.getServerName());
        sb.append("/IPC$");
        String instanceName = jtdsConnection.getInstanceName();
        if (!(instanceName == null || instanceName.length() == 0)) {
            sb.append("/MSSQL$");
            sb.append(instanceName);
        }
        sb.append(DefaultProperties.getNamedPipePath(jtdsConnection.getServerType()));
        setPipe(new SmbNamedPipe(sb.toString(), 3, ntlmPasswordAuthentication));
        setOut(new DataOutputStream(getPipe().getNamedPipeOutputStream()));
        setIn(new DataInputStream(new BufferedInputStream(getPipe().getNamedPipeInputStream(), Support.calculateNamedPipeBufferSize(jtdsConnection.getTdsVersion(), jtdsConnection.getPacketSize()))));
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
        return getPipe() != null;
    }

    /* access modifiers changed from: 0000 */
    public void close() throws IOException {
        super.close();
        getOut().close();
        getIn().close();
    }

    /* access modifiers changed from: 0000 */
    public void forceClose() {
        try {
            getOut().close();
        } catch (IOException unused) {
        } catch (Throwable th) {
            setOut(null);
            throw th;
        }
        setOut(null);
        try {
            getIn().close();
        } catch (IOException unused2) {
        } catch (Throwable th2) {
            setIn(null);
            throw th2;
        }
        setIn(null);
        setPipe(null);
    }

    private SmbNamedPipe getPipe() {
        return this.pipe;
    }

    private void setPipe(SmbNamedPipe smbNamedPipe) {
        this.pipe = smbNamedPipe;
    }
}
