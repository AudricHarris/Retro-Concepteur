package RetroConcepteur;


import java.util.ArrayList;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.vue.FrameUML;

/*
 * Controller est la pont entre notre logique et l'IHM
 */
public class Controller 
{
	private AnalyseFichier analyseFichier;

	private FrameUML frameUML;
	private String cheminDonnees;

	public Controller( String cheminDonnees ) 
	{
		this.cheminDonnees = cheminDonnees;
		this.analyseFichier = new AnalyseFichier(this.cheminDonnees);

		this.frameUML = new FrameUML(this);
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

	public void ouvrirDossier(String dossier)
	{
		this.analyseFichier.ouvrirDossier(dossier);
	}

	public static void main(String[] args) 
	{ 
		String cheminRepertoire = (args.length > 0) ? args[0] : "./data";
        new Controller(cheminRepertoire);
	}
}