
public class Motrice extends Vehicule
{
	private double puissance;

	public Motrice (int nbRoue, double poid, double puissance )
	{
		super(nbRoue, poid);

		this.puissance = puissance;

	}

	public double getPoidEnCharge ()
	{
		return this.poid;
	}

	public String toString ()
	{
		return  super.toString() + 
				String.format("%-20s", "Puissance du train " ) 	+ ": " + this.puissance + " MW\n" ;
	}



}
