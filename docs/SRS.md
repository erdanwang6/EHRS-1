# Software Requirements Specification

## Project

Electronic Health Record System (EHRS)

## Document Purpose

This SRS describes the EHRS application as implemented in the current codebase. It is an as-built requirements document derived from the Angular front-end, routing configuration, view components, models, and HTTP service integrations present in the repository.

## Scope

EHRS is a web-based Electronic Health Record front-end intended to support basic outpatient clinical workflows. The implemented application provides:

- User login UI
- Role-oriented navigation for clinical staff
- Visitation queue viewing and filtering
- Creation of visitation records
- Viewing of medical and visitation history
- Doctor diagnosis entry and order submission
- Patient checkout and payment UI

The current codebase is a client-side Angular application and relies on HTTP APIs for live data. The target system defined by this SRS shall include a Spring Boot backend and a Microsoft SQL Server database to support the front-end workflows. Some features are fully wired to APIs, while others remain prototype or placeholder implementations.

## Product Overview

### Product Perspective

EHRS is implemented as a single-page Angular application using Angular Material for UI components. The root app renders a `router-outlet`, and the main authenticated shell is the Home page, which provides:

- A header area
- A left-side accordion menu
- A nested route outlet for feature screens

The target production architecture shall consist of:

- An Angular web client
- A Spring Boot REST API backend
- A Microsoft SQL Server relational database

### Intended Users

- Doctors
- Nurses
- Front desk / intake staff
- Checkout / billing staff

### Operating Environment

- Web browser supporting modern Angular applications
- Node.js-based development environment for build and serve workflows
- Angular front-end runtime
- Java runtime environment for the Spring Boot backend
- Microsoft SQL Server instance for persistent storage
- HTTP-accessible backend APIs

## Assumptions and Dependencies

- The application depends on Angular Material for user interface widgets.
- The visitation and people-related features use a base API URL configured in Angular environment files.
- Consultation-related features currently call a separate backend at `http://localhost:3000`.
- The system assumes backend APIs return data in shapes compatible with the TypeScript interfaces used by the client.
- The target backend platform shall be Spring Boot.
- The target persistent data store shall be Microsoft SQL Server.

## System Features

### 1. Login

The system shall provide a login form with username and password fields.

The system shall allow a user to submit the login form.

The current implementation only captures form values locally and does not perform authentication against a backend service.

### 2. Application Shell and Navigation

The system shall provide a main home layout containing:

- A header
- A persistent left navigation drawer
- A content area for routed feature views

The system shall provide menu navigation for:

- Visitation history viewing
- Medical record viewing
- Demographics
- Vitals
- Medications
- Labs
- Diagnosis
- Treatment
- Imaging
- Visitation queue
- Visitation record creation
- Patient checkout

### 3. Patient and Doctor Directory Access

The system shall retrieve patient lists from the people API.

The system shall retrieve doctor lists from the people API.

The patient list shall support use in visitation record selection workflows.

### 4. Visitation Queue

The system shall display a visitation queue in tabular form.

Each queue entry shall include:

- Patient name
- Visit date
- Assigned nurse
- Assigned doctor
- Current status

The system shall allow text filtering of the queue.

The system shall allow status-based queue filtering for:

- All
- Waiting for Triage
- Waiting for Doctor
- Waiting for Payment
- Ready for Checkout

The system shall allow users to select a queue row and navigate toward visitation record handling.

### 5. Visitation Record Creation

The system shall allow a user to create a visitation record.

The visitation record creation UI shall support:

- Patient lookup using autocomplete
- Visit date entry
- Visit reason entry
- Status selection

The system shall submit visitation data to the visit API.

The implemented payload currently includes:

- `patientid`
- `status`
- `visit_reason`

### 6. Medical Record Viewing

The system shall provide views for:

- Demographics
- Vitals
- Medications
- Labs
- Diagnosis
- Treatment
- Imaging

The system shall provide a medical record view used by doctor and nurse workflows.

The current implementation includes UI routing for these views, but some data population behavior is incomplete or placeholder-based.

