
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner; // Utilisation exclusive du Scanner
import java.util.Set;

public class AnalyseRequete {
    // Etape 2 : Attributs
    public ArrayList<Requete> listeRequete;
    public HashSet<String> listeSite; // Question 3
    private HashMap<String, ArrayList<Requete>> hashRequete; // Question 4

    // Etape 2, Question 2 : Constructeur AnalyseRequete(String fichier) - Utilisation exclusive de Scanner
    public AnalyseRequete(String fichier) {
        this.listeRequete = new ArrayList<>();
        this.listeSite = new HashSet<>();
        this.hashRequete = new HashMap<>();

        // Lecture du fichier champ par champ avec Scanner.useDelimiter()
        try (Scanner scanner = new Scanner(new File(fichier))) {
            
            // Le délimiteur est le point-virgule (;) ou la fin de ligne (\r\n ou \n).
            // Le scanner lira ainsi les 8 champs consécutivement, peu importe si le séparateur est un ; ou un saut de ligne.
            scanner.useDelimiter(";|\\r?\\n");
            
            while (scanner.hasNext()) {
                try {
                    // Lecture des 8 champs consécutifs
                    String date = scanner.next().trim();
                    String navigateur = scanner.next().trim();
                    String ipVisiteur = scanner.next().trim();
                    String verbeHttp = scanner.next().trim();
                    String urlSite = scanner.next().trim();
                    String ipServeur = scanner.next().trim();
                    String codeHttp = scanner.next().trim();
                    String referer = scanner.next().trim(); // 8ème champ
                    
                    // Création et ajout de la Requête
                    Requete req = new Requete(date, navigateur, ipVisiteur, verbeHttp,
                                              urlSite, ipServeur, codeHttp, referer);
                    
                    // Question 2 : Ajout à la liste
                    this.listeRequete.add(req);
                    
                    // Question 3 : Ajout à l'ensemble des sites
                    this.listeSite.add(req.getUrlSite());
                    
                } catch (java.util.NoSuchElementException e) {
                    // Attrape l'exception si le scanner arrive à la fin du fichier sans trouver les 8 jetons.
                    System.err.println("Avertissement : Fin de fichier ou ligne incomplète détectée. Arrêt de la lecture des requêtes.");
                    break; 
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : Fichier non trouvé : " + fichier);
        }
        
        // Question 4 : Compléter la HashMap
        groupeRequete();
    }

    // Etape 2, Question 4 : Méthode groupeRequete
    public void groupeRequete() {
        for (Requete req : listeRequete) {
            String site = req.getUrlSite();
            // Ajout ou mise à jour de la liste de requêtes pour le site
            this.hashRequete.computeIfAbsent(site, k -> new ArrayList<>()).add(req);
        }
    }

    // Etape 2, Question 5 : Méthode afficheRequete
    public void afficheRequete(String siteWeb) {
        ArrayList<Requete> requetes = this.hashRequete.get(siteWeb);
        if (requetes != null) {
            System.out.println("--- Requêtes pour le site : " + siteWeb + " ---");
            for (Requete req : requetes) {
                System.out.println(req);
            }
        } else {
            System.out.println("Aucune requête trouvée pour le site : " + siteWeb);
        }
    }
    
    // Etape 3, Question 6 : Fonction nbRequeteSiteParIp
    public long nbRequeteSiteParIp(String ipVisiteur, String nomSiteWeb) {
        ArrayList<Requete> requetesPourSite = this.hashRequete.get(nomSiteWeb);
        
        if (requetesPourSite == null) {
            return 0;
        }
        
        return requetesPourSite.stream()
               .filter(req -> req.getIpVisiteur().equals(ipVisiteur))
               .count();
    }
    
    // Etape 4, Question 9 : Méthode listeSiteParMois
    public HashMap<Integer, HashSet<String>> listeSiteParMois() {
        HashMap<Integer, HashSet<String>> sitesParMois = new HashMap<>();
        
        for (Requete req : this.listeRequete) {
            int mois = req.getMois();
            String site = req.getUrlSite();
            sitesParMois.computeIfAbsent(mois, k -> new HashSet<>()).add(site);
        }
        return sitesParMois;
    }
    
    // Etape 4, Question 12 : Fonction nombreSiteParMois
    public ArrayList<Mois> nombreSiteParMois() {
        HashMap<Integer, HashSet<String>> sitesParMois = listeSiteParMois();
        ArrayList<Mois> listeMois = new ArrayList<>();
        
        for (Map.Entry<Integer, HashSet<String>> entry : sitesParMois.entrySet()) {
            listeMois.add(new Mois(entry.getKey(), entry.getValue().size()));
        }
        
        return listeMois;
    }

    // Etape 4, Question 13 : Fonction moisMaxSites
    public int moisMaxSites() {
        ArrayList<Mois> listeMois = nombreSiteParMois();
        
        if (listeMois.isEmpty()) {
            return -1;
        }
        
        Mois moisMax = Collections.max(listeMois);
        return moisMax.getNumeroMois();
    }
    
    // Etape 5, Question 14 : Fonction afficheListeIPVisiteur
    public void afficheListeIPVisiteur() {
        // Logique inchangée...
        String siteCible = "youtube.com";
        String ipCible = "134.23.10.44";
        int seuilVisite = 4;
        
        int indexQuatriemeVisite = -1;
        int compteurVisiteCible = 0;
        
        for (int i = 0; i < this.listeRequete.size(); i++) {
            Requete req = this.listeRequete.get(i);
            if (req.getUrlSite().equals(siteCible) && req.getIpVisiteur().equals(ipCible)) {
                compteurVisiteCible++;
                if (compteurVisiteCible == seuilVisite) {
                    indexQuatriemeVisite = i;
                    break;
                }
            }
        }

        if (indexQuatriemeVisite == -1) {
            System.out.println("L'IP " + ipCible + " n'a pas visité " + siteCible + " " + seuilVisite + " fois.");
            return;
        }

        List<String> ipsTrieesParDate = new ArrayList<>(); 
        Set<String> ipsUniques = new HashSet<>();
        
        for (int i = indexQuatriemeVisite - 1; i >= 0; i--) {
            Requete req = this.listeRequete.get(i);
            String currentIp = req.getIpVisiteur();
            
            if (req.getUrlSite().equals(siteCible) && !currentIp.equals(ipCible)) {
                if (ipsUniques.add(currentIp)) {
                    ipsTrieesParDate.add(currentIp); 
                }
            }
        }
        
        System.out.println("--- IPs visiteurs uniques pour " + siteCible + " avant la 4ème visite de " + ipCible + " (du plus récent au plus ancien) ---");
        for (String ip : ipsTrieesParDate) {
            System.out.println(ip);
        }
    }
    
    // Méthode main pour l'exécution en ligne de commande (inchangée)
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java visite.AnalyseRequete <nom_du_fichier_csv>");
            System.out.println("Ex: java visite.AnalyseRequete data.csv");
            return;
        }
        
        String fichier = args[0];
        AnalyseRequete analyse = new AnalyseRequete(fichier);
        
        // --- Résultats des analyses finales ---
        
        int moisMax = analyse.moisMaxSites();
        System.out.println("\n=======================================================");
        System.out.println("Question 13: Mois avec le plus grand nombre de sites uniques visités.");
        System.out.println("Numéro du mois : " + moisMax);
        System.out.println("=======================================================");
        
        System.out.println("\n=======================================================");
        System.out.println("Question 14: Liste des IPs visiteurs pour youtube.com avant la 4ème visite de 134.23.10.44.");
        analyse.afficheListeIPVisiteur();
        System.out.println("=======================================================");
    }
}
