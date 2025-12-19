package retroconcepteur.metier.classe;

/**
 * Class Attribut est une instance qui permet de stocker les attribut d'un classe
 * Elle contient plus parametres comme type, visibilite, estStatic, son nom et un num
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
	private boolean estStatic;


	/**
	 * Constructeur d'Attribut
	 * @param num numero de l'attribut
	 * @param nom nom de l'attribut
	 * @param constante determine si l'attribut est constante
	 * @param type type de l'attribut
	 * @param visibilite Visibilite de l'attribut
	 * @param estStatic determine si attribut est static
	 */
	public Attribut(int num, String nom, boolean constante, String type, String visibilite, boolean estStatic)
	{
		this.num        = num;
		this.nom        = nom;
		this.constante  = constante;
		this.type       = type;
		this.visibilite = visibilite;
		this.estStatic   = estStatic;
	}

	/*---------------------------------------*/
	/*              Modificateurs            */
	/*---------------------------------------*/

	public void setNom       (String nom       ) { this.nom = nom             ;}
	public void setConstante (boolean constante) {this.constante  = constante ;}
	
	/*---------------------------------------*/
	/*              Accesseurs               */
	/*---------------------------------------*/

	public int     getNum       () {return this.num       ;}
	public String  getNom       () {return this.nom       ;}
	public boolean isConstante  () {return this.constante ;}
	public String  getType      () {return this.type      ;}
	public String  getVisibilite() {return this.visibilite;}
	public boolean estStatic     () {return this.estStatic  ;}

	/*---------------------------------------*/
	/*         Methode instance              */
	/*---------------------------------------*/

	@Override
	public String toString()
	{
		String sRet ="";

		sRet += "attribut : " + this.num + "	nom: "  ;
		sRet += String.format("%-18s", this.nom ) +  "  type : ";
		sRet += String.format("%-20s", this.type) + "  visibilite : ";
		sRet += String.format("%-10s", this.visibilite) + "  portee : ";

		sRet += String.format("%-9s", this.estStatic ? "classe" : "instance");

		sRet += this.constante ? "  constante" : "";

		return sRet;
	}
}


