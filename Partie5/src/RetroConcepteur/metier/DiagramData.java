package RetroConcepteur.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.vue.outil.*;

/**
 * Classe pour stocker les données du diagramme UML pour la sérialisation.
 */
public class DiagramData implements Serializable
{
	private ArrayList<Classe> lstClasse;
	private List<Liaison> lstLiaisons;
	private List<Chemin> lstChemins;
	private HashMap<Classe, Rectangle> mapClasseRectangle;

	public DiagramData(ArrayList<Classe> lstClasse, List<Liaison> lstLiaisons, List<Chemin> lstChemins, HashMap<Classe, Rectangle> mapClasseRectangle)
	{
		this.lstClasse = lstClasse;
		this.lstLiaisons = lstLiaisons;
		this.lstChemins = lstChemins;
		this.mapClasseRectangle = mapClasseRectangle;
	}

	// Getters
	public ArrayList<Classe> getLstClasse() { return lstClasse; }
	public List<Liaison> getLstLiaisons() { return lstLiaisons; }
	public List<Chemin> getLstArcs() { return this.lstChemins; }
	public HashMap<Classe, Rectangle> getMapClasseRectangle() { return mapClasseRectangle; }
}
