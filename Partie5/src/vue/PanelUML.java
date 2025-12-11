package vue;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import controller.Controller;
import metier.classe.*;
import metier.forme.*;



public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;
	private Graphics2D g2;
	private AffichageCUI affichageCUI;

	private List<Classe> lstClasse;
	private HashMap<Classe,Rectangle> mapClasseRectangle;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		this.affichageCUI = new AffichageCUI();

		this.lstClasse = this.ctrl.getLstClasse();
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		
		this.setPreferredSize(new Dimension(1000, 700));
		this.initialiserPositions();

		this.repaint();
		this.setVisible(true);
	}

	//TODO: scanner sur mapAttMethode
	
	private void initialiserPositions()
	{
		int x = 50;
		int y = 50;
		
		for (Classe c : this.ctrl.getLstClasse()) 
		{
			Rectangle rect = new Rectangle(x, y, 0, 0);
			this.mapClasseRectangle.put(c, rect);
			x += 250;
			if (x > 800)  
				x = 50; y += 300;
		}
	}

	public void dessinerRectangle(int x, int y,int tailleX, int tailleY )
	{

		this.g2.setColor(new Color(255, 255, 255)); 
		this.g2.fillRect(x, y, tailleX, tailleY);
		
		this.g2.setColor(Color.BLACK);
		this.g2.drawRect(x, y, tailleX, tailleY);
	}


	/**
	 * Méthode héritée de JPanel pour dessiner le composant.
	 * Configure le contexte graphique et lance le dessin du graphe.
	 * 
	 * @param g Le contexte graphique fourni par Swing
	 */
	protected void paintComponent( Graphics g )
	{
		int largeurAtt;
		int x, y;
		int largeurMeth;

		Font font = new Font("Serif", Font.PLAIN, 12); // Taille 12 c'est mieux que 10

		super.paintComponent(g);
		this.g2 = (Graphics2D) g;
		this.g2.setFont(font); 

		FontMetrics metrics = g.getFontMetrics(font);

		// Hauteur d'une ligne de texte en pixels
		int hauteurLigne = metrics.getHeight();

		for (Classe classe : this.lstClasse) 
		{
			x = this.mapClasseRectangle.get(classe).getX();
			y = this.mapClasseRectangle.get(classe).getY();

			int largeurMaxPixels = metrics.stringWidth(classe.getNom());
			this.dessinerRectangle(x, y, 50,50);
		}
	}

	
}