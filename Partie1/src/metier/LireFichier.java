package metier;

import java.io.File;
import java.util.Scanner;


public class LireFichier 
{
	public static void lireFichier(String chemin, AnalyseFichier analyseFichier)
	{
		boolean estCommentaire = false;
		if (chemin == null || chemin.isEmpty()) 
		{
			throw new IllegalArgumentException("Le chemin ne peut pas être null");
		}
		try
		{
			Scanner scanner = new Scanner(new File(chemin), "UTF-8");
			while (scanner.hasNextLine()) 
			{
				String ligne = scanner.nextLine();
				if (ligne.replace("\t", " ").trim().startsWith("//") || ligne.replace("\t", " ").trim().startsWith("/*")) 
					estCommentaire = true;
				
				if (!estCommentaire)
					analyseFichier.analyserLigne(ligne);

				if (ligne.replace("\t", " ").trim().startsWith("//")) 
					estCommentaire = false;

				if (ligne.trim().endsWith("*/")) 
					estCommentaire = false;

			}
			scanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}
