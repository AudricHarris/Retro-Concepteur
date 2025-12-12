package RetroConcepteur.vue.outil;

public class Rectangle
{
	private int x;
	private int y;
	private int tailleX;
	private int tailleY;

	public Rectangle ( int x, int y, int tailleX, int tailleY )
	{
		this.x = x;
		this.y = y;

		this.tailleX = tailleX;
		this.tailleY = tailleY;
	}

	public int getCentreX()       { return this.x + this.tailleX/2; }
	public int getCentreY()       { return this.y + this.tailleY/2; }
	public int getTailleX()       { return this.tailleX; }
	public int getTailleY()       { return this.tailleY; }
	public int getX      ()       { return this.x;       }
	public int getY      ()       { return this.y;       }

	public void deplacerX (int x) { this.x += x;   }
	public void deplacerY (int y) { this.y += y;   }
	public void setTailleX(int x) { this.tailleX  = x;   }
	public void setTailleY(int y) { this.tailleY  = y;   }

	public void setX( int x ) { this.x = x; }
	public void setY( int y ) { this.y = y; }


	public boolean  possede ( int autreX, int autreY )
	{
		return	autreX >= this.x && autreX <= this.x + this.tailleX &&
				autreY >= this.y && autreY <= this.y + this.tailleY;
	}

	


}