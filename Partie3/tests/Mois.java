package visite;

public class Mois implements Comparable<Mois> {
    private int numeroMois;
    private int nbSite;

    // Question 10 : Constructeur
    public Mois(int numeroMois, int nbSite) {
        this.numeroMois = numeroMois;
        this.nbSite = nbSite;
    }

    // Getters
    public int getNumeroMois() { return numeroMois; }
    public int getNbSite() { return nbSite; }

    // Question 11 : Implémentation de Comparable pour ordonner par nbSite (croissant)
    @Override
    public int compareTo(Mois autreMois) {
        return Integer.compare(this.nbSite, autreMois.nbSite);
    }
}