package RetroConcepteur.vue.outil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import RetroConcepteur.vue.PanelUML;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.metier.*;

import java.awt.Point;


// Classe qui gère les actions de la souris
public class GereSouris extends MouseAdapter
{
	/*-----------*/
	/* Attributs */
	/*-----------*/

	private PanelUML  panelUML;
	private Point     decalage;
	private Rectangle classeActuel;

	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public GereSouris ( PanelUML panelUML )
	{
		this.panelUML     = panelUML;
		this.decalage     = null;
		this.classeActuel = null;
	}

	/*-----------------*/
	/* Autres méthodes */
	/*-----------------*/

	// Action lorsque l'on appuie sur les boutons de la souris
	public void mousePressed ( MouseEvent e )
	{
		for ( Classe classe : panelUML.getMap().keySet() )
		{
			Rectangle rect = panelUML.getMap().get(classe);
			if ( rect.possede(e.getX(), e.getY()) )
			{
				this.classeActuel = rect;
				this.decalage = new Point(e.getX() - rect.getX(), e.getY() - rect.getY());
			}
		}
	}

	public void mouseDragged ( MouseEvent e )
	{
		if ( this.classeActuel != null && this.decalage != null)
		{
			this.classeActuel.setX ( e.getX() - this.decalage.x );
			this.classeActuel.setY ( e.getY() - this.decalage.y );

			//Math.clamp(e.getY() - this.decalage.y, 0, );
			
			//this.panelUML.updatePreferredSize();
			this.panelUML.repaint();
		}
	}

	// Actions lorsque l'on relache la souris
	public void mouseReleased(MouseEvent e)
	{
		this.classeActuel = null;
		this.decalage     = null;

		// this.panelUML.recalerSiNecessaire();
		// this.panelUML.updatePreferredSize();
	}
}