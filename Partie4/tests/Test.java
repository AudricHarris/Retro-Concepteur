// Cas 1 : ligne entièrement commentaire simple
// Cette ligne ne devrait jamais être analysée

public class Test 
{

    // Cas 2 : commentaire de ligne après du code
    int a = 1; // commentaire après du code (ta méthode envoie tout, y compris le commentaire)

    // Cas 3 : début de bloc de commentaire au milieu de ligne
    int b = 2; /* début de bloc
                  suite du commentaire (ta méthode analyse ces lignes comme du code)
                  encore du commentaire
                */

    // Cas 4 : fin de bloc sur une ligne contenant aussi du code
    /* début bloc multi-ligne
       encore dans le bloc
    */ 
   int c = 3; // ta méthode ignore cette ligne complète (code perdu)

    // Cas 5 : bloc multi-ligne avec étoiles
    /*
     * ligne 1 dans bloc
     * ligne 2 dans bloc
     */
    int d = 4; // ici ça va, mais les lignes avec * ne devraient pas être analysées

    // Cas 6 : ligne avec seulement "*/" mais précédée d'espaces
    /*
       commentaire
        */
    int e = 5; // vérifier que seul ce code est pris, pas la ligne avec "*/"

    // Cas 7 : mélange bizarre de séquences
    int f = 6; /* bloc */ 
	int g = 7; // ta méthode voit tout comme une seule ligne de code + commentaire
                                     // mais elle ne sait pas vraiment enlever le bloc ni le //

    // Cas 8 : commentaire qui ressemble à du code
    // int h = 8;  // ne doit pas être analysé

    /* Cas 9 : bloc qui contient ce qui ressemble à du code
       int i = 9;
       public void fake() {}
       fin du bloc
    */

    public void vrai(
		String test
	) 
    {
        int j = 10; /* commentaire dans une méthode
                       qui continue sur plusieurs lignes
                     */ int k = 11; // code avant et après bloc + commentaire
    }
}