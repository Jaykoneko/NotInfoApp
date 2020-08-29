package com.bumptech.glide.gifdecoder;

class GifFrame {
    static final int DISPOSAL_BACKGROUND = 2;
    static final int DISPOSAL_NONE = 1;
    static final int DISPOSAL_PREVIOUS = 3;
    static final int DISPOSAL_UNSPECIFIED = 0;
    int bufferFrameStart;
    int delay;
    int dispose;

    /* renamed from: ih */
    int f48ih;
    boolean interlace;

    /* renamed from: iw */
    int f49iw;

    /* renamed from: ix */
    int f50ix;

    /* renamed from: iy */
    int f51iy;
    int[] lct;
    int transIndex;
    boolean transparency;

    GifFrame() {
    }
}
