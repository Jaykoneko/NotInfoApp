package com.example.notinfoapp.p006ui.gallery;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* renamed from: com.example.notinfoapp.ui.gallery.GalleryFragment */
public class GalleryFragment extends Fragment {
    /* access modifiers changed from: private */
    public ArrayList<ImageButton> botones;
    /* access modifiers changed from: private */
    public int cNoticia = 0;
    /* access modifiers changed from: private */
    public ArrayList<MaterialButton> cancBt;
    /* access modifiers changed from: private */
    public ArrayList<MaterialCardView> cards;
    /* access modifiers changed from: private */
    public ArrayList<ImageView> imgs;
    /* access modifiers changed from: private */
    public ArrayList<Noticia> noticias;
    protected Activity parent;
    /* access modifiers changed from: private */
    public ArrayList<TextView> resumenes;
    /* access modifiers changed from: private */
    public ArrayList<TextView> sTitulos;
    /* access modifiers changed from: private */

    /* renamed from: st */
    public Statement f84st;
    /* access modifiers changed from: private */
    public int tNoticias;
    /* access modifiers changed from: private */
    public ArrayList<TextView> titulos;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        GalleryViewModel galleryViewModel = (GalleryViewModel) ViewModelProviders.m8of((Fragment) this).get(GalleryViewModel.class);
        View inflate = layoutInflater.inflate(C0632R.layout.fragment_gallery, viewGroup, false);
        initUI(inflate);
        ((MainActivity) getActivity()).setCurrent(this);
        FragmentActivity activity = getActivity();
        this.parent = activity;
        activity.setRequestedOrientation(1);
        final Context applicationContext = this.parent.getApplicationContext();
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    StrictMode.setThreadPolicy(new Builder().permitAll().build());
                    Connection connection = DriverManager.getConnection(GalleryFragment.this.getString(C0632R.string.connection_string));
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    GalleryFragment.this.f84st = connection.createStatement();
                    ResultSet executeQuery = GalleryFragment.this.f84st.executeQuery("getNoticiasCarreras");
                    GalleryFragment.this.noticias = new ArrayList();
                    int i = 0;
                    while (executeQuery.next()) {
                        Noticia noticia = new Noticia(executeQuery.getString("Nombre"), executeQuery.getString("Fecha_publicacion"), executeQuery.getString("Resumen"), executeQuery.getBytes("Imagen"), executeQuery.getInt("idNoticia"), executeQuery.getInt("Tipo"));
                        GalleryFragment.this.noticias.add(noticia);
                        System.out.println("~~~~~~~~~~~~~ Añadiendo noticia por carrera ~~~~~~~~~~~~");
                        i++;
                    }
                    if (i == 0) {
                        GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(applicationContext, "No hay noticias que mostrar", 1).show();
                                for (int i = 0; i < GalleryFragment.this.cards.size(); i++) {
                                    ((MaterialCardView) GalleryFragment.this.cards.get(i)).setVisibility(8);
                                }
                            }
                        });
                        return;
                    }
                    GalleryFragment.this.tNoticias = i - 1;
                    if (GalleryFragment.this.tNoticias < 5) {
                        GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                ((ImageButton) GalleryFragment.this.botones.get(1)).setVisibility(8);
                            }
                        });
                    }
                    GalleryFragment.this.cNoticia = 0;
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append(i);
                    sb.append(" becas añadidas correctamente");
                    printStream.println(sb.toString());
                    GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            GalleryFragment.this.postNoticias(0);
                        }
                    });
                } catch (SQLException e) {
                    PrintStream printStream2 = System.out;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("XXXXXXXXXXXXX Algo salió mal en: ");
                    sb2.append(e.getMessage());
                    printStream2.println(sb2.toString());
                }
            }
        });
        ((ImageButton) this.botones.get(0)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (GalleryFragment.this.cNoticia - 10 <= 0) {
                    ((ImageButton) GalleryFragment.this.botones.get(0)).setVisibility(8);
                    GalleryFragment.this.cNoticia = 0;
                    GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            GalleryFragment.this.postNoticias(GalleryFragment.this.cNoticia);
                        }
                    });
                } else {
                    GalleryFragment galleryFragment = GalleryFragment.this;
                    galleryFragment.cNoticia = galleryFragment.cNoticia - 10;
                    GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            GalleryFragment.this.postNoticias(GalleryFragment.this.cNoticia);
                        }
                    });
                }
                ((ImageButton) GalleryFragment.this.botones.get(1)).setVisibility(0);
            }
        });
        ((ImageButton) this.botones.get(1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((ImageButton) GalleryFragment.this.botones.get(0)).setVisibility(0);
                GalleryFragment.this.parent.runOnUiThread(new Runnable() {
                    public void run() {
                        GalleryFragment.this.postNoticias(GalleryFragment.this.cNoticia);
                    }
                });
                if (GalleryFragment.this.cNoticia >= GalleryFragment.this.tNoticias) {
                    ((ImageButton) GalleryFragment.this.botones.get(1)).setVisibility(8);
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
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Cimgc1));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Cimgc2));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Cimgc3));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Cimgc4));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Cimgc5));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtT1));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtT2));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtT3));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtT4));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtT5));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtSt1));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtSt2));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtSt3));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtSt4));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.CtxtSt5));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.CbtnC1));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.CbtnC2));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.CbtnC3));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.CbtnC4));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.CbtnC5));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.CtxtR1));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.CtxtR2));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.CtxtR3));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.CtxtR4));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.CtxtR5));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Ccard));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Ccard2));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Ccard3));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Ccard4));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Ccard5));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.CbtnAtr));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.CbtnAd));
        for (int i = 0; i < 5; i++) {
            ((ImageView) this.imgs.get(i)).setScaleType(ScaleType.FIT_CENTER);
            Glide.with((Fragment) this).load(Integer.valueOf(C0632R.C0634drawable.utn)).into((ImageView) this.imgs.get(i));
        }
    }

    public void postNoticias(final int i) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~ Posteando noticias por carrera desde el index ");
        sb.append(i);
        printStream.println(sb.toString());
        int i2 = this.tNoticias;
        int i3 = i2 - this.cNoticia;
        if (i3 < 5) {
            for (final int i4 = 0; i4 < 5; i4++) {
                if (i4 >= i3) {
                    this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            ((MaterialCardView) GalleryFragment.this.cards.get(i4)).setVisibility(8);
                        }
                    });
                }
            }
            i2 = i3;
        } else if (i2 >= 5) {
            i2 = 5;
        }
        if (i == 0 && this.tNoticias > 5) {
            for (final int i5 = 0; i5 < 5; i5++) {
                this.parent.runOnUiThread(new Runnable() {
                    public void run() {
                        ((MaterialCardView) GalleryFragment.this.cards.get(i5)).setVisibility(0);
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
                    ((MaterialCardView) GalleryFragment.this.cards.get(this.val$finalI)).setVisibility(0);
                    ((TextView) GalleryFragment.this.titulos.get(this.val$finalI)).setText(((Noticia) GalleryFragment.this.noticias.get(i)).getNombre());
                    ((TextView) GalleryFragment.this.sTitulos.get(this.val$finalI)).setText(((Noticia) GalleryFragment.this.noticias.get(i)).getFecha());
                    ((TextView) GalleryFragment.this.resumenes.get(this.val$finalI)).setText(((Noticia) GalleryFragment.this.noticias.get(i)).getResumen());
                    ((ImageView) GalleryFragment.this.imgs.get(i6)).setScaleType(ScaleType.CENTER_INSIDE);
                    ((ImageView) GalleryFragment.this.imgs.get(this.val$finalI)).setImageBitmap(BitmapFactory.decodeByteArray(((Noticia) GalleryFragment.this.noticias.get(i)).getImagen(), 0, ((Noticia) GalleryFragment.this.noticias.get(i)).getImagen().length));
                    if (((Noticia) GalleryFragment.this.noticias.get(i)).getTipo() == 1) {
                        ((MaterialButton) GalleryFragment.this.cancBt.get(this.val$finalI)).setText("Abrir PDF");
                        ((MaterialButton) GalleryFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    File createTempFile = File.createTempFile("temp", ".pdf");
                                    createTempFile.createNewFile();
                                    FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
                                    Statement access$000 = GalleryFragment.this.f84st;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("getData ");
                                    sb.append(((Noticia) GalleryFragment.this.noticias.get(i)).getId());
                                    ResultSet executeQuery = access$000.executeQuery(sb.toString());
                                    if (executeQuery.next()) {
                                        fileOutputStream.write(executeQuery.getBytes("Datos"));
                                        fileOutputStream.flush();
                                        createTempFile.deleteOnExit();
                                        Intent intent = new Intent(GalleryFragment.this.parent.getApplicationContext(), PDFViewer.class);
                                        intent.putExtra("file", createTempFile.toPath().toString());
                                        GalleryFragment.this.parent.startActivity(intent);
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
                    ((MaterialButton) GalleryFragment.this.cancBt.get(this.val$finalI)).setText("Abrir Link");
                    ((MaterialButton) GalleryFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            try {
                                Statement access$000 = GalleryFragment.this.f84st;
                                StringBuilder sb = new StringBuilder();
                                sb.append("getData ");
                                sb.append(((Noticia) GalleryFragment.this.noticias.get(i)).getId());
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
                                    GalleryFragment.this.parent.getApplicationContext().startActivity(intent);
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
        this.cNoticia += i2;
    }
}
