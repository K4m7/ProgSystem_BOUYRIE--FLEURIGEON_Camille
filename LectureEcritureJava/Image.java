import java.io.FileWriter;
import java.io.IOException;

public class Image {
    
    //Nous aurions pu remplacer les int en byte afin de gagner en place mémoire.
    private int width;
    private int height;
    // pixels[y][x][0=R,1=G,2=B]
    private int[][][] pixels; // pixels[y][x][0=R,1=G,2=B]

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Constructeur : initialise une image vide.
     */
    public Image(int wigth, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[height][width][3];
    }

    /**
     * Définit la couleur d'un pixel à la position (x, y)
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x][0] = r;
            pixels[y][x][1] = g;
            pixels[y][x][2] = b;
        }
    }

    /**
     * Sauvegarde l'image au format texte PPM (P3)
     */
    public void save_txt(String filename) throws IOException {
        
        try {
            FileWriter writer = new FileWriter("Image.ppm");

            writer.write("P3\n");
            writer.write("200 100\n");
            writer.write("255\n");
            //TODO Ecrire les couleurs des pixels (boucle for)

        } catch (IOException e) {
            System.err.println("Erreur lors de la création du fichier : ");
        }

        
    }
}
