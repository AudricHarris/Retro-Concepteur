package RetroConcepteur.vue.outil;

import java.awt.Point;
import java.util.HashMap;
import java.io.Serializable;

public class Arc implements Serializable
{
	private HashMap<String, Point> from;
	private HashMap<String, Point> to;
	private String type;
	private int x1;
	private int x2;
	private int y1;
	private int y2;

	public Arc( String classe1,int x1, int y1, String classe2, int x2, int y2)
	{
		this.from = new HashMap<String, Point>();
		this.to   = new HashMap<String, Point>();
		this.x1   = x1;
		this.y1   = y1;
		this.x2   = x2;
		this.y2   = y2;
		this.from.put(classe1, new Point(x1,y1));
		this.to.put(classe2, new Point(x2,y2));

	}

	public HashMap<String, Point> getFrom()   { return this.from;  }
	public HashMap<String, Point> getTo()     { return this.to;    }
	public int getX1()  					  { return this.x1;    }
	public int getX2()                        { return x2; 	       }
	public int getY1()                        { return y1;	       }
	public int getY2()                        { return y2;         }
	public String getType()                   { return this.type;  }

	public void setX1(int x1)                
	{ 
		this.x1 = x1;    
		this.from.replace((String)this.from.keySet().toArray()[0], new Point(x1,this.y1));  
	}
	public void setY1(int y1)                
	{ 
		this.y1 = y1;    
		this.from.replace((String)this.from.keySet().toArray()[0], new Point(this.x1,y1));  
	}
	public void setX2(int x2)
	{ 
		this.x2 = x2;    
		this.from.replace((String)this.from.keySet().toArray()[1], new Point(x2,this.y2));  
	}
	public void setY2(int y2)
	{ 
		this.y2 = y2;    
		this.from.replace((String)this.from.keySet().toArray()[1], new Point(this.x2,y2));  
	}
	
	public void setPoint(String nomClasse, int x, int y)
    {
        if (this.from.containsKey(nomClasse))
        {
            this.x1 = x;
            this.y1 = y;
            this.from.put(nomClasse, new Point(x, y));
        }
        else if (this.to.containsKey(nomClasse))
        {
            this.x2 = x;
            this.y2 = y;
            this.to.put(nomClasse, new Point(x, y));
        }
    }
	public void setType(String type)
	{ 
		if(type != null) 
			this.type = type; 
	}


}