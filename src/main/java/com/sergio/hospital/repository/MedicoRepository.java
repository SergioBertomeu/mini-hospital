package com.sergio.hospital.repository;

import com.sergio.hospital.database.DatabaseManager;
import com.sergio.hospital.model.Medico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicoRepository {

    public void guardar(Medico medico) throws SQLException {
        String sql = """
                INSERT INTO medicos (numero_colegiado, nombre, especialidad)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, medico.getNumeroColegiado());
            statement.setString(2, medico.getNombre());
            statement.setString(3, medico.getEspecialidad());

            statement.executeUpdate();
        }
    }

    public Medico buscarPorNumeroColegiado(String numeroColegiado) throws SQLException {
        String sql = """
                SELECT numero_colegiado, nombre, especialidad
                FROM medicos
                WHERE numero_colegiado = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numeroColegiado);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    String numeroEncontrado = resultSet.getString("numero_colegiado");
                    String nombre = resultSet.getString("nombre");
                    String especialidad = resultSet.getString("especialidad");

                    return new Medico(nombre, especialidad, numeroEncontrado);
                }
            }
        }

        return null;
    }

    public List<Medico> buscarTodos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();

        String sql = """
                SELECT numero_colegiado, nombre, especialidad
                FROM medicos
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String numeroColegiado = resultSet.getString("numero_colegiado");
                String nombre = resultSet.getString("nombre");
                String especialidad = resultSet.getString("especialidad");

                Medico medico = new Medico(nombre, especialidad, numeroColegiado);
                medicos.add(medico);
            }
        }

        return medicos;
    }

    public void actualizar(Medico medico) throws SQLException {
        String sql = """
                UPDATE medicos
                SET nombre = ?, especialidad = ?
                WHERE numero_colegiado = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, medico.getNombre());
            statement.setString(2, medico.getEspecialidad());
            statement.setString(3, medico.getNumeroColegiado());

            statement.executeUpdate();
        }
    }

    public void eliminarPorNumeroColegiado(String numeroColegiado) throws SQLException {
        String sql = """
                DELETE FROM medicos
                WHERE numero_colegiado = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numeroColegiado);
            statement.executeUpdate();
        }
    }
}