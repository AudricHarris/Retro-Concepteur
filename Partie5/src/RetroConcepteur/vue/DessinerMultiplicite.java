package RetroConcepteur.vue;
import RetroConcepteur.vue.outil.*;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * Classe responsable du dessin et de l'interaction avec les multiplicités des liaisons UML.
 * Permet le clic pour éditer les bornes via une popup.
 *
 * @author [Equipe 9]
 * @version 1.0
 */
public class DessinerMultiplicite
{
    private boolean isDepart = false;

	/**
     * Dessine les multiplicités pour un chemin.
     *
     * @param g2 Le contexte graphique.
     * @param chemin Le chemin de liaison.
     * @param multDepart Multiplicité de départ.
     * @param multArrivee Multiplicité d'arrivée.
     */
    public void dessiner(Graphics2D g2, Chemin chemin, String multDepart, String multArrivee, String nomVar)
	{
        LinkedList<Point> points = chemin.getParcours();
        int distClasse = 10;
        int distTrait = 5;
        int distRole = 5; 

        FontMetrics fm = g2.getFontMetrics();
        double ascent = fm.getAscent();

        if (!multDepart.isEmpty())
		{
            Point pDep = points.getFirst();
            Point pSvt = points.get(1);
            int x = pDep.getX();
            int y = pDep.getY();

            if (pDep.getY() == pSvt.getY())
			{
                y += (int)ascent; 
                if (pSvt.getX() > pDep.getX())
                    x += distClasse;
				else
                    x -= distClasse + 15;
            }
			else
			{
                x += distTrait;
                if (pSvt.getY() > pDep.getY())
                    y += (distClasse + 10);
				else 
                    y -= distClasse;
            }
            g2.drawString(multDepart, x, y);
        }
        
        if (!multArrivee.isEmpty())
		{
            Point pArr = points.getLast();
            Point pPrc = points.get(points.size() - 2);

            int x = pArr.getX();
            int y = pArr.getY();
            int xRole = 0;
            int yRole = 0;

            FontMetrics metrics = g2.getFontMetrics();
            int largeurRole = metrics.stringWidth(nomVar);

            // Chemin horizontal
            if (pArr.getY() == pPrc.getY())
			{
                int yMult = y + (int)ascent;
                
                if (pArr.getX() > pPrc.getX())
				{
                    // Flèche vient de la gauche
                    x -= distClasse + 15;
                    xRole = x - largeurRole;
                    yRole = y - distRole;
				}
				else
				{
                    // Flèche vient de la droite
                    x += distClasse;

                    xRole = x + largeurRole;
                    yRole = y - distRole;
				}
                
                g2.drawString(multArrivee, x, yMult);
            }
			// Chemin vertical
			else 
			{
                if (pArr.getY() > pPrc.getY())
				{
                    // Flèche vient du haut
                    int xMult = x + distTrait;
                    int yMult = y - distClasse;
                    g2.drawString(multArrivee, xMult, yMult);
                    
                    // Rôle à GAUCHE du trait
                    xRole = x - largeurRole - distTrait;
                    yRole = y - (distClasse *3);
				}
				else 
				{
                    // Flèche vient du bas

                    int xMult = x + distTrait;
                    int yMult = y + (distClasse + 10);
                    g2.drawString(multArrivee, xMult, yMult);
                    
                    // Rôle à GAUCHE du trait
                    xRole = x - largeurRole - distTrait;
                    yRole = y + ((distClasse + 10) *3);
				}
            }
            
            // Dessiner le rôle seulement s'il n'est pas vide
            if (nomVar != null && !nomVar.isEmpty())
			{
                g2.drawString(nomVar, xRole, yRole);
			}
        }
    }

	/**
     * Vérifie si le clic est sur une multiplicité.
     *
     * @param click Le point du clic.
     * @param chemin Le chemin.
     * @param multDepart Multiplicité de départ.
     * @param multArrivee Multiplicité d'arrivée.
     * @param fm Métriques de police.
     * @return True si clic sur une multiplicité.
     */
    public boolean checkClick(Point click, Chemin chemin, String multDepart, String multArrivee, FontMetrics fm)
	{
        LinkedList<Point> points = chemin.getParcours();
        int distClasse = 10;
        int distTrait = 5;
        double ascent = fm.getAscent();
        if (!multDepart.isEmpty() && points.size() > 1)
		{
            Point pDep = points.getFirst();
            Point pSvt = points.get(1);
            int x = pDep.getX();
            int y = pDep.getY();
            if (pDep.getY() == pSvt.getY())
			{
                y -= distTrait;
                if (pSvt.getX() > pDep.getX())
                    x += distClasse;
				else
                    x -= (distClasse + 15);
            } 
			else
			{
                x += distTrait;
                if (pSvt.getY() > pDep.getY())
                    y += (distClasse + 10);
				else
                    y -= distClasse;
            }

            double width = fm.stringWidth(multDepart);
            double height = fm.getHeight();
            Rectangle2D bounds = new Rectangle2D.Double(x, y - ascent, width, height);
            
			if (bounds.contains(click.getX(), click.getY()))
			{
                this.isDepart = true;
                return true;
            }
        }
        if (!multArrivee.isEmpty() && points.size() > 1)
		{
            Point pArr = points.getLast();
            Point pPrc = points.get(points.size() - 2);
            int x = pArr.getX();
            int y = pArr.getY();
            if (pArr.getY() == pPrc.getY())
			{
                y -= distTrait;
                if (pArr.getX() > pPrc.getX())
                    x -= (distClasse + 15);
				else
                    x += distClasse;
            } 
			else
			{
                x += distTrait;
                if (pArr.getY() > pPrc.getY())
                    y -= distClasse;
				else
                    y += (distClasse + 10);
            }

            double width = fm.stringWidth(multArrivee);
            double height = fm.getHeight();
            
			Rectangle2D bounds = new Rectangle2D.Double(x, y - ascent, width, height);
            
			if (bounds.contains(click.getX(), click.getY()))
			{
                this.isDepart = false;
                return true;
            }
        }
        return false;
    }

    
	/**
     * Indique si c'est la multiplicité de départ.
     *
     * @return True si départ.
     */
    public boolean isDepart()
	{
        return this.isDepart;
    }
}
