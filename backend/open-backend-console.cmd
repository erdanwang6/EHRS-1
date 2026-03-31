@echo off
setlocal

set "BACKEND_DIR=%~dp0"
start "EHRS Backend" cmd /k "cd /d ""%BACKEND_DIR%"" && call run-backend.cmd"

exit /b 0
