import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Boutique
{
	private List<Utilisateur> lstUtilisateurs;
	private List<Produit>     lstProduits;
	private HashMap<Utilisateur, List<Produit>> historiqueVentes;

	public Boutique()
	{}

	public List<Utilisateur> getLstUtilisateurs() { return null; }
	public List<Produit>     getLstProduits() { return null; }
	public HashMap<Utilisateur, List<Produit>> getHistoriqueVentes() { return null; }
	public int getNbStock(){ return 10; }

	public boolean connecter( String nom, String prenom, String mdp) { return false; }
	public Utilisateur creerCompte(String nom, String prenom, String dateDeNaissance, String mdp)
	{
		return null;
	}

	public boolean ajouterProduit(Produit p ) { return true;}
	public boolean acheterProduit(Utilisateur u, Produit p) { return true; }
}
