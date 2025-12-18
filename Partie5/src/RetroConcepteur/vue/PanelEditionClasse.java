package RetroConcepteur.vue;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelEditionClasse extends JPanel implements ActionListener
{
	Controller ctrl;
	Checkbox cbFrozen;
	Checkbox cbAddOnly;
	Checkbox cbRequete;
	JButton  btnValider;
	public PanelEditionClasse(Controller ctrl, Classe classe)
	{
		this.ctrl = ctrl;
		JPanel mainPanel = new JPanel( new GridLayout(4, 1) );
		JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblZoneOption = new JLabel("Options : ");
		this.add(lblZoneOption);
		this.cbFrozen = new Checkbox("Frozen");
		this.cbAddOnly = new Checkbox("Add Only");
		this.cbRequete = new Checkbox("Requete");

		mainPanel.add(optionsPanel);

		optionsPanel.add(lblZoneOption);
		optionsPanel.add(this.cbFrozen);
		optionsPanel.add(this.cbAddOnly);
		optionsPanel.add(this.cbRequete);
		this.btnValider = new JButton("Valider");
		mainPanel.add(this.btnValider);
		this.add(mainPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnValider)
		{
			// TODO : Appliquer les changements à la classe
		}
	}
}
