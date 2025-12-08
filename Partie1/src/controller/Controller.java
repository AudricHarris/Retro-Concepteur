package controller;


import metier.AnalyseFichier;
import vue.AffichageCUI;

public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ha241570/TP/s2/r2.01_dev_objet/tp4");
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


