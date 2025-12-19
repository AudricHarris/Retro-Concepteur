package retroconcepteur.vue.panel;

import retroconcepteur.Controleur;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.vue.FrameEdition;
import retroconcepteur.metier.classe.Attribut;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panneau dedie a l'edition des proprietes specifiques des attributs d'une classe UML.
 * Il permet de modifier le nom, et de definir si un attribut est "Frozen" (constante)
 */
public class PanelEditionAttribut extends JPanel implements ActionListener
{
	private Controleur ctrl;
	private FrameEdition frmParent;
	private Classe classe;

	private JButton btnValider;
	
	// Listes pour conserver les references aux composants graphiques generes dynamiquement
	private List<JCheckBox> listeCbFrozen;
	private List<JTextField> listeTxtNom;

	/**
	 * Constructeur du panneau d'edition des attributs.
	 * Genere une ligne de modification pour chaque attribut existant dans la classe.
	 *
	 * @param ctrl Le contrdoleur de l'application.
	 * @param frmParent La fenetre parente contenant ce panneau.
	 * @param classe La classe dont on veut modifier les attributs.
	 */
	public PanelEditionAttribut(Controleur ctrl, FrameEdition frmParent, Classe classe) 
	{
		this.ctrl = ctrl;
		this.classe = classe;
		this.frmParent = frmParent;
		
		this.listeCbFrozen  = new ArrayList<JCheckBox>();
		this.listeTxtNom    = new ArrayList<JTextField>();
		
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel panelGrille = new JPanel(new GridLayout(0, 2, 5, 0));
		
		panelGrille.add(creerLabelTitre("Nom"));
		panelGrille.add(creerLabelTitre("{freeze}"));

		for (Attribut att : classe.getLstAttribut()) 
		{
			JTextField txtNom = new JTextField(" " + att.getNom());
			txtNom.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
			panelGrille.add(txtNom);
			this.listeTxtNom.add(txtNom);

			JCheckBox cbFrozen = new JCheckBox();
			cbFrozen.setHorizontalAlignment(SwingConstants.CENTER);
			cbFrozen.setSelected(att.isConstante());
			cbFrozen.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
			cbFrozen.setBorderPainted(true);
			panelGrille.add(cbFrozen);
			this.listeCbFrozen.add(cbFrozen);
		}

		JPanel panelConteneur = new JPanel(new BorderLayout());
		panelConteneur.add(panelGrille, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(panelConteneur);
		scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		this.add(scroll, BorderLayout.CENTER);

		this.btnValider = new JButton("Valider");
		this.btnValider.addActionListener(this);
		this.add(this.btnValider, BorderLayout.SOUTH);
	}

	/*------------------------------------------*/
	/*            Methode d'instance            */
	/*------------------------------------------*/

	/**
	 * Cree une etiquette stylisee pour les en-tetes de colonnes.
	 *
	 * @param texte Le texte de l'en-tete.
	 * @return Un JLabel mis en forme avec un fond gris et une bordure.
	 */
	private JLabel creerLabelTitre(String texte) 
	{
		JLabel lbl = new JLabel(texte, SwingConstants.CENTER);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		lbl.setOpaque(true);
		lbl.setBackground(new Color(220, 220, 220)); 
		lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)); 
		return lbl;
	}

	/*------------------------------------------*/
	/*              Methode privee              */
	/*------------------------------------------*/

	/**
	 * Gere les actions de l'utilisateur (clic sur Valider).
	 * Parcourt tous les champs, met a jour les attributs via le controleur,
	 * rafraichit l'affichage principal et ferme la fenetre.
	 *
	 * @param e L'evenement declencheur.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnValider) 
		{
			for (int i = 0; i < this.listeCbFrozen.size(); i++) 
			{
				JCheckBox cbFrozen = this.listeCbFrozen.get(i);
				String nom = this.listeTxtNom.get(i).getText().trim();
				Attribut att = this.classe.getLstAttribut().get(i);

				if (!nom.isEmpty())
				{
					this.ctrl.majAttribut(att, nom, cbFrozen.isSelected());
				}
			}
			this.ctrl.majIHM();
			this.frmParent.dispose();
		}
	}
}