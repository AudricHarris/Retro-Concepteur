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
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ha241570/Téléchargements/metier");
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
