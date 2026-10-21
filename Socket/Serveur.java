import java.net.*;
import java.io.*;

public class Serveur {
    public static void main(String[] args) throws Exception {
        // 1. Création du serveur et liaison au port 5000
		ServerSocket serverSocket = new ServerSocket(5000); //ServerSocket encapsule l’appel système socket() + bind() + listen() en une seule opération
		System.out.println("Serveur démarré sur le port 5000"); //Le port 5000 devient le point d’écoute TCP sur toutes les interfaces réseau de la machine (0.0.0.0)
		//Test dans GitBash avec : netstat -an | grep 5000 (Résultat attendu : tcp  0  0  0.0.0.0:5000  0.0.0.0:*  LISTEN)

		// 2. Acceptation d'une connexion cliente
		Socket client = serverSocket.accept();
		System.out.println("Client connecté : " + client.getInetAddress());
			/* 
			 * Explications : 
			 * - accept() est un appel bloquant : le thread s’endort jusqu’à ce qu’une connexion TCP soit établie
			 * - Au niveau système, cela correspond à l’extraction d’une connexion de la file d’attente 
			 * créée par listen()
			 * - Le retour est une nouvelle Socket représentant le canal bidirectionnel avec ce client spécifique
			 * - Cette Socket est un File Descriptor (FD) au même titre que la socket serveur, mais elle 
			 * représente une connexion établie (état ESTABLISHED du TCP)
			 * 
			 * Attention ! 
			 * ServerSocket : socket d’écoute passive, ne transporte jamais de données applicatives
			 * Socket (retournée par accept) : socket de communication active, utilisée pour les échanges de données
			 */
			 
		// 3. Création des canaux input/output
		BufferedReader in = new BufferedReader(
			new InputStreamReader(client.getInputStream())
		);
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		System.out.println("Création des canaux...");
			/* 
			 * Explications : 
			 * - client.getInputStream()
			 *     Retourne un InputStream brut, flux d’octets bas niveau lisant depuis le buffer de réception TCP du kernel
			 *     Les données arrivent du réseau via le socket et sont bufferisées par le système d’exploitation
			 *     InputStreamReader convertit ce flux d’octets en flux de caractères (décodage UTF-8 par défaut)
			 *     BufferedReader ajoute une couche de bufferisation applicative et permet la lecture ligne par ligne avec readLine()
			 *
			 * - client.getOutputStream()
			 *     Retourne un OutputStream brut, flux d’octets bas niveau écrivant dans le buffer d’émission TCP du kernel
			 *     PrintWriter encapsule ce flux pour faciliter l’écriture de texte formaté
			 *     Le paramètre true active l’auto-flush : chaque println() vide immédiatement le buffer vers le réseau (appel système write())
			 */
			 
		
		// 4. Boucle d'écho (lecture/écriture)
		String ligne;
		while ((ligne = in.readLine()) != null) {
			System.out.println("Reçu : " + ligne);
			out.println("ECHO: " + ligne);
		}
		System.out.println("Création de la boucle de lecture/écriture...");
			/* 
			 * - readLine() bloque jusqu’à recevoir un caractère de fin de ligne (\n ou \r\n)
			 * - La condition != null détecte la fermeture du flux côté client (réception d’un paquet FIN TCP)
			 * - Chaque message reçu est immédiatement renvoyé avec le préfixe “ECHO:”
			 * - Le flush automatique garantit l’envoi immédiat sans attendre un buffer plein
			 * 
			 * Comportement TCP sous-jacent :
			 * - Les données sont transmises en segments TCP (taille variable selon la MTU réseau)
			 * - L’algorithme de Nagle peut regrouper plusieurs println() en un seul paquet si le flush est désactivé
			 * - Le contrôle de flux TCP ajuste automatiquement le débit selon la congestion réseau
			 */

		
		// 5. Fermeture de la connexion client
		System.out.println("Client déconnecté");
		in.close();
		out.close();
		client.close();
		System.out.println("Fermeture de la connexion client.");
			/* 
			 * in.close() et out.close() ferment les flux applicatifs
			 * client.close() envoie un paquet FIN TCP pour initier la fermeture gracieuse (four-way handshake)
			 * Le descripteur de fichier est libéré, le port local redevient disponible
			 * Sans fermeture explicite, le socket reste en état TIME_WAIT pendant 2×MSL (généralement 60 secondes)
			 */
    }
}