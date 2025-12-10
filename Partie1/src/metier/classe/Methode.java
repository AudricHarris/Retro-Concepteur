package metier.classe;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Class methode stock le nom, visibilite, type, lstParam et isStatic d'une classe
 */
public class Methode
{
	/**Stock la visibilité d'une méthode*/
	private String visibilite;

	/**Stock le nom de la methode*/
	private String nom;

	/**Stock le type de retour de la méthode*/
	private String type;

	/**Stock la liste des Parametre*/
	private ArrayList<Parametre> lstParam;

	/**Stock si la methode est static ou non */
	private boolean isStatic;

	/**
	 *	Creer une instance de methode
	 *	@param visibilite visibilité de la methode
	 *	@param nom nom de la methode
	 *	@param type type de retour de la methode
	 *	@param lstParam param de la methode stocker
	 *	@param isStatic si la methode isStatic
	 */
	public Methode(String visibilite,String nom, String type, ArrayList<Parametre> lstParam, boolean isStatic)
	{
		this.visibilite = visibilite;
		this.nom        = nom;
		this.type       = type;
		this.lstParam   = lstParam;
		this.isStatic   = isStatic;
	}

	@Override
	public String toString()
	{

		String sRet ="\n";
		if (this.nom == this.type)
		{
			sRet += "methode : " + String.format("%-20s","Constructor") + "	visibilité: " ;
			sRet += String.format("%-10s", this.visibilite ) + (this.isStatic ? " Statique" : "");			
			sRet += "\nparamètres : \n";

		}
		else
		{
			sRet += "methode : " + String.format("%-20s",this.nom) + "	visibilité: " ;
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
