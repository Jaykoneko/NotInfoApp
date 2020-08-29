package net.sourceforge.jtds.jdbc;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import net.sourceforge.jtds.util.DESEngine;
import net.sourceforge.jtds.util.MD4Digest;
import net.sourceforge.jtds.util.MD5Digest;

public class NtlmAuth {
    public static byte[] answerNtChallenge(String str, byte[] bArr) throws UnsupportedEncodingException {
        return encryptNonce(ntHash(str), bArr);
    }

    public static byte[] answerLmChallenge(String str, byte[] bArr) throws UnsupportedEncodingException {
        byte[] convertPassword = convertPassword(str);
        DESEngine dESEngine = new DESEngine(true, makeDESkey(convertPassword, 0));
        DESEngine dESEngine2 = new DESEngine(true, makeDESkey(convertPassword, 7));
        byte[] bArr2 = new byte[21];
        Arrays.fill(bArr2, 0);
        dESEngine.processBlock(bArr, 0, bArr2, 0);
        dESEngine2.processBlock(bArr, 0, bArr2, 8);
        return encryptNonce(bArr2, bArr);
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3) throws UnsupportedEncodingException {
        return answerNtlmv2Challenge(str, str2, str3, bArr, bArr2, bArr3, System.currentTimeMillis());
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) throws UnsupportedEncodingException {
        return lmv2Response(ntv2Hash(str, str2, str3), createBlob(bArr2, bArr3, bArr4), bArr);
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, long j) throws UnsupportedEncodingException {
        return answerNtlmv2Challenge(str, str2, str3, bArr, bArr2, bArr3, createTimestamp(j));
    }

