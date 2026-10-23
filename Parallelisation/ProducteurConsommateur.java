import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class ProducteurConsommateur {

	private static final int TAILLE_BUFFER = 5;
	private final Integer[] buffer = new Integer[TAILLE_BUFFER];
	
	private int in = 0;  
    private int out = 0;
	private int nbElements = 0;
	
	private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
	private final Semaphore elementsDisponibles = new Semaphore(0);  
	private final Semaphore mutexBuffer = new Semaphore(1); // quelle erreur ici ? vous pouvez le résoudre plus tard
		/*
		 * Aide à la compréhension avec une IA : 
		 * Utiliser un Semaphore comme sémaphore binaire pour simuler un mutex 
		 * fonctionne, mais c'est une mauvaise pratique en Java. Contrairement à un 
		 * à un Mutex, un sémaphore n'a pas de notion d'appartenance : 
		 * n'importe quel thread peut appeler release() sur un sémaphore, 
		 * même s'il ne l'a jamais acquis (acquire()). 
		 * Cela peut masquer des bugs de logique graves si un thread libère le verrou 
		 * par erreur à la place d'un autre.
		 */
		 
	class Producteur implements Runnable {
		public void run() {
			try {
				for (int i = 0; i < 10; i++) {
					int produit = genererProduit();

					// Attendre une place libre
					placesLibres.acquire();
		
					// Acquérir l'accès exclusif au buffer 
					mutexBuffer.acquire();
					
					// Ajouter l'élément au buffer
					buffer[in] = produit;
					nbElements++;
					
					System.out.println("Produit ajouté : " 
										+ produit 
										+ "\nTaille buffer : " 
										+ nbElements);
										
					in = (in + 1) % TAILLE_BUFFER;
					
					// Libérer l'accès au buffer
					mutexBuffer.release();
					
					// Signaler qu'un élément est disponible
					elementsDisponibles.release();
				}
			} catch (InterruptedException e) { // Gestion des interruptions de threads
											   // Exception relevée si un thread est interrompu
											   // alors qu'il était en train d'attendre
            Thread.currentThread().interrupt();
				/*
				 * Exemple : si le producteur est bloqué sur placesLibres.acquire() 
				 * parce que le buffer est plein, et qu'un autre endroit du programme
				 * décide d'arrêter ce thread. 
				 * 
				 * interrupt() --> signale sur le thread lui même qu'il a été interrompu.
				 */
			}
		}
	}
	
	class Consommateur implements Runnable {
		public void run() {
			try {
				for (int i = 0; i < 7; i++) {
					// Attendre qu'un élément soit disponible
					elementsDisponibles.acquire();
					
					//: Acquérir l'accès exclusif au buffer
					mutexBuffer.acquire();
					
					// Retirer un élément du buffer  
					
				/* [Old Version]
					 * int produit = buffer.poll(); // .poll() retire le premier élément de la Queue 
					 *                            // + recherches en plus IA pour mieux comprendre son fonctionnement
					 */
					int produit = buffer[out];
					buffer[out] = null;
					nbElements--;
					
					System.out.println("Consommé : " 
										+ produit 
										+ "\nTaille buffer : " 
										+ nbElements);
										
					out = (out + 1) % TAILLE_BUFFER;
				
					// Libérer l'accès au buffer
					mutexBuffer.release();
					
					// Signaler qu'une place est libre
					placesLibres.release();
				}
			} catch (InterruptedException e) {
            Thread.currentThread().interrupt();
			}
		}
	}
	
	private int genererProduit() {
		return (int) (Math.random() * 100);
	}
	
}