package com.programacion.fragments.modelo;

public class Veterinarios {

    private String idUI;
    private String claveVeterinario;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private String direccion;
    private String telefono;
    private String email;

    public Veterinarios() {
    }

    public Veterinarios(String claveVeterinario, String nombre, String apellidoP, String apellidoM, String direccion, String telefono, String email) {
        this.claveVeterinario = claveVeterinario;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    public String getIdUI() {
        return idUI;
    }

    public void setIdUI(String idUI) {
        this.idUI = idUI;
    }

    public String getClaveVeterinario() {
        return claveVeterinario;
    }

    public void setClaveVeterinario(String claveVeterinario) {
        this.claveVeterinario = claveVeterinario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoP() {
        return apellidoP;
    }

    public void setApellidoP(String apellidoP) {
        this.apellidoP = apellidoP;
    }

    public String getApellidoM() {
        return apellidoM;
    }

    public void setApellidoM(String apellidoM) {
        this.apellidoM = apellidoM;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
