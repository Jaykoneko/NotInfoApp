package net.sourceforge.jtds.jdbc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.sourceforge.jtds.ssl.SocketFactories;
import net.sourceforge.jtds.util.Logger;

class SharedSocket {
    private static final int TDS_DONE_LEN = 9;
    private static final int TDS_DONE_TOKEN = 253;
    private static final int TDS_HDR_LEN = 8;
    private static int globalMemUsage = 0;
    private static int memoryBudget = 100000;
    private static int minMemPkts = 8;
    private static int peakMemUsage;
    private static boolean securityViolation;
    private final AtomicInteger _LastID;
    private final ConcurrentMap<Integer, VirtualSocket> _VirtualSockets;
    private final File bufferDir;
    private final Object cancelMonitor;
    private boolean cancelPending;
    private CharsetInfo charsetInfo;
    private final byte[] doneBuffer;
    private int doneBufferFrag;
    private final byte[] hdrBuf;
    private String host;

    /* renamed from: in */
    private DataInputStream f120in;
    private int maxBufSize;
    private DataOutputStream out;
    private int packetCount;
    private int port;
    private VirtualSocket responseOwner;
    protected final int serverType;
    private Socket socket;
    private Socket sslSocket;
    private int tdsVersion;

    static class VirtualSocket {
        RandomAccessFile diskQueue;

        /* renamed from: id */
        final int f121id;
        int inputPkts;
        final LinkedList pktQueue;
        int pktsOnDisk;
        File queueFile;

        private VirtualSocket(int i) {
            this.f121id = i;
            this.pktQueue = new LinkedList();
        }
    }

    protected SharedSocket(File file, int i, int i2) {
        this.maxBufSize = 512;
        this._LastID = new AtomicInteger();
        this._VirtualSockets = new ConcurrentHashMap();
        this.hdrBuf = new byte[8];
        this.cancelMonitor = new Object();
        this.doneBuffer = new byte[9];
        this.doneBufferFrag = 0;
        this.bufferDir = file;
        this.tdsVersion = i;
        this.serverType = i2;
    }

    SharedSocket(JtdsConnection jtdsConnection) throws IOException, UnknownHostException {
        this(jtdsConnection.getBufferDir(), jtdsConnection.getTdsVersion(), jtdsConnection.getServerType());
        this.host = jtdsConnection.getServerName();
        this.port = jtdsConnection.getPortNumber();
        this.socket = createSocketForJDBC3(jtdsConnection);
        setOut(new DataOutputStream(this.socket.getOutputStream()));
        setIn(new DataInputStream(this.socket.getInputStream()));
        this.socket.setTcpNoDelay(jtdsConnection.getTcpNoDelay());
        this.socket.setSoTimeout(jtdsConnection.getSocketTimeout() * 1000);
        this.socket.setKeepAlive(jtdsConnection.getSocketKeepAlive());
    }

