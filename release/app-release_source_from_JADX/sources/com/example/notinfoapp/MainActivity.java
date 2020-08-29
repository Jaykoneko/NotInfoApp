package com.example.notinfoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.p005ui.AppBarConfiguration;
import androidx.navigation.p005ui.NavigationUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private Fragment current;
    private AppBarConfiguration mAppBarConfiguration;
    private View view;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        super.onCreate(bundle);
        Intent intent = new Intent(getApplicationContext(), NotifService.class);
        intent.putExtra("KEY1", 1);
        ContextCompat.startForegroundService(this, intent);
        setContentView((int) C0632R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(C0632R.C0635id.toolbar));
        ((FloatingActionButton) findViewById(C0632R.C0635id.fab)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.refresh();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(C0632R.C0635id.nav_view);
        this.mAppBarConfiguration = new AppBarConfiguration.Builder(C0632R.C0635id.nav_home, C0632R.C0635id.nav_gallery, C0632R.C0635id.nav_slideshow, C0632R.C0635id.nav_calendario, C0632R.C0635id.nav_memes).setDrawerLayout((DrawerLayout) findViewById(C0632R.C0635id.drawer_layout)).build();
        NavController findNavController = Navigation.findNavController(this, C0632R.C0635id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) this, findNavController, this.mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, findNavController);
    }

    public void refresh() {
        Snackbar.make(this.current.getView(), (CharSequence) "Recargando últimas noticias", 0).setAction((CharSequence) "Segundo", (OnClickListener) null).show();
        FragmentTransaction beginTransaction = this.current.getParentFragmentManager().beginTransaction();
        beginTransaction.detach(this.current);
        beginTransaction.attach(this.current);
        beginTransaction.commit();
    }

    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, C0632R.C0635id.nav_host_fragment), this.mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void setCurrent(Fragment fragment) {
        this.current = fragment;
    }

    public void onResume() {
        super.onResume();
        if (this.current.getView() != null) {
            refresh();
        }
    }
}
