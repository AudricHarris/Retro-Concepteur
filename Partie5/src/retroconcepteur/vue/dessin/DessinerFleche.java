package retroconcepteur.vue.dessin;

import retroconcepteur.vue.outil.Chemin;
import retroconcepteur.vue.outil.Point;
import retroconcepteur.vue.outil.Rectangle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.LinkedList;

/**
 * Classe utilitaire responsable du trace des liaisons (fleches) entre les classes UML.
 * Elle gere le dessin des lignes brisees (chemins) et des differentes pointes de fleches
 * (association, heritage, implementation) selon la norme UML.
 *
 * @author [Equipe 9]
 * @version 2.0
 */
public class DessinerFleche
{

	/*---------------------------------------*/
	/* MeTHODES PUBLIQUES                    */
	/*---------------------------------------*/

	/**
	 * Dessine une liaison complete (ligne + pointe) sur le diagramme.
	 * Gere les differents styles de traits (pointilles pour l'implementation)
	 * et les types de pointes (triangle vide, fleche ouverte, etc.).
	 *
	 * @param g2     Le contexte graphique Java AWT.
	 * @param chemin L'objet chemin contenant les points de passage et le type de liaison.
	 */
	public void dessinerLiaison(Graphics2D g2, Chemin chemin)
	{
		Stroke            strokeOriginal;
		String            type;
		LinkedList<Point> parcours;
		Point             centre;
		Point             avantCentre;
		Rectangle         rectCible;
		Point             intersection;
		double            angle;
		int               tailleFleche;
		float[]           dash;

		strokeOriginal = g2.getStroke();
		type           = chemin.getType();
		parcours       = chemin.getParcours();

		if (type.equals("Implementation"))
		{
			dash = new float[]{10.0f, 5.0f};
			g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
										BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		}

		g2.setColor(Color.BLACK);
		this.dessinerParcours(g2, parcours);

		g2.setStroke(strokeOriginal);

		if (parcours.size() >= 2 && !type.equals("BIDIRECTIONNELLE"))
		{
			centre       = parcours.get(parcours.size() - 1);
			avantCentre  = parcours.get(parcours.size() - 2);
			rectCible    = chemin.getRectangleArrivee();
			
			intersection = calculerIntersection(avantCentre, centre, rectCible);
			
			if (intersection != null)
			{
				angle        = calculerAnglePerpendiculaire(intersection, rectCible);
				tailleFleche = 12;

				switch (type)
				{
					case "UNI":
						this.dessinerPointeOuverte(g2, intersection.getX(), intersection.getY(), 
												angle, tailleFleche);
						break;

					case "Generalisation":
					case "Implementation":
						this.dessinerTriangle(g2, intersection.getX(), intersection.getY(), 
											angle, tailleFleche);
						break;
				}
			}
		}
	}

	/*---------------------------------------*/
	/* MeTHODES PRIVeES (CALCULS)            */
	/*---------------------------------------*/

	/**
	 * Calcule l'angle (en radians) de la fleche par rapport au bord du rectangle touche.
	 * Cela permet d'orienter la pointe correctement (vers le haut, le bas, la gauche ou la droite).
     * @param intersection Le point d'intersection entre la ligne et le rectangle.
     * @param rect Le rectangle de la classe cible.
     * @return L'angle en radians de la fleche perpendiculaire au bord touche.
	 */
	private double calculerAnglePerpendiculaire(Point intersection, Rectangle rect)
	{
		int    x, y;
		int    rectX, rectY, largeurRect, hauteurRect;
		int    centreX, centreY;
		int    tolerance;

		if (rect == null) return 0;
		
		x          = intersection.getX();
		y          = intersection.getY();
		rectX      = rect.getX();
		rectY      = rect.getY();
		largeurRect  = rect.getTailleX();
		hauteurRect = rect.getTailleY();
		tolerance  = 2; 
		
		if      (Math.abs(x - rectX) <= tolerance)                  return 0;        
		else if (Math.abs(x - (rectX + largeurRect)) <= tolerance)  return Math.PI;     
		else if (Math.abs(y - rectY) <= tolerance)                  return Math.PI / 2;  
		else if (Math.abs(y - (rectY + hauteurRect)) <= tolerance)  return -Math.PI / 2; 
		
		centreX = rectX + largeurRect / 2;
		centreY = rectY + hauteurRect / 2;
		return Math.atan2(centreY - y, centreX - x);
	}

	/**
	 * Calcule le point exact où le segment [p1, p2] coupe le bord du rectangle.
	 * C'est essentiel pour que la fleche s'arrete pile au bord de la boite.
     * @param p1   Le point de depart du segment (avant la boite).
     * @param p2   Le point d'arrivee du segment (centre de la boite).
     * @param rect Le rectangle de la classe cible.
     * @return Le point d'intersection entre le segment et le bord du rectangle.
	 */
	private Point calculerIntersection(Point p1, Point p2, Rectangle rect)
	{
		int    x1, y1, x2, y2;
		int    rectX, rectY, rectWidth, rectHeight;
		double dx, dy, t, minT;
		Point  intersection;
		int    x, y;

		if (rect == null) return p2;
		
		x1 = p1.getX(); y1 = p1.getY();
		x2 = p2.getX(); y2 = p2.getY();
		
		rectX      = rect.getX();
		rectY      = rect.getY();
		rectWidth  = rect.getTailleX();
		rectHeight = rect.getTailleY();
		
		dx = x2 - x1;
		dy = y2 - y1;
		
		// Si les points sont confondus
		if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001) 
			return new Point(x2, y2);
		
