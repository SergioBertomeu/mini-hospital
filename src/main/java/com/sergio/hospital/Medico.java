package com.sergio.hospital;

public class Medico {

    private String nombre;
    private String especialidad;
    private String numeroColegiado;

    public Medico(String nombre, String especialidad, String numeroColegiado){

        this.nombre = nombre;
        this.especialidad = especialidad;
        this.numeroColegiado = numeroColegiado;
    }

    public String getNombre(){
        return nombre;
    }

    public String getEspecialidad(){
        return especialidad;
    }

    public String getNumeroColegiado(){
        return numeroColegiado;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", numeroColegiado='" + numeroColegiado + '\'' +
                '}';
    }
}
