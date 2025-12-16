package RetroConcepteur.vue;

import javax.swing.JPanel;

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
import RetroConcepteur.metier.DiagramData;
import RetroConcepteur.metier.classe.*;
import RetroConcepteur.vue.outil.*;

public class PanelUML extends JPanel
{
	private FrameUML frame;
	private Controller ctrl;

	private List<Classe> lstClasse;
	private List<Liaison> lstLiaisons;
	private List<Chemin> lstChemins;

	private HashMap<Classe, Rectangle> mapClasseRectangle;
	private DessinerFleche dessinerFleche;

	/* ================== STYLE ================== */
	private final int PADDING_X = 10;
	private final int PADDING_Y = 5;
	private final int INTERLIGNE = 3;

	private final Color COL_TITRE = Color.LIGHT_GRAY;
	private final Color COL_ATT   = new Color(240, 240, 240);
	private final Color COL_METH  = Color.WHITE;

	/* ================== CONSTRUCTEUR ================== */
	public PanelUML(FrameUML frame, Controller ctrl)
	{
		this.frame = frame;
		this.ctrl = ctrl;
		this.dessinerFleche = new DessinerFleche();

		this.setPreferredSize(new Dimension(2000, 2000));

		this.reinitialiser();

		GereSouris gs = new GereSouris(this);
		this.addMouseListener(gs);
		this.addMouseMotionListener(gs);
	}

	/* ================== INITIALISATION ================== */
	public void reinitialiser()
	{
		this.lstClasse   = ctrl.getLstClasses();
		this.lstLiaisons = new ArrayList<>(ctrl.getListLiaison());
		this.lstChemins  = new ArrayList<>();
		this.mapClasseRectangle = new HashMap<>();

		initialiserPositions();
		creerChemins();
		repaint();
	}

	private void initialiserPositions()
	{
		int x = 50, y = 50;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		for (Classe c : lstClasse)
		{
			mapClasseRectangle.put(c, new Rectangle(x, y, 0, 0));
			x += 320;

			if (x > screen.width - 350)
			{
				x = 50;
				y += 350;
			}
		}
	}

	public List<Classe> getLstClasse() { return this.lstClasse;} 
	public List<Liaison> getLstLiaisons() { return this.lstLiaisons;} 
	public List<Chemin> getLstChemin() { return this.lstChemins;} 

	/* ================== CHEMINS ================== */
	private void creerChemins()
	{
		lstChemins.clear();

		for (Liaison l : lstLiaisons)
		{
			Rectangle r1 = mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = mapClasseRectangle.get(l.getToClass());

			if (r1 != null && r2 != null)
			{
				Point p1 = calculerPointBord(r1, r2);
				Point p2 = calculerPointBord(r2, r1);

				lstChemins.add(new Chemin(
						p1, p2, "ASSOCIATION",
						mapClasseRectangle,
						l.getFromClass(),
						l.getToClass()
				));
			}
		}
	}

	public void mettreAJourChemins()
	{
		for (int i = 0; i < lstChemins.size(); i++)
		{
			Liaison l = lstLiaisons.get(i);
			Chemin c  = lstChemins.get(i);

			Rectangle r1 = mapClasseRectangle.get(l.getFromClass());
			Rectangle r2 = mapClasseRectangle.get(l.getToClass());

			if (r1 != null && r2 != null)
			{
				c.recalculer(
						calculerPointBord(r1, r2),
						calculerPointBord(r2, r1)
				);
			}
		}
	}

	private Point calculerPointBord(Rectangle src, Rectangle dst)
	{
		int dx = dst.getCentreX() - src.getCentreX();
		int dy = dst.getCentreY() - src.getCentreY();

		if (Math.abs(dx) > Math.abs(dy))
			return dx > 0
					? new Point(src.getX() + src.getTailleX(), src.getCentreY())
					: new Point(src.getX(), src.getCentreY());
		else
			return dy > 0
					? new Point(src.getCentreX(), src.getY() + src.getTailleY())
					: new Point(src.getCentreX(), src.getY());
	}

	/* ================== PAINT ================== */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		for (Classe c : lstClasse)
			dessinerClasse(g2, c);

		mettreAJourChemins();
		for (Chemin c : lstChemins)
			dessinerFleche.dessinerLiaison(g2, c);
	}

	/* ================== DESSIN CLASSE ================== */
	private void dessinerClasse(Graphics2D g2, Classe classe)
	{
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		Font fontBold = font.deriveFont(Font.BOLD);
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();
		Rectangle rect = mapClasseRectangle.get(classe);

		int x = rect.getX();
		int y = rect.getY();

		/* ----- PREPARATION TEXTE ----- */
		List<String> atts = new ArrayList<>();
		List<String> meths = new ArrayList<>();

		int maxW = fm.stringWidth(classe.getNom());

		for (Attribut a : classe.getListOrdonneeAttribut())
		{
			if (ctrl.estClasseProjet(a.getType())) continue;
			String s = getVisibiliteSymbole(a.getVisibilite()) + " " + a.getNom() + " : " + a.getType();
			atts.add(s);
			maxW = Math.max(maxW, fm.stringWidth(s));
		}

		for (Methode m : classe.getListOrdonneeMethode())
		{
			if (m.getNom().equals("main")) continue;
			String s = getVisibiliteSymbole(m.getVisibilite()) + " " + m.getNom() + "()";
			meths.add(s);
			maxW = Math.max(maxW, fm.stringWidth(s));
		}

		int width = maxW + PADDING_X * 2;
		int hTitle = fm.getHeight() + PADDING_Y * 2;
		int hAtt   = atts.size() * fm.getHeight() + PADDING_Y * 2;
		int hMeth  = meths.size() * fm.getHeight() + PADDING_Y * 2;

		/* ----- DESSIN ----- */
		fillBlock(g2, x, y, width, hTitle, COL_TITRE);
		fillBlock(g2, x, y + hTitle, width, hAtt, COL_ATT);
		fillBlock(g2, x, y + hTitle + hAtt, width, hMeth, COL_METH);

		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, width, hTitle + hAtt + hMeth);

		g2.setFont(fontBold);
		drawCentered(g2, classe.getNom(), x, y + PADDING_Y, width);
		g2.setFont(font);

		int yy = y + hTitle + fm.getAscent();
		for (String s : atts)
		{
			g2.drawString(s, x + PADDING_X, yy);
			yy += fm.getHeight();
		}

		yy = y + hTitle + hAtt + fm.getAscent();
		for (String s : meths)
		{
			g2.drawString(s, x + PADDING_X, yy);
			yy += fm.getHeight();
		}

		rect.setTailleX(width);
		rect.setTailleY(hTitle + hAtt + hMeth);
	}

	/* ================== OUTILS ================== */
	private void fillBlock(Graphics2D g2, int x, int y, int w, int h, Color c)
	{
		g2.setColor(c);
		g2.fillRect(x, y, w, h);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, w, h);
	}

	private void drawCentered(Graphics g, String s, int x, int y, int w)
	{
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, x + (w - fm.stringWidth(s)) / 2, y + fm.getAscent());
	}

	private String getVisibiliteSymbole(String v)
	{
		if (v == null) return " ";
		return switch (v)
		{
			case "public" -> "+";
			case "private" -> "-";
			case "protected" -> "#";
			default -> "~";
		};
	}

	public HashMap<Classe, Rectangle> getMap()
	{
		return this.mapClasseRectangle;
	}
}

