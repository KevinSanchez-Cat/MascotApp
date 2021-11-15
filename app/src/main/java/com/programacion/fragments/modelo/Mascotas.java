package com.programacion.fragments.modelo;

public class Mascotas {

    private String idUI;
    private String claveMascota;
    private String nombre;
    private String nombrePropietario;
    private String direccion;
    private String telefono;
    private String email;
    private String especie;
    private String fechaNacimiento;
    private String sexo;
    private String raza;
    private String color;
    private String particularidades;
    private String veterinario;
    private String imagen;
    private String estatus;

    public Mascotas() {
    }

    public Mascotas(String claveMascota, String nombre, String nombrePropietario, String direccion, String telefono, String email, String especie, String fechaNacimiento, String sexo, String raza, String color, String particularidades, String veterinario) {
        this.claveMascota = claveMascota;
        this.nombre = nombre;
        this.nombrePropietario = nombrePropietario;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.especie = especie;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.raza = raza;
        this.color = color;
        this.particularidades = particularidades;
        this.veterinario = veterinario;
    }

    public String getIdUI() {
        return idUI;
    }

    public void setIdUI(String idUI) {
        this.idUI = idUI;
    }

    public String getClaveMascota() {
        return claveMascota;
    }

    public void setClaveMascota(String claveMascota) {
        this.claveMascota = claveMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getParticularidades() {
        return particularidades;
    }

    public void setParticularidades(String particularidades) {
        this.particularidades = particularidades;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }
}
