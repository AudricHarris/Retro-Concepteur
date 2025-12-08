package metier.classe;

import metier.classe.Methode;


import java.util.ArrayList;

public class Classe 
{
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	public Classe() 
	{
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
	

}

