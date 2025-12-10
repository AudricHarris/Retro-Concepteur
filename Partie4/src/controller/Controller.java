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
		this.analyseFichier = new AnalyseFichier(this,"/home/etudiant/bp233262/TP/s3/s3.01_dev_application/Retro-Concepteur/Partie4/tests");
		this.affichageCUI = new AffichageCUI();

	}

	public void afficher ()
	{
		System.out.println(this.affichageCUI.afficherClasse(this.analyseFichier.getLstClasses()));
	}


	public static void main(String[] args) 
	{
		new Controller().afficher();
	}

}
