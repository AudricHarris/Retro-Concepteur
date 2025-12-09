package metier.classe;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/*
 * Record methode stock le nom, visibilite, type, lstParam d'une classe
 * Elle aura aussi un toString custom
 */
public class Methode
{
	private String               visibilite;
	private String               nom;
	private String               type;
	private ArrayList<Parametre> lstParam;

	public Methode(String visibilite,String nom, String type, ArrayList<Parametre> lstParam)
	{
		this.visibilite = visibilite;
		this.nom        = nom;
		this.type       = type;
		this.lstParam   = lstParam;
	}

	@Override
	public String toString()
	{

		String sRet ="\n";
		if (this.nom == this.type)
		{
			sRet += "methode : " + String.format("%-12s","Constructeur") + "	visibilité: " ;
			sRet += String.format("%-10s", this.visibilite ) + "\nparamètres : \n";
		}
		else
		{
			sRet += "methode : " + String.format("%-12s",this.nom) + "	visibilité: " ;
			sRet += String.format("%-10s", this.visibilite ) + " type de retour: ";
			sRet += String.format("%-10s", this.type) + "\nparamètres : \n";
		}	
		

		int cpt=0;
		if ( this.lstParam.isEmpty() )
			sRet += "aucun\n\n";
		else
			for ( Parametre param : this.lstParam )
				sRet += "	p" + (++cpt) + ": " + String.format("%-10s", param.getNom()) + " type:" + param.getType() + "\n\n";
	
		return sRet;
	}
}
