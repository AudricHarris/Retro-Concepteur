package vue;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import controller.Controller;
import metier.classe.*;



public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;
	private Graphics2D g2;

	private List<Classe> lstClasse;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;

		this.lstClasse = this.ctrl.getLstClasse();
		this.setVisible(true);
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
		
		super.paintComponent( this.g2 );
		//this.dessinerUml ();

		for (Classe classe : this.lstClasse) 
		{

		}
	}

	
}