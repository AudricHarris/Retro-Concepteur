package RetroConcepteur.vue.panel;

// Nos paquetage
import RetroConcepteur.Controleur;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;
import RetroConcepteur.vue.dessin.*;
import RetroConcepteur.vue.FrameUML;
import RetroConcepteur.vue.FrameEdition;

// paquetage AWT
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// paquetage util
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// paquetage swing
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * Panneau principal de l'application.
 * Il orchestre l'affichage du diagramme en deleguant le dessin specifique
 * aux classes specialisees (DessinerClasse, DessinerFleche, DessinerMultiplicite).
 * * @author [Equipe 9]
 * @version 2.0 (Refactorise)
 */
public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controleur ctrl;

	// --- Donnees du modele (Metier) ---
	private List<Classe> lstClasse;
	private List<Liaison> lstLiaisons;
	private List<Chemin> lstChemins;

	// --- Donnees de la vue (Graphique) ---
	private HashMap<Classe, Rectangle> mapClasseRectangle;
	private HashMap<Chemin, Liaison> mapCheminLiaison; 
	
	// --- Outils de dessin delegues ---
	private DessinerFleche dessinerFleche;
	private DessinerMultiplicite dessinerMultiplicite;
	private DessinerClasse dessinerClasse; // Nouvelle classe deleguee

	private boolean positionDeterminee = false;
	private boolean afficherClassesCachables = true;

	/**
	 * Constructeur du panneau UML.
	 * Initialise les outils de dessin et les ecouteurs de souris.
	 *
	 * @param frame La fenetre principale contenant ce panneau.
	 * @param ctrl Le controleur pour les echanges avec le metier.
	 */
	public PanelUML(FrameUML frame, Controleur ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		
		this.dessinerFleche = new DessinerFleche();
		this.dessinerMultiplicite = new DessinerMultiplicite();
		this.dessinerClasse = new DessinerClasse(ctrl); // On passe le controleur
		
		this.setPreferredSize(new Dimension(2000, 2000));
	
		GereSouris gs = new GereSouris(this.ctrl, this);
		this.addMouseListener(gs);
		this.addMouseMotionListener(gs);
	
		// ecouteur pour le clic sur les multiplicites
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				handleMultipliciteClick(e);
			}
		});
	
		this.reinitialiser();
	}
	
	/**
	 * Recharge toutes les donnees depuis le controleur et reinitialise l'affichage.
	 * Appele lors de l'ouverture d'un nouveau dossier.
	 */
	public void reinitialiser()
	{
		this.lstClasse = this.ctrl.getLstClasses();
		
		this.lstLiaisons        = new ArrayList<Liaison>(this.ctrl.getListLiaisonBinaire());
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		this.positionDeterminee = false;
		this.lstChemins         = new ArrayList<Chemin>();
		this.mapCheminLiaison   = new HashMap<Chemin, Liaison>();
		
		this.initialiserPositions();
		this.determinerPositions();
		this.repaint();
	}

	/**
	 * Force le redessin du composant.
	 */
	public void majIHM()
	{
		this.repaint();
	}
	
	/**
	 * Definit manuellement la carte des positions (utilise lors du chargement XML).
	 * @param map La correspondance Classe -> Rectangle.
	 */
	public void setMap(HashMap<Classe, Rectangle> map)
	{
		this.mapClasseRectangle = map;
		this.positionDeterminee = true;
		this.reconstruireChemins();
		this.repaint();
	}

	/**
	 * Retourne la carte des rectangles actuels.
	 * @return La map Classe -> Rectangle.
	 */
	public HashMap<Classe, Rectangle> getMap()
	{
		return new HashMap<Classe, Rectangle>(mapClasseRectangle);
	}
	
	// =========================================================================
	// DESSIN PRINCIPAL (PAINT COMPONENT)
	// =========================================================================
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		g2.setFont(font);
		g2.setStroke(new BasicStroke(2.0f));

		for (Classe classe : this.lstClasse)
		{
			if (!classe.getCachable() || this.afficherClassesCachables)
			{
				Rectangle rect = this.mapClasseRectangle.get(classe);
				this.dessinerClasse.dessiner(g2, classe, rect);
			}
		}

		if (!this.positionDeterminee)
		{
			this.determinerPositions();
			this.positionDeterminee = true;
			this.repaint();
			return;
		}

		for (Chemin c : this.lstChemins) 
			this.dessinerFleche.dessinerLiaison(g2, c);

		for (Classe classe : this.lstClasse)
		{
			if (!classe.getCachable() || this.afficherClassesCachables)
			{
				Rectangle rect = this.mapClasseRectangle.get(classe);
				this.dessinerClasse.dessiner(g2, classe, rect);
			}
		}

		for (Chemin c : this.lstChemins)    
		{   
			Liaison l = this.mapCheminLiaison.get(c);
			if (l != null)
			{
				String infFrom = l.getFromMultiplicity().getBorneInf();
				String supFrom = l.getFromMultiplicity().getBorneSup();
				String infTo   = l.getToMultiplicity().getBorneInf();
				String supTo   = l.getToMultiplicity().getBorneSup();

				String multFrom = construireLabelMultiplicite(infFrom, supFrom);
				String multTo   = construireLabelMultiplicite(infTo, supTo);

				this.dessinerMultiplicite.dessiner(g2, c, multFrom, multTo, l.getNomVar());
			}
		}
	}

	/**
	 * Formate le texte de la multiplicite (ex: convertit "0" et "*" en "0..*").
	 */
	private String construireLabelMultiplicite(String inf, String sup)
	{
		if ((inf == null || inf.isEmpty()) && (sup == null || sup.isEmpty())) 
			return "";
		if ("1".equals(inf) && "1".equals(sup)) 
			return "1";
		return inf + ".." + sup;
	}

	// =========================================================================
	// GESTION DES LIAISONS ET CHEMINS
	// =========================================================================

	/**
     * Helper method to create and configure a Chemin object from a Liaison.
     * It calculates zones, adds positions to rectangles, and distributes link points.
     *
     * @param l The Liaison object to create a path for.
     * @return The configured Chemin object, or null if rectangles are missing.
     */
    private Chemin creerChemin(Liaison l) 
	{
        Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
        Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());

        if (r1 != null && r2 != null) {
            Point p1 = new Point(r1.getCentreX(), r1.getCentreY());
            Point p2 = new Point(r2.getCentreX(), r2.getCentreY());

            Chemin chemin = new Chemin(p1, p2, l.getType(), this.mapClasseRectangle, l.getFromClass(), l.getToClass());

            char zone = this.getZone(r1, r2);
            char zoneInv = zoneInverse(zone);
            chemin.setZoneArrivee(zoneInv);

            r1.addPos(zone, chemin);
            r2.addPos(zoneInv, chemin);

            r1.repartirPointsLiaison(zone);
            r2.repartirPointsLiaison(zoneInv);

            return chemin;
        }
        return null;
    }
	
	/**
	 * Recree les objets Chemin a partir des rectangles existants.
	 * Utile apres un chargement de fichier ou un deplacement manuel.
	 */
	private void reconstruireChemins()
	{
		this.lstChemins.clear();
		for (Liaison l : this.lstLiaisons)
		{
			this.mapCheminLiaison.clear();
			Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());
			if (r1 != null && r2 != null)
			{
				Point p1 = new Point(r1.getCentreX(), r1.getCentreY());
				Point p2 = new Point(r2.getCentreX(), r2.getCentreY());
				Chemin chemin = new Chemin(p1, p2, l.getType(), this.mapClasseRectangle, l.getFromClass(), l.getToClass());
				
				char zone = this.getZone(r1, r2);
				char zoneInv = zoneInverse(zone);
				chemin.setZoneArrivee(zoneInv);
				
				r1.addPos(zone, chemin);
				r2.addPos(zoneInv, chemin);
				r1.repartirPointsLiaison(zone);
				r2.repartirPointsLiaison(zoneInv);
				
				this.lstChemins.add(chemin);
				this.mapCheminLiaison.put(chemin, l);
			}
		}
		this.recalculerChemins();
	}

	/**
	 * Algorithme principal de routage des liaisons.
	 * Regroupe les liaisons par paires de classes pour eviter les collisions visuelles.
	 */
	public void recalculerChemins()
	{
		// On vide les points d'ancrage des rectangles
		for ( Rectangle rect : this.mapClasseRectangle.values() ) 
			rect.nettoyerLiaisons();

		this.lstChemins.clear();
		this.mapCheminLiaison.clear();  
		HashMap<String, List<Chemin>> mapGroupes = new HashMap<>();
		
		for ( Liaison l : this.lstLiaisons )
		{
			if ( ! this.afficherClassesCachables )
				if ( l.getFromClass().getCachable() || l.getToClass().getCachable() )
					continue;
			
			Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());
			
			if ( r1 != null && r2 != null )
			{
				Point p1 = new Point( r1.getCentreX(), r1.getCentreY() );
				Point p2 = new Point( r2.getCentreX(), r2.getCentreY() );

				Chemin chemin = new Chemin( p1, p2, l.getType(), this.mapClasseRectangle, l.getFromClass(), l.getToClass() );
				
				char zone    = this.getZone(r1, r2);
				char zoneInv = zoneInverse(zone);
				chemin.setZoneArrivee(zoneInv);
				
				r1.addPos(zone, chemin);
				r2.addPos(zoneInv, chemin);

				r1.repartirPointsLiaison(zone);
				r2.repartirPointsLiaison(zoneInv);

				chemin.setRectangleArrivee(r2);
				
				// Creation d'une cle unique pour regrouper les liaisons entre deux memes classes
				String nom1 = l.getFromClass().getNom();
				String nom2 = l.getToClass().getNom();
				String cle = (nom1.compareTo(nom2) < 0) ? nom1 + "-" + nom2 : nom2 + "-" + nom1;

				if ( ! mapGroupes.containsKey(cle) )
					mapGroupes.put(cle, new ArrayList<Chemin>());

				mapGroupes.get(cle).add(chemin);
				this.lstChemins.add(chemin);
				this.mapCheminLiaison.put(chemin, l);
			}
		}

		// Application du decalage (anti-collision) pour les groupes de liaisons
		for (List<Chemin> groupe : mapGroupes.values())
		{
			int total = groupe.size();
			for (int i = 0; i < total; i++)
			{
				Chemin c = groupe.get(i);
				c.setIndexLiaison(i, total);
			}
		}
	}

	/**
	 * Initie les positions des classes de manière optimale
	 */
	private void initialiserPositions()
	{
		int x = 50;
		int y = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		for (Classe c : this.lstClasse)
		{
			Rectangle rect = new Rectangle(x, y, 0, 0);
			this.mapClasseRectangle.put(c, rect);
			x += 350;
			if (x > screenSize.width - 200)
			{
				x = 50;
				y += 350;
			}
		}
	}
	

	/**
	 * Ajuste la mise en page pour eviter que les boites ne se chevauchent.
	 * Utilise un algorithme de saut de ligne si l'ecran est trop petit.
	 */
	private void determinerPositions()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int xCourant = 50;
		int yCourant = 50;
		int hauteurLigneMax = 0;
		
		for (Rectangle rect : this.mapClasseRectangle.values())
		{
			if (xCourant + rect.getTailleX() > screenSize.width - 100 && xCourant > 50)
			{
				xCourant = 50;
				yCourant += hauteurLigneMax + 50;
				hauteurLigneMax = 0;
			}
			rect.setX(xCourant);
			rect.setY(yCourant);
			xCourant += rect.getTailleX() + 50;

			if (rect.getTailleY() > hauteurLigneMax)
				hauteurLigneMax = rect.getTailleY();
		}
		
		this.recalculerChemins();
	}
	
	// =========================================================================
	// INTERACTION UTILISATEUR (CLICS ET eDITION)
	// =========================================================================
	
	/**
	 * Gere le clic de souris sur une multiplicite pour ouvrir la fenetre d'edition.
	 * @param e L'evenement souris.
	 */
	private void handleMultipliciteClick(MouseEvent e)
	{
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		FontMetrics fm = getFontMetrics(font);
		Point point = new Point((int)e.getPoint().getX(), (int) e.getPoint().getY());
		
		for (Chemin chemin : this.lstChemins)
		{
			Liaison l = this.mapCheminLiaison.get(chemin);
			if (l == null) continue;

			String multDep = construireLabelMultiplicite( l.getFromMultiplicity().getBorneInf(), l.getFromMultiplicity().getBorneSup() );
			String multArr   = construireLabelMultiplicite( l.getToMultiplicity  ().getBorneInf(), l.getToMultiplicity  ().getBorneSup() );

			if ( this.dessinerMultiplicite.checkClick(point, chemin, multDep, multArr, fm) )
			{
				Multiplicite multiplicite = this.dessinerMultiplicite.isDepart() ? l.getFromMultiplicity() : l.getToMultiplicity();
				modifMultiplicite(multiplicite);
				this.repaint();
				return;
			}
		}
	}

	/**
	 * Affiche une boite de dialogue pour modifier les bornes d'une multiplicite.
	 * @param multiplicite L'objet metier a modifier.
	 */
	private void modifMultiplicite(Multiplicite multiplicite)
	{
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(240, 60));
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JLabel labelInf = new JLabel("Borne Inferieure:");
		JTextField fieldInf = new JTextField(multiplicite.getBorneInf(), 10);
		JLabel labelSup = new JLabel("Borne Superieure:");
		JTextField fieldSup = new JTextField(multiplicite.getBorneSup(), 10);
	
		panel.add(labelInf);
		panel.add(fieldInf);
		panel.add(labelSup);
		panel.add(fieldSup);
		
		// Mise en page (contraintes SpringLayout)
		layout.putConstraint(SpringLayout.WEST, labelInf, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, labelInf, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, fieldInf, 5, SpringLayout.EAST, labelInf);
		layout.putConstraint(SpringLayout.NORTH, fieldInf, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, labelSup, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, labelSup, 5, SpringLayout.SOUTH, fieldInf);
		layout.putConstraint(SpringLayout.WEST, fieldSup, 5, SpringLayout.EAST, labelSup);
		layout.putConstraint(SpringLayout.NORTH, fieldSup, 5, SpringLayout.SOUTH, fieldInf);
		
		int result = JOptionPane.showConfirmDialog(this.frame, panel, "editer Multiplicite", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
		{
			multiplicite.setBorneInf(fieldInf.getText());
			multiplicite.setBorneSup(fieldSup.getText());
			this.repaint();
		}
	}


	/**
	 * Permet d'activer ou desactiver l'affichage des classes Java de la jdk si implementees ou herite.
	 */
	public void afficherInterfaceHeritage(boolean afficher)
	{
		this.afficherClassesCachables = afficher;
		this.recalculerChemins();
		this.repaint();
	}
	
	// =========================================================================
	// OUTILS GEOMETRIQUES
	// =========================================================================
	
	/**
	 * Determine la zone relative d'un rectangle cible par rapport a une source.
	 * @return 'H' (Haut), 'B' (Bas), 'G' (Gauche), 'D' (Droite).
	 */
	public char getZone(Rectangle source, Rectangle target)
	{
		double dx    = target.getCentreX() - source.getCentreX();
		double dy    = target.getCentreY() - source.getCentreY();
		double xNorm = dx / (double) source.getTailleX();
		double yNorm = dy / (double) source.getTailleY();
		
		if (Math.abs(yNorm) > Math.abs(xNorm))
			return (yNorm < 0) ? 'H' : 'B';
		
		else
			return (xNorm < 0) ? 'G' : 'D';
		
	}

	/**
	 * Retourne la direction opposee (ex: Haut -> Bas).
	 */
	private char zoneInverse(char zone)
	{
		switch (zone)
		{
			case 'H': return 'B';
			case 'B': return 'H';
			case 'G': return 'D';
			case 'D': return 'G';
			default: return ' ';
		}
	}
}