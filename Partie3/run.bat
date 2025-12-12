@echo off

setlocal EnableDelayedExpansion
echo Compilation du projet Retro concepteur...
javac "@Compile.list" -d ./class

REM Vérifier si la compilation a réussi
if !ERRORLEVEL! EQU 0 (
    echo Exécution du programme...
    echo.
    java -cp ./class RetroConcepteur.Controller
) else (
    echo Erreur de compilation!
)
pause
