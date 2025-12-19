package retroconcepteur.metier.classe;

/**
 * Class Attribut est une instance qui permet de stocker les attribut d'un classe
 * Elle contient plus paramètres comme type, visibilite, isStatic, son nom et un num
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
public class Attribut 
{
	private int     num;
	private String  nom;
	private String  type;
	private String  visibilite;
	private boolean constante;
	private boolean isStatic;
	private boolean isAddOnly;


	/**
	 * Constructeur d'Attribut
	 * @param num numero de l'attribut
	 * @param nom nom de l'attribut
	 * @param constante determine si l'attribut est constante
	 * @param type type de l'attribut
	 * @param visibilite Visibilité de l'attribut
	 * @param isStatic determine si attribut est static
	 * @param isAddOnly determine si attribut est addOnly
	 */
	public Attribut(int num, String nom, boolean constante, String type, String visibilite, boolean isStatic, boolean isAddOnly)
	{
		this.num        = num;
		this.nom        = nom;
		this.constante  = constante;
		this.type       = type;
		this.visibilite = visibilite;
		this.isStatic   = isStatic;
		this.isAddOnly  = isAddOnly;
	}

	/*---------------------------------------*/
	/*              Modificateurs            */
	/*---------------------------------------*/

	public void setNom       (String nom       ) { this.nom = nom             ;}
	public void setConstante (boolean constante) {this.constante  = constante ;}
	public void setAddOnly   (boolean isAddOnly) {this.isAddOnly  = isAddOnly ;}
	
	/*---------------------------------------*/
	/*              Accesseurs               */
	/*---------------------------------------*/

	public int     getNum       () {return this.num       ;}
	public String  getNom       () {return this.nom       ;}
	public boolean isConstante  () {return this.constante ;}
	public String  getType      () {return this.type      ;}
	public String  getVisibilite() {return this.visibilite;}
	public boolean isStatic     () {return this.isStatic  ;}
	public boolean isAddOnly    () {return this.isAddOnly ;}

	/*---------------------------------------*/
	/*         Methode instance              */
	/*---------------------------------------*/

	@Override
	public String toString()
	{
		String sRet ="";

		sRet += "attribut : " + this.num + "	nom: "  ;
		sRet += String.format("%-18s", this.nom ) +  "  type : ";
		sRet += String.format("%-20s", this.type) + "  visibilité : ";
		sRet += String.format("%-10s", this.visibilite) + "  portée : ";

		sRet += String.format("%-9s", this.isStatic ? "classe" : "instance");

		sRet += this.constante ? "  constante" : "";

		return sRet;
	}
}


