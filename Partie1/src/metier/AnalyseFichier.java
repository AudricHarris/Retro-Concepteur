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
 *
 * Niveau determine la profondeur dans la classe
 */
public class AnalyseFichier
{
	private ArrayList<Classe> lstClass;
	private int				 niveau;

	//Constructeur qui initialize la lecture
	public AnalyseFichier(String repo)
	{
		this.lstClass = new ArrayList<Classe>();
		this.niveau   = 0;

		ArrayList<String> allFiles = new ArrayList<String>();
		
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);

			// Creation de classe pour chaque fichier et init niveau à 0 pour chaque classe
			for (String file : allFiles)
			{
				Classe classCourante = new Classe(file.substring(file.lastIndexOf("/") + 1,file.lastIndexOf(".")));
				this.lstClass.add(classCourante);

				LireFichier.lireFichier(file, this);
				this.niveau = 0;
			}
		}
		catch (Exception e) { System.out.println("fichier non trouvé"); }

		for (Classe c : this.lstClass) System.out.println(c);
	}

	// Getters
	public ArrayList<Classe> getLstClasses() {return new ArrayList<Classe>(this.lstClass);}
	public int			   getNiveau	() {return this.niveau;}
	
	public boolean getModificateur(String[] parts, int cpt)
	{
		return (parts[cpt].equals("public")    || parts[cpt].equals("private") || 
		        parts[cpt].equals("protected") || parts[cpt].equals("static")  || 
		        parts[cpt].equals("final"));
	}

	// Methode Instance
	
	// Analyse la ligne et mets a jour le positionement du niveau
	public void analyserLigne(String ligne)
	{
		String trimmed = ligne.trim();
		
		// Determiner si commentaire ou si nécessaire de traiter
		if (ligne.length() < 1) return;
		if (trimmed.isEmpty() || trimmed.startsWith("//")) return;
		
		// Metre à jour le niveau
		if (trimmed.contains("{")) this.niveau++;
		if (trimmed.contains("}")) this.niveau--;

		if (this.niveau == 0 && trimmed.contains("class ") && trimmed.contains("{")) 
		{
			int classIdx = trimmed.indexOf("class ");
			if (classIdx >= 0) 
			{
				int end = trimmed.indexOf("{", classIdx);
				if (end > 0) 
				{
					String afterClass = trimmed.substring(classIdx + 6, end).trim();
					String[] words = afterClass.split("\\s+");
				}
			}
		}
		
		if (this.niveau == 1) this.extraireMethodeAttribut(trimmed);
	}

	// traite la ligne et determine si çela est une methode ou non
	public void extraireMethodeAttribut(String ligne)
	{
		String trimmed = ligne.trim();
		if (trimmed.isEmpty()) return;

		String[] parts = trimmed.split("\\s+");

		String visibility = "";
		boolean isStatic = false;
		boolean isFinal = false;

		// Modificateur de l'attribut || methode
		for (int cpt = 0; cpt < parts.length; cpt++)
		{
			switch (parts[cpt])
			{
				case "public": 
				case "private": 
				case "protected":
					if (visibility.isEmpty()) visibility = parts[cpt];
					break;
				case "static": isStatic = true; break;
				case "final":  isFinal  = true; break;
				default: 
					cpt = parts.length; 
					continue;
			}
		}

		parts = trimmed.split("\\s+"); // petit reset
		
		int cpt = 0;
		// Passer les Modificateur
		while (cpt < parts.length && this.getModificateur(parts, cpt))
			cpt++;

		if (cpt >= parts.length) return;

		String type = parts[cpt++];
		String name;

		if (type.contains("("))
		{
			int idx = type.indexOf("(");
			name = type.substring(0, idx);
			type = "";
		}
		else
		{
			if (cpt < parts.length) name = parts[cpt].split("\\(")[0];
			else				  name = type;
		}

		if (name.length() <= 2) return;

		Classe c = lstClass.getLast();

		// Detect method || attribut
		if (trimmed.contains("(") && !trimmed.contains("="))
		{
			int start = trimmed.indexOf('(') + 1;
			int end = trimmed.indexOf(')');
			if (end < 0) end = trimmed.length();

			String paramStr = trimmed.substring(start, end).trim();
			ArrayList<Parametre> params = new ArrayList<>();

			if (!paramStr.isEmpty())
			{
				for (String p : paramStr.split(","))
				{
					String[] tp = p.trim().split("\\s+");
					if (tp.length >= 2)
					{
						String pName = tp[tp.length - 1];
						String pType = String.join(" ", java.util.Arrays.copyOf(tp, tp.length - 1));
						params.add(new Parametre(pName, pType));
					}
				}
			}

			String returnType = type;
			if (name.equals(c.getNom())) returnType = name; // constructeur

			c.ajouterMethode(visibility, name, returnType, params);
		}
		else
		{
			name = name.replace(";", "");
			c.ajouterAttribut(name, isFinal, type, visibility, isStatic);
		}
	}


	// Methode objet

	/* 
	 * Ajoute tout les fichier java dans l'array list fournit
	 * */
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
					if (lastDotIndex > 0 && currentFilePath.substring(lastDotIndex + 1).equals("java"))
						allFiles.add(currentFilePath);
				}
			}
		}
	}
}
