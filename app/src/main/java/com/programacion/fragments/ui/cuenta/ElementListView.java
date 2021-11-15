package com.programacion.fragments.ui.cuenta;

public class ElementListView {


    private String nombre, informacion;
    private boolean editar;
    private int img;

    public ElementListView(String nombre, String informacion, boolean editar, int img) {
        this.nombre = nombre;
        this.informacion = informacion;
        this.editar = editar;
        this.img = img;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
