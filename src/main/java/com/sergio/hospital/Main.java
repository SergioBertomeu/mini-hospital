package com.sergio.hospital;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static HospitalService hospitalService = new HospitalService();

    public static void main(String[] args) {

        DatabaseManager.crearTablas();

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEnteroValido("Elige opción: ");

            switch (opcion) {
                case 1:
                    anadirPaciente();
                    break;
                case 2:
                    mostrarPacientes();
                    break;
                case 3:
                    anadirMedico();
                    break;
                case 4:
                    mostrarMedicos();
                    break;
                case 5:
                    crearCita();
                    break;
                case 6:
                    mostrarCitas();
                    break;
                case 7:
                    buscarCitasPorPaciente();
                    break;
                case 8:
                    cancelarCitaPorId();
                    break;
                case 9:
                    reprogramarCitaPorId();
                    break;
                case 10:
                    actualizarPaciente();
                    break;
                case 11:
                    eliminarPaciente();
                    break;
                case 12:
                    actualizarMedico();
                    break;
                case 13:
                    eliminarMedico();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Elige una opción del menú.");
            }

        } while (opcion != 0);
    }

    public static void mostrarMenu() {
        System.out.println("\n==============================");
        System.out.println("        MINI HOSPITAL");
        System.out.println("==============================");
        System.out.println("1. Añadir paciente");
        System.out.println("2. Mostrar pacientes");
        System.out.println("3. Añadir medico");
        System.out.println("4. Mostrar medicos");
        System.out.println("5. Crear cita");
        System.out.println("6. Mostrar citas");
        System.out.println("7. Buscar citas por DNI de paciente");
        System.out.println("8. Cancelar cita por ID");
        System.out.println("9. Reprogramar cita por ID");
        System.out.println("10. Actualizar paciente");
        System.out.println("11. Eliminar paciente");
        System.out.println("12. Actualizar medico");
        System.out.println("13. Eliminar medico");
        System.out.println("0. Salir");
        System.out.println("==============================");
    }

    public static String leerTextoNoVacio(String mensaje) {
        String texto;

        do {
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();

            if (texto.isEmpty()) {
                System.out.println("Este campo no puede estar vacío.");
            }

        } while (texto.isEmpty());

        return texto;
    }

    public static int leerEnteroValido(String mensaje) {
        int numero = 0;
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);

            if (scanner.hasNextInt()) {
                numero = scanner.nextInt();
                scanner.nextLine();
                valido = true;
            } else {
                System.out.println("Introduce un número válido.");
                scanner.nextLine();
            }
        }

        return numero;
    }

    public static int leerEnteroPositivo(String mensaje) {
        int numero;
        boolean valido = false;

        do {
            numero = leerEnteroValido(mensaje);

            if (numero > 0) {
                valido = true;
            } else {
                System.out.println("El número debe ser mayor que 0.");
            }

        } while (!valido);

        return numero;
    }

    public static int leerEdadValida() {
        int edad;
        boolean edadValida = false;

        do {
            edad = leerEnteroValido("Edad: ");

            if (edad >= 0) {
                edadValida = true;
            } else {
                System.out.println("La edad no puede ser negativa.");
            }

        } while (!edadValida);

        return edad;
    }

    public static LocalDateTime leerFechaValida() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fecha = null;
        boolean fechaValida = false;

        while (!fechaValida) {
            System.out.print("Introduce fecha (dd/MM/yyyy HH:mm): ");
            String texto = scanner.nextLine().trim();

            try {
                fecha = LocalDateTime.parse(texto, formatter);
                fechaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato incorrecto. Ejemplo: 18/04/2026 10:30");
            }
        }

        return fecha;
    }

    public static void anadirPaciente() {
        String nombre = leerTextoNoVacio("Nombre del paciente: ");
        String dni = leerTextoNoVacio("DNI del paciente: ");
        int edad = leerEdadValida();

        Paciente paciente = new Paciente(nombre, dni, edad);

        ResultadoOperacion resultado = hospitalService.agregarPaciente(paciente);
        System.out.println(resultado.getMensaje());
    }

    public static void mostrarPacientes() {
        List<Paciente> pacientes = hospitalService.obtenerTodosLosPacientes();

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            System.out.println("\n--- LISTA DE PACIENTES ---");
            for (Paciente p : pacientes) {
                System.out.println(p);
            }
        }
    }

    public static void anadirMedico() {
        String nombre = leerTextoNoVacio("Nombre del medico: ");
        String especialidad = leerTextoNoVacio("Especialidad: ");
        String numeroColegiado = leerTextoNoVacio("Numero colegiado: ");

        Medico medico = new Medico(nombre, especialidad, numeroColegiado);

        ResultadoOperacion resultado = hospitalService.agregarMedico(medico);
        System.out.println(resultado.getMensaje());
    }

    public static void mostrarMedicos() {
        List<Medico> medicos = hospitalService.obtenerTodosLosMedicos();

        if (medicos.isEmpty()) {
            System.out.println("No hay medicos registrados.");
        } else {
            System.out.println("\n--- LISTA DE MEDICOS ---");
            for (Medico m : medicos) {
                System.out.println(m);
            }
        }
    }

    public static void crearCita() {
        String dniPaciente = leerTextoNoVacio("DNI del paciente: ");
        String numeroColegiado = leerTextoNoVacio("Numero colegiado del medico: ");
        LocalDateTime fecha = leerFechaValida();

        ResultadoOperacion resultado = hospitalService.crearCita(dniPaciente, numeroColegiado, fecha);
        System.out.println(resultado.getMensaje());
    }

    public static void mostrarCitas() {
        List<Cita> citas = hospitalService.obtenerTodasLasCitasOrdenadas();

        if (citas.isEmpty()) {
            System.out.println("No hay citas registradas.");
        } else {
            System.out.println("\n--- LISTA DE CITAS ---");
            for (Cita c : citas) {
                System.out.println(c);
            }
        }
    }

    public static void buscarCitasPorPaciente() {
        String dni = leerTextoNoVacio("Introduce el DNI exacto del paciente: ");

        List<Cita> citas = hospitalService.buscarCitasPorDniPaciente(dni);

        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas para ese paciente.");
        } else {
            System.out.println("\n--- CITAS DEL PACIENTE ---");
            for (Cita c : citas) {
                System.out.println(c);
            }
        }
    }

    public static void cancelarCitaPorId() {
        int id = leerEnteroPositivo("Introduce el ID de la cita a cancelar: ");

        ResultadoOperacion resultado = hospitalService.cancelarCitaPorId(id);
        System.out.println(resultado.getMensaje());
    }

    public static void reprogramarCitaPorId() {
        int id = leerEnteroPositivo("Introduce el ID de la cita a reprogramar: ");

        System.out.println("Nueva fecha para la cita:");
        LocalDateTime nuevaFecha = leerFechaValida();

        ResultadoOperacion resultado = hospitalService.reprogramarCitaPorId(id, nuevaFecha);
        System.out.println(resultado.getMensaje());
    }

    public static void actualizarPaciente() {
        String dni = leerTextoNoVacio("DNI del paciente a actualizar: ");
        String nuevoNombre = leerTextoNoVacio("Nuevo nombre: ");
        int nuevaEdad = leerEdadValida();

        ResultadoOperacion resultado = hospitalService.actualizarPaciente(dni, nuevoNombre, nuevaEdad);
        System.out.println(resultado.getMensaje());
    }

    public static void eliminarPaciente() {
        String dni = leerTextoNoVacio("DNI del paciente a eliminar: ");

        ResultadoOperacion resultado = hospitalService.eliminarPacientePorDni(dni);
        System.out.println(resultado.getMensaje());
    }

    public static void actualizarMedico() {
        String numeroColegiado = leerTextoNoVacio("Numero colegiado del medico a actualizar: ");
        String nuevoNombre = leerTextoNoVacio("Nuevo nombre: ");
        String nuevaEspecialidad = leerTextoNoVacio("Nueva especialidad: ");

        ResultadoOperacion resultado = hospitalService.actualizarMedico(
                numeroColegiado,
                nuevoNombre,
                nuevaEspecialidad
        );

        System.out.println(resultado.getMensaje());
    }

    public static void eliminarMedico() {
        String numeroColegiado = leerTextoNoVacio("Numero colegiado del medico a eliminar: ");

        ResultadoOperacion resultado = hospitalService.eliminarMedicoPorNumeroColegiado(numeroColegiado);
        System.out.println(resultado.getMensaje());
    }
}