package net.sourceforge.jtds.util;

public class SSPIJNIClient {
    private static boolean libraryLoaded = true;
    private static SSPIJNIClient thisInstance;
    private boolean initialized;

    private native void initialize();

    private native byte[] prepareSSORequest();

    private native byte[] prepareSSOSubmit(byte[] bArr, long j);

    private native void unInitialize();

    static {
        try {
            System.loadLibrary("ntlmauth");
        } catch (UnsatisfiedLinkError e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to load library: ");
            sb.append(e);
            Logger.println(sb.toString());
        }
    }

    private SSPIJNIClient() {
    }

    public static synchronized SSPIJNIClient getInstance() throws Exception {
        SSPIJNIClient sSPIJNIClient;
        synchronized (SSPIJNIClient.class) {
            if (thisInstance == null) {
                if (libraryLoaded) {
                    SSPIJNIClient sSPIJNIClient2 = new SSPIJNIClient();
                    thisInstance = sSPIJNIClient2;
                    sSPIJNIClient2.invokeInitialize();
                } else {
                    throw new Exception("Native SSPI library not loaded. Check the java.library.path system property.");
                }
            }
            sSPIJNIClient = thisInstance;
        }
        return sSPIJNIClient;
    }

    public void invokeInitialize() {
        if (!this.initialized) {
            initialize();
            this.initialized = true;
        }
    }

    public void invokeUnInitialize() {
        if (this.initialized) {
            unInitialize();
            this.initialized = false;
        }
    }

    public byte[] invokePrepareSSORequest() throws Exception {
        if (this.initialized) {
            return prepareSSORequest();
        }
        throw new Exception("SSPI Not Initialized");
    }

    public byte[] invokePrepareSSOSubmit(byte[] bArr) throws Exception {
        if (this.initialized) {
            return prepareSSOSubmit(bArr, (long) bArr.length);
        }
        throw new Exception("SSPI Not Initialized");
    }
}
