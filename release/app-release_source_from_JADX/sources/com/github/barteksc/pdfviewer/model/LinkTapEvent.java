package com.github.barteksc.pdfviewer.model;

import android.graphics.RectF;
import com.shockwave.pdfium.PdfDocument.Link;

public class LinkTapEvent {
    private float documentX;
    private float documentY;
    private Link link;
    private RectF mappedLinkRect;
    private float originalX;
    private float originalY;

    public LinkTapEvent(float f, float f2, float f3, float f4, RectF rectF, Link link2) {
        this.originalX = f;
        this.originalY = f2;
        this.documentX = f3;
        this.documentY = f4;
        this.mappedLinkRect = rectF;
        this.link = link2;
    }

    public float getOriginalX() {
        return this.originalX;
    }

    public float getOriginalY() {
        return this.originalY;
    }

    public float getDocumentX() {
        return this.documentX;
    }

    public float getDocumentY() {
        return this.documentY;
    }

    public RectF getMappedLinkRect() {
        return this.mappedLinkRect;
    }

    public Link getLink() {
        return this.link;
    }
}
