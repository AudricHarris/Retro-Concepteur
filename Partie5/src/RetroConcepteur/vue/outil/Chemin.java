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
	
	// NOUVEAUX ATTRIBUTS POUR GERER LE CHEVAUCHEMENT
	private int indexLiaison = 0;
	private int totalLiaisons = 1;
	
	public Chemin(Point depart, Point arrivee, String type, HashMap<Classe,Rectangle> hashMap, Classe classeDep, Classe classeArr)
	{
		this.depart = depart;
		this.arrivee = arrivee;
		this.type = type;
		this.parcours = new LinkedList<Point>();
		this.zoneArrivee = ' ';
		this.calculerChemin();
		this.classeDep = classeDep;
		this.classeArr = classeArr;
	}

	public void setIndexLiaison(int index, int total) 
	{
		this.indexLiaison = index;
		this.totalLiaisons = total;
		this.calculerChemin();
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
		
		// Calcul du sens dominant (Horizontal vs Vertical)
		boolean horizontalDominant;
		if (this.zoneArrivee == 'G' || this.zoneArrivee == 'D') 
			horizontalDominant = true;
		else if (this.zoneArrivee == 'H' || this.zoneArrivee == 'B') 
			horizontalDominant = false;
		else 
			horizontalDominant = Math.abs(dx) > Math.abs(dy);
		
		// Espace de 20 pixels entre chaque ligne parallèle
		int decalage = 0;
		if (this.totalLiaisons > 1) 
		{
			decalage = ((this.indexLiaison - (this.totalLiaisons - 1) / 2) * 20);
		}

		if (horizontalDominant)
		{
			// On décale le "xMilieu" pour que les traits verticaux ne se superposent pas
			int xMilieu = x1 + dx / 2 + decalage;
			this.parcours.add(new Point(xMilieu, y1));
			this.parcours.add(new Point(xMilieu, y2));
		}
		else
		{
			// On décale le "yMilieu" pour que les traits horizontaux ne se superposent pas
			int yMilieu = y1 + dy / 2 + decalage;
			this.parcours.add(new Point(x1, yMilieu));
			this.parcours.add(new Point(x2, yMilieu));
		}
		this.parcours.add(this.arrivee);
	}
	
	// ... (Le reste des méthodes getters/setters reste inchangé) ...
	
	public void recalculer(Point nouveauDepart, Point nouvelleArrivee)
	{
		this.depart = nouveauDepart;
		this.arrivee = nouvelleArrivee;
		this.calculerChemin();
	}
	
	public Point getDepart() { return this.depart; }
	public Point getArrivee() { return this.arrivee; }
	public Classe getClasseDep() { return this.classeDep; }
	public Classe getClasseArr() { return this.classeArr; }
	
	public LinkedList<Point> getParcours() 	{ return new LinkedList<Point>(this.parcours); }
	public String getType() 				{ return this.type; }
	public void setType(String type) 		{ this.type = type; }
	public char getZoneArrivee() 			{ return this.zoneArrivee; }
	public void setZoneArrivee(char zone) 	{ this.zoneArrivee = zone; }

	public void updatePoint(int x, int y)
	{
		double distDepart  = Math.pow(x - depart.getX(), 2) + Math.pow(y - depart.getY(), 2);
		double distArrivee = Math.pow(x - arrivee.getX(), 2) + Math.pow(y - arrivee.getY(), 2);

		if (distDepart < distArrivee) {
			this.depart.setX(x);
			this.depart.setY(y);
		} else {
			this.arrivee.setX(x);
			this.arrivee.setY(y);
		}
		this.calculerChemin();
	}

	public void setRectangleArrivee(Rectangle rect) { this.rectangleArrivee = rect; }
	public Rectangle getRectangleArrivee() { return this.rectangleArrivee; }
}