package com.shockwave.pdfium.util;

public class SizeF {
    private final float height;
    private final float width;

    public SizeF(float f, float f2) {
        this.width = f;
        this.height = f2;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SizeF) {
            SizeF sizeF = (SizeF) obj;
            if (this.width == sizeF.width && this.height == sizeF.height) {
                z = true;
            }
        }
        return z;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.width);
        sb.append("x");
        sb.append(this.height);
        return sb.toString();
    }

    public int hashCode() {
        return Float.floatToIntBits(this.width) ^ Float.floatToIntBits(this.height);
    }

    public Size toSize() {
        return new Size((int) this.width, (int) this.height);
    }
}