    private Socket createSocketForJDBC3(JtdsConnection jtdsConnection) throws IOException {
        String serverName = jtdsConnection.getServerName();
        int portNumber = jtdsConnection.getPortNumber();
        String bindAddress = jtdsConnection.getBindAddress();
        int loginTimeout = jtdsConnection.getLoginTimeout();
        Socket socket2 = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverName, portNumber);
        if (bindAddress != null && !bindAddress.isEmpty()) {
            socket2.bind(new InetSocketAddress(bindAddress, 0));
        }
        socket2.connect(inetSocketAddress, loginTimeout * 1000);
        return socket2;
    }

    /* access modifiers changed from: 0000 */
    public String getMAC() {
        byte[] bArr;
        try {
            NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(this.socket.getLocalAddress());
            if (byInetAddress == null) {
                bArr = null;
            } else {
                bArr = byInetAddress.getHardwareAddress();
            }
            if (bArr != null) {
                String str = "";
                for (byte valueOf : bArr) {
                    String format = String.format("%02X", new Object[]{Byte.valueOf(valueOf)});
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(format);
                    str = sb.toString();
                }
                return str;
            }
        } catch (SocketException unused) {
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void enableEncryption(String str) throws IOException {
        Logger.println("Enabling TLS encryption");
        this.sslSocket = SocketFactories.getSocketFactory(str, this.socket).createSocket(getHost(), getPort());
        setOut(new DataOutputStream(this.sslSocket.getOutputStream()));
        setIn(new DataInputStream(this.sslSocket.getInputStream()));
    }

    /* access modifiers changed from: 0000 */
    public void disableEncryption() throws IOException {
        Logger.println("Disabling TLS encryption");
        this.sslSocket.close();
        this.sslSocket = null;
        setOut(new DataOutputStream(this.socket.getOutputStream()));
        setIn(new DataInputStream(this.socket.getInputStream()));
    }

    /* access modifiers changed from: 0000 */
    public void setCharsetInfo(CharsetInfo charsetInfo2) {
        this.charsetInfo = charsetInfo2;
    }

    /* access modifiers changed from: 0000 */
    public CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    /* access modifiers changed from: 0000 */
    public String getCharset() {
        return this.charsetInfo.getCharset();
    }

    /* access modifiers changed from: 0000 */
    public RequestStream getRequestStream(int i, int i2) {
        int incrementAndGet;
        VirtualSocket virtualSocket;
        do {
            incrementAndGet = this._LastID.incrementAndGet();
            virtualSocket = new VirtualSocket(incrementAndGet);
        } while (this._VirtualSockets.putIfAbsent(Integer.valueOf(incrementAndGet), virtualSocket) != null);
        return new RequestStream(this, virtualSocket, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public ResponseStream getResponseStream(RequestStream requestStream, int i) {
        return new ResponseStream(this, requestStream.getVirtualSocket(), i);
    }

    /* access modifiers changed from: 0000 */
    public int getTdsVersion() {
        return this.tdsVersion;
    }

    /* access modifiers changed from: protected */
    public void setTdsVersion(int i) {
        this.tdsVersion = i;
    }

    static void setMemoryBudget(int i) {
        memoryBudget = i;
    }

    static int getMemoryBudget() {
        return memoryBudget;
    }

    static void setMinMemPkts(int i) {
        minMemPkts = i;
    }

    static int getMinMemPkts() {
        return minMemPkts;
    }

    /* access modifiers changed from: 0000 */
    public boolean isConnected() {
        return this.socket != null;
    }

    /* access modifiers changed from: 0000 */
    public boolean cancel(VirtualSocket virtualSocket) {
        synchronized (this.cancelMonitor) {
            if (this.responseOwner == virtualSocket && !this.cancelPending) {
                try {
                    this.cancelPending = true;
                    this.doneBufferFrag = 0;
                    byte[] bArr = new byte[8];
                    bArr[0] = 6;
                    bArr[1] = 1;
                    bArr[2] = 0;
                    bArr[3] = 8;
                    bArr[4] = 0;
                    bArr[5] = 0;
                    bArr[6] = this.tdsVersion >= 3 ? (byte) 1 : 0;
                    bArr[7] = 0;
                    getOut().write(bArr, 0, 8);
                    getOut().flush();
                    if (Logger.isActive()) {
                        Logger.logPacket(virtualSocket.f121id, false, bArr);
                    }
                    return true;
                } catch (IOException unused) {
                    return false;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void close() throws IOException {
        if (Logger.isActive()) {
            StringBuilder sb = new StringBuilder();
            sb.append("TdsSocket: Max buffer memory used = ");
            sb.append(peakMemUsage / 1024);
            sb.append("KB");
            Logger.println(sb.toString());
        }
        for (VirtualSocket virtualSocket : this._VirtualSockets.values()) {
            if (!(virtualSocket == null || virtualSocket.diskQueue == null)) {
                try {
                    virtualSocket.diskQueue.close();
                    virtualSocket.queueFile.delete();
                } catch (IOException unused) {
                }
            }
        }
        this._VirtualSockets.clear();
        try {
            if (this.sslSocket != null) {
                this.sslSocket.close();
                this.sslSocket = null;
            }
        } finally {
            Socket socket2 = this.socket;
            if (socket2 != null) {
                socket2.close();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void forceClose() {
        Socket socket2 = this.socket;
        if (socket2 != null) {
            try {
                socket2.close();
            } catch (IOException unused) {
            } catch (Throwable th) {
                this.sslSocket = null;
                this.socket = null;
                throw th;
            }
            this.sslSocket = null;
            this.socket = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void closeStream(VirtualSocket virtualSocket) {
        this._VirtualSockets.remove(Integer.valueOf(virtualSocket.f121id));
        if (virtualSocket.diskQueue != null) {
            try {
                virtualSocket.diskQueue.close();
                virtualSocket.queueFile.delete();
            } catch (IOException unused) {
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public byte[] sendNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        synchronized (this._VirtualSockets) {
            while (virtualSocket.inputPkts > 0) {
                if (Logger.isActive()) {
                    Logger.println("TdsSocket: Unread data in input packet queue");
                }
                dequeueInput(virtualSocket);
            }
            if (this.responseOwner == null) {
                getOut().write(bArr, 0, getPktLen(bArr));
            } else {
                boolean z = this.responseOwner == virtualSocket;
                VirtualSocket virtualSocket2 = this.responseOwner;
                byte[] bArr2 = null;
                do {
                    if (!z) {
                        bArr2 = null;
                    }
                    bArr2 = readPacket(bArr2);
                    if (!z) {
                        enqueueInput(virtualSocket2, bArr2);
                    }
                } while (bArr2[1] == 0);
            }
            getOut().write(bArr, 0, getPktLen(bArr));
            if (bArr[1] != 0) {
                getOut().flush();
                this.responseOwner = virtualSocket;
            }
        }
        return bArr;
    }

    /* access modifiers changed from: 0000 */
    public byte[] getNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        synchronized (this._VirtualSockets) {
            if (virtualSocket.inputPkts > 0) {
                byte[] dequeueInput = dequeueInput(virtualSocket);
                return dequeueInput;
            } else if (this.responseOwner == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Stream ");
                sb.append(virtualSocket.f121id);
                sb.append(" attempting to read when no request has been sent");
                throw new IOException(sb.toString());
            } else if (this.responseOwner == virtualSocket) {
                byte[] readPacket = readPacket(bArr);
                return readPacket;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Stream ");
                sb2.append(virtualSocket.f121id);
                sb2.append(" is trying to read data that belongs to stream ");
                sb2.append(this.responseOwner.f121id);
                throw new IOException(sb2.toString());
            }
        }
    }

    private void enqueueInput(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        if (globalMemUsage + bArr.length > memoryBudget && virtualSocket.pktQueue.size() >= minMemPkts && !securityViolation && virtualSocket.diskQueue == null) {
            try {
                virtualSocket.queueFile = File.createTempFile("jtds", ".tmp", this.bufferDir);
                virtualSocket.diskQueue = new RandomAccessFile(virtualSocket.queueFile, "rw");
                while (virtualSocket.pktQueue.size() > 0) {
                    byte[] bArr2 = (byte[]) virtualSocket.pktQueue.removeFirst();
                    virtualSocket.diskQueue.write(bArr2, 0, getPktLen(bArr2));
                    virtualSocket.pktsOnDisk++;
                }
            } catch (SecurityException unused) {
                securityViolation = true;
                virtualSocket.queueFile = null;
                virtualSocket.diskQueue = null;
            }
        }
        if (virtualSocket.diskQueue != null) {
            virtualSocket.diskQueue.write(bArr, 0, getPktLen(bArr));
            virtualSocket.pktsOnDisk++;
        } else {
            virtualSocket.pktQueue.addLast(bArr);
            int length = globalMemUsage + bArr.length;
            globalMemUsage = length;
            if (length > peakMemUsage) {
                peakMemUsage = length;
            }
        }
        virtualSocket.inputPkts++;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [java.io.RandomAccessFile, java.io.File] */
    /* JADX WARNING: type inference failed for: r2v1, types: [byte[]] */
    /* JADX WARNING: type inference failed for: r2v3, types: [byte[]] */
    /* JADX WARNING: type inference failed for: r3v3, types: [byte[], java.lang.Object] */
    /* JADX WARNING: type inference failed for: r2v4 */
    /* JADX WARNING: type inference failed for: r2v5 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r2v0, types: [java.io.RandomAccessFile, java.io.File]
      assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], ?[OBJECT, ARRAY]]
      uses: [java.io.File, java.io.RandomAccessFile, ?[int, boolean, OBJECT, ARRAY, byte, short, char], byte[]]
      mth insns count: 56
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] dequeueInput(net.sourceforge.jtds.jdbc.SharedSocket.VirtualSocket r8) throws java.io.IOException {
        /*
            r7 = this;
            int r0 = r8.pktsOnDisk
            r1 = 1
            r2 = 0
            if (r0 <= 0) goto L_0x005a
            java.io.RandomAccessFile r0 = r8.diskQueue
            long r3 = r0.getFilePointer()
            java.io.RandomAccessFile r0 = r8.diskQueue
            long r5 = r0.length()
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x001d
            java.io.RandomAccessFile r0 = r8.diskQueue
            r3 = 0
            r0.seek(r3)
        L_0x001d:
            java.io.RandomAccessFile r0 = r8.diskQueue
            byte[] r3 = r7.hdrBuf
            r4 = 0
            r5 = 8
            r0.readFully(r3, r4, r5)
            byte[] r0 = r7.hdrBuf
            int r0 = getPktLen(r0)
            byte[] r3 = new byte[r0]
            byte[] r6 = r7.hdrBuf
            java.lang.System.arraycopy(r6, r4, r3, r4, r5)
            java.io.RandomAccessFile r4 = r8.diskQueue
            int r0 = r0 - r5
            r4.readFully(r3, r5, r0)
            int r0 = r8.pktsOnDisk
            int r0 = r0 - r1
            r8.pktsOnDisk = r0
            int r0 = r8.pktsOnDisk
            if (r0 >= r1) goto L_0x0058
            java.io.RandomAccessFile r0 = r8.diskQueue     // Catch:{ all -> 0x0052 }
            r0.close()     // Catch:{ all -> 0x0052 }
            java.io.File r0 = r8.queueFile     // Catch:{ all -> 0x0052 }
            r0.delete()     // Catch:{ all -> 0x0052 }
            r8.queueFile = r2
            r8.diskQueue = r2
            goto L_0x0058
        L_0x0052:
            r0 = move-exception
            r8.queueFile = r2
            r8.diskQueue = r2
            throw r0
        L_0x0058:
            r2 = r3
            goto L_0x0073
        L_0x005a:
            java.util.LinkedList r0 = r8.pktQueue
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0073
            java.util.LinkedList r0 = r8.pktQueue
            java.lang.Object r0 = r0.removeFirst()
            byte[] r0 = (byte[]) r0
            r2 = r0
            byte[] r2 = (byte[]) r2
            int r0 = globalMemUsage
            int r3 = r2.length
            int r0 = r0 - r3
            globalMemUsage = r0
        L_0x0073:
            if (r2 == 0) goto L_0x007a
            int r0 = r8.inputPkts
            int r0 = r0 - r1
            r8.inputPkts = r0
        L_0x007a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.dequeueInput(net.sourceforge.jtds.jdbc.SharedSocket$VirtualSocket):byte[]");
    }

    private byte[] readPacket(byte[] bArr) throws IOException {
        try {
            getIn().readFully(this.hdrBuf);
            byte b = this.hdrBuf[0];
            if (b == 2 || b == 1 || b == 15 || b == 4) {
                int pktLen = getPktLen(this.hdrBuf);
                if (pktLen < 8 || pktLen > 65536) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid network packet length ");
                    sb.append(pktLen);
                    throw new IOException(sb.toString());
                }
                if (bArr == null || pktLen > bArr.length) {
                    bArr = new byte[pktLen];
                    if (pktLen > this.maxBufSize) {
                        this.maxBufSize = pktLen;
                    }
                }
                System.arraycopy(this.hdrBuf, 0, bArr, 0, 8);
                try {
                    int i = pktLen - 8;
                    getIn().readFully(bArr, 8, i);
                    int i2 = this.packetCount + 1;
                    this.packetCount = i2;
                    if (i2 == 1 && this.serverType == 1 && "NTLMSSP".equals(new String(bArr, 11, 7))) {
                        bArr[1] = 1;
                    }
                    synchronized (this.cancelMonitor) {
                        if (this.cancelPending) {
                            int min = Math.min(9, i);
                            int i3 = 9 - min;
                            System.arraycopy(this.doneBuffer, min, this.doneBuffer, 0, i3);
                            System.arraycopy(bArr, pktLen - min, this.doneBuffer, i3, min);
                            int min2 = Math.min(9, this.doneBufferFrag + min);
                            this.doneBufferFrag = min2;
                            if (min2 < 9) {
                                bArr[1] = 0;
                            }
                            if (bArr[1] == 1) {
                                if ((this.doneBuffer[0] & 255) < TDS_DONE_TOKEN) {
                                    throw new IOException("Expecting a TDS_DONE or TDS_DONEPROC.");
                                } else if ((this.doneBuffer[1] & 32) != 0) {
                                    this.cancelPending = false;
                                } else {
                                    bArr[1] = 0;
                                }
                            }
                        }
                        if (bArr[1] != 0) {
                            this.responseOwner = null;
                        }
                    }
                    return bArr;
                } catch (EOFException unused) {
                    throw new IOException("DB server closed connection.");
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unknown packet type 0x");
                sb2.append(Integer.toHexString(b & 255));
                throw new IOException(sb2.toString());
            }
        } catch (EOFException unused2) {
            throw new IOException("DB server closed connection.");
        }
    }

    static int getPktLen(byte[] bArr) {
        return ((bArr[2] & 255) << 8) | (bArr[3] & 255);
    }

    /* access modifiers changed from: protected */
    public void setTimeout(int i) throws SocketException {
        this.socket.setSoTimeout(i);
    }

    /* access modifiers changed from: protected */
    public void setKeepAlive(boolean z) throws SocketException {
        this.socket.setKeepAlive(z);
    }

    /* access modifiers changed from: protected */
    public DataInputStream getIn() {
        return this.f120in;
    }

    /* access modifiers changed from: protected */
    public void setIn(DataInputStream dataInputStream) {
        this.f120in = dataInputStream;
    }

    /* access modifiers changed from: protected */
    public DataOutputStream getOut() {
        return this.out;
    }

    /* access modifiers changed from: protected */
    public void setOut(DataOutputStream dataOutputStream) {
        this.out = dataOutputStream;
    }

    /* access modifiers changed from: protected */
    public String getHost() {
        return this.host;
    }

    /* access modifiers changed from: protected */
    public int getPort() {
        return this.port;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
