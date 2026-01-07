public class Produit
{
	public static final int NOMBRE_MAX_PROD = 500;
	private static int nbProduit;

	private int idProduit;
	private String nomProduit;
	private int prixProduit;
	
	public Produit(String nomProduit, int prixProduit) {
		this.nomProduit = nomProduit;
		this.prixProduit = prixProduit;
	}

	public static int getNombreMaxProd() {
		return NOMBRE_MAX_PROD;
	}

	public static int getNbProduit() {
		return nbProduit;
	}

	public static void setNbProduit(int nbProduit) {
		Produit.nbProduit = nbProduit;
	}

	public int getIdProduit() {
		return idProduit;
	}

	public void setIdProduit(int idProduit) {
		this.idProduit = idProduit;
	}

	public String getNomProduit() {
		return nomProduit;
	}

	public void setNomProduit(String nomProduit) {
		this.nomProduit = nomProduit;
	}

	public int getPrixProduit() {
		return prixProduit;
	}

	public void setPrixProduit(int prixProduit) {
		this.prixProduit = prixProduit;
	}

	public boolean acheterProduit(int nbProduit)
	{
		return true;
	}
 
	@Override
	public String toString() {
		return "Produit [idProduit=" + idProduit + ", nomProduit=" + nomProduit + ", prixProduit=" + prixProduit + "]";
	}

}
