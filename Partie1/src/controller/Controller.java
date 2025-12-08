package controller;

import metier.AnalyseFichier;
import vue.AffichageCUI;

public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ll240582/TP/s3/s3.01_dev_application/Retro-Concepteur/Partie1/src/metier/classe");
		this.affichageCUI = new AffichageCUI();
	}

	public static void main(String[] args) 
	{
		new Controller();
	}





	
}


