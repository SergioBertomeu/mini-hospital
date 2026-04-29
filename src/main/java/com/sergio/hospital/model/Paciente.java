package com.sergio.hospital.model;

public class Paciente {

    private String nombre;
    private  String dni;
    private  int edad;

    public Paciente(String nombre, String dni, int edad){
        this.nombre = nombre;
        this.dni = dni;
        this.edad = edad;
    }
    public String getNombre() {
        return nombre;
    }

    public String getDni(){
        return dni;
    }

    public int getEdad(){
        return edad;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setEdad(int edad){
        this.edad=edad;
    }

    @Override
    public String toString(){
        return "Nombre: " + nombre +
                " | DNI: " + dni +
                " | Edad: " + edad;
    }

}
