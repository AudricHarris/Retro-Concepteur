package RetroConcepteur.metier.classe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class classe est une instance qui permet de stocker les differents class de chaque fichier
 * Elle contient un nom, une list d'attribut et liste de methode pour les variables
 * */
public class Classe
{
	private String              nom;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode>  lstMethode;
	
	private String       nomHeritageClasse;
	private List<String> lstInterfaces;

	private boolean isAbstract;
	private boolean isInterface;
	private boolean cachable;

	private boolean estClique;
	
	/**
	 * Constructeur de la classe
	 * @param nom nom de la classe
	 */

	public Classe(String nom)
	{
		this.nom = nom;
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		this.isAbstract = false;
		this.isInterface = false;
		this.estClique = false;
		this.cachable  = false;
	}
	
	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public String              getNom              (){ return this.nom;              }
	public boolean             isAbstract          (){ return this.isAbstract;       }
	public boolean             isInterface         (){ return this.isInterface;      }
	public String              getNomHeritageClasse(){ return this.nomHeritageClasse;}
	public boolean             estClique           (){ return this.estClique;        }
	public boolean             getCachable         (){return this.cachable ;          } 
	
	public ArrayList<Attribut> getLstAttribut()
	{ 
		return new ArrayList<Attribut>(this.lstAttribut);
	}
	
	public ArrayList<Methode>  getLstMethode()
	{
		return new ArrayList<Methode> (this.lstMethode);
	}

	public ArrayList<String>   getLstInterfaces ()
	{
		if (this.lstInterfaces != null)
			return new ArrayList<String>(this.lstInterfaces);
		
		return new ArrayList<String>();
	}

	/**
	 * Retourne la taille du plus grand Attribut
	 * @return int taille du plus frand Attribut
	 */
	public int getPlusGrandAttribut()
	{
		int grand = 0;
		
		for (Attribut att : this.lstAttribut) 
			if(att.getNom().length() > grand)
				grand = att.getNom().length();
		
		return grand;
	}

	/**
	 * Retourne la taille du plus grand Methode
	 * @return int taille du plus frand Methode
	 */
	public int getPlusGrandeMethode()
	{
		int grand = 0;
		
		for (Methode meth : this.lstMethode) 
		{
			String nomMeth = meth.getNom();
			if (nomMeth.equals("main")) continue;

			int tailleActuelle = nomMeth.length() + 2;

			if (!meth.getLstParam().isEmpty())
			{
				for (Parametre p : meth.getLstParam())
					tailleActuelle += p.getNom().length() + 1 + p.getType().length() + 1;
				
				tailleActuelle--;
			}
			grand = Math.max(grand, tailleActuelle);
		}
		return grand;
	}
	/**
	 * Retourne le nombre de constante de la classe
	 * @return int le nombre de constante
	 */
	public int getNbConstante()
	{
		int cpt=0;
		for ( Attribut att : this.lstAttribut ) if ( att.isConstante() ) cpt++;

		return cpt;
	}
	/**
	 * Retourne le nombre de méthode de la classe
	 * @return int le nombre de méthode
	 */
	public int getNbMethode() { return this.lstMethode.size(); }

	/**
	 * Retourne le nombre d'attribut de la classe
	 * @return int le nombre d'attribut
	 */
	public int getNbAttribut() { return this.lstAttribut.size(); }
	
	/**
	* Retourne une liste ordonnée des attributs :
	* 1. Statiques constants (static final)
	* 2. Finaux non-statiques (final)
	* 3. Instance (non-static, non-final)
	* Dans chaque groupe : tri par visibilité (public > protected > private)
	* @return List<Attribut> liste ordonnée
	*/
	public List<Attribut> getListOrdonneeAttribut()
	{
		List<Attribut> lstOrdonnee = new ArrayList<>();
		
		// Groupes
		List<Attribut> staticFinal = new ArrayList<>();
		List<Attribut> finalInstance = new ArrayList<>();
		List<Attribut> instanceVars = new ArrayList<>();
		
		for (Attribut att : this.lstAttribut)
		{
			if (att.isStatic() && att.isConstante())
			{
				staticFinal.add(att);
			}
			else 
				if (!att.isStatic() && att.isConstante()) finalInstance.add(att);
				else instanceVars.add(att);
		}
		
		List<String> visibilities = Arrays.asList("public", "protected", "private", "default");
		

		for (List<Attribut> group : Arrays.asList(staticFinal, finalInstance, instanceVars))
			for (String vis : visibilities)
				lstOrdonnee.addAll(getAttributsParVisibilite(group, vis));
		
		return lstOrdonnee;
	}
	
	
	/**
	* Retourne une liste ordonnée des méthodes :
	* 1. Méthodes statiques
	* 2. Méthodes d'instance (incluant constructeurs)
	* Dans chaque groupe : tri par visibilité (public > protected > private)
	* @return List<Methode> liste ordonnée
	*/
	public List<Methode> getListOrdonneeMethode()
	{
		List<Methode> lstOrdonnee = new ArrayList<>();
		
		List<Methode> staticMethods = new ArrayList<>();
		List<Methode> instanceMethods = new ArrayList<>();
		
		for (Methode meth : this.lstMethode)
			if (meth.isStatic()) staticMethods.add(meth);
			else instanceMethods.add(meth);
		
		List<String> visibilities = Arrays.asList("public", "protected", "private", "default");
		
		for (List<Methode> group : Arrays.asList(instanceMethods, staticMethods))
			for (String vis : visibilities)
				lstOrdonnee.addAll(getMethodesParVisibilite(group, vis));
		
		return lstOrdonnee;
	}
	
