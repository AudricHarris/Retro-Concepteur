package RetroConcepteur;

import RetroConcepteur.vue.AffichageCUI;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;

/*
 * Controller est la pont entre notre logique et l'IHM
 */
public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;


	public Controller(String cheminDonnees) 
	{
<<<<<<< Updated upstream:Partie3/src/RetroConcepteur/Controller.java
		this.analyseFichier = new AnalyseFichier(cheminDonnees);
=======
		this.analyseFichier = new AnalyseFichier("/home/etudiant/ha241570/TP/s3/s3.01_dev_application/Retro-Concepteur/Partie3/tests");
>>>>>>> Stashed changes:Partie3/src/controller/Controller.java
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

		String cheminRepertoire = (args.length > 0) ? args[0] : "./data";
		Controller controller = new Controller(cheminRepertoire);
		controller.afficher();
		controller.afficherLiaison();
	}
}