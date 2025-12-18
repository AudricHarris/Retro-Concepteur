package RetroConcepteur;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import RetroConcepteur.metier.AnalyseFichier;
import RetroConcepteur.metier.GereXml;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.metier.classe.Position;
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
		HashMap<Classe, Rectangle> mapRect = this.frameUML.getMapPanel();
		HashMap<Classe, Position> mapPositions = new HashMap<>();
    	for (Classe c : lst) 
		{
			Rectangle r = mapRect.get(c);
			if (r != null) 
				{
				mapPositions.put(c, new Position(r.getX(), r.getY(), r.getTailleX(), r.getTailleY()));
			}
		}

		GereXml.sauvegarderXml(chemin, lst, mapPositions, this.getListLiaison());
	}

	public void chargerXml(String chemin)
	{
		ArrayList<Classe> classesLoaded = GereXml.chargerClassesXml(chemin);
		if (classesLoaded == null || classesLoaded.isEmpty()) return;

		HashMap<String, Position> posCharger = GereXml.chargerPositionsXml(chemin);

		List<Liaison> liaisonsCharger = GereXml.chargerLiaisonsXml(chemin, classesLoaded);

		if (liaisonsCharger != null && !liaisonsCharger.isEmpty())
			this.analyseFichier.remplacerClassesEtLiaisons(classesLoaded, liaisonsCharger);
		else
			this.analyseFichier.remplacerClassesEtLiaisons(classesLoaded);

		this.frameUML.reinitialiser();

		HashMap<Classe, Rectangle> nouvMap = new HashMap<Classe, Rectangle>();

		for (Classe c : this.getLstClasses())
		{
			Position pos = posCharger.get(c.getNom());
			if (pos != null)
			{
				Rectangle r = new Rectangle(
					pos.getX(),
					pos.getY(),
					pos.getLargeur(),
					pos.getHauteur()
				);
				nouvMap.put(c, r);
			}
		}

		this.frameUML.setMapPanel(nouvMap);
	}

	public static void main(String[] args) 
	{ 
		String cheminRepertoire = (args.length > 0) ? args[0] : "./test";
        new Controller(cheminRepertoire);
	}
}