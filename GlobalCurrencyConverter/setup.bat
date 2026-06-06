@echo off
:: ============================================================
::  setup.bat — One-click setup for Global Currency Converter
::  Run this file as Administrator the first time.
::  Author: Rohit Sharma
:: ============================================================

echo.
echo =====================================================
echo   Global Currency Converter — Setup Script
echo =====================================================
echo.

:: ---- Check Java ----
java -version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH.
    echo Please install JDK from: https://adoptium.net
    pause & exit /b 1
)
echo [OK] Java found.

:: ---- Check Maven ----
mvn -version 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven already installed. Skipping download.
    goto BUILD
)

echo [INFO] Maven not found. Downloading Maven 3.9.6...

:: Download Maven zip using PowerShell
powershell -Command "& { Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip' -UseBasicParsing }"

if not exist maven.zip (
    echo [ERROR] Maven download failed. Check your internet connection.
    echo Alternatively, download Maven manually from https://maven.apache.org/download.cgi
    echo and add it to your PATH.
    pause & exit /b 1
)

echo [INFO] Extracting Maven...
powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath 'C:\maven' -Force"
del maven.zip

:: Add Maven to PATH for this session
set "PATH=C:\maven\apache-maven-3.9.6\bin;%PATH%"

:: Persist to system PATH
setx PATH "C:\maven\apache-maven-3.9.6\bin;%PATH%" /M >nul 2>&1
echo [OK] Maven installed at C:\maven\apache-maven-3.9.6

:BUILD
echo.
echo =====================================================
echo   Building project with Maven...
echo =====================================================
echo.

mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed. Check the error messages above.
    echo Common fixes:
    echo   1. Update src/database/DBConnection.java with your MySQL password
    echo   2. Make sure MySQL is running
    pause & exit /b 1
)

echo.
echo =====================================================
echo   Build successful!
echo =====================================================
echo.
echo Next steps:
echo   1. Start MySQL and run: database.sql
echo   2. Update DBConnection.java with your MySQL password
echo   3. Run:  mvn tomcat7:run
echo   4. Open: http://localhost:8080
echo.
pause
