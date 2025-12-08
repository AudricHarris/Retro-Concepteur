package metier;

import java.io.File;
import java.util.Scanner;


public class LireFichier 
{
	public static void lireFichier(String chemin, AnalyseFichier analyseFichier)
	{
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
				analyseFichier.analyserLigne(ligne);
			}
			scanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}
