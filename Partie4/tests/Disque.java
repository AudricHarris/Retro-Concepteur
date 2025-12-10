public class Disque 
{

    
    private double rayon;

    
    private Point centre;
    
    public Disque(Point centre, double rayon) 
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
}