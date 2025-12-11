package metier;


// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Import package itern
import metier.classe.*;

import controller.Controller;

/**
 * - Permet la lecture d'un dossier et la creation de classe.
 *   Une fois crée nous ajoutons les methode et attribut à cette classe
 */
public class AnalyseFichier
{
	/**Contrôleur de l'application */
	private Controller ctrl;

	/**Stocke les classes sous format d'array list */
	private ArrayList<Classe> lstClass;

	/**Montre le niveau courant dans la lecture d'un fichier*/
	private int               niveau;

	/**Stocke les liaisons entre les classes*/
	private List<Liaison> lstLiaisons;

	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
	public AnalyseFichier(Controller ctrl, String repo)
	{
		this.ctrl     = ctrl;
		this.lstClass = new ArrayList<Classe>();
		this.niveau   = 0;

		ArrayList<String> allFiles = new ArrayList<String>();

		this.lstLiaisons = new ArrayList<Liaison>();
		
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);

			// Creation de classe pour chaque fichier et init niveau à 0 pour chaque classe
			for (String file : allFiles)
			{
				Classe classCourante = new Classe(file.substring(file.lastIndexOf("/") + 1,file.lastIndexOf(".")));
				this.lstClass.add(classCourante);

				LireFichier.lireFichier(file, this);
				this.niveau = 0;
			}
		}
		catch (Exception e) { System.out.println("fichier non trouvé"); }

		// Création des liaisons entre les classes
		for (Classe classe1 : this.lstClass) 
		{
			for (Classe classe2 : this.lstClass) 
			{
				if (classe1 != classe2) 
				{
					List<Liaison> liaisons = Liaison.creerLiaison(classe1, classe2);
		
					for (Liaison liaison : liaisons) 
					{
						if (liaison != null) 
						{
							this.lstLiaisons.add(liaison);
							System.out.println(liaison);
						}
					}
				}
			}
			
		}
		
	}

	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	/**
	 * Renvoie la list des classes
	 * @return ArrayList ArrayList de tout les classe qu'on a parcourus
	 */
	public ArrayList<Classe> getLstClasses() {return new ArrayList<Classe>(this.lstClass);}

	/**
	 * Renvoie le niveau actuelle (profondeur de lecture)
	 * @return nbNiveau le niveau actuelle
	 */
	public int getNiveau    () {return this.niveau;}



	//---------------------------------------//
	//         methode instance              //
	//---------------------------------------//
	
	/**
	 * Mets à jour le niveau si des accolades sont present
	 * Determine si la ligne pourait posseder des methodes ou attributs
	 * @param ligne une ligne de code
	 */
	public void analyserLigne(String ligne)
	{
		String trimmed = ligne.trim();
		
		// Determiner si commentaire ou si nÃ©cessaire de traiter
		if (ligne.length() < 1) return;
		if (trimmed.isEmpty() || trimmed.startsWith("//")) return;
		


		if (trimmed.contains("abstract")) 
		{
			Classe c = lstClass.getLast();
			c.setIsAbstract(true);
		}
		if (trimmed.contains("interface")) 
		{
			Classe c = lstClass.getLast();
			c.setIsInterface(true);
		}

		if (this.niveau == 0 &&(trimmed.contains("extends")|| trimmed.contains("implements")))
		{
			if (trimmed.contains("extends")) 
			{
				int indFin;
				if (trimmed.contains("implements"))
					indFin = trimmed.indexOf("implements");
				else
					indFin = trimmed.length();
				String heritage = trimmed.substring(trimmed.indexOf("extends") + 7, indFin).trim();
				Classe c = lstClass.getLast();
				c.setHeritageClasse(new Classe(heritage.trim()));
				for (Classe cls : lstClass)
					if (cls.getNom().equals(heritage.trim()))
					{
						c.setHeritageClasse(c);
						break;
					}
			}
			if (trimmed.contains("implements")) 
			{
				int indFin = trimmed.length();
				String partImplement = 	trimmed.substring(trimmed.indexOf("implements")+10, indFin).trim();
				try 
				{
					Scanner sc = new Scanner(partImplement);
					sc.useDelimiter(",");
					Classe c = lstClass.getLast();
					while (sc.hasNext())
					{
						String nomInterface = sc.next().trim();
						if(nomInterface.contains("<"))
						{
							nomInterface = nomInterface.substring(0, nomInterface.indexOf("<")).trim();
						}
						c.ajouterInterface(nomInterface);
					}
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if (this.niveau == 1) this.extraireMethodeAttribut(trimmed);
		// Metre Ã  jour le niveau
		if (trimmed.contains("{")) this.niveau++;
		if (trimmed.contains("}")) this.niveau--;

		
	}


	public void extraireMethodeAttribut(String ligne) 
	{
		ligne = ligne.trim();
		if (ligne.isEmpty() || ligne.startsWith("//") || ligne.startsWith("/*") || ligne.startsWith("*")) return;

		String visibilite = ""; 
		boolean isStatic = false;
		boolean isFinal = false;

		boolean modificateurTrouve = true;
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

		if (ligne.contains("(") && !ligne.contains("=")) 
			traiterMethode(ligne, visibilite, isStatic);
		else 
			traiterAttribut(ligne, visibilite, isStatic, isFinal);
	}
	

	private void traiterAttribut(String ligneRestante, String visibilite, boolean isStatic, boolean isFinal) 
	{

		int indexPointVirgule = ligneRestante.indexOf(';');
		if (indexPointVirgule != -1) 
			ligneRestante = ligneRestante.substring(0, indexPointVirgule).trim();
		

		int indexEgal = ligneRestante.indexOf('=');
		if (indexEgal != -1) 
			ligneRestante = ligneRestante.substring(0, indexEgal).trim();
	

		int dernierEspace = ligneRestante.lastIndexOf(' ');
		if (dernierEspace == -1) 
			return; 

		String type = ligneRestante.substring(0, dernierEspace).trim();
		String nom = ligneRestante.substring(dernierEspace + 1).trim();

		
		Classe c = lstClass.getLast();
		c.ajouterAttribut(nom, isFinal, type, visibilite, isStatic);
	}


	private void traiterMethode(String ligneRestante, String visibilite, boolean isStatic) 
	{
		int indexParOuvrante = ligneRestante.indexOf('(');
		
		String declaration = ligneRestante.substring(0, indexParOuvrante);
		
		int indexParFermante = ligneRestante.lastIndexOf(')');
		String contenuParametres = "";

		contenuParametres = ligneRestante.substring(indexParOuvrante + 1, indexParFermante);
		
		String nom = "";
		String type = "";
		
		int dernierEspace = declaration.lastIndexOf(' ');
		Classe c = lstClass.getLast();

		if (dernierEspace == -1) 
		{
			nom = declaration;
			type = ""; 
		} else 
		{
			type = declaration.substring(0, dernierEspace).trim();
			nom = declaration.substring(dernierEspace + 1).trim();
		}

		ArrayList<Parametre> params = extraireParametres(contenuParametres);

		if (nom.equals(c.getNom())) 
			type = nom;

		c.ajouterMethode(visibilite, nom, type, params, isStatic);
	}


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
				unParametreStr = paramsStr.substring(debut).trim();
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
	//          methode static               //
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

