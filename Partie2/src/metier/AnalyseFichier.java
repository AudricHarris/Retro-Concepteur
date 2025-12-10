package metier;


// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Import package itern
import metier.classe.*;

/**
 * - Permet la lecture d'un dossier et la creation de classe.
 *   Une fois crée nous ajoutons les methode et attribut à cette classe
 */
public class AnalyseFichier
{
	/**Stocke les classes sous format d'array list */
	private ArrayList<Classe> lstClass;

	/**Montre le niveau courant dans la lecture d'un fichier*/
	private int               niveau;

	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
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

		// Affichage Temporaire pour Partie 1
		//for (Classe c : this.lstClass) System.out.println(c);
	}

	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	/**
	 * Renvoie la list des classes
	 * @return ArrayList ArrayList de tout les classe qu'on a parcourus
	 */
	public ArrayList<Classe> getLstClasses() {return new ArrayList<Classe>(this.lstClass);}

	/**
	 * Renvoie le niveau actuelle (profondeur de lecture)
	 * @return nbNiveau le niveau actuelle
	 */
	public int getNiveau    () {return this.niveau;}

	/**
	 * Determine si le string est un Modificateur
	 * @param text fraction d'une ligne
	 * @return boolean si le text contient un Modificateur
	 */
	public boolean getModificateur(String text)
	{
		return (text.equals("public")    || text.equals("private") || 
				text.equals("protected") || text.equals("static")  || 
				text.equals("final"));
	}

	//---------------------------------------//
	//         methode instance              //
	//---------------------------------------//
	
	/**
	 * Mets à jour le niveau si des accolades sont present
	 * Determine si la ligne pourait posseder des methodes ou attributs
	 * @param ligne une ligne de code
	 */
	public void analyserLigne(String ligne)
	{
		String trimmed = ligne.trim();
		
		// Determiner si commentaire ou si nécessaire de traiter
		if (ligne.length() < 1) return;
		if (trimmed.isEmpty() || trimmed.startsWith("//")) return;
		
		// Metre a jour le niveau
		if (trimmed.contains("{")) this.niveau++;
		if (trimmed.contains("}")) this.niveau--;

		if (this.niveau == 1) this.extraireMethodeAttribut(trimmed);
	}

	/**
	 * Traite la ligne et Instancie les méthode ou attribut de cette ligne
	 * @param ligne une ligne de codei à information utile
	 */
	public void extraireMethodeAttribut(String ligne)
	{
		String trimmed = ligne.trim();
		if (trimmed.isEmpty()) return;

		String visibility = "";
		boolean isStatic = false;
		boolean isFinal = false;

		Scanner sc = new Scanner(ligne);
		sc.useDelimiter("\\s+"); // tout espace qui ce répéte une ou plusieurs fois

		while (sc.hasNext())
		{
			String text = sc.next();
			switch (text)
			{
				case "public":
				case "private":
				case "protected":
					if (visibility.isEmpty()) visibility = text;
					break;
				case "static": isStatic = true; break;
				case "final":  isFinal  = true; break;
				default:
					break;
			}
		}

		sc = new Scanner(ligne);
		sc.useDelimiter("\\s+"); // tout espace qui ce rÃ©pÃ¨te une ou plusieur fois

		String text = "";
		while (sc.hasNext())
		{
			text = sc.next();
			if (!this.getModificateur(text)) break;
		}

		if (!sc.hasNext() && this.getModificateur(text)) return;

		String type = text;
		String name = sc.hasNext() ? sc.next() : type;

		if (type.contains("("))
		{
			int idx = type.indexOf("(");
			name = type.substring(0, idx);
			type = "";
		}
		else
			name = name.split("\\(")[0];

		if (name.length() <= 2) return;

		Classe c = lstClass.getLast();

		if (trimmed.contains("(") && !trimmed.contains("="))
		{
			int start = trimmed.indexOf('(') + 1;
			int end   = trimmed.indexOf(')');
			if (end < 0) end = trimmed.length();

			String paramStr = trimmed.substring(start, end).trim();
			ArrayList<Parametre> params = new ArrayList<>();

			if (!paramStr.isEmpty())
			{
				Scanner param = new Scanner(paramStr);
				param.useDelimiter(",");
				while (param.hasNext())
				{
					String p = param.next().trim();
					Scanner sp = new Scanner(p);
					sp.useDelimiter("\\s+");
	
					ArrayList<String> parts = new ArrayList<>();
					while (sp.hasNext()) parts.add(sp.next());

					if (parts.size() >= 2)
					{
						String pName = parts.get(parts.size() - 1);
						String pType = String.join(" ", parts.subList(0, parts.size() - 1));
						params.add(new Parametre(pName, pType));
					}
				}
			}

			String returnType = type;
			if (name.equals(c.getNom())) returnType = name;

			c.ajouterMethode(visibility, name, returnType, params, isStatic);
		}
		else
		{
			if (name.equals(type)) return;

			name = name.replace(";", "");
			c.ajouterAttribut(name, isFinal, type, visibility, isStatic);
		}
	}


	//---------------------------------------//
	//          methode static               //
	//---------------------------------------//

	/** 
	 * Ajoute tout les fichier java dans l'array list fournit
	 * @param path chemin du repertoire
	 * @param allFiles arrayListe vide qu'on va remplir avec les chemins des .java
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

