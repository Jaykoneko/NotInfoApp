package com.example.notinfoapp.p006ui.slideshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.notinfoapp.C0632R;
import com.example.notinfoapp.MainActivity;
import com.example.notinfoapp.PDFViewer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* renamed from: com.example.notinfoapp.ui.slideshow.SlideshowFragment */
public class SlideshowFragment extends Fragment {
    /* access modifiers changed from: private */
    public ScrollView ScrollB;
    /* access modifiers changed from: private */
    public ArrayList<Beca> becas;
    /* access modifiers changed from: private */
    public ArrayList<ImageButton> botones;
    /* access modifiers changed from: private */
    public int cBeca = 0;
    /* access modifiers changed from: private */
    public ArrayList<MaterialButton> cancBt;
    /* access modifiers changed from: private */
    public ArrayList<MaterialCardView> cards;
    /* access modifiers changed from: private */
    public ArrayList<ImageView> imgs;
    protected Activity parent;
    /* access modifiers changed from: private */
    public ArrayList<TextView> resumenes;
    private View roott;
    /* access modifiers changed from: private */
    public ArrayList<TextView> sTitulos;
    private SlideshowViewModel slideshowViewModel;
    /* access modifiers changed from: private */

    /* renamed from: st */
    public Statement f90st;
    /* access modifiers changed from: private */
    public int tBecas;
    /* access modifiers changed from: private */
    public ArrayList<TextView> titulos;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.slideshowViewModel = (SlideshowViewModel) ViewModelProviders.m8of((Fragment) this).get(SlideshowViewModel.class);
        View inflate = layoutInflater.inflate(C0632R.layout.fragment_slideshow, viewGroup, false);
        this.roott = inflate;
        initUI(inflate);
        this.parent = getActivity();
        ((MainActivity) getActivity()).setCurrent(this);
        this.parent.setRequestedOrientation(1);
        final Context applicationContext = this.parent.getApplicationContext();
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    StrictMode.setThreadPolicy(new Builder().permitAll().build());
                    SlideshowFragment.this.f90st = DriverManager.getConnection(SlideshowFragment.this.getString(C0632R.string.connection_string)).createStatement();
                    ResultSet executeQuery = SlideshowFragment.this.f90st.executeQuery("getBecas");
                    SlideshowFragment.this.becas = new ArrayList();
                    int i = 0;
                    while (executeQuery.next()) {
                        Beca beca = new Beca(executeQuery.getInt("id_Becas"), executeQuery.getString("Nombre"), executeQuery.getString("Fecha_publicacion"), executeQuery.getString("Resumen"), executeQuery.getBytes("Imagen"), executeQuery.getInt("Tipo"));
                        SlideshowFragment.this.becas.add(beca);
                        i++;
                    }
                    if (i == 0) {
                        SlideshowFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(applicationContext, "No hay becas que mostrar", 1).show();
                                for (int i = 0; i < SlideshowFragment.this.cards.size(); i++) {
                                    ((MaterialCardView) SlideshowFragment.this.cards.get(i)).setVisibility(8);
                                }
                            }
                        });
                        return;
                    }
                    SlideshowFragment.this.tBecas = i - 1;
                    SlideshowFragment.this.cBeca = 0;
                    SlideshowFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            SlideshowFragment.this.postBecas(0);
                        }
                    });
                } catch (SQLException unused) {
                }
            }
        });
        ((ImageButton) this.botones.get(0)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (SlideshowFragment.this.cBeca - 10 <= 0) {
                    ((ImageButton) SlideshowFragment.this.botones.get(0)).setVisibility(8);
                    SlideshowFragment.this.cBeca = 0;
                    SlideshowFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            SlideshowFragment.this.postBecas(SlideshowFragment.this.cBeca);
                        }
                    });
                    SlideshowFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            SlideshowFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                } else {
                    SlideshowFragment slideshowFragment = SlideshowFragment.this;
                    slideshowFragment.cBeca = slideshowFragment.cBeca - 10;
                    SlideshowFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            SlideshowFragment.this.postBecas(SlideshowFragment.this.cBeca);
                        }
                    });
                    SlideshowFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            SlideshowFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                }
                ((ImageButton) SlideshowFragment.this.botones.get(1)).setVisibility(0);
            }
        });
        ((ImageButton) this.botones.get(1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((ImageButton) SlideshowFragment.this.botones.get(0)).setVisibility(0);
                SlideshowFragment.this.parent.runOnUiThread(new Runnable() {
                    public void run() {
                        SlideshowFragment.this.postBecas(SlideshowFragment.this.cBeca);
                    }
                });
                SlideshowFragment.this.ScrollB.post(new Runnable() {
                    public void run() {
                        SlideshowFragment.this.ScrollB.scrollTo(0, 0);
                    }
                });
                if (SlideshowFragment.this.cBeca >= SlideshowFragment.this.tBecas) {
                    ((ImageButton) SlideshowFragment.this.botones.get(1)).setVisibility(8);
                }
            }
        });
        return inflate;
    }

    public void initUI(View view) {
        this.imgs = new ArrayList<>();
        this.titulos = new ArrayList<>();
        this.sTitulos = new ArrayList<>();
        this.cancBt = new ArrayList<>();
        this.resumenes = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.botones = new ArrayList<>();
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Bimgc1));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Bimgc2));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Bimgc3));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Bimgc4));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Bimgc5));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtT1));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtT2));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtT3));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtT4));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtT5));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtSt1));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtSt2));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtSt3));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtSt4));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.BtxtSt5));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.BbtnC1));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.BbtnC2));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.BbtnC3));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.BbtnC4));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.BbtnC5));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.BtxtR1));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.BtxtR2));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.BtxtR3));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.BtxtR4));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.BtxtR5));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Bcard));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Bcard2));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Bcard3));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Bcard4));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Bcard5));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.BbtnAtr));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.BbtnAd));
        ScrollView scrollView = (ScrollView) view.findViewById(C0632R.C0635id.ScrollB);
        this.ScrollB = scrollView;
        scrollView.post(new Runnable() {
            public void run() {
                SlideshowFragment.this.ScrollB.scrollTo(0, 0);
            }
        });
        for (int i = 0; i < 5; i++) {
            ((ImageView) this.imgs.get(i)).setScaleType(ScaleType.FIT_CENTER);
            Glide.with((Fragment) this).load(Integer.valueOf(C0632R.C0634drawable.utn)).into((ImageView) this.imgs.get(i));
        }
    }

    public void postBecas(final int i) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~ Posteando becas desde el index ");
        sb.append(i);
        printStream.println(sb.toString());
        int i2 = this.tBecas;
        int i3 = i2 - this.cBeca;
        if (i3 < 5) {
            for (final int i4 = 0; i4 < 5; i4++) {
                if (i4 >= i3) {
                    this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            ((MaterialCardView) SlideshowFragment.this.cards.get(i4)).setVisibility(8);
                        }
                    });
                }
            }
            i2 = i3;
        } else if (i2 >= 5) {
            i2 = 5;
        }
        if (i == 0 && this.tBecas > 5) {
            for (final int i5 = 0; i5 < 5; i5++) {
                this.parent.runOnUiThread(new Runnable() {
                    public void run() {
                        ((MaterialCardView) SlideshowFragment.this.cards.get(i5)).setVisibility(0);
                    }
                });
            }
        }
        for (final int i6 = 0; i6 < i2; i6++) {
            this.parent.runOnUiThread(new Runnable(i6) {
                final /* synthetic */ int val$finalI;

                {
                    this.val$finalI = r2;
                }

                public void run() {
                    ((MaterialCardView) SlideshowFragment.this.cards.get(this.val$finalI)).setVisibility(0);
                    ((TextView) SlideshowFragment.this.titulos.get(this.val$finalI)).setText(((Beca) SlideshowFragment.this.becas.get(i)).getTitulo());
                    ((TextView) SlideshowFragment.this.sTitulos.get(this.val$finalI)).setText(((Beca) SlideshowFragment.this.becas.get(i)).getSubtitulo());
                    ((TextView) SlideshowFragment.this.resumenes.get(this.val$finalI)).setText(((Beca) SlideshowFragment.this.becas.get(i)).getResumen());
                    ((ImageView) SlideshowFragment.this.imgs.get(i6)).setScaleType(ScaleType.CENTER_INSIDE);
                    ((ImageView) SlideshowFragment.this.imgs.get(this.val$finalI)).setImageBitmap(BitmapFactory.decodeByteArray(((Beca) SlideshowFragment.this.becas.get(i)).getImagen(), 0, ((Beca) SlideshowFragment.this.becas.get(i)).getImagen().length));
                    if (((Beca) SlideshowFragment.this.becas.get(i)).getTipo() == 1) {
                        ((MaterialButton) SlideshowFragment.this.cancBt.get(this.val$finalI)).setText("Abrir PDF");
                        ((MaterialButton) SlideshowFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    File createTempFile = File.createTempFile("temp", ".pdf");
                                    createTempFile.createNewFile();
                                    FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
                                    Statement access$000 = SlideshowFragment.this.f90st;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("getBecadata ");
                                    sb.append(((Beca) SlideshowFragment.this.becas.get(i)).getId());
                                    ResultSet executeQuery = access$000.executeQuery(sb.toString());
                                    if (executeQuery.next()) {
                                        fileOutputStream.write(executeQuery.getBytes("Datos"));
                                        fileOutputStream.flush();
                                        createTempFile.deleteOnExit();
                                        Intent intent = new Intent(SlideshowFragment.this.parent.getApplicationContext(), PDFViewer.class);
                                        intent.putExtra("file", createTempFile.toPath().toString());
                                        SlideshowFragment.this.parent.startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    PrintStream printStream = System.out;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("XXXXXXXXXXXXX Error en: ");
                                    sb2.append(e.getMessage());
                                    printStream.println(sb2.toString());
                                }
                            }
                        });
                        return;
                    }
                    ((MaterialButton) SlideshowFragment.this.cancBt.get(this.val$finalI)).setText("Abrir Link");
                    ((MaterialButton) SlideshowFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            try {
                                Statement access$000 = SlideshowFragment.this.f90st;
                                StringBuilder sb = new StringBuilder();
                                sb.append("getBecadata ");
                                sb.append(((Beca) SlideshowFragment.this.becas.get(i)).getId());
                                ResultSet executeQuery = access$000.executeQuery(sb.toString());
                                if (executeQuery.next()) {
                                    String str = new String(executeQuery.getBytes("Datos"), StandardCharsets.UTF_8);
                                    if (!str.startsWith("www")) {
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("www.");
                                        sb2.append(str);
                                        str = sb2.toString();
                                    }
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("http://");
                                    sb3.append(str);
                                    intent.setData(Uri.parse(sb3.toString()));
                                    SlideshowFragment.this.parent.getApplicationContext().startActivity(intent);
                                }
                            } catch (SQLException e) {
                                PrintStream printStream = System.out;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("XXXXXXXXXXXXXXXXX Error en: ");
                                sb4.append(e.getMessage());
                                printStream.println(sb4.toString());
                            }
                        }
                    });
                }
            });
            i++;
        }
        this.cBeca += i2;
    }
}
