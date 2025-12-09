package metier.classe;


/*
 * Record Attribut est une instance qui permet de stocker les attribut d'un classe
 * Elle contient plus paramètres comme type, visibilite, isStatic, son nom et un num
 */
public record Attribut(int num, String nom, boolean constante, String type, String visibilite, boolean isStatic) 
{


	@Override
	public String toString()
	{
		String sRet ="";

		sRet += "attribut : " + this.num + "	nom: "  ;
		sRet += String.format("%-18s", this.nom ) +  "  type : ";
		sRet += String.format("%-20s", this.type) + "  visibilité : ";
		sRet += String.format("%-10s", this.visibilite) + "  portée : ";

		if (!this.isStatic ) 
			sRet += String.format("%9s", "instance");
		else
			sRet += String.format("%7s", "classe");

		sRet += this.constante ? "  constante" : "";

		return sRet;
	}
}


