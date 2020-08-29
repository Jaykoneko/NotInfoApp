package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import com.github.barteksc.pdfviewer.exception.PageRenderingException;
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler;
import com.github.barteksc.pdfviewer.link.LinkHandler;
import com.github.barteksc.pdfviewer.listener.Callbacks;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.model.PagePart;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.source.AssetSource;
import com.github.barteksc.pdfviewer.source.ByteArraySource;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.github.barteksc.pdfviewer.source.FileSource;
import com.github.barteksc.pdfviewer.source.InputStreamSource;
import com.github.barteksc.pdfviewer.source.UriSource;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.github.barteksc.pdfviewer.util.MathUtils;
import com.github.barteksc.pdfviewer.util.SnapEdge;
import com.github.barteksc.pdfviewer.util.Util;
import com.shockwave.pdfium.PdfDocument.Bookmark;
import com.shockwave.pdfium.PdfDocument.Link;
import com.shockwave.pdfium.PdfDocument.Meta;
import com.shockwave.pdfium.PdfiumCore;
import com.shockwave.pdfium.util.Size;
import com.shockwave.pdfium.util.SizeF;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDFView extends RelativeLayout {
    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final String TAG = PDFView.class.getSimpleName();
    private AnimationManager animationManager;
    private boolean annotationRendering = false;
    private PaintFlagsDrawFilter antialiasFilter = new PaintFlagsDrawFilter(0, 3);
    private boolean autoSpacing = false;
    private boolean bestQuality = false;
    CacheManager cacheManager;
    Callbacks callbacks = new Callbacks();
    private int currentPage;
    private float currentXOffset = 0.0f;
    private float currentYOffset = 0.0f;
    private Paint debugPaint;
    private DecodingAsyncTask decodingAsyncTask;
    private int defaultPage = 0;
    private boolean doubletapEnabled = true;
    /* access modifiers changed from: private */
    public DragPinchManager dragPinchManager;
    private boolean enableAntialiasing = true;
    private boolean enableSwipe = true;
    private boolean fitEachPage = false;
    /* access modifiers changed from: private */
    public boolean hasSize = false;
    private boolean isScrollHandleInit = false;
    private float maxZoom = 3.0f;
    private float midZoom = 1.75f;
    private float minZoom = 1.0f;
    private boolean nightMode = false;
    private List<Integer> onDrawPagesNums = new ArrayList(10);
    private FitPolicy pageFitPolicy = FitPolicy.WIDTH;
    private boolean pageFling = true;
    private boolean pageSnap = true;
    private PagesLoader pagesLoader;
    private Paint paint;
    PdfFile pdfFile;
    private PdfiumCore pdfiumCore;
    private boolean recycled = true;
    private boolean renderDuringScale = false;
    RenderingHandler renderingHandler;
    private HandlerThread renderingHandlerThread = new HandlerThread("PDF renderer");
    private ScrollDir scrollDir = ScrollDir.NONE;
    private ScrollHandle scrollHandle;
    private int spacingPx = 0;
    private State state = State.DEFAULT;
    private boolean swipeVertical = true;
    /* access modifiers changed from: private */
    public Configurator waitingDocumentConfigurator;
    private float zoom = 1.0f;

    public class Configurator {
        private boolean annotationRendering;
        private boolean antialiasing;
        private boolean autoSpacing;
        private int defaultPage;
        private final DocumentSource documentSource;
        private boolean enableDoubletap;
        private boolean enableSwipe;
        private boolean fitEachPage;
        private LinkHandler linkHandler;
        private boolean nightMode;
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
        private FitPolicy pageFitPolicy;
        private boolean pageFling;
        private int[] pageNumbers;
        private boolean pageSnap;
        private String password;
        private ScrollHandle scrollHandle;
        private int spacing;
        private boolean swipeHorizontal;

        private Configurator(DocumentSource documentSource2) {
            this.pageNumbers = null;
            this.enableSwipe = true;
            this.enableDoubletap = true;
            this.linkHandler = new DefaultLinkHandler(PDFView.this);
            this.defaultPage = 0;
            this.swipeHorizontal = false;
            this.annotationRendering = false;
            this.password = null;
            this.scrollHandle = null;
            this.antialiasing = true;
            this.spacing = 0;
            this.autoSpacing = false;
            this.pageFitPolicy = FitPolicy.WIDTH;
            this.fitEachPage = false;
            this.pageFling = false;
            this.pageSnap = false;
            this.nightMode = false;
            this.documentSource = documentSource2;
        }

        public Configurator pages(int... iArr) {
            this.pageNumbers = iArr;
            return this;
        }

        public Configurator enableSwipe(boolean z) {
            this.enableSwipe = z;
            return this;
        }

        public Configurator enableDoubletap(boolean z) {
            this.enableDoubletap = z;
            return this;
        }

        public Configurator enableAnnotationRendering(boolean z) {
            this.annotationRendering = z;
            return this;
        }

        public Configurator onDraw(OnDrawListener onDrawListener2) {
            this.onDrawListener = onDrawListener2;
            return this;
        }

        public Configurator onDrawAll(OnDrawListener onDrawListener2) {
            this.onDrawAllListener = onDrawListener2;
            return this;
        }

        public Configurator onLoad(OnLoadCompleteListener onLoadCompleteListener2) {
            this.onLoadCompleteListener = onLoadCompleteListener2;
            return this;
        }

        public Configurator onPageScroll(OnPageScrollListener onPageScrollListener2) {
            this.onPageScrollListener = onPageScrollListener2;
            return this;
        }

        public Configurator onError(OnErrorListener onErrorListener2) {
            this.onErrorListener = onErrorListener2;
            return this;
        }

        public Configurator onPageError(OnPageErrorListener onPageErrorListener2) {
            this.onPageErrorListener = onPageErrorListener2;
            return this;
        }

        public Configurator onPageChange(OnPageChangeListener onPageChangeListener2) {
            this.onPageChangeListener = onPageChangeListener2;
            return this;
        }

        public Configurator onRender(OnRenderListener onRenderListener2) {
            this.onRenderListener = onRenderListener2;
            return this;
        }

        public Configurator onTap(OnTapListener onTapListener2) {
            this.onTapListener = onTapListener2;
            return this;
        }

        public Configurator onLongPress(OnLongPressListener onLongPressListener2) {
            this.onLongPressListener = onLongPressListener2;
            return this;
        }

        public Configurator linkHandler(LinkHandler linkHandler2) {
            this.linkHandler = linkHandler2;
            return this;
        }

        public Configurator defaultPage(int i) {
            this.defaultPage = i;
            return this;
        }

        public Configurator swipeHorizontal(boolean z) {
            this.swipeHorizontal = z;
            return this;
        }

        public Configurator password(String str) {
            this.password = str;
            return this;
        }

        public Configurator scrollHandle(ScrollHandle scrollHandle2) {
            this.scrollHandle = scrollHandle2;
            return this;
        }

        public Configurator enableAntialiasing(boolean z) {
            this.antialiasing = z;
            return this;
        }

        public Configurator spacing(int i) {
            this.spacing = i;
            return this;
        }

        public Configurator autoSpacing(boolean z) {
            this.autoSpacing = z;
            return this;
        }

        public Configurator pageFitPolicy(FitPolicy fitPolicy) {
            this.pageFitPolicy = fitPolicy;
            return this;
        }

        public Configurator fitEachPage(boolean z) {
            this.fitEachPage = z;
            return this;
        }

        public Configurator pageSnap(boolean z) {
            this.pageSnap = z;
            return this;
        }

        public Configurator pageFling(boolean z) {
            this.pageFling = z;
            return this;
        }

        public Configurator nightMode(boolean z) {
            this.nightMode = z;
            return this;
        }

        public Configurator disableLongpress() {
            PDFView.this.dragPinchManager.disableLongpress();
            return this;
        }

        public void load() {
            if (!PDFView.this.hasSize) {
                PDFView.this.waitingDocumentConfigurator = this;
                return;
            }
            PDFView.this.recycle();
            PDFView.this.callbacks.setOnLoadComplete(this.onLoadCompleteListener);
            PDFView.this.callbacks.setOnError(this.onErrorListener);
            PDFView.this.callbacks.setOnDraw(this.onDrawListener);
            PDFView.this.callbacks.setOnDrawAll(this.onDrawAllListener);
            PDFView.this.callbacks.setOnPageChange(this.onPageChangeListener);
            PDFView.this.callbacks.setOnPageScroll(this.onPageScrollListener);
            PDFView.this.callbacks.setOnRender(this.onRenderListener);
            PDFView.this.callbacks.setOnTap(this.onTapListener);
            PDFView.this.callbacks.setOnLongPress(this.onLongPressListener);
            PDFView.this.callbacks.setOnPageError(this.onPageErrorListener);
            PDFView.this.callbacks.setLinkHandler(this.linkHandler);
            PDFView.this.setSwipeEnabled(this.enableSwipe);
            PDFView.this.setNightMode(this.nightMode);
            PDFView.this.enableDoubletap(this.enableDoubletap);
            PDFView.this.setDefaultPage(this.defaultPage);
            PDFView.this.setSwipeVertical(!this.swipeHorizontal);
            PDFView.this.enableAnnotationRendering(this.annotationRendering);
            PDFView.this.setScrollHandle(this.scrollHandle);
            PDFView.this.enableAntialiasing(this.antialiasing);
            PDFView.this.setSpacing(this.spacing);
            PDFView.this.setAutoSpacing(this.autoSpacing);
            PDFView.this.setPageFitPolicy(this.pageFitPolicy);
            PDFView.this.setFitEachPage(this.fitEachPage);
            PDFView.this.setPageSnap(this.pageSnap);
            PDFView.this.setPageFling(this.pageFling);
            int[] iArr = this.pageNumbers;
            if (iArr != null) {
                PDFView.this.load(this.documentSource, this.password, iArr);
            } else {
                PDFView.this.load(this.documentSource, this.password);
            }
        }
    }

    enum ScrollDir {
        NONE,
        START,
        END
    }

    private enum State {
        DEFAULT,
        LOADED,
        SHOWN,
        ERROR
    }

    /* access modifiers changed from: 0000 */
    public ScrollHandle getScrollHandle() {
        return this.scrollHandle;
    }

    public PDFView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            this.cacheManager = new CacheManager();
            this.animationManager = new AnimationManager(this);
            this.dragPinchManager = new DragPinchManager(this, this.animationManager);
            this.pagesLoader = new PagesLoader(this);
            this.paint = new Paint();
            Paint paint2 = new Paint();
            this.debugPaint = paint2;
            paint2.setStyle(Style.STROKE);
            this.pdfiumCore = new PdfiumCore(context);
            setWillNotDraw(false);
        }
    }

    /* access modifiers changed from: private */
    public void load(DocumentSource documentSource, String str) {
        load(documentSource, str, null);
    }

    /* access modifiers changed from: private */
    public void load(DocumentSource documentSource, String str, int[] iArr) {
        if (this.recycled) {
            this.recycled = false;
            DecodingAsyncTask decodingAsyncTask2 = new DecodingAsyncTask(documentSource, str, iArr, this, this.pdfiumCore);
            this.decodingAsyncTask = decodingAsyncTask2;
            decodingAsyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return;
        }
        throw new IllegalStateException("Don't call load on a PDF View without recycling it first.");
    }

    public void jumpTo(int i, boolean z) {
        float f;
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 != null) {
            int determineValidPageNumberFrom = pdfFile2.determineValidPageNumberFrom(i);
            if (determineValidPageNumberFrom == 0) {
                f = 0.0f;
            } else {
                f = -this.pdfFile.getPageOffset(determineValidPageNumberFrom, this.zoom);
            }
            if (this.swipeVertical) {
                if (z) {
                    this.animationManager.startYAnimation(this.currentYOffset, f);
                } else {
                    moveTo(this.currentXOffset, f);
                }
            } else if (z) {
                this.animationManager.startXAnimation(this.currentXOffset, f);
            } else {
                moveTo(f, this.currentYOffset);
            }
            showPage(determineValidPageNumberFrom);
        }
    }

    public void jumpTo(int i) {
        jumpTo(i, false);
    }

    /* access modifiers changed from: 0000 */
    public void showPage(int i) {
        if (!this.recycled) {
            this.currentPage = this.pdfFile.determineValidPageNumberFrom(i);
            loadPages();
            if (this.scrollHandle != null && !documentFitsView()) {
                this.scrollHandle.setPageNum(this.currentPage + 1);
            }
            this.callbacks.callOnPageChange(this.currentPage, this.pdfFile.getPagesCount());
        }
    }

    public float getPositionOffset() {
        int i;
        float f;
        float f2;
        if (this.swipeVertical) {
            f2 = -this.currentYOffset;
            f = this.pdfFile.getDocLen(this.zoom);
            i = getHeight();
        } else {
            f2 = -this.currentXOffset;
            f = this.pdfFile.getDocLen(this.zoom);
            i = getWidth();
        }
        return MathUtils.limit(f2 / (f - ((float) i)), 0.0f, 1.0f);
    }

    public void setPositionOffset(float f, boolean z) {
        if (this.swipeVertical) {
            moveTo(this.currentXOffset, ((-this.pdfFile.getDocLen(this.zoom)) + ((float) getHeight())) * f, z);
        } else {
            moveTo(((-this.pdfFile.getDocLen(this.zoom)) + ((float) getWidth())) * f, this.currentYOffset, z);
        }
        loadPageByOffset();
    }

    public void setPositionOffset(float f) {
        setPositionOffset(f, true);
    }

    public void stopFling() {
        this.animationManager.stopFling();
    }

    public int getPageCount() {
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 == null) {
            return 0;
        }
        return pdfFile2.getPagesCount();
    }

    public void setSwipeEnabled(boolean z) {
        this.enableSwipe = z;
    }

    public void setNightMode(boolean z) {
        this.nightMode = z;
        if (z) {
            this.paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f})));
            return;
        }
        this.paint.setColorFilter(null);
    }

    /* access modifiers changed from: 0000 */
    public void enableDoubletap(boolean z) {
        this.doubletapEnabled = z;
    }

    /* access modifiers changed from: 0000 */
    public boolean isDoubletapEnabled() {
        return this.doubletapEnabled;
    }

    /* access modifiers changed from: 0000 */
    public void onPageError(PageRenderingException pageRenderingException) {
        if (!this.callbacks.callOnPageError(pageRenderingException.getPage(), pageRenderingException.getCause())) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot open page ");
            sb.append(pageRenderingException.getPage());
            Log.e(str, sb.toString(), pageRenderingException.getCause());
        }
    }

    public void recycle() {
        this.waitingDocumentConfigurator = null;
        this.animationManager.stopAll();
        this.dragPinchManager.disable();
        RenderingHandler renderingHandler2 = this.renderingHandler;
        if (renderingHandler2 != null) {
            renderingHandler2.stop();
            this.renderingHandler.removeMessages(1);
        }
        DecodingAsyncTask decodingAsyncTask2 = this.decodingAsyncTask;
        if (decodingAsyncTask2 != null) {
            decodingAsyncTask2.cancel(true);
        }
        this.cacheManager.recycle();
        ScrollHandle scrollHandle2 = this.scrollHandle;
        if (scrollHandle2 != null && this.isScrollHandleInit) {
            scrollHandle2.destroyLayout();
        }
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 != null) {
            pdfFile2.dispose();
            this.pdfFile = null;
        }
        this.renderingHandler = null;
        this.scrollHandle = null;
        this.isScrollHandleInit = false;
        this.currentYOffset = 0.0f;
        this.currentXOffset = 0.0f;
        this.zoom = 1.0f;
        this.recycled = true;
        this.callbacks = new Callbacks();
        this.state = State.DEFAULT;
    }

    public boolean isRecycled() {
        return this.recycled;
    }

    public void computeScroll() {
        super.computeScroll();
        if (!isInEditMode()) {
            this.animationManager.computeFling();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        recycle();
        if (this.renderingHandlerThread != null) {
            if (VERSION.SDK_INT >= 18) {
                this.renderingHandlerThread.quitSafely();
            } else {
                this.renderingHandlerThread.quit();
            }
            this.renderingHandlerThread = null;
        }
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        float f;
        float f2;
        this.hasSize = true;
        Configurator configurator = this.waitingDocumentConfigurator;
        if (configurator != null) {
            configurator.load();
        }
        if (!isInEditMode() && this.state == State.SHOWN) {
            float f3 = (-this.currentXOffset) + (((float) i3) * 0.5f);
            float f4 = (-this.currentYOffset) + (((float) i4) * 0.5f);
            if (this.swipeVertical) {
                f2 = f3 / this.pdfFile.getMaxPageWidth();
                f = this.pdfFile.getDocLen(this.zoom);
            } else {
                f2 = f3 / this.pdfFile.getDocLen(this.zoom);
                f = this.pdfFile.getMaxPageHeight();
            }
            float f5 = f4 / f;
            this.animationManager.stopAll();
            this.pdfFile.recalculatePageSizes(new Size(i, i2));
            if (this.swipeVertical) {
                this.currentXOffset = ((-f2) * this.pdfFile.getMaxPageWidth()) + (((float) i) * 0.5f);
                this.currentYOffset = ((-f5) * this.pdfFile.getDocLen(this.zoom)) + (((float) i2) * 0.5f);
            } else {
                this.currentXOffset = ((-f2) * this.pdfFile.getDocLen(this.zoom)) + (((float) i) * 0.5f);
                this.currentYOffset = ((-f5) * this.pdfFile.getMaxPageHeight()) + (((float) i2) * 0.5f);
            }
            moveTo(this.currentXOffset, this.currentYOffset);
            loadPageByOffset();
        }
    }

    public boolean canScrollHorizontally(int i) {
        if (this.pdfFile == null) {
            return true;
        }
        if (this.swipeVertical) {
            if (i < 0 && this.currentXOffset < 0.0f) {
                return true;
            }
            if (i > 0 && this.currentXOffset + toCurrentScale(this.pdfFile.getMaxPageWidth()) > ((float) getWidth())) {
                return true;
            }
        } else if (i < 0 && this.currentXOffset < 0.0f) {
            return true;
        } else {
            if (i > 0 && this.currentXOffset + this.pdfFile.getDocLen(this.zoom) > ((float) getWidth())) {
                return true;
            }
        }
        return false;
    }

    public boolean canScrollVertically(int i) {
        if (this.pdfFile == null) {
            return true;
        }
        if (this.swipeVertical) {
            if (i < 0 && this.currentYOffset < 0.0f) {
                return true;
            }
            if (i > 0 && this.currentYOffset + this.pdfFile.getDocLen(this.zoom) > ((float) getHeight())) {
                return true;
            }
        } else if (i < 0 && this.currentYOffset < 0.0f) {
            return true;
        } else {
            if (i > 0 && this.currentYOffset + toCurrentScale(this.pdfFile.getMaxPageHeight()) > ((float) getHeight())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            if (this.enableAntialiasing) {
                canvas.setDrawFilter(this.antialiasFilter);
            }
            Drawable background = getBackground();
            if (background == null) {
                canvas.drawColor(this.nightMode ? ViewCompat.MEASURED_STATE_MASK : -1);
            } else {
                background.draw(canvas);
            }
            if (!this.recycled && this.state == State.SHOWN) {
                float f = this.currentXOffset;
                float f2 = this.currentYOffset;
                canvas.translate(f, f2);
                for (PagePart drawPart : this.cacheManager.getThumbnails()) {
                    drawPart(canvas, drawPart);
                }
                for (PagePart pagePart : this.cacheManager.getPageParts()) {
                    drawPart(canvas, pagePart);
                    if (this.callbacks.getOnDrawAll() != null && !this.onDrawPagesNums.contains(Integer.valueOf(pagePart.getPage()))) {
                        this.onDrawPagesNums.add(Integer.valueOf(pagePart.getPage()));
                    }
                }
                for (Integer intValue : this.onDrawPagesNums) {
                    drawWithListener(canvas, intValue.intValue(), this.callbacks.getOnDrawAll());
                }
                this.onDrawPagesNums.clear();
                drawWithListener(canvas, this.currentPage, this.callbacks.getOnDraw());
                canvas.translate(-f, -f2);
            }
        }
    }

    private void drawWithListener(Canvas canvas, int i, OnDrawListener onDrawListener) {
        float f;
        if (onDrawListener != null) {
            float f2 = 0.0f;
            if (this.swipeVertical) {
                f = this.pdfFile.getPageOffset(i, this.zoom);
            } else {
                f2 = this.pdfFile.getPageOffset(i, this.zoom);
                f = 0.0f;
            }
            canvas.translate(f2, f);
            SizeF pageSize = this.pdfFile.getPageSize(i);
            onDrawListener.onLayerDrawn(canvas, toCurrentScale(pageSize.getWidth()), toCurrentScale(pageSize.getHeight()), i);
            canvas.translate(-f2, -f);
        }
    }

    private void drawPart(Canvas canvas, PagePart pagePart) {
        float f;
        float f2;
        RectF pageRelativeBounds = pagePart.getPageRelativeBounds();
        Bitmap renderedBitmap = pagePart.getRenderedBitmap();
        if (!renderedBitmap.isRecycled()) {
            SizeF pageSize = this.pdfFile.getPageSize(pagePart.getPage());
            if (this.swipeVertical) {
                f2 = this.pdfFile.getPageOffset(pagePart.getPage(), this.zoom);
                f = toCurrentScale(this.pdfFile.getMaxPageWidth() - pageSize.getWidth()) / 2.0f;
            } else {
                f = this.pdfFile.getPageOffset(pagePart.getPage(), this.zoom);
                f2 = toCurrentScale(this.pdfFile.getMaxPageHeight() - pageSize.getHeight()) / 2.0f;
            }
            canvas.translate(f, f2);
            Rect rect = new Rect(0, 0, renderedBitmap.getWidth(), renderedBitmap.getHeight());
            float currentScale = toCurrentScale(pageRelativeBounds.left * pageSize.getWidth());
            float currentScale2 = toCurrentScale(pageRelativeBounds.top * pageSize.getHeight());
            RectF rectF = new RectF((float) ((int) currentScale), (float) ((int) currentScale2), (float) ((int) (currentScale + toCurrentScale(pageRelativeBounds.width() * pageSize.getWidth()))), (float) ((int) (currentScale2 + toCurrentScale(pageRelativeBounds.height() * pageSize.getHeight()))));
            float f3 = this.currentXOffset + f;
            float f4 = this.currentYOffset + f2;
            if (rectF.left + f3 >= ((float) getWidth()) || f3 + rectF.right <= 0.0f || rectF.top + f4 >= ((float) getHeight()) || f4 + rectF.bottom <= 0.0f) {
                canvas.translate(-f, -f2);
                return;
            }
            canvas.drawBitmap(renderedBitmap, rect, rectF, this.paint);
            if (Constants.DEBUG_MODE) {
                this.debugPaint.setColor(pagePart.getPage() % 2 == 0 ? SupportMenu.CATEGORY_MASK : -16776961);
                canvas.drawRect(rectF, this.debugPaint);
            }
            canvas.translate(-f, -f2);
        }
    }

    public void loadPages() {
        if (this.pdfFile != null) {
            RenderingHandler renderingHandler2 = this.renderingHandler;
            if (renderingHandler2 != null) {
                renderingHandler2.removeMessages(1);
                this.cacheManager.makeANewSet();
                this.pagesLoader.loadPages();
                redraw();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void loadComplete(PdfFile pdfFile2) {
        this.state = State.LOADED;
        this.pdfFile = pdfFile2;
        if (!this.renderingHandlerThread.isAlive()) {
            this.renderingHandlerThread.start();
        }
        RenderingHandler renderingHandler2 = new RenderingHandler(this.renderingHandlerThread.getLooper(), this);
        this.renderingHandler = renderingHandler2;
        renderingHandler2.start();
        ScrollHandle scrollHandle2 = this.scrollHandle;
        if (scrollHandle2 != null) {
            scrollHandle2.setupLayout(this);
            this.isScrollHandleInit = true;
        }
        this.dragPinchManager.enable();
        this.callbacks.callOnLoadComplete(pdfFile2.getPagesCount());
        jumpTo(this.defaultPage, false);
    }

    /* access modifiers changed from: 0000 */
    public void loadError(Throwable th) {
        this.state = State.ERROR;
        OnErrorListener onError = this.callbacks.getOnError();
        recycle();
        invalidate();
        if (onError != null) {
            onError.onError(th);
        } else {
            Log.e("PDFView", "load pdf error", th);
        }
    }

    /* access modifiers changed from: 0000 */
    public void redraw() {
        invalidate();
    }

    public void onBitmapRendered(PagePart pagePart) {
        if (this.state == State.LOADED) {
            this.state = State.SHOWN;
            this.callbacks.callOnRender(this.pdfFile.getPagesCount());
        }
        if (pagePart.isThumbnail()) {
            this.cacheManager.cacheThumbnail(pagePart);
        } else {
            this.cacheManager.cachePart(pagePart);
        }
        redraw();
    }

    public void moveTo(float f, float f2) {
        moveTo(f, f2, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveTo(float r6, float r7, boolean r8) {
        /*
            r5 = this;
            boolean r0 = r5.swipeVertical
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 0
            if (r0 == 0) goto L_0x0088
            com.github.barteksc.pdfviewer.PdfFile r0 = r5.pdfFile
            float r0 = r0.getMaxPageWidth()
            float r0 = r5.toCurrentScale(r0)
            int r3 = r5.getWidth()
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0024
            int r6 = r5.getWidth()
            int r6 = r6 / 2
            float r6 = (float) r6
            float r0 = r0 / r1
        L_0x0022:
            float r6 = r6 - r0
            goto L_0x003b
        L_0x0024:
            int r3 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x002a
            r6 = 0
            goto L_0x003b
        L_0x002a:
            float r3 = r6 + r0
            int r4 = r5.getWidth()
            float r4 = (float) r4
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x003b
            int r6 = r5.getWidth()
            float r6 = (float) r6
            goto L_0x0022
        L_0x003b:
            com.github.barteksc.pdfviewer.PdfFile r0 = r5.pdfFile
            float r3 = r5.zoom
            float r0 = r0.getDocLen(r3)
            int r3 = r5.getHeight()
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0054
            int r7 = r5.getHeight()
            float r7 = (float) r7
            float r7 = r7 - r0
            float r7 = r7 / r1
            goto L_0x006c
        L_0x0054:
            int r1 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x005a
            r7 = 0
            goto L_0x006c
        L_0x005a:
            float r1 = r7 + r0
            int r2 = r5.getHeight()
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x006c
            float r7 = -r0
            int r0 = r5.getHeight()
            float r0 = (float) r0
            float r7 = r7 + r0
        L_0x006c:
            float r0 = r5.currentYOffset
            int r1 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r1 >= 0) goto L_0x0078
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.END
            r5.scrollDir = r0
            goto L_0x0105
        L_0x0078:
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0082
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.START
            r5.scrollDir = r0
            goto L_0x0105
        L_0x0082:
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.NONE
            r5.scrollDir = r0
            goto L_0x0105
        L_0x0088:
            com.github.barteksc.pdfviewer.PdfFile r0 = r5.pdfFile
            float r0 = r0.getMaxPageHeight()
            float r0 = r5.toCurrentScale(r0)
            int r3 = r5.getHeight()
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x00a5
            int r7 = r5.getHeight()
            int r7 = r7 / 2
            float r7 = (float) r7
            float r0 = r0 / r1
        L_0x00a3:
            float r7 = r7 - r0
            goto L_0x00bc
        L_0x00a5:
            int r3 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x00ab
            r7 = 0
            goto L_0x00bc
        L_0x00ab:
            float r3 = r7 + r0
            int r4 = r5.getHeight()
            float r4 = (float) r4
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x00bc
            int r7 = r5.getHeight()
            float r7 = (float) r7
            goto L_0x00a3
        L_0x00bc:
            com.github.barteksc.pdfviewer.PdfFile r0 = r5.pdfFile
            float r3 = r5.zoom
            float r0 = r0.getDocLen(r3)
            int r3 = r5.getWidth()
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x00d5
            int r6 = r5.getWidth()
            float r6 = (float) r6
            float r6 = r6 - r0
            float r6 = r6 / r1
            goto L_0x00ed
        L_0x00d5:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x00db
            r6 = 0
            goto L_0x00ed
        L_0x00db:
            float r1 = r6 + r0
            int r2 = r5.getWidth()
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x00ed
            float r6 = -r0
            int r0 = r5.getWidth()
            float r0 = (float) r0
            float r6 = r6 + r0
        L_0x00ed:
            float r0 = r5.currentXOffset
            int r1 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r1 >= 0) goto L_0x00f8
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.END
            r5.scrollDir = r0
            goto L_0x0105
        L_0x00f8:
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0101
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.START
            r5.scrollDir = r0
            goto L_0x0105
        L_0x0101:
            com.github.barteksc.pdfviewer.PDFView$ScrollDir r0 = com.github.barteksc.pdfviewer.PDFView.ScrollDir.NONE
            r5.scrollDir = r0
        L_0x0105:
            r5.currentXOffset = r6
            r5.currentYOffset = r7
            float r6 = r5.getPositionOffset()
            if (r8 == 0) goto L_0x011e
            com.github.barteksc.pdfviewer.scroll.ScrollHandle r7 = r5.scrollHandle
            if (r7 == 0) goto L_0x011e
            boolean r7 = r5.documentFitsView()
            if (r7 != 0) goto L_0x011e
            com.github.barteksc.pdfviewer.scroll.ScrollHandle r7 = r5.scrollHandle
            r7.setScroll(r6)
        L_0x011e:
            com.github.barteksc.pdfviewer.listener.Callbacks r7 = r5.callbacks
            int r8 = r5.getCurrentPage()
            r7.callOnPageScroll(r8, r6)
            r5.redraw()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.barteksc.pdfviewer.PDFView.moveTo(float, float, boolean):void");
    }

    /* access modifiers changed from: 0000 */
    public void loadPageByOffset() {
        int i;
        float f;
        if (this.pdfFile.getPagesCount() != 0) {
            if (this.swipeVertical) {
                f = this.currentYOffset;
                i = getHeight();
            } else {
                f = this.currentXOffset;
                i = getWidth();
            }
            int pageAtOffset = this.pdfFile.getPageAtOffset(-(f - (((float) i) / 2.0f)), this.zoom);
            if (pageAtOffset < 0 || pageAtOffset > this.pdfFile.getPagesCount() - 1 || pageAtOffset == getCurrentPage()) {
                loadPages();
            } else {
                showPage(pageAtOffset);
            }
        }
    }

    public void performPageSnap() {
        if (this.pageSnap) {
            PdfFile pdfFile2 = this.pdfFile;
            if (!(pdfFile2 == null || pdfFile2.getPagesCount() == 0)) {
                int findFocusPage = findFocusPage(this.currentXOffset, this.currentYOffset);
                SnapEdge findSnapEdge = findSnapEdge(findFocusPage);
                if (findSnapEdge != SnapEdge.NONE) {
                    float snapOffsetForPage = snapOffsetForPage(findFocusPage, findSnapEdge);
                    if (this.swipeVertical) {
                        this.animationManager.startYAnimation(this.currentYOffset, -snapOffsetForPage);
                    } else {
                        this.animationManager.startXAnimation(this.currentXOffset, -snapOffsetForPage);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public SnapEdge findSnapEdge(int i) {
        if (!this.pageSnap || i < 0) {
            return SnapEdge.NONE;
        }
        float f = this.swipeVertical ? this.currentYOffset : this.currentXOffset;
        float f2 = -this.pdfFile.getPageOffset(i, this.zoom);
        int height = this.swipeVertical ? getHeight() : getWidth();
        float pageLength = this.pdfFile.getPageLength(i, this.zoom);
        float f3 = (float) height;
        if (f3 >= pageLength) {
            return SnapEdge.CENTER;
        }
        if (f >= f2) {
            return SnapEdge.START;
        }
        if (f2 - pageLength > f - f3) {
            return SnapEdge.END;
        }
        return SnapEdge.NONE;
    }

    /* access modifiers changed from: 0000 */
    public float snapOffsetForPage(int i, SnapEdge snapEdge) {
        float f;
        float pageOffset = this.pdfFile.getPageOffset(i, this.zoom);
        float height = (float) (this.swipeVertical ? getHeight() : getWidth());
        float pageLength = this.pdfFile.getPageLength(i, this.zoom);
        if (snapEdge == SnapEdge.CENTER) {
            f = pageOffset - (height / 2.0f);
            pageLength /= 2.0f;
        } else if (snapEdge != SnapEdge.END) {
            return pageOffset;
        } else {
            f = pageOffset - height;
        }
        return f + pageLength;
    }

    /* access modifiers changed from: 0000 */
    public int findFocusPage(float f, float f2) {
        if (this.swipeVertical) {
            f = f2;
        }
        float height = (float) (this.swipeVertical ? getHeight() : getWidth());
        if (f > -1.0f) {
            return 0;
        }
        if (f < (-this.pdfFile.getDocLen(this.zoom)) + height + 1.0f) {
            return this.pdfFile.getPagesCount() - 1;
        }
        return this.pdfFile.getPageAtOffset(-(f - (height / 2.0f)), this.zoom);
    }

    public boolean pageFillsScreen() {
        float f = -this.pdfFile.getPageOffset(this.currentPage, this.zoom);
        float pageLength = f - this.pdfFile.getPageLength(this.currentPage, this.zoom);
        boolean z = true;
        if (isSwipeVertical()) {
            float f2 = this.currentYOffset;
            if (f <= f2 || pageLength >= f2 - ((float) getHeight())) {
                z = false;
            }
            return z;
        }
        float f3 = this.currentXOffset;
        if (f <= f3 || pageLength >= f3 - ((float) getWidth())) {
            z = false;
        }
        return z;
    }

    public void moveRelativeTo(float f, float f2) {
        moveTo(this.currentXOffset + f, this.currentYOffset + f2);
    }

    public void zoomTo(float f) {
        this.zoom = f;
    }

    public void zoomCenteredTo(float f, PointF pointF) {
        float f2 = f / this.zoom;
        zoomTo(f);
        moveTo((this.currentXOffset * f2) + (pointF.x - (pointF.x * f2)), (this.currentYOffset * f2) + (pointF.y - (pointF.y * f2)));
    }

    public void zoomCenteredRelativeTo(float f, PointF pointF) {
        zoomCenteredTo(this.zoom * f, pointF);
    }

    public boolean documentFitsView() {
        float docLen = this.pdfFile.getDocLen(1.0f);
        boolean z = true;
        if (this.swipeVertical) {
            if (docLen >= ((float) getHeight())) {
                z = false;
            }
            return z;
        }
        if (docLen >= ((float) getWidth())) {
            z = false;
        }
        return z;
    }

    public void fitToWidth(int i) {
        if (this.state != State.SHOWN) {
            Log.e(TAG, "Cannot fit, document not rendered yet");
            return;
        }
        zoomTo(((float) getWidth()) / this.pdfFile.getPageSize(i).getWidth());
        jumpTo(i);
    }

    public SizeF getPageSize(int i) {
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 == null) {
            return new SizeF(0.0f, 0.0f);
        }
        return pdfFile2.getPageSize(i);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public float getCurrentXOffset() {
        return this.currentXOffset;
    }

    public float getCurrentYOffset() {
        return this.currentYOffset;
    }

    public float toRealScale(float f) {
        return f / this.zoom;
    }

    public float toCurrentScale(float f) {
        return f * this.zoom;
    }

    public float getZoom() {
        return this.zoom;
    }

    public boolean isZooming() {
        return this.zoom != this.minZoom;
    }

    /* access modifiers changed from: private */
    public void setDefaultPage(int i) {
        this.defaultPage = i;
    }

    public void resetZoom() {
        zoomTo(this.minZoom);
    }

    public void resetZoomWithAnimation() {
        zoomWithAnimation(this.minZoom);
    }

    public void zoomWithAnimation(float f, float f2, float f3) {
        this.animationManager.startZoomAnimation(f, f2, this.zoom, f3);
    }

    public void zoomWithAnimation(float f) {
        this.animationManager.startZoomAnimation((float) (getWidth() / 2), (float) (getHeight() / 2), this.zoom, f);
    }

    /* access modifiers changed from: private */
    public void setScrollHandle(ScrollHandle scrollHandle2) {
        this.scrollHandle = scrollHandle2;
    }

    public int getPageAtPositionOffset(float f) {
        PdfFile pdfFile2 = this.pdfFile;
        return pdfFile2.getPageAtOffset(pdfFile2.getDocLen(this.zoom) * f, this.zoom);
    }

    public float getMinZoom() {
        return this.minZoom;
    }

    public void setMinZoom(float f) {
        this.minZoom = f;
    }

    public float getMidZoom() {
        return this.midZoom;
    }

    public void setMidZoom(float f) {
        this.midZoom = f;
    }

    public float getMaxZoom() {
        return this.maxZoom;
    }

    public void setMaxZoom(float f) {
        this.maxZoom = f;
    }

    public void useBestQuality(boolean z) {
        this.bestQuality = z;
    }

    public boolean isBestQuality() {
        return this.bestQuality;
    }

    public boolean isSwipeVertical() {
        return this.swipeVertical;
    }

    public boolean isSwipeEnabled() {
        return this.enableSwipe;
    }

    /* access modifiers changed from: private */
    public void setSwipeVertical(boolean z) {
        this.swipeVertical = z;
    }

    public void enableAnnotationRendering(boolean z) {
        this.annotationRendering = z;
    }

    public boolean isAnnotationRendering() {
        return this.annotationRendering;
    }

    public void enableRenderDuringScale(boolean z) {
        this.renderDuringScale = z;
    }

    public boolean isAntialiasing() {
        return this.enableAntialiasing;
    }

    public void enableAntialiasing(boolean z) {
        this.enableAntialiasing = z;
    }

    public int getSpacingPx() {
        return this.spacingPx;
    }

    public boolean isAutoSpacingEnabled() {
        return this.autoSpacing;
    }

    public void setPageFling(boolean z) {
        this.pageFling = z;
    }

    public boolean isPageFlingEnabled() {
        return this.pageFling;
    }

    /* access modifiers changed from: private */
    public void setSpacing(int i) {
        this.spacingPx = Util.getDP(getContext(), i);
    }

    /* access modifiers changed from: private */
    public void setAutoSpacing(boolean z) {
        this.autoSpacing = z;
    }

    /* access modifiers changed from: private */
    public void setPageFitPolicy(FitPolicy fitPolicy) {
        this.pageFitPolicy = fitPolicy;
    }

    public FitPolicy getPageFitPolicy() {
        return this.pageFitPolicy;
    }

    /* access modifiers changed from: private */
    public void setFitEachPage(boolean z) {
        this.fitEachPage = z;
    }

    public boolean isFitEachPage() {
        return this.fitEachPage;
    }

    public boolean isPageSnap() {
        return this.pageSnap;
    }

    public void setPageSnap(boolean z) {
        this.pageSnap = z;
    }

    public boolean doRenderDuringScale() {
        return this.renderDuringScale;
    }

    public Meta getDocumentMeta() {
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 == null) {
            return null;
        }
        return pdfFile2.getMetaData();
    }

    public List<Bookmark> getTableOfContents() {
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 == null) {
            return Collections.emptyList();
        }
        return pdfFile2.getBookmarks();
    }

    public List<Link> getLinks(int i) {
        PdfFile pdfFile2 = this.pdfFile;
        if (pdfFile2 == null) {
            return Collections.emptyList();
        }
        return pdfFile2.getPageLinks(i);
    }

    public Configurator fromAsset(String str) {
        return new Configurator(new AssetSource(str));
    }

    public Configurator fromFile(File file) {
        return new Configurator(new FileSource(file));
    }

    public Configurator fromUri(Uri uri) {
        return new Configurator(new UriSource(uri));
    }

    public Configurator fromBytes(byte[] bArr) {
        return new Configurator(new ByteArraySource(bArr));
    }

    public Configurator fromStream(InputStream inputStream) {
        return new Configurator(new InputStreamSource(inputStream));
    }

    public Configurator fromSource(DocumentSource documentSource) {
        return new Configurator(documentSource);
    }
}
