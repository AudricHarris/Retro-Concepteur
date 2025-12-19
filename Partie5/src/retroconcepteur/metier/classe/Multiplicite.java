package retroconcepteur.metier.classe;

/**
 * Instance qui stock la Multiplicite d'une Liaison
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
public class Multiplicite
{
	private String borneInf;
	private String borneSup;

	/**
	 * Constructeur de Multiplicite
	 * @param borneInf Multiplicite de la borne inferieur
	 * @param borneSup Multiplicite de la borne superieur
	 * */
	public Multiplicite(String borneInf, String borneSup)
	{
		this.borneInf = borneInf;
		this.borneSup = borneSup;
	}

	/*---------------------------------------*/
	/*            Modificateur               */
	/*---------------------------------------*/
	
	public void setBorneInf(String borneInf) 
	{ 
		this.borneInf = borneInf;
	}

	public void setBorneSup(String borneSup) 
	{ 
		this.borneSup = borneSup;
	}

	/*---------------------------------------*/
	/*             Accesseurs                */
	/*---------------------------------------*/
	
	public String getBorneInf() { return this.borneInf; }
	public String getBorneSup() { return this.borneSup; }

	/*---------------------------------------*/
	/*            Methode instance           */
	/*---------------------------------------*/
	
	public String toString()
	{
		return "(" + this.borneInf + ".." + this.borneSup + ")";
	}
}
