public abstract class Vehicule 
{
	private int 	nbRoue;
	protected double 	poid;
	
	public Vehicule (int nbRoue, double poid)
	{
		this.nbRoue = nbRoue;
		this.poid = poid;
	}

	public abstract double getPoidEnCharge ();


	public double getPoidMaxParRoue()
	{
		return this.getPoidEnCharge()/this.nbRoue;
	}

	public String toString()
	{
		String separateur = "----------------------------------------------\n" +
							"**********************************************\n" +
							"----------------------------------------------";
		
		
		
		return	separateur + "\n" +
				String.format("%-20s", "Vehicule " ) 			+ ": " + this.getClass().getName().substring(15) + "\n" +
				String.format("%-20s", "Nombre de roues " ) 		+ ": " + this.nbRoue + " roues" +"\n" +
				String.format("%-20s", "Poid " ) 				+ ": " + this.poid   + " tonnes" +"\n" +
				String.format("%-20s", "Poid a vide " ) 	+ ": " + this.getPoidEnCharge() + " tonnes" + "\n" +
				String.format("%-20s", "Poid max par roues ") 	+ ": " + String.format("%.2f",this.getPoidMaxParRoue()) +  " tonnes" + "\n";

	}

	public static void main(String[] args) 
	{
		Motrice m =  new Motrice(8, 26, 485);
		Vehicule v = new Voiture(14 , 32, 98) ;
		Wagon w =  new Wagon(18, 48, 59);

		m.toString();
		v.toString();
		w.toString();


	}
	




}

