package RetroConcepteur.vue;



import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;

public class FrameEdition extends JFrame
{
    // 1. Ajoutez cette variable statique (partagée par toute l'application)
    private static FrameEdition instanceOuverte = null;
	private Controller ctrl;
	private JPanel pnlEditionClasse;
	private JPanel pnlEditionAttribut;
	private JPanel pnlEditionMethode;

	public FrameEdition(Controller ctrl, Classe classe, char type)
	{
        // 2. AJOUTEZ CE BLOC AU TOUT DÉBUT DU CONSTRUCTEUR
        if (instanceOuverte != null) 
        {
            instanceOuverte.dispose(); // On ferme l'ancienne fenêtre proprement
        }
        instanceOuverte = this; // On enregistre celle-ci comme la nouvelle fenêtre active
		
		this.ctrl = ctrl;
		switch (type) 
		{
			case 'C':
				if (this.pnlEditionClasse == null) 
					this.pnlEditionClasse = new PanelEditionClasse(ctrl, classe);
				else
				{
					this.pnlEditionClasse.removeAll();
					this.pnlEditionClasse = new PanelEditionClasse(ctrl, classe);
				}
				break;
			case 'A':
				if (this.pnlEditionAttribut == null)
					this.pnlEditionAttribut = new PanelEditionAttribut(ctrl,this, classe);
				else
				{
					this.pnlEditionAttribut.removeAll();
					this.pnlEditionAttribut = new PanelEditionAttribut(ctrl,this, classe);
				}
				break;
			case 'M':
				if (this.pnlEditionMethode == null)
					this.pnlEditionMethode = new PanelEditionMethode(ctrl, classe);
				else
				{
					this.pnlEditionMethode.removeAll();
					this.pnlEditionMethode = new PanelEditionMethode(ctrl, classe);
				}
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

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);

		this.setVisible(true);
		
	}

}
