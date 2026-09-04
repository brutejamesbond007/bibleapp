@echo off
setlocal

REM ============================================================
REM Chronological Bible Reader - Portable Windows Launcher
REM ============================================================

REM Always run from the folder containing this BAT file.
REM This allows the application to work regardless of the USB
REM drive letter assigned by Windows.
cd /d "%~dp0"

REM ------------------------------------------------------------
REM Portable Windows Java runtime
REM ------------------------------------------------------------

set "JAVA=%~dp0runtime-windows\java\bin\java.exe"

if not exist "%JAVA%" (
    echo.
    echo ========================================================
    echo ERROR: Windows Java runtime not found.
    echo ========================================================
    echo.
    echo Expected:
    echo %JAVA%
    echo.
    pause
    exit /b 1
)

REM ------------------------------------------------------------
REM Bible Reader application JAR
REM ------------------------------------------------------------

if not exist "BibleReader.jar" (
    echo.
    echo ========================================================
    echo ERROR: BibleReader.jar not found.
    echo ========================================================
    echo.
    echo Expected:
    echo %~dp0BibleReader.jar
    echo.
    pause
    exit /b 1
)

REM ------------------------------------------------------------
REM SQLite database
REM ------------------------------------------------------------

if not exist "data\bible-reader.db" (
    echo.
    echo ========================================================
    echo ERROR: SQLite database not found.
    echo ========================================================
    echo.
    echo Expected:
    echo %~dp0data\bible-reader.db
    echo.
    pause
    exit /b 1
)

REM ------------------------------------------------------------
REM SQLite JDBC driver
REM ------------------------------------------------------------

if not exist "lib\sqlite\sqlite-jdbc-3.53.2.1.jar" (
    echo.
    echo ========================================================
    echo ERROR: SQLite JDBC driver not found.
    echo ========================================================
    echo.
    echo Expected:
    echo %~dp0lib\sqlite\sqlite-jdbc-3.53.2.1.jar
    echo.
    pause
    exit /b 1
)

REM ------------------------------------------------------------
REM Start Chronological Bible Reader
REM ------------------------------------------------------------

"%JAVA%" ^
-cp "BibleReader.jar;lib\sqlite\sqlite-jdbc-3.53.2.1.jar" ^
BibleReader

REM ------------------------------------------------------------
REM If Java returns an error, keep the window open so the error
REM can be read instead of immediately closing.
REM ------------------------------------------------------------

if errorlevel 1 (
    echo.
    echo ========================================================
    echo Bible Reader exited with an error.
    echo ========================================================
    echo.
    pause
    exit /b 1
)

endlocal
exit /b 0
