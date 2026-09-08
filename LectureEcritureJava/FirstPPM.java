import java.io.FileWriter;
import java.io.IOException;

public class FirstPPM {
    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("FirstPPM.ppm");

            writer.write("P3\n");
            // Écriture des dimensions
            // Écriture de la valeur maximal
            // Écriture des pixels
            // Première ligne : rouge, vert, bleu
            // Deuxième ligne : jaune, blanc, noir

            writer.close(); // Fermeture du fichier

            System.out.println("Image PPM créée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
}
