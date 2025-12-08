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
				ligne = ligne.replace("\t", "").trim();

				if (ligne.startsWith("//") || ligne.startsWith("/*") || ligne.endsWith("*/") || ligne.startsWith("*"))
				{
					estCommentaire = ligne.startsWith("/*") && !ligne.endsWith("*/") ;
				}
				else
				{
					analyseFichier.analyserLigne(ligne);
					if (ligne.contains("/*")) estCommentaire = true;
				}
				


			}
			scanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}
