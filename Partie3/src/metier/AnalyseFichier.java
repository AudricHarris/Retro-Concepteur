package metier;
// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
// Import package itern
import metier.classe.*;
import controller.Controller;
/**
 * - Permet la lecture d'un dossier et la creation de classe.
 * Une fois crée nous ajoutons les methode et attribut à cette classe
 */
public class AnalyseFichier
{
	private Controller ctrl;
	private ArrayList<Classe> lstClass;
	private List<Liaison> lstLiaisons;
	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
	public AnalyseFichier(Controller ctrl, String repo)
	{
		this.ctrl = ctrl;
		this.lstClass = new ArrayList<Classe>();
		ArrayList<String> allFiles = new ArrayList<String>();
		this.lstLiaisons = new ArrayList<Liaison>();
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);
			for (String file : allFiles)
			{
				Classe classCourante = new Classe(file.substring(file.lastIndexOf("/") + 1,file.lastIndexOf(".")));
				this.lstClass.add(classCourante);
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
					{
						if (liaison != null)
							this.lstLiaisons.add(liaison);
					}
				}
			}
		}
	}
	//---------------------------------------//
	// Getters //
	//---------------------------------------//
	/**
	 * Renvoie la list des classes
	 * @return ArrayList ArrayList de tout les classe qu'on a parcourus
	 */
	public ArrayList<Classe> getLstClasses() {return new ArrayList<Classe>(this.lstClass);}
	/**
	 * Determine si le string est un Modificateur
	 * @param text fraction d'une ligne
	 * @return boolean si le text contient un Modificateur
	 */
	public boolean getModificateur(String text)
	{
		return (text.equals("public") || text.equals("private") ||
				text.equals("protected") || text.equals("static") ||
				text.equals("final"));
	}

	public List<Liaison> getListLiaison() { return this.lstLiaisons; }

	public List<Liaison> getListLiaisonUnique()
	{
		List<Liaison> lstUnique = new ArrayList<Liaison>();
		for (Liaison l : this.lstLiaisons)
			if (!l.estBinaire())
				lstUnique.add(l);
		return lstUnique;
	}

	public List<Liaison> getListLiaisonBinaire()
	{
		HashMap<Classe, Liaison> lstBinaire = new HashMap<Classe, Liaison>();
		for (Liaison l : this.lstLiaisons)
			if (l.estBinaire() && !lstBinaire.containsKey(l.getToClass()) &&
					!lstBinaire.containsKey(l.getFromClass()))
				lstBinaire.put(l.getToClass(), l);
		return new ArrayList<Liaison>(lstBinaire.values());
	}

	public boolean isAClass(String nom)
	{
		for ( Classe c : this.lstClass)
			if (c.getNom().equals(nom)) return true;
		return false;
	}

	public boolean refersToProjectClass(String type) 
	{
		if (type == null || type.trim().isEmpty()) return false;
		type = type.trim();

		while (type.endsWith("[]")) {
			type = type.substring(0, type.length() - 2).trim();
		}

		if (!type.contains("<")) {
			return isAClass(type);
		}

		int start = type.indexOf('<');
		int end = type.lastIndexOf('>');

		if (start != -1 && end > start) 
		{
			String base = type.substring(0, start).trim();
			if (isAClass(base)) return true;

			String args = type.substring(start + 1, end).trim();
			if (!args.isEmpty()) 
			{
				String[] parts = args.split(",");
				for (String part : parts)
					if (refersToProjectClass(part.trim())) 
						return true;
			}
		}

		return false;
	}

	//---------------------------------------//
	// methode instance //
	//---------------------------------------//
	public void insererProprieteClass(String ligne)
	{
		String trimmed = ligne.trim();
		Classe c = this.lstClass.getLast();
		if (trimmed.contains("{")) trimmed = trimmed.substring(0, trimmed.length()-1);
		if (trimmed.contains("abstract")) 
			c.setIsAbstract(true);
		

		if (trimmed.contains("extends"))
		{
			int indFin;
			if (trimmed.contains("implements"))
				indFin = trimmed.indexOf("implements");
			else
				indFin = trimmed.length();
			String heritage = trimmed.substring(trimmed.indexOf("extends") + 7, indFin).trim();
			c.setHeritageClasse(new Classe(heritage.trim()));
			for (Classe cls : lstClass)
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

		ligneRestante = ligneRestante.replaceAll("\\s+", " ").trim();
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

		String nom = "";
		String type = "";
		declaration = declaration.replaceAll("\\s+", " ").trim();
		int dernierEspace = declaration.lastIndexOf(' ');
		Classe c = lstClass.getLast();

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
	// methode static //
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
