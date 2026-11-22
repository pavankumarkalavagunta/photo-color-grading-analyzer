@echo off
setlocal enabledelayedexpansion

echo ==============================================================================
echo   Photo Color Grading Analyzer - JSP, Servlets, Tomcat 10, JDBC, and Maven
echo ==============================================================================

:: Ensure Maven in PATH
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    set "PATH=%PATH%;C:\Users\pavan\apache-maven-3.9.6\bin"
)

:: Find Tomcat Home
set "CATALINA_HOME=C:\Users\pavan\tomcat\apache-tomcat-10.1.57"
if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\apache-tomcat-10.1.57"
)

echo [1/3] Using Tomcat at: %CATALINA_HOME%

:: Build with Maven and Deploy
echo [2/3] Building with Maven and deploying application...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0compile_and_deploy.ps1"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Deployment failed!
    pause
    exit /b %ERRORLEVEL%
)

:: Check if Python Analyzer is running
netstat -ano | findstr ":8000" | findstr "LISTENING" >nul
if %ERRORLEVEL% neq 0 (
    echo [3/3] Starting Python OpenCV Analyzer Engine on port 8000...
    start "Python Analyzer Engine" cmd /k "cd /d %~dp0..\analyzer && python -m uvicorn app.main:app --host 127.0.0.1 --port 8000"
    timeout /t 3 /nobreak >nul
) else (
    echo [3/3] Python OpenCV Engine is already running on port 8000.
)

echo.
echo ==============================================================================
echo Application is ready with JSP, Servlets, Tomcat, and JDBC!
echo   -> http://localhost:8081/photo-analyzer/
echo   -> http://localhost:8081/
echo ==============================================================================
echo.

:: Start Tomcat
"%CATALINA_HOME%\bin\catalina.bat" run
