echo "Compilation du projet retroconcepteur..."
javac "@Compile.list" -d ./class

# Verifier si la compilation a reussi
if [ $? -eq 0 ]; then
    echo "Execution du programme..."
    echo
    # Passer le repertoire data en argument
    java -cp ./class retroconcepteur.Controleur $1
else
    echo "Erreur de compilation!"
    exit 1
fi