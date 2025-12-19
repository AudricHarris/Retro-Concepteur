package retroconcepteur.vue.dessin;

import retroconcepteur.Controleur;
import retroconcepteur.metier.classe.*;
import retroconcepteur.vue.outil.Rectangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire responsable du rendu graphique d'une classe UML.
 * Elle gère le dessin du rectangle, du titre, des attributs et des méthodes,
 * ainsi que le calcul dynamique des dimensions.
 *
 * @author [Equipe 9]
 * @version 2.0
 */
public class DessinerClasse
{
	private final int MARGE_X    = 10;
	private final int MARGE_Y    = 5;
	private final int INTERLIGNE = 2;

	private Controleur ctrl;

	/**
	 * Constructeur.
	 * @param ctrl Le contrôleur pour accéder aux informations du projet (types, liaisons).
	 */
	public DessinerClasse(Controleur ctrl)
	{
		this.ctrl = ctrl;
	}

	/*------------------------------------------*/
	/* Méthode d'instance                       */
	/*------------------------------------------*/

	/**
	 * Dessine une classe UML complète dans son rectangle associé.
	 * Met à jour la taille du rectangle en fonction du contenu.
	 *
	 * @param g2     Le contexte graphique Java AWT.
	 * @param classe La classe métier à afficher.
	 * @param rect   Le rectangle graphique associé (contenant les coordonnées X, Y).
	 */
	public void dessiner(Graphics2D g2, Classe classe, Rectangle rect)
	{
		FontMetrics    metrics;
		int            x, y, cpt, maxLargeurGaucheAtt;
		int            maxLargeurGaucheMeth, largeurRect, hauteurTotale;
		int            largTitre, larAtt, largMeth;
		int            hTitre, hAtt, hMeth;
		boolean        tropAtt, tropMeth;
		List<Attribut> lstAtt;
		List<Methode>  lstMeth;
		List<String>   strAtts, strMeths;
		String         gauche;

		if (rect == null) return;

		metrics = g2.getFontMetrics();
		x       = rect.getX();
		y       = rect.getY();

		// Filtrage des Attributs
		lstAtt              = new ArrayList<>();
		maxLargeurGaucheAtt = 0;
		cpt                 = 0;
		tropAtt             = false;

		for (Attribut att : classe.getListOrdonneeAttribut())
		{
			if (this.ctrl.estClasseProjet(att.getType()))
				continue;
			if (cpt >= 3 && !classe.estClique())
			{
				tropAtt = true;
				break;
			}
			gauche              = this.determineVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
			maxLargeurGaucheAtt = Math.max(maxLargeurGaucheAtt, metrics.stringWidth(gauche));
			lstAtt.add(att);
			cpt++;
		}

		lstMeth              = new ArrayList<>();
		maxLargeurGaucheMeth = 0;
		cpt                  = 0;
		tropMeth             = false;

		for (Methode meth : classe.getListOrdonneeMethode())
		{
			if (meth.getNom().equals("main")) continue; 
			
			if (cpt >= 3 && !classe.estClique())
			{
				tropMeth = true;
				break;
			}
			gauche               = this.determineDebutSignatureMethode(meth);
			maxLargeurGaucheMeth = Math.max(maxLargeurGaucheMeth, metrics.stringWidth(gauche));
			lstMeth.add(meth);
			cpt++;
		}
		
		strAtts = new ArrayList<>();
		for (Attribut a : lstAtt) strAtts.add(determineSignatureAttributAlignee(a, maxLargeurGaucheAtt, metrics));
		
		strMeths = new ArrayList<>();
		for (Methode m : lstMeth) strMeths.add(determineSignatureMethodeAlignee(m, maxLargeurGaucheMeth, metrics));

		// Calcul des Dimensions
		largTitre     = this.calculerLargeurTitre(classe, metrics);
		larAtt        = this.calculerLargeurMax(strAtts, metrics);
		largMeth      = this.calculerLargeurMax(strMeths, metrics);
		
		largeurRect   = Math.max(largTitre, Math.max(larAtt, largMeth)) + (MARGE_X * 2);
		
		hTitre        = this.calculerHauteurTitre(classe, metrics.getHeight());
		hAtt          = this.calculerHauteurBloc(lstAtt.size(), metrics.getHeight(), tropAtt);
		hMeth         = this.calculerHauteurBloc(lstMeth.size(), metrics.getHeight(), tropMeth);
		
		hauteurTotale = hTitre + hAtt + hMeth;

		// Dessin des blocs
		this.dessinerFondBloc(g2, x, y, largeurRect, hTitre);
		this.dessinerContenuTitre(g2, classe, x, y + MARGE_Y, largeurRect, metrics.getHeight());
		
		this.dessinerFondBloc(g2, x, y + hTitre, largeurRect, hAtt);
		this.dessinerContenuListe(g2, strAtts, lstAtt, x, y + hTitre + MARGE_Y, metrics, tropAtt);
		
		this.dessinerFondBloc(g2, x, y + hTitre + hAtt, largeurRect, hMeth);
		this.dessinerContenuListe(g2, strMeths, lstMeth, x, y + hTitre + hAtt + MARGE_Y, metrics, tropMeth);

		// Contour global final
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeurRect, hauteurTotale);


