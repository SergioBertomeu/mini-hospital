package com.sergio.hospital;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:hospital.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void crearTablas() {
        String sqlPacientes = """
                CREATE TABLE IF NOT EXISTS pacientes (
                    dni TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    edad INTEGER NOT NULL
                );
                """;

        String sqlMedicos = """
                CREATE TABLE IF NOT EXISTS medicos (
                    numero_colegiado TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    especialidad TEXT NOT NULL
                );
                """;

        String sqlCitas = """
                CREATE TABLE IF NOT EXISTS citas (
                    id INTEGER PRIMARY KEY,
                    dni_paciente TEXT NOT NULL,
                    numero_colegiado TEXT NOT NULL,
                    fecha TEXT NOT NULL,
                    FOREIGN KEY (dni_paciente) REFERENCES pacientes(dni),
                    FOREIGN KEY (numero_colegiado) REFERENCES medicos(numero_colegiado)
                );
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sqlPacientes);
            statement.execute(sqlMedicos);
            statement.execute(sqlCitas);

            System.out.println("Base de datos preparada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al preparar la base de datos:");
            System.out.println(e.getMessage());
        }
    }
}
