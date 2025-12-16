package RetroConcepteur.vue.outil;

import java.awt.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import RetroConcepteur.vue.outil.*;

public class Rectangle implements Serializable
{
	private int x;
	private int y;
	private int tailleX;
	private int tailleY;
	private HashMap<Character, ArrayList<Chemin>> hashPosPrises;

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

	public int getCentreX()
	{
		return this.x + this.tailleX / 2;
	}

	public int getCentreY()
	{
		return this.y + this.tailleY / 2;
	}

	public int getTailleX()
	{
		return this.tailleX;
	}

	public int getTailleY()
	{
		return this.tailleY;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public void deplacerX(int x)
	{
		this.x += x;
	}

	public void deplacerY(int y)
	{
		this.y += y;
	}

	public void setTailleX(int x)
	{
		this.tailleX = x;
	}

	public void setTailleY(int y)
	{
		this.tailleY = y;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void addPos(char c, Chemin chemin)
	{
		this.hashPosPrises.get(c).add(chemin);
	}

	public void supPos(char c)
	{
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get(c);
		if (listeChemins.size() > 0)
		{
			listeChemins.remove(listeChemins.size() - 1);
		}
	}

	public int getNbPoint(char c)
	{
		return this.hashPosPrises.get(c).size();
	}

	public boolean possede(Point autre)
	{
		int autreX = autre.getX();
		int autreY = autre.getY();

		return autreX >= this.x && autreX <= this.x + this.tailleX &&
			   autreY >= this.y && autreY <= this.y + this.tailleY;
	}

	public void repartirPointsLiaison(char zone)
	{
		ArrayList<Chemin> listeChemins = this.hashPosPrises.get( zone );
		int nbPoints = listeChemins.size();
		if ( nbPoints > 0 )
		{
			double step = 0;
			
			if (zone == 'H' || zone == 'B') 
			{
				step = (double) this.tailleX / (nbPoints + 1);
				
				for (int i = 0; i < nbPoints; i++) 
				{
					Chemin chemin = listeChemins.get(i);
					// Position = X départ + (i+1) * pas
					int positionX = this.x + (int)(step * (i + 1));
					
					if (zone == 'H') 
					{
						chemin.setPoint(positionX, this.y);
					} 
					else 
					{
						chemin.setPoint(positionX, this.y + this.tailleY);
					}
				}
			}
			else if (zone == 'G' || zone == 'D') 
			{
				step = (double) this.tailleY / (nbPoints + 1);

				for (int i = 0; i < nbPoints; i++) 
				{
					Chemin chemin = listeChemins.get(i);
					int positionY = this.y + (int)(step * (i + 1));
					
					if (zone == 'G') 
					{
						chemin.setPoint(this.x, positionY);
					} 
					else 
					{
						chemin.setPoint(this.x + this.tailleX, positionY);
					}
				}
			}
		}
	}

}
