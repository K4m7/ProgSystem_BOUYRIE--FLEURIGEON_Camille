public class TestCompteur {

    public static void main(String[] args) {

        final int NB_TESTS = 50;
        final int NB_THREADS = 9; //initial 9
        final int NB_INCREMENTS = 100; //initial 1111111
        final int ATTENDU = NB_THREADS * NB_INCREMENTS; // 9999999
		int nbCorrects = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

        // On répète le test 50 fois
        for (int test = 1; test <= NB_TESTS; test++) {

            // État propre à chaque test
            CompteurDangereux.resetCompteur();

            // Création des 9 threads
            Thread[] threads = new Thread[NB_THREADS];
            for (int noT = 0; noT < NB_THREADS; noT++) {
                threads[noT] = new Thread(
                    new CompteurDangereux.Incrementeur("T" + noT, NB_INCREMENTS));
            }

            // Démarrage de tous les threads
            for (int noT = 0; noT < NB_THREADS; noT++) {
                threads[noT].start();
            }

            // Attente de la fin de tous les threads
            for (int noT = 0; noT < NB_THREADS; noT++) {
                try {
                    threads[noT].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Affichage et comparaison avec la valeur attendue
            int resultat = CompteurDangereux.getCompteur();
            System.out.println("Test " + test + " : obtenu = " + resultat);
            System.out.println("Attendu = " + ATTENDU 
							   + (resultat == ATTENDU ? " OK" : " ERREUR (perdu : " 
							   + (ATTENDU - resultat) + ")"));
			if (resultat == ATTENDU) {
				nbCorrects++;
			}
			if (resultat < min) {
				min = resultat;
			}
			if (resultat > max) {
				max = resultat;
			}
        }
		double pourcentage = 100.0 * nbCorrects / NB_TESTS;
		System.out.println("\n----- BILAN -----");
		
		System.out.println("Threads            : " + NB_THREADS);
		System.out.println("Incréments/thread  : " + NB_INCREMENTS);
		System.out.println("Résultats corrects : " + nbCorrects + "/" + NB_TESTS
						   + " (" + pourcentage + " %)");
		System.out.println("Minimum obtenu     : " + min);
		System.out.println("Maximum obtenu     : " + max);
		System.out.println("Attendu            : " + ATTENDU);
    }
}