		minT         = Double.MAX_VALUE;
		intersection = null;
		
		// Test bord gauche
		if (Math.abs(dx) > 0.001)
		{
			t = (rectX - x1) / dx;
			if (t >= 0 && t <= 1)
			{
				y = (int)(y1 + t * dy);
				if (y >= rectY && y <= rectY + rectHeight && t < minT)
				{
					minT         = t;
					intersection = new Point(rectX, y);
				}
			}
		}
		
		// Test bord droit
		if (Math.abs(dx) > 0.001)
		{
			t = (rectX + rectWidth - x1) / dx;
			if (t >= 0 && t <= 1)
			{
				y = (int)(y1 + t * dy);
				if (y >= rectY && y <= rectY + rectHeight && t < minT)
				{
					minT         = t;
					intersection = new Point(rectX + rectWidth, y);
				}
			}
		}
		
		//Test bord haut
		if (Math.abs(dy) > 0.001)
		{
			t = (rectY - y1) / dy;
			if (t >= 0 && t <= 1)
			{
				x = (int)(x1 + t * dx);
				if (x >= rectX && x <= rectX + rectWidth && t < minT)
				{
					minT         = t;
					intersection = new Point(x, rectY);
				}
			}
		}
		
		//Test bord bas
		if (Math.abs(dy) > 0.001)
		{
			t = (rectY + rectHeight - y1) / dy;
			if (t >= 0 && t <= 1)
			{
				x = (int)(x1 + t * dx);
				if (x >= rectX && x <= rectX + rectWidth && t < minT)
				{
					minT         = t;
					intersection = new Point(x, rectY + rectHeight);
				}
			}
		}
		
		return intersection != null ? intersection : new Point(x2, y2);
	}

	/*---------------------------------------*/
	/* MeTHODES PRIVeES (DESSIN)             */
	/*---------------------------------------*/

	/**
	 * Trace les segments de ligne reliant les points du chemin.
     * @param g2      Le contexte graphique Java AWT.
     * @param parcours La liste des points formant le chemin.
	 */
	private void dessinerParcours(Graphics2D g2, LinkedList<Point> parcours)
	{       
		for (int i = 0; i < parcours.size() - 1; i++)
		{
			Point p1 = parcours.get(i);
			Point p2 = parcours.get(i + 1);
			g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}

	/**
	 * Dessine une fleche simple (deux traits) pour les associations unidirectionnelles.
     * @param g2     Le contexte graphique Java AWT.
     * @param x      La coordonnee x du point de la fleche (la pointe ).
     * @param y      La coordonnee y du point de la fleche (la pointe).
     * @param angle  L'angle en radians de la fleche.
     * @param taille La taille de la fleche.
	 */
	private void dessinerPointeOuverte(Graphics2D g2, int x, int y, double angle, int taille)
	{
		int x1, y1, x2, y2;

		x1 = (int)(x - taille * Math.cos(angle - Math.PI / 6));
		y1 = (int)(y - taille * Math.sin(angle - Math.PI / 6));
		x2 = (int)(x - taille * Math.cos(angle + Math.PI / 6));
		y2 = (int)(y - taille * Math.sin(angle + Math.PI / 6));

		g2.drawLine(x, y, x1, y1);
		g2.drawLine(x, y, x2, y2);
	}

	/**
	 * Dessine un triangle vide pour l'heritage et l'implementation.
     * @param g2     Le contexte graphique Java AWT.
     * @param x      La coordonnee x du point de la fleche (pointe).
     * @param y      La coordonnee y du point de la fleche (pointe).
     * @param angle  L'angle en radians de la fleche.
     * @param taille La taille de la fleche.
	 */
	private void dessinerTriangle(Graphics2D g2, int x, int y, double angle, int taille)
	{
		int[] xPoints;
		int[] yPoints;
		Color oldColor;

		xPoints = new int[3];
		yPoints = new int[3];

		xPoints[0] = x;
		yPoints[0] = y;

		xPoints[1] = (int)(x - taille * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int)(y - taille * Math.sin(angle - Math.PI / 6));

		xPoints[2] = (int)(x - taille * Math.cos(angle + Math.PI / 6));
		yPoints[2] = (int)(y - taille * Math.sin(angle + Math.PI / 6));

		oldColor = g2.getColor();
		g2.setColor(Color.WHITE);
		g2.fillPolygon(xPoints, yPoints, 3);

		g2.setColor(oldColor);
		g2.drawPolygon(xPoints, yPoints, 3);
	}
}