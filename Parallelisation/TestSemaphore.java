/**
 * ! NOTE POUR LE PROFESSEUR !
 * La partie consernant le monitoring à été faite en partie 
 * avec l'aide d'une IA pour m'aider à comprendre le fonctionnement
 * global de celui-ci, son rôle et sa pertinence.
 */

import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class TestSemaphore {
	
    private static final int TAILLE_BUFFER = 5;
    private final Queue<Integer> buffer = new LinkedList<>();

    private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
    private final Semaphore elementsDisponibles = new Semaphore(0);
    private final Semaphore mutexBuffer = new Semaphore(1);
	
	//Compteurs pour le monitoring
	private int totalProduitsGlobal = 0;
    private int totalConsommesGlobal = 0;
	
    class Producteur implements Runnable {
		
        private final String nom;
        private final int nbProduits;

        public Producteur(String nom, int nbProduits) {
            this.nom = nom;
            this.nbProduits = nbProduits;
        }

        public void run() {
            try {
                for (int p = 0; p < nbProduits; p++) {
					//Simulation de la création du produit : entre 0 et 99
                    int produit = (int) (Math.random() * 100);

                    placesLibres.acquire();
                    mutexBuffer.acquire();  

                    buffer.add(produit);
					
					// MONITORING : Incrémentation du compteur global de production
                    totalProduitsGlobal++;
					
                    // Affichage direct pour suivre l'état du buffer en temps réel
                    System.out.println("[Etat buffer : " + nom 
					                   + "] Produit ajouté : " + produit 
									   + "\nBuffer : " + buffer.size() 
									   + "/" + TAILLE_BUFFER
									   + "\nTotal produits : " + totalProduitsGlobal);

                    mutexBuffer.release();  
                    elementsDisponibles.release(); 

                    Thread.sleep(100); //Ralentissement de l'affichage 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Consommateur implements Runnable {
        private final String nom;
        private final int nbConsommations;

        public Consommateur(String nom, int nbConsommations) {
            this.nom = nom;
            this.nbConsommations = nbConsommations;
        }

        public void run() {
            try {
                for (int i = 0; i < nbConsommations; i++) {
                    elementsDisponibles.acquire();
                    mutexBuffer.acquire();  

                    int produit = buffer.poll();
					
					// MONITORING : Incrémentation du compteur global de consommation
                    totalConsommesGlobal++;
					
                    // Affichage direct pour suivre la consommation
                    System.out.println("[Consommation : " + nom 
					                   + "] Consommé : " + produit 
									   + "\nBuffer : " + buffer.size() 
									   + "/" + TAILLE_BUFFER
									   + "Total consommés : " + totalConsommesGlobal);

                    mutexBuffer.release();   
                    placesLibres.release();  

                    Thread.sleep(100); //Ralentissement de l'affichage 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestSemaphore test = new TestSemaphore();

			// SCÉNARIO 1 : EQUILIBRE (2 PRODUCTEURS, 3 CONSOMMATEURS)
        System.out.println("-- DÉBUT SCÉNARIO ÉQUILIBRÉ --");
		
		TestSemaphore test1 = new TestSemaphore();
		
        Thread p1 = new Thread(test.new Producteur("Prod 1", 15));
        Thread p2 = new Thread(test.new Producteur("Prod 2", 15));
        Thread c1 = new Thread(test.new Consommateur("Cons 1", 10));
        Thread c2 = new Thread(test.new Consommateur("Cons 2", 10));
        Thread c3 = new Thread(test.new Consommateur("Cons 3", 10));

        // Démarrage des threads
        p1.start(); p2.start();
        c1.start(); c2.start(); c3.start();

        // Attente de la fin de tous les threads du scénario 1
        p1.join(); p2.join();
        c1.join(); c2.join(); c3.join();
		
		System.out.println("[BILAN SCÉNARIO ÉQUILIBRÉ]"
		                   + "\nProduits totaux : " + test1.totalProduitsGlobal 
		                   + "\nConsommés totaux : " + test1.totalConsommesGlobal 
						   + "\n");


			// SCÉNARIO 2 : DESEQUILIBRE (5 PRODUCTEURS, 2 CONSOMMATEURS)
        System.out.println("-- DÉBUT SCÉNARIO DÉSEQUILIBRÉ --");
        		
		TestSemaphore test2 = new TestSemaphore();
		
        Thread p3 = new Thread(test.new Producteur("Prod 3", 10));
        Thread p4 = new Thread(test.new Producteur("Prod 4", 10));
        Thread p5 = new Thread(test.new Producteur("Prod 5", 10));
        Thread p6 = new Thread(test.new Producteur("Prod 6", 10));
        Thread p7 = new Thread(test.new Producteur("Prod 7", 10));
        
        Thread c4 = new Thread(test.new Consommateur("Cons 4", 25));
        Thread c5 = new Thread(test.new Consommateur("Cons 5", 25));

        // Démarrage des threads
        p3.start(); p4.start(); p5.start(); p6.start(); p7.start();
        c4.start(); c5.start();

        // Attente de la fin de tous les threads du scénario 2
        p3.join(); p4.join(); p5.join(); p6.join(); p7.join();
        c4.join(); c5.join();
		
        System.out.println("[BILAN SCÉNARIO ÉQUILIBRÉ]"
		                   + "\nProduits totaux : " + test2.totalProduitsGlobal 
		                   + "\nConsommés totaux : " + test2.totalConsommesGlobal 
						   + "\n");
    }
}