package metier.classe;

import java.util.ArrayList;
import java.util.List;

/*
 * Class classe est une instance qui permet de stocker les differents class de chaque fichier
 * Elle contient un nom, une list d'attribut et liste de methode pour les variables
 * */
public class Classe
{
	private String nom;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;
	
	private Classe heritageClasse;
	private List<String> lstInterfaces;

	private boolean isAbstract;
	private boolean isInterface;

	public Classe(String nom)
	{
		this.nom = nom;
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		this.isAbstract = false;
		this.isInterface = false;
	}

	// Getter
	public String getNom()                      { return this.nom; }
	public boolean getIsAbstract()                 { return this.isAbstract; }
	public boolean getIsInterface()                { return this.isInterface; }
	public Classe getHeritageClasse()           { return  this.heritageClasse; }
	public ArrayList<Attribut> getLstAttribut() { return new ArrayList<Attribut>(this.lstAttribut); }
	public ArrayList<Methode> getLstMethode()   { return new ArrayList<Methode> (this.lstMethode); }
	public ArrayList<String> getLstInterfaces() { return new ArrayList<String>  (this.lstInterfaces); }

	// Méthode modifcateur

	public void setIsAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
	public void setIsInterface(boolean isInterface) { this.isInterface = isInterface; }
	public void setHeritageClasse(Classe cls) { this.heritageClasse = cls; }
	
	public void ajouterInterface(String nomInterface)
	{
		if (this.lstInterfaces == null)
			this.lstInterfaces = new ArrayList<String>();

		this.lstInterfaces.add(nomInterface);
	}
	
	// Cette methode permet l'ajout de tache en fonction des paramètres données
	public void ajouterAttribut(String nomAtt, boolean constante, String type, String visibilite, boolean isStatic)
	{
		int num = this.lstAttribut.size() + 1;
		Attribut attribut = new Attribut(num, nomAtt, constante, type, visibilite, isStatic);
		if (attribut != null)
			this.lstAttribut.add(attribut);
	}

	// Cette methode permet L'ajout de méthodes pour une classe
	public void ajouterMethode(String visibilite, String nomMeth, String type, ArrayList<Parametre> lstParam, boolean isStatic)
	{
		Methode meth = new Methode(visibilite, nomMeth, type, lstParam, isStatic);
		if ( meth != null)
			this.lstMethode.add(meth);
	}


	public int getPlusGrandAttribut()
	{
		int grand = 0;
		
		for (Attribut att : this.lstAttribut) 
			if(att.getNom().length() > grand)
				grand = att.getNom().length();
		

		return grand;
	}

	public int getPlusGrandeMethode()
	{
		int grand = 0;
		
		for (Methode meth : this.lstMethode) 
		{
			if (meth.getNom().equals("main")) continue;

			int tailleActuelle = meth.getNom().length() + 2;

			if (!meth.getLstParam().isEmpty())
			{
				for (Parametre p : meth.getLstParam())
				{
					tailleActuelle += p.getNom().length() + 1 + p.getType().length() + 1;
				}
				tailleActuelle--;
			}

			if(tailleActuelle > grand)
				grand = tailleActuelle;
		}

		return grand;
	}


	public int getNbConstante()
	{
		int cpt=0;
		for ( Attribut att : this.lstAttribut )
		{
			if ( att.isConstante() ) cpt++;
		}
		return cpt;
	}


	public String toString()
	{
		String sRet = this.nom + (isAbstract ? " { abstract }" + "\n" : "\n");
		for (Attribut attribut : this.lstAttribut)
			sRet += attribut.toString() + "\n";

		for (Methode methode : this.lstMethode)
			sRet+= methode.toString();

		if (this.heritageClasse != null) 
			sRet += " extends " + this.heritageClasse + "\n";
		if (this.lstInterfaces != null && !this.lstInterfaces.isEmpty()) 
		{
			sRet += "\nimplements :";
			for (String inter : this.lstInterfaces) 
				sRet += "\n\t" + inter + "\n";
		}

		return sRet;
	}
}
