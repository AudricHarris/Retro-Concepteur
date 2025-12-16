package tests;

import java.util.List;
import java.util.ArrayList;

/**
 * Ce fichier sert à tester les limites de l'analyseur Retro-Concepteur.
 * Il contient des modificateurs variés et des structures complexes.
 */
public abstract interface TestComplet extends Object implements Cloneable{

    // --- Attributs avec différents modificateurs ---

    // Cas standard
    private int simpleAttribute;
    
    // Modificateurs multiples
    public static final double PI = 3.14159;
    
    // Cas non gérés actuellement (transient, volatile)
    // Risque : Votre analyseur pourrait prendre "transient" pour le type de la variable !

    // Types complexes (Génériques)
    public List<String> maListe;
    private ArrayList<Integer> nombres;

    // Tableaux
    public int[] tableauEntiers;
    private String[][] matriceChaines;

    // --- Méthodes avec différents modificateurs ---

    // Constructeur
    public TestComplet() {
        this.maListe = new ArrayList<>();
    }

    // Méthode abstraite (pas de corps)
    public abstract void methodeAbstraite(int beuteu,
		String ligne);

    // Méthode synchronisée
    // Risque : Votre analyseur pourrait croire que le type de retour est "synchronized"
    public synchronized void methodeSynchronisee() 	{
        this.estActif = true;
    }
	/*
	public static int estCommentaoire()
	{
	}
	*/

    // Méthode statique finale avec paramètres
    static final int calculer(int a,
    						 int b) 	{
        return a + b;
    }

    // Méthode native (mot clé native)
    public void methodeNative();

    // Méthode qui lance des exceptions (throws)
    // Risque : Votre analyseur gère-t-il le "throws Exception" après la parenthèse ?
    public void methodeAvecException() throws Exception {
        throw new Exception("Erreur");
    }
}
