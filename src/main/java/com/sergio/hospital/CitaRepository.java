package com.sergio.hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaRepository {

    private PacienteRepository pacienteRepository = new PacienteRepository();
    private MedicoRepository medicoRepository = new MedicoRepository();

    public void guardar(Cita cita) throws SQLException {
        String sql = """
                INSERT INTO citas (id, dni_paciente, numero_colegiado, fecha)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cita.getId());
            statement.setString(2, cita.getPaciente().getDni());
            statement.setString(3, cita.getMedico().getNumeroColegiado());
            statement.setString(4, cita.getFecha().toString());

            statement.executeUpdate();
        }
    }

    public Cita buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, dni_paciente, numero_colegiado, fecha
                FROM citas
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return convertirFilaEnCita(resultSet);
                }
            }
        }

        return null;
    }

    public List<Cita> buscarTodas() throws SQLException {
        List<Cita> citas = new ArrayList<>();

        String sql = """
                SELECT id, dni_paciente, numero_colegiado, fecha
                FROM citas
                ORDER BY fecha ASC
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Cita cita = convertirFilaEnCita(resultSet);
                citas.add(cita);
            }
        }

        return citas;
    }

    public List<Cita> buscarPorDniPaciente(String dniPaciente) throws SQLException {
        List<Cita> citas = new ArrayList<>();

        String sql = """
                SELECT id, dni_paciente, numero_colegiado, fecha
                FROM citas
                WHERE dni_paciente = ?
                ORDER BY fecha ASC
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dniPaciente);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Cita cita = convertirFilaEnCita(resultSet);
                    citas.add(cita);
                }
            }
        }

        return citas;
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = """
                DELETE FROM citas
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void actualizarFecha(int id, LocalDateTime nuevaFecha) throws SQLException {
        String sql = """
                UPDATE citas
                SET fecha = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nuevaFecha.toString());
            statement.setInt(2, id);

            statement.executeUpdate();
        }
    }

    public int obtenerSiguienteId() throws SQLException {
        String sql = """
                SELECT MAX(id) AS max_id
                FROM citas
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int maxId = resultSet.getInt("max_id");
                return maxId + 1;
            }
        }

        return 1;
    }

    private Cita convertirFilaEnCita(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String dniPaciente = resultSet.getString("dni_paciente");
        String numeroColegiado = resultSet.getString("numero_colegiado");
        String fechaTexto = resultSet.getString("fecha");

        Paciente paciente = pacienteRepository.buscarPorDni(dniPaciente);
        Medico medico = medicoRepository.buscarPorNumeroColegiado(numeroColegiado);
        LocalDateTime fecha = LocalDateTime.parse(fechaTexto);

        return new Cita(id, paciente, medico, fecha);
    }
}