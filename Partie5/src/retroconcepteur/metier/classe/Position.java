package retroconcepteur.metier.classe;

/**
 * Class Position pour sauvegarder les positions des rectangles dans le xml
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */

public class Position
{

	private int x;
	private int y;
	private int largeur;
	private int hauteur;

	public Position(int x, int y, int largeur, int hauteur) 
	{
		this.x = x;
		this.y = y;
		this.largeur = largeur;
		this.hauteur = hauteur;
	}

	/*--------------------------------*/
	/*            Accesseurs          */
	/*--------------------------------*/

	public int getX()       { return x;       }
	public int getY()       { return y;       } 
	public int getLargeur() { return largeur; }
	public int getHauteur() { return hauteur; }
}
