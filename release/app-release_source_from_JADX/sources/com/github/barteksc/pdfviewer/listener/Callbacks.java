package com.github.barteksc.pdfviewer.listener;

import android.view.MotionEvent;
import com.github.barteksc.pdfviewer.link.LinkHandler;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;

public class Callbacks {
    private LinkHandler linkHandler;
    private OnDrawListener onDrawAllListener;
    private OnDrawListener onDrawListener;
    private OnErrorListener onErrorListener;
    private OnLoadCompleteListener onLoadCompleteListener;
    private OnLongPressListener onLongPressListener;
    private OnPageChangeListener onPageChangeListener;
    private OnPageErrorListener onPageErrorListener;
    private OnPageScrollListener onPageScrollListener;
    private OnRenderListener onRenderListener;
    private OnTapListener onTapListener;

    public void setOnLoadComplete(OnLoadCompleteListener onLoadCompleteListener2) {
        this.onLoadCompleteListener = onLoadCompleteListener2;
    }

    public void callOnLoadComplete(int i) {
        OnLoadCompleteListener onLoadCompleteListener2 = this.onLoadCompleteListener;
        if (onLoadCompleteListener2 != null) {
            onLoadCompleteListener2.loadComplete(i);
        }
    }

    public void setOnError(OnErrorListener onErrorListener2) {
        this.onErrorListener = onErrorListener2;
    }

    public OnErrorListener getOnError() {
        return this.onErrorListener;
    }

    public void setOnPageError(OnPageErrorListener onPageErrorListener2) {
        this.onPageErrorListener = onPageErrorListener2;
    }

    public boolean callOnPageError(int i, Throwable th) {
        OnPageErrorListener onPageErrorListener2 = this.onPageErrorListener;
        if (onPageErrorListener2 == null) {
            return false;
        }
        onPageErrorListener2.onPageError(i, th);
        return true;
    }

    public void setOnRender(OnRenderListener onRenderListener2) {
        this.onRenderListener = onRenderListener2;
    }

    public void callOnRender(int i) {
        OnRenderListener onRenderListener2 = this.onRenderListener;
        if (onRenderListener2 != null) {
            onRenderListener2.onInitiallyRendered(i);
        }
    }

    public void setOnPageChange(OnPageChangeListener onPageChangeListener2) {
        this.onPageChangeListener = onPageChangeListener2;
    }

    public void callOnPageChange(int i, int i2) {
        OnPageChangeListener onPageChangeListener2 = this.onPageChangeListener;
        if (onPageChangeListener2 != null) {
            onPageChangeListener2.onPageChanged(i, i2);
        }
    }

    public void setOnPageScroll(OnPageScrollListener onPageScrollListener2) {
        this.onPageScrollListener = onPageScrollListener2;
    }

    public void callOnPageScroll(int i, float f) {
        OnPageScrollListener onPageScrollListener2 = this.onPageScrollListener;
        if (onPageScrollListener2 != null) {
            onPageScrollListener2.onPageScrolled(i, f);
        }
    }

    public void setOnDraw(OnDrawListener onDrawListener2) {
        this.onDrawListener = onDrawListener2;
    }

    public OnDrawListener getOnDraw() {
        return this.onDrawListener;
    }

    public void setOnDrawAll(OnDrawListener onDrawListener2) {
        this.onDrawAllListener = onDrawListener2;
    }

    public OnDrawListener getOnDrawAll() {
        return this.onDrawAllListener;
    }

    public void setOnTap(OnTapListener onTapListener2) {
        this.onTapListener = onTapListener2;
    }

    public boolean callOnTap(MotionEvent motionEvent) {
        OnTapListener onTapListener2 = this.onTapListener;
        return onTapListener2 != null && onTapListener2.onTap(motionEvent);
    }

    public void setOnLongPress(OnLongPressListener onLongPressListener2) {
        this.onLongPressListener = onLongPressListener2;
    }

    public void callOnLongPress(MotionEvent motionEvent) {
        OnLongPressListener onLongPressListener2 = this.onLongPressListener;
        if (onLongPressListener2 != null) {
            onLongPressListener2.onLongPress(motionEvent);
        }
    }

    public void setLinkHandler(LinkHandler linkHandler2) {
        this.linkHandler = linkHandler2;
    }

    public void callLinkHandler(LinkTapEvent linkTapEvent) {
        LinkHandler linkHandler2 = this.linkHandler;
        if (linkHandler2 != null) {
            linkHandler2.handleLinkEvent(linkTapEvent);
        }
    }
}
