import java.util.HashMap;
import java.util.List;

public class Admin extends Utilisateur
{
	private String permissions;
	private HashMap<String,HashMap<Integer,List<String>>> testHashMap;

	public Admin(String nom, String prenom, String dateDeNaissance, String password, String permissions)
	{
		super(nom, prenom, dateDeNaissance, password);
		this.permissions = permissions;
	}

	public void setPermissions(String permissions)
	{
		return;
	}

	public String getPermissions() { return ""; }

	public boolean bannir(Utilisateur membre) { return true; }


}
