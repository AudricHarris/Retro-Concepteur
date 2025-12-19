package retroconcepteur.metier.classe;

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

    public int getX()       { return x;       }
    public int getY()       { return y;       } 
    public int getLargeur() { return largeur; }
    public int getHauteur() { return hauteur; }
}
