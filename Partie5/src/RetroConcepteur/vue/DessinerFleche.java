package RetroConcepteur.vue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.LinkedList;

import RetroConcepteur.vue.outil.*;

public class DessinerFleche
{
	public void dessinerLiaison(Graphics2D g2, Chemin chemin)
	{
		java.awt.Stroke strokeOriginal = g2.getStroke();
		String type = chemin.getType();

		if (type.equals("Implementation"))
		{
			float[] dash = {10.0f, 5.0f};
			g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
			             BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		}

		g2.setColor(Color.BLACK);
		this.dessinerParcours(g2, chemin.getParcours());

		g2.setStroke(strokeOriginal);

		Point arrivee = chemin.getArrivee();
		LinkedList<Point> parcours = chemin.getParcours();
		
		if (parcours.size() >= 2)
		{
			Point avantDernier = parcours.get(parcours.size() - 2);

			double angle = Math.atan2(arrivee.getY() - avantDernier.getY(),
									arrivee.getX() - avantDernier.getX());
			int tailleFleche = 12;

			switch (type)
			{
				case "UNI":
					this.dessinerPointeOuverte(g2, arrivee.getX(), arrivee.getY(), 
											angle, tailleFleche);
					break;

				case "Generalisation":
				case "Implementation":
					this.dessinerTriangle(g2, arrivee.getX(), arrivee.getY(), 
										angle, tailleFleche);
					break;


				case "BIDIRECTIONNELLE":
					break;
			}
		}
	}

	private void dessinerParcours(Graphics2D g2, LinkedList<Point> parcours)
	{
		for (int i = 0; i < parcours.size() - 1; i++)
		{
			Point p1 = parcours.get(i);
			Point p2 = parcours.get(i + 1);
			g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}

	private void dessinerPointeOuverte(Graphics2D g2, int x, int y, 
	                                   double angle, int taille)
	{
		int x1 = (int)(x - taille * Math.cos(angle - Math.PI / 6));
		int y1 = (int)(y - taille * Math.sin(angle - Math.PI / 6));
		int x2 = (int)(x - taille * Math.cos(angle + Math.PI / 6));
		int y2 = (int)(y - taille * Math.sin(angle + Math.PI / 6));

		g2.drawLine(x, y, x1, y1);
		g2.drawLine(x, y, x2, y2);
	}

	private void dessinerTriangle(Graphics2D g2, int x, int y, 
	                              double angle, int taille)
	{
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];

		xPoints[0] = x;
		yPoints[0] = y;

		xPoints[1] = (int)(x - taille * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int)(y - taille * Math.sin(angle - Math.PI / 6));

		xPoints[2] = (int)(x - taille * Math.cos(angle + Math.PI / 6));
		yPoints[2] = (int)(y - taille * Math.sin(angle + Math.PI / 6));

		Color oldColor = g2.getColor();
		g2.setColor(Color.WHITE);
		g2.fillPolygon(xPoints, yPoints, 3);

		g2.setColor(oldColor);
		g2.drawPolygon(xPoints, yPoints, 3);
	}

	private void dessinerLosange(Graphics2D g2, int x, int y, 
	                             double angle, int taille)
	{
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];

		xPoints[0] = x;
		yPoints[0] = y;

		xPoints[1] = (int)(x - taille * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int)(y - taille * Math.sin(angle - Math.PI / 6));

		xPoints[2] = (int)(x - (taille * 1.7) * Math.cos(angle));
		yPoints[2] = (int)(y - (taille * 1.7) * Math.sin(angle));

		xPoints[3] = (int)(x - taille * Math.cos(angle + Math.PI / 6));
		yPoints[3] = (int)(y - taille * Math.sin(angle + Math.PI / 6));

		Color oldColor = g2.getColor();
		g2.setColor(Color.BLACK);
		g2.fillPolygon(xPoints, yPoints, 4);
		g2.setColor(oldColor);
	}
}

