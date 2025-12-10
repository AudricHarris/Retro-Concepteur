package metier.classe;

// Record paramètres stock les informations des paramètres d'une méthode
public class Parametre
{
	private String nom;
	private String type;

	public Parametre(String nom, String type)
	{
		this.nom  = nom;
		this.type = type;
	}

	public String getNom () { return this.nom;  }
	public String getType() { return this.type; }
}
