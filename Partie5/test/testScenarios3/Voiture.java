package testScenarios1.testOff1;

public class Voiture extends Vehicule
{
	private int nbPassagers;

	public Voiture ( int nbRoue, double poid, int nbPassagers)
	{
		super(nbRoue, poid);
		this.nbPassagers = nbPassagers;
	}

	public double getPoidEnCharge(){return this.poid + this.nbPassagers * ((76 +6 )/ 1000);}
	

	public String toString ()
	{
		return  super.toString() + 
				String.format("%-20s", "Nombre de passager" ) 	+ ": " + this.nbPassagers + " passagers\n";
	}
}
