package com.github.barteksc.pdfviewer.util;

import com.shockwave.pdfium.util.Size;
import com.shockwave.pdfium.util.SizeF;

public class PageSizeCalculator {
    private boolean fitEachPage;
    private FitPolicy fitPolicy;
    private float heightRatio;
    private SizeF optimalMaxHeightPageSize;
    private SizeF optimalMaxWidthPageSize;
    private final Size originalMaxHeightPageSize;
    private final Size originalMaxWidthPageSize;
    private final Size viewSize;
    private float widthRatio;

    /* renamed from: com.github.barteksc.pdfviewer.util.PageSizeCalculator$1 */
    static /* synthetic */ class C07031 {
        static final /* synthetic */ int[] $SwitchMap$com$github$barteksc$pdfviewer$util$FitPolicy;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                com.github.barteksc.pdfviewer.util.FitPolicy[] r0 = com.github.barteksc.pdfviewer.util.FitPolicy.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$github$barteksc$pdfviewer$util$FitPolicy = r0
                com.github.barteksc.pdfviewer.util.FitPolicy r1 = com.github.barteksc.pdfviewer.util.FitPolicy.HEIGHT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$github$barteksc$pdfviewer$util$FitPolicy     // Catch:{ NoSuchFieldError -> 0x001d }
                com.github.barteksc.pdfviewer.util.FitPolicy r1 = com.github.barteksc.pdfviewer.util.FitPolicy.BOTH     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.github.barteksc.pdfviewer.util.PageSizeCalculator.C07031.<clinit>():void");
        }
    }

    public PageSizeCalculator(FitPolicy fitPolicy2, Size size, Size size2, Size size3, boolean z) {
        this.fitPolicy = fitPolicy2;
        this.originalMaxWidthPageSize = size;
        this.originalMaxHeightPageSize = size2;
        this.viewSize = size3;
        this.fitEachPage = z;
        calculateMaxPages();
    }

    public SizeF calculate(Size size) {
        if (size.getWidth() <= 0 || size.getHeight() <= 0) {
            return new SizeF(0.0f, 0.0f);
        }
        float width = this.fitEachPage ? (float) this.viewSize.getWidth() : ((float) size.getWidth()) * this.widthRatio;
        float height = this.fitEachPage ? (float) this.viewSize.getHeight() : ((float) size.getHeight()) * this.heightRatio;
        int i = C07031.$SwitchMap$com$github$barteksc$pdfviewer$util$FitPolicy[this.fitPolicy.ordinal()];
        if (i == 1) {
            return fitHeight(size, height);
        }
        if (i != 2) {
            return fitWidth(size, width);
        }
        return fitBoth(size, width, height);
    }

    public SizeF getOptimalMaxWidthPageSize() {
        return this.optimalMaxWidthPageSize;
    }

    public SizeF getOptimalMaxHeightPageSize() {
        return this.optimalMaxHeightPageSize;
    }

    private void calculateMaxPages() {
        int i = C07031.$SwitchMap$com$github$barteksc$pdfviewer$util$FitPolicy[this.fitPolicy.ordinal()];
        if (i == 1) {
            SizeF fitHeight = fitHeight(this.originalMaxHeightPageSize, (float) this.viewSize.getHeight());
            this.optimalMaxHeightPageSize = fitHeight;
            this.heightRatio = fitHeight.getHeight() / ((float) this.originalMaxHeightPageSize.getHeight());
            Size size = this.originalMaxWidthPageSize;
            this.optimalMaxWidthPageSize = fitHeight(size, ((float) size.getHeight()) * this.heightRatio);
        } else if (i != 2) {
            SizeF fitWidth = fitWidth(this.originalMaxWidthPageSize, (float) this.viewSize.getWidth());
            this.optimalMaxWidthPageSize = fitWidth;
            this.widthRatio = fitWidth.getWidth() / ((float) this.originalMaxWidthPageSize.getWidth());
            Size size2 = this.originalMaxHeightPageSize;
            this.optimalMaxHeightPageSize = fitWidth(size2, ((float) size2.getWidth()) * this.widthRatio);
        } else {
            float width = fitBoth(this.originalMaxWidthPageSize, (float) this.viewSize.getWidth(), (float) this.viewSize.getHeight()).getWidth() / ((float) this.originalMaxWidthPageSize.getWidth());
            Size size3 = this.originalMaxHeightPageSize;
            SizeF fitBoth = fitBoth(size3, ((float) size3.getWidth()) * width, (float) this.viewSize.getHeight());
            this.optimalMaxHeightPageSize = fitBoth;
            this.heightRatio = fitBoth.getHeight() / ((float) this.originalMaxHeightPageSize.getHeight());
            SizeF fitBoth2 = fitBoth(this.originalMaxWidthPageSize, (float) this.viewSize.getWidth(), ((float) this.originalMaxWidthPageSize.getHeight()) * this.heightRatio);
            this.optimalMaxWidthPageSize = fitBoth2;
            this.widthRatio = fitBoth2.getWidth() / ((float) this.originalMaxWidthPageSize.getWidth());
        }
    }

    private SizeF fitWidth(Size size, float f) {
        return new SizeF(f, (float) Math.floor((double) (f / (((float) size.getWidth()) / ((float) size.getHeight())))));
    }

    private SizeF fitHeight(Size size, float f) {
        return new SizeF((float) Math.floor((double) (f / (((float) size.getHeight()) / ((float) size.getWidth())))), f);
    }

    private SizeF fitBoth(Size size, float f, float f2) {
        float width = ((float) size.getWidth()) / ((float) size.getHeight());
        float floor = (float) Math.floor((double) (f / width));
        if (floor > f2) {
            f = (float) Math.floor((double) (width * f2));
        } else {
            f2 = floor;
        }
        return new SizeF(f, f2);
    }
}
