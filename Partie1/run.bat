@echo off

setlocal EnableDelayedExpansion
echo Compilation du projet Retro concepteur...
javac "@Compile.list" -d ./bin

REM Vérifier si la compilation a réussi
if !ERRORLEVEL! EQU 0 (
    echo Exécution du programme...
    echo.
    java -cp ./bin controller.Controler
) else (
    echo Erreur de compilation!
)
pause