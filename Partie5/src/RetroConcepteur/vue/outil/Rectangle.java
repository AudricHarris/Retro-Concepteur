package RetroConcepteur.vue.outil;

import java.awt.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Rectangle 
{
	private String id;
	private int x;
	private int y;
	private int tailleX;
	private int tailleY;
	private HashMap<Character,ArrayList<Arc>> hashPosPrises;

	public Rectangle ( int x, int y, int tailleX, int tailleY, String id )
	{
		this.x = x;
		this.y = y;

		this.tailleX = tailleX;
		this.tailleY = tailleY;
	
        this.hashPosPrises = new HashMap<Character,ArrayList<Arc>>();
        this.hashPosPrises.put('H', new ArrayList<Arc>());
        this.hashPosPrises.put('B', new ArrayList<Arc>());
        this.hashPosPrises.put('D', new ArrayList<Arc>());
        this.hashPosPrises.put('G', new ArrayList<Arc>());	
	}

	public int getCentreX()       { return this.x + this.tailleX/2; }
	public int getCentreY()       { return this.y + this.tailleY/2; }
	public int getTailleX()       { return this.tailleX; }
	public int getTailleY()       { return this.tailleY; }
	public int getX      ()       { return this.x;       }
	public int getY      ()       { return this.y;       }

	public void deplacerX (int x) { this.x += x;   }
	public void deplacerY (int y) { this.y += y;   }
	public void setTailleX(int x) { this.tailleX  = x;   }
	public void setTailleY(int y) { this.tailleY  = y;   }

	public void setX( int x ) { this.x = x; }
	public void setY( int y ) { this.y = y; }

	public void ajoutArc(char c, Arc arc)
	{
		this.hashPosPrises.get(c).add(arc); 
		this.repartirPointsLiaison(c);
	}


	public boolean  possede ( int autreX, int autreY )
	{
		return	autreX >= this.x && autreX <= this.x + this.tailleX &&
				autreY >= this.y && autreY <= this.y + this.tailleY;
	}

	public void repartirPointsLiaison(char zone)
	{
        ArrayList<Arc> listeArc = this.hashPosPrises.get( zone );
        int nbPoints = listeArc.size();
        if ( nbPoints > 0 )
        {
            double step = 0;
            
            if (zone == 'H' || zone == 'B') 
            {
                step = (double) this.tailleX / (nbPoints + 1);
                
                for (int i = 0; i < nbPoints; i++) 
                {
                    Arc arc = listeArc.get(i);
                    // Position = X départ + (i+1) * pas
                    int positionX = this.x + (int)(step * (i + 1));
                    
                    if (zone == 'H') 
                    {
						arc.setPoint(this.id, positionX, this.y);
                    } 
                    else 
                    {
						arc.setPoint(this.id, positionX, this.y + this.tailleY);
                    }
                }
            }
            else if (zone == 'G' || zone == 'D') 
            {
                step = (double) this.tailleY / (nbPoints + 1);

                for (int i = 0; i < nbPoints; i++) 
                {
                    Arc arc = listeArc.get(i);
                    int positionY = this.y + (int)(step * (i + 1));
                    
                    if (zone == 'G') 
                    {
						arc.setPoint(this.id, this.x, positionY);
                    } 
                    else 
                    {
						arc.setPoint(this.id, this.x + this.tailleX, positionY);
                    }
                }
            }
        }
    }

}