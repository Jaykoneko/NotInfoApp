package net.sourceforge.jtds.util;

import net.sourceforge.jtds.jdbc.TdsCore;

public class MD4Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 16;
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;

    /* renamed from: H1 */
    private int f124H1;

    /* renamed from: H2 */
    private int f125H2;

    /* renamed from: H3 */
    private int f126H3;

    /* renamed from: H4 */
    private int f127H4;

    /* renamed from: X */
    private int[] f128X;
    private int xOff;

    /* renamed from: F */
    private int m17F(int i, int i2, int i3) {
        return ((i ^ -1) & i3) | (i2 & i);
    }

    /* renamed from: G */
    private int m18G(int i, int i2, int i3) {
        return (i & i3) | (i & i2) | (i2 & i3);
    }

    /* renamed from: H */
    private int m19H(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int rotateLeft(int i, int i2) {
        return (i >>> (32 - i2)) | (i << i2);
    }

    public String getAlgorithmName() {
        return "MD4";
    }

    public int getDigestSize() {
        return 16;
    }

    public MD4Digest() {
        this.f128X = new int[16];
        reset();
    }

    public MD4Digest(MD4Digest mD4Digest) {
        super(mD4Digest);
        int[] iArr = new int[16];
        this.f128X = iArr;
        this.f124H1 = mD4Digest.f124H1;
        this.f125H2 = mD4Digest.f125H2;
        this.f126H3 = mD4Digest.f126H3;
        this.f127H4 = mD4Digest.f127H4;
        int[] iArr2 = mD4Digest.f128X;
        System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
        this.xOff = mD4Digest.xOff;
    }

    /* access modifiers changed from: protected */
    public void processWord(byte[] bArr, int i) {
        int[] iArr = this.f128X;
        int i2 = this.xOff;
        int i3 = i2 + 1;
        this.xOff = i3;
        iArr[i2] = ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << TdsCore.MSLOGIN_PKT);
        if (i3 == 16) {
            processBlock();
        }
    }

    /* access modifiers changed from: protected */
    public void processLength(long j) {
        if (this.xOff > 14) {
            processBlock();
        }
        int[] iArr = this.f128X;
        iArr[14] = (int) (-1 & j);
        iArr[15] = (int) (j >>> 32);
    }

    private void unpackWord(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        bArr[i2 + 1] = (byte) (i >>> 8);
        bArr[i2 + 2] = (byte) (i >>> 16);
        bArr[i2 + 3] = (byte) (i >>> 24);
    }

    public int doFinal(byte[] bArr, int i) {
        finish();
        unpackWord(this.f124H1, bArr, i);
        unpackWord(this.f125H2, bArr, i + 4);
        unpackWord(this.f126H3, bArr, i + 8);
        unpackWord(this.f127H4, bArr, i + 12);
        reset();
        return 16;
    }

    public void reset() {
        super.reset();
        this.f124H1 = 1732584193;
        this.f125H2 = -271733879;
        this.f126H3 = -1732584194;
        this.f127H4 = 271733878;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f128X;
            if (i != iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void processBlock() {
        int i = this.f124H1;
        int i2 = this.f125H2;
        int i3 = this.f126H3;
        int i4 = this.f127H4;
        int rotateLeft = rotateLeft(i + m17F(i2, i3, i4) + this.f128X[0], 3);
        int rotateLeft2 = rotateLeft(i4 + m17F(rotateLeft, i2, i3) + this.f128X[1], 7);
        int rotateLeft3 = rotateLeft(i3 + m17F(rotateLeft2, rotateLeft, i2) + this.f128X[2], 11);
        int rotateLeft4 = rotateLeft(i2 + m17F(rotateLeft3, rotateLeft2, rotateLeft) + this.f128X[3], 19);
        int rotateLeft5 = rotateLeft(rotateLeft + m17F(rotateLeft4, rotateLeft3, rotateLeft2) + this.f128X[4], 3);
        int rotateLeft6 = rotateLeft(rotateLeft2 + m17F(rotateLeft5, rotateLeft4, rotateLeft3) + this.f128X[5], 7);
        int rotateLeft7 = rotateLeft(rotateLeft3 + m17F(rotateLeft6, rotateLeft5, rotateLeft4) + this.f128X[6], 11);
        int rotateLeft8 = rotateLeft(rotateLeft4 + m17F(rotateLeft7, rotateLeft6, rotateLeft5) + this.f128X[7], 19);
        int rotateLeft9 = rotateLeft(rotateLeft5 + m17F(rotateLeft8, rotateLeft7, rotateLeft6) + this.f128X[8], 3);
        int rotateLeft10 = rotateLeft(rotateLeft6 + m17F(rotateLeft9, rotateLeft8, rotateLeft7) + this.f128X[9], 7);
        int rotateLeft11 = rotateLeft(rotateLeft7 + m17F(rotateLeft10, rotateLeft9, rotateLeft8) + this.f128X[10], 11);
        int rotateLeft12 = rotateLeft(rotateLeft8 + m17F(rotateLeft11, rotateLeft10, rotateLeft9) + this.f128X[11], 19);
        int rotateLeft13 = rotateLeft(rotateLeft9 + m17F(rotateLeft12, rotateLeft11, rotateLeft10) + this.f128X[12], 3);
        int rotateLeft14 = rotateLeft(rotateLeft10 + m17F(rotateLeft13, rotateLeft12, rotateLeft11) + this.f128X[13], 7);
        int rotateLeft15 = rotateLeft(rotateLeft11 + m17F(rotateLeft14, rotateLeft13, rotateLeft12) + this.f128X[14], 11);
        int rotateLeft16 = rotateLeft(rotateLeft12 + m17F(rotateLeft15, rotateLeft14, rotateLeft13) + this.f128X[15], 19);
        int rotateLeft17 = rotateLeft(rotateLeft13 + m18G(rotateLeft16, rotateLeft15, rotateLeft14) + this.f128X[0] + 1518500249, 3);
        int rotateLeft18 = rotateLeft(rotateLeft14 + m18G(rotateLeft17, rotateLeft16, rotateLeft15) + this.f128X[4] + 1518500249, 5);
        int rotateLeft19 = rotateLeft(rotateLeft15 + m18G(rotateLeft18, rotateLeft17, rotateLeft16) + this.f128X[8] + 1518500249, 9);
        int rotateLeft20 = rotateLeft(rotateLeft16 + m18G(rotateLeft19, rotateLeft18, rotateLeft17) + this.f128X[12] + 1518500249, 13);
        int rotateLeft21 = rotateLeft(rotateLeft17 + m18G(rotateLeft20, rotateLeft19, rotateLeft18) + this.f128X[1] + 1518500249, 3);
        int rotateLeft22 = rotateLeft(rotateLeft18 + m18G(rotateLeft21, rotateLeft20, rotateLeft19) + this.f128X[5] + 1518500249, 5);
        int rotateLeft23 = rotateLeft(rotateLeft19 + m18G(rotateLeft22, rotateLeft21, rotateLeft20) + this.f128X[9] + 1518500249, 9);
        int rotateLeft24 = rotateLeft(rotateLeft20 + m18G(rotateLeft23, rotateLeft22, rotateLeft21) + this.f128X[13] + 1518500249, 13);
        int rotateLeft25 = rotateLeft(rotateLeft21 + m18G(rotateLeft24, rotateLeft23, rotateLeft22) + this.f128X[2] + 1518500249, 3);
        int rotateLeft26 = rotateLeft(rotateLeft22 + m18G(rotateLeft25, rotateLeft24, rotateLeft23) + this.f128X[6] + 1518500249, 5);
        int rotateLeft27 = rotateLeft(rotateLeft23 + m18G(rotateLeft26, rotateLeft25, rotateLeft24) + this.f128X[10] + 1518500249, 9);
        int rotateLeft28 = rotateLeft(rotateLeft24 + m18G(rotateLeft27, rotateLeft26, rotateLeft25) + this.f128X[14] + 1518500249, 13);
        int rotateLeft29 = rotateLeft(rotateLeft25 + m18G(rotateLeft28, rotateLeft27, rotateLeft26) + this.f128X[3] + 1518500249, 3);
        int rotateLeft30 = rotateLeft(rotateLeft26 + m18G(rotateLeft29, rotateLeft28, rotateLeft27) + this.f128X[7] + 1518500249, 5);
        int rotateLeft31 = rotateLeft(rotateLeft27 + m18G(rotateLeft30, rotateLeft29, rotateLeft28) + this.f128X[11] + 1518500249, 9);
        int rotateLeft32 = rotateLeft(rotateLeft28 + m18G(rotateLeft31, rotateLeft30, rotateLeft29) + this.f128X[15] + 1518500249, 13);
        int rotateLeft33 = rotateLeft(rotateLeft29 + m19H(rotateLeft32, rotateLeft31, rotateLeft30) + this.f128X[0] + 1859775393, 3);
        int rotateLeft34 = rotateLeft(rotateLeft30 + m19H(rotateLeft33, rotateLeft32, rotateLeft31) + this.f128X[8] + 1859775393, 9);
        int rotateLeft35 = rotateLeft(rotateLeft31 + m19H(rotateLeft34, rotateLeft33, rotateLeft32) + this.f128X[4] + 1859775393, 11);
        int rotateLeft36 = rotateLeft(rotateLeft32 + m19H(rotateLeft35, rotateLeft34, rotateLeft33) + this.f128X[12] + 1859775393, 15);
        int rotateLeft37 = rotateLeft(rotateLeft33 + m19H(rotateLeft36, rotateLeft35, rotateLeft34) + this.f128X[2] + 1859775393, 3);
        int rotateLeft38 = rotateLeft(rotateLeft34 + m19H(rotateLeft37, rotateLeft36, rotateLeft35) + this.f128X[10] + 1859775393, 9);
        int rotateLeft39 = rotateLeft(rotateLeft35 + m19H(rotateLeft38, rotateLeft37, rotateLeft36) + this.f128X[6] + 1859775393, 11);
        int rotateLeft40 = rotateLeft(rotateLeft36 + m19H(rotateLeft39, rotateLeft38, rotateLeft37) + this.f128X[14] + 1859775393, 15);
        int rotateLeft41 = rotateLeft(rotateLeft37 + m19H(rotateLeft40, rotateLeft39, rotateLeft38) + this.f128X[1] + 1859775393, 3);
        int rotateLeft42 = rotateLeft(rotateLeft38 + m19H(rotateLeft41, rotateLeft40, rotateLeft39) + this.f128X[9] + 1859775393, 9);
        int rotateLeft43 = rotateLeft(rotateLeft39 + m19H(rotateLeft42, rotateLeft41, rotateLeft40) + this.f128X[5] + 1859775393, 11);
        int rotateLeft44 = rotateLeft(rotateLeft40 + m19H(rotateLeft43, rotateLeft42, rotateLeft41) + this.f128X[13] + 1859775393, 15);
        int rotateLeft45 = rotateLeft(rotateLeft41 + m19H(rotateLeft44, rotateLeft43, rotateLeft42) + this.f128X[3] + 1859775393, 3);
        int rotateLeft46 = rotateLeft(rotateLeft42 + m19H(rotateLeft45, rotateLeft44, rotateLeft43) + this.f128X[11] + 1859775393, 9);
        int rotateLeft47 = rotateLeft(rotateLeft43 + m19H(rotateLeft46, rotateLeft45, rotateLeft44) + this.f128X[7] + 1859775393, 11);
        int rotateLeft48 = rotateLeft(rotateLeft44 + m19H(rotateLeft47, rotateLeft46, rotateLeft45) + this.f128X[15] + 1859775393, 15);
        this.f124H1 += rotateLeft45;
        this.f125H2 += rotateLeft48;
        this.f126H3 += rotateLeft47;
        this.f127H4 += rotateLeft46;
        this.xOff = 0;
        int i5 = 0;
        while (true) {
            int[] iArr = this.f128X;
            if (i5 != iArr.length) {
                iArr[i5] = 0;
                i5++;
            } else {
                return;
            }
        }
    }
}
