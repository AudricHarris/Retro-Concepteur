package retroconcepteur.metier.classe;

/**
 * Class parametres stock les informations des parametres d'une methode
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
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

	/*---------------------------------------*/
	/*            Accesseurs                 */
	/*---------------------------------------*/
	
	public String getNom () { return this.nom;  }
	public String getType() { return this.type; }
}
