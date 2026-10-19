public class CompteurDangereux {
    private static int compteurGlobal = 0;  // Variable partagée MODIFIABLE

    static class Incrementeur implements Runnable {
        private final String nom;
        private final int nombreIncrements;

        public Incrementeur(String nom, int nombreIncrements) {
            this.nom = nom;
            this.nombreIncrements = nombreIncrements;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreIncrements; i++) {
                // TODO: Expliquer pourquoi cette ligne pose problème
				// compteurGlobal++ n'est pas atomique : c'est en réalité 3 étapes
				// distinctes pour le processeur :
				//   1. LOAD  : lire la valeur de compteurGlobal dans un registre
				//   2. ADD   : ajouter 1 à la valeur dans le registre
				//   3. STORE : réécrire le résultat dans compteurGlobal
				// Un autre thread peut s'intercaler entre ces étapes. Exemple :
				//   Thread A lit 100, Thread B lit 100 (même valeur !),
				//   A calcule 101 et écrit 101, B calcule 101 et écrit 101.
				//   Résultat : 101 au lieu de 102, un incrément a été perdu.
                compteurGlobal++;  // RACE CONDITION ICI !
            }
            // System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static int getCompteur() {
        return compteurGlobal;
    }

    public static void resetCompteur() {
        compteurGlobal = 0;
    }
}