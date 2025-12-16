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
	
	public Chemin(Point depart, Point arrivee, String type, HashMap<Classe,Rectangle> hashMap, Classe dep, Classe arriv)
	{
		this.depart = depart;
		this.arrivee = arrivee;
		this.type = type;
		this.parcours = new LinkedList<Point>();
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
		
		if (Math.abs(dx) > Math.abs(dy))
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

	public void setPoint(int posX, int posY)
	{
		this.depart.setX(posX);
		this.depart.setY(posY);
	}
}
