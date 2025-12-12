@echo off
REM 
chcp 65001 > nul

echo Compilation du projet RetroConcepteur...
REM 
javac -encoding UTF-8 "@Compile.list" -d ./class

REM 
if %ERRORLEVEL% EQU 0 (
    echo Exécution du programme...
    echo.
    REM 
    REM 
    java -Dfile.encoding=UTF-8 -cp ./class RetroConcepteur.Controller %1
) else (
    echo Erreur de compilation!
    exit /b 1
)
pause