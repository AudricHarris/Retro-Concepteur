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
 * Il orchestre l'affichage du diagramme en déléguant le dessin spécifique
 * aux classes spécialisées (DessinerClasse, DessinerFleche, DessinerMultiplicite).
 * * @author [Equipe 9]
 * @version 2.0 (Refactorisé)
 */
public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controleur ctrl;

	// --- Données du modèle (Métier) ---
	private List<Classe> lstClasse;
	private List<Liaison> lstLiaisons;
	private List<Chemin> lstChemins;

	// --- Données de la vue (Graphique) ---
	private HashMap<Classe, Rectangle> mapClasseRectangle;
	private HashMap<Chemin, Liaison> mapCheminLiaison; 
	
	// --- Outils de dessin délégués ---
	private DessinerFleche dessinerFleche;
	private DessinerMultiplicite dessinerMultiplicite;
	private DessinerClasse dessinerClasse; // Nouvelle classe déléguée

	private boolean positionDeterminee = false;
	private boolean afficherClassesCachables = true;

	/**
	 * Constructeur du panneau UML.
	 * Initialise les outils de dessin et les écouteurs de souris.
	 *
	 * @param frame La fenêtre principale contenant ce panneau.
	 * @param ctrl Le contrôleur pour les échanges avec le métier.
	 */
	public PanelUML(FrameUML frame, Controleur ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		
		// Instanciation des outils de dessin
		this.dessinerFleche = new DessinerFleche();
		this.dessinerMultiplicite = new DessinerMultiplicite();
		this.dessinerClasse = new DessinerClasse(ctrl); // On passe le contrôleur
		
		this.setPreferredSize(new Dimension(2000, 2000));
	
		// Initialisation des écouteurs pour le déplacement des boîtes
		GereSouris gs = new GereSouris(this);
		this.addMouseListener(gs);
		this.addMouseMotionListener(gs);
	
		// Écouteur pour le clic sur les multiplicités
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
	 * Recharge toutes les données depuis le contrôleur et réinitialise l'affichage.
	 * Appelé lors de l'ouverture d'un nouveau dossier.
	 */
	public void reinitialiser()
	{
		this.lstClasse = this.ctrl.getLstClasses();
		// Utilisation de la liste binaire pour fusionner les doubles liens
		this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaisonBinaire());
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		this.positionDeterminee = false;
		this.lstChemins = new ArrayList<Chemin>();
		this.mapCheminLiaison = new HashMap<Chemin, Liaison>();
		
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
	 * Définit manuellement la carte des positions (utilisé lors du chargement XML).
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
		return this.mapClasseRectangle;
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

		// 1. Premier passage : Dessiner les classes pour calculer leurs dimensions
		for (Classe classe : this.lstClasse)
		{
			if (!classe.getCachable() || this.afficherClassesCachables)
			{
				Rectangle rect = this.mapClasseRectangle.get(classe);
				this.dessinerClasse.dessiner(g2, classe, rect);
			}
		}

		// Si c'est le premier lancement, on calcule les positions maintenant qu'on a les tailles
		if (!this.positionDeterminee)
		{
			this.determinerPositions();
			this.positionDeterminee = true;
			this.repaint(); // On redemande un dessin avec les bonnes positions
			return;
		}

		// 2. Dessiner les liaisons (flèches) en dessous des boîtes
		for (Chemin c : this.lstChemins) 
			this.dessinerFleche.dessinerLiaison(g2, c);

		// 3. Deuxième passage : Redessiner les classes par-dessus les fils pour la propreté
		for (Classe classe : this.lstClasse)
		{
			if (!classe.getCachable() || this.afficherClassesCachables)
			{
				Rectangle rect = this.mapClasseRectangle.get(classe);
				this.dessinerClasse.dessiner(g2, classe, rect);
			}
		}

		// 4. Dessiner les multiplicités
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

				// Appel au délégué de dessin multiplicité
				this.dessinerMultiplicite.dessiner(g2, c, multFrom, multTo, l.getNomVar());
			}
		}
	}

	/**
	 * Formate le texte de la multiplicité (ex: convertit "0" et "*" en "0..*").
	 */
	private String construireLabelMultiplicite(String inf, String sup)
	{
		if ((inf == null || inf.isEmpty()) && (sup == null || sup.isEmpty())) return "";
		if ("1".equals(inf) && "1".equals(sup)) return "1";
		return inf + ".." + sup;
	}

	// =========================================================================
	// GESTION DES LIAISONS ET CHEMINS
	// =========================================================================
	
	/**
	 * Recrée les objets Chemin à partir des rectangles existants.
	 * Utile après un chargement de fichier ou un déplacement manuel.
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
	 * Regroupe les liaisons par paires de classes pour éviter les collisions visuelles.
	 */
	public void recalculerChemins()
	{
		// On vide les points d'ancrage des rectangles
		for (Rectangle rect : this.mapClasseRectangle.values()) rect.nettoyerLiaisons();

		this.lstChemins.clear();
		this.mapCheminLiaison.clear();  
		HashMap<String, List<Chemin>> mapGroupes = new HashMap<>();
		
		for (Liaison l : this.lstLiaisons)
		{
			if (!this.afficherClassesCachables)
				if (l.getFromClass().getCachable() || l.getToClass().getCachable())
					continue;
			
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
				chemin.setRectangleArrivee(r2);
				
				// Création d'une clé unique pour regrouper les liaisons entre deux mêmes classes
				String nom1 = l.getFromClass().getNom();
				String nom2 = l.getToClass().getNom();
				String cle = (nom1.compareTo(nom2) < 0) ? nom1 + "-" + nom2 : nom2 + "-" + nom1;

				if (!mapGroupes.containsKey(cle))
					mapGroupes.put(cle, new ArrayList<Chemin>());

				mapGroupes.get(cle).add(chemin);
				this.lstChemins.add(chemin);
				this.mapCheminLiaison.put(chemin, l);
			}
		}

		// Application du décalage (anti-collision) pour les groupes de liaisons
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
	 * Place initialement les boîtes en grille simple.
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
	 * Ajuste la mise en page pour éviter que les boîtes ne se chevauchent.
	 * Utilise un algorithme de saut de ligne si l'écran est trop petit.
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
	// INTERACTION UTILISATEUR (CLICS ET ÉDITION)
	// =========================================================================
	
	/**
	 * Gère le clic de souris sur une multiplicité pour ouvrir la fenêtre d'édition.
	 * @param e L'événement souris.
	 */
	private void handleMultipliciteClick(MouseEvent e)
	{
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		FontMetrics fm = getFontMetrics(font);
		Point p = new Point((int)e.getPoint().getX(), (int) e.getPoint().getY());
		
		for (Chemin c : this.lstChemins)
		{
			Liaison l = this.mapCheminLiaison.get(c);
			if (l == null) continue;

			String multFrom = construireLabelMultiplicite(l.getFromMultiplicity().getBorneInf(), l.getFromMultiplicity().getBorneSup());
			String multTo   = construireLabelMultiplicite(l.getToMultiplicity().getBorneInf(), l.getToMultiplicity().getBorneSup());

			if (this.dessinerMultiplicite.checkClick(p, c, multFrom, multTo, fm))
			{
				Multiplicite multiplicite = this.dessinerMultiplicite.isDepart() ? l.getFromMultiplicity() : l.getToMultiplicity();
				modifMultiplicite(multiplicite);
				this.repaint();
				return;
			}
		}
	}

	/**
	 * Affiche une boîte de dialogue pour modifier les bornes d'une multiplicité.
	 * @param multiplicite L'objet métier à modifier.
	 */
	private void modifMultiplicite(Multiplicite multiplicite)
	{
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(240, 60));
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JLabel labelInf = new JLabel("Borne Inférieure:");
		JTextField fieldInf = new JTextField(multiplicite.getBorneInf(), 10);
		JLabel labelSup = new JLabel("Borne Supérieure:");
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
		
		int result = JOptionPane.showConfirmDialog(this.frame, panel, "Éditer Multiplicité", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
		{
			multiplicite.setBorneInf(fieldInf.getText());
			multiplicite.setBorneSup(fieldSup.getText());
			this.repaint();
		}
	}

	/**
	 * Détecte sur quelle partie de la classe l'utilisateur a cliqué (Titre, Attributs ou Méthodes)
	 * et ouvre la fenêtre d'édition correspondante.
	 * * @param classe La classe concernée.
	 * @param rect Le rectangle graphique de la classe.
	 * @param pSouris La position du clic.
	 */
	public void detecterZoneEtOuvrirEdition(Classe classe, Rectangle rect, Point pSouris)
	{
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		FontMetrics metrics = g2.getFontMetrics();
		int hauteurLigne = metrics.getHeight();
		int padding = 5; 
		int hauteurTitre = hauteurLigne + (padding * 2);
		
		// Calcul rapide de la hauteur de la zone attributs pour savoir où on a cliqué
		int nbLignesAttributs = 0;
		int cpt = 0;
		for (Attribut att : classe.getListOrdonneeAttribut())
		{
			if (this.ctrl.estClasseProjet(att.getType())) continue;
			if (cpt >= 3) { nbLignesAttributs++; break; }
			nbLignesAttributs++;
			cpt++;
		}
		if (nbLignesAttributs == 0) nbLignesAttributs = 1; 
		int hauteurZoneAttributs = (nbLignesAttributs * (hauteurLigne + 2)) + 10; 
		
		int yRelatif = pSouris.getY() - rect.getY();

		if (yRelatif < hauteurTitre) 
		{
			new FrameEdition(this.ctrl, classe, 'C'); // Édition Classe (Titre)
		} 
		else if (yRelatif < (hauteurTitre + hauteurZoneAttributs)) 
		{
			new FrameEdition(this.ctrl, classe, 'A'); // Édition Attributs
		} 
		else 
		{
			new FrameEdition(this.ctrl, classe, 'M'); // Édition Méthodes
		}
	}

	/**
	 * Permet d'activer ou désactiver l'affichage des classes Java internes (String, etc.) si implémentées.
	 */
	public void afficherInterfaceHeritage(boolean afficher)
	{
		this.afficherClassesCachables = afficher;
		this.recalculerChemins();
		this.repaint();
	}
	
	// =========================================================================
	// OUTILS GÉOMÉTRIQUES
	// =========================================================================
	
	/**
	 * Détermine la zone relative d'un rectangle cible par rapport à une source.
	 * @return 'H' (Haut), 'B' (Bas), 'G' (Gauche), 'D' (Droite).
	 */
	public char getZone(Rectangle source, Rectangle target)
	{
		double dx = target.getCentreX() - source.getCentreX();
		double dy = target.getCentreY() - source.getCentreY();
		double xNorm = dx / (double) source.getTailleX();
		double yNorm = dy / (double) source.getTailleY();
		
		if (Math.abs(yNorm) > Math.abs(xNorm))
		{
			return (yNorm < 0) ? 'H' : 'B';
		}
		else
		{
			return (xNorm < 0) ? 'G' : 'D';
		}
	}

	/**
	 * Retourne la direction opposée (ex: Haut -> Bas).
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