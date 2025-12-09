package metier.classe;

import java.util.ArrayList;

/*
 * Record methode stock le nom, visibilite, type, lstParam d'une classe
 * Elle aura aussi un toString custom
 */
public record Methode(String visibilite,String methode, String type, ArrayList<Parametre> lstParam)
{
    @Override
    public String toString()
    {
        String sRet ="";
        sRet += "methode : " + String.format("%20s",this.methode) + "	visibilité: " ;
        sRet += String.format("%10s", this.visibilite ) + " type de retour: ";
        sRet += String.format("%10s", this.type) + "\nparamètres : \n";

		int cpt=0;
        if ( this.lstParam.isEmpty() )
            sRet += "aucun\n";
        else
            for ( Parametre param : this.lstParam )
                sRet += "	p" + (++cpt) + ":" + String.format("%10s", param.nom()) + " type:" + param.type() + "\n ";
        
		return sRet;
    }
}
