package RetroConcepteur.vue.outil;

import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import RetroConcepteur.vue.PanelUML;
import RetroConcepteur.metier.classe.*;

public class GereSouris extends MouseAdapter
{
	private PanelUML panelUML;
	private Point decalage;
	private Rectangle rectClasseActuel;
	private Classe classeActuel;

	public GereSouris(PanelUML panelUML)
	{
		this.panelUML = panelUML;
		this.decalage = null;
		this.classeActuel = null;
		this.classeActuel = null;
	}

	public void mousePressed(MouseEvent e)
	{
		Point p=null;

		for (Classe classe : panelUML.getMap().keySet())
		{
			Rectangle rect = panelUML.getMap().get(classe);
			p = new Point(e.getX(), e.getY());
			if (rect.possede(p))
			{
				this.rectClasseActuel = rect;
				this.classeActuel = classe;
				this.decalage = new Point(e.getX() - rect.getX(), 
				                         e.getY() - rect.getY());
			}
		}

		if ( this.rectClasseActuel != null && SwingUtilities.isRightMouseButton(e) && p!= null && rectClasseActuel.possede(p) )
		{
			this.classeActuel.setEstClique(true);
			this.panelUML.repaint();
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (this.classeActuel != null && this.decalage != null && SwingUtilities.isLeftMouseButton(e) )
		{
			int futurX = e.getX() - this.decalage.getX();
			int futurY = e.getY() - this.decalage.getY();

			this.rectClasseActuel.setX(futurX);
			this.rectClasseActuel.setY(futurY);
			
			this.panelUML.recalculerChemins();
			this.panelUML.repaint();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if ( this.classeActuel != null ) this.classeActuel.setEstClique(false);
		
		this.classeActuel = null;
		this.rectClasseActuel = null;
		this.decalage = null;
		this.panelUML.repaint();
	}

	public void mouseClicked(MouseEvent e) 
	{
		
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) 
		{
			int x = e.getX();
			int y = e.getY();

			
			for (Classe c : this.panelUML.getMap().keySet()) 
			{
				Point pSouris = new Point(x, y);
				Rectangle r = this.panelUML.getMap().get(c);
				if (r.possede(pSouris)) 
				{
					this.panelUML.detecterZoneEtOuvrirEdition(c, r, pSouris);
					break;
				}
			}
		}
	}
}
