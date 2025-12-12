package RetroConcepteur.metier.classe;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Class methode stock le nom, visibilite, type, lstParam et isStatic d'une classe
 */
public class Methode
{
	private String visibilite;
	private String nom;
	private String type;
	private ArrayList<Parametre> lstParam;
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

	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public String               getVisibilite() {return this.visibilite;}
	public String               getNom       () {return this.nom       ;}
	public String               getType      () {return this.type      ;}
	public ArrayList<Parametre> getLstParam  () {return this.lstParam  ;}
	public boolean              isStatic     () {return this.isStatic  ;}

	//---------------------------------------//
	//          methode instance             //
	//---------------------------------------//

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
