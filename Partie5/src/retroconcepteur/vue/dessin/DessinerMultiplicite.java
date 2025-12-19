package retroconcepteur.vue.dessin;

import retroconcepteur.vue.outil.*;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 * Classe responsable du rendu graphique et de l'interaction avec les multiplicites des liaisons UML.
 *
 * Elle gere l'affichage du texte (cardinalites et roles) ainsi que la detection des clics souris
 * sur ces zones de texte pour permettre leur edition. Elle s'assure que le texte est positionne
 * intelligemment par rapport au trace du lien (horizontal ou vertical).
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


	public boolean estDepart() {return this.estDepart;}

	/*------------------------------------------*/
	/* METHODES PUBLIQUES                       */
	/*------------------------------------------*/

	/**
	 * Dessine les multiplicites et le nom du role  sur le diagramme.
	 *
	 * Cette methode calcule les positions en temps reel en fonction des points du chemin
	 * pour eviter les decalages visuels.
	 *
	 * @param graphique Le contexte graphique (Graphics2D) utilise pour le dessin.
	 * @param chemin L'objet representant le trace du lien (liste de points).
	 * @param multDepart La chaine de caracteres de la multiplicite de depart (ex: 0..1).
	 * @param multArrivee La chaine de caracteres de la multiplicite d'arrivee (ex: *).
	 * @param nomVariable Le nom du role cote arrivee.
	 */
	public void dessiner(Graphics2D graphique, Chemin chemin, String multDepart, String multArrivee, String nomVariable)
	{
		LinkedList<Point> points;
		FontMetrics       metriques; 
		Rectangle         zone;
		int               x, y;
		
		points    = chemin.getParcours();
		metriques = graphique.getFontMetrics();

		if (multDepart != null && !multDepart.isEmpty() && points.size() > 1)
		{
			zone = this.calculerZoneTexte(points.getFirst(), points.get(1), multDepart, metriques);
			
			x = (int) zone.getX();
			y = (int) (zone.getY() + metriques.getAscent());
			
			graphique.drawString(multDepart, x, y);
		}
		
		if (multArrivee != null && !multArrivee.isEmpty() && points.size() > 1)
		{
			zone = this.calculerZoneTexte(points.getLast(), points.get(points.size() - 2), multArrivee, metriques);
			
			x = (int) zone.getX();
			y = (int) (zone.getY() + metriques.getAscent());
			
			graphique.drawString(multArrivee, x, y);

			if (nomVariable != null && !nomVariable.isEmpty())
				this.dessinerRole(graphique, points.getLast(), points.get(points.size() - 2), nomVariable, x, y, metriques);
			
		}
	}

	/**
	 * Verifie si un clic souris a eu lieu a l'interieur d'une zone de texte de multiplicite.
	 *
	 * Si le clic est detecte, la methode met a jour l'attribut estDepart pour
	 * indiquer quel champ doit etre edite.
	 *
	 * @param pointClic Les coordonnees du clic souris.
	 * @param chemin Le chemin du lien (pour recalculer les zones).
	 * @param multDepart Le texte actuel de la multiplicite de depart.
	 * @param multArrivee Le texte actuel de la multiplicite d'arrivee.
	 * @param metriques Les metriques de la police actuelle (necessaire pour la taille des zones).
	 * @return True si le clic touche une multiplicite, False sinon.
	 */
	public boolean verifierClic(Point pointClic, Chemin chemin, String multDepart, String multArrivee, FontMetrics metriques)
	{
		LinkedList<Point> points;
		Rectangle         zoneDepart, zoneArrivee;
		
		points = chemin.getParcours();
		
		if (points.size() < 2) return false;

		// Verification Depart
		if (multDepart != null && !multDepart.isEmpty())
		{
			zoneDepart = this.calculerZoneTexte(points.getFirst(), points.get(1), multDepart, metriques);
			
			if (zoneDepart.possede(new Point(pointClic.getX(), pointClic.getY())))
			{
				this.estDepart = true;
				return true;
			}
		}

		// Verification Arrivee
		if (multArrivee != null && !multArrivee.isEmpty())
		{
			zoneArrivee = this.calculerZoneTexte(points.getLast(), points.get(points.size() - 2), multArrivee, metriques);
			
			if (zoneArrivee.possede(new Point(pointClic.getX(), pointClic.getY())))
			{
				this.estDepart = false;
				return true;
			}
		}
		
		return false;
	}

	/*------------------------------------------*/
	/* METHODES PRIVEES                         */
	/*------------------------------------------*/

	/**
	 * Calcule le rectangle englobant le texte de la multiplicite en fonction de l'orientation du trait.
	 *
	 * Logique de positionnement :
	 * - Si horizontal : Le texte est place en dessous du trait.
	 * - Si vertical   : Le texte est place a gauche ou a droite selon la direction.
	 *
	 * @param pAncre Le point d'ancrage.
	 * @param pDirection Le point suivant, donnant la direction du trait.
	 * @param texte Le texte a afficher.
	 * @param metriques Les metriques de police pour calculer la largeur/hauteur du texte.
	 * @return Un objet Rectangle representant la zone occupee par le texte.
	 */
	private Rectangle calculerZoneTexte(Point pAncre, Point pDirection, String texte, FontMetrics metriques)
	{
		int    distClasse, distTrait;
		int    largeurTexte, hauteurTexte;
		int    x, y;
		double yHaut;

		distClasse   = 10; 
		distTrait    = 5;  
		largeurTexte = metriques.stringWidth(texte);
		hauteurTexte = metriques.getHeight();
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
	 * Dessine le role a cote de la multiplicite d'arrivee.
	 *
	 * Positionne le role du cote oppose a la multiplicite
	 * et legerement decale pour ne pas chevaucher la ligne.
	 *
	 * @param graphique Le contexte graphique.
	 * @param pArr Le point d'arrivee (sur la classe).
	 * @param pPrec Le point precedent (avant l'arrivee).
	 * @param nom Le nom du role a afficher.
	 * @param xMult La position X de la multiplicite associee (pour reference).
	 * @param yMult La position Y de la multiplicite associee (pour reference).
	 * @param metriques Les metriques de police.
	 */
	private void dessinerRole(Graphics2D graphique, Point pArr, Point pPrec, String nom, int xMult, int yMult, FontMetrics metriques)
	{
		int largeurRole, xRole, yRole;
		int distClasseVert = 20;
		int distClasseHori = 40;
		int distTrait      = 5;

		largeurRole = metriques.stringWidth(nom);

		if (pArr.getY() == pPrec.getY()) 
		{
			yRole = pArr.getY() - distTrait; 
			
			if (pArr.getX() > pPrec.getX()) 
				xRole = pArr.getX() - distClasseHori - largeurRole;
			else 
				xRole = pArr.getX() + distClasseHori;
		}
		else 
		{
			xRole = pArr.getX() - distTrait - largeurRole; 

			if (pArr.getY() > pPrec.getY()) 
				yRole = yMult - distClasseVert;
			else
				yRole = yMult + distClasseVert;
		}

		graphique.drawString(nom, xRole, yRole);
	}
}