USE [EHRS];
GO

SET NOCOUNT ON;
GO

DECLARE @RoleDoctorId UNIQUEIDENTIFIER = '10000000-0000-0000-0000-000000000001';
DECLARE @RoleNurseId UNIQUEIDENTIFIER = '10000000-0000-0000-0000-000000000002';
DECLARE @RoleIntakeId UNIQUEIDENTIFIER = '10000000-0000-0000-0000-000000000003';
DECLARE @RoleCheckoutId UNIQUEIDENTIFIER = '10000000-0000-0000-0000-000000000004';
DECLARE @RoleAdminId UNIQUEIDENTIFIER = '10000000-0000-0000-0000-000000000005';

DECLARE @AdminUserId UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000001';
DECLARE @DoctorUserId UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000002';
DECLARE @NurseUserId UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000003';
DECLARE @IntakeUserId UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000004';
DECLARE @CheckoutUserId UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000005';

DECLARE @DoctorId UNIQUEIDENTIFIER = '30000000-0000-0000-0000-000000000001';
DECLARE @NurseId UNIQUEIDENTIFIER = '30000000-0000-0000-0000-000000000002';

DECLARE @Patient1Id UNIQUEIDENTIFIER = '40000000-0000-0000-0000-000000000001';
DECLARE @Patient2Id UNIQUEIDENTIFIER = '40000000-0000-0000-0000-000000000002';
DECLARE @Patient3Id UNIQUEIDENTIFIER = '40000000-0000-0000-0000-000000000003';
DECLARE @Patient4Id UNIQUEIDENTIFIER = '40000000-0000-0000-0000-000000000004';

DECLARE @AdminPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$/5A3Na2aON7vTrNpd9YaAw==$etmKc8aDPJCKgCsGhxBsfG+JPDg8RdMLEwi5GQLviDc=';
DECLARE @DoctorPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$Xx5Vg2fGZTVRxcldMRqlHg==$17F9ktXZmD+c5LQIOcZcmcSxakEeEi8DIqIm+EZWwmU=';
DECLARE @NursePasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$u4SdOLYol3hTftuFm/F0Sg==$xvVCh87tO2WovTJepYN+BMkH0mHxJPkCUmSDBBJt/9E=';
DECLARE @IntakePasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$UEY+p95ZwbTWwnnJwaowzA==$QeJE32GiWG9SR7Q6UAqrEsbHR4ZxM3NIs8FBMOVik5M=';
DECLARE @CheckoutPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$fi3IUEs2duNqHB8znzc4NA==$Qmxjc2a3dE0OR3Sj8Nb36zpQ5pPTkgxBdjb8fKTTsvQ=';

IF NOT EXISTS (SELECT 1 FROM dbo.ROLE WHERE role_id = @RoleDoctorId)
BEGIN
    INSERT INTO dbo.ROLE (role_id, role_code, role_name, description)
    VALUES
        (@RoleDoctorId, N'DOCTOR', N'Doctor', N'Clinical provider role'),
        (@RoleNurseId, N'NURSE', N'Nurse', N'Nursing staff role'),
        (@RoleIntakeId, N'INTAKE', N'Intake Staff', N'Front desk and intake role'),
        (@RoleCheckoutId, N'CHECKOUT', N'Checkout Staff', N'Billing and checkout role'),
        (@RoleAdminId, N'ADMIN', N'Administrator', N'System administration role');
END
GO

DECLARE @AdminPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$/5A3Na2aON7vTrNpd9YaAw==$etmKc8aDPJCKgCsGhxBsfG+JPDg8RdMLEwi5GQLviDc=';
DECLARE @DoctorPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$Xx5Vg2fGZTVRxcldMRqlHg==$17F9ktXZmD+c5LQIOcZcmcSxakEeEi8DIqIm+EZWwmU=';
DECLARE @NursePasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$u4SdOLYol3hTftuFm/F0Sg==$xvVCh87tO2WovTJepYN+BMkH0mHxJPkCUmSDBBJt/9E=';
DECLARE @IntakePasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$UEY+p95ZwbTWwnnJwaowzA==$QeJE32GiWG9SR7Q6UAqrEsbHR4ZxM3NIs8FBMOVik5M=';
DECLARE @CheckoutPasswordHash NVARCHAR(255) = N'pbkdf2_sha256$210000$fi3IUEs2duNqHB8znzc4NA==$Qmxjc2a3dE0OR3Sj8Nb36zpQ5pPTkgxBdjb8fKTTsvQ=';

IF NOT EXISTS (SELECT 1 FROM dbo.APP_USER WHERE user_id = '20000000-0000-0000-0000-000000000001')
BEGIN
    INSERT INTO dbo.APP_USER
    (
        user_id,
        username,
        password_hash,
        first_name,
        last_name,
        email,
        status,
        last_login_at
    )
    VALUES
        ('20000000-0000-0000-0000-000000000001', N'admin', @AdminPasswordHash, N'System', N'Admin', N'admin@ehrs.local', N'ACTIVE', NULL),
        ('20000000-0000-0000-0000-000000000002', N'dr.chen', @DoctorPasswordHash, N'Olivia', N'Chen', N'olivia.chen@ehrs.local', N'ACTIVE', NULL),
        ('20000000-0000-0000-0000-000000000003', N'nurse.walker', @NursePasswordHash, N'Jasmine', N'Walker', N'jasmine.walker@ehrs.local', N'ACTIVE', NULL),
        ('20000000-0000-0000-0000-000000000004', N'intake.ross', @IntakePasswordHash, N'Daniel', N'Ross', N'daniel.ross@ehrs.local', N'ACTIVE', NULL),
        ('20000000-0000-0000-0000-000000000005', N'checkout.kim', @CheckoutPasswordHash, N'Hannah', N'Kim', N'hannah.kim@ehrs.local', N'ACTIVE', NULL);
