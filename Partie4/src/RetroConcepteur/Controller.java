package RetroConcepteur;

import RetroConcepteur.vue.AffichageCUI;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;

/**
 * Controller est la pont entre notre logique et l'IHM
 */
public class Controller 
{
	private AnalyseFichier analyseFichier;
	private AffichageCUI   affichageCUI;

	/**
	 * Constructeur de controller
	 * @param cheminDonnees chemin du projets java avec fichier java
	 */
	public Controller(String cheminDonnees) 
	{
		this.analyseFichier = new AnalyseFichier(cheminDonnees);
		this.affichageCUI   = new AffichageCUI(this);
		
	}

	//---------------------------------------//
	//             Getters                   //
	//---------------------------------------//
	public ArrayList<Classe> getLstClasses () { return this.analyseFichier.getLstClasses (); }
	public List<Liaison>     getListLiaison() { return this.analyseFichier.getListLiaison(); }

	public List<Liaison> getListLiaisonUnique() 
	{
		return this.analyseFichier.getListLiaisonUnique();
	}

	public List<Liaison> getListLiaisonBinaire()
	{
		return this.analyseFichier.getListLiaisonBinaire();
	}


	public boolean estClasseProjet(String type)
	{
		return this.analyseFichier.estClasseProjet(type);
	}

	public Classe get(int ind)
	{
		return this.analyseFichier.getLstClasses().get(ind);
	}

	//---------------------------------------//
	//             Affichage                 //
	//---------------------------------------//


	public void afficherClasse()
	{
		System.out.println(this.affichageCUI.afficherClasse());
	}

	public void afficherLiaison()
	{
		System.out.println(this.affichageCUI.afficherLiaison());
	}

	//---------------------------------------//
	//             Methode static            //
	//---------------------------------------//


	public static void main(String[] args) 
	{

		String cheminRepertoire = (args.length > 0) ? args[0] : "./data";
		Controller controller = new Controller(cheminRepertoire);
		controller.afficherClasse();
		controller.afficherLiaison();
	}
}
