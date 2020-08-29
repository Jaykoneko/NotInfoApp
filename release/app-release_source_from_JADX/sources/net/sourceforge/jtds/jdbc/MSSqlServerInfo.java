package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;

public class MSSqlServerInfo {
    private final int numRetries = 3;
    private String[] serverInfoStrings;
    private final int timeout = 2000;

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0072 A[Catch:{ all -> 0x0069 }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MSSqlServerInfo(java.lang.String r10) throws java.sql.SQLException {
        /*
            r9 = this;
            r9.<init>()
            r0 = 3
            r9.numRetries = r0
            r1 = 2000(0x7d0, float:2.803E-42)
            r9.timeout = r1
            r2 = 0
            java.net.InetAddress r3 = java.net.InetAddress.getByName(r10)     // Catch:{ Exception -> 0x006b }
            java.net.DatagramSocket r4 = new java.net.DatagramSocket     // Catch:{ Exception -> 0x006b }
            r4.<init>()     // Catch:{ Exception -> 0x006b }
            r2 = 1
            byte[] r5 = new byte[r2]     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r6 = 2
            r7 = 0
            r5[r7] = r6     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            java.net.DatagramPacket r6 = new java.net.DatagramPacket     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r8 = 1434(0x59a, float:2.01E-42)
            r6.<init>(r5, r2, r3, r8)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r4.setSoTimeout(r1)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r1 = 0
        L_0x0026:
            if (r1 >= r0) goto L_0x005f
            byte[] r2 = new byte[r7]     // Catch:{ InterruptedIOException -> 0x0052 }
        L_0x002a:
            int r2 = r2.length     // Catch:{ InterruptedIOException -> 0x0052 }
            int r2 = r2 + 4096
            byte[] r3 = new byte[r2]     // Catch:{ InterruptedIOException -> 0x0052 }
            java.net.DatagramPacket r5 = new java.net.DatagramPacket     // Catch:{ InterruptedIOException -> 0x0052 }
            r5.<init>(r3, r2)     // Catch:{ InterruptedIOException -> 0x0052 }
            r4.send(r6)     // Catch:{ InterruptedIOException -> 0x0052 }
            r4.receive(r5)     // Catch:{ InterruptedIOException -> 0x0052 }
            int r5 = r5.getLength()     // Catch:{ InterruptedIOException -> 0x0052 }
            if (r5 == r2) goto L_0x0050
            java.lang.String r2 = extractString(r3, r5)     // Catch:{ InterruptedIOException -> 0x0052 }
            r3 = 59
            java.lang.String[] r2 = split(r2, r3)     // Catch:{ InterruptedIOException -> 0x0052 }
            r9.serverInfoStrings = r2     // Catch:{ InterruptedIOException -> 0x0052 }
            r4.close()
            return
        L_0x0050:
            r2 = r3
            goto L_0x002a
        L_0x0052:
            r2 = move-exception
            boolean r3 = net.sourceforge.jtds.util.Logger.isActive()     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            if (r3 == 0) goto L_0x005c
            net.sourceforge.jtds.util.Logger.logException(r2)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
        L_0x005c:
            int r1 = r1 + 1
            goto L_0x0026
        L_0x005f:
            r4.close()
            goto L_0x007a
        L_0x0063:
            r10 = move-exception
            r2 = r4
            goto L_0x0088
        L_0x0066:
            r0 = move-exception
            r2 = r4
            goto L_0x006c
        L_0x0069:
            r10 = move-exception
            goto L_0x0088
        L_0x006b:
            r0 = move-exception
        L_0x006c:
            boolean r1 = net.sourceforge.jtds.util.Logger.isActive()     // Catch:{ all -> 0x0069 }
            if (r1 == 0) goto L_0x0075
            net.sourceforge.jtds.util.Logger.logException(r0)     // Catch:{ all -> 0x0069 }
        L_0x0075:
            if (r2 == 0) goto L_0x007a
            r2.close()
        L_0x007a:
            java.sql.SQLException r0 = new java.sql.SQLException
            java.lang.String r1 = "error.msinfo.badinfo"
            java.lang.String r10 = net.sourceforge.jtds.jdbc.Messages.get(r1, r10)
            java.lang.String r1 = "HY000"
            r0.<init>(r10, r1)
            throw r0
        L_0x0088:
            if (r2 == 0) goto L_0x008d
            r2.close()
        L_0x008d:
            goto L_0x008f
        L_0x008e:
            throw r10
        L_0x008f:
            goto L_0x008e
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.MSSqlServerInfo.<init>(java.lang.String):void");
    }

    public int getPortForInstance(String str) throws SQLException {
        if (this.serverInfoStrings == null) {
            return -1;
        }
        if (str == null || str.length() == 0) {
            str = "MSSQLSERVER";
        }
        int i = 0;
        String str2 = null;
        String str3 = null;
        while (true) {
            String[] strArr = this.serverInfoStrings;
            if (i >= strArr.length) {
                return -1;
            }
            if (strArr[i].length() == 0) {
                str2 = null;
                str3 = null;
            } else {
                String[] strArr2 = this.serverInfoStrings;
                String str4 = strArr2[i];
                i++;
                String str5 = i < strArr2.length ? strArr2[i] : "";
                if ("InstanceName".equals(str4)) {
                    str2 = str5;
                }
                if ("tcp".equals(str4)) {
                    str3 = str5;
                }
                if (!(str2 == null || str3 == null || !str2.equalsIgnoreCase(str))) {
                    try {
                        return Integer.parseInt(str3);
                    } catch (NumberFormatException unused) {
                        throw new SQLException(Messages.get("error.msinfo.badport", (Object) str), "HY000");
                    }
                }
            }
            i++;
        }
    }

    private static final String extractString(byte[] bArr, int i) {
        return new String(bArr, 3, i - 3);
    }

    public static String[] split(String str, int i) {
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 != -1) {
            i3 = str.indexOf(i, i3 + 1);
            i4++;
        }
        String[] strArr = new String[i4];
        int indexOf = str.indexOf(i);
        int i5 = 0;
        while (true) {
            int i6 = i2 + 1;
            strArr[i2] = str.substring(i5, indexOf == -1 ? str.length() : indexOf);
            i5 = indexOf + 1;
            indexOf = str.indexOf(i, i5);
            if (i5 == 0) {
                return strArr;
            }
            i2 = i6;
        }
    }
}
