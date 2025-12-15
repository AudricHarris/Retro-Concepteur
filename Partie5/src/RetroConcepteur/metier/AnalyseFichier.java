package RetroConcepteur.metier;

// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import RetroConcepteur.metier.classe.*;

/**
 * Permet la lecture d'un dossier et la creation de classe.
 * Une fois crée nous ajoutons les methode et attribut à cette classe
 */
public class AnalyseFichier
{
	private ArrayList<Classe> lstClass;
	private List<Liaison>     lstLiaisons;

	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
	public AnalyseFichier( String repo)
	{
		this.lstClass    = new ArrayList<Classe> ();
		this.lstLiaisons = new ArrayList<Liaison>();
		
		ArrayList<String> allFiles = new ArrayList<String>();
		
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);

			for (String file : allFiles)
			{
				String nomClasse = file.substring(file.lastIndexOf("/") + 1,
				                                    file.lastIndexOf("."));

				this.lstClass.add(new Classe(nomClasse));

				LireFichier.lireFichier(file, this);
			}
		}
		catch (Exception e) { System.out.println("fichier non trouvé"); }

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


	//---------------------------------------//
	//             Getters                   //
	//---------------------------------------//
	

	public ArrayList<Classe> getLstClasses () { return new ArrayList<Classe> (this.lstClass);   }
	public List<Liaison>     getListLiaison() { return new ArrayList<Liaison>(this.lstLiaisons);}

	/**
	 * Retourne la liste de tout les liaison uni-directionelle
	 * @return lstUnique la liste des liaison unique
	 */
	public List<Liaison> getListLiaisonUnique()
	{
		List<Liaison> lstUnique = new ArrayList<Liaison>();
		for (Liaison l : this.lstLiaisons)
			if (!l.estBinaire())
				lstUnique.add(l);
		return lstUnique;
	}

	/**
	 * Retourne la liste de tout les liaison bi-directionelle
	 * @return lstBinaire la liste des liaison binaire
	 */
	public List<Liaison> getListLiaisonBinaire()
	{
		HashMap<Classe, Liaison> lstBinaire = new HashMap<Classe, Liaison>();
		
		for (Liaison l : this.lstLiaisons)
		{
			if (l.estBinaire() && !lstBinaire.containsKey(l.getToClass()) &&
				!lstBinaire.containsKey(l.getFromClass()))
			{
				lstBinaire.put(l.getToClass(), l);
			}
			else
			{
				if (l.estBinaire()) 
				{
					Liaison liaisonBinaire;

					if (lstBinaire.get(l.getToClass()) != null)
					{
						liaisonBinaire = lstBinaire.get(l.getToClass());
						liaisonBinaire.setFromMultiplicte( l.getFromMultiplicity());
					}
					
					if (lstBinaire.get(l.getFromClass()) != null)
					{
						liaisonBinaire = lstBinaire.get(l.getFromClass());
						liaisonBinaire.setFromMultiplicte(l.getToMultiplicity());
					}
				}
			}
		}

		return new ArrayList<Liaison>(lstBinaire.values());
	}

	/**
	 * Retourne si le nom existe dans liste classe
	 * @param nom nom du potentiel classe
	 * @return boolean si oui ou nom la classe nom existe
	 */
	public boolean estClasse(String nom)
	{
		for ( Classe c : this.lstClass) if (c.getNom().equals(nom)) return true;
		
		return false;
	}

	/**
	 * Retourne si oui ou non la classe existe même inseré dans une collections
	 * @param type la chaine de caractère du type
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
	public void insererProprieteClass(String ligne)
	{
		String trimmed = ligne.trim();
		Classe c = this.lstClass.getLast();
		if (trimmed.contains("{")) trimmed = trimmed.substring(0, trimmed.length()-1);
		if (trimmed.contains("abstract") && (trimmed.contains("class") || trimmed.contains("interface")))
			c.setIsAbstract(true);

		if (trimmed.contains("interface")) 
			c.setIsInterface(true);		
		

		if (trimmed.contains("extends"))
		{
			int indFin;
			if (trimmed.contains("implements"))
				indFin = trimmed.indexOf("implements");
			else
				indFin = trimmed.length();
			String heritage = trimmed.substring(trimmed.indexOf("extends") + 7, indFin).trim();
			c.setHeritageClasse(new Classe(heritage.trim()));
			for (Classe cls : this.lstClass)
			{
				if (cls.getNom().equals(heritage.trim()))
				{
					c.setHeritageClasse(c);
					break;
				}
			}
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
					c.ajouterInterface(nomInterface);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mets à jour le niveau si des accolades sont present
	 * Determine si la ligne pourait posseder des methodes ou attributs
	 * @param ligne une ligne de code
	 */
	public void analyserLigne(String ligne)
	{
		ligne = ligne.trim();
		
		String visibilite = "";
		boolean isStatic = false;
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
					isStatic = true;
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
			traiterMethode(ligne, visibilite, isStatic);
		else
			traiterAttribut(ligne, visibilite, isStatic, isFinal);
	}

	/**
	 * Traite la ligne pour créer l'attribut
	 * @param ligneRestante ligne de code avec attribut
	 * @param visibilite    Visibilite de l'attribut
	 * @param isStatic      si l'attribut est global ou instance
	 * @param isFinal       si l'attribut est une constante
	 */
	private void traiterAttribut(String ligneRestante, String visibilite, boolean isStatic, boolean isFinal)
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

		c.ajouterAttribut(nom, isFinal, type, visibilite, isStatic);
	}


	/**
	 * Traite la ligne pour créer la methode
	 * @param ligneRestante ligne de code avec attribut
	 * @param visibilite    Visibilite de l'attribut
	 * @param isStatic      si l'attribut est global ou instance
	 */
	private void traiterMethode(String ligneRestante, String visibilite, boolean isStatic)
	{
		int indexParOuvrante = ligneRestante.indexOf('(');
		String declaration = ligneRestante.substring(0, indexParOuvrante);
		int indexParFermante = ligneRestante.lastIndexOf(')');
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
		c.ajouterMethode(visibilite, nom, type, params, isStatic);
	}

	/**
	 * Creer les Parametre à partir d'une ligne de code
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
	/* 
	* Getters
	 */paramsStr.substring(debut).trim();
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

	//---------------------------------------//
	//           methode static              //
	//---------------------------------------//
	
	/**
	 * Ajoute tout les fichier java dans l'array list fournit
	 * @param path chemin du repertoire
	 * @param allFiles arrayListe vide qu'on va remplir avec les chemins des .java
	 * */
	public static void listeRepertoire(File path, List<String> allFiles)
	{
		File[] list = path.listFiles();
		if (list != null)
		{
			for (File f : list)
			{
				if (f.isDirectory())
				{
					listeRepertoire(f, allFiles);
				}
				else
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
