package net.sourceforge.jtds.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;

public class Logger {
    private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static PrintWriter log;

    public static void setLogWriter(PrintWriter printWriter) {
        log = printWriter;
    }

    public static PrintWriter getLogWriter() {
        return log;
    }

    public static boolean isActive() {
        return (log == null && DriverManager.getLogWriter() == null) ? false : true;
    }

    public static void println(String str) {
        PrintWriter printWriter = log;
        if (printWriter != null) {
            printWriter.println(str);
            return;
        }
        PrintWriter logWriter = DriverManager.getLogWriter();
        if (logWriter != null) {
            logWriter.println(str);
            logWriter.flush();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00e1 A[LOOP:2: B:45:0x00df->B:46:0x00e1, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void logPacket(int r9, boolean r10, byte[] r11) {
        /*
            r0 = 2
            byte r1 = r11[r0]
            r1 = r1 & 255(0xff, float:3.57E-43)
            int r1 = r1 << 8
            r2 = 3
            byte r3 = r11[r2]
            r3 = r3 & 255(0xff, float:3.57E-43)
            r1 = r1 | r3
            java.lang.StringBuffer r3 = new java.lang.StringBuffer
            r4 = 80
            r3.<init>(r4)
            java.lang.String r4 = "----- Stream #"
            r3.append(r4)
            r3.append(r9)
            if (r10 == 0) goto L_0x0021
            java.lang.String r9 = " read"
            goto L_0x0023
        L_0x0021:
            java.lang.String r9 = " send"
        L_0x0023:
            r3.append(r9)
            r9 = 1
            byte r10 = r11[r9]
            if (r10 == 0) goto L_0x002e
            java.lang.String r10 = " last "
            goto L_0x0030
        L_0x002e:
            java.lang.String r10 = " "
        L_0x0030:
            r3.append(r10)
            r10 = 0
            byte r4 = r11[r10]
            if (r4 == r9) goto L_0x0081
            if (r4 == r0) goto L_0x007b
            if (r4 == r2) goto L_0x0075
            r9 = 4
            if (r4 == r9) goto L_0x006f
            r9 = 6
            if (r4 == r9) goto L_0x0069
            switch(r4) {
                case 14: goto L_0x0063;
                case 15: goto L_0x005d;
                case 16: goto L_0x0057;
                case 17: goto L_0x0051;
                case 18: goto L_0x004b;
                default: goto L_0x0045;
            }
        L_0x0045:
            java.lang.String r9 = "Invalid packet "
            r3.append(r9)
            goto L_0x0086
        L_0x004b:
            java.lang.String r9 = "MS Prelogin packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0051:
            java.lang.String r9 = "NTLM Authentication packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0057:
            java.lang.String r9 = "MS Login packet "
            r3.append(r9)
            goto L_0x0086
        L_0x005d:
            java.lang.String r9 = "TDS5 Request packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0063:
            java.lang.String r9 = "XA control packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0069:
            java.lang.String r9 = "Cancel packet "
            r3.append(r9)
            goto L_0x0086
        L_0x006f:
            java.lang.String r9 = "Reply packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0075:
            java.lang.String r9 = "RPC packet "
            r3.append(r9)
            goto L_0x0086
        L_0x007b:
            java.lang.String r9 = "Login packet "
            r3.append(r9)
            goto L_0x0086
        L_0x0081:
            java.lang.String r9 = "Request packet "
            r3.append(r9)
        L_0x0086:
            java.lang.String r9 = r3.toString()
            println(r9)
            java.lang.String r9 = ""
            println(r9)
            r3.setLength(r10)
            r0 = 0
        L_0x0096:
            if (r0 >= r1) goto L_0x011d
            r2 = 1000(0x3e8, float:1.401E-42)
            r4 = 32
            if (r0 >= r2) goto L_0x00a1
            r3.append(r4)
        L_0x00a1:
            r2 = 100
            if (r0 >= r2) goto L_0x00a8
            r3.append(r4)
        L_0x00a8:
            r2 = 10
            if (r0 >= r2) goto L_0x00af
            r3.append(r4)
        L_0x00af:
            r3.append(r0)
            r2 = 58
            r3.append(r2)
            r3.append(r4)
            r2 = 0
        L_0x00bb:
            r5 = 16
            if (r2 >= r5) goto L_0x00df
            int r6 = r0 + r2
            if (r6 >= r1) goto L_0x00df
            byte r5 = r11[r6]
            r5 = r5 & 255(0xff, float:3.57E-43)
            char[] r6 = hex
            int r7 = r5 >> 4
            char r6 = r6[r7]
            r3.append(r6)
            char[] r6 = hex
            r5 = r5 & 15
            char r5 = r6[r5]
            r3.append(r5)
            r3.append(r4)
            int r2 = r2 + 1
            goto L_0x00bb
        L_0x00df:
            if (r2 >= r5) goto L_0x00e9
            java.lang.String r6 = "   "
            r3.append(r6)
            int r2 = r2 + 1
            goto L_0x00df
        L_0x00e9:
            r2 = 124(0x7c, float:1.74E-43)
            r3.append(r2)
            r6 = 0
        L_0x00ef:
            if (r6 >= r5) goto L_0x010c
            int r7 = r0 + r6
            if (r7 >= r1) goto L_0x010c
            byte r7 = r11[r7]
            r7 = r7 & 255(0xff, float:3.57E-43)
            r8 = 31
            if (r7 <= r8) goto L_0x0106
            r8 = 127(0x7f, float:1.78E-43)
            if (r7 >= r8) goto L_0x0106
            char r7 = (char) r7
            r3.append(r7)
            goto L_0x0109
        L_0x0106:
            r3.append(r4)
        L_0x0109:
            int r6 = r6 + 1
            goto L_0x00ef
        L_0x010c:
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            println(r2)
            r3.setLength(r10)
            int r0 = r0 + 16
            goto L_0x0096
        L_0x011d:
            println(r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.Logger.logPacket(int, boolean, byte[]):void");
    }

    public static void logException(Exception exc) {
        PrintWriter printWriter = log;
        if (printWriter != null) {
            exc.printStackTrace(printWriter);
            return;
        }
        PrintWriter logWriter = DriverManager.getLogWriter();
        if (logWriter != null) {
            exc.printStackTrace(logWriter);
            logWriter.flush();
        }
    }

    public static void setActive(boolean z) {
        if (z && log == null) {
            try {
                log = new PrintWriter(new FileOutputStream("log.out"), true);
            } catch (IOException unused) {
                log = null;
            }
        }
    }
}
