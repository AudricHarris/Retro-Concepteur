package RetroConcepteur.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.vue.outil.Arc;
import RetroConcepteur.vue.outil.Rectangle;

/**
 * Classe pour stocker les données du diagramme UML pour la sérialisation.
 */
public class DiagramData
{
    private ArrayList<Classe> lstClasse;
    private List<Liaison> lstLiaisons;
    private List<Arc> lstArcs;
    private HashMap<Classe, Rectangle> mapClasseRectangle;

    public DiagramData(ArrayList<Classe> lstClasse, List<Liaison> lstLiaisons, List<Arc> lstArcs, HashMap<Classe, Rectangle> mapClasseRectangle)
    {
        this.lstClasse = lstClasse;
        this.lstLiaisons = lstLiaisons;
        this.lstArcs = lstArcs;
        this.mapClasseRectangle = mapClasseRectangle;
    }

    // Getters
    public ArrayList<Classe> getLstClasse() { return lstClasse; }
    public List<Liaison> getLstLiaisons() { return lstLiaisons; }
    public List<Arc> getLstArcs() { return lstArcs; }
    public HashMap<Classe, Rectangle> getMapClasseRectangle() { return mapClasseRectangle; }
}