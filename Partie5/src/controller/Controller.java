package controller;

import metier.classe.*;

import java.util.ArrayList;

import metier.AnalyseFichier;
import vue.FrameUML;

/*
 * Controller est la pont entre notre logique et l'IHM
 * TODO : Ammeliorer le système de chemin de fichier pour eviter de le changer à chaque fois
 */
public class Controller 
{
	private AnalyseFichier analyseFichier;
	private FrameUML   frame;


	public Controller() 
	{
		this.analyseFichier = new AnalyseFichier("/home/etudiant/lk240510/Documents/TESTSAE301");
		this.frame = new FrameUML(this);

	}

	public ArrayList<Classe> getLstClasse(){return analyseFichier.getLstClasses();}



	public static void main(String[] args) 
	{
		new Controller();
	}

}
