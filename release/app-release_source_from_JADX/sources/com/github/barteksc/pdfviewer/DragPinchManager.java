package com.github.barteksc.pdfviewer;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import com.github.barteksc.pdfviewer.listener.Callbacks;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.Constants.Pinch;
import com.shockwave.pdfium.PdfDocument.Link;
import com.shockwave.pdfium.util.SizeF;

class DragPinchManager implements OnGestureListener, OnDoubleTapListener, OnScaleGestureListener, OnTouchListener {
    private AnimationManager animationManager;
    private boolean enabled = false;
    private GestureDetector gestureDetector;
    private PDFView pdfView;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean scaling = false;
    private boolean scrolling = false;

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    DragPinchManager(PDFView pDFView, AnimationManager animationManager2) {
        this.pdfView = pDFView;
        this.animationManager = animationManager2;
        this.gestureDetector = new GestureDetector(pDFView.getContext(), this);
        this.scaleGestureDetector = new ScaleGestureDetector(pDFView.getContext(), this);
        pDFView.setOnTouchListener(this);
    }

    /* access modifiers changed from: 0000 */
    public void enable() {
        this.enabled = true;
    }

    /* access modifiers changed from: 0000 */
    public void disable() {
        this.enabled = false;
    }

    /* access modifiers changed from: 0000 */
    public void disableLongpress() {
        this.gestureDetector.setIsLongpressEnabled(false);
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        boolean callOnTap = this.pdfView.callbacks.callOnTap(motionEvent);
        boolean checkLinkTapped = checkLinkTapped(motionEvent.getX(), motionEvent.getY());
        if (!callOnTap && !checkLinkTapped) {
            ScrollHandle scrollHandle = this.pdfView.getScrollHandle();
            if (scrollHandle != null && !this.pdfView.documentFitsView()) {
                if (!scrollHandle.shown()) {
                    scrollHandle.show();
                } else {
                    scrollHandle.hide();
                }
            }
        }
        this.pdfView.performClick();
        return true;
    }

    private boolean checkLinkTapped(float f, float f2) {
        int i;
        int i2;
        PdfFile pdfFile = this.pdfView.pdfFile;
        if (pdfFile == null) {
            return false;
        }
        float f3 = (-this.pdfView.getCurrentXOffset()) + f;
        float f4 = (-this.pdfView.getCurrentYOffset()) + f2;
        int pageAtOffset = pdfFile.getPageAtOffset(this.pdfView.isSwipeVertical() ? f4 : f3, this.pdfView.getZoom());
        SizeF scaledPageSize = pdfFile.getScaledPageSize(pageAtOffset, this.pdfView.getZoom());
        if (this.pdfView.isSwipeVertical()) {
            i2 = (int) pdfFile.getSecondaryPageOffset(pageAtOffset, this.pdfView.getZoom());
            i = (int) pdfFile.getPageOffset(pageAtOffset, this.pdfView.getZoom());
        } else {
            i = (int) pdfFile.getSecondaryPageOffset(pageAtOffset, this.pdfView.getZoom());
            i2 = (int) pdfFile.getPageOffset(pageAtOffset, this.pdfView.getZoom());
        }
        int i3 = i2;
        int i4 = i;
        for (Link link : pdfFile.getPageLinks(pageAtOffset)) {
            RectF mapRectToDevice = pdfFile.mapRectToDevice(pageAtOffset, i3, i4, (int) scaledPageSize.getWidth(), (int) scaledPageSize.getHeight(), link.getBounds());
            mapRectToDevice.sort();
            if (mapRectToDevice.contains(f3, f4)) {
                Callbacks callbacks = this.pdfView.callbacks;
                LinkTapEvent linkTapEvent = new LinkTapEvent(f, f2, f3, f4, mapRectToDevice, link);
                callbacks.callLinkHandler(linkTapEvent);
                return true;
            }
        }
        return false;
    }

