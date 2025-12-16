package RetroConcepteur.vue.outil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import RetroConcepteur.vue.PanelUML;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;

public class GereSouris extends MouseAdapter
{
	private PanelUML panelUML;
	private Point decalage;
	private Rectangle classeActuel;

	public GereSouris(PanelUML panelUML)
	{
		this.panelUML = panelUML;
		this.decalage = null;
		this.classeActuel = null;
	}

	public void mousePressed(MouseEvent e)
	{
		for (Classe classe : panelUML.getMap().keySet())
		{
			Rectangle rect = panelUML.getMap().get(classe);
			Point p = new Point(e.getX(), e.getY());
			if (rect.possede(p))
			{
				this.classeActuel = rect;
				this.decalage = new Point(e.getX() - rect.getX(), 
				                         e.getY() - rect.getY());
			}
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (this.classeActuel != null && this.decalage != null)
		{
			for ( Rectangle rect : this.panelUML.getMap().values() )
			{
				if ( ! this.collision(rect) )
				{
					this.classeActuel.setX(e.getX() - this.decalage.getX());
					this.classeActuel.setY(e.getY() - this.decalage.getY());
				}
			}			

			this.panelUML.repaint();
		}
	}

	private boolean collision( Rectangle rect )
	{
		// if (this.classeActuel == null) return false;

		// for ( Rectangle autreRect : this.panelUML.getMap().values() )
		// {
		// 	if (autreRect == this.classeActuel) continue;

			
		// 	int xDeb = this.classeActuel.getX();
		// 	int xFin = this.classeActuel.getX() + this.classeActuel.getTailleX();
		// 	int yDeb = this.classeActuel.getY();
		// 	int yFin = this.classeActuel.getY() + this.classeActuel.getTailleY();

		// 	for (int x = xDeb; x <= xFin; x++)
		// 	{
		// 		for (int y = yDeb; y <= yFin; y++)
		// 		{
		// 			Point p = new Point(x, y);
		// 			if ( autreRect.possede(p) )
		// 			{
		// 				return true;
		// 			}
		// 		}
		// 	}
		// }

		return false;
	}

	public void mouseReleased(MouseEvent e)
	{
		this.classeActuel = null;
		this.decalage = null;
	}
}

