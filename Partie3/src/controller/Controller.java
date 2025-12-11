package controller;


import metier.AnalyseFichier;
import vue.AffichageCUI;

import metier.classe.Classe;
import metier.classe.Liaison;

import java.util.ArrayList;
import java.util.List;

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
		this.analyseFichier = new AnalyseFichier("/home/etudiant/lk240510/TP/s3/s3.01_dev_application/Retro-Concepteur/Partie3/tests");
		this.affichageCUI   = new AffichageCUI(this);
	}

	/* 
	* Getters
	 */
	public ArrayList<Classe> getLstClasses()
	{
		return this.analyseFichier.getLstClasses();
	}

	public List<Liaison> getListLiaisonUnique()
	{
		return this.analyseFichier.getListLiaisonUnique();
	}

	public List<Liaison> getListLiaison()
	{
		return this.analyseFichier.getListLiaison();
	}

	/*
	* Autres Methodes
	*/

	public boolean estClasseProjet(String type)
	{
		return this.analyseFichier.estClasseProjet(type);
	}

	public Classe get(int ind)
	{
		return this.analyseFichier.getLstClasses().get(ind);
	}

	public void afficher ()
	{
		System.out.println(this.affichageCUI.afficherClasse());
	}

	public void afficherLiaison()
	{
			System.out.println(this.affichageCUI.afficherLiaison());
	}

	public static void main(String[] args) 
	{
		Controller controller = new Controller();
		controller.afficher();
		controller.afficherLiaison();
	}

}
