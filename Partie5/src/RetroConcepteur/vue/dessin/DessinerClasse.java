package RetroConcepteur.vue.dessin;

import RetroConcepteur.Controleur;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.Rectangle;

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
 */
public class DessinerClasse
{
	private Controleur ctrl;

	// Constantes de style (Marges et espacements)
	private final int MARGE_X = 10;
	private final int MARGE_Y = 5;
	private final int INTERLIGNE = 2;

	/**
	 * Constructeur.
	 * @param ctrl Le contrôleur pour accéder aux informations du projet (types, liaisons).
	 */
	public DessinerClasse(Controleur ctrl)
	{
		this.ctrl = ctrl;
	}

	/**
	 * Dessine une classe UML complète dans son rectangle associé.
	 * Met à jour la taille du rectangle en fonction du contenu.
	 *
	 * @param g2 Le contexte graphique Java AWT.
	 * @param classe La classe métier à afficher.
	 * @param rect Le rectangle graphique associé (contenant les coordonnées X, Y).
	 */
	public void dessiner(Graphics2D g2, Classe classe, Rectangle rect)
	{
		if (rect == null) return;

		FontMetrics metrics = g2.getFontMetrics();
		int x = rect.getX();
		int y = rect.getY();

		// --- 1. Préparation et filtrage des données à afficher ---

		// Filtrage des attributs (on cache ceux qui sont des liaisons vers d'autres classes du projet)
		List<Attribut> lstAtt = new ArrayList<>();
		int maxLargeurGaucheAtt = 0;
		int cpt = 0;
		boolean tropAtt = false;

		for (Attribut att : classe.getListOrdonneeAttribut())
		{
			if (this.ctrl.estClasseProjet(att.getType())) continue; // Ignore les associations
			
			// Limite d'affichage à 3 éléments si non cliqué
			if (cpt >= 3 && !classe.estClique())
			{
				tropAtt = true;
				break;
			}
			String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
			maxLargeurGaucheAtt = Math.max(maxLargeurGaucheAtt, metrics.stringWidth(gauche));
			lstAtt.add(att);
			cpt++;
		}

		// Filtrage des méthodes
		List<Methode> lstMeth = new ArrayList<>();
		int maxLargeurGaucheMeth = 0;
		cpt = 0;
		boolean tropMeth = false;

		for (Methode meth : classe.getListOrdonneeMethode())
		{
			if (meth.getNom().equals("main")) continue; // On cache souvent le main en UML
			
			if (cpt >= 3 && !classe.estClique())
			{
				tropMeth = true;
				break;
			}
			String gauche = this.getDebutSignatureMethode(meth);
			maxLargeurGaucheMeth = Math.max(maxLargeurGaucheMeth, metrics.stringWidth(gauche));
			lstMeth.add(meth);
			cpt++;
		}

		// --- 2. Calcul des dimensions nécessaires ---
		
		List<String> strAtts = new ArrayList<>();
		for (Attribut a : lstAtt) strAtts.add(getSignatureAttributAlignee(a, maxLargeurGaucheAtt, metrics));
		
		List<String> strMeths = new ArrayList<>();
		for (Methode m : lstMeth) strMeths.add(getSignatureMethodeAlignee(m, maxLargeurGaucheMeth, metrics));

		int largTitre = this.calculerLargeurTitre(classe, metrics);
		int larAtt    = this.calculerLargeurMax(strAtts, metrics);
		int largMeth  = this.calculerLargeurMax(strMeths, metrics);
		
		// La largeur finale est la max des 3 sections + les marges
		int largeurRect = Math.max(largTitre, Math.max(larAtt, largMeth)) + (MARGE_X * 2);
		
		int hTitre = this.calculerHauteurTitre(classe, metrics.getHeight());
		int hAtt   = this.calculerHauteurBloc(lstAtt.size(), metrics.getHeight(), tropAtt);
		int hMeth  = this.calculerHauteurBloc(lstMeth.size(), metrics.getHeight(), tropMeth);
		
		int hauteurTotale = hTitre + hAtt + hMeth;

		// --- 3. Dessin des blocs ---
		
		// Bloc Titre
		this.dessinerFondBloc(g2, x, y, largeurRect, hTitre);
		this.dessinerContenuTitre(g2, classe, x, y + MARGE_Y, largeurRect, metrics.getHeight());
		
		// Bloc Attributs
		this.dessinerFondBloc(g2, x, y + hTitre, largeurRect, hAtt);
		this.dessinerContenuListe(g2, strAtts, lstAtt, x, y + hTitre + MARGE_Y, metrics, tropAtt);
		
		// Bloc Méthodes
		this.dessinerFondBloc(g2, x, y + hTitre + hAtt, largeurRect, hMeth);
		this.dessinerContenuListe(g2, strMeths, lstMeth, x, y + hTitre + hAtt + MARGE_Y, metrics, tropMeth);

		// Contour global (pour la propreté)
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeurRect, hauteurTotale);

