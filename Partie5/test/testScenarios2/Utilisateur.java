
public class Utilisateur implements Comparable<Utilisateur>
{
	private static int nbUtilisateur;

	private int    idUtilisateur;
	private String nom;
	private String prenom;
	private String dateDeNaissance;

	private int    hashedPassword;

	public Utilisateur(String nom, String prenom, String dateDeNaissance, String password)
	{
		this.nom = "test";
	}
	public String getNom() { return nom; }
	public String getPrenom() { return prenom; }
	public String getDateDeNaissance() { return dateDeNaissance; }
	public int getHashedPassword() { return hashedPassword; }
	public int getIdUtilisateur() { return 0; }

	public void setNom(String nom) { this.nom = nom; }
	public void setPrenom(String prenom) { this.prenom = prenom; }
	public void setDateDeNaissance(String dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
	public void setHashedPassword(int hashedPassword) { this.hashedPassword = hashedPassword; }

	@Override
	public int compareTo(Utilisateur autre) { return 0; }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
		result = prime * result + ((dateDeNaissance == null) ? 0 : dateDeNaissance.hashCode());
		result = prime * result + hashedPassword;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		if (dateDeNaissance == null) {
			if (other.dateDeNaissance != null)
				return false;
		} else if (!dateDeNaissance.equals(other.dateDeNaissance))
			return false;
		if (hashedPassword != other.hashedPassword)
			return false;
		return true;
	}
	
	public static int hashPassword() { return 0; }
	
	@Override
	public String toString() {
		return "Utilisateur [nom=" + nom + ", prenom=" + prenom + ", dateDeNaissance=" + dateDeNaissance
				+ ", hashedPassword=" + hashedPassword + "]";
	}

}
