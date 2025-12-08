package metier.classe;

import java.util.ArrayList;

public record Methode(String visibilite,String methode, String type, ArrayList<Parametre> lstParam) 
{

	@Override
	public String toString()
	{
		String sRet ="";
		String nomMeth;


		sRet += "methode : " + String.format("%20s",this.methode) + "  visibilité: " ;
		sRet += String.format("%10s", this.visibilite ) +  "    type de retour: ";

		if (this.type.isEmpty()) 
			sRet += String.format("%10s", this.type) + "\nparamètres : ";

		int cpt=0;
		if ( this.lstParam.isEmpty() )
			sRet += "aucun";
		else
			for ( Parametre param : this.lstParam )
				sRet += "p" + (++cpt) + ":" + String.format("%10s", param.nom()) + " type:" + param.type() + "\n             ";

		return sRet;

	}
}
