package RetroConcepteur.vue;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;;

public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;
	private Graphics2D g2;
	private Formateur formateur;

	private List<Classe> lstClasse;
	private HashMap<Classe, Rectangle> mapClasseRectangle;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		this.formateur = new Formateur(this.ctrl);

		this.lstClasse = this.ctrl.getLstClasses();
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
		
		for (Classe c : this.ctrl.getLstClasses()) 
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
		int x, y;
		int largeurMeth;
		int largeurAttribut;
		String blocAtt, blocMeth;
		int largeurX, hauteurY;

		Font font = new Font("Serif", Font.PLAIN, 12); 

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

			largeurAttribut = classe.getPlusGrandAttribut() +1;
			largeurMeth     = classe.getPlusGrandeMethode() +1;

			blocAtt = this.formateur.getBlocAttribut(classe, largeurAttribut);
			blocMeth = this.formateur.getBlocMethode(classe, largeurMeth);

			largeurX = metrics.stringWidth(this.formateur.getLigneMax(blocMeth, blocAtt));
			hauteurY = hauteurLigne * 

			this.dessinerRectangle(x, y, largeurX, hauteurY);
		}
	}

	
}