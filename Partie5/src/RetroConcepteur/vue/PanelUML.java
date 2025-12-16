package RetroConcepteur.vue;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;

public class PanelUML extends JPanel
{
    private FrameUML frame;
    private Controller ctrl;

    // Donnees du modele
    private List<Classe>  lstClasse;
    private List<Liaison> lstLiaisons;
    private List<Chemin>  lstChemins;
    
    // Donnees de la vue
    private HashMap<Classe, Rectangle> mapClasseRectangle;
    private DessinerFleche dessinerFleche;

	private boolean   positionDeterminee = false;


    // --- CONSTANTES DE STYLE ---
    private final int PADDING_X = 10; 
    private final int PADDING_Y = 5;  
    private final int INTERLIGNE = 2; 

    private final Color COL_TITRE = Color.LIGHT_GRAY; 
    private final Color COL_ATT   = new Color(240, 240, 240); 
    private final Color COL_METH  = Color.WHITE;      

    public PanelUML(FrameUML frame, Controller ctrl)
    {
        this.frame = frame;
        this.ctrl = ctrl;
        this.dessinerFleche = new DessinerFleche();

        this.setPreferredSize(new Dimension(2000, 2000));   
        
        // Initialisation des ecouteurs souris pour le deplacement
        GereSouris gs = new GereSouris(this);
        this.addMouseListener(gs);
        this.addMouseMotionListener(gs);

        this.reinitialiser();
    }

    /**
     * Recharge les donnees depuis le controleur et recalcule les positions
     */
    public void reinitialiser()
    {
        this.lstClasse = this.ctrl.getLstClasses();
        this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaison());
        this.mapClasseRectangle = new HashMap<Classe,Rectangle>();
		this.positionDeterminee = false;
        this.lstChemins = new ArrayList<Chemin>();

