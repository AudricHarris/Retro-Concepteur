package RetroConcepteur.vue;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;

public class PanelUML extends JPanel
{
    private FrameUML frame;
    private Controller ctrl;
    private List<Classe> lstClasse;
    private HashMap<Classe, Rectangle> mapClasseRectangle;

    // Marges internes du rectangle UML
    private final int PADDING = 10; 
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
            
            x += 350; // Décalage vers la droite
            if (x > 1000) { // Retour à la ligne si trop large
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
        Font fontItalic = new Font("SansSerif", Font.ITALIC, 12);
        
        g2.setFont(font); 
        FontMetrics metrics = g2.getFontMetrics();
        int hauteurTexte = metrics.getHeight();

        for (Classe classe : this.lstClasse)
        {
            Rectangle rect = this.mapClasseRectangle.get(classe);
            int x = rect.getX();
            int startY = rect.getY();
            
            // --- ETAPE 1 : CALCUL DES LARGEURS ---
            
            // Calcul largeur Titre (Nom + stéréotypes)
            int largeurTitre = metrics.stringWidth(classe.getNom());
            if (classe.isInterface() || classe.isAbstract()) 
                largeurTitre = Math.max(largeurTitre, metrics.stringWidth("<<Interface>>"));
            

            // Calcul largeur Attributs
            int largeurAttributs = 0;
            List<String> strAttributs = new ArrayList<>();
            for (Attribut att : classe.getLstAttribut()) 
			{
                // On ignore les attributs "Liaison" (si c'est une classe du projet)
                if (this.ctrl.estClasseProjet(att.getType())) continue;

                String s = getSignatureAttribut(att);
                strAttributs.add(s);
                int w = metrics.stringWidth(s);
                if (w > largeurAttributs) largeurAttributs = w;
            }

            // Calcul largeur Méthodes
            int largeurMethodes = 0;
            List<String> strMethodes = new ArrayList<>();
            for (Methode meth : classe.getLstMethode()) 
			{
                String s = getSignatureMethode(meth);
                strMethodes.add(s);
                int w = metrics.stringWidth(s);
                if (w > largeurMethodes) largeurMethodes = w;
            }

            // Largeur finale du rectangle (max des 3 blocs + marges)
            int largeurRect = Math.max(largeurTitre, Math.max(largeurAttributs, largeurMethodes)) + (PADDING * 2);
            
            
            // --- ETAPE 2 : DESSIN ---

            int yCourant = startY;

            // 1. Fond blanc
            // (On ne connait pas encore la hauteur totale, on la calculera à la fin, 
            // ou on dessine le fond au fur et à mesure, ici on dessine les éléments puis le cadre)
            
            // 2. Titre
            yCourant += PADDING;
            
            if (classe.isInterface()) {
                drawCenteredString(g2, "<<Interface>>", x, yCourant, largeurRect);
                yCourant += hauteurTexte;
            }
            if (classe.isAbstract() && !classe.isInterface()) {
                drawCenteredString(g2, "<<Abstract>>", x, yCourant, largeurRect);
                yCourant += hauteurTexte;
            }

            // Nom de la classe (Gras)
            g2.setFont(fontGras);
            if (classe.isAbstract()) g2.setFont(fontItalic); // Nom en italique si abstrait
            
            // Centrage du nom
            int wNom = g2.getFontMetrics().stringWidth(classe.getNom());
            g2.drawString(classe.getNom(), x + (largeurRect - wNom) / 2, yCourant + metrics.getAscent());
            yCourant += hauteurTexte + INTERLIGNE;
            
            g2.setFont(font); // Retour police normale

            // Séparateur 1
            g2.drawLine(x, yCourant, x + largeurRect, yCourant);
            yCourant += INTERLIGNE;

            // 3. Attributs
            int i = 0;
            for (Attribut att : classe.getLstAttribut()) {
                if (this.ctrl.estClasseProjet(att.getType())) continue;

                String s = strAttributs.get(i++);
                g2.drawString(s, x + PADDING, yCourant + metrics.getAscent());
                
                // Souligner si statique
                if (att.isStatic()) 
				{
                    int w = metrics.stringWidth(s);
                    g2.drawLine(x + PADDING, yCourant + metrics.getAscent() + 2, x + PADDING + w, yCourant + metrics.getAscent() + 2);
                }
                yCourant += hauteurTexte;
            }
            yCourant += INTERLIGNE;

            // Séparateur 2
            g2.drawLine(x, yCourant, x + largeurRect, yCourant);
            yCourant += INTERLIGNE;

            // 4. Méthodes
            int j = 0;
            for (Methode meth : classe.getLstMethode()) {
                String s = strMethodes.get(j++);
                g2.drawString(s, x + PADDING, yCourant + metrics.getAscent());

                // Souligner si statique
                if (meth.isStatic()) {
                    int w = metrics.stringWidth(s);
                    g2.drawLine(x + PADDING, yCourant + metrics.getAscent() + 2, x + PADDING + w, yCourant + metrics.getAscent() + 2);
                }
                yCourant += hauteurTexte;
            }
            yCourant += PADDING;

            // --- ETAPE 3 : CADRE ET MISE A JOUR ---
            
            int hauteurRect = yCourant - startY;

            // Mise à jour de l'objet métier Rectangle
            rect.setTailleX(largeurRect);
            rect.setTailleY(hauteurRect);

            // Dessin du contour final
            g2.setColor(Color.BLACK);
            g2.drawRect(x, startY, largeurRect, hauteurRect);
            
            // Remplissage blanc (en arrière plan, donc on aurait du le faire avant, 
            // astuce : on dessine le rect blanc AVANT le contenu dans une vraie implémentation,
            // ici je dessine juste le cadre pour ne pas effacer le texte).
            // Pour faire propre : 
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.DST_OVER));
            g2.setColor(Color.WHITE);
            g2.fillRect(x, startY, largeurRect, hauteurRect);
            g2.setComposite(java.awt.AlphaComposite.SrcOver);
            g2.setColor(Color.BLACK);
        }
    }

    // --- Méthodes utilitaires pour générer les chaînes ---

    private String getSignatureAttribut(Attribut att) 
	{
        String s = getVisibiliteSymbole(att.getVisibilite()) + " " + att.getNom() + " : " + att.getType();
        if (att.isConstante()) s += " {readOnly}";
        return s;
    }

    private String getSignatureMethode(Methode meth) 
	{
        if (meth.getNom().equals("main")) return ""; // On masque le main souvent en UML, ou on l'affiche

        StringBuilder sb = new StringBuilder();
        sb.append(getVisibiliteSymbole(meth.getVisibilite())).append(" ");
        sb.append(meth.getNom()).append("(");
        
        List<Parametre> params = meth.getLstParam();
        for (int k = 0; k < params.size(); k++) {
            sb.append(params.get(k).getNom()).append(" : ").append(params.get(k).getType());
            if (k < params.size() - 1) sb.append(", ");
        }
        sb.append(")");
        
        if (!meth.getType().equals("void") && !meth.getType().isEmpty()) {
            sb.append(" : ").append(meth.getType());
        }
        return sb.toString();
    }

    private String getVisibiliteSymbole(String visibilite) 
	{
        if (visibilite == null) return " ";
        switch (visibilite) {
            case "public": return "+";
            case "private": return "-";
            case "protected": return "#";
            default: return "~"; // package-private
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, int width) 
	{
        FontMetrics metrics = g.getFontMetrics();
        int xCentered = x + (width - metrics.stringWidth(text)) / 2;
        g.drawString(text, xCentered, y + metrics.getAscent());
    }
}