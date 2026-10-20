public class Calcul extends Thread {

	/** Une valeur à traiter (une donnée spécifique)*/
	private int valeur;
	
	/** contrôleur de l'intensité du calcul */
	private int multiplicateur;
	
	/** identificateur de thread, utile pour le debbuging et l'affichage */
	private int index;
	
	/** Stockage du résultat */
	private long resultat;
	
	/**
	 * Constructeur de configuration d'un thread avec ses données de travail.
	 * @args valeur la valeur à traiter (une donnée spécifique) ;
	 * @args multiplicateur contrôleur de l'intensité du calcul ;
	 * @args index un identificateur de thread.
	 */
	public Calcul(int valeur,int multiplicateur,int index) {
		this.valeur = valeur;
		this.multiplicateur = multiplicateur;
		this.index = index;
	}
	
	@Override
	public void run() {
		
		long debut = System.nanoTime();
        long sommeTotale = 0;
		resultat = 0;
		
        for (int iteration = 0; iteration < this.multiplicateur; iteration++) {
            resultat += this.valeur * this.valeur + this.valeur;
        }

        sommeTotale += resultat; // Réduction
        System.out.println("Traitement de " + this.valeur + " terminé : " + resultat);
        
        long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotale);
        System.out.println("Durée : " + duree + " ms");
	}
	
	public long getResultat(){
		return this.resultat;
	}
}