        this.initialiserPositions();
        this.determinerPositions();
        this.repaint();
    }
    
    private void initialiserPositions()
    {
        int x = 50;
        int y = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();         

        for (Classe c : this.lstClasse) 
        {
            // On initialise avec une taille 0, elle sera recalculee dans paintComponent
            Rectangle rect = new Rectangle(x, y, 0, 0);
            this.mapClasseRectangle.put(c, rect);

			
            // Gestion simple du retour a la ligne si on depasse l'ecran
            x += 350;
            if (x > screenSize.width - 200) 
            { 
                x = 50;
                y += 350; // Saut de ligne arbitraire, sera affine dynamiquement si besoin
            }
        }
    }

    public HashMap<Classe,Rectangle> getMap() { return this.mapClasseRectangle; }

    // =========================================================================
    //                             DESSIN PRINCIPAL
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Configuration globale de la police
        Font font = new Font("SansSerif", Font.PLAIN, 12);
        g2.setFont(font);
        
        // 1. D'abord on dessine et calcule la taille de toutes les classes
        for (Classe classe : this.lstClasse)
            this.dessinerClasse(g2, classe);
        
		if (!this.positionDeterminee) 
		{
			this.determinerPositions();
			this.positionDeterminee = true;
			this.repaint();
			return;
		}

        // 2. Ensuite on trace les fleches (maintenant que les rectangles ont leur taille finale)
        
        for (Chemin c : this.lstChemins) 
        {
            // On dessine directement le chemin intelligent calculé plus tôt
            this.dessinerFleche.dessinerLiaison(g2, c);
        }
        
    }

    // =========================================================================
    //                        DESSIN D'UNE CLASSE 
    // =========================================================================

    private void dessinerClasse(Graphics2D g2, Classe classe)
    {
        FontMetrics metrics = g2.getFontMetrics();
        Rectangle rect = this.mapClasseRectangle.get(classe);
        int x = rect.getX();
        int y = rect.getY();
        
        // --- 1. PREPARATION DES TEXTES AVEC ALIGNEMENT ET FILTRAGE ---
        
        // 1a. Attributs
        List<Attribut> lstAtt = new ArrayList<Attribut>();
        int maxLargeurGaucheAtt = 0;
        int cpt = 0;
        boolean tropAtt = false;

        for (Attribut att : classe.getListOrdonneeAttribut()) 
		{
            if (this.ctrl.estClasseProjet(att.getType())) continue; // On ignore les attributs qui sont des liens
            
            if (cpt >= 3) 
			{ 
				tropAtt = true;
				break; 
			} // Limite a 3 elements
            
            String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
            maxLargeurGaucheAtt = Math.max(maxLargeurGaucheAtt, metrics.stringWidth(gauche));
            lstAtt.add(att);
            cpt++;
        }

        // 1b. Methodes
        List<Methode> lstMeth = new ArrayList<Methode>();
        int maxLargeurGaucheMeth = 0;
        cpt = 0;
        boolean tropMeth = false;

        for (Methode meth : classe.getListOrdonneeMethode()) 
		{
            if (meth.getNom().equals("main")) 
				continue;

            if (cpt >= 3) 
			{ 
				tropMeth = true;
				break; // Limite a 3 elements
			} 
			
            String gauche = this.getDebutSignatureMethode(meth);
            maxLargeurGaucheMeth = Math.max(maxLargeurGaucheMeth, metrics.stringWidth(gauche));
            lstMeth.add(meth);
            cpt++;
        }

        //  2 CALCUL DES DIMENSIONS
        
        // Génération des chaînes alignées pour calcul de largeur
        List<String> strAtts = new ArrayList<String>();
        for (Attribut a : lstAtt) strAtts.add(getSignatureAttributAlignee(a, maxLargeurGaucheAtt, metrics));
        
        List<String> strMeths = new ArrayList<String>();
        for (Methode m : lstMeth) strMeths.add(getSignatureMethodeAlignee(m, maxLargeurGaucheMeth, metrics));

        int largTitre = this.calculerLargeurTitre(classe, metrics);
        int larAtt   = this.calculerLargeurMax(strAtts, metrics);
        int largMeth  = this.calculerLargeurMax(strMeths, metrics);
        
        int largeurRect = Math.max(largTitre, Math.max(larAtt, largMeth)) + (PADDING_X * 2);

        int hTitre = this.calculerHauteurTitre(classe, metrics.getHeight());
        int hAtt   = this.calculerHauteurBloc(lstAtt.size(), metrics.getHeight(), tropAtt);
        int hMeth  = this.calculerHauteurBloc(lstMeth.size(), metrics.getHeight(), tropMeth);
        
        int hauteurTotale = hTitre + hAtt + hMeth;

        //  3 DESSIN DES BLOCS 
        
        // Titre
        this.dessinerFondBloc(g2, x, y, largeurRect, hTitre, COL_TITRE);
        this.dessinerContenuTitre(g2, classe, x, y + PADDING_Y, largeurRect, metrics.getHeight());
        
        // Attributs
        this.dessinerFondBloc(g2, x, y + hTitre, largeurRect, hAtt, COL_ATT);
        this.dessinerContenuListe(g2, strAtts, lstAtt, x, y + hTitre + PADDING_Y, metrics, tropAtt);
        
        // Méthodes
        this.dessinerFondBloc(g2, x, y + hTitre + hAtt, largeurRect, hMeth, COL_METH);
        this.dessinerContenuListe(g2, strMeths, lstMeth, x, y + hTitre + hAtt + PADDING_Y, metrics, tropMeth); 

        // Contour Global
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, largeurRect, hauteurTotale);

        // Mise à jour du rectangle
        rect.setTailleX(largeurRect);
        rect.setTailleY(hauteurTotale);
    }

    // =========================================================================
    //                        DESSIN DES LIAISONS
    // =========================================================================

    private void dessinerLiaison(Graphics2D g2, Liaison l) 
    {
        Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
        Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());

        if (r1 != null && r2 != null) 
        {
            Point pDepart = this.calculerPointBord(r1, r2);
            Point pArrivee = this.calculerPointBord(r2, r1);

            //this.dessinerMultiplicite(pDepart, pArrivee, multiplicite1, multiplicite2);

            this.dessinerFleche.dessinerLiaison(g2,new Chemin( pDepart, pArrivee, l.getType(),
												 this.mapClasseRectangle, l.getFromClass(), l.getToClass() )
                                );
            String multiplicite1;
            String multiplicite2;
        }
    }

    private void dessinerMultiplicite(Point p1, Point p2, String multiplicite1, String multiplicite2 )
    {
        
    }

    /**
     * Calcule le point d'intersection entre le segment reliant les centres et le bord du rectangle source.
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

        // On détermine si on tape sur les bords horizontaux ou verticaux
        // en comparant les ratios
        double largeurMoitie = rect1.getTailleX() / 2.0;
        double hMoitie = rect1.getTailleY() / 2.0;

        // Évite la division par zéro
        if (largeurMoitie == 0 || hMoitie == 0) return new Point(cx1, cy1);

        double ratioX = Math.abs(dx) / largeurMoitie;
        double ratioY = Math.abs(dy) / hMoitie;

        if (ratioX > ratioY) 
		{
            // Intersection gauche ou droite
            return dx > 0 
                ? new Point(rect1.getX() + rect1.getTailleX(), cy1) // Droite
                : new Point(rect1.getX(), cy1);                      // Gauche
        } 
		else 
		{
            // Intersection haut ou bas
            return dy > 0 
                ? new Point(cx1, rect1.getY() + rect1.getTailleY()) // Bas
                : new Point(cx1, rect1.getY());                      // Haut
        }
    }

    // =========================================================================
    //                        OUTILS DE DESSIN 
    // =========================================================================

    private void dessinerFondBloc(Graphics2D g2, int x, int y, int largeur, int h, Color c) 
    {
        Color old = g2.getColor();
        g2.setColor(c);
        g2.fillRect(x, y, largeur, h);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, largeur, h);
        g2.setColor(old);
    }

    private void dessinerContenuTitre(Graphics2D g2, Classe classe, int x, int y, int largeur, int hLigne) 
    {
        Font fontNormal = g2.getFont();
        Font fontGras = fontNormal.deriveFont(Font.BOLD);

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

        g2.setFont(fontGras);
        dessinerStringCentre(g2, classe.getNom(), x, y, largeur);
        g2.setFont(fontNormal);
    }

    // Methode generique pour dessiner Attributs ET Methodes
    // Utilise "<?>" pour accepter Attribut ou Methode tant qu'on a la string formatee
    private void dessinerContenuListe(Graphics2D g2, List<String> textes, List<?> objets, int x, int y, FontMetrics fm, boolean showPoints) 
    {
        int hLigne = fm.getHeight();
        
        for (int i = 0; i < textes.size(); i++) 
        {
            String s = textes.get(i);
            Object obj = objets.get(i);
            
            g2.drawString(s, x + PADDING_X, y + fm.getAscent());
            
            // Gestion generique du Static pour Attribut et Methode
            boolean isStatic = false;
            if (obj instanceof Attribut) 
                isStatic = ((Attribut)obj).isStatic();
            if (obj instanceof Methode)  
                isStatic = ((Methode)obj).isStatic();

            if (isStatic) 
                souligner(g2, x + PADDING_X, y + fm.getAscent(), fm.stringWidth(s));
            
            y += hLigne + INTERLIGNE;
        }

        if (showPoints) {
            g2.drawString("...", x + PADDING_X, y + fm.getAscent());
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

    // =========================================================================
    //                        CALCULS DE TEXTE ET ALIGNEMENT
    // =========================================================================

    private int calculerLargeurTitre(Classe c, FontMetrics fm) 
	{
        int w = fm.stringWidth(c.getNom());
        if (c.isInterface() || c.isAbstract()) 
            w = Math.max(w, fm.stringWidth("<<Interface>>"));
        return w;
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
        int h = PADDING_Y * 2 + hLigne; 
        if (c.isInterface() || (c.isAbstract() && !c.isInterface())) 
            h += hLigne; 
        return h;
    }

    private int calculerHauteurBloc(int nbLignes, int hLigne, boolean avecPoints) 
	{
        int nbLignesReelles = avecPoints ? nbLignes + 1 : nbLignes;
        if (nbLignesReelles == 0) 
            return PADDING_Y * 2 + INTERLIGNE;
        return (PADDING_Y * 2) + (nbLignesReelles * hLigne) + ((nbLignesReelles - 1) * INTERLIGNE);
    }

    // --- Generation de chaines ---

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

	private void determinerPositions()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int xCourant = 50;
		int yCourant = 50;
		int hauteurLigneMax = 0;

		for (Rectangle rect : this.mapClasseRectangle.values()) 
		{
			// Vérifier si on dépasse la largeur de l'écran
			if (xCourant + rect.getTailleX() > screenSize.width - 100 && xCourant > 50) 
			{
				// Passer à la ligne suivante
				xCourant = 50;
				yCourant += hauteurLigneMax + 50;
				hauteurLigneMax = 0;
			}

			// Positionner le rectangle
			rect.setX(xCourant);
			rect.setY(yCourant);

			// Mettre à jour pour le prochain rectangle
			xCourant += rect.getTailleX() + 50; // Espacement horizontal
			if (rect.getTailleY() > hauteurLigneMax) 
				hauteurLigneMax = rect.getTailleY();
		}

		// Recalculer les arcs après repositionnement
		this.lstChemins.clear();
		for (Liaison l : this.lstLiaisons) 
		{
			Rectangle r1 = this.mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = this.mapClasseRectangle.get(l.getToClass());
			
			if (r1 != null && r2 != null) 
			{
				int x1 = r1.getCentreX();
				int y1 = r1.getCentreY();
                Point p1 = new Point(x1,y1);

				int x2 = r2.getCentreX();
				int y2 = r2.getCentreY();
                Point p2 = new Point(x2,y2);
				
				Chemin chemin = new Chemin(p1, p2, l.getType(),this.mapClasseRectangle,l.getFromClass(), l.getToClass() );
				char zone = this.getZone(r1, r2);
				r1.addPos(zone, chemin);
				char zoneInv = zoneInverse(zone);
				r2.addPos(zoneInv, chemin);

                r1.repartirPointsLiaison(zone);
                r2.repartirPointsLiaison(zoneInv);
				
				this.lstChemins.add(chemin);
			}
		}
	}

    private String getSignatureAttributAlignee(Attribut att, int wGaucheMax, FontMetrics fm) 
	{
        String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
        String droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
        return padding(gauche, droite, wGaucheMax, fm);
    }

    private String getDebutSignatureMethode(Methode meth) 
	{
        String s = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
        List<Parametre> params = meth.getLstParam();

        for (int i = 0; i < params.size(); i++) 
		{
            s += params.get(i).getNom() + " : " + params.get(i).getType();
            if (i < params.size() - 1) 
                s += ", ";
            if (i >= 2) 
                return s + " ...)"; 
        }
        return s + ")";
    }

    private String getSignatureMethodeAlignee(Methode meth, int wGaucheMax, FontMetrics fm) 
	{
        String gauche = getDebutSignatureMethode(meth);
        String droite = "";

        if (!meth.getType().equals("void") && !meth.getType().isEmpty() && !meth.getType().equals(meth.getNom())) 
            droite = " : " + meth.getType();

        return padding(gauche, droite, wGaucheMax, fm);
    }

    private String padding(String gauche, String droite, int wMax, FontMetrics fm) 
	{
        int wActuel = fm.stringWidth(gauche);
        int espace = Math.max(0, (wMax - wActuel) / fm.stringWidth(" "));

        return gauche + " ".repeat(espace) + droite;
    }
    
    public char getZone(Rectangle source, Rectangle target) 
    {
        // Calcul des deltas entre les centres
        double dx = target.getCentreX() - source.getCentreX();
        double dy = target.getCentreY() - source.getCentreY();

        // Normalisation par la taille du rectangle source
        double xNorm = dx / (double)source.getTailleX();
        double yNorm = dy / (double)source.getTailleY();

        // Détermination de la zone
        if (Math.abs(yNorm) > Math.abs(xNorm)) 
        {
            return (yNorm < 0) ? 'H' : 'B';
        } 
        else 
        {
            return (xNorm < 0) ? 'G' : 'D';
        }
    }

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
