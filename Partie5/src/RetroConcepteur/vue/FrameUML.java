package RetroConcepteur.vue;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.DiagramData;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.vue.outil.Rectangle;

import RetroConcepteur.vue.BarreMenu;

public class FrameUML extends JFrame
{
	private Controller ctrl;

	private BarreMenu barreMenu;

	private JMenuItem menuQuitter;
	private JMenuItem menuOuvrir;

	private PanelUML panelUml;

	private File dossierUML;
	
	
	public FrameUML(Controller ctrl)
	{

		this.ctrl = ctrl;

		this.setLayout( new BorderLayout() );

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setLocation(100, 100);
		this.setSize     ( screenSize.width, screenSize.height );
		this.setTitle("Diagramme UML");
		

		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */

		this.panelUml     = new PanelUML(this, this.ctrl);

		this.barreMenu    = new BarreMenu(this);

		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */

		this.setJMenuBar(this.barreMenu);
		this.add(this.panelUml, BorderLayout.CENTER);

		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */

		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void ouvrirFichier()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog( null );

		if ( returnValue == JFileChooser.APPROVE_OPTION )
		{
			this.dossierUML  = fileChooser.getSelectedFile();
			this.ctrl.ouvrirDossier("" + this.dossierUML);
			this.panelUml.reinitialiser();
			System.out.println(this.dossierUML);
		}
	}

	public void sauverFichier()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Sauvegarder le diagramme");
		fileChooser.setSelectedFile(new File("diagramme.png"));
		int valRet = fileChooser.showSaveDialog(this);
		if (valRet == JFileChooser.APPROVE_OPTION) 
		{
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".png")) 
			{
				file = new File(file.getAbsolutePath() + ".png");
			}
			try 
			{
				BufferedImage image = new BufferedImage(this.panelUml.getWidth(), this.panelUml.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = image.createGraphics();
				this.panelUml.paint(g2d);
				g2d.dispose();
				ImageIO.write(image, "png", file);
				JOptionPane.showMessageDialog(this, "Diagramme sauvegardé avec succès!");
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde: " + e.getMessage());
			}
		}
	}
	
	public void exporter()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Exporter le diagramme UML");
		fileChooser.setSelectedFile(new File("diagramme.xml"));
		
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) 
		{
			File fichier = fileChooser.getSelectedFile();
			if (!fichier.getName().endsWith(".xml")) 
			{
				fichier = new File(fichier.getAbsolutePath() + ".xml");
			}
			try (FileOutputStream out = new FileOutputStream(fichier);
				 ObjectOutputStream objtOut = new ObjectOutputStream(out)) 
			{
				/*DiagramData data = new DiagramData(
					new ArrayList<Classe>(this.panelUml.getLstClasse()),
					this.panelUml.getLstLiaisons(),
					this.panelUml.getLstArcs(),
					this.panelUml.getMap()
				);
				objtOut.writeObject(data);*/
				JOptionPane.showMessageDialog(this, "Diagramme exporté avec succès!");
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(this, "Erreur lors de l'export: " + e.getMessage());
			}
		}
	}

	public void importer()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Importer le diagramme UML");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers UML", "xml"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) 
		{
			File file = fileChooser.getSelectedFile();
			try (FileInputStream in = new FileInputStream(file);
				 ObjectInputStream objtIn = new ObjectInputStream(in)) 
			{
				/*DiagramData data = (DiagramData) objtIn.readObject();
				this.panelUml.chargerDiagramme(data);*/
				this.repaint();
				JOptionPane.showMessageDialog(this, "Diagramme importé avec succès!");
			} 

			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(this, "Erreur lors de l'import: " + e.getMessage());
			}
		}
	}
}