### 7. Visitation History Viewing

The system shall provide a visitation history view for doctor and nurse workflows.

The system shall request visitation history data from the consultation backend.

The current implementation contains placeholder handling after the API call and does not yet fully define how the returned data is rendered.

### 8. Doctor Diagnosis Workflow

The system shall allow a doctor to view a diagnosis list.

The system shall allow a doctor to enter remarks for a patient visit.

The system shall allow a doctor to submit a visit summary and update the patient status for checkout progression.

The system shall allow a doctor to navigate to order-entry screens for:

- Prescription orders
- Radiology orders
- Lab test orders

### 9. Order Entry

The system shall provide separate order-entry screens for:

- Prescription orders
- Radiology orders
- Lab test orders

Each order-entry screen shall allow a clinician to enter an order name.

Each order-entry screen shall generate an order identifier and submit an order payload to the consultation backend.

The current implementation includes hard-coded clinician and patient identifiers in these flows.

### 10. Checkout

The system shall provide a checkout view for patient payment and treatment cost display.

The current implementation displays treatment rows, quantities, and total cost using generated placeholder data rather than backend-sourced billing data.

The system shall provide a visitation checkout list view for selecting patient records to check out.

### 11. Spring Boot Backend

The system shall provide a Spring Boot backend that exposes REST APIs required by the Angular front-end.

The backend shall consolidate the currently observed visit and consultation endpoints behind a consistent API surface.

The backend shall implement business logic for:

- Patient lookup
- Doctor lookup
- Visit creation
- Queue retrieval by status
- Medical record retrieval
- Visitation history retrieval
- Diagnosis list retrieval
- Order creation
- Visit summary updates
- Checkout-related data retrieval

The backend shall validate request payloads before persisting or updating data.

The backend shall return appropriate HTTP status codes for successful, invalid, unauthorized, and failed requests.

The backend shall provide centralized error handling and structured error responses for the front-end.

The backend should use a layered architecture including:

- Controller layer for HTTP endpoints
- Service layer for business rules
- Repository or data access layer for persistence

The backend should externalize configuration such as database connection strings, ports, and environment-specific settings.

The backend should support Cross-Origin Resource Sharing (CORS) configuration for the Angular client.

### 12. SQL Server Database

The system shall use Microsoft SQL Server as the system of record for operational data.

The database shall persist data required for:

- Patients
- Doctors
- Nurses
- Visits
- Visit status transitions
- Diagnoses
- Orders
- Medical record sections
- Checkout or billing records

The database design shall enforce primary keys for core entities and foreign-key relationships between related records.

The database should normalize reference and transactional data sufficiently to avoid inconsistent clinical and operational records.

The database shall support querying by patient identifier, visit identifier, clinician identifier, and visit status.

The database shall preserve audit-relevant timestamps such as creation and update times for key clinical transactions.

The database should support database migration or schema versioning through backend-managed scripts or migration tooling.

## External Interface Requirements

### User Interface

The UI shall use Angular Material components including:

- Cards
- Toolbar
- Drawer
- Accordion / expansion panels
- Tables
- Sort headers
- Form fields
- Inputs
- Date picker
- Select controls
- Buttons
- Snack bars
- Lists

### Software Interfaces

The current codebase integrates with two HTTP API surfaces. The target architecture should replace these with a Spring Boot backend that standardizes and owns these interfaces.

#### Visit API

Configured via Angular environment files.

Observed endpoints include:

- `/person/patient`
- `/person/doctor`
- `/visit`
- `/queue`
- `/queue/triage`
- `/queue/doctor`
- `/queue/payment`
- `/queue/checkout`

#### Consultation API

Currently hard-coded to `http://localhost:3000`.

Observed endpoints include:

- `/DiagnosisList`
- `/VisitationHistory`
- `/MedicalRecord`
- `/doctorID`
- `/nurseID`
- `/order`
- `/updateVisitSummary`

#### Spring Boot API Requirements

The backend API should expose versioned REST endpoints, for example under `/api/v1`.

