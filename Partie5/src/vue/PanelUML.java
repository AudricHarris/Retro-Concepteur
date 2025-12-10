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

	public int getLargeurMax (Classe classe)
	{
		HashMap<Classe,ArrayList<String>> map = this.affichageCUI.getHashMap();
		Scanner sc;
		String ligne;
		int i;

		for (String string  : map.get(classe) ) 
		{
			try
			{
				sc = new Scanner( string );
				sc.useDelimiter("\\n");
				while (sc.hasNext()) 
				{
					this.tabAttMethLigne[0] = sc.next();
				}
				


				sc.close();
			}
			catch ( Exception e ) { e.printStackTrace(); }
		}
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
			
			largeurMaxPixels = metrics.

			// On ajoute une petite marge de 10px
			int tailleX = largeurMaxPixels + 10;


			// 2. CALCUL DE LA HAUTEUR EN PIXELS
			// Nombre d'éléments * hauteur d'une ligne
			int nbLignes = 1 + classe.getLstAttribut().size() + classe.getLstMethode().size(); // +1 pour le titre
			int tailleY = nbLignes * hauteurLigne + 10; // +10 de marge


			// Mise à jour et dessin
			this.mapClasseRectangle.get(classe).setTailleX(tailleX);
			this.mapClasseRectangle.get(classe).setTailleY(tailleY);
			
			this.dessinerRectangle(x, y, tailleX, tailleY);     
		}
	}

	
}