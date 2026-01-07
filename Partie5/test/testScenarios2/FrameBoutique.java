import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// FrameBoutique.java - La frame principale de l'application GUI
public class FrameBoutique extends JFrame {
	protected Controleur controleur; // Référence au contrôleur pour les interactions
	protected JPanel panelPrincipal; // Panel principal contenant les sous-panels
	protected CardLayout cardLayout; // Layout pour switcher entre panels

	public FrameBoutique(Controleur controleur) {
		this.controleur = controleur;
		initialiserFrame();
		initialiserLayout();
		setVisible(true);
	}

	private void initialiserFrame() {
		setTitle("Boutique en Ligne - MVC GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null); // Centrer la fenêtre
	}

	private void initialiserLayout() {
		cardLayout = new CardLayout();
		panelPrincipal = new JPanel(cardLayout);

		// Ajouter les panels initiaux
		panelPrincipal.add(new PanelLogin(this), "login");
		panelPrincipal.add(new PanelBoutique(this), "boutique");
		panelPrincipal.add(new PanelAdmin(this), "admin");
		panelPrincipal.add(new PanelProduits(this), "produits");

		add(panelPrincipal);
		cardLayout.show(panelPrincipal, "login"); // Démarrer par le login
	}

	// Méthodes publiques pour switcher panels et notifier le contrôleur
	public void switchToPanel(String panelName) {
		cardLayout.show(panelPrincipal, panelName);
	}

	public void afficherMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public void setUtilisateurConnecte(Utilisateur u) {
		// Méthode pour notifier le panel boutique de l'utilisateur connecté
		if (u != null) {
			((PanelBoutique) panelPrincipal.getComponent(1)).setUtilisateurConnecte(u);
		}
	}
}
