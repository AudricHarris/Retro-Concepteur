package metier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * Permet la lecture d'un fichier .java
 * Classe regroupant la lecture de la ligne
 */
public class LireFichier 
{

	/*
	 * Prend en paramètre un chemin vers un fichier et un analyseFichier
	 * Lis ligne par ligne chaque fichier
	 * si ce n'est pas un commentaire on fait une analyseFichier de la ligne
	 */
	public static void lireFichier(String chemin, AnalyseFichier analyseFichier)
	{
		boolean estCommentaire = false;
		if (chemin == null || chemin.isEmpty())
		{
			throw new IllegalArgumentException("Le chemin ne peut pas être null");
		}

		List<String> codeNetoye = LireFichier.codePropre(chemin);
		System.out.println(codeNetoye);
		for (String s : codeNetoye)
			analyseFichier.analyserLigne(s);
	}

	public static List<String> codePropre(String chemin)
	{
		List<String> codeLines = new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(new File(chemin), "UTF-8");
			int niveau = 0;
			boolean estCommentaire = false;
			while (scanner.hasNextLine()) {
				String ligne = scanner.nextLine();
				ligne = ligne.replace("\t", "").trim();
				if (ligne.isEmpty()) {
					continue;
				}
				boolean skip = false;
				if (estCommentaire) {
					if (ligne.contains("*/")) {
						estCommentaire = false;
					}
					skip = true;
				} else if (ligne.startsWith("//")) {
					skip = true;
				} else if (ligne.startsWith("/*")) {
					if (!ligne.endsWith("*/")) {
						estCommentaire = true;
					}
					skip = true;
				} else if (ligne.startsWith("*")) {
					if (ligne.contains("*/")) {
						estCommentaire = false;
					}
					skip = true;
				}
				if (skip) {
					continue;
				}
				// Gérer les commentaires inline /* potentiels
				if (ligne.contains("/*")) {
					estCommentaire = true;
				}
				// Ajouter si niveau == 1
				if (niveau == 1) {
					codeLines.add(ligne);
				}
				// Mettre à jour le niveau
				int opens = ligne.length() - ligne.replace("{", "").length();
				int closes = ligne.length() - ligne.replace("}", "").length();
				niveau += opens - closes;
				if (niveau < 0) {
					niveau = 0;
				}
			}
			scanner.close();
		}
		catch (FileNotFoundException e) { e.printStackTrace();}

		return codeLines;
	}
}
