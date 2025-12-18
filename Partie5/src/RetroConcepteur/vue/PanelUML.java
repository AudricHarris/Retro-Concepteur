package RetroConcepteur.vue;
import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
/**
 * Panneau principal pour l'affichage et l'édition du diagramme UML.
 * Gère le dessin des classes, des liaisons et des multiplicités.
 *
 * @author [Equipe 9]
 * @version 1.0
 */
public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;

	// Données du modèle
	private List<Classe> lstClasse;
	private List<Liaison> lstLiaisons;
	private List<Chemin> lstChemins;

	// Données de la vue
	private HashMap<Classe, Rectangle> mapClasseRectangle;
	private DessinerFleche dessinerFleche;
	private DessinerMultiplicite dessinerMultiplicite;
	private boolean positionDeterminee = false;

	// Constantes de style
	private final int PADDING_X = 10;
	private final int PADDING_Y = 5;
	private final int INTERLIGNE = 2;
	
	/**
	 * Constructeur du panneau UML.
	 *
	 * @param frame La fenêtre principale UML.
	 * @param ctrl Le contrôleur associé.
	 */
	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		this.dessinerFleche = new DessinerFleche();
		this.dessinerMultiplicite = new DessinerMultiplicite();
		this.setPreferredSize(new Dimension(2000, 2000));
	
		// Initialisation des écouteurs souris pour le déplacement et l'édition
		GereSouris gs = new GereSouris(this);
		this.addMouseListener(gs);
		this.addMouseMotionListener(gs);
	
		// Écouteur spécifique pour les multiplicités
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
	 * Recharge les données depuis le contrôleur et recalcule les positions.
	 */
	public void reinitialiser()
	{
		this.lstClasse = this.ctrl.getLstClasses();
		this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaisonBinaire());
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		this.positionDeterminee = false;
		this.lstChemins = new ArrayList<Chemin>();
		this.initialiserPositions();
		this.determinerPositions();
		this.repaint();
	}
	
	/**
	 * Applique une map de positions (Classe -> Rectangle) pré-calculée.
	 * Utile pour restaurer les positions après chargement XML.
	 *
	 * @param map La map des positions à appliquer.
	 */
	public void setMap(HashMap<Classe, Rectangle> map)
	{
		this.mapClasseRectangle = map;
		this.positionDeterminee = true;
		this.reconstruireChemins();
		this.repaint();
	}

	public HashMap<Classe, Rectangle> getMap()
	{
		return this.mapClasseRectangle;
	}
	
	// =========================================================================
	// DESSIN PRINCIPAL
	// =========================================================================
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		g2.setFont(font);
		g2.setStroke(new BasicStroke(2.0f));
		for (Classe classe : this.lstClasse) this.dessinerClasse(g2, classe);

		if (!this.positionDeterminee)
		{
			this.determinerPositions();
			this.positionDeterminee = true;
			this.repaint();
			return;
		}

		for (Chemin c : this.lstChemins) this.dessinerFleche.dessinerLiaison(g2, c);

		for (Classe classe : this.lstClasse) this.dessinerClasse(g2, classe);
		
		int numLiaisons = Math.min(this.lstLiaisons.size(), this.lstChemins.size());
		
		for (int i = 0; i < numLiaisons; i++)
		{
			Liaison l = this.lstLiaisons.get(i);
			Chemin c = this.lstChemins.get(i);
			

			String infFrom = l.getFromMultiplicity().getBorneInf();
			String supFrom = l.getFromMultiplicity().getBorneSup();
			String infTo   = l.getToMultiplicity().getBorneInf();
			String supTo   = l.getToMultiplicity().getBorneSup();

			String multFrom = construireLabelMultiplicite(infFrom, supFrom);
			String multTo   = construireLabelMultiplicite(infTo, supTo);
			
			this.dessinerMultiplicite.dessiner(g2, c, multFrom, multTo, l.getNomVar());
		}
	}

	/**
	 * Construit le label (ex: "0..1" ou "1") et gère les cas vides.
	 */
	private String construireLabelMultiplicite(String inf, String sup)
	{
		if ((inf == null || inf.isEmpty()) && (sup == null || sup.isEmpty())) return "";
		
		if ("1".equals(inf) && "1".equals(sup)) return "1";
		
		return inf + ".." + sup;
	}

	// =========================================================================
	// DESSIN D'UNE CLASSE
	// =========================================================================
	
	/**
	 * Dessine une classe UML avec ses attributs et méthodes.
	 *
	 * @param g2 Le contexte graphique.
	 * @param classe La classe à dessiner.
	 */
	private void dessinerClasse(Graphics2D g2, Classe classe)
	{
		FontMetrics metrics = g2.getFontMetrics();
		Rectangle rect = this.mapClasseRectangle.get(classe);
		int x = rect.getX();
		int y = rect.getY();

		// Préparation des textes avec alignement et filtrage

		List<Attribut> lstAtt = new ArrayList<Attribut>();
		int maxLargeurGaucheAtt = 0;
		int cpt = 0;
		boolean tropAtt = false;
		
		for (Attribut att : classe.getListOrdonneeAttribut())
		{
			if (this.ctrl.estClasseProjet(att.getType())) continue;
			if (cpt >= 3 && ! classe.estClique())
			{
				tropAtt = true;
				break;
			}
			String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
			maxLargeurGaucheAtt = Math.max(maxLargeurGaucheAtt, metrics.stringWidth(gauche));
			lstAtt.add(att);
			cpt++;
		}

		List<Methode> lstMeth = new ArrayList<Methode>();
		int maxLargeurGaucheMeth = 0;
		cpt = 0;
		boolean tropMeth = false;
		
		for (Methode meth : classe.getListOrdonneeMethode())
		{
			if (meth.getNom().equals("main")) continue;
			if (cpt >= 3 && ! classe.estClique() )
			{
				tropMeth = true;
				break;
			}
			String gauche = this.getDebutSignatureMethode(meth);
			maxLargeurGaucheMeth = Math.max(maxLargeurGaucheMeth, metrics.stringWidth(gauche));
			lstMeth.add(meth);
			cpt++;
		}

		// Calcul des dimensions
		List<String> strAtts = new ArrayList<String>();
		for (Attribut a : lstAtt) strAtts.add(getSignatureAttributAlignee(a, maxLargeurGaucheAtt, metrics));
		List<String> strMeths = new ArrayList<String>();
		for (Methode m : lstMeth) strMeths.add(getSignatureMethodeAlignee(m, maxLargeurGaucheMeth, metrics));
		
		int largTitre = this.calculerLargeurTitre(classe, metrics);
		int larAtt = this.calculerLargeurMax(strAtts, metrics);
		int largMeth = this.calculerLargeurMax(strMeths, metrics);
		int largeurRect = Math.max(largTitre, Math.max(larAtt, largMeth)) + (PADDING_X * 2);
		int hTitre = this.calculerHauteurTitre(classe, metrics.getHeight());
		int hAtt = this.calculerHauteurBloc(lstAtt.size(), metrics.getHeight(), tropAtt);
		int hMeth = this.calculerHauteurBloc(lstMeth.size(), metrics.getHeight(), tropMeth);
		int hauteurTotale = hTitre + hAtt + hMeth;
		
		// Dessin des blocs
		this.dessinerFondBloc(g2, x, y, largeurRect, hTitre);
		this.dessinerContenuTitre(g2, classe, x, y + PADDING_Y, largeurRect, metrics.getHeight());
		this.dessinerFondBloc(g2, x, y + hTitre, largeurRect, hAtt);
		this.dessinerContenuListe(g2, strAtts, lstAtt, x, y + hTitre + PADDING_Y, metrics, tropAtt);
		this.dessinerFondBloc(g2, x, y + hTitre + hAtt, largeurRect, hMeth);
		this.dessinerContenuListe(g2, strMeths, lstMeth, x, y + hTitre + hAtt + PADDING_Y, metrics, tropMeth);
		
		// Contour global
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeurRect, hauteurTotale);
		
		// Mise à jour du rectangle
		rect.setTailleX(largeurRect);
		rect.setTailleY(hauteurTotale);
	}
	
	// =========================================================================
	// GESTION DES LIAISONS ET CHEMINS
	// =========================================================================
	
	/**
	 * Reconstruit les chemins à partir de la map de positions fournie.
	 */
	private void reconstruireChemins()
	{
		this.lstChemins.clear();
		for (Liaison l : this.lstLiaisons)
		{
			Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());
			if (r1 != null && r2 != null)
			{
				int x1 = r1.getCentreX();
				int y1 = r1.getCentreY();
				Point p1 = new Point(x1, y1);
				int x2 = r2.getCentreX();
				int y2 = r2.getCentreY();
				Point p2 = new Point(x2, y2);
				Chemin chemin = new Chemin(p1, p2, l.getType(), this.mapClasseRectangle, l.getFromClass(), l.getToClass());
				char zone = this.getZone(r1, r2);
				char zoneInv = zoneInverse(zone);
				chemin.setZoneArrivee(zoneInv);
				r1.addPos(zone, chemin);
				r2.addPos(zoneInv, chemin);
				r1.repartirPointsLiaison(zone);
				r2.repartirPointsLiaison(zoneInv);
				this.lstChemins.add(chemin);
			}
		}
		this.recalculerChemins();
	}

	/**
	 * Recalcule les chemins pour éviter les chevauchements.
	 */
	public void recalculerChemins()
	{
		for (Rectangle rect : this.mapClasseRectangle.values()) rect.nettoyerLiaisons();

		this.lstChemins.clear();
		HashMap<String, List<Chemin>> mapGroupes = new HashMap<>();
		
		for (Liaison l : this.lstLiaisons)
		{
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
				String nom1 = l.getFromClass().getNom();
				String nom2 = l.getToClass().getNom();
				String cle;
				if (nom1.compareTo(nom2) < 0)
				{
					cle = nom1 + "-" + nom2;
				}
				else 
				{
					cle = nom2 + "-" + nom1;
				}

				if (!mapGroupes.containsKey(cle))
					mapGroupes.put(cle, new ArrayList<Chemin>());

				mapGroupes.get(cle).add(chemin);
				this.lstChemins.add(chemin);
			}
		}

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
	 * Initialise les positions des rectangles de classes.
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
	 * Détermine les positions finales des rectangles en évitant les chevauchements.
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
	// GESTION DES CLICS SUR MULTIPLICITÉS
	// =========================================================================
	
	/**
	 * Gère le clic sur une multiplicité pour l'édition.
	 *
	 * @param e L'événement souris.
	 */
	private void handleMultipliciteClick(MouseEvent e)
	{
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		FontMetrics fm = getFontMetrics(font);
		Point p = new Point((int)e.getPoint().getX(), (int) e.getPoint().getY());
		int numLiaisons = Math.min(this.lstLiaisons.size(), this.lstChemins.size());
		
		for (int i = 0; i < numLiaisons; i++)
		{
			Liaison l = this.lstLiaisons.get(i);
			Chemin c = this.lstChemins.get(i);
			String multFrom = l.getFromMultiplicity().getBorneInf() + "." + l.getFromMultiplicity().getBorneSup();
			String multTo = l.getToMultiplicity().getBorneInf() + "." + l.getToMultiplicity().getBorneSup();
			if (multFrom.equals(".")) multFrom = "";
			if (multTo.equals(".")) multTo = "";
			if (multFrom.equals("1.1")) multFrom = "1";
			if (multTo.equals("1.1")) multTo = "1";
			if (this.dessinerMultiplicite.checkClick(p, c, multFrom, multTo, fm))
			{
				Multiplicite multiplicite = this.dessinerMultiplicite.isDepart() ? l.getFromMultiplicity() : l.getToMultiplicity();
				editMultiplicite(multiplicite);
				this.repaint();
				return;
			}
		}
	}

	/**
	 * Affiche une popup pour éditer une multiplicité.
	 *
	 * @param multiplicite La multiplicité à éditer.
	 */
	private void editMultiplicite(Multiplicite multiplicite)
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
		
		layout.putConstraint(layout.WEST, labelInf, 5, layout.WEST, panel);
		layout.putConstraint(layout.NORTH, labelInf, 5, layout.NORTH, panel);
		layout.putConstraint(layout.WEST, fieldInf, 5, layout.EAST, labelInf);
		layout.putConstraint(layout.NORTH, fieldInf, 5, layout.NORTH, panel);
		layout.putConstraint(layout.WEST, labelSup, 5, layout.WEST, panel);
		layout.putConstraint(layout.NORTH, labelSup, 5, layout.SOUTH, fieldInf);
		layout.putConstraint(layout.WEST, fieldSup, 5, layout.EAST, labelSup);
		layout.putConstraint(layout.NORTH, fieldSup, 5, layout.SOUTH, fieldInf);
		
		int result = JOptionPane.showConfirmDialog(this.frame, panel, "Éditer Multiplicité", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
		{
			multiplicite.setBorneInf(fieldInf.getText());
			multiplicite.setBorneSup(fieldSup.getText());
			this.repaint();
		}
	}

	// =========================================================================
	// OUTILS DE DESSIN
	// =========================================================================
	
	/**
	 * Dessine le fond d'un bloc.
	 *
	 * @param g2 Le contexte graphique.
	 * @param x Coordonnée X.
	 * @param y Coordonnée Y.
	 * @param largeur Largeur du bloc.
	 * @param h Hauteur du bloc.
	 */
	private void dessinerFondBloc(Graphics2D g2, int x, int y, int largeur, int h)
	{
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, largeur, h);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeur, h);
		g2.setColor(Color.BLACK);
	}
	
	/**
	 * Dessine le contenu du titre d'une classe.
	 *
	 * @param g2 Le contexte graphique.
	 * @param classe La classe.
	 * @param x Coordonnée X.
	 * @param y Coordonnée Y.
	 * @param largeur Largeur disponible.
	 * @param hLigne Hauteur de ligne.
	 */
	private void dessinerContenuTitre(Graphics2D g2, Classe classe, int x, int y, int largeur, int hLigne)
	{
		Font fontNormal = g2.getFont();
		Font fontGras = fontNormal.deriveFont(Font.BOLD);
		g2.setFont(fontGras);
		dessinerStringCentre(g2, classe.getNom(), x, y, largeur);
		y += hLigne;
		g2.setFont(fontNormal);
		if (classe.isInterface())
		{
			dessinerStringCentre(g2, "<<Interface>>", x, y, largeur);
			y += hLigne;
		}
		
		if (classe.isAbstract() && !classe.isInterface())
		{
			dessinerStringCentre(g2, "<<Abstract>>", x, y, largeur);
			y += hLigne;
		}
	}

	/**
	 * Dessine le contenu d'une liste (attributs ou méthodes).
	 *
	 * @param g2 Le contexte graphique.
	 * @param textes Les textes alignés.
	 * @param objets Les objets (Attribut ou Methode).
	 * @param x Coordonnée X.
	 * @param y Coordonnée Y.
	 * @param fm Métriques de police.
	 * @param showPoints Afficher les points de suspension.
	 */
	private void dessinerContenuListe(Graphics2D g2, List<String> textes, List<?> objets, int x, int y, FontMetrics fm, boolean showPoints)
	{
		int hLigne = fm.getHeight();
		for (int i = 0; i < textes.size(); i++)
		{
			String s = textes.get(i);
			Object obj = objets.get(i);
			g2.drawString(s, x + PADDING_X, y + fm.getAscent());
			boolean isStatic = false;
			if (obj instanceof Attribut)
				isStatic = ((Attribut) obj).isStatic();
			
			if (obj instanceof Methode)
				isStatic = ((Methode) obj).isStatic();
			
			if (isStatic)
				souligner(g2, x + PADDING_X, y + fm.getAscent(), fm.stringWidth(s));
			
			y += hLigne + INTERLIGNE;
		}

		if (showPoints)
			g2.drawString("...", x + PADDING_X, y + fm.getAscent());
	}

	/**
	 * Souligne un texte statique.
	 *
	 * @param g2 Le contexte graphique.
	 * @param x Coordonnée X de départ.
	 * @param yBase Base Y.
	 * @param largeur Largeur du texte.
	 */
	private void souligner(Graphics2D g2, int x, int yBase, int largeur)
	{
		g2.drawLine(x, yBase + 2, x + largeur, yBase + 2);
	}
	
	/**
	 * Dessine une chaîne centrée.
	 *
	 * @param g Le contexte graphique.
	 * @param texte Le texte à centrer.
	 * @param x Coordonnée X.
	 * @param y Coordonnée Y.
	 * @param largeurConteneur Largeur du conteneur.
	 */
	private void dessinerStringCentre(Graphics g, String texte, int x, int y, int largeurConteneur)
	{
		FontMetrics metrics = g.getFontMetrics();
		int xCentre = x + (largeurConteneur - metrics.stringWidth(texte)) / 2;
		g.drawString(texte, xCentre, y + metrics.getAscent());
	}
	
	// =========================================================================
	// CALCULS DE TEXTE ET ALIGNEMENT
	// =========================================================================
	
	/**
	 * Calcule la largeur du titre.
	 *
	 * @param c La classe.
	 * @param fm Métriques de police.
	 * @return La largeur calculée.
	 */
	private int calculerLargeurTitre(Classe c, FontMetrics fm)
	{
		int w = fm.stringWidth(c.getNom());
		if (c.isInterface() || c.isAbstract())
		{
			w = Math.max(w, fm.stringWidth("<<Interface>>"));
		}
		return w;
	}

	/**
	 * Calcule la largeur maximale d'une liste de chaînes.
	 *
	 * @param lignes Les lignes de texte.
	 * @param fm Métriques de police.
	 * @return La largeur maximale.
	 */
	private int calculerLargeurMax(List<String> lignes, FontMetrics fm)
	{
		int max = 0;
		for (String s : lignes)
			max = Math.max(max, fm.stringWidth(s));
		
		return max;
	}

	/**
	 * Calcule la hauteur du titre.
	 *
	 * @param c La classe.
	 * @param hLigne Hauteur de ligne.
	 * @return La hauteur calculée.
	 */
	private int calculerHauteurTitre(Classe c, int hLigne)
	{
		int h = PADDING_Y * 2 + hLigne;
		if (c.isInterface() || (c.isAbstract() && !c.isInterface()))
			h += hLigne;
		
		return h;
	}

	public void detecterZoneEtOuvrirEdition(Classe classe, Rectangle rect, Point pSouris)
	{
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		FontMetrics metrics = g2.getFontMetrics();
		int hauteurLigne = metrics.getHeight();
		int padding = 5; 

		int hauteurTitre = hauteurLigne + (padding * 2);

		int nbLignesAttributs = 0;
		int cpt = 0;
		
		for (Attribut att : classe.getListOrdonneeAttribut())
		{
			if (this.ctrl.estClasseProjet(att.getType())) continue; 

			if (cpt >= 3) 
			{ 
				nbLignesAttributs++; 
				break; 
			}
			
			nbLignesAttributs++;
			cpt++;
		}

		
		if (nbLignesAttributs == 0) nbLignesAttributs = 1; 

		int hauteurZoneAttributs = (nbLignesAttributs * (hauteurLigne + 2)) + 10; 

		
		int yRelatif = pSouris.getY() - rect.getY();

		if (yRelatif < hauteurTitre) 
		{
			System.out.println("Ouverture édition Titre");
			
			new FrameEdition(this.ctrl, classe, 'C');
		} 
		else if (yRelatif < (hauteurTitre + hauteurZoneAttributs)) 
		{
			
			System.out.println("Ouverture édition Attributs");
			new FrameEdition(this.ctrl, classe, 'A');
		} 
		else 
		{
			
			new FrameEdition(this.ctrl, classe, 'M');
		}
	}

	/**
	 * Calcule la hauteur d'un bloc.
	 *
	 * @param nbLignes Nombre de lignes.
	 * @param hLigne Hauteur de ligne.
	 * @param avecPoints Avec points de suspension.
	 * @return La hauteur calculée.
	 */
	private int calculerHauteurBloc(int nbLignes, int hLigne, boolean avecPoints)
	{
		int nbLignesReelles = avecPoints ? nbLignes + 1 : nbLignes;
		if (nbLignesReelles == 0)
			return PADDING_Y * 2 + INTERLIGNE;
		
		return (PADDING_Y * 2) + (nbLignesReelles * hLigne) + ((nbLignesReelles - 1) * INTERLIGNE);
	}

	/**
	 * Génère le symbole de visibilité.
	 *
	 * @param visibilite La visibilité.
	 * @return Le symbole correspondant.
	 */
	private String getVisibiliteSymbole(String visibilite)
	{
		if (visibilite == null) return " ";
		switch (visibilite)
		{
			case "public": return "+";
			case "private": return "-";
			case "protected": return "#";
			default: return "~";
		}
	}
	
	/**
	 * Génère le début de la signature d'une méthode.
	 *
	 * @param meth La méthode.
	 * @return La signature partielle.
	 */
	private String getDebutSignatureMethode(Methode meth)
	{
		String s = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
		Classe classe = this.ctrl.getClasseAvecMeth(meth);
		List<Parametre> params = meth.getLstParam();
		for (int i = 0; i < params.size(); i++)
		{
			if (i >= 2 && ! classe.estClique() )
				return s + " ...)";

			s += params.get(i).getNom() + " : " + params.get(i).getType();
			if (i < params.size() - 1)
				s += ", ";
			
		}
		return s + ")";
	}

	/**
	 * Génère la signature alignée d'un attribut.
	 *
	 * @param att L'attribut.
	 * @param wGaucheMax Largeur max gauche.
	 * @param fm Métriques de police.
	 * @return La signature alignée.
	 */
	private String getSignatureAttributAlignee(Attribut att, int wGaucheMax, FontMetrics fm)
	{
		String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
		String droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
		return padding(gauche, droite, wGaucheMax, fm);
	}
	
	/**
	 * Génère la signature alignée d'une méthode.
	 *
	 * @param meth La méthode.
	 * @param wGaucheMax Largeur max gauche.
	 * @param fm Métriques de police.
	 * @return La signature alignée.
	 */
	private String getSignatureMethodeAlignee(Methode meth, int wGaucheMax, FontMetrics fm)
	{
		String gauche = getDebutSignatureMethode(meth);
		String droite = "";
		if (!meth.getType().equals("void") && !meth.getType().isEmpty() && !meth.getType().equals(meth.getNom()))
		{
			droite = " : " + meth.getType();
		}

		return padding(gauche, droite, wGaucheMax, fm);
	}

	/**
	 * Ajoute du padding pour aligner.
	 *
	 * @param gauche Partie gauche.
	 * @param droite Partie droite.
	 * @param wMax Largeur max.
	 * @param fm Métriques de police.
	 * @return La chaîne paddée.
	 */
	private String padding(String gauche, String droite, int wMax, FontMetrics fm)
	{
		int wActuel = fm.stringWidth(gauche);
		int espace = Math.max(0, (wMax - wActuel) / fm.stringWidth(" "));
		return gauche + " ".repeat(espace) + droite;
	}
	
	// =========================================================================
	// CALCULS GÉOMÉTRIQUES
	// =========================================================================
	
	/**
	 * Calcule le point de bord pour une liaison.
	 *
	 * @param rect1 Rectangle source.
	 * @param rect2 Rectangle cible.
	 * @return Le point d'intersection.
	 */
	private Point calculerPointBord(Rectangle rect1, Rectangle rect2)
	{
		int cx1 = rect1.getCentreX();
		int cy1 = rect1.getCentreY();
		int cx2 = rect2.getCentreX();
		int cy2 = rect2.getCentreY();
		int dx = cx2 - cx1;
		int dy = cy2 - cy1;
	
		if (Math.abs(dx) == 0 && Math.abs(dy) == 0) return new Point(cx1, cy1);
		
		double largeurMoitie = rect1.getTailleX() / 2.0;
		double hMoitie = rect1.getTailleY() / 2.0;
		
		if (largeurMoitie == 0 || hMoitie == 0) return new Point(cx1, cy1);
		
		double ratioX = Math.abs(dx) / largeurMoitie;
		
		double ratioY = Math.abs(dy) / hMoitie;
		
		if (ratioX > ratioY)
		{
			return dx > 0 ? new Point(rect1.getX() + rect1.getTailleX(), cy1) : new Point(rect1.getX(), cy1);
		}
		else
		{
			return dy > 0 ? new Point(cx1, rect1.getY() + rect1.getTailleY()) : new Point(cx1, rect1.getY());
		}
	}

	/**
	 * Détermine la zone de sortie d'une liaison.
	 *
	 * @param source Rectangle source.
	 * @param target Rectangle cible.
	 * @return La zone ('H', 'B', 'G', 'D').
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
	 * Inverse une zone.
	 *
	 * @param zone La zone à inverser.
	 * @return La zone inversée.
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


