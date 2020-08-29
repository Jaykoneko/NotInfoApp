package com.example.notinfoapp.p006ui.home;

import android.app.Activity;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.notinfoapp.C0632R;
import com.example.notinfoapp.MainActivity;
import com.example.notinfoapp.PDFViewer;
import com.example.notinfoapp.p006ui.slideshow.SlideshowViewModel;
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

/* renamed from: com.example.notinfoapp.ui.home.HomeFragment */
public class HomeFragment extends Fragment {
    /* access modifiers changed from: private */
    public ScrollView ScrollB;
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
    private View roott;
    /* access modifiers changed from: private */
    public ArrayList<TextView> sTitulos;
    private SlideshowViewModel slideshowViewModel;
    /* access modifiers changed from: private */

    /* renamed from: st */
    public Statement f86st;
    /* access modifiers changed from: private */
    public int tNoticias;
    /* access modifiers changed from: private */
    public ArrayList<TextView> titulos;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        HomeViewModel homeViewModel = (HomeViewModel) ViewModelProviders.m8of((Fragment) this).get(HomeViewModel.class);
        View inflate = layoutInflater.inflate(C0632R.layout.fragment_home, viewGroup, false);
        initUI(inflate);
        FragmentActivity activity = getActivity();
        this.parent = activity;
        activity.setRequestedOrientation(1);
        this.roott = inflate;
        ((MainActivity) getActivity()).setCurrent(this);
        getNot();
        ((ImageButton) this.botones.get(0)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (HomeFragment.this.cNoticia - 10 <= 0) {
                    ((ImageButton) HomeFragment.this.botones.get(0)).setVisibility(8);
                    HomeFragment.this.cNoticia = 0;
                    HomeFragment homeFragment = HomeFragment.this;
                    homeFragment.postNoticias(homeFragment.cNoticia);
                    HomeFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            HomeFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                } else {
                    HomeFragment homeFragment2 = HomeFragment.this;
                    homeFragment2.cNoticia = homeFragment2.cNoticia - 10;
                    HomeFragment homeFragment3 = HomeFragment.this;
                    homeFragment3.postNoticias(homeFragment3.cNoticia);
                    HomeFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            HomeFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                }
                ((ImageButton) HomeFragment.this.botones.get(1)).setVisibility(0);
            }
        });
        ((ImageButton) this.botones.get(1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((ImageButton) HomeFragment.this.botones.get(0)).setVisibility(0);
                HomeFragment homeFragment = HomeFragment.this;
                homeFragment.postNoticias(homeFragment.cNoticia);
                HomeFragment.this.ScrollB.post(new Runnable() {
                    public void run() {
                        HomeFragment.this.ScrollB.scrollTo(0, 0);
                    }
                });
                if (HomeFragment.this.cNoticia >= HomeFragment.this.tNoticias) {
                    ((ImageButton) HomeFragment.this.botones.get(1)).setVisibility(8);
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
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.imgc1));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.imgc2));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.imgc3));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.imgc4));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.imgc5));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.txtT1));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.txtT2));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.txtT3));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.txtT4));
        this.titulos.add((TextView) view.findViewById(C0632R.C0635id.txtT5));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.txtSt1));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.txtSt2));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.txtSt3));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.txtSt4));
        this.sTitulos.add((TextView) view.findViewById(C0632R.C0635id.txtSt5));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.btnC1));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.btnC2));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.btnC3));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.btnC4));
        this.cancBt.add((MaterialButton) view.findViewById(C0632R.C0635id.btnC5));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.txtR1));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.txtR2));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.txtR3));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.txtR4));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.txtR5));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.card));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.card2));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.card3));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.card4));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.card5));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.btnAtr));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.btnAd));
        ScrollView scrollView = (ScrollView) view.findViewById(C0632R.C0635id.ScrollB);
        this.ScrollB = scrollView;
        scrollView.post(new Runnable() {
            public void run() {
                HomeFragment.this.ScrollB.scrollTo(0, 0);
            }
        });
        loadAnim();
    }

    public void loadAnim() {
        for (int i = 0; i < 5; i++) {
            Glide.with((Fragment) this).load(Integer.valueOf(C0632R.C0634drawable.utn)).into((ImageView) this.imgs.get(i));
        }
    }

    public void postNoticias(final int i) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~ Posteando noticias desde el index ");
        sb.append(i);
        printStream.println(sb.toString());
        int i2 = this.tNoticias - this.cNoticia;
        if (i2 >= 5) {
            i2 = 5;
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                if (i3 >= i2) {
                    ((MaterialCardView) this.cards.get(i3)).setVisibility(8);
                }
            }
        }
        if (i == 0) {
            for (int i4 = 0; i4 < 5; i4++) {
                ((MaterialCardView) this.cards.get(i4)).setVisibility(0);
            }
        }
        for (final int i5 = 0; i5 < i2; i5++) {
            this.parent.runOnUiThread(new Runnable(i5) {
                final /* synthetic */ int val$finalI;

                {
                    this.val$finalI = r2;
                }

                public void run() {
                    ((MaterialCardView) HomeFragment.this.cards.get(this.val$finalI)).setVisibility(0);
                    ((TextView) HomeFragment.this.titulos.get(this.val$finalI)).setText(((Noticia) HomeFragment.this.noticias.get(i)).getNombre());
                    ((TextView) HomeFragment.this.sTitulos.get(this.val$finalI)).setText(((Noticia) HomeFragment.this.noticias.get(i)).getFecha());
                    ((TextView) HomeFragment.this.resumenes.get(this.val$finalI)).setText(((Noticia) HomeFragment.this.noticias.get(i)).getResumen());
                    ((ImageView) HomeFragment.this.imgs.get(i5)).setScaleType(ScaleType.CENTER_INSIDE);
                    ((ImageView) HomeFragment.this.imgs.get(this.val$finalI)).setImageBitmap(BitmapFactory.decodeByteArray(((Noticia) HomeFragment.this.noticias.get(i)).getImagen(), 0, ((Noticia) HomeFragment.this.noticias.get(i)).getImagen().length));
                    if (((Noticia) HomeFragment.this.noticias.get(i)).getTipo() == 1) {
                        ((MaterialButton) HomeFragment.this.cancBt.get(this.val$finalI)).setText("Abrir PDF");
                        ((MaterialButton) HomeFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    File createTempFile = File.createTempFile("temp", ".pdf");
                                    createTempFile.createNewFile();
                                    FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
                                    Statement access$1100 = HomeFragment.this.f86st;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("getData ");
                                    sb.append(((Noticia) HomeFragment.this.noticias.get(i)).getId());
                                    ResultSet executeQuery = access$1100.executeQuery(sb.toString());
                                    if (executeQuery.next()) {
                                        fileOutputStream.write(executeQuery.getBytes("Datos"));
                                        fileOutputStream.flush();
                                        createTempFile.deleteOnExit();
                                        PrintStream printStream = System.out;
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("********************** Tamaño: ");
                                        sb2.append(createTempFile.length());
                                        printStream.println(sb2.toString());
                                        System.out.println("---------------- Abriendo pdf");
                                        Intent intent = new Intent(HomeFragment.this.parent.getApplicationContext(), PDFViewer.class);
                                        intent.putExtra("file", createTempFile.toPath().toString());
                                        HomeFragment.this.parent.startActivity(intent);
                                    }
                                } catch (Exception unused) {
                                }
                            }
                        });
                        return;
                    }
                    ((MaterialButton) HomeFragment.this.cancBt.get(this.val$finalI)).setText("Abrir Link");
                    ((MaterialButton) HomeFragment.this.cancBt.get(this.val$finalI)).setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            try {
                                Statement access$1100 = HomeFragment.this.f86st;
                                StringBuilder sb = new StringBuilder();
                                sb.append("getData ");
                                sb.append(((Noticia) HomeFragment.this.noticias.get(i)).getId());
                                ResultSet executeQuery = access$1100.executeQuery(sb.toString());
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
                                    HomeFragment.this.parent.getApplicationContext().startActivity(intent);
                                }
                            } catch (Exception unused) {
                            }
                        }
                    });
                }
            });
            i++;
        }
        this.cNoticia += i2;
    }

    public void getNot() {
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    StrictMode.setThreadPolicy(new Builder().permitAll().build());
                    Connection connection = DriverManager.getConnection(HomeFragment.this.getString(C0632R.string.connection_string));
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    HomeFragment.this.f86st = connection.createStatement();
                    ResultSet executeQuery = HomeFragment.this.f86st.executeQuery("getNoticias");
                    HomeFragment.this.noticias = new ArrayList();
                    ArrayList arrayList = new ArrayList();
                    int i = 0;
                    while (executeQuery.next()) {
                        Noticia noticia = new Noticia(executeQuery.getString("Nombre"), executeQuery.getString("Fecha_publicacion"), executeQuery.getString("Resumen"), executeQuery.getBytes("Imagen"), executeQuery.getInt("idNoticia"), executeQuery.getInt("Tipo"));
                        arrayList.add(noticia);
                        System.out.println("~~~~~~~~~~~~~ Añadiendo Noticia ~~~~~~~~~~~~");
                        i++;
                    }
                    for (int size = arrayList.size() - 1; size > 0; size--) {
                        HomeFragment.this.noticias.add(arrayList.get(size));
                    }
                    if (i == 0) {
                        HomeFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(HomeFragment.this.getContext(), "No hay noticias que mostrar", 1).show();
                                for (int i = 0; i < HomeFragment.this.cards.size(); i++) {
                                    ((MaterialCardView) HomeFragment.this.cards.get(i)).setVisibility(8);
                                }
                            }
                        });
                        return;
                    }
                    HomeFragment.this.tNoticias = i - 1;
                    HomeFragment.this.cNoticia = 0;
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append(i);
                    sb.append(" noticias añadidas correctamente");
                    printStream.println(sb.toString());
                    HomeFragment.this.postNoticias(0);
                } catch (SQLException e) {
                    PrintStream printStream2 = System.out;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("XXXXXXXXXXXXX Algo salió mal en: ");
                    sb2.append(e.getMessage());
                    printStream2.println(sb2.toString());
                }
            }
        });
    }

    public void Reload() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(getId(), new HomeFragment()).commit();
    }
}
