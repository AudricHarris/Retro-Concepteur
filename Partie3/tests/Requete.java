package visite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Requete {
    private String date;
    private String navigateur;
    private String ipVisiteur;
    private String verbeHttp;
    private String urlSite;
    private String ipServeur;
    private String codeHttp;
    private String referer;

    // Question 1 : Constructeur
    public Requete(String date, String navigateur, String ipVisiteur, String verbeHttp,
                   String urlSite, String ipServeur, String codeHttp, String referer) {
        this.date = date;
        this.navigateur = navigateur;
        this.ipVisiteur = ipVisiteur;
        this.verbeHttp = verbeHttp;
        this.urlSite = urlSite;
        this.ipServeur = ipServeur;
        this.codeHttp = codeHttp;
        this.referer = referer;
    }

    // Question 8 : Méthode getMois
    public int getMois() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm", Locale.FRANCE);
        LocalDateTime dateTime = LocalDateTime.parse(this.date, formatter);
        return dateTime.getMonthValue();
    }

    // Question 1 : Méthodes d'accès (Getters)
    public String getIpVisiteur() { return ipVisiteur; }
    public String getUrlSite() { return urlSite; }
    // Autres getters omis pour la concision...

    @Override
    public String toString() {
        return "Requete{" +
                "date='" + date + '\'' +
                ", navigateur='" + navigateur + '\'' +
                ", ipVisiteur='" + ipVisiteur + '\'' +
                ", urlSite='" + urlSite + '\'' +
                ", codeHttp='" + codeHttp + '\'' +
                '}';
    }
}