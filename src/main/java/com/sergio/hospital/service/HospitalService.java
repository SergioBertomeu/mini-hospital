package com.sergio.hospital.service;

import com.sergio.hospital.model.Cita;
import com.sergio.hospital.model.Medico;
import com.sergio.hospital.model.Paciente;
import com.sergio.hospital.repository.CitaRepository;
import com.sergio.hospital.repository.MedicoRepository;
import com.sergio.hospital.repository.PacienteRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HospitalService {

    private static final int DURACION_CITA_MINUTOS = 20;

    private PacienteRepository pacienteRepository = new PacienteRepository();
    private MedicoRepository medicoRepository = new MedicoRepository();
    private CitaRepository citaRepository = new CitaRepository();

    public ResultadoOperacion agregarPaciente(Paciente paciente) {
        try {
            Paciente existente = pacienteRepository.buscarPorDni(paciente.getDni());

            if (existente != null) {
                return new ResultadoOperacion(false, "Ya existe un paciente con ese DNI.");
            }

            pacienteRepository.guardar(paciente);

            return new ResultadoOperacion(true, "Paciente añadido correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al guardar paciente en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion agregarMedico(Medico medico) {
        try {
            Medico existente = medicoRepository.buscarPorNumeroColegiado(medico.getNumeroColegiado());

            if (existente != null) {
                return new ResultadoOperacion(false, "Ya existe un medico con ese numero colegiado.");
            }

            medicoRepository.guardar(medico);

            return new ResultadoOperacion(true, "Medico añadido correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al guardar medico en la base de datos: " + e.getMessage());
        }
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
        try {
            return medicoRepository.buscarTodos();
        } catch (SQLException e) {
            System.out.println("Error al leer medicos desde la base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Paciente buscarPacienteExactoPorDni(String dni) {
        try {
            return pacienteRepository.buscarPorDni(dni);
        } catch (SQLException e) {
            System.out.println("Error al buscar paciente en base de datos: " + e.getMessage());
            return null;
        }
    }

    public Medico buscarMedicoExactoPorColegiado(String numeroColegiado) {
        try {
            return medicoRepository.buscarPorNumeroColegiado(numeroColegiado);
        } catch (SQLException e) {
            System.out.println("Error al buscar medico en base de datos: " + e.getMessage());
            return null;
        }
    }

    public boolean fechaEsPasada(LocalDateTime fecha) {
        return fecha.isBefore(LocalDateTime.now());
    }

    public boolean medicoTieneSolapamiento(String numeroColegiado, LocalDateTime nuevaFechaInicio) {
        try {
            LocalDateTime nuevaFechaFin = nuevaFechaInicio.plusMinutes(DURACION_CITA_MINUTOS);

            List<Cita> citas = citaRepository.buscarTodas();

            for (Cita citaExistente : citas) {

                boolean mismoMedico = citaExistente.getMedico()
                        .getNumeroColegiado()
                        .equalsIgnoreCase(numeroColegiado);

                if (mismoMedico) {
                    LocalDateTime citaExistenteInicio = citaExistente.getFecha();
                    LocalDateTime citaExistenteFin = citaExistenteInicio.plusMinutes(DURACION_CITA_MINUTOS);

                    boolean haySolapamiento = nuevaFechaInicio.isBefore(citaExistenteFin)
                            && nuevaFechaFin.isAfter(citaExistenteInicio);

                    if (haySolapamiento) {
                        return true;
                    }
                }
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error al comprobar solapamiento: " + e.getMessage());
            return true;
        }
    }

    public boolean medicoTieneSolapamientoExcluyendoCita(
            String numeroColegiado,
            LocalDateTime nuevaFechaInicio,
            Cita citaAExcluir
    ) {
        try {
            LocalDateTime nuevaFechaFin = nuevaFechaInicio.plusMinutes(DURACION_CITA_MINUTOS);

            List<Cita> citas = citaRepository.buscarTodas();

            for (Cita citaExistente : citas) {

                if (citaExistente.getId() == citaAExcluir.getId()) {
                    continue;
                }

                boolean mismoMedico = citaExistente.getMedico()
                        .getNumeroColegiado()
                        .equalsIgnoreCase(numeroColegiado);

                if (mismoMedico) {
                    LocalDateTime citaExistenteInicio = citaExistente.getFecha();
                    LocalDateTime citaExistenteFin = citaExistenteInicio.plusMinutes(DURACION_CITA_MINUTOS);

                    boolean haySolapamiento = nuevaFechaInicio.isBefore(citaExistenteFin)
                            && nuevaFechaFin.isAfter(citaExistenteInicio);

                    if (haySolapamiento) {
                        return true;
                    }
                }
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error al comprobar solapamiento: " + e.getMessage());
            return true;
        }
    }

    public ResultadoOperacion crearCita(String dniPaciente, String numeroColegiado, LocalDateTime fecha) {
        try {
            Paciente paciente = buscarPacienteExactoPorDni(dniPaciente);
            Medico medico = buscarMedicoExactoPorColegiado(numeroColegiado);

            if (paciente == null) {
                return new ResultadoOperacion(false, "No existe un paciente con ese DNI.");
            }

            if (medico == null) {
                return new ResultadoOperacion(false, "No existe un medico con ese numero colegiado.");
            }

            if (fechaEsPasada(fecha)) {
                return new ResultadoOperacion(false, "No se puede crear una cita en el pasado.");
            }

            if (medicoTieneSolapamiento(numeroColegiado, fecha)) {
                return new ResultadoOperacion(false, "El medico ya tiene una cita en ese horario.");
            }

            int id = citaRepository.obtenerSiguienteId();

            Cita cita = new Cita(id, paciente, medico, fecha);
            citaRepository.guardar(cita);

            return new ResultadoOperacion(true, "Cita creada correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al guardar cita en la base de datos: " + e.getMessage());
        }
    }

    public Cita buscarCitaPorId(int id) {
        try {
            return citaRepository.buscarPorId(id);
        } catch (SQLException e) {
            System.out.println("Error al buscar cita en base de datos: " + e.getMessage());
            return null;
        }
    }

    public ResultadoOperacion cancelarCitaPorId(int id) {
        try {
            Cita cita = citaRepository.buscarPorId(id);

            if (cita == null) {
                return new ResultadoOperacion(false, "No existe ninguna cita con ese ID.");
            }

            citaRepository.eliminarPorId(id);

            return new ResultadoOperacion(true, "Cita cancelada correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al cancelar cita en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion reprogramarCitaPorId(int id, LocalDateTime nuevaFecha) {
        try {
            Cita cita = citaRepository.buscarPorId(id);

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

            citaRepository.actualizarFecha(id, nuevaFecha);

            return new ResultadoOperacion(true, "Cita reprogramada correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al reprogramar cita en la base de datos: " + e.getMessage());
        }
    }

    public List<Cita> obtenerTodasLasCitasOrdenadas() {
        try {
            return citaRepository.buscarTodas();
        } catch (SQLException e) {
            System.out.println("Error al leer citas desde la base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Cita> buscarCitasPorDniPaciente(String dni) {
        try {
            return citaRepository.buscarPorDniPaciente(dni);
        } catch (SQLException e) {
            System.out.println("Error al buscar citas en base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ResultadoOperacion actualizarPaciente(String dni, String nuevoNombre, int nuevaEdad) {
        try {
            Paciente paciente = pacienteRepository.buscarPorDni(dni);

            if (paciente == null) {
                return new ResultadoOperacion(false, "No existe un paciente con ese DNI.");
            }

            Paciente pacienteActualizado = new Paciente(nuevoNombre, dni, nuevaEdad);
            pacienteRepository.actualizar(pacienteActualizado);

            return new ResultadoOperacion(true, "Paciente actualizado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al actualizar paciente: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarPacientePorDni(String dni) {
        try {
            Paciente paciente = pacienteRepository.buscarPorDni(dni);

            if (paciente == null) {
                return new ResultadoOperacion(false, "No existe un paciente con ese DNI.");
            }

            List<Cita> citasPaciente = citaRepository.buscarPorDniPaciente(dni);

            if (!citasPaciente.isEmpty()) {
                return new ResultadoOperacion(false, "No se puede eliminar el paciente porque tiene citas registradas.");
            }

            pacienteRepository.eliminarPorDni(dni);

            return new ResultadoOperacion(true, "Paciente eliminado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al eliminar paciente: " + e.getMessage());
        }
    }


    public ResultadoOperacion actualizarMedico(String numeroColegiado, String nuevoNombre, String nuevaEspecialidad) {
        try {
            Medico medico = medicoRepository.buscarPorNumeroColegiado(numeroColegiado);

            if (medico == null) {
                return new ResultadoOperacion(false, "No existe un medico con ese numero colegiado.");
            }

            Medico medicoActualizado = new Medico(nuevoNombre, nuevaEspecialidad, numeroColegiado);
            medicoRepository.actualizar(medicoActualizado);

            return new ResultadoOperacion(true, "Medico actualizado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al actualizar medico: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarMedicoPorNumeroColegiado(String numeroColegiado) {
        try {
            Medico medico = medicoRepository.buscarPorNumeroColegiado(numeroColegiado);

            if (medico == null) {
                return new ResultadoOperacion(false, "No existe un medico con ese numero colegiado.");
            }

            List<Cita> citasMedico = citaRepository.buscarPorNumeroColegiado(numeroColegiado);

            if (!citasMedico.isEmpty()) {
                return new ResultadoOperacion(false, "No se puede eliminar el medico porque tiene citas registradas.");
            }

            medicoRepository.eliminarPorNumeroColegiado(numeroColegiado);

            return new ResultadoOperacion(true, "Medico eliminado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false, "Error al eliminar medico: " + e.getMessage());
        }
    }
}