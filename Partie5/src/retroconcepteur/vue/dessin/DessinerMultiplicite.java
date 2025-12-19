package retroconcepteur.vue.dessin;

import retroconcepteur.vue.outil.*;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * Classe responsable du rendu graphique et de l'interaction avec les multiplicités des liaisons UML.
 * Elle gère l'affichage du texte (cardinalités et rôles) ainsi que la détection des clics pour l'édition.
 *
 * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost]
 * @version 1.0
 */
public class DessinerMultiplicite
{
	private boolean estDepart = false;

	/*---------------------------------------*/
	/* ACCESSEURS                            */
	/*---------------------------------------*/

	/**
	 * Indique si la derniere interaction concernait la multiplicité de depart (source).
	 *
	 * @return True si c'est le départ, False si c'est l'arrivée.
	 */
	
	public boolean estDepart()	{return this.estDepart;}

	/*------------------------------------------*/
	/* MÉTHODES PUBLIQUES À MODIFIER            */
	/*------------------------------------------*/

	/**
	 * Dessine les multiplicités et le rôle sur le diagramme.
	 * Utilise la même logique de positionnement que la détection de clic pour éviter les décalages.
	 */
	public void dessiner(Graphics2D g2, Chemin chemin, String multDepart, String multArrivee, String nomVariable)
	{
		LinkedList<Point> points;
		FontMetrics       fm;
		Rectangle         zone;
		int               x, y;
		
		points = chemin.getParcours();
		fm     = g2.getFontMetrics();

		// Dessin de la Multiplicité de depart
		if (multDepart != null && !multDepart.isEmpty() && points.size() > 1)
		{
			zone = this.calculerZoneTexte(points.getFirst(), points.get(1), multDepart, fm);
			
			x = (int) zone.getX();
			y = (int) (zone.getY() + fm.getAscent());
			
			g2.drawString(multDepart, x, y);
		}
		
		// Dessin de la Multiplicité d'arrive
		if (multArrivee != null && !multArrivee.isEmpty() && points.size() > 1)
		{
			zone = this.calculerZoneTexte(points.getLast(), points.get(points.size() - 2), multArrivee, fm);
			
			x = (int) zone.getX();
			y = (int) (zone.getY() + fm.getAscent());
			
			g2.drawString(multArrivee, x, y);

			// Dessin du Rôle 
			if (nomVariable != null && !nomVariable.isEmpty())
				this.dessinerRole(g2, points.getLast(), points.get(points.size() - 2), nomVariable, x, y, fm);
			
		}
	}

	/**
	 * Vérifie si un clic souris est à l'intérieur d'une zone de multiplicité.
	 * Met à jour le flag estDepart si vrai.
	 */
	public boolean verifierClic(Point pointClic, Chemin chemin, String multDepart, String multArrivee, FontMetrics fm)
	{
		LinkedList<Point> points;
		Rectangle       zoneDepart, zoneArrivee;
		
		points = chemin.getParcours();
		
		if (points.size() < 2) return false;

		if (multDepart != null && !multDepart.isEmpty())
		{

			zoneDepart = this.calculerZoneTexte(points.getFirst(), points.get(1), multDepart, fm);
			if (zoneDepart.possede(new Point(pointClic.getX(), pointClic.getY())))
			{
				this.estDepart = true;
				return true;
			}
		}

		// Vérification Arrivée
		if (multArrivee != null && !multArrivee.isEmpty())
		{
			zoneArrivee = this.calculerZoneTexte(points.getLast(), points.get(points.size() - 2), multArrivee, fm);
			if (zoneArrivee.possede(new Point(pointClic.getX(), pointClic.getY())))
			{
				this.estDepart = false;
				return true;
			}
		}
		
		return false;
	}

	/*------------------------------------------*/
	/* MÉTHODES PRIVÉES                         */
	/*------------------------------------------*/

	/**
	 * Calcule le rectangle englobant le texte de la multiplicité.
	 * Modifié : Place le texte EN DESSOUS du trait pour les liaisons horizontales.
	 */
	private Rectangle calculerZoneTexte(Point pAncre, Point pDirection, String texte, FontMetrics fm)
	{
		int    distClasse, distTrait;
		int    largeurTexte, hauteurTexte;
		int    x, y;
		double yHaut;

		distClasse   = 10;
		distTrait    = 5;
		largeurTexte = fm.stringWidth(texte);
		hauteurTexte = fm.getHeight();
		x            = pAncre.getX();
		y            = pAncre.getY();

		if (pAncre.getY() == pDirection.getY())
		{
			
			yHaut = y + distTrait; 

			if (pDirection.getX() > pAncre.getX())
				x += distClasse;
			else
				x -= (distClasse + largeurTexte);
		}
		else
		{
			x += distTrait; 

			if (pDirection.getY() > pAncre.getY())
				yHaut = y + distClasse;
			else
				yHaut = y - distClasse - hauteurTexte;
		}

		return new Rectangle(x, (int)yHaut, largeurTexte, hauteurTexte);
	}

	/**
	 * Dessine le rôle (nom de variable) à côté de la multiplicité d'arrivée.
	 * Modifié : Positionne le rôle AU-DESSUS du trait (opposé à la multiplicité) et plus proche de la classe.
	 */
	private void dessinerRole(Graphics2D g2, Point pArr, Point pPrec, String nom, int xMult, int yMult, FontMetrics fm)
	{
		int largeurRole, xRole, yRole;
		int distClasseVert = 20;
		int distClasseHori = 40;
		int distTrait  = 5;

		largeurRole = fm.stringWidth(nom);

		if (pArr.getY() == pPrec.getY()) 
		{
			yRole = pArr.getY() - distTrait; 
			
			if (pArr.getX() > pPrec.getX()) 
				xRole = pArr.getX() - distClasseHori - largeurRole  ;
			
			else 
				xRole = pArr.getX() + distClasseHori ;
			
		}
		else 
		{
			xRole = pArr.getX() - distTrait - largeurRole; 

			if (pArr.getY() > pPrec.getY()) 
				yRole = yMult - distClasseVert;
			else
				yRole = yMult + distClasseVert ;
		}

		g2.drawString(nom, xRole, yRole);
	}
}