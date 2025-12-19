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
 * Elle gere le dessin du rectangle, du titre, des attributs et des methodes,
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
	 * @param ctrl Le controleur pour acceder aux informations du projet (types, liaisons).
	 */
	public DessinerClasse(Controleur ctrl)
	{
		this.ctrl = ctrl;
	}

	/*------------------------------------------*/
	/* Methode d'instance                       */
	/*------------------------------------------*/

	/**
	 * Dessine une classe UML complete dans son rectangle associe.
	 * Met a jour la taille du rectangle en fonction du contenu.
	 *
	 * @param g2     Le contexte graphique Java AWT.
	 * @param classe La classe metier a afficher.
	 * @param rect   Le rectangle graphique associe (contenant les coordonnees X, Y).
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
	/* Methodes privees                         */
	/*------------------------------------------*/

	/**
     * Dessine un rectangle de fond blanc avec une bordure noire.
     * Utilise pour dessiner chaque section de la classe (Titre, Attributs, Methodes).
     *
     * @param g2 Le contexte graphique.
     * @param x La position X du coin superieur gauche.
     * @param y La position Y du coin superieur gauche.
     * @param largeur La largeur du bloc.
     * @param h La hauteur du bloc.
     */
	private void dessinerFondBloc(Graphics2D g2, int x, int y, int largeur, int h)
	{
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, largeur, h);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, largeur, h);
	}

	
	/**
     * Affiche le nom de la classe et ses stereotypes (Interface, Abstract).
     * Le nom de la classe est affiche en gras et centre.
     *
     * @param g2 Le contexte graphique.
     * @param classe La classe metier contenant les informations.
     * @param x La position X de depart du bloc.
     * @param y La position Y de depart du bloc.
     * @param largeur La largeur totale disponible pour centrer le texte.
     * @param hLigne La hauteur d'une ligne de texte.
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
		if (classe.estInterface())
		{
			dessinerStringCentre(g2, "<<Interface>>", x, y, largeur);
			y += hLigne;
		}

		if (classe.estAbstract() && !classe.estInterface())
		{
			dessinerStringCentre(g2, "<<Abstract>>", x, y, largeur);
		}
	}

	
	
	/**
     * Affiche une liste de chaines de caracteres ligne par ligne.
     * Le soulignement des elements statiques (Attributs ou Methodes).
     * L'affichage des "..." si la liste est tronquee.
     *
     * @param g2 Le contexte graphique.
     * @param textes La liste des chaines formates a afficher.
     * @param lst La liste des objets originaux (Attribut ou Methode) pour verifier les proprietes (static).
     * @param x La position X de reference.
     * @param y La position Y de depart.
     * @param fm Les metriques de la police courante.
     * @param showPoints Booleen indiquant s'il faut afficher "..." a la fin.
     */
	private void dessinerContenuListe(Graphics2D g2, List<String> textes, List<?> lst, int x, int y, FontMetrics fm, boolean montrer)
	{
		int     hLigne;
		String  s;
		Object  obj;
		boolean estStatic;

		hLigne = fm.getHeight();
		
		for (int i = 0; i < textes.size(); i++)
		{
			s   = textes.get(i);
			obj = lst.get(i);
			
			g2.drawString(s, x + MARGE_X, y + fm.getAscent());
			
			estStatic = false;
			if (obj instanceof Attribut) estStatic = ((Attribut) obj).estStatic();
			if (obj instanceof Methode)  estStatic = ((Methode) obj).estStatic();

			if (estStatic)
				this.souligner(g2, x + MARGE_X, y + fm.getAscent(), fm.stringWidth(s));
			

			y += hLigne + INTERLIGNE;
		}

		if (montrer)
			g2.drawString("...", x + MARGE_X, y + fm.getAscent());
		
	}

	private void souligner(Graphics2D g2, int x, int yBase, int largeur)
	{
		g2.drawLine(x, yBase + 2, x + largeur, yBase + 2);
	}


	/**
     * Dessine une chaine de texte centree horizontalement dans un conteneur donne.
     *
     * @param g Le contexte graphique (Graphics ou Graphics2D).
     * @param texte La chaine a afficher.
     * @param x La position X du debut du conteneur.
     * @param y La position Y actuelle.
     * @param largeurConteneur La largeur du conteneur pour calculer le centre.
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
	 * Calcule la largeur necessaire pour afficher le titre de la classe.
	 */
	private int calculerLargeurTitre(Classe c, FontMetrics fm)
	{
		int largeur;

		largeur = fm.stringWidth(c.getNom());
		
		if (c.estInterface() || c.estAbstract())
		{
			largeur = Math.max(largeur, fm.stringWidth("<<Interface>>"));
		}
		return largeur;
	}


	private int calculerLargeurMax(List<String> lignes, FontMetrics fm)
	{
		int max = 0;
		for (String s : lignes) 
			max = Math.max(max, fm.stringWidth(s));
		return max;
	}


	private int calculerHauteurTitre(Classe c, int hLigne)
	{
		int h = MARGE_Y * 2 + hLigne;
		if (c.estInterface() || (c.estAbstract() && !c.estInterface()))
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


	/**
     * Convertit la visibilite (public, private, protected ou paquetage) en symbole UML (+, -, #, ~).
     *
     * @param visibilite La chaine representant la visibilite (ex: "public").
     * @return Le symbole UML correspondant.
     */
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

	/**	
     * Construit le debut de la signature d'une methode (Visibilite + Nom + Arguments).
     * Gere la troncature des arguments si la classe n'est pas cliquee.
     *
     * @param meth L'objet Methode a traiter.
     * @return Une chaine representant le debut de la signature.
     */
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


	private String determineSignatureAttributAlignee(Attribut att, int largeurGaucheMax, FontMetrics fm)
	{
		String gauche, droite;

		gauche = this.determineVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
		droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
		
		return this.marge(gauche, droite, largeurGaucheMax, fm);
	}

	/**
     * Construit la signature complete d'un attribut en inserant des espaces pour aligner
     * visuellement les types a droite.
     *
     * @param att L'attribut a formater.
     * @param largeurGaucheMax La largeur maximale de la partie gauche (nom) pour l'alignement.
     * @param fm Les metriques de police.
     * @return La chaine formatee avec padding.
     */
	private String determineSignatureMethodeAlignee(Methode meth, int largeurGaucheMax, FontMetrics fm)
	{
		String gauche, droite;

		gauche = this.determineDebutSignatureMethode(meth);
		droite = "";
		
		if (!meth.getType().equals("void") && !meth.getType().isEmpty() && !meth.getType().equals(meth.getNom()))
			droite = " : " + meth.getType();

		return this.marge(gauche, droite, largeurGaucheMax, fm);
	}


	private String marge(String gauche, String droite, int largeurMax, FontMetrics fm)
	{
		int largeurActuel, espace;

		largeurActuel = fm.stringWidth(gauche);
		espace        = Math.max(0, (largeurMax - largeurActuel) / fm.stringWidth(" "));
		
		return gauche + " ".repeat(espace) + droite;
	}
}