    public static byte[] answerLmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2) throws UnsupportedEncodingException {
        return lmv2Response(ntv2Hash(str, str2, str3), bArr2, bArr);
    }

    private static byte[] ntv2Hash(String str, String str2, String str3) throws UnsupportedEncodingException {
        byte[] ntHash = ntHash(str3);
        StringBuilder sb = new StringBuilder();
        sb.append(str2.toUpperCase());
        sb.append(str.toUpperCase());
        return hmacMD5(sb.toString().getBytes("UnicodeLittleUnmarked"), ntHash);
    }

    private static byte[] lmv2Response(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[(bArr3.length + bArr2.length)];
        System.arraycopy(bArr3, 0, bArr4, 0, bArr3.length);
        System.arraycopy(bArr2, 0, bArr4, bArr3.length, bArr2.length);
        byte[] hmacMD5 = hmacMD5(bArr4, bArr);
        byte[] bArr5 = new byte[(hmacMD5.length + bArr2.length)];
        System.arraycopy(hmacMD5, 0, bArr5, 0, hmacMD5.length);
        System.arraycopy(bArr2, 0, bArr5, hmacMD5.length, bArr2.length);
        return bArr5;
    }

    private static byte[] hmacMD5(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[64];
        byte[] bArr4 = new byte[64];
        for (int i = 0; i < 64; i++) {
            bArr3[i] = 54;
            bArr4[i] = 92;
        }
        for (int length = bArr2.length - 1; length >= 0; length--) {
            bArr3[length] = (byte) (bArr3[length] ^ bArr2[length]);
            bArr4[length] = (byte) (bArr4[length] ^ bArr2[length]);
        }
        byte[] bArr5 = new byte[(bArr.length + 64)];
        System.arraycopy(bArr3, 0, bArr5, 0, 64);
        System.arraycopy(bArr, 0, bArr5, 64, bArr.length);
        byte[] md5 = md5(bArr5);
        byte[] bArr6 = new byte[(md5.length + 64)];
        System.arraycopy(bArr4, 0, bArr6, 0, 64);
        System.arraycopy(md5, 0, bArr6, 64, md5.length);
        return md5(bArr6);
    }

    private static byte[] md5(byte[] bArr) {
        MD5Digest mD5Digest = new MD5Digest();
        mD5Digest.update(bArr, 0, bArr.length);
        byte[] bArr2 = new byte[16];
        mD5Digest.doFinal(bArr2, 0);
        return bArr2;
    }

    public static byte[] createTimestamp(long j) {
        long j2 = (j + 11644473600000L) * 10000;
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) ((int) j2);
            j2 >>>= 8;
        }
        return bArr;
    }

    private static byte[] createBlob(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = {0, 0, 0, 0};
        byte[] bArr5 = {0, 0, 0, 0};
        byte[] bArr6 = {0, 0, 0, 0};
        byte[] bArr7 = new byte[(bArr3.length + 8 + bArr2.length + 4 + bArr.length + 4)];
        System.arraycopy(new byte[]{1, 1, 0, 0}, 0, bArr7, 0, 4);
        System.arraycopy(bArr4, 0, bArr7, 4, 4);
        System.arraycopy(bArr3, 0, bArr7, 8, bArr3.length);
        int length = 8 + bArr3.length;
        System.arraycopy(bArr2, 0, bArr7, length, bArr2.length);
        int length2 = length + bArr2.length;
        System.arraycopy(bArr5, 0, bArr7, length2, 4);
        int i = length2 + 4;
        System.arraycopy(bArr, 0, bArr7, i, bArr.length);
        System.arraycopy(bArr6, 0, bArr7, i + bArr.length, 4);
        return bArr7;
    }

    private static byte[] encryptNonce(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[24];
        DESEngine dESEngine = new DESEngine(true, makeDESkey(bArr, 0));
        DESEngine dESEngine2 = new DESEngine(true, makeDESkey(bArr, 7));
        DESEngine dESEngine3 = new DESEngine(true, makeDESkey(bArr, 14));
        dESEngine.processBlock(bArr2, 0, bArr3, 0);
        dESEngine2.processBlock(bArr2, 0, bArr3, 8);
        dESEngine3.processBlock(bArr2, 0, bArr3, 16);
        return bArr3;
    }

    private static byte[] ntHash(String str) throws UnsupportedEncodingException {
        byte[] bArr = new byte[21];
        Arrays.fill(bArr, 0);
        byte[] bytes = str.getBytes("UnicodeLittleUnmarked");
        MD4Digest mD4Digest = new MD4Digest();
        mD4Digest.update(bytes, 0, bytes.length);
        mD4Digest.doFinal(bArr, 0);
        return bArr;
    }

    private static byte[] convertPassword(String str) throws UnsupportedEncodingException {
        byte[] bytes = str.toUpperCase().getBytes("UTF8");
        int i = 14;
        byte[] bArr = new byte[14];
        Arrays.fill(bArr, 0);
        if (bytes.length <= 14) {
            i = bytes.length;
        }
        System.arraycopy(bytes, 0, bArr, 0, i);
        return bArr;
    }

    private static byte[] makeDESkey(byte[] bArr, int i) {
        int i2 = i + 0;
        int i3 = i + 1;
        int i4 = i + 2;
        int i5 = i + 3;
        int i6 = i + 4;
        int i7 = i + 5;
        int i8 = i + 6;
        byte[] bArr2 = {(byte) ((bArr[i2] >> 1) & 255), (byte) ((((bArr[i2] & 1) << 6) | (((bArr[i3] & 255) >> 2) & 255)) & 255), (byte) ((((bArr[i3] & 3) << 5) | (((bArr[i4] & 255) >> 3) & 255)) & 255), (byte) ((((bArr[i4] & 7) << 4) | (((bArr[i5] & 255) >> 4) & 255)) & 255), (byte) ((((bArr[i5] & TdsCore.SYBQUERY_PKT) << 3) | (((bArr[i6] & 255) >> 5) & 255)) & 255), (byte) ((((bArr[i6] & 31) << 2) | (((bArr[i7] & 255) >> 6) & 255)) & 255), (byte) ((((bArr[i7] & 63) << 1) | (((bArr[i8] & 255) >> 7) & 255)) & 255), (byte) (bArr[i8] & Byte.MAX_VALUE)};
        for (int i9 = 0; i9 < 8; i9++) {
            bArr2[i9] = (byte) (bArr2[i9] << 1);
        }
        return bArr2;
    }
}
