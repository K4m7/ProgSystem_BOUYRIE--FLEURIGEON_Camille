public class CalculParallele {
	
	/** Données de travail */
	private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final int MULTIPLICATEUR = 1000;
	
	/** Tableau de Threads */
	private static Calcul[] threads = new Calcul[DONNEES.length];
	
	/** Le verrou partagé */
	private Object mutex = new Object();
	
	/** nombre de cœurs physiques/logiques de la machine */
	int cores = Runtime.getRuntime().availableProcessors();
	
	/** nombre fixe d'ouvriers qui vont se partager le travail */
	int nbThreads = 2*cores;
	
	public class ThreadLocal extends Thread {
		
		private Object mutex;
        private long somme = 0;
        private int valeur;
		
		/** 
		 * Constructeur du ThreadLocal.
		 */
		public ThreadLocal(Object mutex) {
			this.mutex = mutex;
		}	 
		
		@Override
		public void run() {
			
			//mutex : sert de verrou pour éviter que plusieurs threads écrivent 
			//en même temps dans la même variable.
			
			//synchronized : garantit qu'un seul thread à la fois peut exécuter 
			//le bloc de code. Les autres attendent leur tour.
			synchronized(mutex) { 
				somme += valeur;
			}
		} 
		
		long getResults() {
            return somme;
        }
	}
	
	/** 
     * Point d'entrée principal pour démarrer et exécuter les Threads
     */
    public static void main(String[] args) {

        // Démarrage du Calcul Parallèle
        // Création des instances de Calcul + Stockage dans le tableau de threads
        for (int noT = 0; noT < DONNEES.length; noT++) {
            threads[noT] = new Calcul(DONNEES[noT], MULTIPLICATEUR, noT);
        }

        // Démarrez tous les threads avec une boucle utilisant .start()
        for (int noT = 0; noT < threads.length; noT++) {
            threads[noT].start();
        }

        // Attendre la fin de tous les threads (join())
		    // Explications : join() indique au thread principal (main) d'attendre que 
		    // le thread cible (threads[i]) ait fini son travail (c'est-à-dire que 
		    // sa méthode run() soit terminée).
		for (int noT = 0; noT < threads.length; noT++) {
			try {
				threads[noT].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Récupération et addition de tous les résultats
		long debut = System.nanoTime();
		long sommeTotale = 0;

		for (int result = 0; result < threads.length; result++) {
			sommeTotale += threads[result].getResultat();
		}
		
		long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotale);
        System.out.println("Durée totale du calcul parallèle : " + duree + " ms");
	}
}