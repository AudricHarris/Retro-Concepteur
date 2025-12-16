import java.util.ArrayList;

public class Point extends Mois
{

	
	private int x;
	private int y;

	private Disque d;

	
	public Point(String nom, int x, int y) 
	{
		
		this.x = x;
		this.y = y;
	}

	
	public int getX() 
	{
		return x;
	}

	
	public int getY() 
	{
		return y;
	}
	

	public void setX(int x) 
	{
		this.x = x;
	}

	
	public void setY(int y) 
	{
		this.y = y;
	}
}
