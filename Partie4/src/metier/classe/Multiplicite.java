package metier.classe;

public class Multiplicite 
{
	private String borneInf;
	private String borneSup;

	public Multiplicite(String borneInf, String borneSup)
	{
		this.borneInf = borneInf;
		this.borneSup = borneSup;
	}

	public String getBorneInf() { return this.borneInf; }
	public String getBorneSup() { return this.borneSup; }

	public void setBorneInf(String borneInf) { this.borneInf = borneInf; }
	public void setBorneSup(String borneSup) { this.borneSup = borneSup; }
	public String toString()
	{
		return "[" + this.borneInf + ".." + this.borneSup + "]";
	}
}
