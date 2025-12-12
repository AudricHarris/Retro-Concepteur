package RetroConcepteur;

import RetroConcepteur.vue.AffichageCUI;

import java.util.ArrayList;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;

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
		this.analyseFichier = new AnalyseFichier("/home/etudiant/lk240510/Bureau/Retro-Concepteur/Partie3/data");
		this.affichageCUI   = new AffichageCUI(this);
		
	}

	/* 
	* Getters
	 */
	public ArrayList<Classe> getLstClasses()
	{
		return this.analyseFichier.getLstClasses();
	}

	public List<Liaison> getListLiaison()
	{
		return this.analyseFichier.getListLiaison();
	}
	
	public List<Liaison> getListLiaisonUnique()
	{
		return this.analyseFichier.getListLiaisonUnique();
	}

	public List<Liaison> getListLiaisonBinaire()
	{
		return this.analyseFichier.getListLiaisonBinaire();
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
