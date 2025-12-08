package controller;


import metier.AnalyseFichier;
import vue.AffichageCUI;

public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/bp233262/TP/s1/r1.01_init_dev/tp10/exo2");
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


