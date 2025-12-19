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
	
	public boolean estDepart()
	{
		return this.estDepart;
	}

	/*---------------------------------------*/
	/* AUTRES MÉTHODES PUBLIQUES             */
	/*---------------------------------------*/

	/**
	 * Dessine les textes de multiplicites et le nom de variable sur le contexte graphique.
	 *
	 * @param g2            Le contexte graphique Java AWT.
	 * @param chemin        L'objet chemin contenant les points de la liaison.
	 * @param multDepart    Le texte de la multiplicite cote source.
	 * @param multArrivee   Le texte de la multiplicite cote cible.
	 * @param nomVariable   Le nom du rôle côté cible.
	 */
	public void dessiner(Graphics2D g2, Chemin chemin, String multDepart, String multArrivee, String nomVariable)
	{
		LinkedList<Point> points;
		FontMetrics       metriquesPolice;
		double            ascent;
		int               distanceClasse;
		int               distanceTrait;
		int               distanceRole;
		int               x, y;
		
		points          = chemin.getParcours();
		metriquesPolice = g2.getFontMetrics();
		ascent          = metriquesPolice.getAscent();
		
		distanceClasse  = 10;
		distanceTrait   = 5;
		distanceRole    = 5;

		if (multDepart != null && !multDepart.isEmpty())
		{
			Point pDepart  = points.getFirst();
			Point pSuivant = points.get(1);
			
			x = pDepart.getX();
			y = pDepart.getY();

			// Si le segment est horizontal
			if (pDepart.getY() == pSuivant.getY())
			{
				y += (int) ascent; 
				if (pSuivant.getX() > pDepart.getX())
					x += distanceClasse;
				else
					x -= (distanceClasse + 15);
			}
			// Si le segment est vertical
			else
			{
				x += distanceTrait;
				if (pSuivant.getY() > pDepart.getY())
					y += (distanceClasse + 10);
				else 
					y -= distanceClasse;
			}
			g2.drawString(multDepart, x, y);
		}
		
		if (multArrivee != null && !multArrivee.isEmpty())
		{
			Point pArrivee    = points.getLast();
			Point pPrecedent  = points.get(points.size() - 2);
			int   largeurRole = metriquesPolice.stringWidth(nomVariable == null ? "" : nomVariable);
			int   xRole       = 0;
			int   yRole       = 0;

			x = pArrivee.getX();
			y = pArrivee.getY();

			// Si le segment est horizontal
			if (pArrivee.getY() == pPrecedent.getY())
			{
				int yMult = y + (int) ascent;
				
				if (pArrivee.getX() > pPrecedent.getX())
				{
					// Flèche vient de la gauche 
					x    -= (distanceClasse + 15);
					xRole = x - largeurRole;
					yRole = y - distanceRole;
				}
				else
				{
					// Flèche vient de la droite 
					x    += distanceClasse;
					xRole = x + (largeurRole / 2);
					yRole = y - distanceRole;
				}
				
				g2.drawString(multArrivee, x, yMult);
			}
			// Si le segment est vertical
			else 
			{
				int xMult, yMult;
				
				if (pArrivee.getY() > pPrecedent.getY())
				{
					// Flèche vient du haut 
					xMult = x + distanceTrait;
					yMult = y - distanceClasse;
					g2.drawString(multArrivee, xMult, yMult);
					
					// Rôle à gauche du trait
					xRole = x - largeurRole - distanceTrait;
					yRole = y - (distanceClasse * 3);
				}
				else 
				{
					// Flèche vient du bas 
					xMult = x + distanceTrait;
					yMult = y + (distanceClasse + 10);
					g2.drawString(multArrivee, xMult, yMult);
					
					// Rôle a gauche du trait
					xRole = x - largeurRole - distanceTrait;
					yRole = y + (distanceClasse + 10) * 2;
				}
			}
			
			if (nomVariable != null && !nomVariable.isEmpty())
				g2.drawString(nomVariable, xRole, yRole);
		}
	}

	/**
	 * Vérifie si un clic de souris correspond à la zone d'une des multiplicités.
	 * Met à jour l'état interne (estDepart) si un clic est détecté.
	 *
	 * @param pointClic     Les coordonnées du clic souris.
	 * @param chemin        Le chemin de la liaison.
	 * @param multDepart    Le texte de la multiplicité de départ.
	 * @param multArrivee   Le texte de la multiplicité d'arrivée.
	 * @param fm            Les métriques de police pour calculer les zones de clic.
	 * @return True si le clic a touché une zone de texte, False sinon.
	 */
	public boolean verifierClic(Point pointClic, Chemin chemin, String multDepart, String multArrivee, FontMetrics fm)
	{
		LinkedList<Point> points;
		double            ascent;
		double            largeur, hauteur;
		int               distanceClasse;
		int               distanceTrait;
		int               x, y;
		Rectangle2D       limites;
		
		points          = chemin.getParcours();
		ascent          = fm.getAscent();
		distanceClasse  = 10;
		distanceTrait   = 5;

		if (multDepart != null && !multDepart.isEmpty() && points.size() > 1)
		{
			Point pDepart  = points.getFirst();
			Point pSuivant = points.get(1);
			
			x = pDepart.getX();
			y = pDepart.getY();
			
			if (pDepart.getY() == pSuivant.getY())
			{
				y -= distanceTrait;
				if (pSuivant.getX() > pDepart.getX())
					x += distanceClasse;
				else
					x -= (distanceClasse + 15);
			} 
			else
			{
				x += distanceTrait;
				if (pSuivant.getY() > pDepart.getY())
					y += (distanceClasse + 10);
				else
					y -= distanceClasse;
			}

			largeur = fm.stringWidth(multDepart);
			hauteur = fm.getHeight();
			limites = new Rectangle2D.Double(x, y - ascent, largeur, hauteur);
			
			if (limites.contains(pointClic.getX(), pointClic.getY()))
			{
				this.estDepart = true;
				return true;
			}
		}

		if (multArrivee != null && !multArrivee.isEmpty() && points.size() > 1)
		{
			Point pArrivee   = points.getLast();
			Point pPrecedent = points.get(points.size() - 2);
			
			x = pArrivee.getX();
			y = pArrivee.getY();
			
			if (pArrivee.getY() == pPrecedent.getY())
			{
				y -= distanceTrait;
				if (pArrivee.getX() > pPrecedent.getX())
					x -= (distanceClasse + 15);
				else
					x += distanceClasse;
			} 
			else
			{
				x += distanceTrait;
				if (pArrivee.getY() > pPrecedent.getY())
					y -= distanceClasse;
				else
					y += (distanceClasse + 10);
			}

			largeur = fm.stringWidth(multArrivee);
			hauteur = fm.getHeight();
			limites = new Rectangle2D.Double(x, y - ascent, largeur, hauteur);
			
			if (limites.contains(pointClic.getX(), pointClic.getY()))
			{
				this.estDepart = false;
				return true;
			}
		}
		
		return false;
	}
}