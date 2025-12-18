package RetroConcepteur.vue;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;
import javax.swing.JPanel;

public class PanelEditionAttribut extends JPanel
{
	Controller ctrl;
	public PanelEditionAttribut(Controller ctrl, Classe classe)
	{
		this.ctrl = ctrl;
	}
}
