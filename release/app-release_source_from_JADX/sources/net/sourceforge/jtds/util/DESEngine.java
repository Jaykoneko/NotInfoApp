package net.sourceforge.jtds.util;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESEngine {
    protected static final int BLOCK_SIZE = 8;

    /* renamed from: cf */
    private Cipher f123cf;

    public String getAlgorithmName() {
        return "DES";
    }

    public int getBlockSize() {
        return 8;
    }

    public void reset() {
    }

    public DESEngine() {
    }

    public DESEngine(boolean z, byte[] bArr) {
        init(z, bArr);
    }

    public void init(boolean z, byte[] bArr) {
        try {
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(bArr));
            Cipher instance = Cipher.getInstance("DES/ECB/NoPadding");
            this.f123cf = instance;
            instance.init(z ? 1 : 2, generateSecret);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error initializing DESEngine", e);
        }
    }

    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        Cipher cipher = this.f123cf;
        if (cipher == null) {
            throw new IllegalStateException("DES engine not initialised");
        } else if (i + 8 > bArr.length) {
            throw new IllegalArgumentException("input buffer too short");
        } else if (i2 + 8 <= bArr2.length) {
            try {
                return cipher.doFinal(bArr, i, 8, bArr2, i2);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException("Error processing data block in DESEngine", e);
            }
        } else {
            throw new IllegalArgumentException("output buffer too short");
        }
    }
}
