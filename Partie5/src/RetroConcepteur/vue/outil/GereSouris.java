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

			this.classeActuel.setX(futurX);
			this.classeActuel.setY(futurY);

			this.panelUML.repaint();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		this.classeActuel = null;
		this.decalage = null;
	}
}
