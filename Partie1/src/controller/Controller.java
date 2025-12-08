package controller;


import metier.AnalyseFichier;
import vue.AffichageCUI;

public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ha241570/TP/s3/s3.01_dev_application/Retro-Concepteur/Partie1/src/controller");
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


