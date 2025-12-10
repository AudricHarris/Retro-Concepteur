package metier.classe;
import controller.Controller;
import metier.AnalyseFichier;

import java.util.ArrayList;
import java.util.List;

/**
 * Determine les Liaison potentiel entre les classe et leur multiplicité.
 */
public class Liaison 
{
	public static final String[] LST_COLLECTIONS = {"List<","ArrayList<", "LinkedList<" ,"Set<" ,"HashSet<" ,"Collection" ,"Iterable<"};

    private String fromClass;
    private String toClass;
    private Multiplicite toMultiplicity;
	private String nomVar;


	private Controller ctrl;
	private AnalyseFichier analyseFichier;

	/**
	 * Fabrique de la classe Liaison
	 * @param classe1 Une première classe
	 * @param classe2 une deuxième classe
	 */
	public static List<Liaison> creerLiaison(Classe classe1 , Classe classe2)
	{
		List<Liaison> lstLiaisons = new ArrayList<Liaison>();
		Liaison liaison = null;
		for (Attribut attribut1 : classe1.getLstAttribut())
		{
			if (attribut1.getType().startsWith(classe2.getNom()) ||
				getCollectionType(attribut1.getType(), classe2.getNom())) 
			{
				liaison = new Liaison( classe1, classe2, attribut1);
				lstLiaisons.add(liaison);
			}
		}
		
		return lstLiaisons;
	}
	
	/**
	 * Constructeur de la classe Liaison
	 * @param classe1 une premièr classe
	 * @param classe2 une deuxième classe
	 * @param attribut attribut qui lie les deux classe
	 * */
	private Liaison(Classe classe1 , Classe classe2, Attribut attribut)
	{
		this.toMultiplicity = new Multiplicite("temp","temp");

		this.fromClass =  classe1.getNom();
		this.toClass = classe2.getNom();
		this.nomVar = attribut1.getNom();
		
		Methode constructeur =  classe1.getLstMethode().get(0);
		List<Parametre> params = constructeur.getLstParam();
		for (Parametre parametre : params) 
		{
			if (parametre.getType().contains(classe2.getNom()))
			{
				this.toMultiplicity = new Multiplicite("1","temp");
				break;
			}
			else this.toMultiplicity = new Multiplicite("0","temp");
			
		}

		if (Liaison.estCollection(attribut.getType())) this.toMultiplicity.setBorneSup("*");
		else this.toMultiplicity.setBorneSup("1");
	}
	

		
	public String toString()
	{
		return this.fromClass + " ----> " + this.toMultiplicity.toString() + " " + this.toClass + " " + this.nomVar;
	}
	

	//---------------------------------------//
	//              Methode static           //
	//---------------------------------------//

	/**
	 *	Parcours la liste LST_COLLECTIONS et renvoie si type peut être former avec nom
	 *	@param type type entier
	 *	@param nom nom de la classe
	 *	@return boolean si oui ou non le type est une collections de la classe
	 * */
	public static boolean getCollectionType(String type, String nom)
	{
		for (String pattern : Liaison.LST_COLLECTIONS)
			if (type.matches(pattern + nom+ ">")) return true;

		return false;
	}

	/**
	 *	Parcours la liste LST_COLLECTIONS et determine si le type est une collection
	 *	@param type type entier
	 *	@return boolean si oui ou non le type est une collection
	 * */
	private static boolean estCollection(String type) 
	{
		if (type == null) return false;
		
		String t = type.trim();

		if (t.contains("[]")) return true;

		for (String col : Liaison.LST_COLLECTIONS)
			if (type.startsWith(col))
				return true;

		return false;
	}
}
