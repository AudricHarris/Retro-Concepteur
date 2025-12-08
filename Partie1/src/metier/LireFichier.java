package src.metier;

import java.io.File;
import java.util.Scanner;

public class LireFichier 
{
	private AnalyseFichier analyseFichier;
	public LireFichier(String chemin, AnalyseFichier analyseFichier)
	{
		this.analyseFichier = analyseFichier;
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
				this.analyseFichier.analyserLigne(ligne);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}