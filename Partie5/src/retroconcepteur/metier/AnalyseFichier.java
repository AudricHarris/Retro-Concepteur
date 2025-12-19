package retroconcepteur.metier;

// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import retroconcepteur.metier.classe.*;

/**
 * Permet la lecture d'un dossier et la creation de classe.
 * Une fois cree nous ajoutons les methode et attribut a cette classe
 */
public class AnalyseFichier
{
	private ArrayList<Classe> lstClass;
	private List<Liaison>     lstLiaisons;

	private HashSet<String>   lstExtends;
	private HashSet<String>   lstImplement;


	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
	public AnalyseFichier( String repo)
	{
		this.lstClass    = new ArrayList<Classe> ();
		this.lstLiaisons = new ArrayList<Liaison>();
		this.lstExtends  = new HashSet<String>();
		this.lstImplement = new HashSet<String>();

		this.ouvrirDossier(repo);
	}


	/*---------------------------------------*/
	/*             Accesseur                 */
	/*---------------------------------------*/
	
	public ArrayList<Classe> getLstClasses () { return new ArrayList<Classe> (this.lstClass);   }
	public List<Liaison>     getListLiaison() { return new ArrayList<Liaison>(this.lstLiaisons);} 

	/**
	 * Retourne la liste de toutes les liaisons unidirectionnelles.
	 * Les liaisons bidirectionnelles sont ignorees dans cette liste.
	 * @return une List de `Liaison` ne contenant que les liaisons non bidirectionnelles
	 */
	public List<Liaison> getListLiaisonUnique()
	{
		List<Liaison> lstUnique = new ArrayList<Liaison>();
		for (Liaison l : this.lstLiaisons)
			if (!l.estBidirectionel())
				lstUnique.add(l);
		return lstUnique;
	}

	/**
	 * Retourne la liste des liaisons optimisee pour l'affichage.
	 * Les paires de liaisons A->B et B->A sont fusionnees en une liaison binaire.
	 * @return une List de `Liaison` optimisee pour l'affichage (liaisons binaires et simples)
	 */
	public List<Liaison> getListLiaisonBinaire()
	{
		List<Liaison> lstFinale = new ArrayList<>();
		List<Liaison> aIgnorer = new ArrayList<>(); // Pour stocker les doublons inverses a ne pas ajouter

		for (Liaison l : this.lstLiaisons)
		{
			if (aIgnorer.contains(l)) continue;

			if (l.estBidirectionel())
			{
				Liaison inverse = null;
				for (Liaison candidate : this.lstLiaisons) 
				{
					if (candidate != l && 
						candidate.getClasseDep() == l.getClasseArr() && 
						candidate.getClasseArr() == l.getClasseDep()) 
					{
						inverse = candidate;
						break;
					}
				}

				if (inverse != null) 
				{
					l.setFromMultiplicte(inverse.getMultArr());
					
					lstFinale.add(l);
					aIgnorer.add(inverse); 
				}
				else
					lstFinale.add(l);
				
			}
			else
				lstFinale.add(l);
			
		}
		return lstFinale;
	}

	/**
	 * Retourne si le nom existe dans liste classe
	 * @param nom nom du potentiel classe
	 * @return boolean si oui ou nom la classe nom existe
	 */
	public boolean estClasse(String nom)
	{
		for ( Classe c : this.lstClass) 
			if (c.getNom().equals(nom)) 
				return true;
		
		return false;
	}

	/**
	 * Retourne si oui ou non la classe existe meme insere dans une collections
	 * @param type la chaine de caractere du type
	 * @return Boolean si la classe du type existe
	 */
	public boolean estClasseProjet(String type)    
	{
		
		for (Classe c : this.lstClass)
			if (Liaison.getCollectionType(type, c.getNom()))
				return true;

		return false;
	}

