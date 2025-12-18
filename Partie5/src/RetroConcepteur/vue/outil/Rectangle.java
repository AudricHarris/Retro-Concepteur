package RetroConcepteur.vue.outil;

// Paquetage awt
import java.awt.List;

// Paquetage Util
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

// Nos paquetage
import RetroConcepteur.vue.outil.*;

public class Rectangle
{
	private int x;
	private int y;
	private int tailleX;
	private int tailleY;
	private HashMap<Character, ArrayList<Chemin>> hashPosPrises;

	/**
	 *	Creer une instance de Rectangle
	 *	@param x pos X de coin haut gauche
	 *	@param y cord Y
	 *	@param tailleX taille de longeur
	 *	@param tailleY taille de la hauteur
	 */
	public Rectangle(int x, int y, int tailleX, int tailleY)
	{
		this.x = x;
		this.y = y;

		this.tailleX = tailleX;
		this.tailleY = tailleY;
		
		this.hashPosPrises = new HashMap<Character, ArrayList<Chemin>>();

		this.hashPosPrises.put('H', new ArrayList<Chemin>());
		this.hashPosPrises.put('B', new ArrayList<Chemin>());
		this.hashPosPrises.put('D', new ArrayList<Chemin>());
		this.hashPosPrises.put('G', new ArrayList<Chemin>());
	}

	//---------------------------------------//
	//          Gestion de souris            //
	//---------------------------------------//
	
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	
	public int getCentreX() { return this.x + this.tailleX / 2; }
	public int getCentreY() { return this.y + this.tailleY / 2; }

	public int getTailleX() { return this.tailleX; }
	public int getTailleY() { return this.tailleY; }

	public ArrayList<Chemin> getListeChemin()
	{
		ArrayList<Chemin> liste = new ArrayList<Chemin>();
		for (ArrayList<Chemin> lst : this.hashPosPrises.values())
			liste.addAll(lst);

		return liste;
	}

	public int getNbPoint(char c) { return this.hashPosPrises.get(c).size(); }

	//---------------------------------------//
	//          Gestion de souris            //
	//---------------------------------------//
	
	public void deplacerX(int x)
	{
		this.x += x;
	}

	public void deplacerY(int y)
	{
		this.y += y;
	}

	public void setTailleX(int x) { this.tailleX = x; mettreAJourToutesLesLiaisons(); }
	public void setTailleY(int y) { this.tailleY = y; mettreAJourToutesLesLiaisons(); }
	public void setX(int x) { this.x = x; mettreAJourToutesLesLiaisons(); }
	public void setY(int y) { this.y = y; mettreAJourToutesLesLiaisons(); }

	//---------------------------------------//
	//          Gestion de souris            //
	//---------------------------------------//
	
	/**
	 *	Ajoute le chemin a la hashmap pour la clé de la direction
	 *	@param c la direction en char
	 *	@param chemin le chemin
	 */
	public void addPos(char c, Chemin chemin) { this.hashPosPrises.get(c).add(chemin); }
	
	/**
	 *  supprime le dernier chemin a la hashmap pour la clé de direction
	 *	@param c la direction en char
	 */
	public void supPos(char c)
	{
		
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get(c);
		if (listeChemins.size() > 0)
			listeChemins.removeLast()
	}
	
	/**
	 *	Retourne si un point est dans le rectangle
	 *	@param autre un point
	 */
	public boolean possede(Point autre)
	{
		int autreX = autre.getX();
		int autreY = autre.getY();

		return autreX >= this.x && autreX <= this.x + this.tailleX &&
			   autreY >= this.y && autreY <= this.y + this.tailleY;
	}

	/**
	 *	Mets à jour un chemin autour d'un rectangle
	 *	@param zone direction 
	 */
	public void repartirPointsLiaison(char zone)
	{
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get( zone );
		int nbPoints = listeChemins.size();
		
		if ( nbPoints > 0 )
		{
			double espacement = 0;
			
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
			// Cas Vertical (Gauche / Droite)
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


	public void mettreAJourToutesLesLiaisons() 
	{
		this.repartirPointsLiaison('H');
		this.repartirPointsLiaison('B');
		this.repartirPointsLiaison('G');
		this.repartirPointsLiaison('D');
	}

	public void nettoyerLiaisons()
	{
		this.hashPosPrises.get('H').clear();
		this.hashPosPrises.get('B').clear();
		this.hashPosPrises.get('G').clear();
		this.hashPosPrises.get('D').clear();
	}

}
