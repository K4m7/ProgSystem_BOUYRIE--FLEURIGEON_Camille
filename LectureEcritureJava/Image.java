import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;


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

    public static Image read_txt(String filename) throws IOException {
        //TODO at home
		return null;
    }
	
	/**
     * Sauvegarde l'image au format binaire (P6)
     */
    public void save_bin(String filename) throws IOException {
        
        // Création du tableau final avec la taille exacte connue à l'avance
        byte[] tableau = new byte[15014]; //15000 = 100*50*3 + les 16 bits d'entete

        // En-tête "P6\n"
        tableau[0] = 0x50; // 'P'
        tableau[1] = 0x36; // '6'
        tableau[2] = 0x0A; // '\n'

        // Largeur "100" + Espace (0x20) + Hauteur "50" + Saut de ligne (0x0A)
        tableau[3] = 0x31; // '1'
        tableau[4] = 0x30; // '0'
        tableau[5] = 0x30; // '0'
        tableau[6] = 0x20; // ' '
        tableau[7] = 0x35; // '5'
        tableau[8] = 0x30; // '0'
        tableau[9] = 0x0A; // '\n'

        // Maximum "255\n"
        tableau[10] = 0x32; // '2'
        tableau[11] = 0x35; // '5'
        tableau[12] = 0x35; // '5'
        tableau[13] = 0x0A; // '\n'

        // Ajout des pixels avec le décalage de l'en-tête (+14)
        int index = 14;

        for (int ligne = 0; ligne < height; ligne++) {
            for (int colonne = 0; colonne < width; colonne++) {
                tableau[index++] = (byte) (pixels[ligne][colonne][0] & 0xFF); // R
                tableau[index++] = (byte) (pixels[ligne][colonne][1] & 0xFF); // G
                tableau[index++] = (byte) (pixels[ligne][colonne][2] & 0xFF); // B
            }
        }

        // Écriture du fichier
        try (FileOutputStream fichier = new FileOutputStream(filename)) { 
            //FileWriter inutilisable ici car char entre -128 et 127
            fichier.write(tableau);

        } catch (IOException e) {
            System.err.println("Erreur lors de la création du fichier : " + e.getMessage());
		}
    }
	
	public static Image read_bin(String filename) throws IOException {
        
        // Création d'une image vide aux bonnes dimensions
        Image img = new Image(100, 50);

        try (FileInputStream file = new FileInputStream(filename)) {
            
            // 1. On ignore les 14 octets de l'en-tête ("P6\n100 50\n255\n")
            file.skip(14);
            
            // 2. On lit les pixels et on remplit l'image
            for (int hauteur = 0; hauteur < img.getHeight(); hauteur++) {
                for (int largeur = 0; largeur < img.getWidth(); largeur++) {

                    int r = file.read() & 0xFF;
                    int g = file.read() & 0xFF;
                    int b = file.read() & 0xFF;
                    
                    img.setPixel(largeur, hauteur, r, g, b);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        
        return img;
    }
}
