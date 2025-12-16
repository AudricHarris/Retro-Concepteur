package RetroConcepteur.metier.classe;

/**
 * Class Attribut est une instance qui permet de stocker les attribut d'un classe
 * Elle contient plus paramètres comme type, visibilite, isStatic, son nom et un num
 */
public class Attribut 
{
	private int     num;
	private String  nom;
	private boolean constante;
	private String  type;
	private String  visibilite;
	private boolean isStatic;

	/**
	 * Constructeur d'Attribut
	 * @param num numero de l'attribut
	 * @param nom nom de l'attribut
	 * @param constante determine si l'attribut est constante
	 * @param type type de l'attribut
	 * @param visibilite Visibilité de l'attribut
	 * @param isStatic determine si attribut est static
	 */
	public Attribut(int num, String nom, boolean constante, String type, String visibilite, boolean isStatic)
	{
		this.num        = num;
		this.nom        = nom;
		this.constante  = constante;
		this.type       = type;
		this.visibilite = visibilite;
		this.isStatic   = isStatic;
	}
	
	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public int     getNum       () {return this.num       ;}
	public String  getNom       () {return this.nom       ;}
	public boolean isConstante  () {return this.constante ;}
	public String  getType      () {return this.type      ;}
	public String  getVisibilite() {return this.visibilite;}
	public boolean isStatic     () {return this.isStatic  ;}

	//---------------------------------------//
	//         Methode instance              //
	//---------------------------------------//

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