		// Mise à jour du modèle graphique (Rectangle) pour les clics futurs
		rect.setTailleX(largeurRect);
		rect.setTailleY(hauteurTotale);
	}

	// =========================================================================
	// MÉTHODES UTILITAIRES DE DESSIN ET CALCUL (PRIVÉES)
	// =========================================================================

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
	 * Affiche le nom de la classe et ses stéréotypes (<<Interface>>, <<Abstract>>).
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
		}
	}

	/**
	 * Affiche une liste de textes (attributs ou méthodes) ligne par ligne.
	 * Gère le soulignement pour les éléments statiques.
	 */
	private void dessinerContenuListe(Graphics2D g2, List<String> textes, List<?> objets, int x, int y, FontMetrics fm, boolean showPoints)
	{
		int hLigne = fm.getHeight();
		for (int i = 0; i < textes.size(); i++)
		{
			String s = textes.get(i);
			Object obj = objets.get(i);
			
			g2.drawString(s, x + MARGE_X, y + fm.getAscent());
			
			// Gestion du soulignement pour 'static'
			boolean isStatic = false;
			if (obj instanceof Attribut) isStatic = ((Attribut) obj).isStatic();
			if (obj instanceof Methode)  isStatic = ((Methode) obj).isStatic();

			if (isStatic)
			{
				souligner(g2, x + MARGE_X, y + fm.getAscent(), fm.stringWidth(s));
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

	private void dessinerStringCentre(Graphics g, String texte, int x, int y, int largeurConteneur)
	{
		FontMetrics metrics = g.getFontMetrics();
		int xCentre = x + (largeurConteneur - metrics.stringWidth(texte)) / 2;
		g.drawString(texte, xCentre, y + metrics.getAscent());
	}

	// --- CALCULS DE HAUTEUR ET LARGEUR ---

	private int calculerLargeurTitre(Classe c, FontMetrics fm)
	{
		int largeur = fm.stringWidth(c.getNom());
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
		if (nbLignesReelles == 0) return MARGE_Y * 2 + INTERLIGNE;
		return (MARGE_Y * 2) + (nbLignesReelles * hLigne) + ((nbLignesReelles - 1) * INTERLIGNE);
	}

	// --- FORMATAGE DE TEXTE ---

	private String getVisibiliteSymbole(String visibilite)
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

	private String getDebutSignatureMethode(Methode meth)
	{
		String s = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
		Classe classe = this.ctrl.getClasseAvecMeth(meth);
		List<Parametre> params = meth.getLstParam();
		
		for (int i = 0; i < params.size(); i++)
		{
			if (i >= 2 && !classe.estClique()) return s + " ...)";

			s += params.get(i).getNom() + " : " + params.get(i).getType();
			if (i < params.size() - 1) s += ", ";
		}
		return s + ")";
	}

	private String getSignatureAttributAlignee(Attribut att, int wGaucheMax, FontMetrics fm)
	{
		String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
		String droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
		droite += att.isAddOnly() ? " {addOnly}" : "";
		return padding(gauche, droite, wGaucheMax, fm);
	}

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
	 * Ajoute des espaces entre la partie gauche (nom) et droite (type) pour aligner les deux colonnes.
	 */
	private String padding(String gauche, String droite, int largeurMax, FontMetrics fm)
	{
		int largeurActuel = fm.stringWidth(gauche);
		int espace = Math.max(0, (largeurMax - largeurActuel) / fm.stringWidth(" "));
		return gauche + " ".repeat(espace) + droite;
	}
}