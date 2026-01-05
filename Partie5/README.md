# RetroConcepteur (Generateur de Diagrammes UML)

**RetroConcepteur** est un outil de retro-ingenierie developpe en Java. Il permet d'analyser du code Java existant pour generer et visualiser automatiquement son diagramme de classes UML.

Ce projet a ete realise par l'**equipe 9** dans le cadre de la SAE 3.01 a l'IUT du Havre.

## 🚀 Fonctionnalites Cles

* **Retro-ingenierie :** Analyse automatique des fichiers `.java` pour extraire la structure.
* **Visualisation Graphique :**
    * Affichage des Classes, Interfaces et Classes Abstraites.
    * Affichage reduite ou detaille des Attributs et Methodes avec leur visibilite (+, -, #).
* **Gestion des Liaisons Intelligente :**
    * Detection automatique des Associations, Heritages, Implementations, etc.
    * Algorithme de routage des fleches (evite le chevauchement des liens multiples).
* **edition et Manipulation :**
    * Deplacement des classes  avec prise en compte des points d'encrages.
    * edition des Attributs d'une classe (Nom, freeze) via double-clic.
    * Modification des multiplicites sur les liens.
* **Sauvegarde, exportation et chargement**
	* Sauvegarde du projet dans un fichier XML
	* Chargement d'un projet via un fichier XML
	* Exportation du projet en image


## 🛠️ Architecture Technique

Le projet respecte strictement le Modele de conception **MVC (Modele-Vue-Controleur)** :

* **Modele (`metier`) :** Contient la representation des donnees (`Classe`, `Attribut`, `Liaison`). Independant de l'interface.
* **Vue (`vue`) :** Gere l'affichage Swing (`PanelUML`, `FrameUML`).
    * Utilisation de *classes deleguees* pour le dessin (`DessinerFleche`, `DessinerClasse`).
* **Controleur :** Orchestre les interactions utilisateur et met a jour le modele.

**Technologies :** Java Swing (AWT).

## 📋 Prerequis

* **Java JDK :** Version [17 ou 21] minimum.

## 🔧 Installation et Lancement

### Depuis un IDE (Eclipse/IntelliJ)
1.  Clonez ce depot :
    ```bash
    git clone https://github.com/AudricHarris/Retro-Concepteur.git
    ```
2.  Ouvrez le projet dans votre IDE.
3.  Lancez l'execution (`start`):

### Linux / Mac
Exécutez le script fourni à la racine :
```bash
./start.sh + "chemin du dossier si besoin"
```

### Windows
Lancez le script batch :
```cmd
start.bat + "chemin du dossier si besoin"
```


