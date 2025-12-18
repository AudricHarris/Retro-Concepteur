package RetroConcepteur.vue;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;
import javax.swing.JPanel;

public class PanelEditionMethode extends JPanel
{
	Controller ctrl;
	public PanelEditionMethode(Controller ctrl, Classe classe)
	{
		this.ctrl = ctrl;
	}
}
