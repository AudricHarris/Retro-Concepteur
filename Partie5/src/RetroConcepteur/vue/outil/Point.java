package RetroConcepteur.vue.outil;

/**
 * Classe responsable des points
 *
 * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost]
 * @version 1.0
 */
public class Point
{	
	private int x;
	private int y;

	/**
	 *	Creer une instance de Point
	 *	@param x cord X
	 *	@param y cord Y
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/*---------------------------------------*/
	/*               Modificateurs           */
	/*---------------------------------------*/
	
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }

	/*---------------------------------------*/
	/*               Accesseurs              */
	/*---------------------------------------*/
	
	public int getX() { return x; }
	public int getY() { return y; }

	

}
