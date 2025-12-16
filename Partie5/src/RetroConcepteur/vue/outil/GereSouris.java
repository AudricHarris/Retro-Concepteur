package RetroConcepteur.vue.outil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import RetroConcepteur.vue.PanelUML;
import RetroConcepteur.metier.classe.*;

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
			int futurX = e.getX() - this.decalage.getX();
			int futurY = e.getY() - this.decalage.getY();

			
			if ( ! collision( futurX, this.classeActuel.getY() ) )
				this.classeActuel.setX(futurX);

			
			if ( ! collision( this.classeActuel.getX(), futurY ) )
				this.classeActuel.setY(futurY);

			this.panelUML.repaint();
		}
	}



	private boolean collision(int x1, int y1)
	{
		int largeur = this.classeActuel.getTailleX();
		int hauteur = this.classeActuel.getTailleY();

		for (Rectangle rect : this.panelUML.getMap().values())
		{
			if (rect == this.classeActuel) continue;

			int x2 = rect.getX();
			int y2 = rect.getY();
			int autreLargeur = rect.getTailleX();
			int autreHauteur = rect.getTailleY();

			if ( ! (x1 + largeur <= x2-5 || x1 >= x2 + autreLargeur+5      ||
				y1 + hauteur <= y2-5     || y1 >= y2 + autreHauteur+5         ) )
				return true;
		}

		return false;
	}



	public void mouseReleased(MouseEvent e)
	{
		this.classeActuel = null;
		this.decalage = null;
	}
}