END

UPDATE dbo.APP_USER
SET password_hash = CASE username
    WHEN N'admin' THEN @AdminPasswordHash
    WHEN N'dr.chen' THEN @DoctorPasswordHash
    WHEN N'nurse.walker' THEN @NursePasswordHash
    WHEN N'intake.ross' THEN @IntakePasswordHash
    WHEN N'checkout.kim' THEN @CheckoutPasswordHash
    ELSE password_hash
END
WHERE username IN (N'admin', N'dr.chen', N'nurse.walker', N'intake.ross', N'checkout.kim');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.USER_ROLE WHERE user_id = '20000000-0000-0000-0000-000000000001' AND role_id = '10000000-0000-0000-0000-000000000005')
BEGIN
    INSERT INTO dbo.USER_ROLE (user_role_id, user_id, role_id)
    VALUES
        ('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000005'),
        ('50000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001'),
        ('50000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002'),
        ('50000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003'),
        ('50000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.DOCTOR WHERE doctor_id = '30000000-0000-0000-0000-000000000001')
BEGIN
    INSERT INTO dbo.DOCTOR
    (
        doctor_id,
        user_id,
        employee_number,
        license_number,
        specialty,
        department,
        status
    )
    VALUES
        ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', N'DOC-1001', N'LIC-DOC-1001', N'Family Medicine', N'Outpatient Care', N'ACTIVE');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.NURSE WHERE nurse_id = '30000000-0000-0000-0000-000000000002')
BEGIN
    INSERT INTO dbo.NURSE
    (
        nurse_id,
        user_id,
        employee_number,
        license_number,
        department,
        status
    )
    VALUES
        ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003', N'NUR-2001', N'LIC-NUR-2001', N'Triage', N'ACTIVE');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.PATIENT WHERE patient_id = '40000000-0000-0000-0000-000000000001')
BEGIN
    INSERT INTO dbo.PATIENT
    (
        patient_id,
        medical_record_number,
        first_name,
        middle_name,
        last_name,
        date_of_birth,
        sex,
        phone_number,
        email,
        address_line_1,
        address_line_2,
        city,
        state,
        postal_code,
        emergency_contact_name,
        emergency_contact_phone,
        status
    )
    VALUES
        ('40000000-0000-0000-0000-000000000001', N'MRN-100001', N'Thomas', NULL, N'Gomez', '1989-04-16', N'Male', N'555-0101', N'thomas.gomez@example.com', N'14 Cedar St', NULL, N'Boston', N'MA', N'02118', N'Elena Gomez', N'555-1101', N'ACTIVE'),
        ('40000000-0000-0000-0000-000000000002', N'MRN-100002', N'Maria', N'R', N'Lopez', '1978-11-02', N'Female', N'555-0102', N'maria.lopez@example.com', N'88 Harbor Ave', N'Apt 3B', N'Cambridge', N'MA', N'02139', N'Carlos Lopez', N'555-1102', N'ACTIVE'),
        ('40000000-0000-0000-0000-000000000003', N'MRN-100003', N'James', NULL, N'Patel', '1995-07-22', N'Male', N'555-0103', N'james.patel@example.com', N'250 Maple Rd', NULL, N'Somerville', N'MA', N'02144', N'Anita Patel', N'555-1103', N'ACTIVE'),
        ('40000000-0000-0000-0000-000000000004', N'MRN-100004', N'Sophia', NULL, N'Nguyen', '2001-01-30', N'Female', N'555-0104', N'sophia.nguyen@example.com', N'501 Pine Dr', NULL, N'Quincy', N'MA', N'02169', N'Minh Nguyen', N'555-1104', N'ACTIVE');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.DIAGNOSIS_REFERENCE WHERE diagnosis_code = N'J02.9')
BEGIN
    INSERT INTO dbo.DIAGNOSIS_REFERENCE
    (
        diagnosis_reference_id,
        diagnosis_code,
        diagnosis_name,
        description,
        status
    )
    VALUES
        ('60000000-0000-0000-0000-000000000001', N'J02.9', N'Pharyngitis, unspecified', N'Sore throat and upper respiratory irritation', N'ACTIVE'),
        ('60000000-0000-0000-0000-000000000002', N'R51.9', N'Headache, unspecified', N'General headache symptoms', N'ACTIVE'),
        ('60000000-0000-0000-0000-000000000003', N'Z23', N'Encounter for immunization', N'Vaccination visit', N'ACTIVE'),
        ('60000000-0000-0000-0000-000000000004', N'R05.9', N'Cough, unspecified', N'Cough symptoms requiring evaluation', N'ACTIVE');
END
GO

SELECT
    (SELECT COUNT(*) FROM dbo.ROLE) AS role_count,
    (SELECT COUNT(*) FROM dbo.APP_USER) AS user_count,
    (SELECT COUNT(*) FROM dbo.DOCTOR) AS doctor_count,
    (SELECT COUNT(*) FROM dbo.NURSE) AS nurse_count,
    (SELECT COUNT(*) FROM dbo.PATIENT) AS patient_count,
    (SELECT COUNT(*) FROM dbo.DIAGNOSIS_REFERENCE) AS diagnosis_count;
GO
