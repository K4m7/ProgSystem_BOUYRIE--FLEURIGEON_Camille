import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws Exception {
        // 1. Connexion au serveur
		Socket socket = new Socket("localhost", 5000);
		System.out.println("Connecté au serveur");
			/*
			 * - new Socket("localhost", 5000) initie le three-way handshake TCP (SYN → SYN-ACK → ACK)
			 * - “localhost” est résolu en 127.0.0.1 (interface loopback)
			 * - Le système d’exploitation alloue automatiquement un port éphémère (généralement 49152-65535)
			 * - La connexion est établie quand le constructeur retourne (ou lève ConnectException si refusée)
			 */
			 
        // 2. Flux réseau (vers/depuis le serveur)
		BufferedReader in = new BufferedReader(
			new InputStreamReader(socket.getInputStream())
		);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader clavier = new BufferedReader(
			new InputStreamReader(System.in)
		);
			/* 
			 * - socket.getInputStream() : lit les données envoyées par le serveur
			 * - socket.getOutputStream() : écrit les données à envoyer au serveur
			 * - System.in : lit les entrées utilisateur depuis le clavier (flux standard)
			 */

		
        // 3. Flux clavier (entrée utilisateur)
		String texte;
		while ((texte = clavier.readLine()) != null) {
			out.println(texte);
			String reponse = in.readLine();
			System.out.println(reponse);
		}
		socket.close();
			/* 
             * clavier.readLine() bloque jusqu’à ce que l’utilisateur tape Entrée
			 * out.println(texte) envoie le message au serveur (avec flush automatique)
			 * in.readLine() bloque jusqu’à recevoir la réponse du serveur
			 * Ctrl+D (Linux/Mac) ou Ctrl+Z (Windows) termine la boucle
			 */

		
        // 4. Boucle d'envoi/réception
        // 5. Fermeture
    }
}