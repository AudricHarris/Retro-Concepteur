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
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ll240582/TP/s2/s2.01_dev_application/repo/sae2125/Groupe8-SAE125/source/ihm/");
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

}
