package com.example.notinfoapp.p006ui.memes;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import com.google.android.material.card.MaterialCardView;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* renamed from: com.example.notinfoapp.ui.memes.MemesFragment */
public class MemesFragment extends Fragment {
    /* access modifiers changed from: private */
    public ScrollView ScrollB;
    /* access modifiers changed from: private */
    public ArrayList<ImageButton> botones;
    /* access modifiers changed from: private */
    public int cMeme = 0;
    /* access modifiers changed from: private */
    public ArrayList<MaterialCardView> cards;
    /* access modifiers changed from: private */
    public ArrayList<ImageView> imgs;
    /* access modifiers changed from: private */
    public ArrayList<Memes> memes;
    private MemesViewModel memesViewModel;
    protected Activity parent;
    /* access modifiers changed from: private */
    public ArrayList<TextView> resumenes;
    private View roott;
    /* access modifiers changed from: private */

    /* renamed from: st */
    public Statement f88st;
    /* access modifiers changed from: private */
    public int tMemes;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        MemesViewModel memesViewModel2 = (MemesViewModel) ViewModelProviders.m8of((Fragment) this).get(MemesViewModel.class);
        View inflate = layoutInflater.inflate(C0632R.layout.fragment_memes, viewGroup, false);
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
                    MemesFragment.this.f88st = DriverManager.getConnection(MemesFragment.this.getString(C0632R.string.connection_string)).createStatement();
                    ResultSet executeQuery = MemesFragment.this.f88st.executeQuery("getMeme");
                    MemesFragment.this.memes = new ArrayList();
                    int i = 0;
                    while (executeQuery.next()) {
                        MemesFragment.this.memes.add(new Memes(executeQuery.getInt("id_meme"), executeQuery.getString("contenido_meme"), executeQuery.getBytes("imagen_meme"), executeQuery.getString("Fecha_publicacion")));
                        System.out.println("~~~~~~~~~~~~~ Añadiendo meme ~~~~~~~~~~~~");
                        i++;
                    }
                    if (i == 0) {
                        MemesFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(applicationContext, "No hay contenido que mostrar", 1).show();
                                for (int i = 0; i < MemesFragment.this.cards.size(); i++) {
                                    ((MaterialCardView) MemesFragment.this.cards.get(i)).setVisibility(8);
                                }
                            }
                        });
                        return;
                    }
                    if (i > 3) {
                        MemesFragment.this.tMemes = i - 1;
                    } else {
                        MemesFragment.this.tMemes = i;
                    }
                    MemesFragment.this.cMeme = 0;
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append(i);
                    sb.append(" memes añadidas correctamente");
                    printStream.println(sb.toString());
                    MemesFragment.this.parent.runOnUiThread(new Runnable() {
                        public void run() {
                            MemesFragment.this.postMemes(0);
                            if (MemesFragment.this.tMemes < 4) {
                                ((ImageButton) MemesFragment.this.botones.get(0)).setVisibility(8);
                            }
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
                if (MemesFragment.this.cMeme - 10 <= 0) {
                    ((ImageButton) MemesFragment.this.botones.get(0)).setVisibility(8);
                    MemesFragment.this.cMeme = 0;
                    MemesFragment memesFragment = MemesFragment.this;
                    memesFragment.postMemes(memesFragment.cMeme);
                    MemesFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            MemesFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                } else {
                    MemesFragment memesFragment2 = MemesFragment.this;
                    memesFragment2.cMeme = memesFragment2.cMeme - 10;
                    MemesFragment memesFragment3 = MemesFragment.this;
                    memesFragment3.postMemes(memesFragment3.cMeme);
                    MemesFragment.this.ScrollB.post(new Runnable() {
                        public void run() {
                            MemesFragment.this.ScrollB.scrollTo(0, 0);
                        }
                    });
                }
                ((ImageButton) MemesFragment.this.botones.get(1)).setVisibility(0);
            }
        });
        ((ImageButton) this.botones.get(1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((ImageButton) MemesFragment.this.botones.get(0)).setVisibility(0);
                MemesFragment memesFragment = MemesFragment.this;
                memesFragment.postMemes(memesFragment.cMeme);
                MemesFragment.this.ScrollB.post(new Runnable() {
                    public void run() {
                        MemesFragment.this.ScrollB.scrollTo(0, 0);
                    }
                });
                if (MemesFragment.this.cMeme >= MemesFragment.this.tMemes) {
                    ((ImageButton) MemesFragment.this.botones.get(1)).setVisibility(8);
                }
            }
        });
        return inflate;
    }

    public void postMemes(final int i) {
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~ Posteando memes desde el index ");
        sb.append(i);
        printStream.println(sb.toString());
        int i2 = this.tMemes - this.cMeme;
        if (i2 >= 5) {
            i2 = 5;
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                if (i3 >= i2) {
                    ((MaterialCardView) this.cards.get(i3)).setVisibility(8);
                }
            }
        }
        for (final int i4 = 0; i4 < i2; i4++) {
            this.parent.runOnUiThread(new Runnable(i4) {
                final /* synthetic */ int val$finalI;

                {
                    this.val$finalI = r2;
                }

                public void run() {
                    ((MaterialCardView) MemesFragment.this.cards.get(this.val$finalI)).setVisibility(0);
                    ((TextView) MemesFragment.this.resumenes.get(this.val$finalI)).setText(((Memes) MemesFragment.this.memes.get(i)).getContenido_meme());
                    ((ImageView) MemesFragment.this.imgs.get(i4)).setScaleType(ScaleType.CENTER_INSIDE);
                    ((ImageView) MemesFragment.this.imgs.get(this.val$finalI)).setImageBitmap(BitmapFactory.decodeByteArray(((Memes) MemesFragment.this.memes.get(i)).getImagen_meme(), 0, ((Memes) MemesFragment.this.memes.get(i)).getImagen_meme().length));
                }
            });
            i++;
        }
        this.cMeme += i2;
        if (this.tMemes < 4) {
            ((ImageButton) this.botones.get(1)).setVisibility(8);
        }
    }

    public void initUI(View view) {
        this.imgs = new ArrayList<>();
        this.resumenes = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.botones = new ArrayList<>();
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Mimgc1));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Mimgc2));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Mimgc3));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Mimgc4));
        this.imgs.add((ImageView) view.findViewById(C0632R.C0635id.Mimgc5));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.MtxtR1));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.MtxtR2));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.MtxtR3));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.MtxtR4));
        this.resumenes.add((TextView) view.findViewById(C0632R.C0635id.MtxtR5));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Mcard));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Mcard2));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Mcard3));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Mcard4));
        this.cards.add((MaterialCardView) view.findViewById(C0632R.C0635id.Mcard5));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.MbtnAtr));
        this.botones.add((ImageButton) view.findViewById(C0632R.C0635id.MbtnAd));
        ScrollView scrollView = (ScrollView) view.findViewById(C0632R.C0635id.ScrollM);
        this.ScrollB = scrollView;
        scrollView.post(new Runnable() {
            public void run() {
                MemesFragment.this.ScrollB.scrollTo(0, 0);
            }
        });
        for (int i = 0; i < 5; i++) {
            ((ImageView) this.imgs.get(i)).setScaleType(ScaleType.FIT_CENTER);
            Glide.with((Fragment) this).load(Integer.valueOf(C0632R.C0634drawable.utn)).into((ImageView) this.imgs.get(i));
        }
    }
}
