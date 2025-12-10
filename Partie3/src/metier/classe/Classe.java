package metier.classe;

import java.util.ArrayList;

/**
 * Class classe est une instance qui permet de stocker les differents class de chaque fichier
 * Elle contient un nom, une list d'attribut et liste de methode pour les variables
 * */
public class Classe
{
	private String nom;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	/**
	 * Constructeur de classe
	 * @param nom nom de la classe
	 * */
	public Classe(String nom)
	{
		this.nom = nom;
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
	}


	//---------------------------------------//
	//              Getters                  //
	//---------------------------------------//

	public String              getNom        () { return this.nom;         }
	public ArrayList<Attribut> getLstAttribut() { return this.lstAttribut; }
	public ArrayList<Methode>  getLstMethode () { return this.lstMethode;  }

	/**
	 * Retourne la taille du plus grand attribut
	 * @return grand taille du plus grand
	 * */
	public int getPlusGrandAttribut()
	{
		int grand = 0;
		
		for (Attribut att : this.lstAttribut) 
			if(att.getNom().length() > grand) grand = att.getNom().length();
		
		return grand;
	}

	/**
	 * Retourne la taille de la plus grand methode et param
	 * @return grand taille du plus grand methode / param
	 * */
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
					tailleActuelle += p.getNom().length() + 1 + p.getType().length() + 1;

				tailleActuelle--;
			}
			
			if(tailleActuelle > grand) grand = tailleActuelle;
		}

		return grand;
	}


	/**
	 * Retourne le nombre de constante de la classe
	 * @return nombre de constante
	 * */
	public int getNbConstante()
	{
		int cpt=0;
		for ( Attribut att : this.lstAttribut ) if ( att.isConstante() ) cpt++;

		return cpt;
	}
	//---------------------------------------//
	//            Modificateur               //
	//---------------------------------------//

	/**
	 * Cette methode permet l'ajout de tache en fonction des paramètres données
	 * @param nomAtt nom de l'attribut
	 * @param constante si l'attribut est constante
	 * @param type le type de l'attribut
	 * @param visibilite Visibilité de l'attribut
	 * @param isStatic si l'attribut est static
	 * */
	public void ajouterAttribut(String nomAtt, boolean constante, String type, String visibilite, boolean isStatic)
	{
		int num = this.lstAttribut.size() + 1;
		Attribut attribut = new Attribut(num, nomAtt, constante, type, visibilite, isStatic);
		this.lstAttribut.add(attribut);
	}

	/**
	 * Cette methode permet l'ajout de tache en fonction des paramètres données
	 * @param visibilite Visibilité de la methode
	 * @param nomMeth nom de la methode
	 * @param type le type renvoyer de la methode
	 * @param lstParam lst des param de la methode
	 * @param isStatic si la methode est static
	 * */
	public void ajouterMethode(String visibilite, String nomMeth, String type, ArrayList<Parametre> lstParam, boolean isStatic)
	{
		Methode meth = new Methode(visibilite, nomMeth, type, lstParam, isStatic);
		this.lstMethode.add(meth);
	}

	//---------------------------------------//
	//            Methode instance           //
	//---------------------------------------//
	
	public String toString()
	{
		String sRet = this.nom + "\n";

		for (Attribut attribut : this.lstAttribut) sRet += attribut.toString() + "\n";
		for (Methode  methode  : this.lstMethode ) sRet += methode.toString();

		return sRet;
	}
}
