package retroconcepteur.metier.classe;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import retroconcepteur.metier.AnalyseFichier;

/**
 * Determine les Liaison potentiel entre les classe et leur multiplicite.
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
public class Liaison
{
	public static final String[] LST_COLLECTIONS = {"List<","ArrayList<", "LinkedList<" ,
	                                                "Set<" ,"HashSet<"  ,"Collection"   ,
	                                                "Iterable<", "Iterator<",
												     "Map<", "HashMap<", "TreeMap<"};

	private Classe classeDep;
	private Classe classeArr;

	private Multiplicite multDep;
	private Multiplicite multArr;
	
	private AnalyseFichier analyseFichier;
	
	private String nomVar;


	/**
	 * Fabrique de la classe Liaison
	 * @param classe1 Une premiere classe
	 * @param classe2 une deuxieme classe
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

		if(classe1.getNomHeritageClasse() != null && 
			classe1.getNomHeritageClasse().equals(classe2.getNom()))
		{
			lstLiaisons.add(new Liaison(classe1, classe2, null, analyseFichier));
		}

		if (classe1.getLstInterfaces().contains(classe2.getNom()))
			lstLiaisons.add(new Liaison(classe1, classe2, null, analyseFichier));
		

		return lstLiaisons;
	}
	
	/**
	 * Constructeur de la classe Liaison
	 * @param classe1 une premier classe
	 * @param classe2 une deuxieme classe
	 * @param attribut attribut qui lie les deux classe
	 * @param analyseFichier classe analyse 
	 * */
	private Liaison(Classe classe1 , Classe classe2, Attribut attribut,
		            AnalyseFichier analyseFichier                      )
	{
		this.classeDep        = classe1;
		this.classeArr          = classe2;
		this.analyseFichier   = analyseFichier;
		
		this.multArr   = new Multiplicite("", "");
		this.multDep = new Multiplicite("", ""); 
		this.nomVar           = "";

		if (attribut != null)
		{
			this.nomVar = attribut.getNom();
			this.multArr = new Multiplicite("1", "1");
			this.multDep = new Multiplicite("0", "*");

			Methode constructeur = classe1.getLstMethode().size() >= 1 ? classe1.getLstMethode().get(0) : null;
			List<Parametre> params = constructeur != null ? constructeur.getLstParam() : new ArrayList<Parametre>();

			for (Parametre parametre : params) 
			{
				if (parametre.getType().contains(classe2.getNom()))
				{
					this.multArr = new Multiplicite("1", "temp");
					break;
				}
			}

			if (Liaison.estCollection(attribut.getType())) 
				this.multArr.setBorneSup("*");
			else 
				this.multArr.setBorneSup("1");
		}
	}

	/*
	 * Deuxieme constructeur public pour la sauvegarde et dans analyse fichier
	 */
	public Liaison(Classe classeDep, Classe classeArr, Multiplicite multDep,
	               Multiplicite multArr, String nomVar, AnalyseFichier analyseFichier)
	{
		this.classeDep = classeDep;
		this.classeArr = classeArr;
		this.multDep = multDep == null ? new Multiplicite("","") : multDep;
		this.multArr   = multArr == null   ? new Multiplicite("","") : multArr;
		this.nomVar = nomVar == null ? "" : nomVar;
		this.analyseFichier = analyseFichier;
	}

	/*---------------------------------------*/
	/*              Getters                  */
	/*---------------------------------------*/

	public Classe getClasseDep() { return this.classeDep;   }
	public Classe getClasseArr  () { return this.classeArr;   }

	public Multiplicite getMultArr  () { return this.multArr;   }
	public Multiplicite getMultADep() { return this.multDep;   }

	public String getNomVar() { return this.nomVar; }

	public String getType()
	{
		String  sType = "Association";
		
		// Verifie si c'est une implementation d'interface
		if (this.classeDep.getLstInterfaces().contains(this.classeArr.getNom()) &&
			this.classeArr.estInterface()) 
		{
			sType = "Implementation";
		}

		// Verifier si c'est un heritage
		if (this.classeDep.getNomHeritageClasse() != null &&
		    this.classeDep.getNomHeritageClasse().equals(this.classeArr.getNom()))
		{
			sType = "Generalisation";
		}

		// Si c'est une association, determiner si elle est bidirectionnelle
		if (sType.equals("Association")) 
		{
			if (this.estBidirectionel())
				sType = "BIDIRECTIONNELLE";
			else
				sType = "UNI";
		}
		
		return sType;
	}

	public boolean estBidirectionel()
	{
		for (Liaison l : this.analyseFichier.getListLiaison())
			if (l.getClasseArr() == this.classeDep && l.getClasseDep() == this.classeArr)
				return true;
		return false;
	}
	
	/*---------------------------------------*/
	/*            Modificateur               */
	/*---------------------------------------*/

	
	public void setFromMultiplicte(Multiplicite m) { this.multDep = m;}

	/*---------------------------------------*/
	/*            Methode instance           */
	/*---------------------------------------*/

	public String toString()
	{
		return this.classeDep.getNom() + " ----> " + this.multArr.toString() + " " +
		       this.classeArr.getNom  () + " "       + this.nomVar;
	}
	

	/*---------------------------------------*/
	/*			  Methode static		     */
	/*---------------------------------------*/

	/**
	 *	Parcours la liste LST_COLLECTIONS et renvoie si type peut etre former avec nom
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
					{
						scPartit.close();
						return true;
					}
					
				scPartit.close();
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
