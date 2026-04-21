package com.sergio.hospital;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class HospitalService {

    private static final int duracionCitaMinutos = 20;

    private Map<String, Paciente> pacientes = new HashMap<>();
    private Map<String, Medico> medicos = new HashMap<>();
    private List<Cita> citas = new ArrayList<>();

    private int siguienteIdCita = 1;

    private PacienteRepository pacienteRepository = new PacienteRepository();

    public ResultadoOperacion agregarPaciente(Paciente paciente) {
        try {
            Paciente existente = pacienteRepository.buscarPorDni(paciente.getDni());

            if (existente != null){
                return new ResultadoOperacion(false, "Ya existe un paciente con ese DNI.");
            }

            pacienteRepository.guardar(paciente);
            pacientes.put(paciente.getDni().toLowerCase(), paciente);

            return new ResultadoOperacion(true, "Paciente añadido correctamente.");

        }catch (SQLException e){
            return new ResultadoOperacion(false, "Error al guardar paciente en la base de datos: " + e.getMessage());
        }
    }



    public ResultadoOperacion agregarMedico(Medico medico) {
        String numeroColegiado = medico.getNumeroColegiado().toLowerCase();

        if (medicos.containsKey(numeroColegiado)) {
            return new ResultadoOperacion(false, "Ya existe un medico con ese numero colegiado.");
        }

        medicos.put(numeroColegiado, medico);
        return new  ResultadoOperacion(true, "Medico añadido correctamente.");
    }


    public List<Paciente> obtenerTodosLosPacientes() {
        try {
            return pacienteRepository.buscarTodos();
        } catch (SQLException e) {
            System.out.println("Error al leer pacientes desde la base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Medico> obtenerTodosLosMedicos() {
        return new ArrayList<>(medicos.values());
    }


    public Paciente buscarPacienteExactoPorDni(String dni) {
        return pacientes.get(dni.toLowerCase());
    }


    public Medico buscarMedicoExactoPorColegiado(String numeroColegiado) {
        return medicos.get(numeroColegiado.toLowerCase());
    }


    public boolean fechaEsPasada(LocalDateTime fecha){
        return fecha.isBefore(LocalDateTime.now());
    }

    public boolean medicoTieneSolapamiento(String numeroColegiado, LocalDateTime nuevaFechaInicio) {

        LocalDateTime nuevaFechaFin = nuevaFechaInicio.plusMinutes(duracionCitaMinutos);

        for (Cita citaExistente : citas) {

            boolean mismoMedico = citaExistente.getMedico()
                    .getNumeroColegiado()
                    .equalsIgnoreCase(numeroColegiado);

            if (mismoMedico) {

                LocalDateTime citaExistenteInicio = citaExistente.getFecha();
                LocalDateTime citaExistenteFin = citaExistenteInicio.plusMinutes(duracionCitaMinutos);

                boolean haySolapamiento = nuevaFechaInicio.isBefore(citaExistenteFin)
                        && nuevaFechaFin.isAfter(citaExistenteInicio);

                if (haySolapamiento) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean existeCitaDuplicada(String dniPaciente, String numeroColegiado, LocalDateTime fecha) {
        for (Cita c : citas) {
            if (c.getPaciente().getDni().equalsIgnoreCase(dniPaciente)
                    && c.getMedico().getNumeroColegiado().equalsIgnoreCase(numeroColegiado)
                    && c.getFecha().equals(fecha)) {
                return true;
            }
        }
        return false;
    }




    public ResultadoOperacion crearCita(String dniPaciente, String numeroColegiado, LocalDateTime fecha) {
        Paciente paciente = buscarPacienteExactoPorDni(dniPaciente);
        Medico medico = buscarMedicoExactoPorColegiado(numeroColegiado);

        if (paciente == null) {
            return new ResultadoOperacion(false, "No existe un paciente con ese DNI.");
        }

        if (medico == null) {
            return new ResultadoOperacion(false, "No existe un medico con ese numero de colegiado.");
        }

        if (fechaEsPasada(fecha)){
            return new ResultadoOperacion(false, "No se puede crear una cita en el pasado.");
        }

        if (medicoTieneSolapamiento(numeroColegiado, fecha)){
            return new ResultadoOperacion(false, "El medico ya tiene una cita en ese horario.");
        }

        Cita cita = new Cita(siguienteIdCita, paciente, medico, fecha);
        citas.add(cita);
        siguienteIdCita++;

        return new ResultadoOperacion(true, "cita creada correctamente");
    }

    public Cita buscarCitaPorId(int id){
        for (Cita c : citas){
            if (c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public ResultadoOperacion cancelarCitaPorId(int id){
        Cita cita = buscarCitaPorId(id);

        if (cita == null){
            return new ResultadoOperacion(false, "No existe ninguna cita con ese ID.");
        }

        citas.remove(cita);
        return new ResultadoOperacion(true, "Cita cancelada correctamente");
    }

    public ResultadoOperacion reprogramarCitaPorId(int id, LocalDateTime nuevaFecha) {
        Cita cita = buscarCitaPorId(id);

        if (cita == null) {
            return new ResultadoOperacion(false, "No existe ninguna cita con ese ID.");
        }

        if (fechaEsPasada(nuevaFecha)) {
            return new ResultadoOperacion(false, "No se puede reprogramar una cita a una fecha pasada.");
        }

        String numeroColegiado = cita.getMedico().getNumeroColegiado();

        boolean haySolapamiento = medicoTieneSolapamientoExcluyendoCita(
                numeroColegiado,
                nuevaFecha,
                cita
        );

        if (haySolapamiento) {
            return new ResultadoOperacion(false, "El medico ya tiene otra cita en ese horario.");
        }

        cita.setFecha(nuevaFecha);
        return new ResultadoOperacion(true, "Cita reprogramada correctamente.");
    }

    public List<Cita> obtenerTodasLasCitasOrdenadas() {

        List<Cita> citasordenadas =new ArrayList<>(citas);
        citasordenadas.sort(Comparator.comparing(Cita::getFecha));
        return citasordenadas;
    }

    public List<Cita> buscarCitasPorDniPaciente(String dni) {
        List<Cita> resultados = new ArrayList<>();

        for (Cita c : citas) {
            if (c.getPaciente().getDni().equalsIgnoreCase(dni)) {
                resultados.add(c);
            }
        }

         resultados.sort(Comparator.comparing(Cita::getFecha));
        return resultados;
    }

    public Cita buscarCitaExacta(String dniPaciente, String numeroColegiado, LocalDateTime fecha){
        for (Cita c : citas){
            boolean mismoPaciente = c.getPaciente().getDni().equalsIgnoreCase(dniPaciente);
            boolean mismoMedico = c.getMedico().getNumeroColegiado().equalsIgnoreCase(numeroColegiado);
            boolean mismaFecha = c.getFecha().equals(fecha);

            if (mismoPaciente && mismoMedico && mismaFecha){
                return c;
            }
        }
        return null;
    }


    public boolean cancelarCita(String dniPaciente, String numeroColegiado, LocalDateTime fecha){
        Cita cita = buscarCitaExacta(dniPaciente, numeroColegiado, fecha);
        if (cita == null){
            return false;
        }

        citas.remove(cita);
        return true;
    }

    public boolean medicoTieneSolapamientoExcluyendoCita(String numeroColegiado, LocalDateTime nuevaFechaInicio, Cita citaAExcluir){
        LocalDateTime nuevaFechaFin = nuevaFechaInicio.plusMinutes(duracionCitaMinutos);

        for (Cita citaExistente : citas){
            if (citaExistente == citaAExcluir){
                continue;
            }
            boolean mismoMedico = citaExistente.getMedico().getNumeroColegiado().equalsIgnoreCase(numeroColegiado);

            if (mismoMedico){
                LocalDateTime citaExistenteInicio = citaExistente.getFecha();
                LocalDateTime citaExistenteFin = citaExistenteInicio.plusMinutes(duracionCitaMinutos);

                boolean haySolapamiento = nuevaFechaInicio.isBefore(citaExistenteFin) && nuevaFechaFin.isAfter(citaExistenteInicio);

                if (haySolapamiento){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean reprogramarCita(String dniPaciente, String numeroColegiado, LocalDateTime fechaActual, LocalDateTime nuevaFecha){
        Cita cita = buscarCitaExacta(dniPaciente, numeroColegiado, fechaActual);

        if (cita == null){
            return false;
        }

        if (fechaEsPasada(nuevaFecha)){
            return false;
        }

        boolean haySolapamiento = medicoTieneSolapamientoExcluyendoCita(numeroColegiado, nuevaFecha, cita);

        if (haySolapamiento){
            return false;
        }

        cita.setFecha(nuevaFecha);
        return true;
    }
}