	//---------------------------------------//
	//         methode instance              //
	//---------------------------------------//
	/**
	 * Parcourt le repertoire fourni, cree les instances de `Classe` pour
	 * chaque fichier Java trouve et lance l'analyse des fichiers.
	 * @param repo chemin du repertoire racine contenant le code source Java
	 */
	public void ouvrirDossier(String repo)
	{
		this.lstClass.clear();
		this.lstLiaisons.clear();
		this.lstExtends.clear();
		this.lstImplement.clear();

		ArrayList<String> allFiles = new ArrayList<String>();
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);

			for (String file : allFiles)
			{
				String nomClasse = file.substring(file.lastIndexOf(File.separator) + 1,
				                                    file.lastIndexOf("."));

				this.lstClass.add(new Classe(nomClasse));

				LireFichier.lireFichier(file, this);
			}

			// Affichage des classes de la jdk extend
			for ( String nom : this.lstExtends )
				if ( ! this.estClasse(nom) )
				{
					Classe classe = new Classe(nom);
					classe.setCachable(true);
					this.lstClass.add( classe);
				}

			// Affichage des classe de la jdk implement
			for ( String nom : this.lstImplement )
				if ( ! this.estClasse(nom) )
				{
					Classe classe = new Classe(nom);
					classe.setestInterface(true);
					classe.setCachable(true);
					this.lstClass.add( classe );
				}
			