		rect.setTailleX(largeurRect);
		rect.setTailleY(hauteurTotale);
	}

	/*------------------------------------------*/
	/* Méthodes privées                         */
	/*------------------------------------------*/

	/**
	 * Dessine le rectangle de fond blanc et sa bordure noire.
	 */
	private void dessinerFondBloc(Graphics2D g2, int x, int y, int largeur, int h)
	{
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, largeur, h);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeur, h);
	}

	/**
	 * Affiche le nom de la classe et ses stéréotypes
	 */
	private void dessinerContenuTitre(Graphics2D g2, Classe classe, int x, int y, int largeur, int hLigne)
	{
		Font fontNormal, fontGras;

		fontNormal = g2.getFont();
		fontGras   = fontNormal.deriveFont(Font.BOLD);
		
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
		}
	}

	/**
	 * Affiche une liste de textes ligne par ligne.
	 * Gère le soulignement pour les éléments statiques.
	 */
	private void dessinerContenuListe(Graphics2D g2, List<String> textes, List<?> lst, int x, int y, FontMetrics fm, boolean showPoints)
	{
		int     hLigne;
		String  s;
		Object  obj;
		boolean isStatic;

		hLigne = fm.getHeight();
		
		for (int i = 0; i < textes.size(); i++)
		{
			s   = textes.get(i);
			obj = lst.get(i);
			
			g2.drawString(s, x + MARGE_X, y + fm.getAscent());
			
			isStatic = false;
			if (obj instanceof Attribut) isStatic = ((Attribut) obj).isStatic();
			if (obj instanceof Methode)  isStatic = ((Methode) obj).isStatic();

			if (isStatic)
			{
				this.souligner(g2, x + MARGE_X, y + fm.getAscent(), fm.stringWidth(s));
			}

			y += hLigne + INTERLIGNE;
		}

		if (showPoints)
		{
			g2.drawString("...", x + MARGE_X, y + fm.getAscent());
		}
	}

	private void souligner(Graphics2D g2, int x, int yBase, int largeur)
	{
		g2.drawLine(x, yBase + 2, x + largeur, yBase + 2);
	}

	/**
	 * Dessine une chaine de texte centrée horizontalement dans un conteneur.
	 */
	private void dessinerStringCentre(Graphics g, String texte, int x, int y, int largeurConteneur)
	{
		FontMetrics metrics;
		int         xCentre;

		metrics = g.getFontMetrics();
		xCentre = x + (largeurConteneur - metrics.stringWidth(texte)) / 2;
		
		g.drawString(texte, xCentre, y + metrics.getAscent());
	}




	/**
	 * Calcule la largeur nécessaire pour afficher le titre de la classe.
	 */
	private int calculerLargeurTitre(Classe c, FontMetrics fm)
	{
		int largeur;

		largeur = fm.stringWidth(c.getNom());
		
		if (c.isInterface() || c.isAbstract())
		{
			largeur = Math.max(largeur, fm.stringWidth("<<Interface>>"));
		}
		return largeur;
	}


	private int calculerLargeurMax(List<String> lignes, FontMetrics fm)
	{
		int max = 0;
		for (String s : lignes) max = Math.max(max, fm.stringWidth(s));
		return max;
	}


	private int calculerHauteurTitre(Classe c, int hLigne)
	{
		int h = MARGE_Y * 2 + hLigne;
		if (c.isInterface() || (c.isAbstract() && !c.isInterface()))
			h += hLigne;
		return h;
	}

	private int calculerHauteurBloc(int nbLignes, int hLigne, boolean avecPoints)
	{
		int nbLignesReelles = avecPoints ? nbLignes + 1 : nbLignes;
		
		if (nbLignesReelles == 0) 
			return MARGE_Y * 2 + INTERLIGNE;
		
		return (MARGE_Y * 2) + (nbLignesReelles * hLigne) + ((nbLignesReelles - 1) * INTERLIGNE);
	}

	private String determineVisibiliteSymbole(String visibilite)
	{
		if (visibilite == null) return " ";
		switch (visibilite)
		{
			case "public":    return "+";
			case "private":   return "-";
			case "protected": return "#";
			default:          return "~";
		}
	}


	private String determineDebutSignatureMethode(Methode meth)
	{
		Classe          classe;
		List<Parametre> params;
		String          s;

		s      = this.determineVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
		classe = this.ctrl.getClasseAvecMeth(meth);
		params = meth.getLstParam();
		
		for (int i = 0; i < params.size(); i++)
		{
			if (i >= 2 && !classe.estClique()) 
				return s + " ...)";

			s += params.get(i).getNom() + " : " + params.get(i).getType();
			if (i < params.size() - 1) 
				s += ", ";
		}
		return s + ")";
	}

	/**
	 * Construit la signature alignée d'un attribut (avec espaces pour aligner les colonnes).
	 */
	private String determineSignatureAttributAlignee(Attribut att, int wGaucheMax, FontMetrics fm)
	{
		String gauche, droite;

		gauche = this.determineVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();

		droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
		
		return this.marge(gauche, droite, wGaucheMax, fm);
	}

	/**
	 * Construit la signature alignée d'une méthode (avec espaces pour aligner les colonnes).
	 */
	private String determineSignatureMethodeAlignee(Methode meth, int wGaucheMax, FontMetrics fm)
	{
		String gauche, droite;

		gauche = this.determineDebutSignatureMethode(meth);
		droite = "";
		
		if (!meth.getType().equals("void") && !meth.getType().isEmpty() && !meth.getType().equals(meth.getNom()))
			droite = " : " + meth.getType();

		return this.marge(gauche, droite, wGaucheMax, fm);
	}

	/**
	 * Ajoute des espaces entre la partie gauche (nom) et droite (type) pour aligner les deux colonnes.
	 */
	private String marge(String gauche, String droite, int largeurMax, FontMetrics fm)
	{
		int largeurActuel, espace;

		largeurActuel = fm.stringWidth(gauche);
		espace        = Math.max(0, (largeurMax - largeurActuel) / fm.stringWidth(" "));
		
		return gauche + " ".repeat(espace) + droite;
	}
}