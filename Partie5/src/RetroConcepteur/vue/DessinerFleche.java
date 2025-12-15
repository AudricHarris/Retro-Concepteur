package RetroConcepteur.vue;

import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import RetroConcepteur.vue.outil.Arc;

public class DessinerFleche
{
	/**
	 * Dessine une liaison UML entre deux points.
	 * @param g2 Le contexte graphique
	 * @param x1 X départ (Centre classe 1)
	 * @param y1 Y départ (Centre classe 1)
	 * @param x2 X arrivée (Centre classe 2)
	 * @param y2 Y arrivée (Centre classe 2)
	 * @param type Le type : "ASSOCIATION", "BIDIRECTIONNELLE", "HERITAGE", "COMPOSITION", "DEPENDANCE"
	 */
	public  void dessinerLiaison(Graphics2D g2, int x1, int y1, int x2, int y2, String type) 
	{
		// 1. Sauvegarde du style original (trait plein)
		java.awt.Stroke strokeOriginal = g2.getStroke();
		
		// 2. Configuration du trait (Pointillé pour Dépendance, Plein pour le reste)
		if (type.equals("DEPENDANCE")) 
		{
			// Motif : 10px plein, 5px vide
			float[] dash = {10.0f, 5.0f}; 
			g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		}

		// 3. Dessiner la ligne principale
		g2.setColor(Color.BLACK);
		g2.drawLine(x1, y1, x2, y2);
		
		// On remet le trait plein pour dessiner la tête (les triangles/losanges ne sont pas pointillés)
		g2.setStroke(strokeOriginal);

		// 4. Calculs mathématiques pour l'angle de la ligne
		double angle = Math.atan2(y2 - y1, x2 - x1); 
		int tailleFleche = 12; 

		// 5. Dessin de la tête selon le type
		switch (type) {
			case "ASSOCIATION": // Flèche ouverte
			case "DEPENDANCE":  // Flèche ouverte aussi
				this.dessinerPointeOuverte(g2, x2, y2, angle, tailleFleche);
				break;
				
			case "HERITAGE": // Triangle blanc
				this.dessinerTriangle(g2, x2, y2, angle, tailleFleche);
				break;
				
			case "COMPOSITION": // Losange noir
				this.dessinerLosange(g2, x2, y2, angle, tailleFleche);
				break;
				
			case "BIDIRECTIONNELLE":
				// Rien à faire : juste le trait (déjà dessiné à l'étape 3)
				break;
		}
	}

	// --- SOUS-METHODES DE DESSIN ---

	private void dessinerPointeOuverte(Graphics2D g2, int x, int y, double angle, int taille) 
	{
		// On recule depuis la pointe selon l'angle + 30° et l'angle - 30°
		int x1 = (int) (x - taille * Math.cos(angle - Math.PI / 6));
		int y1 = (int) (y - taille * Math.sin(angle - Math.PI / 6));
		int x2 = (int) (x - taille * Math.cos(angle + Math.PI / 6));
		int y2 = (int) (y - taille * Math.sin(angle + Math.PI / 6));

		g2.drawLine(x, y, x1, y1);
		g2.drawLine(x, y, x2, y2);
	}

	private void dessinerTriangle(Graphics2D g2, int x, int y, double angle, int taille) 
	{
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];

		// Pointe (sur la cible)
		xPoints[0] = x;
		yPoints[0] = y;

		// Coin gauche arrière
		xPoints[1] = (int) (x - taille * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int) (y - taille * Math.sin(angle - Math.PI / 6));

		// Coin droit arrière
		xPoints[2] = (int) (x - taille * Math.cos(angle + Math.PI / 6));
		yPoints[2] = (int) (y - taille * Math.sin(angle + Math.PI / 6));

		// Remplir en blanc (pour cacher le trait dessous)
		Color oldColor = g2.getColor();
		g2.setColor(Color.WHITE);
		g2.fillPolygon(xPoints, yPoints, 3);
		
		// Contour noir
		g2.setColor(oldColor);
		g2.drawPolygon(xPoints, yPoints, 3);
	}

	private void dessinerLosange(Graphics2D g2, int x, int y, double angle, int taille) 
	{
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];

		// Pointe avant
		xPoints[0] = x;
		yPoints[0] = y;

		// Coin gauche
		xPoints[1] = (int) (x - taille * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int) (y - taille * Math.sin(angle - Math.PI / 6));

		// Pointe arrière (plus loin sur la ligne)
		xPoints[2] = (int) (x - (taille * 1.7) * Math.cos(angle)); 
		yPoints[2] = (int) (y - (taille * 1.7) * Math.sin(angle));

		// Coin droit
		xPoints[3] = (int) (x - taille * Math.cos(angle + Math.PI / 6));
		yPoints[3] = (int) (y - taille * Math.sin(angle + Math.PI / 6));

		// Remplir en NOIR pour la composition
		Color oldColor = g2.getColor();
		g2.setColor(Color.BLACK);
		g2.fillPolygon(xPoints, yPoints, 4);
		g2.setColor(oldColor);
	}
}