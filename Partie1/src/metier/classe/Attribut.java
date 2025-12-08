package metier.classe;

public record Attribut(int num, String nom, String type, String visibilite, char porte) 
{


	@Override
	public String toString()
	{
		String sRet ="";

		sRet += "attribut : " + this.num + "  nom: "  ;
		sRet += String.format("%10s", this.nom ) +  "  type : ";
		sRet += String.format("%20s", this.type) + "  visibilité : ";
		sRet += String.format("%10s", this.visibilite) + "  portée : ";

		if (porte == 'I') 
			sRet += String.format("%9s", "instance");
		else
			sRet += String.format("%7s", "classe");

		return sRet;

	}
}


