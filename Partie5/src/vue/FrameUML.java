package vue;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import controller.*;

public class FrameUML extends JFrame implements ActionListener
{
	private Controller ctrl;

	private JMenuBar barFichier;

	private JMenuItem menuQuitter;
	private JMenuItem menuOuvrir;

	private PanelUML panelUml;

	private File dossierUML;
	
	
	public FrameUML(Controller ctrl)
	{

		this.ctrl = ctrl;

		this.setLayout( new BorderLayout() );

		this.setLocation(100, 100);
		this.setSize(1000, 700);
		this.setTitle("Diagramme UML");
		

		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */

		this.panelUml   = new PanelUML(this, this.ctrl);

		this.barFichier = new JMenuBar();

		JMenu menuFichier = new JMenu("Fichier");

		this.menuOuvrir = new JMenuItem("Ouvrir");
		this.menuQuitter = new JMenuItem("Quitter");

		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */

		menuFichier.add(this.menuOuvrir);
		menuFichier.add(this.menuQuitter);

		this.barFichier.add(menuFichier);

		this.setJMenuBar(barFichier);
		this.add(this.panelUml, BorderLayout.CENTER);

		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */
		
		this.menuOuvrir.addActionListener(this);
		this.menuQuitter.addActionListener(this);

		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}


	public void actionPerformed( ActionEvent evt )
	{
		if ( evt.getSource() == this.menuOuvrir )
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog( null );

			if ( returnValue == JFileChooser.APPROVE_OPTION )
			{
				this.dossierUML  = fileChooser.getSelectedFile();
				System.out.println(this.dossierUML);
			}
			
		}

		if ( evt.getSource() == this.menuQuitter )
		{
			System.exit( 0 );
		}
			
	}
}
