package metier;


// Import package extern
import java.io.File;

import java.util.ArrayList;
import java.util.List;


// Import package itern
//import metier.classe.*;

public class AnalyseFichier 
{
	//private ArrayList<Classe> lstClass;
	//private Classe classCourante;

	public AnalyseFichier(String repo)
	{
		//this.lstClass = new ArrayList<Classe>();

		ArrayList<String> allFiles = new ArrayList<String>();
		try
		{
			File f = new File(repo);

			AnalyseFichier.listeRepertoire(f, allFiles);

			for (String file : allFiles)
			{
				LireFichier.lireFichier(file, this);
			}
		}
		catch (Exception e)
		{
			System.out.println("fichier non trouvé");
		}
	}

	public static void listeRepertoire(File path, List<String> allFiles)
	{
 
		File[] list = path.listFiles();
		if (list != null)
		{
			for (int i = 0; i < list.length; i++)
			{
				String currentFilePath = list[i].getAbsolutePath();
				allFiles.add(currentFilePath);
			}
		}
		else
		{
			System.err.println(path + " : Erreur de lecture.");
		}
	}

	public void analyserLigne(String ligne)
	{
		System.out.println(ligne);
	}


}
