package com.example.notinfoapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

public class PDFViewer extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0632R.layout.activity_p_d_f_viewer);
        PDFView pDFView = (PDFView) findViewById(C0632R.C0635id.viewer);
        File file = new File((String) Objects.requireNonNull(((Bundle) Objects.requireNonNull(getIntent().getExtras())).getString("file")));
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("°°°°°°°°°°°° Peso del archivo en la clase de PDF : ");
        sb.append(file.length());
        sb.append(" °°°°°°°°°");
        printStream.println(sb.toString());
        pDFView.fromFile(file).enableSwipe(true).scrollHandle(new DefaultScrollHandle(this)).swipeHorizontal(false).onError(new OnErrorListener() {
            public void onError(Throwable th) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("XXXXXXXXXXXXXX Algo salio mal cos' : ");
                sb.append(th.getMessage());
                printStream.println(sb.toString());
            }
        }).enableAntialiasing(true).spacing(10).pageFitPolicy(FitPolicy.WIDTH).load();
    }
}
