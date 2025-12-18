package RetroConcepteur.vue;



import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;

public class FrameEdition extends JFrame
{
	private Controller ctrl;
	private JPanel pnlEditionClasse;
	private JPanel pnlEditionAttribut;
	private JPanel pnlEditionMethode;

	public FrameEdition(Controller ctrl, Classe classe, char type)
	{
		this.ctrl = ctrl;
		switch (type) 
		{
			case 'C':
				this.pnlEditionClasse = new PanelEditionClasse(ctrl, classe);
				break;
			case 'A':
				this.pnlEditionAttribut = new PanelEditionAttribut(ctrl, classe);
				break;
			case 'M':
				this.pnlEditionMethode = new PanelEditionMethode(ctrl, classe);
				break;
		
			default:
				break;
		}
		JLabel lblTitle = new JLabel("Edition de la classe : " + classe.getNom());
		this.setTitle("Edition de la classe : " + classe.getNom());
		this.add(lblTitle);
		if (type == 'C')
			this.add(this.pnlEditionClasse);
		else if (type == 'A')
			this.add(this.pnlEditionAttribut);
		else if (type == 'M')
			this.add(this.pnlEditionMethode);

		this.setSize(500, 300);
		this.setVisible(true);
		
	}

}
