package RetroConcepteur;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.GereXml;
import RetroConcepteur.metier.classe.Attribut;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.vue.FrameUML;
import RetroConcepteur.vue.outil.Rectangle;

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
	
	public void majAttribut(Attribut att, String nom, boolean estConstante, boolean isAddOnly) 
	{
		att.setNom(nom);
		att.setConstante(estConstante);
		att.setAddOnly(isAddOnly);
	}

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

	public void sauvegarderXml(String chemin)
	{
		ArrayList<Classe> lst = this.getLstClasses();
		HashMap<Classe, Rectangle> map = this.frameUML.getMapPanel();
		GereXml.sauvegarderXml(chemin, lst, map, this.getListLiaison());
	}

	public void chargerXml(String chemin)
	{
		ArrayList<Classe> classesLoaded = GereXml.chargerClassesXml(chemin);
		if (classesLoaded == null || classesLoaded.isEmpty()) return;

		HashMap<String, Rectangle> positionsLoaded = GereXml.chargerPositionsXml(chemin);

		// Charger les liaisons (si présentes dans le fichier)
		List<Liaison> liaisonsLoaded = GereXml.chargerLiaisonsXml(chemin, classesLoaded);

		if (liaisonsLoaded != null && !liaisonsLoaded.isEmpty())
			this.analyseFichier.remplacerClassesEtLiaisons(classesLoaded, liaisonsLoaded);
		else
			this.analyseFichier.remplacerClassesEtLiaisons(classesLoaded);

		this.frameUML.reinitialiser();

		HashMap<Classe, Rectangle> newMap = new HashMap<>();
		for (Classe c : this.getLstClasses())
		{
			Rectangle r = positionsLoaded.get(c.getNom());
			if (r != null) newMap.put(c, r);
		}

		this.frameUML.setMapPanel(newMap);
	}
	public void majIHM()
	{
		this.frameUML.majIHM();
	}

	public static void main(String[] args) 
	{ 
		String cheminRepertoire = (args.length > 0) ? args[0] : "./test";
        new Controller(cheminRepertoire);
	}



}