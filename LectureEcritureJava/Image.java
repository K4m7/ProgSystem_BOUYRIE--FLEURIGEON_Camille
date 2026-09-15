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
    public Image(int width, int height) {
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
        
        try (FileWriter writer = new FileWriter(filename)) { //Ferme automatiquement le fichier

            writer.write("P3\n");
            writer.write("200 100\n");
            writer.write("255\n");
            
            for (int hauteur = 0; hauteur < height; hauteur++) {
                for (int largeur = 0; largeur < width; largeur++) {
                    writer.write(pixels[hauteur][largeur][0] + " " +
                                 pixels[hauteur][largeur][1] + " " +
                                 pixels[hauteur][largeur][2] + "\n");
                }
            }
          
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du fichier : ");
        }

    }

    static public read_txt(String filename) trows IOException {
        //TODO at home
    }
	
	/**
     * Sauvegarde l'image au format binaire (P6)
     */
    public void save_bin(String filename) throws IOException {
		
		private final int NB_PIXEL = 200*100*3;
		
		byte[] representationBinaire = new byte[NB_PIXEL];
        
        try (FileWriter writer = new FileWriter(filename)) { //Ferme automatiquement le fichier

            writer.write("P6\n");
            writer.write("200 100\n");
            writer.write("255\n");
            
            for (int hauteur = 0; hauteur < height; hauteur++) {
                for (int largeur = 0; largeur < width; largeur++) {
                    writer.write(Integer.toBinaryString(pixels[hauteur][largeur][0]) +
                                 Integer.toBinaryString(pixels[hauteur][largeur][1]) +
                                 Integer.toBinaryString(pixels[hauteur][largeur][2]));
                }
            }
          
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du fichier : ");
        }

    }
	
	static public read_bin(String filename) trows IOException {
        //TODO
    }
	

}
