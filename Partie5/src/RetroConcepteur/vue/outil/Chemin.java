package RetroConcepteur.vue.outil;

// Package util
import java.util.HashMap;
import java.util.LinkedList;

// Nos packetage
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.vue.outil.*;

/**
 * Classe responsable du calcul des chemin
 *
 * @author [Equipe 9]
 * @version 1.0
 */
public class Chemin
{
	private int    indexLiaison  = 0;
	private int    totalLiaisons = 1;
	private char   zoneArrivee;
	private String type;

	// Attribut avec nos classes
	private Point     depart;
	private Point     arrivee;
	private Classe    classeDep;
	private Classe    classeArr;
	private Rectangle rectangleArrivee;
	
	private LinkedList<Point>          parcours;
	private HashMap<Classe, Rectangle> obstacle;
	
	/**
	 *	Creer une instance de chemin
	 *	@param depart Point de depart
	 *	@param arrivee Point d'arrivee
	 *	@param type le type du chemin
	 *	@param hashMap la liste des obstacles et autres methode
	 *	@param classeDep la classe de depart pour partir
	 *	@param classeArr la classe d'arrive du chemin
	 */
	public Chemin(Point depart, Point arrivee, String type, 
		          HashMap<Classe,Rectangle> hashMap, Classe classeDep, 
		          Classe classeArr)
	{
		this.depart      = depart;
		this.arrivee     = arrivee;
		this.type        = type;
		this.parcours    = new LinkedList<Point>();
		this.zoneArrivee = ' ';
		this.obstacle    = hashMap;
		this.classeDep   = classeDep;
		this.classeArr   = classeArr;

		if (hashMap != null) this.rectangleArrivee = hashMap.get(classeArr);

		this.calculerChemin();
	}
	

	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public Point getDepart () { return this.depart;  }
	public Point getArrivee() { return this.arrivee; }

	public Classe getClasseDep() { return this.classeDep; }
	public Classe getClasseArr() { return this.classeArr; }
	
	public String getType       () { return this.type;        }
	public char   getZoneArrivee() { return this.zoneArrivee; }
	
	public Rectangle getRectangleArrivee() { return this.rectangleArrivee;                  }
	public LinkedList<Point> getParcours() { return new LinkedList<Point>(this.parcours); }


	//---------------------------------------//
	//              Setters                  //
	//---------------------------------------//
	
	public void setZoneArrivee(char zone) { this.zoneArrivee = zone; }
	
	public void setIndexLiaison(int index, int total) 
	{
		this.indexLiaison = index;
		this.totalLiaisons = total;
		this.calculerChemin();
	}

	public void setType            (String    type) { this.type = type;             }
	public void setRectangleArrivee(Rectangle rect) { this.rectangleArrivee = rect; }
	
	public void updatePoint(Rectangle rectOrigine, int x, int y)
	{
		if (this.rectangleArrivee != null && rectOrigine == this.rectangleArrivee) 
		{
			this.arrivee.setX(x);
			this.arrivee.setY(y);
		} 
		else 
		{
			this.depart.setX(x);
			this.depart.setY(y);
		}
		this.calculerChemin();
	}


	//---------------------------------------//
	//      Methode d'instance               //
	//---------------------------------------//
	
	/**
	 *	Calcul le chemin (On ajoute 2 points entre arrivee & depart)
	 *	On deplace ces deux point pour avoir soi la posX ou PosY de leur point respectif
	 */
	private void calculerChemin()
	{
		this.parcours.clear();
		this.parcours.add(this.depart);

		int x1 = this.depart.getX();
		int y1 = this.depart.getY();
		int x2 = this.arrivee.getX();
		int y2 = this.arrivee.getY();
		
		// Distance X & Y
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		boolean horizontalDominant;

		if (this.zoneArrivee == 'G' || this.zoneArrivee == 'D')
			horizontalDominant = true;
		else
			if (this.zoneArrivee == 'H' || this.zoneArrivee == 'B') 
				horizontalDominant = false;
			else 
				horizontalDominant = Math.abs(dx) > Math.abs(dy);
		
		int decalage = 0;
		if (this.totalLiaisons > 1) 
		{
			int numeroMilieu = (this.totalLiaisons - 1) / 2;
			int placesDeDifference = this.indexLiaison - numeroMilieu;
			decalage = placesDeDifference * 20;
		}

		if (horizontalDominant)
		{
			int xMilieu = x1 + dx / 2 + decalage;

			this.parcours.add(new Point(xMilieu, y1));
			this.parcours.add(new Point(xMilieu, y2));
		}
		else
		{
			
			int yMilieu = y1 + dy / 2 + decalage;

			this.parcours.add(new Point(x1, yMilieu));
			this.parcours.add(new Point(x2, yMilieu));
		}

		this.parcours.add(this.arrivee);
	}
	
	/**
	 * Recalcul le chemin apartir de nouveau point
	 * Definit en parametre
	 * @param nouveauDepart point de depart
	 * @param nouvelleArrivee point d'arrivee
	 */
	public void recalculer(Point nouveauDepart, Point nouvelleArrivee)
	{
		this.depart  = nouveauDepart;
		this.arrivee = nouvelleArrivee;
		this.calculerChemin();
	}
	


	

}
