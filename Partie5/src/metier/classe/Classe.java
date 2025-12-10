package metier.classe;

import java.util.ArrayList;

/*
 * Class classe est une instance qui permet de stocker les differents class de chaque fichier
 * Elle contient un nom, une list d'attribut et liste de methode pour les variables
 * */
public class Classe
{
	private String nom;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	public Classe(String nom)
	{
		this.nom = nom;
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
	}

	// Getter
	public String getNom() { return this.nom; }
	public ArrayList<Attribut> getLstAttribut() { return this.lstAttribut; }
	public ArrayList<Methode> getLstMethode() { return this.lstMethode; }

	// Méthode modifcateur

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


	public int getPlusGrandAttributMethode()
	{
		return Math.max(this.getPlusGrandAttribut(), this.getPlusGrandeMethode());
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
		String sRet = this.nom + "\n";
		for (Attribut attribut : this.lstAttribut)
			sRet += attribut.toString() + "\n";

		for (Methode methode : this.lstMethode)
			sRet+= methode.toString();

		return sRet;
	}
}
