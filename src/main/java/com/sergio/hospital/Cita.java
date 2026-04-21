package com.sergio.hospital;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cita {

    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime fecha;

   public Cita(int id, Paciente paciente, Medico medico, LocalDateTime fecha){
       this.id = id;
       this.paciente = paciente;
       this.medico = medico;
       this.fecha = fecha;
   }

   public int getId(){
       return id;
   }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha){
       this.fecha = fecha;
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return "Cita ID: " + id +
                " | Paciente: " + paciente.getNombre() +
                " | DNI: " + paciente.getDni() +
                " | Medico: " + medico.getNombre() +
                " | Especialidad: " + medico.getEspecialidad() +
                " | Fecha: " + fecha.format(formatter);
    }
}
