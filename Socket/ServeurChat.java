import java.net.*;
import java.io.*;
import java.util.*;

public class ServeurChat {
    // Liste thread-safe des flux de sortie
    private static final List<PrintWriter> clients = 
        Collections.synchronizedList(new ArrayList<>());
			/* 
			 * Encapsule une ArrayList normale dans un wrapper synchronisé
			 * Chaque méthode (add, remove, get) est protégée par un verrou intrinsèque
			 * Mais attention : l’itération n’est pas automatiquement thread-safe !
			 */


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Serveur Chat démarré");

        while (true) {
            Socket client = serverSocket.accept();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			
            synchronized (clients) { //Verrou explicite
				for (PrintWriter w : clients) { w.println(msg); }
			} //L’itération fait plusieurs appels internes (hasNext(), next()), qui doivent être atomiques.
			
            System.out.println("Client connecté. Total : " + clients.size());
            new Thread(new ChatHandler(client, out)).start();
				/* 
				 * Chaque objet Java possède un monitor (verrou intrinsèque)
				 * synchronized (obj) acquiert ce monitor pour le thread courant
				 * Si un autre thread le détient, le thread actuel est bloqué (état BLOCKED)
				 * À la sortie du bloc, le monitor est libéré automatiquement (même en cas d’exception)
				 */

        }
    }

    static class ChatHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ChatHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
        }

        private void broadcast(String message) {
            synchronized (clients) {
				for (PrintWriter writer : clients) {
					writer.println(">>> " + message);
				}
			}
				/*
				 * Le verrou est maintenu pendant toute la durée de l’envoi à tous les clients
				 * Si un client a une connexion lente, tous les autres threads sont bloqués
				 * Dans un système de production, on préférerait :
				 * Copier la liste sous verrou : List<PrintWriter> snapshot = new ArrayList<>(clients);
				 * Itérer sur la copie sans verrou
				 * Gérer les erreurs d’envoi individuellement
				 */

        }
    }
}
