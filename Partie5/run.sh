echo "Compilation du projet RetroConcepteur..."
javac "@Compile.list" -d ./class

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Exécution du programme..."
    echo
    # Passer le répertoire data en argument
    java -cp ./class RetroConcepteur.Controleur $1
else
    echo "Erreur de compilation!"
    exit 1
fi