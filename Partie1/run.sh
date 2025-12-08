echo "Compilation du projet RetroConcepteur..."
javac "@Compile.list" -d ./bin

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Exécution du programme..."
    echo
    java -cp ./bin controller.Controler
else
    echo "Erreur de compilation!"
    exit 1
fi