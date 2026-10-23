import java.net.*;
import java.io.*;

public class ClientAsync {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Connecté au serveur");

        // Thread de lecture (messages du serveur)
        Thread lectureThread = new Thread(new ReceptionHandler(socket));
        lectureThread.start();
			/* 
			 * - Le thread principal conserve la responsabilité de l’envoi (écriture)
             * - Un thread dédié gère la réception (lecture)
             * - Les deux threads partagent le même Socket, mais utilisent des flux 
			 *   distincts (InputStream vs OutputStream)
			 * 
			 * Sécurité de cette approche :
             * - TCP garantit que les flux d’entrée et de sortie sont indépendants au 
			 *   niveau protocole
             * - Java garantit que getInputStream() et getOutputStream() retournent des 
			 *   objets thread-safe
             * - Aucune synchronisation nécessaire car il n’y a pas de ressource partagée 
			 *   modifiable
			 */

        // Thread principal = écriture (envoi au serveur)
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader clavier = new BufferedReader(
            new InputStreamReader(System.in)
        );

        String texte;
        while ((texte = clavier.readLine()) != null) {
            out.println(texte);
			/*
			 * - Le thread de lecture boucle indéfiniment jusqu’à ce que le serveur 
			 *   ferme la connexion
             * - Quand le serveur envoie FIN, readLine() retourne null et le thread se 
			 *   termine proprement
             * - Le thread principal termine quand l’utilisateur ferme l’entrée standard (Ctrl+D)
             *
			 * Explication des améliorations :
			 * - setDaemon(true) :
			 *	Marque le thread de lecture comme daemon
			 *	Permet à la JVM de terminer même si ce thread tourne encore
			 *  Évite que l’application reste bloquée après la fermeture du socket
             *
			 * - volatile boolean running :
			 *	Le mot-clé volatile garantit la visibilité entre threads
			 *	Quand le thread principal modifie running, le thread de lecture voit immédiatement le changement
			 *	Sans volatile, le compilateur pourrait cacher la variable dans un registre CPU
			 */
        }

        socket.close();
    }
}

class ReceptionHandler implements Runnable {
    private Socket socket;

    public ReceptionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
            try {
            // On crée un flux de lecture pour écouter ce que le serveur envoie à travers le socket
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            String message;
            // Boucle infinie (tant que le serveur est connecté) pour lire les messages reçus
            while ((message = in.readLine()) != null) {
                // On affiche immédiatement le message du serveur sur la console
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Connexion perdue avec le serveur.");
        }
    }
}
