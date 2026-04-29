# Mini Hospital - Java + SQLite

Mini Hospital is a console-based Java project created to practice backend fundamentals, object-oriented programming, layered architecture, JDBC and SQLite persistence.

The application allows managing patients, doctors and medical appointments from a console menu.

---

## Technologies used

- Java 17 / 21
- Maven
- SQLite
- JDBC
- Git and GitHub
- IntelliJ IDEA

---

## Features

### Patients

- Add patients
- List patients
- Update patients
- Delete patients
- Prevent duplicated DNI
- Prevent deleting patients with registered appointments

### Doctors

- Add doctors
- List doctors
- Update doctors
- Delete doctors
- Prevent duplicated medical license number
- Prevent deleting doctors with registered appointments

### Appointments

- Create appointments
- List appointments ordered by date
- Search appointments by patient DNI
- Cancel appointments by ID
- Reschedule appointments by ID
- Prevent appointments in the past
- Prevent appointment overlaps for the same doctor
- Store appointments in SQLite

---

## Project structure

```text
src/main/java/com/sergio/hospital
│
├── app
│   └── Main.java
│
├── model
│   ├── Paciente.java
│   ├── Medico.java
│   └── Cita.java
│
├── service
│   ├── HospitalService.java
│   └── ResultadoOperacion.java
│
├── repository
│   ├── PacienteRepository.java
│   ├── MedicoRepository.java
│   └── CitaRepository.java
│
└── database
    └── DatabaseManager.java