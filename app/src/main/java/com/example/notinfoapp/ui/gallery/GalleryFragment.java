package com.example.notinfoapp.ui.gallery;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.notinfoapp.MainActivity;
import com.example.notinfoapp.PDFViewer;
import com.example.notinfoapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GalleryFragment extends Fragment { // Noticias por carrera
    private ArrayList<ImageView> imgs; // ArrayLists de componentes
    private ArrayList<TextView> titulos, sTitulos, resumenes;
    private ArrayList<MaterialButton> cancBt;
    private ArrayList<MaterialCardView> cards;
    private ArrayList<ImageButton> botones;
    private ArrayList<Noticia> noticias;
    protected Activity parent; // Activity para correr runOnUIThread dentro de las AsyncTasks
    private Statement st; // Statement de la bd
    private int cNoticia = 0, tNoticias; // Variables de control de flujo de las noticias

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false); // Cosas raras de fragment
        initUI(root); // Inicializar la UI
        ((MainActivity)getActivity()).setCurrent(this);
        parent=getActivity(); // Obtener activity para el runOnUIThread
        parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Context context=parent.getApplicationContext(); // Obtener context para ejecutar activity dentro de AsyncTasks
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() { // Ejecutar en segundo plano
                try{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
                    StrictMode.setThreadPolicy(policy); // Establecer politica
                    Connection connection = DriverManager.getConnection(getString(R.string.connection_string)); // Datos provisionales para la prueba de bd
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    st = connection.createStatement(); // Crear statement
                    ResultSet rs=st.executeQuery("getNoticiasCarreras"); // Obtener las noticias de las carreras
                    noticias=new ArrayList<>(); // Inicialiar el ArrayList
                    int aux=0; // Variable auxiliar a 0 para iniciar con la primera noticia
                    ArrayList<Noticia> tmp=new ArrayList<>();
                    while(rs.next()){ // Mientras haya noticias
                        //Crear objeto de noticia
                        Noticia temp=new Noticia(rs.getString("Nombre"),rs.getString("Fecha_publicacion"),rs.getString("Resumen"),rs.getBytes("Imagen"),rs.getInt("idNoticia"),rs.getInt("Tipo"));
                        tmp.add(temp); // Añadir noticia al ArrayList
                        System.out.println("~~~~~~~~~~~~~ Añadiendo noticia por carrera ~~~~~~~~~~~~");
                        aux++; // Aumentar el auxiliar en 1 para aumentar el numero de noticias
                    }
                    for(int v=tmp.size()-1;v>0;v--){
                        noticias.add(tmp.get(v));
                    }
                    if(aux==0){ // Si no hay noticias
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() { // Ejecutar en hilo de UI
                                Toast.makeText(context,"No hay noticias que mostrar",Toast.LENGTH_LONG).show(); // Notificar a usuario que no hay noticias
                                for(int i=0;i<cards.size();i++){
                                    cards.get(i).setVisibility(View.GONE); // Poner en GONE todas las CardView
                                }
                            }
                        });
                        return; // Parar la ejecucion de codigo
                    }else{ // Si hay noticias
                        tNoticias=aux; // Establecer el total de noticias
                        if(tNoticias<5){ // Si el total de noticias es menor a 5
                            parent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    botones.get(1).setVisibility(View.GONE); //Ocultar el botón de adelante
                                }
                            });
                        }
                        cNoticia=0; // Poner noticia actual a 0 para iniciar con la primera
                        System.out.println(aux + " noticias por carrera añadidas correctamente");
                    }
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postNoticias(0);
                        }
                    }); // Ejecutar el pose noticias en el UI
                }catch(SQLException ex){
                    System.out.println("XXXXXXXXXXXXX Algo salió mal en: "+ex.getMessage());
                }
            }
        });
        botones.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // Listener del boton atras
                if(cNoticia-10<=0){ // Si la noticia anterior -10
                    botones.get(0).setVisibility(View.GONE); // Ocultar boton atras
                    cNoticia=0; // Establecer noticia actual en 0
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // Correr en hilo UI
                            postNoticias(cNoticia); // Post noticia en 0
                        }
                    });

                }else {
                    cNoticia=cNoticia-10; // Restar 10 a la noticia actual, 5 de las noticias posteadas actualmente y 5 mas para iniciar desde antes de las que se postearán
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // UI
                            postNoticias(cNoticia); // Post de noticia actual
                        }
                    });

                }
                botones.get(1).setVisibility(View.VISIBLE); // Botón adelante visible
            }
        });
        botones.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Listener botón adelante
                botones.get(0).setVisibility(View.VISIBLE); // Poner botón actual a visible (Si se avanza, lógicamente hay noticias atrás)
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postNoticias(cNoticia);
                    }
                }); // Post noticia desde UI
                if(cNoticia>=tNoticias){ // Si la noticia actual es mayor o igual al total
                    botones.get(1).setVisibility(View.GONE); // Ocultar botón adelante
                }
            }
        });
        return root;
    }
    public void initUI(View root) { // Metodo para rellenar los componentes gráficos
        imgs = new ArrayList<>();
        titulos = new ArrayList<>();
        sTitulos = new ArrayList<>();
        cancBt = new ArrayList<>();
        resumenes = new ArrayList<>();
        cards = new ArrayList<>();
        botones = new ArrayList<>();

        imgs.add((ImageView) root.findViewById(R.id.Cimgc1));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc2));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc3));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc4));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc5));

        //--------------------------------

        titulos.add((TextView) root.findViewById(R.id.CtxtT1));
        titulos.add((TextView) root.findViewById(R.id.CtxtT2));
        titulos.add((TextView) root.findViewById(R.id.CtxtT3));
        titulos.add((TextView) root.findViewById(R.id.CtxtT4));
        titulos.add((TextView) root.findViewById(R.id.CtxtT5));

        //--------------------------------

        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt1));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt2));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt3));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt4));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt5));

        //---------------------------------

        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC1));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC2));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC3));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC4));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC5));


        //---------------------------------

        resumenes.add((TextView) root.findViewById(R.id.CtxtR1));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR2));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR3));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR4));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR5));

        //----------------------------------

        cards.add((MaterialCardView)root.findViewById(R.id.Ccard));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard2));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard3));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard4));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard5));

        //----------------------------------

        botones.add((ImageButton) root.findViewById(R.id.CbtnAtr));
        botones.add((ImageButton) root.findViewById(R.id.CbtnAd));


        // Inicializar animaciones de carga de imagenes

        for(int i=0;i<5;i++){
            imgs.get(i).setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(R.drawable.utn).into(imgs.get(i));
        }
    }
    public void postNoticias(int index){ // Mostrar noticias desde un indice
        System.out.println("~~~~~~~~~~~~~ Posteando noticias por carrera desde el index " + index);
        //determinar cuantas cards se van a usar
        int aux=tNoticias-cNoticia; // Determinar la diferencia entre el total de noticias y la noticia actual
        int contint=index; // Variable para llevar la cuenta de la noticia actual
        if(aux>=5){ // Si la diferencia de la noticia actual y el total es mayor o igual a 5
            aux=5; // Poner el auxiliar a 5 para evitar Overflows
            if(tNoticias<5){ // Si el total de noticias es menor a 5
                aux=tNoticias; // El auxiliar es igual al total de noticias
            }
        }else{
            for(int l=0;l<5;l++){ //Recorrer los CardView
                if(l>=aux){ // Si el indice es mayor o igual al auxiliar (Cards que no se usan)
                    final int finalL = l; // Solo finales pueden ser usadas en UI
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // UI
                            cards.get(finalL).setVisibility(View.GONE); // Poner la card a GONE
                        }
                    });
                }
            }
        }
        if(index==0 && tNoticias>5){ // Si el indice (noticia inicial para el metodo) es igual a 0 y el total de noticias es mayor a 5
            for(int k=0;k<5;k++){ // Recorrer los 5 CardView
                final int finalK = k; // Final para el UI
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cards.get(finalK).setVisibility(View.VISIBLE); // Poner visible los Cardview que se usan ( talvez esta de mas, pero mas vale que esté lol)
                    }
                });
            }
        }

        for(int i=0;i<aux;i++){ // Recorrer los cardview desde 0 hasta el número que se vayan a usar
            final int finalI = i; // Final I para el UI
            final int finalContint=contint; // Final Contint para el UI
            final int finalI1 = i;
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() { // UI
                    cards.get(finalI).setVisibility(View.VISIBLE); //Poner el card a usar en visible
                    titulos.get(finalI).setText(noticias.get(finalContint).getNombre()); // Setear el titulo
                    sTitulos.get(finalI).setText(noticias.get(finalContint).getFecha()); // Seter la fecha
                    resumenes.get(finalI).setText(noticias.get(finalContint).getResumen()); // Setear el resumen
                    imgs.get(finalI1).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgs.get(finalI).setImageBitmap(BitmapFactory.decodeByteArray(noticias.get(finalContint).getImagen(),0,noticias.get(finalContint).getImagen().length)); // Setear la imagen
                    if(noticias.get(finalContint).getTipo()==1){// Si la noticia es PDF
                        cancBt.get(finalI).setText("Abrir PDF"); // Cambiar texto del botón a Abrir PDF
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(View v) { // Establecer listener
                                try{
                                    File temp = File.createTempFile("temp",".pdf"); // Crear archivo temporal
                                    //noinspection ResultOfMethodCallIgnored
                                    temp.createNewFile(); // Crear archivo físico
                                    FileOutputStream fos = new FileOutputStream(temp); // Crear stream de escritura para el archivo
                                    ResultSet rs=st.executeQuery("getData "+noticias.get(finalContint).getId()); // Obtener el PDF de la base de datos
                                    if(rs.next()){ // Si hay datos
                                        byte[] dat = rs.getBytes("Datos"); // Guardar el pdf en un byte[]
                                        fos.write(dat); // Escribir el byte[] al archivo
                                        fos.flush(); // Forzar la escritura de los bytes
                                        temp.deleteOnExit(); // Establecer en borrar al salir de la app
                                        Intent intent = new Intent(parent.getApplicationContext(), PDFViewer.class); // Crear intent para abrir el PDFViewer
                                        intent.putExtra("file",temp.toPath().toString()); // Poner el path del archivo en el intent
                                        parent.startActivity(intent); // Ejecutar el PDFViewer
                                    }
                                }catch (Exception ex){
                                    System.out.println("XXXXXXXXXXXXX Error en: "+ex.getMessage());
                                }

                            }
                        });
                    }else{ // Si es Link
                        cancBt.get(finalI).setText("Abrir Link"); // Poner texto del botón en Abrir Link
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { // Listener
                                try {
                                    ResultSet rs = st.executeQuery("getData " + noticias.get(finalContint).getId()); // Descargar link
                                    if(rs.next()) {
                                        String url = new String(rs.getBytes("Datos"), StandardCharsets.UTF_8); // Convertir los bytes del link a string y guardarlo en variable
                                        if(!url.startsWith("www") || !url.startsWith("http") || !url.startsWith("https")){ // Verificar si la url inicia con "www"
                                            url="www."+url; // Si no, agregar el www.
                                        }
                                        Intent in = new Intent(Intent.ACTION_VIEW); // Intent action view para abrir el navegador
                                        in.setData(Uri.parse("http://" + url)); // Agregar el url con el prefijo http://
                                        parent.getApplicationContext().startActivity(in); // Ejecutar activity del PDFViewer
                                    }
                                }catch (SQLException ex){
                                    System.out.println("XXXXXXXXXXXXXXXXX Error en: " + ex.getMessage());
                                }
                            }
                        });
                    }
                }
            });
            contint++; // Aumentar el contint
        }
        cNoticia+=aux; // La noticia actual aumenta en auxilar
    }
}

class Noticia{ // Clase noticia de puros datos.
    public final int id;
    public final int tipo;
    public final String Nombre;
    public final String Fecha;
    public final String Resumen;
    public final byte[] Imagen;

    public Noticia(String Nombre, String Fecha, String Resumen, byte[] Imagen,int id, int tipo){
        this.Nombre=Nombre;
        this.Fecha=Fecha;
        this.Resumen = Resumen;
        this.Imagen = Imagen;
        this.id=id;
        this.tipo=tipo;
    }
    public String getNombre(){
        return Nombre;
    }
    public String getFecha(){
        return Fecha;
    }

    public String getResumen() {
        return Resumen;
    }

    public int getId() {
        return id;
    }

    public int getTipo() {
        return tipo;
    }

    public byte[] getImagen() {
        return Imagen;
    }
}