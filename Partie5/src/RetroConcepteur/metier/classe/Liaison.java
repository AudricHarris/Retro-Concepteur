package RetroConcepteur.metier.classe;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.Serializable;

import RetroConcepteur.metier.AnalyseFichier;

/**
 * Determine les Liaison potentiel entre les classe et leur multiplicité.
 */
public class Liaison implements Serializable
{
	public static final String[] LST_COLLECTIONS = {"List<","ArrayList<", "LinkedList<" ,
	                                                "Set<" ,"HashSet<"  ,"Collection"   ,
	                                                "Iterable<", "Iterator<"};

	private Classe fromClass;
	private Classe toClass;

	private Multiplicite fromMultiplicity;
	private Multiplicite toMultiplicity;
	
	private AnalyseFichier analyseFichier;
	
	private String nomVar;


	/**
	 * Fabrique de la classe Liaison
	 * @param classe1 Une première classe
	 * @param classe2 une deuxième classe
	 * @param analyseFichier classe AnalyseFichier
	 * @return lstLiaison list des liaison entre les classe
	 */
	public static List<Liaison> creerLiaison(Classe classe1 , Classe classe2,
		                                     AnalyseFichier analyseFichier   )
	{
		List<Liaison> lstLiaisons = new ArrayList<Liaison>();
		Liaison       liaison     = null;

		for (Attribut attribut1 : classe1.getLstAttribut())
		{
			if (attribut1.getType().startsWith(classe2.getNom()) ||
				getCollectionType(attribut1.getType(), classe2.getNom())) 
			{
				liaison = new Liaison( classe1, classe2, attribut1, analyseFichier);
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
	 * @param analyseFichier classe analyse 
	 * */
	private Liaison(Classe classe1 , Classe classe2, Attribut attribut,
		            AnalyseFichier analyseFichier                      )
	{

		this.toMultiplicity   = new Multiplicite("1","temp");
		this.fromMultiplicity = new Multiplicite("0", "*");
		this.fromClass        = classe1;
		this.toClass          = classe2;
		this.nomVar           = attribut.getNom();
		this.analyseFichier   = analyseFichier;

		Methode constructeur =  classe1.getLstMethode().size() >= 1  ? 
		                        classe1.getLstMethode().get(0) : 
		                        null;

		List<Parametre> params = constructeur != null       ?
		                         constructeur.getLstParam() : 
		                         new ArrayList<Parametre>();

		for (Parametre parametre : params) 
		{
			if (parametre.getType().contains(classe2.getNom()))
			{
				this.toMultiplicity = new Multiplicite("1","temp");
				break;
			}
			
		}

		if (Liaison.estCollection(attribut.getType())) this.toMultiplicity.setBorneSup("*");
		else this.toMultiplicity.setBorneSup("1");
	}

	

	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public Classe getFromClass() { return this.fromClass; }
	public Classe getToClass  () { return this.toClass;   }

	public Multiplicite getToMultiplicity  () { return this.toMultiplicity;   }
	public Multiplicite getFromMultiplicity() { return this.fromMultiplicity; }

	public String getType()
	{
		String  sType = "Association";
		
		// Vérifie si c'est une implémentation d'interface
		if (this.fromClass.getLstInterfaces().contains(this.toClass.getNom()) &&
			this.toClass.isInterface()) 
		{
			sType = "Implementation";
		}

		// Vérifier si c'est un héritage
		if (this.fromClass.getHeritageClasse() != null &&
		    this.fromClass.getHeritageClasse().getNom().equals(this.toClass.getNom()))
		{
			sType = "Generalisation";
		}

		// Si c'est une association, déterminer si elle est bidirectionnelle
		if (sType.equals("Association")) 
		{
			if (this.estBidirectionel())
			{
				sType = "BIDIRECTIONNELLE";
			}
			else
			{
				sType = "UNI";
			}
		}
		
		return sType;
	}

	public boolean estBidirectionel()
	{
		for (Liaison l : this.analyseFichier.getListLiaison())
			if (l.getToClass() == this.fromClass && l.getFromClass() == this.toClass)
				return true;
		return false;
	}
	
	//---------------------------------------//
	//            Modificateur               //
	//---------------------------------------//

	
	public void setFromMultiplicte(Multiplicite m) { this.fromMultiplicity = m;}

	//---------------------------------------//
	//            Methode instance           //
	//---------------------------------------//

	public String toString()
	{
		return this.fromClass.getNom() + " ----> " + this.toMultiplicity.toString() + " " +
		       this.toClass.getNom  () + " "       + this.nomVar;
	}
	

	//---------------------------------------//
	//			  Methode static		   //
	//---------------------------------------//

	/**
	 *	Parcours la liste LST_COLLECTIONS et renvoie si type peut être former avec nom
	 *	@param type type entier
	 *	@param nom nom de la classe
	 *	@return boolean si oui ou non le type est une collections de la classe
	 * */
	public static boolean getCollectionType(String type, String nom)
	{
		type = type.trim();
		
		if (type == null || type.trim().isEmpty()) return false;

		while (type.endsWith("[]")) 
			type = type.substring(0, type.length() - 2).trim();

		if (!type.contains("<") && type.equals(nom)) return true;

		int start = type.indexOf('<');
		int end = type.lastIndexOf('>');

		if (start != -1 && end > start) 
		{
			String base = type.substring(0, start).trim();
			if (base.equals(nom)) return true;

			String args = type.substring(start + 1, end).trim();
			if (!args.isEmpty()) 
			{
				Scanner scPartit = new Scanner(args);
				scPartit.useDelimiter(",");

				while (scPartit.hasNext())
					if (getCollectionType(scPartit.next().trim(), nom)) 
						return true;
			}
		}

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
