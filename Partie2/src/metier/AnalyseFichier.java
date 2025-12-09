package metier;
// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.List;
// Import package itern
import metier.classe.*;
/*
 * Le corps du metier :
 * - Permet la lecture d'un dossier et la creation de classe
 * Elle comporte une methode traitement de ligne et determinerPropriete
 * qui créer les methodes et attribut d'une classe
 */
public class AnalyseFichier
{
	private ArrayList<Classe> lstClass;
	private int niveau;
	// Instancier les variables + lire le repo et tout les fichier du repo en .java
	// TODO : Remplacer classCourante par this.lstClass.getLast()
	public AnalyseFichier(String repo)
	{
		this.lstClass = new ArrayList<Classe>();
		this.niveau = 0;
		ArrayList<String> allFiles = new ArrayList<String>();
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);
			for (String file : allFiles)
			{
				Classe classCourante = new Classe(file.substring(file.lastIndexOf("/") + 1,file.lastIndexOf(".")));
				this.lstClass.add(classCourante);
				LireFichier.lireFichier(file, this);
				this.niveau = 0;
			}
		}
		catch (Exception e)
		{
			System.out.println("fichier non trouvé");
		}
	}
	// methode permettant la lecture d'un repertoire pour avoir tout ces enfants du repertoire
	// TODO: Re-organiser les static pour qu'il soit dans le bon ordre de lecture en java
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
					if (lastDotIndex > 0 && currentFilePath.substring(lastDotIndex + 1).toLowerCase().equals("java"))
						allFiles.add(currentFilePath);
				}
			}
		}
		else
		{
			System.err.println(path + " : Erreur de lecture.");
		}
	}
	// Analyse la ligne et mets a jour le positionement du niveau
	public void analyserLigne(String ligne)
	{
		if (ligne.length() < 1) return;
		String trimmed = ligne.trim();
		if (trimmed.isEmpty() || trimmed.startsWith("//"))
			return;
		if (trimmed.contains("{"))
			this.niveau++;
		if (trimmed.contains("}"))
			this.niveau--;
		if (this.niveau == 0 && trimmed.contains("class ") && trimmed.contains("{")) {
			int classIdx = trimmed.indexOf("class ");
			if (classIdx >= 0) {
				int end = trimmed.indexOf("{", classIdx);
				if (end > 0) {
					String afterClass = trimmed.substring(classIdx + 6, end).trim();
					String[] words = afterClass.split("\\s+");
				}
			}
		}
		if (this.niveau == 1)
			this.extraireMethodeAttribut(trimmed);
	}

	// traite la ligne et determine si çela est une methode ou non
	// Fixed: Improved parsing to handle method/constructor names with attached
	// parentheses,
	// parameter types with spaces/generics/arrays, and trims semicolons from field
	// names.
	// Constructors now correctly identified and parsed even with parameters.
	//TODO: Rendre propre le code;
	public void extraireMethodeAttribut(String ligne)
	{

		String trimmed = ligne.trim();
		if (trimmed.isEmpty())
			return;
		String[] parts = trimmed.split("\\s+");
		int i = 0;
		String visibilite = "";
		boolean isStatic = false;
		boolean constante = false;

		Classe classCourante = this.lstClass.getLast();

		while (i < parts.length && (parts[i].equals("public") || parts[i].equals("private")
				|| parts[i].equals("protected") || parts[i].equals("static") || parts[i].equals("final"))) 
		{
			if (parts[i].equals("public") || parts[i].equals("private") || parts[i].equals("protected")) 
			{
				if (visibilite.isEmpty())
					visibilite = parts[i];
			} 
			else if (parts[i].equals("static"))
				isStatic = true;
			else if (parts[i].equals("final"))
				constante = true;
			i++;
		}
		if (i >= parts.length)
			return;
		String typePart = parts[i++];
		String nom = "";
		if (typePart.contains("(")) 
		{
			// Likely constructor: typePart contains the name followed by (
			int parenIdx = typePart.indexOf('(');
			nom = typePart.substring(0, parenIdx);
			typePart = "";
		} 
		else 
		{
			// Normal case
			if (i >= parts.length) 
			{
				nom = typePart;
			} else 
			{
				nom = parts[i++];
				int parenIdx = nom.indexOf('(');
				if (parenIdx > 0) 
					nom = nom.substring(0, parenIdx);
				
			}
		}
		if (nom.length() <= 2)
			return;
		if (ligne.contains("(") && !ligne.contains("=")) 
		{
			int start = ligne.indexOf('(') + 1;
			int end = ligne.indexOf(')');
			if (end < 0)
				end = ligne.length();
			String paramsStr = ligne.substring(start, end).trim();
			ArrayList<Parametre> lstParam = new ArrayList<Parametre>();
			if (!paramsStr.isEmpty()) 
			{
				String[] paramParts = paramsStr.split(",");
				for (String pp : paramParts) 
				{
					pp = pp.trim();
					if (pp.isEmpty())
						continue;
					String[] tp = pp.split("\\s+");
					if (tp.length >= 2) 
					{
						String pnom = tp[tp.length - 1].trim();
						StringBuilder ptype = new StringBuilder();
						// TODO : Le coluege à fait de la merde
						for (int k = 0; k < tp.length - 1; k++) 
						{
							if (k > 0)
								ptype.append(" ");
							ptype.append(tp[k]);
						}
						lstParam.add(new Parametre(pnom, ptype.toString()));
					}
				}
			}
			String returnType = typePart;
			if (nom.equals(classCourante.getNom())) 
			{
				returnType = nom; // Constructor
			}
			classCourante.ajouterMethode(visibilite, nom, returnType, lstParam, isStatic);
		} else {
			// Attribute
			int semi = nom.indexOf(';');
			if (semi >= 0 && semi < nom.length()) {
				nom = nom.substring(0, semi);
			}
			classCourante.ajouterAttribut(nom, constante, typePart, visibilite, isStatic);
		}
	}

	// TODO: deplacer les getters et faire des getters pour tout les variables (ça
	// nous aidera si besoin)
	public ArrayList<Classe> getLstClasses() {
		return new ArrayList<Classe>(this.lstClass);
	}

	public int getNiveau() {
		return this.niveau;
	}
}
