echo "Compilation du projet retroconcepteur..."
javac "@Compile.list" -d ./class

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Exécution du programme..."
    echo
    # Passer le répertoire data en argument
    java -cp ./class retroconcepteur.Controleur $1
else
    echo "Erreur de compilation!"
    exit 1
fi