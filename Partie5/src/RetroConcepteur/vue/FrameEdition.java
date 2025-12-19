package RetroConcepteur.vue;



import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import RetroConcepteur.Controleur;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.vue.panel.PanelEditionAttribut;

public class FrameEdition extends JFrame
{
    // 1. Ajoutez cette variable statique (partagée par toute l'application)
    private static FrameEdition instanceOuverte = null;
	private Controleur ctrl;
	private JPanel pnlEditionClasse;
	private JPanel pnlEditionAttribut;
	private JPanel pnlEditionMethode;

	public FrameEdition(Controleur ctrl, Classe classe, char type)
	{
        // 2. AJOUTEZ CE BLOC AU TOUT DÉBUT DU CONSTRUCTEUR
        if (instanceOuverte != null) 
        {
            instanceOuverte.dispose(); 
        }
        instanceOuverte = this;
		
		this.ctrl = ctrl;
		
		if (this.pnlEditionAttribut == null)
			this.pnlEditionAttribut = new PanelEditionAttribut(ctrl,this, classe);
		else
		{
			this.pnlEditionAttribut.removeAll();
			this.pnlEditionAttribut = new PanelEditionAttribut(ctrl,this, classe);
		}

		JLabel lblTitle = new JLabel("Edition de la classe : " + classe.getNom());
		this.setTitle("Edition de la classe : " + classe.getNom());
		this.add(lblTitle);
		
		this.add(this.pnlEditionAttribut);

		this.setSize(500, 300);
		this.setVisible(true);
		
	}

}
