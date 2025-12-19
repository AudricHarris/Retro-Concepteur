package retroconcepteur.metier.classe;

import java.util.ArrayList;

/**
 * Class methode stock le nom, visibilite, type, lstParam et estStatic d'une classe
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
public class Methode
{
	private String               visibilite;
	private String               nom;
	private String               type;
	private ArrayList<Parametre> lstParam;
	private boolean              estStatic;

	/**
	 *	Creer une instance de methode
	 *	@param visibilite visibilite de la methode
	 *	@param nom nom de la methode
	 *	@param type type de retour de la methode
	 *	@param lstParam param de la methode stocker
	 *	@param estStatic si la methode estStatic
	 */
	public Methode(String visibilite,String nom, String type, ArrayList<Parametre> lstParam, boolean estStatic)
	{
		this.visibilite = visibilite;
		this.nom        = nom;
		this.type       = type;
		this.lstParam   = lstParam;
		this.estStatic   = estStatic;
	}

	/*---------------------------------------*/
	/*              Getters                  */
	/*---------------------------------------*/

	public String               getVisibilite() {return this.visibilite;}
	public String               getNom       () {return this.nom       ;}
	public String               getType      () {return this.type      ;}
	public ArrayList<Parametre> getLstParam  () {return this.lstParam  ;}
	public boolean              estStatic     () {return this.estStatic  ;}

	/*---------------------------------------*/
	/*          Methode instance             */
	/*---------------------------------------*/

	@Override
	public String toString()
	{

		String sRet ="\n";
		if (this.nom == this.type)
		{
			sRet += "methode : " + String.format("%-20s","Constructor") + "	visibilite: " ;
			sRet += String.format("%-10s", this.visibilite ) + (this.estStatic ? " Statique" : "");			
			sRet += "\nparametres : \n";

		}
		else
		{
			sRet += "methode : " + String.format("%-20s",this.nom) + "	visibilite: " ;
			sRet += String.format("%-10s", this.visibilite ) + " type de retour: ";
			sRet += String.format("%-10s", this.type) + "\nparametres : \n";
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