The backend API should provide JSON request and response bodies for front-end consumption.

The backend API should provide endpoint groups covering:

- People and directory services
- Visitation and queue management
- Medical record retrieval
- Diagnosis and consultation workflows
- Order management
- Checkout and billing workflows

The backend API should support environment-based base URL configuration from the Angular client.

## Data Requirements

### Patient

Observed patient structure:

- `patientid: string`
- `namebirth: string`

### Visit

Observed visit structure:

- `patientid: string`
- `visit_date?: string`
- `status: string`
- `nurse_id?: string`
- `doctor_id?: string`
- `visit_reason: string`
- `symptoms?: string`
- `visit_summary?: string`
- `created_by?: string`

### Queue Entry

Observed queue entry structure:

- `visit_id: number`
- `patientid: string`
- `patient_name: string`
- `visit_date: string`
- `nurse: string`
- `doctor: string`
- `status: string`

### Database Entities

The backend and SQL Server implementation should define persisted entities equivalent to the front-end data needs, including:

- Patient
- Doctor
- Nurse
- Visit
- Queue entry or visit status projection
- Diagnosis
- Order
- Medical record
- Checkout or billing item

Each persisted entity should define a stable identifier and required relationships to support visit-centric workflows.

## Non-Functional Requirements

### Usability

The system should provide a simple, menu-driven workflow for clinical users.

The system should use recognizable Material Design components for consistency.

### Maintainability

The application should separate UI components from HTTP service calls.

The application should centralize API configuration where practical.

The backend should separate controllers, business services, and persistence concerns.

The database schema should be managed through repeatable migrations.

### Reliability

The system should handle API failures gracefully and present user-friendly feedback.

The current implementation has limited error handling and should be strengthened.

The backend should use transactional boundaries where partial writes would create inconsistent visit or order data.

The database should support backup and recovery procedures appropriate for healthcare operational data.

### Performance

The system should load queue and directory data on demand through HTTP requests.

The front-end should render tabular and form-driven screens with acceptable responsiveness in modern browsers.

The backend should provide queue and record lookups with response times appropriate for interactive clinical workflows.

The database should support indexed access patterns for patient lookup, visit lookup, and queue-by-status queries.

### Security

The backend shall protect clinical APIs from unauthorized access.

The backend should support authenticated user sessions or token-based authentication.

The backend should enforce role-based authorization for doctor, nurse, intake, and checkout capabilities.

The backend should avoid storing secrets directly in source-controlled configuration files.

The database connection should use secured credentials managed through environment-specific configuration.

## Constraints

- The current application is front-end only and depends on external services for persistent data.
- Some workflows are partially implemented with hard-coded IDs or placeholder data.
- Authentication and authorization are not implemented in the current codebase.
- Some route flows pass context through navigation, but downstream components do not consistently consume that context.
- The current repository does not yet contain the Spring Boot backend or SQL Server schema implementation described by this target architecture.

## Known Gaps in the Current Implementation

- Login is not connected to authentication logic.
- Consultation features use hard-coded localhost URLs rather than shared environment configuration.
- Several workflows use hard-coded doctor or patient identifiers.
- Some views contain TODO markers and placeholder logic.
- Checkout data is generated locally rather than retrieved from backend billing data.
- Automated tests are present but mostly validate component/service creation rather than end-to-end behavior.

## Acceptance Basis

Based on the current codebase, the application can be considered minimally acceptable when:

- The Angular application builds successfully
- The home shell and routed screens render
- Queue data can be retrieved from the visit API
- A visitation record can be submitted
- Diagnosis and order forms can be opened and submitted to their configured endpoints
- Checkout and record-viewing pages render without client-side crashes

## Future Requirements Candidates

- Real authentication and role-based authorization
- Unified backend configuration through environment files
- Spring Boot backend implementation
- SQL Server schema and migrations
- Dynamic patient and clinician identity propagation across workflows
- Complete medical record rendering for all record sections
- Billing integration for real checkout totals
- Stronger validation, error handling, and automated tests
