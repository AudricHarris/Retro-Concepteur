package RetroConcepteur.vue;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelEditionMethode extends JPanel implements ActionListener
{
	Controller ctrl;
	FrameEdition frmParent;
	JButton btnValider;
	
	public PanelEditionMethode(Controller ctrl, FrameEdition frmParent, Classe classe)
	{
		this.ctrl = ctrl;
		this.frmParent = frmParent;
		// TODO : faire la classe d'édition de méthode
		JPanel mainPanel = new JPanel();
		this.btnValider = new JButton("Valider");
		mainPanel.add(this.btnValider);
		this.add(mainPanel);
	}

	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
}
