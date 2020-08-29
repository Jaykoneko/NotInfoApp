package com.example.notinfoapp.p006ui.gallery;

/* renamed from: com.example.notinfoapp.ui.gallery.Noticia */
/* compiled from: GalleryFragment */
class Noticia {
    public final String Fecha;
    public final byte[] Imagen;
    public final String Nombre;
    public final String Resumen;

    /* renamed from: id */
    public final int f85id;
    public final int tipo;

    public Noticia(String str, String str2, String str3, byte[] bArr, int i, int i2) {
        this.Nombre = str;
        this.Fecha = str2;
        this.Resumen = str3;
        this.Imagen = bArr;
        this.f85id = i;
        this.tipo = i2;
    }

    public String getNombre() {
        return this.Nombre;
    }

    public String getFecha() {
        return this.Fecha;
    }

    public String getResumen() {
        return this.Resumen;
    }

    public int getId() {
        return this.f85id;
    }

    public int getTipo() {
        return this.tipo;
    }

    public byte[] getImagen() {
        return this.Imagen;
    }
}
