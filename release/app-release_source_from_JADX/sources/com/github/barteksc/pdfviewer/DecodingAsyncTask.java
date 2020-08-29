package com.github.barteksc.pdfviewer;

import android.os.AsyncTask;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.shockwave.pdfium.PdfiumCore;
import com.shockwave.pdfium.util.Size;
import java.lang.ref.WeakReference;

class DecodingAsyncTask extends AsyncTask<Void, Void, Throwable> {
    private boolean cancelled = false;
    private DocumentSource docSource;
    private String password;
    private PdfFile pdfFile;
    private WeakReference<PDFView> pdfViewReference;
    private PdfiumCore pdfiumCore;
    private int[] userPages;

    DecodingAsyncTask(DocumentSource documentSource, String str, int[] iArr, PDFView pDFView, PdfiumCore pdfiumCore2) {
        this.docSource = documentSource;
        this.userPages = iArr;
        this.pdfViewReference = new WeakReference<>(pDFView);
        this.password = str;
        this.pdfiumCore = pdfiumCore2;
    }

    /* access modifiers changed from: protected */
    public Throwable doInBackground(Void... voidArr) {
        try {
            PDFView pDFView = (PDFView) this.pdfViewReference.get();
            if (pDFView == null) {
                return new NullPointerException("pdfView == null");
            }
            PdfFile pdfFile2 = new PdfFile(this.pdfiumCore, this.docSource.createDocument(pDFView.getContext(), this.pdfiumCore, this.password), pDFView.getPageFitPolicy(), getViewSize(pDFView), this.userPages, pDFView.isSwipeVertical(), pDFView.getSpacingPx(), pDFView.isAutoSpacingEnabled(), pDFView.isFitEachPage());
            this.pdfFile = pdfFile2;
            return null;
        } catch (Throwable th) {
            return th;
        }
    }

    private Size getViewSize(PDFView pDFView) {
        return new Size(pDFView.getWidth(), pDFView.getHeight());
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Throwable th) {
        PDFView pDFView = (PDFView) this.pdfViewReference.get();
        if (pDFView != null) {
            if (th != null) {
                pDFView.loadError(th);
            } else if (!this.cancelled) {
                pDFView.loadComplete(this.pdfFile);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCancelled() {
        this.cancelled = true;
    }
}
