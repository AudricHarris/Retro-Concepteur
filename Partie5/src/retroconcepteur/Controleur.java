package retroconcepteur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retroconcepteur.metier.AnalyseFichier;
import retroconcepteur.metier.classe.Attribut;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.metier.classe.Methode;
import retroconcepteur.metier.classe.Liaison;
import retroconcepteur.metier.classe.Position;

import retroconcepteur.metier.gerexml.ChargerXml;
import retroconcepteur.metier.gerexml.SauvegarderXml;

import retroconcepteur.vue.FrameUML;
import retroconcepteur.vue.outil.Rectangle;


/**
 * Controleur est la pont entre notre logique et l'IHM
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */

public class Controleur 
{
	private AnalyseFichier analyseFichier;

	private FrameUML frameUML;
	private String cheminDonnees;

	public Controleur( String cheminDonnees ) 
	{
		this.cheminDonnees = cheminDonnees;
		this.analyseFichier = new AnalyseFichier(this.cheminDonnees);

		this.frameUML = new FrameUML(this);
	}

	/*---------------------------------------*/
	/*              Accesseurs               */
	/*---------------------------------------*/

	public ArrayList<Classe> getLstClasses        () { return this.analyseFichier.getLstClasses()        ; }
	public List<Liaison>     getListLiaison       () { return this.analyseFichier.getListLiaison()       ; }
	public List<Liaison>     getListLiaisonUnique () { return this.analyseFichier.getListLiaisonUnique() ; }
	public List<Liaison>     getListLiaisonBinaire() { return this.analyseFichier.getListLiaisonBinaire(); }

	public Classe getClasseAvecMeth( Methode meth )
	{
		for ( Classe c : this.getLstClasses() )
			if ( c.getLstMethode().contains(meth)) 
				return c;
		return null;
	}

	/*  */
	
	public void majAttribut(Attribut att, String nom, boolean estConstante, boolean isAddOnly) 
	{
		att.setNom(nom);
		att.setConstante(estConstante);
		att.setAddOnly(isAddOnly);
		this.analyseFichier.majLiaison();
	}

	public boolean estClasseProjet(String type)
	{
		return this.analyseFichier.estClasseProjet(type);
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
				mapPositions.put(c, new Position(r.getX(), r.getY(), r.getTailleX(), r.getTailleY()));
		}

		SauvegarderXml.sauvegarderXml(chemin, lst, mapPositions, this.getListLiaison());
	}

	public void chargerXml(String chemin)
	{
		ArrayList<Classe> classesLoaded = ChargerXml.chargerClassesXml(chemin);
		if (classesLoaded == null || classesLoaded.isEmpty()) return;

		HashMap<String, Position> posCharger = ChargerXml.chargerPositionsXml(chemin);

		List<Liaison> liaisonsCharger = ChargerXml.chargerLiaisonsXml(chemin, classesLoaded);

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
	public void majIHM()
	{
		this.frameUML.majIHM();
	}

	public static void main(String[] args) 
	{ 
		String cheminRepertoire = (args.length > 0) ? args[0] : "./test";
        new Controleur(cheminRepertoire);
	}



}