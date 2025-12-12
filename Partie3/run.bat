@echo off

echo Compilation du projet RetroConcepteur...

javac -encoding UTF-8 "@Compile.list" -d ./class

REM Vérifier si la compilation a réussi
if %ERRORLEVEL% EQU 0 (
    echo Exécution du programme...
    echo.
    REM Passer le répertoire data en argument
	java -Dfile.encoding=UTF-8 -cp ./class RetroConcepteur.Controller %1
) else (
    echo Erreur de compilation!
    exit /b 1
)
pause
