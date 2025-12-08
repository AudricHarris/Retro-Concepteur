package metier;


// Import package extern
import java.io.File;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;


// Import package itern
import metier.classe.*;

public class AnalyseFichier 
{
	private ArrayList<Classe> lstClass;
	//private Classe classCourante;
	private int niveau;

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
				int lastDotIndex = currentFilePath.lastIndexOf('.');
				System.out.println(currentFilePath);
				if (currentFilePath.substring(lastDotIndex + 1).toLowerCase().equals("java"))
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
		String[] tabString = ligne.split(" ");
		System.out.println();
		for (String s : tabString)
		{
			if (this.niveau == 1)
				System.out.print(s.trim() + " ");
		}

		if (tabString[tabString.length-1].trim().equals("{")) this.niveau++;
		if (tabString[tabString.length-1].trim().equals("}")) this.niveau--;
	}

	public ArrayList<Classe> getLstClasses()
	{
		return this.lstClass;
	}


}