    private void startPageFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float f3;
        float f4;
        if (checkDoPageFling(f, f2)) {
            int i = -1;
            if (!this.pdfView.isSwipeVertical() ? f <= 0.0f : f2 <= 0.0f) {
                i = 1;
            }
            if (this.pdfView.isSwipeVertical()) {
                f3 = motionEvent2.getY();
                f4 = motionEvent.getY();
            } else {
                f3 = motionEvent2.getX();
                f4 = motionEvent.getX();
            }
            float f5 = f3 - f4;
            int max = Math.max(0, Math.min(this.pdfView.getPageCount() - 1, this.pdfView.findFocusPage(this.pdfView.getCurrentXOffset() - (this.pdfView.getZoom() * f5), this.pdfView.getCurrentYOffset() - (f5 * this.pdfView.getZoom())) + i));
            this.animationManager.startPageFlingAnimation(-this.pdfView.snapOffsetForPage(max, this.pdfView.findSnapEdge(max)));
        }
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (!this.pdfView.isDoubletapEnabled()) {
            return false;
        }
        if (this.pdfView.getZoom() < this.pdfView.getMidZoom()) {
            this.pdfView.zoomWithAnimation(motionEvent.getX(), motionEvent.getY(), this.pdfView.getMidZoom());
        } else if (this.pdfView.getZoom() < this.pdfView.getMaxZoom()) {
            this.pdfView.zoomWithAnimation(motionEvent.getX(), motionEvent.getY(), this.pdfView.getMaxZoom());
        } else {
            this.pdfView.resetZoomWithAnimation();
        }
        return true;
    }

    public boolean onDown(MotionEvent motionEvent) {
        this.animationManager.stopFling();
        return true;
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.scrolling = true;
        if (this.pdfView.isZooming() || this.pdfView.isSwipeEnabled()) {
            this.pdfView.moveRelativeTo(-f, -f2);
        }
        if (!this.scaling || this.pdfView.doRenderDuringScale()) {
            this.pdfView.loadPageByOffset();
        }
        return true;
    }

    private void onScrollEnd(MotionEvent motionEvent) {
        this.pdfView.loadPages();
        hideHandle();
        if (!this.animationManager.isFlinging()) {
            this.pdfView.performPageSnap();
        }
    }

    public void onLongPress(MotionEvent motionEvent) {
        this.pdfView.callbacks.callOnLongPress(motionEvent);
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float f3;
        float f4;
        int i;
        if (!this.pdfView.isSwipeEnabled()) {
            return false;
        }
        if (this.pdfView.isPageFlingEnabled()) {
            if (this.pdfView.pageFillsScreen()) {
                onBoundedFling(f, f2);
            } else {
                startPageFling(motionEvent, motionEvent2, f, f2);
            }
            return true;
        }
        int currentXOffset = (int) this.pdfView.getCurrentXOffset();
        int currentYOffset = (int) this.pdfView.getCurrentYOffset();
        PdfFile pdfFile = this.pdfView.pdfFile;
        if (this.pdfView.isSwipeVertical()) {
            f3 = -(this.pdfView.toCurrentScale(pdfFile.getMaxPageWidth()) - ((float) this.pdfView.getWidth()));
            f4 = pdfFile.getDocLen(this.pdfView.getZoom());
            i = this.pdfView.getHeight();
        } else {
            f3 = -(pdfFile.getDocLen(this.pdfView.getZoom()) - ((float) this.pdfView.getWidth()));
            f4 = this.pdfView.toCurrentScale(pdfFile.getMaxPageHeight());
            i = this.pdfView.getHeight();
        }
        this.animationManager.startFlingAnimation(currentXOffset, currentYOffset, (int) f, (int) f2, (int) f3, 0, (int) (-(f4 - ((float) i))), 0);
        return true;
    }

    private void onBoundedFling(float f, float f2) {
        float f3;
        float f4;
        int currentXOffset = (int) this.pdfView.getCurrentXOffset();
        int currentYOffset = (int) this.pdfView.getCurrentYOffset();
        PdfFile pdfFile = this.pdfView.pdfFile;
        float f5 = -pdfFile.getPageOffset(this.pdfView.getCurrentPage(), this.pdfView.getZoom());
        float pageLength = f5 - pdfFile.getPageLength(this.pdfView.getCurrentPage(), this.pdfView.getZoom());
        float f6 = 0.0f;
        if (this.pdfView.isSwipeVertical()) {
            f4 = -(this.pdfView.toCurrentScale(pdfFile.getMaxPageWidth()) - ((float) this.pdfView.getWidth()));
            f3 = pageLength + ((float) this.pdfView.getHeight());
            f6 = f5;
            f5 = 0.0f;
        } else {
            float width = pageLength + ((float) this.pdfView.getWidth());
            f3 = -(this.pdfView.toCurrentScale(pdfFile.getMaxPageHeight()) - ((float) this.pdfView.getHeight()));
            f4 = width;
        }
        this.animationManager.startFlingAnimation(currentXOffset, currentYOffset, (int) f, (int) f2, (int) f4, (int) f5, (int) f3, (int) f6);
    }

    public boolean onScale(ScaleGestureDetector scaleGestureDetector2) {
        float scaleFactor = scaleGestureDetector2.getScaleFactor();
        float zoom = this.pdfView.getZoom() * scaleFactor;
        float min = Math.min(Pinch.MINIMUM_ZOOM, this.pdfView.getMinZoom());
        float min2 = Math.min(Pinch.MAXIMUM_ZOOM, this.pdfView.getMaxZoom());
        if (zoom < min) {
            scaleFactor = min / this.pdfView.getZoom();
        } else if (zoom > min2) {
            scaleFactor = min2 / this.pdfView.getZoom();
        }
        this.pdfView.zoomCenteredRelativeTo(scaleFactor, new PointF(scaleGestureDetector2.getFocusX(), scaleGestureDetector2.getFocusY()));
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector2) {
        this.scaling = true;
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector2) {
        this.pdfView.loadPages();
        hideHandle();
        this.scaling = false;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.enabled) {
            return false;
        }
        boolean z = this.gestureDetector.onTouchEvent(motionEvent) || this.scaleGestureDetector.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == 1 && this.scrolling) {
            this.scrolling = false;
            onScrollEnd(motionEvent);
        }
        return z;
    }

    private void hideHandle() {
        ScrollHandle scrollHandle = this.pdfView.getScrollHandle();
        if (scrollHandle != null && scrollHandle.shown()) {
            scrollHandle.hideDelayed();
        }
    }

    private boolean checkDoPageFling(float f, float f2) {
        float abs = Math.abs(f);
        float abs2 = Math.abs(f2);
        if (this.pdfView.isSwipeVertical()) {
            if (abs2 > abs) {
                return true;
            }
        } else if (abs > abs2) {
            return true;
        }
        return false;
    }
}