			this.majLiaison();
		}
		catch (Exception e) { System.out.println("fichier non trouve"); }
	}

	/**
	 * Insere/modifie les proprietes de la classe courante a partir de la ligne
	 * de declaration (ex: abstract, interface, extends, implements).
	 * @param ligne la ligne contenant la declaration de la classe
	 */
	public void insererProprieteClass(String ligne)
	{
		
		String trimmed = ligne.trim();
		Classe c = this.lstClass.getLast();
		if (trimmed.contains("{")) 
			trimmed = trimmed.substring(0, trimmed.length()-1);

		if (trimmed.contains("abstract") && (trimmed.contains("class") || trimmed.contains("interface")))
			c.setestAbstract(true);

		if (trimmed.contains("interface")) 
			c.setestInterface(true);		
		

		if (trimmed.contains("extends"))
		{
			int indFin;
			if (trimmed.contains("implements"))
				indFin = trimmed.indexOf("implements");
			else
				indFin = trimmed.length();

			String heritage = trimmed.substring(trimmed.indexOf("extends") + 7, indFin).trim();
			
			
			lstExtends.add(heritage);
			c.setNomHeritageClasse(heritage);
		}

		if (trimmed.contains("implements"))
		{
			int indFin = trimmed.length();
			String partImplement = trimmed.substring(trimmed.indexOf("implements")+10, indFin).trim();	
			try
			{
				Scanner sc = new Scanner(partImplement);
				sc.useDelimiter(",");
				while (sc.hasNext())
				{
					String nomInterface = sc.next().trim();

					int ind = nomInterface.contains("<") ? nomInterface.indexOf("<") : nomInterface.length();
					nomInterface = nomInterface.substring(0, ind);
					this.lstImplement.add(nomInterface);
					c.ajouterInterface(nomInterface);
				}
				sc.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mets a jour le niveau si des accolades sont present
	 * Determine si la ligne pourait posseder des methodes ou attributs
	 * @param ligne une ligne de code
	 */
	public void analyserLigne(String ligne)
	{
		ligne = ligne.trim();
		
		String visibilite = "";
		boolean estStatic = false;
		boolean isFinal = false;
		boolean modificateurTrouve = true;

		if (ligne.isEmpty() || ligne.startsWith("//") || ligne.startsWith("/*") || ligne.startsWith("*")) return;


		while (modificateurTrouve)
		{
			modificateurTrouve = false;
			int indexEspace = ligne.indexOf(' ');
			if (indexEspace == -1) break;

			String mot = ligne.substring(0, indexEspace);
			
			switch (mot)
			{
				case "public":
				case "private":
				case "protected":
				{
					visibilite = mot;
					modificateurTrouve = true;
					break;
				}
				case "static":
				{
					estStatic = true;
					modificateurTrouve = true;
					break;
				}
				case "final" :
				{
					isFinal = true;
					modificateurTrouve = true;
					break;
				}
				case "abstract":
				case "synchronized":
				{
					modificateurTrouve = true;
					break;
				}
			}
			if (modificateurTrouve)
				ligne = ligne.substring(indexEspace + 1).trim();
		}

		if (ligne.contains("{"))
			ligne = ligne.substring(0,ligne.indexOf("{")-1);
		
		if (ligne.contains("@Override")) return;

		if (ligne.contains("(") && !ligne.contains("="))
			traiterMethode(ligne, visibilite, estStatic);
		else
			traiterAttribut(ligne, visibilite, estStatic, isFinal);
	}

	/**
	 * Traite la ligne pour creer l'attribut
	 * @param ligneRestante ligne de code avec attribut
	 * @param visibilite    Visibilite de l'attribut
	 * @param estStatic      si l'attribut est global ou instance
	 * @param isFinal       si l'attribut est une constante
	 */
	private void traiterAttribut(String ligneRestante, String visibilite, boolean estStatic, boolean isFinal)
	{
		int indexPointVirgule = ligneRestante.indexOf(';');
		if (indexPointVirgule != -1)
			ligneRestante = ligneRestante.substring(0, indexPointVirgule).trim();

		int indexEgal = ligneRestante.indexOf('=');
		if (indexEgal != -1)
			ligneRestante = ligneRestante.substring(0, indexEgal).trim();

		ligneRestante = ligneRestante.replaceAll("\\s+", " ").trim();
		int dernierEspace = ligneRestante.lastIndexOf(' ');
		if (dernierEspace == -1)
			return;

		String type = ligneRestante.substring(0, dernierEspace).trim();
		String nom = ligneRestante.substring(dernierEspace + 1).trim();
		Classe c = this.lstClass.getLast();

		c.ajouterAttribut(nom, isFinal, type, visibilite, estStatic);
	}


	/**
	 * Traite la ligne pour creer la methode
	 * @param ligneRestante ligne de code avec attribut
	 * @param visibilite    Visibilite de l'attribut
	 * @param estStatic      si l'attribut est global ou instance
	 */
	private void traiterMethode(String ligneRestante, String visibilite, boolean estStatic)
	{
		int indexParOuvrante = ligneRestante.indexOf('(');
		int indexParFermante = ligneRestante.lastIndexOf(')');

		if (indexParFermante == -1 || indexParFermante <= indexParOuvrante) 
			return; 

		String declaration = ligneRestante.substring(0, indexParOuvrante);
		String contenuParametres = "";

		String nom = "";
		String type = "";
		declaration = declaration.replaceAll("\\s+", " ").trim();
		int dernierEspace = declaration.lastIndexOf(' ');
		Classe c = this.lstClass.getLast();

		contenuParametres = ligneRestante.substring(indexParOuvrante + 1, indexParFermante);
		
		if (dernierEspace == -1)
		{
			nom = declaration;
			type = "";
		} 
		
		else
		{
			type = declaration.substring(0, dernierEspace).trim();
			nom = declaration.substring(dernierEspace + 1).trim();
		}
		
		ArrayList<Parametre> params = extraireParametres(contenuParametres);
		if (nom.equals(c.getNom()))
			type = nom;
		c.ajouterMethode(visibilite, nom, type, params, estStatic);
	}

	/**
	 * Creer les Parametre a partir d'une ligne de code
	 * @param paramsStr le string du param avec type
	 * @return lstParam lst des Parametre
	 */
	private ArrayList<Parametre> extraireParametres(String paramsStr)
	{
		ArrayList<Parametre> listeParams = new ArrayList<Parametre>();
		if (paramsStr.isEmpty())
			return listeParams;
		int debut = 0;
		while (true)
		{
			int indexVirgule = paramsStr.indexOf(',', debut);
			String unParametreStr;
			if (indexVirgule == -1)
				unParametreStr = 
			paramsStr.substring(debut).trim();
			else
				unParametreStr = paramsStr.substring(debut, indexVirgule).trim();
			int espace = unParametreStr.lastIndexOf(' ');
			if (espace != -1)
			{
				String pType = unParametreStr.substring(0, espace).trim();
				String pNom = unParametreStr.substring(espace + 1).trim();
				listeParams.add(new Parametre(pNom, pType));
			}
			if (indexVirgule == -1) break;
			debut = indexVirgule + 1;
		}
		return listeParams;
	}

	/**
	 * Remplace la liste des classes par celle fournie et reconstruit
	 * toutes les liaisons entre ces classes.
	 * Utilise lors du chargement depuis un fichier XML sans liaisons explicites.
	 * @param nouvelles la nouvelle liste de `Classe` a utiliser
	 */
	public void remplacerClassesEtLiaisons(ArrayList<Classe> nouvelles)
	{
		this.lstClass = new ArrayList<Classe>(nouvelles);
		this.lstLiaisons.clear();

		// Creer les liaisons apres avoir ajoute toutes les classes
		for (Classe classe1 : this.lstClass)
		{
			for (Classe classe2 : this.lstClass)
			{
				if (classe1 != classe2)
				{
					List<Liaison> liaisons = Liaison.creerLiaison(classe1, classe2, this);
					for (Liaison liaison : liaisons)
						if (liaison != null) this.lstLiaisons.add(liaison);
				}
			}
		}
	}
	

	/**
	 * Remplace la liste des classes par celle fournie et associe la liste de
	 * liaisons fournie aux instances de `Classe` correspondantes.
	 * Utilise lors du chargement depuis un fichier XML contenant des liaisons explicites.
	 * @param nouvelles la nouvelle liste de `Classe` a utiliser
	 * @param liaisons la liste des liaisons a reassocier aux instances de classe
	 */
	public void remplacerClassesEtLiaisons(ArrayList<Classe> nouvelles, List<Liaison> liaisons)
	{
		this.lstClass = new ArrayList<Classe>(nouvelles);
		this.lstLiaisons = new ArrayList<Liaison>();

		// Associer les liaisons fournies aux instances de Classe correctes
		for (Liaison l : liaisons)
		{
			// Assurer que la liaison reference les objets Classe charges
			Classe cFrom = null, cTo = null;
			for (Classe c : this.lstClass)
			{
				if (c.getNom().equals(l.getClasseDep().getNom())) cFrom = c;
				if (c.getNom().equals(l.getClasseArr().getNom())) cTo = c;
			}
			if (cFrom != null && cTo != null)
			{
				Liaison nl = new Liaison(cFrom, cTo, l.getMultADep(), l.getMultArr(), l.getNomVar(), this);
				this.lstLiaisons.add(nl);
			}
		}
	}

	/**
	 * Met a jour la liste des liaisons en (re)creant toutes les liaisons
	 * possibles entre les classes actuellement connues.
	 */
	public void majLiaison()
	{
		this.lstLiaisons.clear();

		for (Classe classe1 : this.lstClass)
		{
			for (Classe classe2 : this.lstClass)
			{
				if (classe1 != classe2)
				{
					List<Liaison> liaisons = Liaison.creerLiaison(classe1, classe2, this);
					for (Liaison liaison : liaisons)
						if (liaison != null) 
							this.lstLiaisons.add(liaison);
				}
			}
		}
	}

	/*---------------------------------------*/
	/*           Methode static              */
	/*---------------------------------------*/
	
	/**
	 * Ajoute tout les fichier java dans l'array list fournit
	 * @param path chemin du repertoire
	 * @param allFiles arrayListe vide qu'on va remplir avec les chemins des .java
	 * */
	public static void listeRepertoire(File path, List<String> allFiles)
	{
		File[] lst = path.listFiles();
		if (lst != null)
		{
			for (File f : lst)
			{
				if (! f.isDirectory())
				{				
					String currentFilePath = f.getAbsolutePath();
					int lastDotIndex = currentFilePath.lastIndexOf('.');
					if (lastDotIndex > 0 && currentFilePath.substring(lastDotIndex + 1).equals("java"))
						allFiles.add(currentFilePath);
				}
			}
		}
	}

}
