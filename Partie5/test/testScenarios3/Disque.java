package testScenarios1.testOff1;

import java.util.List;

public class Disque implements Comparable<Disque>
{
    private boolean estAbstract;
    private boolean estBooleen;

    private String  estString;
    private int     estEntier;
	
	private double  rayon;

	private Point           centre;
	private List<Point> 	lstPoint;
	
	public Disque(Point centre,  double rayon) 
	{
		this.centre = centre;
		this.rayon = rayon;
	}

	
	public double calculerAire() 
	{
		return Math.PI * rayon * rayon;
	}

	
	public double calculerPerimetre() 
	{
		return 2 * Math.PI * rayon;
	}

	
	public void setX(int x) 
	{
		if (centre != null) 
		{
			centre.setX(x);
		}
	}

	
	public void setY(int y) 
	{
		if (centre != null) 
		{
			centre.setY(y);
		}
	}


	public int compareTo(Disque o) 
	{
		return Double.compare(this.rayon, o.rayon);
	}
}
