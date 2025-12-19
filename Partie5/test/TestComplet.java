package tests;

import java.util.List;
import java.util.ArrayList;

/**
 * Ce fichier sert a tester les limites de l'analyseur Retro-Concepteur.
 * Il contient des modificateurs varies et des structures complexes.
 */
public abstract interface TestComplet extends Object implements Cloneable{

    // --- Attributs avec differents modificateurs ---

    // Cas standard
    private int simpleAttribute;
    
    // Modificateurs multiples
    public static final double PI = 3.14159;
    
    // Cas non geres actuellement (transient, volatile)
    // Risque : Votre analyseur pourrait prendre "transient" pour le type de la variable !

    // Types complexes (Generiques)
    public List<String> maListe;
    private ArrayList<Integer> nombres;

    // Tableaux
    public int[] tableauEntiers;
    private String[][] matriceChaines;

    // --- Methodes avec differents modificateurs ---

    // Constructeur
    public TestComplet() {
        this.maListe = new ArrayList<>();
    }

    // Methode abstraite (pas de corps)
    public abstract void methodeAbstraite(int test,
		String ligne);

    // Methode synchronisee
    // Risque : Votre analyseur pourrait croire que le type de retour est "synchronized"
    public synchronized void methodeSynchronisee() 	{
        this.estActif = true;
    }
	/*
	public static int estCommentaoire()
	{
	}
	*/

    // Methode statique finale avec parametres
    static final int calculer(int a,
    						 int b) 	{
        return a + b;
    }

    // Methode native (mot cle native)
    public void methodeNative();

    // Methode qui lance des exceptions (throws)
    // Risque : Votre analyseur gere-t-il le "throws Exception" apres la parenthese ?
    public void methodeAvecException() throws Exception {
        throw new Exception("Erreur");
    }
}
