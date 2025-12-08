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
	private Classe classCourante;
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
				this.niveau = 0;
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
		if(tabString.length>4)
			if(tabString[3].contains("main"))
				return;
		
		if (this.niveau == 1) this.determinerPropriete(ligne);

		if (tabString[tabString.length-1].contains("{")) this.niveau++;
		if (tabString[tabString.length-1].contains("}")) this.niveau--;
		if (tabString[tabString.length-1].contains("/*")) this.niveau++;
		if (tabString[tabString.length-1].contains("*/")) this.niveau--;
	}

	
	public void determinerPropriete(String ligne)
	{
		ligne = ligne.replace("\t", "");
		if (ligne.length() <= 1 ) return;
		if (ligne.substring(0, 1).equals("//")) return;

		String  visibilite = "public";
		boolean isStatic   =    false;
		String  type       =   "void";
		String  nom        =       "";

		if (ligne.contains("(") || ligne.contains(")"))
		{
			System.out.println("Methode : "+ ligne );
		}
		else
		{
			System.out.println("Attribut : " + ligne);
		}
	}
	

	public ArrayList<Classe> getLstClasses()
	{
		return this.lstClass;
	}


}
