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
    private List<Classe> lstClasse;
    private HashMap<Classe, Rectangle> mapClasseRectangle;
    private List<Liaison> lstLiaisons;
	private List<Arc> lstArcs;
    
    private DessinerFleche dessinerFleche;

    // --- CONSTANTES DE STYLE ---
    private final int PADDING_X = 10; 
    private final int PADDING_Y = 5;  
    private final int INTERLIGNE = 2; 

    private final Color COL_TITRE = Color.LIGHT_GRAY; 
    private final Color COL_ATT   = new Color(240, 240, 240); // Gris très clair
    private final Color COL_METH  = Color.WHITE;      

    public PanelUML(FrameUML frame, Controller ctrl)
    {
        this.frame = frame;
        this.ctrl = ctrl;
        this.dessinerFleche = new DessinerFleche();

        this.reinitialiserDonnees();
        
        this.setPreferredSize(new Dimension(2000, 2000));   
        this.initialiserPositions();

        GereSouris gs = new GereSouris(this);
        this.addMouseListener(gs);
        this.addMouseMotionListener(gs);

        this.setVisible(true);
    }

    public void reinitialiser()
    {
        this.reinitialiserDonnees();
        this.initialiserPositions();
        this.repaint();
    }

    private void reinitialiserDonnees() 
    {
        this.lstClasse = this.ctrl.getLstClasses();
        this.lstLiaisons = new ArrayList<Liaison>(this.ctrl.getListLiaison());
		this.lstArcs = new ArrayList<Arc>();
        this.mapClasseRectangle = new HashMap<Classe, Rectangle>();
    }
    
    private void initialiserPositions()
    {
        int x = 50;
        int y = 50; 
        int yMax = 0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        for (Classe c : this.lstClasse) 
        {
            Rectangle rect = new Rectangle(x, y, 0, 0, c.getNom());
            this.mapClasseRectangle.put(c, rect);

            if ( yMax < rect.getTailleY() ) yMax = rect.getTailleY();

            x += 350; // Décalage arbitraire initial
            if (x > screenSize.width - 200) 
            { 
                x = 50;
                y += 30 + yMax;
            }
        }

		for (Liaison l : this.lstLiaisons) 
		{
			int x1 = this.mapClasseRectangle.get( l.getFromClass() ).getCentreX();
			int y1 = this.mapClasseRectangle.get( l.getFromClass() ).getCentreY();
			int x2 = this.mapClasseRectangle.get( l.getToClass() ).getCentreX();
			int y2 = this.mapClasseRectangle.get( l.getToClass() ).getCentreY();
			
			Arc arc = new Arc( l.getFromClass().getNom(), x1, y1, l.getToClass().getNom(),x2 ,y2 );
			char zone = this.getZone( this.mapClasseRectangle.get( l.getFromClass() ), this.mapClasseRectangle.get( l.getToClass() ) );
			this.mapClasseRectangle.get(l.getFromClass()).ajoutArc(zone, arc);
			char zoneInverse = zoneInverse( zone );
			this.mapClasseRectangle.get(l.getToClass()).ajoutArc( zoneInverse, arc);
			
			this.lstArcs.add( arc );
		}
    }

	private int getPlusGrandeHauteur()
	{
		int max=0;
		for ( Rectangle rect : this.mapClasseRectangle.values() )
			if ( rect.getTailleY() > max ) max = rect.getTailleY();
		return max;
	}

	private void determinerPositions()
	{
		int xAvant, yAvant, largeurPrc, borPrc;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int hauteurMax = this.getPlusGrandeHauteur();

		xAvant = 0;
		yAvant = 0;
		largeurPrc = 0;

		for (Rectangle rect : this.mapClasseRectangle.values()) 
		{
            borPrc = xAvant + largeurPrc;

            rect.setX(borPrc + 50 );
			if (rect.getX() > screenSize.width - 200 ) 
			{
                rect.setY(rect.getY() + hauteurMax + 50);
                rect.setX(50);
			}

            xAvant = rect.getX();
            yAvant = rect.getY();
            largeurPrc = rect.getTailleX();

		}
	}

    public HashMap<Classe,Rectangle> getMap() { return this.mapClasseRectangle; }

    public List<Classe> getLstClasse() { return this.lstClasse; }
    public List<Liaison> getLstLiaisons() { return this.lstLiaisons; }
    public List<Arc> getLstArcs() { return this.lstArcs; }	

    // =========================================================================
    //                             DESSIN PRINCIPAL
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Configuration globale
        Font font = new Font("SansSerif", Font.PLAIN, 12);
        g2.setFont(font);
        
        // 1. Dessiner les classes
        for (Classe classe : this.lstClasse)
            this.dessinerClasse(g2, classe);

        // 2. Dessiner les liaisons
        for (Liaison l : this.lstLiaisons) 
            this.dessinerLiaison(g2, l);




    }

    // =========================================================================
    //                        DÉCOMPOSITION DU DESSIN CLASSE
    // =========================================================================

    private void dessinerClasse(Graphics2D g2, Classe classe)
    {
        FontMetrics metrics = g2.getFontMetrics();
        Rectangle rect = this.mapClasseRectangle.get(classe);
        int x = rect.getX();
        int yDepart = rect.getY();
		boolean plusDeTroisAtt  = false;
        boolean plusDeTroisMeth = false;
		int largeur;
		
        // --- ETAPE 1 : Préparation des listes de chaînes avec ALIGNEMENT ---
        
        // 1a. Attributs : Calcul de la largeur max de la partie gauche (Visibilité + Nom)
        int maxLargeurGaucheAtt = 0;
        ArrayList<Attribut> lstAttAfficher = new ArrayList<>();
        
		int cpt=0;
        for (Attribut att : classe.getListOrdonneeAttribut())
        {
            if (!this.ctrl.estClasseProjet(att.getType())) 
            {
                String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
                largeur= metrics.stringWidth(gauche);
                if (largeur > maxLargeurGaucheAtt) 
					maxLargeurGaucheAtt = largeur;
                lstAttAfficher.add(att);
            }
			if ( ++cpt > 3 ) 
			{
				plusDeTroisAtt = true;
				break;
			}
        }

        // 1b. Generation des chaînes attributs alignees
        ArrayList<String> lstStrAttributs = new ArrayList<>();
        for (Attribut att : lstAttAfficher) 
            lstStrAttributs.add(this.getSignatureAttributAlignee(att, maxLargeurGaucheAtt, metrics));
        

        // 1c. Méthodes : Calcul de la largeur max de la partie gauche (Visibilité + Signature)
        int maxLargeurGaucheMeth = 0;
        ArrayList<Methode> lstMethAfficher= new ArrayList<>();

		cpt=0;
        for (Methode meth : classe.getListOrdonneeMethode()) 
        {
            if (!meth.getNom().equals("main")) 
            {
                String gauche = this.getDebutSignatureMethode(meth);
                largeur= metrics.stringWidth(gauche);
                if (largeur > maxLargeurGaucheMeth) maxLargeurGaucheMeth = largeur;
                lstMethAfficher.add(meth);
            }
			if ( ++cpt > 3 ) 
			{
				plusDeTroisMeth = true;
				break;
			}
				
        }

        // 1d. Génération des chaînes méthodes alignées
        ArrayList<String> lstStrMethodes = new ArrayList<>();
        for (Methode meth : lstMethAfficher) 
            lstStrMethodes.add(this.getSignatureMethodeAlignee(meth, maxLargeurGaucheMeth, metrics));
        

        // --- ETAPE 2 : Calcul des dimensions ---
        int largeurTitre = this.calculerLargeurTitre(classe, metrics);
        int largeurAtt   = this.calculerLargeurMax(lstStrAttributs, metrics);
        int largeurMeth  = this.calculerLargeurMax(lstStrMethodes, metrics);
        
        int largeurRect = Math.max(largeurTitre, Math.max(largeurAtt, largeurMeth)) + (PADDING_X * 2);

        int hTitre = this.calculerHauteurTitre(classe, metrics.getHeight());
        int hAtt   = this.calculerHauteurBloc(lstStrAttributs.size(), metrics.getHeight());
        int hMeth  = this.calculerHauteurBloc(lstStrMethodes.size(), metrics.getHeight());
        
        int hauteurTotale = hTitre + hAtt + hMeth;

        // --- ETAPE 3 : Dessin des Fonds et Contours ---
        int yCourant = yDepart;

        // Titre
        this.dessinerFondBloc(g2, x, yCourant, largeurRect, hTitre, COL_TITRE);
        yCourant += hTitre;

        // Attributs
        this.dessinerFondBloc(g2, x, yCourant, largeurRect, hAtt, COL_ATT);
        yCourant += hAtt;

        // Méthodes
        this.dessinerFondBloc(g2, x, yCourant, largeurRect, hMeth, COL_METH);
        
        // Contour global
        g2.setColor(Color.BLACK);
        g2.drawRect(x, yDepart, largeurRect, hauteurTotale);


        // --- ETAPE 4 : Dessin du Texte ---
        yCourant = yDepart + PADDING_Y;

        // Texte Titre
        this.dessinerContenuTitre(g2, classe, x, yCourant, largeurRect, metrics.getHeight());
        yCourant = yDepart + hTitre + PADDING_Y;

        // Texte Attributs
        this.dessinerContenuListeAttribut(g2, lstStrAttributs, lstAttAfficher, x, yCourant, metrics, plusDeTroisAtt);
        yCourant = yDepart + hTitre + hAtt + PADDING_Y;

        // Texte Méthodes
        this.dessinerContenuListeMethodes(g2, lstStrMethodes, lstMethAfficher, x, yCourant, metrics, plusDeTroisMeth);

        // --- ETAPE 5 : Mise à jour du modèle ---
        rect.setTailleX(largeurRect);
        rect.setTailleY(hauteurTotale);


    }

    // =========================================================================
    //                        MÉTHODES DE DESSIN 
    // =========================================================================

    private void dessinerFondBloc(Graphics2D g2, int x, int y, int w, int h, Color c) 
    {
        Color old = g2.getColor();
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, w, h);
        g2.setColor(old);
    }

    private void dessinerContenuTitre(Graphics2D g2, Classe classe, int x, int y, int w, int hLigne) 
    {
        Font fontNormal = g2.getFont();
        Font fontGras = fontNormal.deriveFont(Font.BOLD);

        if (classe.isInterface()) 
        {
            dessinerStringCentre(g2, "<<Interface>>", x, y, w);
            y += hLigne;
        }
        if (classe.isAbstract() && !classe.isInterface()) 
        {
            dessinerStringCentre(g2, "<<Abstract>>", x, y, w);
            y += hLigne;
        }

        g2.setFont(fontGras);
        dessinerStringCentre(g2, classe.getNom(), x, y, w);
        g2.setFont(fontNormal);
    }

    private void dessinerContenuListeAttribut(Graphics2D g2, ArrayList<String> lstStrAtt, ArrayList<Attribut> lstAtt, int x, int y, FontMetrics fm, boolean plusDeTrois) 
    {
        int hLigne = fm.getHeight();
        for (int i = 0; i < lstStrAtt.size(); i++) 
        {
            String s = lstStrAtt.get(i);
            Attribut att = lstAtt.get(i);
			
            
            g2.drawString(s, x + PADDING_X, y + fm.getAscent());
            
            if (att.isStatic()) 
                souligner(g2, x + PADDING_X, y + fm.getAscent(), fm.stringWidth(s));
			
			y += hLigne + INTERLIGNE;

			if ( i>= 2 && plusDeTrois) 
			{
				g2.drawString("...", x + PADDING_X, y + fm.getAscent());
				y += hLigne + INTERLIGNE;
				break;
			}	

			
        }
    }

    private void dessinerContenuListeMethodes(Graphics2D g2, List<String> textes, List<Methode> lstMeth, int x, int y, FontMetrics fm, boolean plusDeTrois) 
    {
        int hLigne = fm.getHeight();
        for (int i = 0; i < textes.size(); i++) 
        {
            String s = textes.get(i);
            Methode meth = lstMeth.get(i);
            
            g2.drawString(s, x + PADDING_X, y + fm.getAscent());
            
            if (meth.isStatic()) 
                souligner(g2, x + PADDING_X, y + fm.getAscent(), fm.stringWidth(s));
            
            y += hLigne + INTERLIGNE;

			if ( i>= 2 && plusDeTrois) 
			{
				g2.drawString("...", x + PADDING_X, y + fm.getAscent());
				y += hLigne + INTERLIGNE;
				break;
			}	
        }
    }
	

    private void souligner(Graphics2D g2, int x, int yBase, int largeur) 
    {
        g2.drawLine(x, yBase + 2, x + largeur, yBase + 2);
    }

    private void dessinerLiaison(Graphics2D g2, Liaison l) 
    {
		for (Arc arc : this.lstArcs) 
		{
			Rectangle r1 = null;
			Rectangle r2 = null;
			 r1 = this.mapClasseRectangle.get( this.getClasseByNom( arc.getFrom().keySet().iterator().next() ) );
			 r2 = this.mapClasseRectangle.get( this.getClasseByNom( arc.getTo().keySet().iterator().next() ) );
            if ( r1 != null && r2 != null )
            {
                this.dessinerFleche.dessinerLiaison(g2,arc.getX1(), arc.getY1(), arc.getX2(), arc.getY2(), "Association"); 
            }
		}

        //if (r1 != null && r2 != null) 
        //{
        //    this.dessinerFleche.dessinerLiaison(g2, 
        //                        r1.getCentreX(), r1.getCentreY(), 
        //                        r2.getCentreX(), r2.getCentreY(), 
        //                        "ASSOCIATION"); 
        //}
    }

    // =========================================================================
    //                        CALCULS & UTILITAIRES
    // =========================================================================

    private int calculerLargeurTitre(Classe c, FontMetrics fm) 
    {
        int largeur= fm.stringWidth(c.getNom());
        if (c.isInterface() || c.isAbstract()) 
            largeur = Math.max(largeur, fm.stringWidth("<<Interface>>"));
        
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
        int h = PADDING_Y * 2 + hLigne; 
        if (c.isInterface() || (c.isAbstract() && !c.isInterface())) 
            h += hLigne; 
        
        return h;
    }

    private int calculerHauteurBloc(int nbLignes, int hLigne) 
    {
        if (nbLignes == 0) 
            return PADDING_Y * 2 + INTERLIGNE; 
        return (PADDING_Y * 2) + (nbLignes * hLigne) + ((nbLignes - 1) * INTERLIGNE);
    }

    private void dessinerStringCentre(Graphics g, String texte, int x, int y, int largeurConteneur) 
    {
        FontMetrics metrics = g.getFontMetrics();
        int xCentre = x + (largeurConteneur - metrics.stringWidth(texte)) / 2;
        g.drawString(texte, xCentre, y + metrics.getAscent());
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

    // --- Génération de chaînes alignées ---

    private String getSignatureAttributAlignee(Attribut att, int largeurMaxGauche, FontMetrics fm) 
    {
        String gauche = this.getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom();
        String droite = " : " + att.getType() + (att.isConstante() ? " {freeze}" : "");
        
        int largeurGauche = fm.stringWidth(gauche);
        int espaceManquant = largeurMaxGauche - largeurGauche;
        
        int nbEspaces = Math.max(0, espaceManquant / fm.stringWidth(" "));
        
        String espaces = "";
        for(int i=0; i<nbEspaces; i++) espaces += " ";
        
        return gauche + espaces + droite;
    }

    private String getDebutSignatureMethode(Methode meth) 
    {
        String s = getVisibiliteSymbole(meth.getVisibilite()) + " " + meth.getNom() + "(";
        List<Parametre> params = meth.getLstParam();
        for (int i = 0; i < params.size(); i++) 
        {
            s += params.get(i).getNom() + " : " + params.get(i).getType();
            if (i < params.size() - 1) s += ", ";
			if ( i >= 2 )
			{
				s+= " ...)";
				return s;
			}
        }
        s += ")";
        return s;
    }

    private String getSignatureMethodeAlignee(Methode meth, int largeurMaxGauche, FontMetrics fm) 
    {
        String gauche = getDebutSignatureMethode(meth);
        String droite = "";
        
        if (!meth.getType().equals("void") && !meth.getType().isEmpty() && !meth.getType().equals(meth.getNom())) 
            droite = " : " + meth.getType();
        
        
        int wGauche = fm.stringWidth(gauche);
        int espaceManquant = largeurMaxGauche - wGauche;
        int nbEspaces = Math.max(0, espaceManquant / fm.stringWidth(" "));
        
        String espaces = "";
        for(int i=0; i<nbEspaces; i++) espaces += " ";
        
        return gauche + espaces + droite;
    }

	public Classe getClasseByNom(String nom) 
	{
		for (Classe c : this.lstClasse) 
		{
			if ( c.getNom().equals(nom) ) 
				return c;
		}
		return null;
	}

	public char getZone(Rectangle source, Rectangle target) 
	{
		char zone = ' ';
		double cx1 = source.getCentreX();
		double cy1 = source.getCentreY();
		
		double cx2 = target.getCentreX();
		double cy2 = target.getCentreY();

		double dx = cx2 - cx1;
		double dy = cy2 - cy1;

		double xNormalized = dx / source.getTailleX();
		double yNormalized = dy / source.getTailleY();

		if (Math.abs(yNormalized) > Math.abs(xNormalized)) 
		{
			
			if (yNormalized < 0) 
			{
				zone = 'H'; 
			} else 
			{
				zone = 'B';
			}
		} else 
		{
			
			if (xNormalized < 0) 
			{
				zone = 'G';
			} else 
			{
				zone = 'D';
			}
		}
		return zone;
	}
	private static final char zoneInverse(char zone) 
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