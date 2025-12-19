@echo off

chcp 65001 > nul

echo Compilation du projet retroconcepteur...

javac -encoding UTF-8 "@Compile.list" -d ./class

REM Verifier si la compilation a reussi
if %ERRORLEVEL% EQU 0 (
    echo Execution du programme...
    echo.
    REM Passer le repertoire data en argument
	java -Dfile.encoding=UTF-8 -cp ./class retroconcepteur.Controleur %1
) else (
    echo Erreur de compilation!
    exit /b 1
)
pause
