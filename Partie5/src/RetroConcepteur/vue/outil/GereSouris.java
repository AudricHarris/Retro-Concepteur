package RetroConcepteur.vue.outil;

// Packetage SWING 
import javax.swing.SwingUtilities;

// Packetage AWT
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Nos packetage
import RetroConcepteur.vue.PanelUML;
import RetroConcepteur.metier.classe.*;

/**
 * Classe responsable du calcul des chemin
 *
 * @author [Equipe 9]
 * @version 1.0
 */
public class GereSouris extends MouseAdapter
{
	private Point     decalage;
	private Classe    classeActuel;
	private Rectangle rectClasseActuel;

	private PanelUML panelUML;
	
	/**
	 *	Creer une instance de Gere Souris
	 *	@param panelUML le panel parent de gere souris
	 */
	public GereSouris(PanelUML panelUML)
	{
		this.panelUML = panelUML;
		this.decalage = null;
		this.classeActuel = null;
		this.classeActuel = null;
	}

	//---------------------------------------//
	//          Gestion de souris            //
	//---------------------------------------//
	
	/**
	 *	Gestion pour la souris quand maintenue
	 *	@param e Mouse event
	 */
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
	
	/**
	 *	Gestion pour la souris quand deplacer et maintenue
	 *	@param e Mouse event
	 */
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
	
	/**
	 *	Gestion pour la souris quand lacher
	 *	@param e Mouse event
	 */
	public void mouseReleased(MouseEvent e)
	{
		if ( this.classeActuel != null ) this.classeActuel.setEstClique(false);
		
		this.classeActuel     = null;
		this.rectClasseActuel = null;
		this.decalage         = null;

		this.panelUML.repaint();
	}
	
	/**
	 *	Gestion pour la souris quand cliquer
	 *	@param e Mouse event
	 */
	public void mouseClicked(MouseEvent e) 
	{
		
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) 
		{
			int x = e.getX();
			int y = e.getY();

			
			for (Classe c : this.panelUML.getMap().keySet()) 
			{
				Point     pSouris = new Point(x, y);
				Rectangle r       = this.panelUML.getMap().get(c);
				if (r.possede(pSouris)) 
				{
					this.panelUML.detecterZoneEtOuvrirEdition(c, r, pSouris);
					break;
				}
			}
		}
	}
	public void mouseClicked(MouseEvent e) 
	{
		
		if (e.getClickCount() == 2) 
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
