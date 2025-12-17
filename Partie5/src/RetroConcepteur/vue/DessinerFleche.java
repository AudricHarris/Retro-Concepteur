package RetroConcepteur.vue; 

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.Stroke;

import RetroConcepteur.vue.outil.*;

public class DessinerFleche
{
    
    public void dessinerLiaison(Graphics2D g2, Chemin chemin)
    {
        Stroke strokeOriginal = g2.getStroke();
        String type = chemin.getType();

        if (type.equals("Implementation"))
        {
            float[] dash = {10.0f, 5.0f};
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                         BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        }

        g2.setColor(Color.BLACK);
        LinkedList<Point> parcours = chemin.getParcours();
        
        // Dessiner la ligne complète jusqu'au centre
        this.dessinerParcours(g2, parcours);

        g2.setStroke(strokeOriginal);

        if (parcours.size() >= 2 && !type.equals("BIDIRECTIONNELLE"))
        {
            // Récupérer le centre (dernier point du parcours)
            Point centre = parcours.get(parcours.size() - 1);
            Point avantCentre = parcours.get(parcours.size() - 2);
            
            // Calculer le point d'intersection avec le rectangle cible
            Rectangle rectCible = chemin.getRectangleArrivee();
            Point intersection = calculerIntersection(avantCentre, centre, rectCible);
            
            if (intersection != null)
            {
                // Calculer l'angle perpendiculaire au bord du rectangle
                double angle = calculerAnglePerpendiculaire(intersection, rectCible);
                int tailleFleche = 12;

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

    private double calculerAnglePerpendiculaire(Point intersection, Rectangle rect)
    {
        
        if (rect == null) 
        {
            return intersection != null ? 0 : 0;
        }
        int x = intersection.getX();
        int y = intersection.getY();
        
        int rectX = rect.getX();
        int rectY = rect.getY();
        int rectWidth = rect.getTailleX();
        int rectHeight = rect.getTailleY();
        
        // Déterminer sur quel bord se trouve l'intersection (avec une tolérance de 2 pixels)
        int tolerance = 2;
        
        // Bord gauche
        if (Math.abs(x - rectX) <= tolerance)
        {
            return 0; // Flèche pointant vers la droite (vers l'intérieur)
        }
        // Bord droit
        else if (Math.abs(x - (rectX + rectWidth)) <= tolerance)
        {
            return Math.PI; // Flèche pointant vers la gauche (vers l'intérieur)
        }
        // Bord haut
        else if (Math.abs(y - rectY) <= tolerance)
        {
            return Math.PI / 2; // Flèche pointant vers le bas (vers l'intérieur)
        }
        // Bord bas
        else if (Math.abs(y - (rectY + rectHeight)) <= tolerance)
        {
            return -Math.PI / 2; // Flèche pointant vers le haut (vers l'intérieur)
        }
        
        // Par défaut, calculer l'angle basé sur la position relative au centre
        int centreX = rectX + rectWidth / 2;
        int centreY = rectY + rectHeight / 2;
        return Math.atan2(centreY - y, centreX - x);
    }

    private Point calculerIntersection(Point p1, Point p2, Rectangle rect)
    {
        if (rect == null)
        {
            
            return p2;
        }
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();
        
        int rectX = rect.getX();
        int rectY = rect.getY();
        int rectWidth = rect.getTailleX();
        int rectHeight = rect.getTailleY();
        
        // Vecteur direction de la ligne
        double dx = x2 - x1;
        double dy = y2 - y1;
        
        // Normaliser pour éviter les divisions par zéro
        if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001)
            return new Point(x2, y2);
        
        double minT = Double.MAX_VALUE;
        Point intersection = null;
        
        // Test intersection avec bord gauche (x = rectX)
        if (Math.abs(dx) > 0.001)
        {
            double t = (rectX - x1) / dx;
            if (t >= 0 && t <= 1)
            {
                int y = (int)(y1 + t * dy);
                if (y >= rectY && y <= rectY + rectHeight && t < minT)
                {
                    minT = t;
                    intersection = new Point(rectX, y);
                }
            }
        }
        
        // Test intersection avec bord droit (x = rectX + rectWidth)
        if (Math.abs(dx) > 0.001)
        {
            double t = (rectX + rectWidth - x1) / dx;
            if (t >= 0 && t <= 1)
            {
                int y = (int)(y1 + t * dy);
                if (y >= rectY && y <= rectY + rectHeight && t < minT)
                {
                    minT = t;
                    intersection = new Point(rectX + rectWidth, y);
                }
            }
        }
        
        // Test intersection avec bord haut (y = rectY)
        if (Math.abs(dy) > 0.001)
        {
            double t = (rectY - y1) / dy;
            if (t >= 0 && t <= 1)
            {
                int x = (int)(x1 + t * dx);
                if (x >= rectX && x <= rectX + rectWidth && t < minT)
                {
                    minT = t;
                    intersection = new Point(x, rectY);
                }
            }
        }
        
        // Test intersection avec bord bas (y = rectY + rectHeight)
        if (Math.abs(dy) > 0.001)
        {
            double t = (rectY + rectHeight - y1) / dy;
            if (t >= 0 && t <= 1)
            {
                int x = (int)(x1 + t * dx);
                if (x >= rectX && x <= rectX + rectWidth && t < minT)
                {
                    minT = t;
                    intersection = new Point(x, rectY + rectHeight);
                }
            }
        }
        
        return intersection != null ? intersection : new Point(x2, y2);
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
}

