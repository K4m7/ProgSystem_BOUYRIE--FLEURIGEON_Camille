public class CalculParallele {
	
	/** Tableau de Threads */
	Calcul[] threads = new Calcul[DONNEES.length];
	
	/** Le verrou partagé */
	private Object mutex = new Object(mutex);
	
	/** nombre de cœurs physiques/logiques de la machine */
	int cores = Runtime.getRuntime().availableProcessors();
	
	/** nombre fixe d'ouvriers qui vont se partager le travail */
	int nbThreads = 2*cores;
	
	public class ThreadLocal extends Thread {
		
		//TODO variables d'instances
		
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
		
		int getResults() {
			
		}
	}
	
	/** 
	 * Démarrage de tous les Threads
	 */
	@Override
	public class start() {
		//TODO
	}
	

}