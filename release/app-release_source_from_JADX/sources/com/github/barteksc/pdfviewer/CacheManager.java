package com.github.barteksc.pdfviewer;

import android.graphics.RectF;
import com.github.barteksc.pdfviewer.model.PagePart;
import com.github.barteksc.pdfviewer.util.Constants.Cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

class CacheManager {
    private final PriorityQueue<PagePart> activeCache = new PriorityQueue<>(Cache.CACHE_SIZE, this.orderComparator);
    private final PagePartComparator orderComparator = new PagePartComparator();
    private final Object passiveActiveLock = new Object();
    private final PriorityQueue<PagePart> passiveCache = new PriorityQueue<>(Cache.CACHE_SIZE, this.orderComparator);
    private final List<PagePart> thumbnails = new ArrayList();

    class PagePartComparator implements Comparator<PagePart> {
        PagePartComparator() {
        }

        public int compare(PagePart pagePart, PagePart pagePart2) {
            if (pagePart.getCacheOrder() == pagePart2.getCacheOrder()) {
                return 0;
            }
            return pagePart.getCacheOrder() > pagePart2.getCacheOrder() ? 1 : -1;
        }
    }

    public void cachePart(PagePart pagePart) {
        synchronized (this.passiveActiveLock) {
            makeAFreeSpace();
            this.activeCache.offer(pagePart);
        }
    }

    public void makeANewSet() {
        synchronized (this.passiveActiveLock) {
            this.passiveCache.addAll(this.activeCache);
            this.activeCache.clear();
        }
    }

    private void makeAFreeSpace() {
        synchronized (this.passiveActiveLock) {
            while (this.activeCache.size() + this.passiveCache.size() >= Cache.CACHE_SIZE && !this.passiveCache.isEmpty()) {
                ((PagePart) this.passiveCache.poll()).getRenderedBitmap().recycle();
            }
            while (this.activeCache.size() + this.passiveCache.size() >= Cache.CACHE_SIZE && !this.activeCache.isEmpty()) {
                ((PagePart) this.activeCache.poll()).getRenderedBitmap().recycle();
            }
        }
    }

    public void cacheThumbnail(PagePart pagePart) {
        synchronized (this.thumbnails) {
            while (this.thumbnails.size() >= Cache.THUMBNAILS_CACHE_SIZE) {
                ((PagePart) this.thumbnails.remove(0)).getRenderedBitmap().recycle();
            }
            addWithoutDuplicates(this.thumbnails, pagePart);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean upPartIfContained(int r8, android.graphics.RectF r9, int r10) {
        /*
            r7 = this;
            com.github.barteksc.pdfviewer.model.PagePart r6 = new com.github.barteksc.pdfviewer.model.PagePart
            r2 = 0
            r4 = 0
            r5 = 0
            r0 = r6
            r1 = r8
            r3 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            java.lang.Object r8 = r7.passiveActiveLock
            monitor-enter(r8)
            java.util.PriorityQueue<com.github.barteksc.pdfviewer.model.PagePart> r9 = r7.passiveCache     // Catch:{ all -> 0x0032 }
            com.github.barteksc.pdfviewer.model.PagePart r9 = find(r9, r6)     // Catch:{ all -> 0x0032 }
            r0 = 1
            if (r9 == 0) goto L_0x0026
            java.util.PriorityQueue<com.github.barteksc.pdfviewer.model.PagePart> r1 = r7.passiveCache     // Catch:{ all -> 0x0032 }
            r1.remove(r9)     // Catch:{ all -> 0x0032 }
            r9.setCacheOrder(r10)     // Catch:{ all -> 0x0032 }
            java.util.PriorityQueue<com.github.barteksc.pdfviewer.model.PagePart> r10 = r7.activeCache     // Catch:{ all -> 0x0032 }
            r10.offer(r9)     // Catch:{ all -> 0x0032 }
            monitor-exit(r8)     // Catch:{ all -> 0x0032 }
            return r0
        L_0x0026:
            java.util.PriorityQueue<com.github.barteksc.pdfviewer.model.PagePart> r9 = r7.activeCache     // Catch:{ all -> 0x0032 }
            com.github.barteksc.pdfviewer.model.PagePart r9 = find(r9, r6)     // Catch:{ all -> 0x0032 }
            if (r9 == 0) goto L_0x002f
            goto L_0x0030
        L_0x002f:
            r0 = 0
        L_0x0030:
            monitor-exit(r8)     // Catch:{ all -> 0x0032 }
            return r0
        L_0x0032:
            r9 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0032 }
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.barteksc.pdfviewer.CacheManager.upPartIfContained(int, android.graphics.RectF, int):boolean");
    }

    public boolean containsThumbnail(int i, RectF rectF) {
        PagePart pagePart = new PagePart(i, null, rectF, true, 0);
        synchronized (this.thumbnails) {
            for (PagePart equals : this.thumbnails) {
                if (equals.equals(pagePart)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void addWithoutDuplicates(Collection<PagePart> collection, PagePart pagePart) {
        for (PagePart equals : collection) {
            if (equals.equals(pagePart)) {
                pagePart.getRenderedBitmap().recycle();
                return;
            }
        }
        collection.add(pagePart);
    }

    private static PagePart find(PriorityQueue<PagePart> priorityQueue, PagePart pagePart) {
        Iterator it = priorityQueue.iterator();
        while (it.hasNext()) {
            PagePart pagePart2 = (PagePart) it.next();
            if (pagePart2.equals(pagePart)) {
                return pagePart2;
            }
        }
        return null;
    }

    public List<PagePart> getPageParts() {
        ArrayList arrayList;
        synchronized (this.passiveActiveLock) {
            arrayList = new ArrayList(this.passiveCache);
            arrayList.addAll(this.activeCache);
        }
        return arrayList;
    }

    public List<PagePart> getThumbnails() {
        List<PagePart> list;
        synchronized (this.thumbnails) {
            list = this.thumbnails;
        }
        return list;
    }

    public void recycle() {
        synchronized (this.passiveActiveLock) {
            Iterator it = this.passiveCache.iterator();
            while (it.hasNext()) {
                ((PagePart) it.next()).getRenderedBitmap().recycle();
            }
            this.passiveCache.clear();
            Iterator it2 = this.activeCache.iterator();
            while (it2.hasNext()) {
                ((PagePart) it2.next()).getRenderedBitmap().recycle();
            }
            this.activeCache.clear();
        }
        synchronized (this.thumbnails) {
            for (PagePart renderedBitmap : this.thumbnails) {
                renderedBitmap.getRenderedBitmap().recycle();
            }
            this.thumbnails.clear();
        }
    }
}
