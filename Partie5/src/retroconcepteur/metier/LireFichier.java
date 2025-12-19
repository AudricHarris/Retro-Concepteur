package retroconcepteur.metier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Permet la lecture d'un fichier .java
 * Classe regroupant la lecture de la ligne
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */
public class LireFichier 
{

	/** 
	 * Prend en parametre un chemin vers un fichier et un analyseFichier
	 * Lis ligne par ligne chaque fichier
	 * si ce n'est pas un commentaire on fait une analyseFichier de la ligne
	 * @param chemin Le chemin du fichier a lire
	 * @param analyseFichier L'analyseur de fichier
	 */
	public static void lireFichier(String chemin, AnalyseFichier analyseFichier)
	{
		if (chemin == null || chemin.isEmpty())
			throw new IllegalArgumentException("Le chemin ne peut pas etre null");

		List<String> codeNetoye = LireFichier.codePropre(chemin, analyseFichier);
		for (String s : codeNetoye)
			analyseFichier.analyserLigne(s);
	}

	/** 
	 * Sert a nettoyer renvoyer le code sans les commentaires
	 * @param chemin Le chemin du fichier a lire
	 * @param analyseFichier L'analyseur de fichier
	 * @return Une liste de lignes de code sans commentaires
	*/
	public static List<String> codePropre(String chemin, AnalyseFichier analyseFichier) 
	{
		List<String> codeLines = new ArrayList<String>();
		StringBuilder currentSignature = null;
		try 
		{
			Scanner scanner = new Scanner(new File(chemin), "UTF-8");
			int niveau = 0;
			boolean estCommentaire = false;
			while (scanner.hasNextLine()) 
			{
				String ligne = scanner.nextLine();
				ligne = ligne.replace("\t", "").trim();
				
				if (ligne.contains("class") || ligne.contains("interface"))
					analyseFichier.insererProprieteClass(ligne);

				if (ligne.isEmpty()) continue;

				boolean skip = false;

				if (estCommentaire) 
				{
					if (ligne.contains("*/"))
						estCommentaire = false;
					
					skip = true;
				}

				else if (ligne.startsWith("//"))
					skip = true;
				
				else if (ligne.startsWith("/*"))
				{
					if (!ligne.endsWith("*/")) 
						estCommentaire = true;

					skip = true;
				} 
				
				else if (ligne.startsWith("*")) 
				{
					if (ligne.contains("*/")) 
						estCommentaire = false;
					skip = true;
				}
				
				if (skip) 
					continue;

				if (ligne.contains("/*")) estCommentaire = true;

				if (niveau == 1) 
				{
					if (currentSignature == null) currentSignature = new StringBuilder();

					currentSignature.append(ligne).append("\n");
					if (ligne.contains(";") || ligne.contains("{")) 
					{
						String s = currentSignature.toString().trim();

						if (s.endsWith("{")) 
							s = s.substring(0, s.length() - 1).trim();

						codeLines.add(s);
						currentSignature = null;
					}
				}

				int opens = ligne.length() - ligne.replace("{", "").length();
				int closes = ligne.length() - ligne.replace("}", "").length();
				niveau += opens - closes;
				if (niveau < 0)
					niveau = 0;
			}
			scanner.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		return codeLines;
	}
}
