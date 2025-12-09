package controller;


import metier.AnalyseFichier;
import vue.AffichageCUI;

/*
 * Controller est la pont entre notre logique et l'IHM
 * TODO : Ammeliorer le système de chemin de fichier pour eviter de le changer à chaque fois
 */
public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/lk240510/TP/s2/r2.01_dev_objet/tp6/exercice_4_5_6/quarto/metier");
		this.affichageCUI = new AffichageCUI();

	}

	public void afficher ()
	{
		this.affichageCUI.afficherClasse(this.analyseFichier.getLstClasses());
	}


	public static void main(String[] args) 
	{
		new Controller();
	}

	private String nettoieNom(String nom)
	{
		int indexFin = nom.indexOf("(");
		if ( indexFin > 0 )
			nom = nom.substring(0, indexFin);
			
		return nom;
	}

}
