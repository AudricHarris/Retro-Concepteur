package metier.classe;

import java.util.ArrayList;

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

	public void ajouterAttribut(Attribut attribut)
	{
		if (attribut!= null) 
			this.lstAttribut.add(attribut);		
		
	}

	public void ajouterMethode(Methode meth)
	{
		if ( meth != null)
			this.lstMethode.add(meth);
	}

	public String toString()
	{
		String sRet = "";

		for (Attribut attribut : this.lstAttribut) 
			sRet += attribut.toString() + "\n";
		
		for (Methode methode : this.lstMethode) 
			sRet+= methode.toString();

		return sRet;
		
	}
	

}

