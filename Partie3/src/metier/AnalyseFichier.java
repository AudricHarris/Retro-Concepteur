package metier;


// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

// Import package itern
import metier.classe.*;

import controller.Controller;

/**
 * - Permet la lecture d'un dossier et la creation de classe.
 *   Une fois crée nous ajoutons les methode et attribut à cette classe
 */
public class AnalyseFichier
{
	private Controller ctrl;
	private ArrayList<Classe> lstClass;
	private int			   niveau;
	private List<Liaison> lstLiaisons;

	/**
	 * Construit l'instance AnalyseFichier et parcours le repo
	 * @param repo chemin du repositoire avec programme java
	 */
	public AnalyseFichier(Controller ctrl, String repo)
	{
		this.ctrl	 = ctrl;
		this.lstClass = new ArrayList<Classe>();
		this.niveau   = 0;

		ArrayList<String> allFiles = new ArrayList<String>();

		this.lstLiaisons = new ArrayList<Liaison>();
		
		try
		{
			File f = new File(repo);
			AnalyseFichier.listeRepertoire(f, allFiles);

			for (String file : allFiles)
			{
				Classe classCourante = new Classe(file.substring(file.lastIndexOf("/") + 1,file.lastIndexOf(".")));
				this.lstClass.add(classCourante);

				LireFichier.lireFichier(file, this);
				this.niveau = 0;
			}
		}
		catch (Exception e) { System.out.println("fichier non trouvé"); }

		for (Classe classe1 : this.lstClass) 
		{
			for (Classe classe2 : this.lstClass) 
			{
				if (classe1 != classe2) 
				{
					List<Liaison> liaisons = Liaison.creerLiaison(classe1, classe2, this);
		
					for (Liaison liaison : liaisons) 
					{
						if (liaison != null) 
							this.lstLiaisons.add(liaison);
					}
				}
			}
			
		}

		System.out.println("Unique");
		System.out.println(this.getListLiaisonUnique());
		System.out.println("Binaire");
		System.out.println(this.getListLiaisonBinaire());
	}

	//---------------------------------------//
	//			  Getters				  //
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
	public int getNiveau	() {return this.niveau;}

	/**
	 * Determine si le string est un Modificateur
	 * @param text fraction d'une ligne
	 * @return boolean si le text contient un Modificateur
	 */
	public boolean getModificateur(String text)
	{
		return (text.equals("public")	|| text.equals("private") || 
				text.equals("protected") || text.equals("static")  || 
				text.equals("final"));
	}

	public List<Liaison> getListLiaison() { return this.lstLiaisons; }

	public List<Liaison> getListLiaisonUnique()
	{
		List<Liaison> lstUnique = new ArrayList<Liaison>();
		
		for (Liaison l : this.lstLiaisons)
			if (!l.estBinaire())
				lstUnique.add(l);

		return lstUnique;
	}
	
	public List<Liaison> getListLiaisonBinaire()
	{
		HashMap<Classe, Liaison> lstBinaire = new HashMap<Classe, Liaison>();
		for (Liaison l : this.lstLiaisons)
			if (l.estBinaire() && !lstBinaire.containsKey(l.getToClass()) && 
				!lstBinaire.containsKey(l.getFromClass())) 
				lstBinaire.put(l.getToClass(), l);

		return new ArrayList<Liaison>(lstBinaire.values());
	}

	//---------------------------------------//
	//		 methode instance			  //
	//---------------------------------------//
	
	/**
	 * Mets à jour le niveau si des accolades sont present
	 * Determine si la ligne pourait posseder des methodes ou attributs
	 * @param ligne une ligne de code
	 */
	public void analyserLigne(String ligne)
	{
		String trimmed = ligne.trim();
		
		// Determiner si commentaire ou si nÃ©cessaire de traiter
		if (ligne.length() < 1) return;
		if (trimmed.isEmpty() || trimmed.startsWith("//")) return;
		
		// Metre Ã  jour le niveau
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
		sc.useDelimiter("\\s+"); // tout espace qui ce rÃ©pÃ¨te une ou plusieur fois

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
		else if (!name.equals("("))
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
	//		  methode static			   //
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

