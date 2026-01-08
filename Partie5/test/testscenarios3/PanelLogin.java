

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// PanelLogin.java - Panel pour la connexion et création de compte
public class PanelLogin extends JPanel {
	protected FrameBoutique frame;
	protected JTextField txtNom;
	protected JTextField txtPrenom;
	protected JPasswordField txtMdp;
	protected JButton btnConnexion;
	protected JButton btnCreerCompte;

	public PanelLogin(FrameBoutique frame) {
		this.frame = frame;
		initialiserPanel();
		ajouterEcouteurs();
	}

	private void initialiserPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		// Labels et champs
		gbc.gridx = 0; gbc.gridy = 0;
		add(new JLabel("Nom:"), gbc);
		gbc.gridx = 1;
		txtNom = new JTextField(15);
		add(txtNom, gbc);

		gbc.gridx = 0; gbc.gridy = 1;
		add(new JLabel("Prénom:"), gbc);
		gbc.gridx = 1;
		txtPrenom = new JTextField(15);
		add(txtPrenom, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		add(new JLabel("Mot de passe:"), gbc);
		gbc.gridx = 1;
		txtMdp = new JPasswordField(15);
		add(txtMdp, gbc);

		// Boutons
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
		btnConnexion = new JButton("Se connecter");
		add(btnConnexion, gbc);

		gbc.gridy = 4;
		btnCreerCompte = new JButton("Créer un compte");
		add(btnCreerCompte, gbc);
	}

	private void ajouterEcouteurs() {
		btnConnexion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Appeler le contrôleur via la frame
				String nom = txtNom.getText();
				String prenom = txtPrenom.getText();
				String mdp = new String(txtMdp.getPassword());
				// Simulation d'appel au contrôleur (à connecter dans Controleur)
				frame.afficherMessage("Connexion tentée pour " + nom + " " + prenom);
				frame.switchToPanel("boutique"); // Switch pour test
			}
		});

		btnCreerCompte.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Ajouter date de naissance pour création complète
				frame.afficherMessage("Création de compte (date de naissance manquante pour simplifier)");
				frame.switchToPanel("boutique");
			}
		});
	}
}