	private List<Attribut> getAttributsParVisibilite(List<Attribut> liste, String visibiliteCible)
	{
		List<Attribut> result = new ArrayList<>();
		
		for (Attribut att : liste)
		{
			String visibilite = att.getVisibilite();
			if (visibilite.equals(visibiliteCible) || 
				(visibiliteCible.equals("default")     && 
				!visibilite.matches("public|protected|private")))
			{
				result.add(att);
			}
		}
		return result;
	}
	private List<Methode> getMethodesParVisibilite(List<Methode> liste, String visibiliteCible)
	{
		List<Methode> result = new ArrayList<>();
		
		for (Methode meth : liste)
		{
			String visiblite = meth.getVisibilite();
			if (visiblite.equals(visibiliteCible)    || 
				(visibiliteCible.equals("default") && 
				!visiblite.matches("public|protected|private")))
			{
				result.add(meth);
			}
		}
		return result;
	}
	//---------------------------------------//
	//          Modificateur                 //
	//---------------------------------------//

	public void setIsAbstract       (boolean isAbstract ) { this.isAbstract        = isAbstract;  }
	public void setIsInterface      (boolean isInterface) { this.isInterface       = isInterface; }
	public void setNomHeritageClasse(String  nom        ) { this.nomHeritageClasse = nom;         }
	public void setEstClique        (boolean bClique    ) { this.estClique         =bClique;      }
	public void setCachable         (boolean bCache     ) { this.cachable          = bCache;      }
	
	/**
	 * Ajoute l'interface passer en param
	 * @param nomInterface nom de l'interface ajouter
	 */
	public void ajouterInterface(String nomInterface)
	{
		if (this.lstInterfaces == null) this.lstInterfaces = new ArrayList<String>();

		this.lstInterfaces.add(nomInterface);
	}

	/**
	 * Creer et ajoute un attribut par rapport au param passer
	 * @param nomAtt     nom de l'attribut
	 * @param constante  si l'attribut est constante
	 * @param type       le type de l'attribut
	 * @param visibilite Visibilite de l'attribut
	 * @param isStatic   si l'attribut est static
	 * @param isAddOnly  si l'attribut est addOnly
	 */
	public void ajouterAttribut(String nomAtt    , boolean constante, String type,
		                        String visibilite, boolean isStatic , boolean isAddOnly)
	{
		int num = this.lstAttribut.size() + 1;
		Attribut attribut = new Attribut(num, nomAtt, constante, type, visibilite, isStatic, isAddOnly);
		
		if (attribut != null) this.lstAttribut.add(attribut);
	}

	/**
	 * Creer et ajoute une methode par rapport au param passer
	 * @param visibilite Visibilite de la methode
	 * @param nomMeth    nom de la methode
	 * @param type       le type de la methode
	 * @param lstParam   la liste des param de la méthode
	 * @param isStatic   si l'attribut est static
	 */
	public void ajouterMethode(String visibilite, String nomMeth, String type, 
		                       ArrayList<Parametre> lstParam, boolean isStatic)
	{
		Methode meth = new Methode(visibilite, nomMeth, type, lstParam, isStatic);
		
		if ( meth != null) this.lstMethode.add(meth);
	}

	//---------------------------------------//
	//         Methode instance              //
	//---------------------------------------//

	public String toString()
	{
		String sRet = this.nom + (this.isAbstract ? " { abstract }" + "\n" : "\n");
		for (Attribut attribut : this.lstAttribut)
			sRet += attribut.toString() + "\n";

		for (Methode methode : this.lstMethode)
			sRet+= methode.toString();

		if (this.nomHeritageClasse != null) 
			sRet += " extends " + this.nomHeritageClasse + "\n";
		if (this.lstInterfaces != null && !this.lstInterfaces.isEmpty()) 
		{
			sRet += "\nimplements :";
			for (String inter : this.lstInterfaces) 
				sRet += "\n\t" + inter + "\n";
		}

		return sRet;
	}
}
