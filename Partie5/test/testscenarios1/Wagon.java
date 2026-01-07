public class Wagon extends Vehicule
{
	private double capacite;
	
	public Wagon ( int nbRoue , double poid, double capacite)
	{
		super(nbRoue, poid);
		this.capacite = capacite;
	}

	
	public double getPoidEnCharge() {return poid + capacite;}
	
	

	public String toString ()
	{
		return  super.toString() + 
				String.format("%-20s", "Capacité du train " ) 	+ ": " + this.capacite + " tonnes\n" ;
	}

	
}
