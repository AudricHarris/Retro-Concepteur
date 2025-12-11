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
		this.analyseFichier = new AnalyseFichier(this,"/home/etudiant/ha241570/TP/s3/r3.05_prog_syst/test");
		this.affichageCUI = new AffichageCUI();

	}

	public void afficher ()
	{
		System.out.println(this.affichageCUI.afficherClasse(this.analyseFichier.getLstClasses()));
	}

	public void afficherLiaison()
	{
			System.out.println(this.affichageCUI.afficherLiaison(this.analyseFichier));
	}

	public static void main(String[] args) 
	{
		Controller controller = new Controller();
		controller.afficher();
		controller.afficherLiaison();
	}

}
