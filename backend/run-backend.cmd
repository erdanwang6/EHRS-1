@echo off
setlocal

set "BACKEND_DIR=%~dp0"
set "REPO_ROOT=%BACKEND_DIR%.."
set "MAVEN_CMD=%REPO_ROOT%\.tools\apache-maven-3.9.9\bin\mvn.cmd"
set "JDBC_AUTH_DIR=%REPO_ROOT%\.tools\mssql-jdbc-auth"
set "JDBC_AUTH_DLL=%JDBC_AUTH_DIR%\mssql-jdbc_auth-12.6.3.x64.dll"

if not exist "%MAVEN_CMD%" (
    echo Maven was not found at "%MAVEN_CMD%".
    echo Install Maven in .tools or update this launcher.
    exit /b 1
)

if exist "%JDBC_AUTH_DLL%" (
    if defined MAVEN_OPTS (
        set "MAVEN_OPTS=%MAVEN_OPTS% -Djava.library.path=%JDBC_AUTH_DIR%"
    ) else (
        set "MAVEN_OPTS=-Djava.library.path=%JDBC_AUTH_DIR%"
    )
)

pushd "%BACKEND_DIR%"
echo Starting EHRS backend...
echo Backend logs will remain in this command window.
call "%MAVEN_CMD%" -Dspring-boot.run.fork=false spring-boot:run %*
set "EXIT_CODE=%ERRORLEVEL%"
popd

exit /b %EXIT_CODE%
