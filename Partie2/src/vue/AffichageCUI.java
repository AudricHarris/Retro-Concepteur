package vue;


import java.util.ArrayList;

import metier.classe.Classe;
import metier.classe.Methode;
import metier.classe.Attribut;
import metier.classe.Parametre;

public class AffichageCUI 
{
	public final static String LIGNE = "------------------------------------------------";

	public String afficherClasse (ArrayList<Classe> lstClasse)
	{	
		String sRet="";
		String partTitre;
		String partConstante;
		String partAttribut;
		String partMethode;
		int tailleAttributMax;
		int indiceStatique ;
		

		for (Classe classe : lstClasse) 
		{
			String ligneAttribut = "";
			 

			partTitre="";
			partAttribut="";
			partMethode="";


			tailleAttributMax = classe.getPlusGrandAttribut() +1;

			sRet += AffichageCUI.LIGNE + "\n";

			int decalage = 24 - classe.getNom().length()/2;

			partTitre += String.format("%" + decalage + "s", " ") + classe.getNom() + "\n";
			partTitre += AffichageCUI.LIGNE + "\n";

			for ( Attribut att : classe.getLstAttribut() )
			{
				indiceStatique = 0;

				if (att.isStatic())
					indiceStatique = att.nom().length();
				
				ligneAttribut += AffichageCUI.getSigneVisibilite(att.visibilite()) + " " ;
				ligneAttribut += String.format("%-"+tailleAttributMax +"s" , " " + att.nom()) + " : " + att.type();
				
				if ( att.constante() )
					ligneAttribut += "<<freeze>>";
				ligneAttribut += "\n";

				if (indiceStatique > 0)
					ligneAttribut += "   " + "\u203E".repeat(indiceStatique) + "\n";
	
			}
			partAttribut += ligneAttribut + "\n";
			partAttribut += AffichageCUI.LIGNE + "\n";

			for ( Methode meth : classe.getLstMethode() )
			{
				indiceStatique = 0;

				if (meth.isStatic())
					indiceStatique = meth.methode().length();

				if ( ! meth.methode().equals("main") )
				{
					partMethode += AffichageCUI.getSigneVisibilite(meth.visibilite()) + " " + meth.methode() + "(";
					for ( Parametre p : meth.lstParam() )
						partMethode += p.nom() + ":" + p.type() + ",";

					if ( meth.lstParam().size() > 0 )
						partMethode = partMethode.substring(0, partMethode.length()-1);

					partMethode += ")" + "\t";

					if (!meth.type().equals("void") && !meth.methode().equals(classe.getNom())) 
					{
						partMethode +=  String.format("%15s", ": " + meth.type());
					}

					partMethode += "\n";
					
				}

				if (indiceStatique > 0)
					partMethode += "   " + "\u203E".repeat(indiceStatique) + "\n";
			}
			sRet += partTitre + partAttribut + partMethode;
			sRet += AffichageCUI.LIGNE + "\n\n\n";
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
