package com.github.barteksc.pdfviewer;

import android.graphics.RectF;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.Constants.Cache;
import com.github.barteksc.pdfviewer.util.MathUtils;
import com.github.barteksc.pdfviewer.util.Util;
import com.shockwave.pdfium.util.SizeF;
import java.util.List;

class PagesLoader {
    private int cacheOrder;
    private float pageRelativePartHeight;
    private float pageRelativePartWidth;
    private float partRenderHeight;
    private float partRenderWidth;
    private PDFView pdfView;
    private final int preloadOffset;
    private final RectF thumbnailRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    private float xOffset;
    private float yOffset;

    private class GridSize {
        int cols;
        int rows;

        private GridSize() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("GridSize{rows=");
            sb.append(this.rows);
            sb.append(", cols=");
            sb.append(this.cols);
            sb.append('}');
            return sb.toString();
        }
    }

    private class Holder {
        int col;
        int row;

        private Holder() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Holder{row=");
            sb.append(this.row);
            sb.append(", col=");
            sb.append(this.col);
            sb.append('}');
            return sb.toString();
        }
    }

    private class RenderRange {
        GridSize gridSize;
        Holder leftTop;
        int page = 0;
        Holder rightBottom;

        RenderRange() {
            this.gridSize = new GridSize();
            this.leftTop = new Holder();
            this.rightBottom = new Holder();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("RenderRange{page=");
            sb.append(this.page);
            sb.append(", gridSize=");
            sb.append(this.gridSize);
            sb.append(", leftTop=");
            sb.append(this.leftTop);
            sb.append(", rightBottom=");
            sb.append(this.rightBottom);
            sb.append('}');
            return sb.toString();
        }
    }

    PagesLoader(PDFView pDFView) {
        this.pdfView = pDFView;
        this.preloadOffset = Util.getDP(pDFView.getContext(), Constants.PRELOAD_OFFSET);
    }

    private void getPageColsRows(GridSize gridSize, int i) {
        SizeF pageSize = this.pdfView.pdfFile.getPageSize(i);
        float width = 1.0f / pageSize.getWidth();
        float height = (Constants.PART_SIZE * (1.0f / pageSize.getHeight())) / this.pdfView.getZoom();
        float zoom = (Constants.PART_SIZE * width) / this.pdfView.getZoom();
        gridSize.rows = MathUtils.ceil(1.0f / height);
        gridSize.cols = MathUtils.ceil(1.0f / zoom);
    }

    private void calculatePartSize(GridSize gridSize) {
        this.pageRelativePartWidth = 1.0f / ((float) gridSize.cols);
        this.pageRelativePartHeight = 1.0f / ((float) gridSize.rows);
        this.partRenderWidth = Constants.PART_SIZE / this.pageRelativePartWidth;
        this.partRenderHeight = Constants.PART_SIZE / this.pageRelativePartHeight;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x01a6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.github.barteksc.pdfviewer.PagesLoader.RenderRange> getRenderRangeList(float r20, float r21, float r22, float r23) {
        /*
            r19 = this;
            r0 = r19
            r1 = 0
            r2 = r20
            float r2 = com.github.barteksc.pdfviewer.util.MathUtils.max(r2, r1)
            float r2 = -r2
            r3 = r21
            float r3 = com.github.barteksc.pdfviewer.util.MathUtils.max(r3, r1)
            float r3 = -r3
            r4 = r22
            float r4 = com.github.barteksc.pdfviewer.util.MathUtils.max(r4, r1)
            float r4 = -r4
            r5 = r23
            float r5 = com.github.barteksc.pdfviewer.util.MathUtils.max(r5, r1)
            float r5 = -r5
            com.github.barteksc.pdfviewer.PDFView r6 = r0.pdfView
            boolean r6 = r6.isSwipeVertical()
            if (r6 == 0) goto L_0x0029
            r6 = r3
            goto L_0x002a
        L_0x0029:
            r6 = r2
        L_0x002a:
            com.github.barteksc.pdfviewer.PDFView r7 = r0.pdfView
            boolean r7 = r7.isSwipeVertical()
            if (r7 == 0) goto L_0x0034
            r7 = r5
            goto L_0x0035
        L_0x0034:
            r7 = r4
        L_0x0035:
            com.github.barteksc.pdfviewer.PDFView r8 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r8 = r8.pdfFile
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            float r9 = r9.getZoom()
            int r6 = r8.getPageAtOffset(r6, r9)
            com.github.barteksc.pdfviewer.PDFView r8 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r8 = r8.pdfFile
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            float r9 = r9.getZoom()
            int r7 = r8.getPageAtOffset(r7, r9)
            int r8 = r7 - r6
            r9 = 1
            int r8 = r8 + r9
            java.util.LinkedList r10 = new java.util.LinkedList
            r10.<init>()
            r11 = r6
        L_0x005b:
            if (r11 > r7) goto L_0x021a
            com.github.barteksc.pdfviewer.PagesLoader$RenderRange r12 = new com.github.barteksc.pdfviewer.PagesLoader$RenderRange
            r12.<init>()
            r12.page = r11
            if (r11 != r6) goto L_0x00a7
            if (r8 != r9) goto L_0x006f
            r13 = r2
            r15 = r3
            r14 = r4
        L_0x006b:
            r16 = r5
            goto L_0x00fe
        L_0x006f:
            com.github.barteksc.pdfviewer.PDFView r13 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r13 = r13.pdfFile
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            float r14 = r14.getZoom()
            float r13 = r13.getPageOffset(r11, r14)
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r14 = r14.pdfFile
            com.github.barteksc.pdfviewer.PDFView r15 = r0.pdfView
            float r15 = r15.getZoom()
            com.shockwave.pdfium.util.SizeF r14 = r14.getScaledPageSize(r11, r15)
            com.github.barteksc.pdfviewer.PDFView r15 = r0.pdfView
            boolean r15 = r15.isSwipeVertical()
            if (r15 == 0) goto L_0x009b
            float r14 = r14.getHeight()
            float r13 = r13 + r14
            r14 = r13
            r13 = r4
            goto L_0x00a1
        L_0x009b:
            float r14 = r14.getWidth()
            float r13 = r13 + r14
            r14 = r5
        L_0x00a1:
            r15 = r3
            r16 = r14
            r14 = r13
            r13 = r2
            goto L_0x00fe
        L_0x00a7:
            if (r11 != r7) goto L_0x00c7
            com.github.barteksc.pdfviewer.PDFView r13 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r13 = r13.pdfFile
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            float r14 = r14.getZoom()
            float r13 = r13.getPageOffset(r11, r14)
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            boolean r14 = r14.isSwipeVertical()
            if (r14 == 0) goto L_0x00c2
            r14 = r13
            r13 = r2
            goto L_0x00c3
        L_0x00c2:
            r14 = r3
        L_0x00c3:
            r16 = r5
            r15 = r14
            goto L_0x00f4
        L_0x00c7:
            com.github.barteksc.pdfviewer.PDFView r13 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r13 = r13.pdfFile
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            float r14 = r14.getZoom()
            float r13 = r13.getPageOffset(r11, r14)
            com.github.barteksc.pdfviewer.PDFView r14 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r14 = r14.pdfFile
            com.github.barteksc.pdfviewer.PDFView r15 = r0.pdfView
            float r15 = r15.getZoom()
            com.shockwave.pdfium.util.SizeF r14 = r14.getScaledPageSize(r11, r15)
            com.github.barteksc.pdfviewer.PDFView r15 = r0.pdfView
            boolean r15 = r15.isSwipeVertical()
            if (r15 == 0) goto L_0x00f6
            float r14 = r14.getHeight()
            float r14 = r14 + r13
            r15 = r13
            r16 = r14
            r13 = r2
        L_0x00f4:
            r14 = r4
            goto L_0x00fe
        L_0x00f6:
            float r14 = r14.getWidth()
            float r14 = r14 + r13
            r15 = r3
            goto L_0x006b
        L_0x00fe:
            com.github.barteksc.pdfviewer.PagesLoader$GridSize r9 = r12.gridSize
            int r1 = r12.page
            r0.getPageColsRows(r9, r1)
            com.github.barteksc.pdfviewer.PDFView r1 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r1 = r1.pdfFile
            int r9 = r12.page
            r17 = r2
            com.github.barteksc.pdfviewer.PDFView r2 = r0.pdfView
            float r2 = r2.getZoom()
            com.shockwave.pdfium.util.SizeF r1 = r1.getScaledPageSize(r9, r2)
            float r2 = r1.getHeight()
            com.github.barteksc.pdfviewer.PagesLoader$GridSize r9 = r12.gridSize
            int r9 = r9.rows
            float r9 = (float) r9
            float r2 = r2 / r9
            float r1 = r1.getWidth()
            com.github.barteksc.pdfviewer.PagesLoader$GridSize r9 = r12.gridSize
            int r9 = r9.cols
            float r9 = (float) r9
            float r1 = r1 / r9
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r9 = r9.pdfFile
            r21 = r3
            com.github.barteksc.pdfviewer.PDFView r3 = r0.pdfView
            float r3 = r3.getZoom()
            float r3 = r9.getSecondaryPageOffset(r11, r3)
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            boolean r9 = r9.isSwipeVertical()
            if (r9 == 0) goto L_0x01a6
            com.github.barteksc.pdfviewer.PagesLoader$Holder r9 = r12.leftTop
            r22 = r4
            com.github.barteksc.pdfviewer.PDFView r4 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r4 = r4.pdfFile
            r23 = r5
            int r5 = r12.page
            r18 = r6
            com.github.barteksc.pdfviewer.PDFView r6 = r0.pdfView
            float r6 = r6.getZoom()
            float r4 = r4.getPageOffset(r5, r6)
            float r15 = r15 - r4
            float r4 = java.lang.Math.abs(r15)
            float r4 = r4 / r2
            int r4 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r4)
            r9.row = r4
            com.github.barteksc.pdfviewer.PagesLoader$Holder r4 = r12.leftTop
            float r13 = r13 - r3
            r5 = 0
            float r6 = com.github.barteksc.pdfviewer.util.MathUtils.min(r13, r5)
            float r6 = r6 / r1
            int r5 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r6)
            r4.col = r5
            com.github.barteksc.pdfviewer.PagesLoader$Holder r4 = r12.rightBottom
            com.github.barteksc.pdfviewer.PDFView r5 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r5 = r5.pdfFile
            int r6 = r12.page
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            float r9 = r9.getZoom()
            float r5 = r5.getPageOffset(r6, r9)
            float r16 = r16 - r5
            float r5 = java.lang.Math.abs(r16)
            float r5 = r5 / r2
            int r2 = com.github.barteksc.pdfviewer.util.MathUtils.ceil(r5)
            r4.row = r2
            com.github.barteksc.pdfviewer.PagesLoader$Holder r2 = r12.rightBottom
            float r14 = r14 - r3
            r3 = 0
            float r4 = com.github.barteksc.pdfviewer.util.MathUtils.min(r14, r3)
            float r4 = r4 / r1
            int r1 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r4)
            r2.col = r1
            r4 = 0
            goto L_0x0207
        L_0x01a6:
            r22 = r4
            r23 = r5
            r18 = r6
            com.github.barteksc.pdfviewer.PagesLoader$Holder r4 = r12.leftTop
            com.github.barteksc.pdfviewer.PDFView r5 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r5 = r5.pdfFile
            int r6 = r12.page
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            float r9 = r9.getZoom()
            float r5 = r5.getPageOffset(r6, r9)
            float r13 = r13 - r5
            float r5 = java.lang.Math.abs(r13)
            float r5 = r5 / r1
            int r5 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r5)
            r4.col = r5
            com.github.barteksc.pdfviewer.PagesLoader$Holder r4 = r12.leftTop
            float r15 = r15 - r3
            r5 = 0
            float r6 = com.github.barteksc.pdfviewer.util.MathUtils.min(r15, r5)
            float r6 = r6 / r2
            int r5 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r6)
            r4.row = r5
            com.github.barteksc.pdfviewer.PagesLoader$Holder r4 = r12.rightBottom
            com.github.barteksc.pdfviewer.PDFView r5 = r0.pdfView
            com.github.barteksc.pdfviewer.PdfFile r5 = r5.pdfFile
            int r6 = r12.page
            com.github.barteksc.pdfviewer.PDFView r9 = r0.pdfView
            float r9 = r9.getZoom()
            float r5 = r5.getPageOffset(r6, r9)
            float r14 = r14 - r5
            float r5 = java.lang.Math.abs(r14)
            float r5 = r5 / r1
            int r1 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r5)
            r4.col = r1
            com.github.barteksc.pdfviewer.PagesLoader$Holder r1 = r12.rightBottom
            float r3 = r16 - r3
            r4 = 0
            float r3 = com.github.barteksc.pdfviewer.util.MathUtils.min(r3, r4)
            float r3 = r3 / r2
            int r2 = com.github.barteksc.pdfviewer.util.MathUtils.floor(r3)
            r1.row = r2
        L_0x0207:
            r10.add(r12)
            int r11 = r11 + 1
            r3 = r21
            r4 = r22
            r5 = r23
            r2 = r17
            r6 = r18
            r1 = 0
            r9 = 1
            goto L_0x005b
        L_0x021a:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.barteksc.pdfviewer.PagesLoader.getRenderRangeList(float, float, float, float):java.util.List");
    }

    private void loadVisible() {
        float f = (float) this.preloadOffset;
        float f2 = this.xOffset;
        float f3 = (-f2) + f;
        float width = ((-f2) - ((float) this.pdfView.getWidth())) - f;
        float f4 = this.yOffset;
        List<RenderRange> renderRangeList = getRenderRangeList(f3, (-f4) + f, width, ((-f4) - ((float) this.pdfView.getHeight())) - f);
        for (RenderRange renderRange : renderRangeList) {
            loadThumbnail(renderRange.page);
        }
        int i = 0;
        for (RenderRange renderRange2 : renderRangeList) {
            calculatePartSize(renderRange2.gridSize);
            i += loadPage(renderRange2.page, renderRange2.leftTop.row, renderRange2.rightBottom.row, renderRange2.leftTop.col, renderRange2.rightBottom.col, Cache.CACHE_SIZE - i);
            if (i >= Cache.CACHE_SIZE) {
                return;
            }
        }
    }

    private int loadPage(int i, int i2, int i3, int i4, int i5, int i6) {
        int i7 = 0;
        while (i2 <= i3) {
            for (int i8 = i4; i8 <= i5; i8++) {
                if (loadCell(i, i2, i8, this.pageRelativePartWidth, this.pageRelativePartHeight)) {
                    i7++;
                }
                if (i7 >= i6) {
                    return i7;
                }
            }
            i2++;
        }
        return i7;
    }

    private boolean loadCell(int i, int i2, int i3, float f, float f2) {
        float f3 = ((float) i3) * f;
        float f4 = ((float) i2) * f2;
        float f5 = this.partRenderWidth;
        float f6 = this.partRenderHeight;
        float f7 = f3 + f > 1.0f ? 1.0f - f3 : f;
        float f8 = f4 + f2 > 1.0f ? 1.0f - f4 : f2;
        float f9 = f5 * f7;
        float f10 = f6 * f8;
        RectF rectF = new RectF(f3, f4, f7 + f3, f8 + f4);
        if (f9 <= 0.0f || f10 <= 0.0f) {
            return false;
        }
        if (!this.pdfView.cacheManager.upPartIfContained(i, rectF, this.cacheOrder)) {
            this.pdfView.renderingHandler.addRenderingTask(i, f9, f10, rectF, false, this.cacheOrder, this.pdfView.isBestQuality(), this.pdfView.isAnnotationRendering());
        }
        this.cacheOrder++;
        return true;
    }

    private void loadThumbnail(int i) {
        SizeF pageSize = this.pdfView.pdfFile.getPageSize(i);
        float width = pageSize.getWidth() * Constants.THUMBNAIL_RATIO;
        float height = pageSize.getHeight() * Constants.THUMBNAIL_RATIO;
        if (!this.pdfView.cacheManager.containsThumbnail(i, this.thumbnailRect)) {
            this.pdfView.renderingHandler.addRenderingTask(i, width, height, this.thumbnailRect, true, 0, this.pdfView.isBestQuality(), this.pdfView.isAnnotationRendering());
        }
    }

    /* access modifiers changed from: 0000 */
    public void loadPages() {
        this.cacheOrder = 1;
        this.xOffset = -MathUtils.max(this.pdfView.getCurrentXOffset(), 0.0f);
        this.yOffset = -MathUtils.max(this.pdfView.getCurrentYOffset(), 0.0f);
        loadVisible();
    }
}
