/**
 * Principe du modèle thread-per-connection
 * 
 * Séparation des responsabilités :
 * - Thread principal : boucle d’acceptation dédiée, création rapide de threads handlers
 * - Threads workers : un par client, exécutent la logique métier (lecture/écho) de manière isolée
 *
 * Avantages :
 * - Scalabilité immédiate (N clients = N threads)
 * - Isolation des erreurs (un crash client n’affecte pas les autres)
 * - Simplicité de développement (code séquentiel par client)
 *
 * Inconvénients (à considérer pour la production) :
 * - Coût mémoire élevé (chaque thread = ~1 Mo de stack)
 * - Overhead de context switching au-delà de quelques milliers de threads
 * - Limite système du nombre de threads (ulimit -u)
 */

import java.net.*;
import java.io.*;


public class ServeurMultiClient {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(5000);
		System.out.println("Serveur multi-clients démarré");

		while (true) {
			Socket client = serverSocket.accept();
			System.out.println("Nouveau client : " + client.getInetAddress());

			// Délégation à un thread dédié
			Thread t = new Thread(new ClientHandler(client));
			t.start();
				/*
				 * Création du thread :
                 * - new Thread(Runnable) crée un nouveau contexte d’exécution mais ne le démarre pas
                 * - start() invoque l’appel système natif (pthread_create sur Linux) qui :
                 * 		Alloue une stack dédiée (taille configurable via -Xss, défaut 1 Mo)
				 *		Place le thread dans l’état RUNNABLE du scheduler OS
				 *		Planifie l’exécution de la méthode run() dès qu’un cœur CPU se libère
				 * 
				 * Passage du socket au thread :
				 * - Le constructeur ClientHandler(Socket) transfère la propriété du socket au thread
				 * - Le thread principal ne doit jamais manipuler ce socket après le démarrage
				 * - Cette isolation garantit qu’aucune race condition ne survient sur les flux I/O
				 */
		}
	}
}

class ClientHandler implements Runnable {
	private Socket client;

	public ClientHandler(Socket socket) {
		this.client = socket;
	}

	@Override
	public void run() {
		try {
			// 1. Création des canaux input/output
			BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream())
			);
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			System.out.println("Création des canaux...");
			
			// 2. Boucle d'écho (lecture/écriture)
			String ligne;
			while ((ligne = in.readLine()) != null) {
				System.out.println("[Thread-"+ Thread.currentThread().getName() +"] Reçu : " + ligne);
				out.println("ECHO: " + ligne);
					/* 
					 * Chaque thread a un nom auto-généré (Thread-0, Thread-1, etc.)
					 * Utile pour le debugging et la corrélation des logs
					 * Peut être personnalisé avec t.setName("Client-" + client.getPort())
					 */

			}
			System.out.println("Création de la boucle de lecture/écriture...");
						
			// 3. Fermeture de la connexion client
			System.out.println("Client déconnecté");
			in.close();
			out.close();
			client.close();
			System.out.println("Fermeture de la connexion client.");
			
		} catch (Exception e){
			System.err.println("Une erreur s'est produite :" + e.getMessage());
		} finally {
			try { client.close(); } catch (IOException ignored) {}
				/*
				 * Garantit la fermeture du socket même en cas d’exception (IOException, RuntimeException)
				 * Le double try-catch gère le cas rare où close() lui-même lève une exception
				 * Sans ce bloc, un client malveillant pourrait épuiser les descripteurs de fichiers disponibles
				 */
		}

	}
}
