package com.example.notinfoapp.p006ui.memes;

/* renamed from: com.example.notinfoapp.ui.memes.Memes */
/* compiled from: MemesFragment */
class Memes {
    private String Fecha_publicacion;
    private String contenido_meme;
    private int id_meme;
    private byte[] imagen_meme;

    public Memes(int i, String str, byte[] bArr, String str2) {
        this.id_meme = i;
        this.contenido_meme = str;
        this.imagen_meme = bArr;
        this.Fecha_publicacion = str2;
    }

    public int getId_meme() {
        return this.id_meme;
    }

    public void setId_meme(int i) {
        this.id_meme = i;
    }

    public String getContenido_meme() {
        return this.contenido_meme;
    }

    public void setContenido_meme(String str) {
        this.contenido_meme = str;
    }

    public byte[] getImagen_meme() {
        return this.imagen_meme;
    }

    public void setImagen_meme(byte[] bArr) {
        this.imagen_meme = bArr;
    }

    public String getFecha_publicacion() {
        return this.Fecha_publicacion;
    }

    public void setFecha_publicacion(String str) {
        this.Fecha_publicacion = str;
    }
}
