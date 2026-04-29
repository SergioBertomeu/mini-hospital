package com.sergio.hospital.repository;

import com.sergio.hospital.database.DatabaseManager;
import com.sergio.hospital.model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {

    public void guardar(Paciente paciente) throws SQLException {
        String sql = """
                INSERT INTO pacientes (dni, nombre, edad)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, paciente.getDni());
            statement.setString(2, paciente.getNombre());
            statement.setInt(3, paciente.getEdad());

            statement.executeUpdate();
        }
    }

    public Paciente buscarPorDni(String dni) throws SQLException {
        String sql = """
                SELECT dni, nombre, edad
                FROM pacientes
                WHERE dni = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dni);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    String dniEncontrado = resultSet.getString("dni");
                    String nombre = resultSet.getString("nombre");
                    int edad = resultSet.getInt("edad");

                    return new Paciente(nombre, dniEncontrado, edad);
                }
            }
        }

        return null;
    }

    public List<Paciente> buscarTodos() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();

        String sql = """
                SELECT dni, nombre, edad
                FROM pacientes
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String dni = resultSet.getString("dni");
                String nombre = resultSet.getString("nombre");
                int edad = resultSet.getInt("edad");

                Paciente paciente = new Paciente(nombre, dni, edad);
                pacientes.add(paciente);
            }
        }

        return pacientes;
    }

    public void actualizar(Paciente paciente) throws SQLException {
        String sql = """
                UPDATE pacientes
                SET nombre = ?, edad = ?
                WHERE dni = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, paciente.getNombre());
            statement.setInt(2, paciente.getEdad());
            statement.setString(3, paciente.getDni());

            statement.executeUpdate();
        }
    }

    public void eliminarPorDni(String dni) throws SQLException {
        String sql = """
                DELETE FROM pacientes
                WHERE dni = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dni);
            statement.executeUpdate();
        }
    }
}