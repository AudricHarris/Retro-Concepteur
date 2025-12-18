package RetroConcepteur.vue.outil;

/**
 * Classe responsable des points
 *
 * @author [Equipe 9]
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

	//---------------------------------------//
	//               Getters                 //
	//---------------------------------------//
	
	public int getX() { return x; }
	public int getY() { return y; }

	//---------------------------------------//
	//               Setters                 //
	//---------------------------------------//
	
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }

}
