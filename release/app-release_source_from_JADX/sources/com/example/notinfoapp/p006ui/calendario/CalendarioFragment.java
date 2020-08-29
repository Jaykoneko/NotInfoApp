package com.example.notinfoapp.p006ui.calendario;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.notinfoapp.C0632R;
import com.example.notinfoapp.MainActivity;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/* renamed from: com.example.notinfoapp.ui.calendario.CalendarioFragment */
public class CalendarioFragment extends Fragment {
    /* access modifiers changed from: private */
    public ImageView calendario;
    /* access modifiers changed from: private */
    public Connection connection;
    /* access modifiers changed from: private */
    public TextView fecha;
    /* access modifiers changed from: private */
    public float mScaleFactor = 1.0f;
    /* access modifiers changed from: private */
    public Activity parent;
    /* access modifiers changed from: private */
    public ScaleGestureDetector sgd;
    /* access modifiers changed from: private */
    public TextView titl;

    /* renamed from: com.example.notinfoapp.ui.calendario.CalendarioFragment$ScaleListener */
    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            CalendarioFragment calendarioFragment = CalendarioFragment.this;
            calendarioFragment.mScaleFactor = calendarioFragment.mScaleFactor * scaleGestureDetector.getScaleFactor();
            CalendarioFragment calendarioFragment2 = CalendarioFragment.this;
            calendarioFragment2.mScaleFactor = Math.max(0.1f, Math.min(calendarioFragment2.mScaleFactor, 10.0f));
            CalendarioFragment.this.calendario.setScaleX(CalendarioFragment.this.mScaleFactor);
            CalendarioFragment.this.calendario.setScaleY(CalendarioFragment.this.mScaleFactor);
            return true;
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        CalendarioViewModel calendarioViewModel = (CalendarioViewModel) ViewModelProviders.m8of((Fragment) this).get(CalendarioViewModel.class);
        View inflate = layoutInflater.inflate(C0632R.layout.fragment_calendario, viewGroup, false);
        inflate.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CalendarioFragment.this.sgd.onTouchEvent(motionEvent);
                return true;
            }
        });
        this.calendario = (ImageView) inflate.findViewById(C0632R.C0635id.calImg);
        this.fecha = (TextView) inflate.findViewById(C0632R.C0635id.fecha);
        this.titl = (TextView) inflate.findViewById(C0632R.C0635id.calendario);
        FragmentActivity activity = getActivity();
        this.parent = activity;
        activity.setRequestedOrientation(0);
        this.calendario.setScaleType(ScaleType.FIT_CENTER);
        this.sgd = new ScaleGestureDetector(getContext(), new ScaleListener());
        ((MainActivity) getActivity()).setCurrent(this);
        Glide.with((Fragment) this).load(Integer.valueOf(C0632R.C0634drawable.utn)).into(this.calendario);
        AsyncTask.execute(new Runnable() {
            public void run() {
                System.out.println("°°°°°°°°°°°°°° doInBK iniciado °°°°°°°°°°°°°°");
                try {
                    StrictMode.setThreadPolicy(new Builder().permitAll().build());
                    CalendarioFragment.this.connection = DriverManager.getConnection(CalendarioFragment.this.getString(C0632R.string.connection_string));
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    Statement createStatement = CalendarioFragment.this.connection.createStatement();
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Statement listo °°°°°°°°°°°°°°°°°°");
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Listo para ejecutar operaciones con la base de datos :D °°°°°°°°°°°°°°°°°°");
                    ResultSet executeQuery = createStatement.executeQuery("getCalendarioA");
                    if (executeQuery.next()) {
                        final byte[] bytes = executeQuery.getBytes("Imagen_Calendario");
                        final String string = executeQuery.getString("fecha");
                        final String string2 = executeQuery.getString("titulo_Calendario");
                        CalendarioFragment.this.parent.runOnUiThread(new Runnable() {
                            public void run() {
                                CalendarioFragment.this.calendario.setScaleType(ScaleType.CENTER_CROP);
                                ImageView access$300 = CalendarioFragment.this.calendario;
                                byte[] bArr = bytes;
                                access$300.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
                                TextView access$400 = CalendarioFragment.this.fecha;
                                StringBuilder sb = new StringBuilder();
                                sb.append("Fecha de actualización: ");
                                sb.append(string);
                                access$400.setText(sb.toString());
                                CalendarioFragment.this.titl.setText(string2);
                            }
                        });
                    }
                } catch (Exception e) {
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append("XXXXXXXXXXXXXXXXXXXXXXXX Error en: ");
                    sb.append(e.getMessage());
                    sb.append(" XXXXXXXXXXXXXXXX");
                    printStream.println(sb.toString());
                }
            }
        });
        return inflate;
    }
}
