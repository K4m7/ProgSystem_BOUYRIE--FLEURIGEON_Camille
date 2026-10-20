public class CompteurSecurise {
    private static int compteurGlobal = 0;  // Variable partagée MODIFIABLE
	
	/** Verrou en objet neutre dédié --> évite les verrou classiques 
	 * qui peuvent entrer en conflit avec le reste du code
	 */
	private static final Object verrou = new Object();
	
    static class Incrementeur implements Runnable {
        private final String nom;
        private final int nombreIncrements;

        public Incrementeur(String nom, int nombreIncrements) {
            this.nom = nom;
            this.nombreIncrements = nombreIncrements;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreIncrements; i++) {
                
				//Si un thread arrive ici alors qu'un autre y est déjà, 
				//il s'arrête net et attend que le verrou se libère.
                synchronized(verrou) {
					compteurGlobal++;
				}
            }
            // System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static int getCompteur() {
        synchronized(verrou) { //Même principe
			return compteurGlobal;
		}
    }

    public static void resetCompteur() {
        synchronized(verrou) { //Même principe
			compteurGlobal = 0;
		}
    }
}