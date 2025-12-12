package RetroConcepteur.vue.outil;

import java.awt.Point;

public class Arc
{
	private Point p1;
	private Point p2;
	private String type;
	private String primeFleche;
	private String secondFleche;



	public Arc( int x1, int y1, int x2, int y2)
	{
		this.p1 = new Point(x1,y1);
		this.p1 = new Point(x2,y2);
		this.type = "";
	}

	public Point getP1()   { return this.p1  ;}
	public Point getP2()   { return this.p2;  }
	public String getType(){ return this.type;}


	public void setType(String type)
	{ 
		if(type != null) 
			this.type = type; 
	}

	public void dessinerArc()
	{

	}

	public void tracerLigneContinue()
	{
		
	}


}