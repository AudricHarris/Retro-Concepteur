package RetroConcepteur.vue.outil;
import java.util.HashMap;
import java.util.LinkedList;

import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.vue.outil.*;

public class Chemin
{
	private Point depart;
	private Point arrivee;
	private LinkedList<Point> parcours;
	private String type;

	private Classe classeDep;
	private Classe classeArr;
	private char zoneArrivee;
	private Rectangle rectangleArrivee;
	
	public Chemin(Point depart, Point arrivee, String type, HashMap<Classe,Rectangle> hashMap, Classe classeDep, Classe classeArr)
	{
		this.depart = depart;
		this.arrivee = arrivee;
		this.type = type;
		this.parcours = new LinkedList<Point>();
		this.calculerChemin();
		this.classeDep = classeDep;
		this.classeArr = classeArr;
		this.zoneArrivee = ' ';
	}

	public void updateChemin()
    {
        this.calculerChemin();
    }
	
	private void calculerChemin()
	{
		this.parcours.clear();
		this.parcours.add(this.depart);
		int x1 = this.depart.getX();
		int y1 = this.depart.getY();
		int x2 = this.arrivee.getX();
		int y2 = this.arrivee.getY();
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		// if (Math.abs(dx) > Math.abs(dy))
		// {
		// 	int xMilieu = x1 + dx / 2;
		// 	this.parcours.add(new Point(xMilieu, y1));
		// 	this.parcours.add(new Point(xMilieu, y2));
		// }
		// else
		// {
		// 	int yMilieu = y1 + dy / 2;
		// 	this.parcours.add(new Point(x1, yMilieu));
		// 	this.parcours.add(new Point(x2, yMilieu));
		// }
		// this.parcours.add(this.arrivee);

		// MODIFICATION : On privilégie la zone d'arrivée pour décider du sens du tracé
        // Si on arrive sur les côtés (Gauche/Droite), on veut un tracé à dominante Horizontale (Coupure en X)
        // Sinon (Haut/Bas), on veut un tracé à dominante Verticale (Coupure en Y)
        boolean horizontalDominant;

        if (this.zoneArrivee == 'G' || this.zoneArrivee == 'D') 
            horizontalDominant = true;
        else if (this.zoneArrivee == 'H' || this.zoneArrivee == 'B') 
            horizontalDominant = false;
        else 
            // Fallback sur l'ancienne logique si la zone n'est pas définie
            horizontalDominant = Math.abs(dx) > Math.abs(dy);
        

		if (horizontalDominant)
		{
			int xMilieu = x1 + dx / 2;
			this.parcours.add(new Point(xMilieu, y1));
			this.parcours.add(new Point(xMilieu, y2));
		}
		else
		{
			int yMilieu = y1 + dy / 2;
			this.parcours.add(new Point(x1, yMilieu));
			this.parcours.add(new Point(x2, yMilieu));
		}
		this.parcours.add(this.arrivee);
	}

	
	
	public void recalculer(Point nouveauDepart, Point nouvelleArrivee)
	{
		this.depart = nouveauDepart;
		this.arrivee = nouvelleArrivee;
		this.calculerChemin();
	}
	
	public Point getDepart()
	{
		return this.depart;
	}
	
	public Point getArrivee()
	{
		return this.arrivee;
	}

	public Classe getClasseDep() { return this.classeDep; }
	public Classe getClasseArr() { return this.classeArr; }
	
	public LinkedList<Point> getParcours()
	{
		return new LinkedList<Point>(this.parcours);
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

	public char getZoneArrivee()
	{
		return this.zoneArrivee;
	}

	public void setZoneArrivee(char zone)
	{
		this.zoneArrivee = zone;
	}

	public void updatePoint(int x, int y)
	{
		
		double distDepart  = Math.pow(x - depart.getX(), 2) + Math.pow(y - depart.getY(), 2);
		double distArrivee = Math.pow(x - arrivee.getX(), 2) + Math.pow(y - arrivee.getY(), 2);

		if (distDepart < distArrivee) 
		{
			this.depart.setX(x);
			this.depart.setY(y);
		} 
		else 
		{
			this.arrivee.setX(x);
			this.arrivee.setY(y);
		}
		
		this.calculerChemin();
	}

	public void setRectangleArrivee(Rectangle rect)
	{
	    this.rectangleArrivee = rect;
	}

	public Rectangle getRectangleArrivee()
	{
	    return this.rectangleArrivee;
	}
}
