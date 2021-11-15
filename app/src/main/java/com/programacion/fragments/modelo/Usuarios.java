package com.programacion.fragments.modelo;

public class Usuarios {

    private String Uid;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenia;
    private String imagen;
    private String telefono;

    public Usuarios(String uid, String nombre, String apellido, String email, String contrasenia) {
        Uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public Usuarios(String uid, String nombre, String apellido, String email, String contrasenia, String imagen, String telefono) {
        Uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.imagen = imagen;
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuarios(String uid, String nombre, String apellido, String email, String contrasenia, String imagen) {
        Uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.imagen = imagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Usuarios() {
    }

    public Usuarios(String uid) {
        Uid = uid;
    }

    public Usuarios(String nombre, String apellido, String email, String contrasenia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
