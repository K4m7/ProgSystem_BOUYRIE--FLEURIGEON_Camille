import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class ProducteurConsommateur {

	private static final int TAILLE_BUFFER = 5;
	private final Integer[] buffer = new Integer[TAILLE_BUFFER];

	private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
	private final Semaphore elementsDisponibles = new Semaphore(0);  
	private final Semaphore mutexBuffer = new Semaphore(1); // quelle erreur ici ? vous pouvez le résoudre plus tard

	class Producteur implements Runnable {
		public void run() {
			for (int i = 0; i < 10; i++) {
				int produit = genererProduit();

				// Attendre une place libre
				//Si un thread arrive ici alors qu'un autre y est déjà, 
				//il s'arrête net et attend que le verrou se libère.
				synchronized(placesLibres) {
					buffer.aquire(produit);
					}
				}
				// TODO: Acquérir l'accès exclusif au buffer  
				// TODO: Ajouter l'élément au buffer
				// TODO: Libérer l'accès au buffer
				// TODO: Signaler qu'un élément est disponible
			}
		}
	}
	
	class Consommateur implements Runnable {
		public void run() {
			for (int i = 0; i < 7; i++) {
				// Attendre qu'un élément soit disponible
				synchronized(elementsDisponibles) {
					buffer.release();
					}
				}
				// TODO: Acquérir l'accès exclusif au buffer
				// TODO: Retirer un élément du buffer  
				// TODO: Libérer l'accès au buffer
				// TODO: Signaler qu'une place est libre
			}
		}
	}
	
	class genererProduit {
		return 
	}
	
	//TODO Mettre des getter ou des copies de references (en args de constructeur) pour les private
}