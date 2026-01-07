import java.util.List;

// Controleur.java - Contrôleur complet pour l'application MVC Boutique avec GUI (sans adaptateurs)
public class Controleur {
	protected Boutique metier;  // Le modèle/métier
	protected FrameBoutique frame;  // La vue GUI
	protected Utilisateur utilisateurConnecte;  // État de l'utilisateur connecté

	public Controleur() {
		this.metier = new Boutique();
		this.frame = new FrameBoutique(this);
		this.utilisateurConnecte = null;
		// Les listeners sont maintenant gérés directement dans les panels (via méthodes exposées)
	}

	// Méthodes appelées par la vue (logique métier et mise à jour vue)
	public void gererConnexion(String nom, String prenom, String mdp) {
		boolean succes = metier.connecter(nom, prenom, mdp);
		if (succes) {
			// Simulation: créer utilisateur (à améliorer avec vrai retour du métier)
			utilisateurConnecte = new Utilisateur(nom, prenom, "1990-01-01", mdp); // Date placeholder
			frame.setUtilisateurConnecte(utilisateurConnecte);
			frame.switchToPanel("boutique");
			// Vérifier si admin et switcher si nécessaire
			if (estAdminConnecte()) {
				frame.switchToPanel("admin");
			}
			frame.afficherMessage("Connexion réussie!");
		} else {
			frame.afficherMessage("Échec de connexion. Vérifiez vos identifiants.");
		}
	}

	public void gererCreationCompte(String nom, String prenom, String dateDeNaissance, String mdp) {
		Utilisateur nouveau = metier.creerCompte(nom, prenom, dateDeNaissance, mdp);
		if (nouveau != null) {
			frame.afficherMessage("Compte créé avec succès! Vous pouvez maintenant vous connecter.");
		} else {
			frame.afficherMessage("Échec de création de compte. Vérifiez les informations.");
		}
	}

	public void gererAjoutProduit(String nomProduit, int prixProduit) {
		if (!estAdminConnecte()) {
			frame.afficherMessage("Accès refusé. Seuls les admins peuvent ajouter des produits.");
			return;
		}
		Produit p = new Produit(nomProduit, prixProduit);
		// Assigner un ID (simulation, à améliorer avec compteur dans Produit ou Boutique)
		p.setIdProduit((int) (Math.random() * 1000) + 1);
		boolean succes = metier.ajouterProduit(p);
		if (succes) {
			frame.afficherMessage("Produit ajouté avec succès!");
			// Optionnel: rafraîchir la liste des produits
			gererAffichageProduits();
		} else {
			frame.afficherMessage("Échec d'ajout du produit. Vérifiez les paramètres.");
		}
	}

	public void gererAchat(Utilisateur u, int idProduit) {
		if (u == null) {
			frame.afficherMessage("Veuillez vous connecter avant d'acheter.");
			return;
		}
		// Simulation: créer Produit par ID (à améliorer: méthode getProduitById dans Boutique)
		Produit p = new Produit("Produit ID " + idProduit, 100); // Placeholder
		p.setIdProduit(idProduit);
		boolean succes = metier.acheterProduit(u, p);
		if (succes) {
			frame.afficherMessage("Achat effectué avec succès pour " + p.getNomProduit() + "!");
		} else {
			frame.afficherMessage("Échec de l'achat. Produit indisponible ou stock insuffisant.");
		}
	}

	public void gererAffichageProduits() {
		// Récupérer la liste du métier (assumer que getLstProduits() est implémenté pour retourner une vraie liste)
		List<Produit> produits = metier.getLstProduits();
		// Récupérer le panel et mettre à jour (accès via attributs protégés de FrameBoutique)
		PanelProduits panel = (PanelProduits) frame.panelPrincipal.getComponent(3);
		panel.majProduits(produits);
		frame.switchToPanel("produits");
	}

	public void gererDeconnexion() {
		utilisateurConnecte = null;
		frame.switchToPanel("login");
		frame.afficherMessage("Déconnexion réussie.");
	}

	// Méthode utilitaire pour vérifier si l'utilisateur connecté est admin
	protected boolean estAdminConnecte() {
		return utilisateurConnecte instanceof Admin;
	}

	// Méthodes utilitaires exposées pour la frame et les panels
	public void switchToPanel(String panelName) {
		frame.switchToPanel(panelName);
	}

	public void afficherMessage(String message) {
		frame.afficherMessage(message);
	}

	// Accès au métier pour les panels si besoin (ex. pour listes)
	public Boutique getMetier() {
		return metier;
	}

	public Utilisateur getUtilisateurConnecte() {
		return utilisateurConnecte;
	}

	public static void main(String[] args) {
		// Lancer l'application avec GUI
		new Controleur();
	}
}
