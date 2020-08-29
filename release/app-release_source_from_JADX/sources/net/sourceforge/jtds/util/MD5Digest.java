package net.sourceforge.jtds.util;

import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class MD5Digest {
    private static final int DIGEST_LENGTH = 16;

    /* renamed from: md */
    MessageDigest f129md;

    public String getAlgorithmName() {
        return "MD5";
    }

    public int getDigestSize() {
        return 16;
    }

    public MD5Digest() {
        try {
            this.f129md = MessageDigest.getInstance("MD5");
            reset();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error initializing MD5Digest", e);
        }
    }

    public int doFinal(byte[] bArr, int i) {
        try {
            this.f129md.digest(bArr, i, 16);
            return 16;
        } catch (DigestException e) {
            throw new RuntimeException("Error processing data for MD5Digest", e);
        }
    }

    public void reset() {
        this.f129md.reset();
    }

    public void update(byte b) {
        this.f129md.update(b);
    }

    public void update(byte[] bArr, int i, int i2) {
        this.f129md.update(bArr, i, i2);
    }

    public void finish() {
        doFinal(new byte[16], 0);
    }
}
