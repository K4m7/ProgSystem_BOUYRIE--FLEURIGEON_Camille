public class Gradient {
    public static void main(String[] args) {
        int largeur = 200;
        int hauteur = 100;
        Image img = new Image(largeur,hauteur);
        
	//Génération du dégradé de bleu
        for (int height = 0; height < hauteur; height++) {
            for (int width = 0; width < largeur; width++) {
                int bleu = (width * 255) / (largeur - 1);
                img.setPixel(width,height,0,0,bleu);
            }
        }
    
    	try {
            img.save_txt("Gradient.ppm");
            System.out.println("Dégradé créé avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dégradé : " + e.getMessage());
        }
    }
}
