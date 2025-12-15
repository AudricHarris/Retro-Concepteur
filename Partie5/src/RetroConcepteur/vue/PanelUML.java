package RetroConcepteur.vue;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.awt.AlphaComposite;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;

import java.awt.Panel;
import java.awt.Toolkit;

public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;
	private List<Classe> lstClasse;
	private HashMap<Classe, Rectangle> mapClasseRectangle;
	private List<Liaison> lstLiaisons;
	private List<Arc>  lstArcs;

	private DessinerFleche dessinerFleche;

	// Marges et interligne du rectangle de l'uml
	private final int MARGE = 10; 
	private final int INTERLIGNE = 5;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		this.dessinerFleche = new DessinerFleche();

		this.lstClasse = this.ctrl.getLstClasses();
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaison());
		this.lstArcs = new ArrayList<Arc>();
		
		// On définit une grande zone pour permettre le scroll si besoin
		this.setPreferredSize(new Dimension(2000, 2000));	
		this.initialiserPositions();

		GereSouris gs = new GereSouris(this);
		this.addMouseListener      (gs);
		this.addMouseMotionListener(gs);

		this.repaint();
		this.setVisible(true);
	}

	public void reinitialiser()
	{
		this.lstClasse = this.ctrl.getLstClasses();
		this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaison());
		this.mapClasseRectangle.clear();
		this.lstArcs.clear();
		this.reinitialiser();
		this.initialiserPositions();
		this.repaint();
	}
	
	/**
	 * Initialise les positions de départ des classes (grille simple)
	 */
	private void initialiserPositions()
	{
		int x = 50;
		int y = 50;	
		int yMax = 0;

		// for ( Classe c : this.lstClasse )
		// {
		// 	Rectangle rect = new Rectangle(0, 0, 0,0);
		// 	this.mapClasseRectangle.put(c, rect);
		// }

		//this.repaint();

		for (Classe c : this.lstClasse) 
		{
			Rectangle rect = new Rectangle(x, y, 0, 0);
			this.mapClasseRectangle.put(c, rect);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();			

			x += rect.getX() + 50;

			if ( yMax < rect.getTailleY() ) yMax = rect.getTailleY();

			if (x+rect.getTailleX() > screenSize.width) 
			{ 
				x = 50;
				y += 350;
			}
		}
		
		for (Liaison l : this.lstLiaisons) 
		{
			String classe1 = l.getFromClass().getNom();
			String classe2 = l.getToClass().getNom();
			//HashMap<Point,Point> p = this.determinerPosLibre(this.mapClasseRectangle.get(classe1), this.mapClasseRectangle.get(classe2));
			//Arc arc = new Arc ();

		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// Configuration de la police
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		Font fontGras = new Font("SansSerif", Font.BOLD, 12);
		

		
		g2.setFont(font); 
		FontMetrics metrics = g2.getFontMetrics();
		int hauteurTexte    = metrics.getHeight();
		int largeur,yDepartGrosRect, yDepartTmp;
		String s;
		int hauteurRect;

		for (Classe classe : this.lstClasse)
		{
			Rectangle rect = this.mapClasseRectangle.get(classe);
			int x = rect.getX();
			yDepartGrosRect = rect.getY();
			largeur = 0;
			
			// --- ETAPE 1 : CALCUL DES LARGEURS ---
			
			// Calcul largeur titre
			int largeurTitre = metrics.stringWidth(classe.getNom());
			if (classe.isInterface() || classe.isAbstract()) 
				largeurTitre = Math.max(largeurTitre, metrics.stringWidth("<<Interface>>"));
			

			// Calcul largeur attributs
			int largeurAttributsMax = 0;
			int largeurAvantDeuxPointsAtt = 0;
			ArrayList<String> strAttributs = new ArrayList<>();
			for (Attribut att : classe.getListOrdonneeAttribut()) 
			{
				// On ignore les attributs Liaison
				if (this.ctrl.estClasseProjet(att.getType())) continue;

				String avant = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
				int largeurAvant = metrics.stringWidth(avant);
				
				if (largeurAvant > largeurAvantDeuxPointsAtt)
					largeurAvantDeuxPointsAtt = largeurAvant;
			}

			for (Attribut att : classe.getListOrdonneeAttribut()) 
			{
				if (this.ctrl.estClasseProjet(att.getType())) continue;
				
				s = this.getSignatureAttribut(att, largeurAvantDeuxPointsAtt, metrics);
				strAttributs.add(s);
				largeur = metrics.stringWidth(s);

				if (largeur > largeurAttributsMax) 
					largeurAttributsMax = largeur ;
			}

			 
			int largeurMethodesMax = 0;
			int largeurAvantDeuxPointsMeth = 0; 
			ArrayList<String> strMethodes = new ArrayList<>();
			
			for (Methode meth : classe.getListOrdonneeMethode()) 
			{
				if (meth.getNom().equals("main")) continue;
				
				String avant = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
				List<Parametre> params = meth.getLstParam();
				for (int i = 0; i < params.size(); i++) 
				{
					avant += params.get(i).getNom() + " : " + params.get(i).getType();
					if (i < params.size() - 1) 
						avant += ", ";
				}
				avant += ")";
				
				int largeurAvant = metrics.stringWidth(avant);
				if (largeurAvant > largeurAvantDeuxPointsMeth)
					largeurAvantDeuxPointsMeth = largeurAvant;
			}
			
			for (Methode meth : classe.getListOrdonneeMethode()) 
			{
				s = this.getSignatureMethode(meth, largeurAvantDeuxPointsMeth, metrics);
				if (!s.isEmpty()) 
				{
					strMethodes.add(s);
					largeur = metrics.stringWidth(s);

					if (largeur > largeurMethodesMax) 
						largeurMethodesMax = largeur ;
				}
			}

			int largeurRect = Math.max(largeurTitre, Math.max(largeurAttributsMax, largeurMethodesMax)) + (MARGE * 2);
			
			

			int yCourant = yDepartTmp = yDepartGrosRect;

			// --- Calcul et dessin de la section titre ---
			
			yDepartTmp = yCourant;
			yCourant += MARGE;
			
			if (classe.isInterface()) 
				yCourant += hauteurTexte;

			if (classe.isAbstract() && !classe.isInterface()) 
				yCourant += hauteurTexte;

			yCourant += hauteurTexte + INTERLIGNE;

			hauteurRect = yCourant - yDepartTmp;
			this.RemplirRect(g2, x, yDepartTmp, largeurRect, hauteurRect, Color.gray);
			
			g2.setColor(Color.BLACK);
			yCourant = yDepartTmp + MARGE;
			
			if (classe.isInterface()) 
			{
				dessinerStringCentre(g2, "<<Interface>>", x, yCourant, largeurRect);
				yCourant += hauteurTexte;
			}

			if (classe.isAbstract() && !classe.isInterface()) 
			{
				dessinerStringCentre(g2, "<<Abstract>>", x, yCourant, largeurRect);
				yCourant += hauteurTexte;
			}

			g2.setFont(fontGras);
			dessinerStringCentre(g2, classe.getNom(), x, yCourant, largeurRect);
			g2.setFont(font);

			yCourant += hauteurTexte + INTERLIGNE;
			
			// --- Calcul et dessin de la section ATTRIBUTS ---
			
			// Calcul de la hauteur des attributs
			yDepartTmp = yCourant;
			yCourant += INTERLIGNE;
			
			int nbAttributsVisibles = 0;
			for (Attribut att : classe.getLstAttribut())
				if (!this.ctrl.estClasseProjet(att.getType()))
					nbAttributsVisibles++;
					
			yCourant += nbAttributsVisibles * hauteurTexte + INTERLIGNE;

			// Dessin du rectangle attributs 
			hauteurRect = yCourant - yDepartTmp;
			this.RemplirRect(g2, x, yDepartTmp, largeurRect, hauteurRect, Color.lightGray);
			g2.drawRect(x, yDepartTmp, largeurRect, hauteurRect);
			
			// Maintenant on dessine le texte des attributs par-dessus le rectangle
			g2.setColor(Color.BLACK);
			yCourant = yDepartTmp + INTERLIGNE;
			int i = 0;
			for (Attribut att : classe.getListOrdonneeAttribut())
			{
				if (this.ctrl.estClasseProjet(att.getType())) 
					continue;

				s = strAttributs.get(i++);
				g2.drawString(s, x + MARGE, yCourant + metrics.getAscent());
				// metrics.getAscent() Distance verticale entre la ligne de base et le sommet des caractères les plus hauts
				
				// Souligner si statique
				if (att.isStatic()) 
				{
					largeur = metrics.stringWidth(s);
					g2.drawLine(x + MARGE, yCourant + metrics.getAscent() + 2, x + MARGE + largeur, yCourant + metrics.getAscent() + 2);
				}
				yCourant += hauteurTexte;
			}
			yCourant += INTERLIGNE;

			// --- Calcul et dessin de la section METHODES ---
			
			// Calcul de la hauteur des méthodes
			yDepartTmp = yCourant;
			yCourant += INTERLIGNE;
			yCourant += strMethodes.size() * hauteurTexte + MARGE;

			// Dessin du rectangle méthodes 
			hauteurRect = yCourant - yDepartTmp;
			this.RemplirRect(g2, x, yDepartTmp, largeurRect, hauteurRect, Color.WHITE);
			g2.drawRect(x, yDepartTmp, largeurRect, hauteurRect);
			
			// Maintenant on dessine le texte des méthodes par-dessus le rectangle
			g2.setColor(Color.BLACK);
			yCourant = yDepartTmp + INTERLIGNE;
			i = 0;
			for (Methode meth : classe.getListOrdonneeMethode()) 
			{
				if (meth.getNom().equals("main")) continue;
				
				s = strMethodes.get(i++);
				g2.drawString(s, x + MARGE, yCourant + metrics.getAscent());

				// Souligner si statique
				if (meth.isStatic()) 
				{
					int w = metrics.stringWidth(s);
					g2.drawLine(x + MARGE, yCourant + metrics.getAscent() + 2, x + MARGE + w, yCourant + metrics.getAscent() + 2);
				}
				yCourant += hauteurTexte;
			}
			yCourant += MARGE;

			// Rectangle global de la classe
			hauteurRect = yCourant - yDepartGrosRect;
			g2.setColor(Color.BLACK);
			g2.drawRect(x, yDepartGrosRect, largeurRect, hauteurRect);

			// Mise à jour de l'objet métier Rectangle
			rect.setTailleX(largeurRect);
			rect.setTailleY(hauteurRect);

		}

		for (Liaison l : this.ctrl.getListLiaison()) 
		{
		
			// Récupérer les rectangles correspondants
			Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());

			if (r1 != null && r2 != null) 
			{
				// Pour l'instant, on trace de centre à centre (c'est le plus simple)
				// Note : Cela dessinera la flèche SOUS le rectangle d'arrivée si on ne calcule pas l'intersection
				this.dessinerFleche.dessinerLiaison(g2, 
									r1.getCentreX(), r1.getCentreY(), 
									r2.getCentreX(), r2.getCentreY(), 
									"DEPENDANCE"); // Remplacer par l.getType()
			}
		}
	}

	// --- Méthodes utilitaires pour générer les chaînes ---

	private String getSignatureAttribut(Attribut att, int largeurMax, FontMetrics metrics) 
	{
		String freeze = "";
		if (att.isConstante()) freeze = " {freeze}";
		
		String avant = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
		int largeurAvant = metrics.stringWidth(avant);
		int espacesNecessaires = largeurMax - largeurAvant;
		
		// Calculer le nombre d'espaces à ajouter
		int nbEspaces = espacesNecessaires / metrics.stringWidth(" ");
		String espaces = " ".repeat(Math.max(0, nbEspaces));
		
		return avant + espaces + " : " + att.getType() + freeze;
	}

	private String getSignatureMethode(Methode meth, int largeurMax, FontMetrics metrics) 
	{
		if (meth.getNom().equals("main")) return ""; 

		String avant = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
		
		List<Parametre> params = meth.getLstParam();
		for (int i = 0; i < params.size(); i++) 
		{
			avant += params.get(i).getNom() + " : " + params.get(i).getType();
			if (i < params.size() - 1) 
				avant += ", ";
		}
		avant += ")";
		
		if (meth.getType().equals("void") || meth.getType().isEmpty() || meth.getType().equals(meth.getNom())) 
			return avant;
		
		// Calculer l'espacement pour aligner les types de retour
		int largeurAvant = metrics.stringWidth(avant);
		int espacesNecessaires = largeurMax - largeurAvant;
		int nbEspaces = espacesNecessaires / metrics.stringWidth(" ");
		String espaces = " ".repeat(Math.max(0, nbEspaces));
		
		return avant + espaces + "   : " + meth.getType();
	}

	public HashMap<Classe,Rectangle> getMap()
	{
		return this.mapClasseRectangle;
	}


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

	private void RemplirRect(Graphics2D g2, int x, int y, int largeur, int hauteur, Color couleur)
	{
		Color couleurOriginale = g2.getColor();
		g2.setColor(couleur);
		g2.fillRect(x, y, largeur, hauteur);
		g2.setColor(couleurOriginale);
	}

	private void dessinerStringCentre(Graphics g, String texte, int x, int y, int largeur) 
	{
		FontMetrics metrics = g.getFontMetrics();
		int xCentre = x + (largeur - metrics.stringWidth(texte)) / 2;
		// metrics.getAscent() Distance verticale entre la ligne de base et le sommet des caractères les plus hauts
		g.drawString(texte, xCentre, y + metrics.getAscent());
	}

	// public HashMap<Point,Point> determinerPosLibre(Rectangle source, Rectangle target) 
	// {
	// 	char zone = ' ';
	// 	int nbPoints = 0;
	// 	double cx1 = source.getCentreX();
	// 	double cy1 = source.getCentreY();
		
	// 	double cx2 = target.getCentreX();
	// 	double cy2 = target.getCentreY();

	// 	double dx = cx2 - cx1;
	// 	double dy = cy2 - cy1;


	// 	double xNormalized = dx / source.getTailleX();
	// 	double yNormalized = dy / source.getTailleY();

	// 	if (Math.abs(yNormalized) > Math.abs(xNormalized)) 
	// 	{
	// 		if (yNormalized < 0) 
	// 			zone = 'H';
	// 		else 
	// 			zone ='B';
	// 	} 
		
	// 	else 
	// 	{
	// 		if (xNormalized < 0)
	// 			zone = 'G';
	// 		else
	// 			zone = 'D';
	// 	}

	// }

	private void repartirPointsLiaison( char zone) 
	{
		// À implémenter plus tard
	}
}