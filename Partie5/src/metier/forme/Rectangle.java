package metier.forme;

public class Rectangle
{
	private int centreX;
	private int centreY;
	private int tailleX;
	private int tailleY;

	public Rectangle ( int centreX, int centreY, int tailleX, int tailleY )
	{
		this.centreX = centreX;
		this.centreY = centreY;

		this.tailleX = tailleX;
		this.tailleY = tailleY;
	}

	public int getCentreX()       { return this.centreX; }
	public int getCentreY()       { return this.centreY; }
	public int getTailleX()       { return this.tailleX; }
	public int getTailleY()       { return this.tailleY; }

	public void deplacerX (int x) { this.centreX += x;   }
	public void deplacerY (int y) { this.centreY += y;   }
	public void setTailleX(int x) { this.tailleX  = x;   }
	public void setTailleY(int y) { this.tailleY  = y;   }

	public void setCentreX( int centreX ) { this.centreX = centreX; }
	public void setCentreY( int centreY ) { this.centreY = centreY; }


	public boolean  possede ( int x, int y )
	{
		int demiLargeur = this.tailleX/2;
		int demiHauteur = this.tailleY/2;

		return x >= this.centreX-demiLargeur && x <= this.centreX+demiLargeur &&
			   y >= this.centreY-demiHauteur && y <= this.centreY+demiHauteur;
	}	
}