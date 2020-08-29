package com.example.notinfoapp.p006ui.slideshow;

/* renamed from: com.example.notinfoapp.ui.slideshow.Beca */
/* compiled from: SlideshowFragment */
class Beca {
    private byte[] datos;

    /* renamed from: id */
    private int f89id;
    private byte[] imagen;
    private String resumen;
    private String subtitulo;
    private int tipo;
    private String titulo;

    public Beca(int i, String str, String str2, String str3, byte[] bArr, int i2) {
        this.f89id = i;
        this.titulo = str;
        this.subtitulo = str2;
        this.resumen = str3;
        this.imagen = bArr;
        this.tipo = i2;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getSubtitulo() {
        return this.subtitulo;
    }

    public String getResumen() {
        return this.resumen;
    }

    public byte[] getImagen() {
        return this.imagen;
    }

    public int getId() {
        return this.f89id;
    }

    public byte[] getDatos() {
        return this.datos;
    }

    public void setDatos(byte[] bArr) {
        this.datos = bArr;
    }

    public int getTipo() {
        return this.tipo;
    }
}
