# EHRS Backend

Spring Boot backend for the EHRS Angular frontend.

## What Is Included

- Spring Boot 2.7 application
- SQL Server JPA configuration
- REST controllers for people, visits, queue, consultations, orders, and records
- Backward-compatible legacy endpoints for the existing Angular frontend

## Prerequisites

- Java 15+
- Maven 3.8+
- SQL Server with the `EHRS` database created

This backend is configured to compile to Java class file version `59.0` via Java 15.

## Database Setup

Run these scripts first if you have not already:

- [001_create_ehrs.sql](../database/sqlserver/001_create_ehrs.sql)
- [002_seed_ehrs.sql](../database/sqlserver/002_seed_ehrs.sql)

## Environment Variables

Recommended environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `EHRS_ALLOWED_ORIGINS`

Example SQL Server URL:

```text
jdbc:sqlserver://localhost:1433;databaseName=EHRS;encrypt=false;trustServerCertificate=true
```

For local IntelliJ runs against the default local SQL Server instance on this machine, the backend also includes an `intellij` profile in [application-intellij.yml](./src/main/resources/application-intellij.yml) that uses Windows integrated security.

Important: the Microsoft JDBC driver requires SQL Server to accept TCP/IP connections. If your local SQL Server instance is not listening on TCP yet, enable TCP/IP in SQL Server Configuration Manager and restart the SQL Server service before running the backend.

## Run

```bash
mvn spring-boot:run
```

For the local Windows setup in this repo, use the launcher below when you want the backend logs to stay attached to the backend command window:

```bat
run-backend.cmd
```

If you want a dedicated backend cmd window to open automatically and stream the Spring Boot logs there, use:

```bat
open-backend-console.cmd
```

These launchers do not redirect output to a log file. Standard output and error stay visible in the console window, and the launcher disables the extra Spring Boot run fork so the app logs stay attached to that backend cmd session.

For IntelliJ, set the active profile to `intellij` or pass:

```text
--spring.profiles.active=intellij
```

The backend now defaults to port `8081` to avoid collisions with other local services already using `8080`.

## Main API Groups

- `/api/v1/people`
- `/api/v1/visits`
- `/api/v1/queue`
- `/api/v1/consultations`
- `/api/v1/orders`
- `/api/v1/medical-records`

## Legacy Compatibility Endpoints

These are included so the current Angular frontend can transition with minimal rewiring:

- `/person/patient`
- `/person/doctor`
- `/visit`
- `/queue`
- `/queue/triage`
- `/queue/doctor`
- `/queue/payment`
- `/queue/checkout`
- `/DiagnosisList`
- `/VisitationHistory`
- `/MedicalRecord`
- `/doctorID`
- `/nurseID`
- `/order`
- `/updateVisitSummary`
