package retroconcepteur.vue;

import retroconcepteur.Controleur;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.vue.panel.PanelEditionAttribut;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Fenetre flottante permettant l'edition des proprietes d'une classe (Attributs, etc.).
 * Elle gere un mecanisme d'instance unique pour eviter d'ouvrir plusieurs fenetres d'edition a la fois.
 */

public class FrameEdition extends JFrame
{
	// Variable statique pour conserver la reference de la fenetre active
	private static FrameEdition instanceOuverte = null;

	private Controleur          ctrl;
	private JPanel              pnlEditionAttribut;

	/**
	 * Constructeur de la fenetre d'edition.
	 * Ferme automatiquement toute autre fenetre d'edition deja ouverte.
	 *
	 * @param ctrl Le controleur de l'application.
	 * @param classe La classe metier a modifier.
	 * @param type Le type d'edition (Actuellement non utilise, par defaut 'A' pour attributs).
	 */
	public FrameEdition(Controleur ctrl, Classe classe, char type)
	{
		JLabel lblTitle;

		if (FrameEdition.instanceOuverte != null) 
			FrameEdition.instanceOuverte.dispose(); 
		
		FrameEdition.instanceOuverte = this;
		
		this.ctrl = ctrl;
		
		this.setTitle("edition de la classe : " + classe.getNom());
		this.setSize (500, 300);
		this.setLayout(new BorderLayout(0, 10));

		lblTitle = new JLabel("edition de la classe : " + classe.getNom(), SwingConstants.CENTER);
		
		this.pnlEditionAttribut = new PanelEditionAttribut(this.ctrl, this, classe);

		
		this.add(lblTitle, BorderLayout.NORTH);
		this.add(this.pnlEditionAttribut, BorderLayout.CENTER);

		this.setLocationRelativeTo(null); 
		this.setVisible(true);
	}
}