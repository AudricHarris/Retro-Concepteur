package RetroConcepteur.vue;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;
import java.awt.AlphaComposite;
import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;
import java.awt.Panel;

public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;
	private List<Classe> lstClasse;
	private HashMap<Classe, Rectangle> mapClasseRectangle;

	// Marges et interligne du rectangle de l'uml
	private final int MARGE = 10; 
	private final int INTERLIGNE = 5;

	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;

		this.lstClasse = this.ctrl.getLstClasses();
		this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
		
		// On définit une grande zone pour permettre le scroll si besoin
		this.setPreferredSize(new Dimension(2000, 2000));
		this.initialiserPositions();

		this.repaint();
		this.setVisible(true);
	}
	
	/**
	 * Initialise les positions de départ des classes (grille simple)
	 */
	private void initialiserPositions()
	{
		int x = 50;
		int y = 50;
		
		for (Classe c : this.lstClasse) 
		{
			Rectangle rect = new Rectangle(x, y, 0, 0); 
			this.mapClasseRectangle.put(c, rect);
			
			x += 500; 
			if (x > 1000) 
			{ 
				x = 50; 
				y += 350; 
			}
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
			ArrayList<String> strAttributs = new ArrayList<>();
			for (Attribut att : classe.getLstAttribut()) 
			{
				// On ignore les attributs "Liaison" (si c'est une classe du projet)
				if (this.ctrl.estClasseProjet(att.getType())) continue;

				s = this.getSignatureAttribut(att);
				strAttributs.add(s);

				largeur = metrics.stringWidth(s);
				if (largeur > largeurAttributsMax) 
					largeurAttributsMax = largeur;
			}

			// Calcul largeur méthodes
			int largeurMethodesMax = 0;
			ArrayList<String> strMethodes = new ArrayList<>();
			for (Methode meth : classe.getLstMethode()) 
			{
				s = this.getSignatureMethode(meth);
				strMethodes.add(s);
				largeur = metrics.stringWidth(s);

				if (largeur > largeurMethodesMax) 
					largeurMethodesMax = largeur ;
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
			for (Attribut att : classe.getLstAttribut())
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
			for (Methode meth : classe.getLstMethode()) 
			{
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
	}

	// --- Méthodes utilitaires pour générer les chaînes ---

	private String getSignatureAttribut(Attribut att) 
	{
		String s = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom() + " : " + att.getType();
		if (att.isConstante()) s += " {freeze}";
		return s;
	}

	private String getSignatureMethode(Methode meth) 
	{
		if (meth.getNom().equals("main")) return ""; 

		String sRet = "";
		sRet += getVisibiliteSymbole(meth.getVisibilite()) + " ";
		sRet += meth.getNom() + "(";
		
		List<Parametre> params = meth.getLstParam();
		for (int i = 0; i < params.size(); i++) 
		{
			sRet += params.get(i).getNom() + " : " + params.get(i).getType();
			if (i < params.size() - 1) 
				sRet += ", ";
		}
		sRet += ")";
		
		if (!meth.getType().equals("void") && !meth.getType().isEmpty()) 
			sRet += " : " + meth.getType();
		
		
		return sRet;
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
}