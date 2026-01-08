

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// PanelBoutique.java - Panel principal pour les actions utilisateur (achat, etc.)
public class PanelBoutique extends JPanel {
	protected FrameBoutique frame;
	protected Utilisateur utilisateurConnecte;
	protected JButton btnAcheter;
	protected JButton btnDeconnexion;
	protected JButton btnAfficherProduits;
	protected JTextField txtIdProduit;

	public PanelBoutique(FrameBoutique frame) {
		this.frame = frame;
		initialiserPanel();
		ajouterEcouteurs();
	}

	private void initialiserPanel() {
		setLayout(new BorderLayout());

		// Panel nord pour boutons
		JPanel panelNord = new JPanel(new FlowLayout());
		btnAcheter = new JButton("Acheter un produit");
		btnAfficherProduits = new JButton("Afficher produits");
		btnDeconnexion = new JButton("Déconnexion");
		panelNord.add(btnAcheter);
		panelNord.add(btnAfficherProduits);
		panelNord.add(btnDeconnexion);
		add(panelNord, BorderLayout.NORTH);

		// Panel centre pour saisie
		JPanel panelCentre = new JPanel(new FlowLayout());
		panelCentre.add(new JLabel("ID Produit:"));
		txtIdProduit = new JTextField(10);
		panelCentre.add(txtIdProduit);
		add(panelCentre, BorderLayout.CENTER);

		// Label pour utilisateur
		JLabel lblUser = new JLabel("Utilisateur non connecté");
		add(lblUser, BorderLayout.SOUTH);
	}

	private void ajouterEcouteurs() {
		btnAcheter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (utilisateurConnecte != null) {
					int id = Integer.parseInt(txtIdProduit.getText());
					frame.afficherMessage("Achat pour ID " + id + " par " + utilisateurConnecte.getNom());
				} else {
					frame.afficherMessage("Veuillez vous connecter.");
				}
			}
		});

		btnAfficherProduits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.switchToPanel("produits");
			}
		});

		btnDeconnexion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				utilisateurConnecte = null;
				frame.switchToPanel("login");
			}
		});
	}

	public void setUtilisateurConnecte(Utilisateur u) {
		this.utilisateurConnecte = u;
		if (u != null) {
			((JLabel) getComponent(3)).setText("Connecté: " + u.getNom() + " " + u.getPrenom());
		}
	}
}
