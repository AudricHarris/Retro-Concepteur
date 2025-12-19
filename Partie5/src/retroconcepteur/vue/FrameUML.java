package retroconcepteur.vue;

// Nos paquetage
import retroconcepteur.Controleur;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.vue.outil.Rectangle;
import retroconcepteur.vue.panel.PanelUML;
import retroconcepteur.vue.outil.BarreMenu;

// Paquetage AWT
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics2D;

// Paquetage IO
import java.io.File;
import java.io.IOException;

// Paquetage Util
import java.util.HashMap;

// Paquetage Swing & Image
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.imageio.ImageIO;

/**
 * Fenetre principale de l'application de Retro-Conception.
 * Elle contient le panneau de dessin (PanelUML) et la barre de menu.
 * Elle gere egalement les dialogues d'ouverture et de sauvegarde (Fichiers et Images).
 */
public class FrameUML extends JFrame
{
	private Controleur  ctrl;
	private BarreMenu   barreMenu;
	private PanelUML    panelUml;
	private File        dossierUML;
	
	/**
	 * Constructeur de la fenetre principale.
	 * Configure la taille, le titre et initialise les composants graphiques.
	 * @param ctrl Le contrdoleur de l'application.
	 */
	public FrameUML(Controleur ctrl)
	{
		Dimension screenSize;

		this.ctrl = ctrl;
		
		// Configuration de la fenetre
		this.setLayout   (new BorderLayout());
		screenSize       = Toolkit.getDefaultToolkit().getScreenSize();

		this.setLocation (100, 100);
		this.setSize     (screenSize.width, screenSize.height);
		this.setTitle    ("Diagramme UML");
		
		/* ----------------------------- */
		/* Creation des Composants       */
		/* ----------------------------- */

		this.panelUml    = new PanelUML(this, this.ctrl);
		this.barreMenu   = new BarreMenu(this);

		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */

		this.setJMenuBar (this.barreMenu);
		this.add         (this.panelUml, BorderLayout.CENTER);

		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */

		this.setVisible              (true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public HashMap<Classe, Rectangle> getMapPanel()
	{
		return new HashMap<Classe, Rectangle>(panelUml.getMap());
	}

	public void setMapPanel(HashMap<Classe, Rectangle> map) { this.panelUml.setMap(map); }

	public void reinitialiser() { this.panelUml.reinitialiser(); }

	public void majIHM() { this.panelUml.repaint(); }

	/**
	 * Ouvre un selecteur de fichier pour choisir le dossier contenant les sources Java a analyser.
	 */
	public void ouvrirFichier()
	{
		JFileChooser fileChooser;
		int          returnValue;

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			this.dossierUML = fileChooser.getSelectedFile();
			this.ctrl.ouvrirDossier("" + this.dossierUML);
			this.panelUml.reinitialiser();
		}
	}

	/**
	 * Exporte le contenu visuel du panneau UML vers un fichier image PNG.
	 */
	public void exporterImage()
	{
		JFileChooser  fileChooser;
		int           valRet;
		File          file;
		BufferedImage image;
		Graphics2D    g2d;

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Sauvegarder le diagramme");
		fileChooser.setSelectedFile(new File("diagramme.png"));
		
		valRet = fileChooser.showSaveDialog(this);

		if (valRet == JFileChooser.APPROVE_OPTION) 
		{
			file = fileChooser.getSelectedFile();
			
			if (!file.getName().endsWith(".png")) 
				file = new File(file.getAbsolutePath() + ".png");
			
			
			try 
			{
				image = new BufferedImage(this.panelUml.getWidth(), this.panelUml.getHeight(), BufferedImage.TYPE_INT_RGB);
				g2d   = image.createGraphics();
				
				this.panelUml.paint(g2d);
				g2d.dispose();
				
				ImageIO.write(image, "png", file);
				
				JOptionPane.showMessageDialog(this, "Diagramme sauvegarde avec succes !");
			} 
			catch (IOException e){JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + e.getMessage());}
		}
	}

	/**
	 * Sauvegarde l'etat actuel du diagramme (positions, notes, etc.) dans un fichier XML.
	 */
	public void sauvegardeFichier()
	{
		JFileChooser fileChooser;
		int          valRet;
		File         file;

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Sauvegarder le diagramme en XML");
		fileChooser.setSelectedFile(new File("diagramme.xml"));
		
		valRet = fileChooser.showSaveDialog(this);

		if (valRet == JFileChooser.APPROVE_OPTION)
		{
			file = fileChooser.getSelectedFile();
			
			if (!file.getName().endsWith(".xml")) 
				file = new File(file.getAbsolutePath() + ".xml");
			
			try 
			{
				this.ctrl.sauvegarderXml(file.getAbsolutePath());
				JOptionPane.showMessageDialog(this, "Diagramme sauvegarde en XML avec succes !");
			} 
			catch (Exception e) {JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde en XML : " + e.getMessage());}
		}
	}

	/**
	 * Charge un diagramme depuis un fichier XML existant pour restaurer les positions.
	 */
	public void ouvrirXml()
	{
		JFileChooser            fileChooser;
		FileNameExtensionFilter filter;
		int                     valRet;
		File                    file;

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Ouvrir un diagramme XML");

		filter      = new FileNameExtensionFilter("Fichiers XML", "xml");
		fileChooser.setFileFilter(filter);

		valRet      = fileChooser.showOpenDialog(this);

		if (valRet == JFileChooser.APPROVE_OPTION)
		{
			file = fileChooser.getSelectedFile();
			try
			{
				this.ctrl.chargerXml(file.getAbsolutePath());
				JOptionPane.showMessageDialog(this, "Diagramme XML charge avec succes !");
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, "Erreur lors du chargement du XML : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Active ou desactive l'affichage des classes cachables (interfaces/heritage JDK) dans le panneau.
	 * @param afficher true pour afficher, false pour masquer.
	 */
	public void afficherImplHerit(boolean afficher)
	{
		this.panelUml.afficherInterfaceHeritage(afficher);
	}
}