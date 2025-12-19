package retroconcepteur.vue.outil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represente la zone rectangulaire d'une classe UML sur le diagramme.
 * Gere les dimensions, la position et les points d'ancrage des liaisons (chemins).
 */
public class Rectangle
{
	private int x;
	private int y;
	private int tailleX;
	private int tailleY;
	
	// Stocke les chemins connectes sur chaque cote ('H', 'B', 'G', 'D')
	private HashMap<Character, ArrayList<Chemin>> hashPosPrises;

	/**
	 * Cree une nouvelle instance de Rectangle.
	 *
	 * @param x       Position X du coin haut-gauche.
	 * @param y       Position Y du coin haut-gauche.
	 * @param tailleX Largeur du rectangle.
	 * @param tailleY Hauteur du rectangle.
	 */
	public Rectangle(int x, int y, int tailleX, int tailleY)
	{
		this.x       = x;
		this.y       = y;
		this.tailleX = tailleX;
		this.tailleY = tailleY;
		
		this.hashPosPrises = new HashMap<>();

		this.hashPosPrises.put('H', new ArrayList<Chemin>()); // Haut
		this.hashPosPrises.put('B', new ArrayList<Chemin>()); // Bas
		this.hashPosPrises.put('D', new ArrayList<Chemin>()); // Droite
		this.hashPosPrises.put('G', new ArrayList<Chemin>()); // Gauche
	}

	/*---------------------------------------*/
	/* GETTERS                */
	/*---------------------------------------*/
	
	public int getX      () { return this.x                   ; }
	public int getY      () { return this.y                   ; }
	public int getCentreX() { return this.x + this.tailleX / 2; }
	public int getCentreY() { return this.y + this.tailleY / 2; }
	public int getTailleX() { return this.tailleX             ; }
	public int getTailleY() {return this.tailleY              ;	}

	/**
	 * Retourne la liste complete de tous les chemins connectes a ce rectangle,
	 * tous cotes confondus.
	 */
	public List<Chemin> getListeChemin()
	{
		ArrayList<Chemin> liste = new ArrayList<>();
		for (ArrayList<Chemin> lst : this.hashPosPrises.values())
		{
			liste.addAll(lst);
		}
		return liste;
	}


	public int getNbPoint(char c) {return this.hashPosPrises.get(c).size();}

	/*---------------------------------------*/
	/*               SETTERS                 */
	/*---------------------------------------*/
	

	public void setTailleX(int x) 
	{ 
		this.tailleX = x; 
		this.mettreAJourToutesLesLiaisons(); 
	}


	public void setTailleY(int y) 
	{ 
		this.tailleY = y; 
		this.mettreAJourToutesLesLiaisons(); 
	}


	public void setX(int x) 
	{ 
		this.x = x; 
		this.mettreAJourToutesLesLiaisons(); 
	}


	public void setY(int y) 
	{ 
		this.y = y; 
		this.mettreAJourToutesLesLiaisons(); 
	}

	/*---------------------------------------*/
	/*          MODIFICATEURS                */
	/*---------------------------------------*/


	public void deplacerX(int deltaX) {this.x += deltaX;}
	public void deplacerY(int deltaY) {this.y += deltaY;}

	/**
	 * Ajoute un chemin (liaison) sur un cote specifique du rectangle.
	 * @param c La direction ('H', 'B', 'G', 'D').
	 * @param chemin L'objet chemin a connecter.
	 */
	public void addPos(char c, Chemin chemin) {this.hashPosPrises.get(c).add(chemin);}
	
	/**
	 * Supprime le dernier chemin ajoute sur un cote specifique.
	 * @param c La direction ('H', 'B', 'G', 'D').
	 */
	public void supPos(char c)
	{
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get(c);
		if (!listeChemins.isEmpty())
			listeChemins.remove(listeChemins.size() - 1);
	}

	/*---------------------------------------*/
	/*         MeTHODES D'INSTANCE           */
	/*---------------------------------------*/
	
	
	
	/**
	 * Verifie si un point donne se trouve a l'interieur du rectangle.
	 * @param autre Le point a tester.
	 * @return true si le point est dans le rectangle, false sinon.
	 */
	public boolean possede(Point autre)
	{
		int autreX = autre.getX();
		int autreY = autre.getY();

		return autreX >= this.x && autreX <= this.x + this.tailleX &&
			autreY >= this.y && autreY <= this.y + this.tailleY;
	}

	/**
	 * Recalcule la position des points d'ancrage sur un cote donne
	 * pour qu'ils soient equitablement espaces.
	 * @param zone Le cote a mettre a jour ('H', 'B', 'G', 'D').
	 */
	public void repartirPointsLiaison(char zone)
	{
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get(zone);
		int nbPoints = listeChemins.size();
		
		if (nbPoints > 0)
		{
			double espacement;
			 
			if (zone == 'H' || zone == 'B') 
			{
				espacement = (double) this.tailleX / (nbPoints + 1);
				
				for (int i = 0; i < nbPoints; i++) 
				{
					Chemin chemin = listeChemins.get(i);
					int positionX = this.x + (int)(espacement * (i + 1));
					int positionY = (zone == 'H') ? this.y : this.y + this.tailleY;

					chemin.updatePoint(this, positionX, positionY);                     
				}
			}
			else if (zone == 'G' || zone == 'D') 
			{
				espacement = (double) this.tailleY / (nbPoints + 1);

				for (int i = 0; i < nbPoints; i++) 
				{
					Chemin chemin = listeChemins.get(i);
					int positionY = this.y + (int)(espacement * (i + 1));
					int positionX = (zone == 'G') ? this.x : this.x + this.tailleX;

					chemin.updatePoint(this, positionX, positionY);             
				}
			}
		}
	}

	/**
	 * Force le recalcul des positions de toutes les liaisons connectees au rectangle.
	 */
	public void mettreAJourToutesLesLiaisons() 
	{
		this.repartirPointsLiaison('H');
		this.repartirPointsLiaison('B');
		this.repartirPointsLiaison('G');
		this.repartirPointsLiaison('D');
	}

	/**
	 * Vide toutes les listes de connexions.
	 * Utilise avant de recalculer entierement le graphe.
	 */
	public void nettoyerLiaisons()
	{
		this.hashPosPrises.get('H').clear();
		this.hashPosPrises.get('B').clear();
		this.hashPosPrises.get('G').clear();
		this.hashPosPrises.get('D').clear();
	}
}