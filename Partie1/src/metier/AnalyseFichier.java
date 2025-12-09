package metier;

// Import package extern
import java.io.File;
import java.util.ArrayList;
import java.util.List;
// Import package itern
import metier.classe.*;

// TODO : Remplacer Le nom de determinerPropriete pour un nom plus representatif

/*
 * Le corps du metier : 
 * - Permet la lecture d'un dossier et la creation de classe
 * Elle comporte une methode traitement de ligne et determinerPropriete
 * qui créer les methodes et attribut d'une classe
 */
public class AnalyseFichier
{
    private ArrayList<Classe> lstClass;
    private Classe classCourante;
    private int niveau;

	// Instancier les variables + lire le repo et tout les fichier du repo en .java
	// TODO : Remplacer classCourante par this.lstClass.getLast()
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
                System.out.println(file);
                LireFichier.lireFichier(file, this);
                this.classCourante = new Classe(file);
				this.lstClass.add(this.classCourante);
                this.niveau = 0;
            }
        }
        catch (Exception e)
        {
            System.out.println("fichier non trouvé");
        }

		for (Classe c : this.lstClass)
		{
			System.out.println(c);
		}
    }

	// methode permettant la lecture d'un repertoire pour avoir tout ces enfants du repertoire
	// TODO: Re-organiser les static pour qu'il soit dans le bon ordre de lecture en java
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
                    if (lastDotIndex > 0 && currentFilePath.substring(lastDotIndex + 1).toLowerCase().equals("java"))
                        allFiles.add(currentFilePath);
                }
            }
        }
        else
        {
            System.err.println(path + " : Erreur de lecture.");
        }
    }

	// Analyse la ligne et mets a jour le positionement du niveau
    public void analyserLigne(String ligne)
    {
        if (ligne.length() < 1) return;
        String trimmed = ligne.trim();
        if (trimmed.isEmpty()) return;
        if (trimmed.startsWith("//")) return;
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
                    if (words.length > 0)
                    {
                        String nom = words[0];
                        this.classCourante = new Classe(nom);
                    }
                }
            }
        }
        if (this.niveau == 1) this.determinerPropriete(trimmed);
    }

	// traite la ligne et determine si çela est une methode ou non
    public void determinerPropriete(String ligne)
    {
        if (this.classCourante == null) return;
        String[] parts = ligne.split("\\s+");
        int i = 0;
        String visibilite = "";
        boolean isStatic = false;
        boolean constante = false;
        while (i < parts.length && (parts[i].equals("public") || parts[i].equals("private") || parts[i].equals("protected") || parts[i].equals("static") || parts[i].equals("final")))
        {
            if (parts[i].equals("public") || parts[i].equals("private") || parts[i].equals("protected"))
            {
                if (visibilite.isEmpty()) visibilite = parts[i];
            }
            else if (parts[i].equals("static")) isStatic = true;
            else if (parts[i].equals("final")) constante = true;
            i++;
        }
        if (i >= parts.length) return;
        String type = parts[i++];
        if (i >= parts.length) return;
        String nom = parts[i++];
        if (ligne.contains("("))
        {
            int start = ligne.indexOf('(') + 1;
            int end = ligne.indexOf(')');
            String paramsStr = ligne.substring(start, end);
            ArrayList<Parametre> lstParam = new ArrayList<Parametre>();
            if (!paramsStr.trim().isEmpty())
            {
                String[] paramParts = paramsStr.split(",");
                for (String pp : paramParts)
                {
                    pp = pp.trim();
                    if (pp.isEmpty()) continue;
                    String[] tp = pp.split("\\s+");
                    if (tp.length >= 2)
                    {
                        String ptype = tp[0];
                        String pnom = tp[1];
                        lstParam.add(new Parametre(pnom, ptype));
                    }
                }
            }
            this.classCourante.ajouterMethode(visibilite, nom, type, lstParam);
        }
        else
        {
            this.classCourante.ajouterAttribut(nom, constante, type, visibilite, isStatic);
        }
    }

	//TODO: deplacer les getters et faire des getters pour tout les variables (ça nous aidera si besoin)
    public ArrayList<Classe> getLstClasses()
    {
        return new ArrayList<Classe>(this.lstClass);
    }
	
	public Classe getClassCourante()
	{
		return this.classCourante;
	}

    public int getNiveau()
	{
		return this.niveau;
	}
}
