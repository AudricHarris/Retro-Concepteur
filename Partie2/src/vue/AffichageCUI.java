package vue;

import java.util.ArrayList;

import metier.classe.Classe;

public class AffichageCUI 
{
	
	public void afficherClasse (ArrayList<Classe> lstClasse)
	{
		for (Classe classe : lstClasse) 
		{
			System.out.println("------------------------------------------------");
			System.out.println(String.format("%48s", classe.getNom()));
			System.out.println("------------------------------------------------");
			if ( )
		}
		
	}
}	
