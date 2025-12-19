package retroconcepteur.metier.classe;

/**
 * Class paramètres stock les informations des paramètres d'une méthode
 * */
public class Parametre
{
	private String nom;
	private String type;

	/**
	 * Constructeur de Parametre
	 * @param nom  nom du Parametre
	 * @param type type du Parametre
	 * */
	public Parametre(String nom, String type)
	{
		this.nom  = nom;
		this.type = type;
	}

	//---------------------------------------//
	//            Getter                     //
	//---------------------------------------//
	public String getNom () { return this.nom;  }
	public String getType() { return this.type; }
}
