package vue;


import java.util.ArrayList;

import metier.classe.Classe;
import metier.classe.Methode;
import metier.classe.Attribut;
import metier.classe.Parametre;

public class AffichageCUI 
{
	
	public String afficherClasse (ArrayList<Classe> lstClasse)
	{	
		final String LIGNE = "------------------------------------------------";
		String sRet="";
		for (Classe classe : lstClasse) 
		{
			sRet += LIGNE + "\n";
			sRet += String.format("%48s", classe.getNom()) + "\n";
			sRet += LIGNE + "\n";

			for ( Attribut att : classe.getLstAttribut() )
			{
				if ( att.constante() )
				{
					sRet += AffichageCUI.getSigneVisibilite(att.visibilite()) + " " + att.nom() + " :" + att.type()+ "\n";
				}
			}
			if ( classe.getNbConstante() > 0 )
			{
				sRet += LIGNE + "\n";
				System.out.println(classe.getNbConstante());
			}	
				

			for ( Attribut att : classe.getLstAttribut() )
			{
				if ( !att.constante() )
				{
					sRet += AffichageCUI.getSigneVisibilite(att.visibilite()) + " " + att.nom() + " :" + att.type() + "\n";
				}
			}
			sRet += LIGNE + "\n";

			for ( Methode meth : classe.getLstMethode() )
			{
				sRet += AffichageCUI.getSigneVisibilite(meth.visibilite()) + " " + meth.methode() + "(";
				for ( Parametre p : meth.lstParam() )
				{
					sRet += p.nom() + ":" + p.type() + ",";
				}
				if ( meth.lstParam().size() > 0 )
					sRet = sRet.substring(0, sRet.length()-1);
				sRet += ")" + "\t" + ":" + meth.type() + "\n";				
			}
			sRet += LIGNE + "\n";
		}
		

		return sRet;
		
	}

	private static char getSigneVisibilite(String visibilite)
	{
		switch (visibilite) 
		{
			case "public": return '+';

			case "private" : return '-';

			case "protected" : return '#'; 
		
			default: return '~';
		}
	}
}	
