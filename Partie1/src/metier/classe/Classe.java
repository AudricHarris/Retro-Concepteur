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
	public void ajouterMethode(String visibilite, String nomMeth, String type, ArrayList<Parametre> lstParam)
	{
		Methode meth = new Methode(visibilite, nomMeth, type, lstParam);
		if ( meth != null)
			this.lstMethode.add(meth);
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
