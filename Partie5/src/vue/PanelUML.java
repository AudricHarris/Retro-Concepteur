package vue;

import javax.swing.*;

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

	private List<Classe> lstClasse;
	private HashMap<Classe,Rectangle> mapClasseRectangle;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;

		this.lstClasse = this.ctrl.getLstClasse();
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();

		this.setPreferredSize(new Dimension(1000, 700));
		this.initialiserPositions();
		this.setVisible(true);
	}

	private void initialiserPositions()
	{
		int x = 50;
		int y = 50;
		
		for (Classe c : this.ctrl.getLstClasse()) 
		{
			this.mapClasseRectangle.put(c, new Rectangle(x, y, 0, 0));
			x += 250;
			if (x > 800) { x = 50; y += 300; }
		}
	}

	public void dessinerRectangle(int x, int y,int tailleX, int tailleY )
	{
		this.g2.drawLine(x, y, x, y+tailleY);
		this.g2.drawLine(x, y, x+tailleX, y);
		this.g2.drawLine(x+tailleX, y, x+tailleX, y+tailleY);
		this.g2.drawLine(x,y+tailleY, x+tailleX, y+tailleY);
	}

	/**
	 * Méthode héritée de JPanel pour dessiner le composant.
	 * Configure le contexte graphique et lance le dessin du graphe.
	 * 
	 * @param g Le contexte graphique fourni par Swing
	 */
	protected void paintComponent( Graphics g )
	{
		this.g2 = ( Graphics2D ) g;
		int tailleX, tailleY, x, y;
		
		super.paintComponent( this.g2 );
		
		Font font = new Font("Serif", Font.PLAIN, 10);
		FontMetrics metrics = getFontMetrics(font);

		for (Classe classe : this.lstClasse) 
		{
			x = this.mapClasseRectangle.get(classe).getX();
			y = this.mapClasseRectangle.get(classe).getY();

			tailleX = classe.getPlusGrandAttributMethode();
			tailleY = classe.getLstAttribut().size() + classe.getLstMethode().size();

			

			this.mapClasseRectangle.get(classe).setTailleX(tailleX + 4);
			this.mapClasseRectangle.get(classe).setTailleY(tailleY + 4);
			
			this.dessinerRectangle(x, y, tailleX, tailleY);		


		}
	}

	
}