package com.example.notinfoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.widget.Toast;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NotifService extends Service {
    private static String TAG = "NotifService";
    private int bAnt = 0;
    private int cAnt = 0;
    private Connection con;
    /* access modifiers changed from: private */
    public Handler handler;
    private int mAnt = 0;
    private int nAnt = 0;
    private PendingIntent pendingIntent;
    /* access modifiers changed from: private */
    public Runnable runnable;
    private final int runtime = 4000;

    /* renamed from: st */
    private Statement f63st;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        String str = "NotInfoChannelForeground";
        if (VERSION.SDK_INT >= 26) {
            String str2 = "NotInfoChannel";
            NotificationChannel notificationChannel = new NotificationChannel(str2, str, 3);
            notificationChannel.setDescription("Canal de notificaciones de NotInfo");
            NotificationChannel notificationChannel2 = new NotificationChannel(str2, "NotinfoChannel", 4);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
        startForeground(1, new Builder(this, str).setContentTitle("Bienvenido a NotInfo").setContentText("Disfruta de la información").setContentIntent(this.pendingIntent).build());
        try {
            StrictMode.setThreadPolicy(new ThreadPolicy.Builder().permitAll().build());
            Connection connection = DriverManager.getConnection(getString(C0632R.string.connection_string));
            this.con = connection;
            this.f63st = connection.createStatement();
            checkStatus(0);
        } catch (SQLException e) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("Error en: ");
            sb.append(e.getMessage());
            printStream.println(sb.toString());
        }
        this.handler = new Handler();
        C06291 r0 = new Runnable() {
            public void run() {
                NotifService.this.handler.postDelayed(NotifService.this.runnable, 4000);
                try {
                    NotifService.this.startService(new Intent(NotifService.this, NotifService.class));
                } catch (IllegalStateException unused) {
                    System.out.println("~~~~~~~~~~~~~~~~~ No se puede iniciar el servicio, saltando...");
                }
            }
        };
        this.runnable = r0;
        this.handler.post(r0);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        checkStatus(1);
        return 2;
    }

    public void checkStatus(int i) {
        try {
            ResultSet executeQuery = this.f63st.executeQuery("getNoticiasC");
            String str = "Cant";
            if (executeQuery.next()) {
                if (i == 0) {
                    this.cAnt = executeQuery.getInt(str);
                } else {
                    int i2 = executeQuery.getInt(str);
                    if (i2 > this.cAnt) {
                        this.cAnt = i2;
                        alert(0);
                    }
                    if (i2 < this.cAnt) {
                        this.cAnt = i2;
                    }
                }
            }
            if (executeQuery.next()) {
                if (i == 0) {
                    this.mAnt = executeQuery.getInt(str);
                } else {
                    int i3 = executeQuery.getInt(str);
                    if (i3 > this.mAnt) {
                        this.mAnt = i3;
                        alert(1);
                    }
                    if (i3 < this.mAnt) {
                        this.mAnt = i3;
                    }
                }
            }
            if (executeQuery.next()) {
                if (i == 0) {
                    this.bAnt = executeQuery.getInt(str);
                } else {
                    int i4 = executeQuery.getInt(str);
                    if (i4 > this.bAnt) {
                        this.bAnt = i4;
                        alert(2);
                    }
                    if (i4 < this.bAnt) {
                        this.bAnt = i4;
                    }
                }
            }
            if (!executeQuery.next()) {
                return;
            }
            if (i == 0) {
                this.nAnt = executeQuery.getInt(str);
                return;
            }
            int i5 = executeQuery.getInt(str);
            if (i5 > this.nAnt) {
                this.nAnt = i5;
                alert(3);
            }
            if (i5 < this.nAnt) {
                this.nAnt = i5;
            }
        } catch (SQLException unused) {
        }
    }

    public void alert(int i) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(268468224);
        this.pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        String str = i != 0 ? i != 1 ? i != 2 ? i != 3 ? "" : "Una nueva noticia ha sido publicada!" : "Una nueva beca ha sido publicada!" : "Un nuevo meme ha sido publicado!" : "Un nuevo calendario ha sido publicado!";
        NotificationManagerCompat.from(this).notify(1, new Builder(this, "NotInfoChannel").setSmallIcon(C0632R.C0634drawable.noticia).setContentTitle("Una nueva nota ha sido publicada!").setContentText(str).setPriority(0).setContentIntent(this.pendingIntent).setAutoCancel(true).build());
    }

    public void onTaskRemoved(Intent intent) {
        new Handler(getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(NotifService.this.getBaseContext(), "Task muerta", 1).show();
            }
        });
        Intent intent2 = new Intent(getApplicationContext(), getClass());
        intent2.setPackage(getPackageName());
        startService(intent2);
        super.onTaskRemoved(intent);
    }
}
