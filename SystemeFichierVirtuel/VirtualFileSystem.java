import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager =
                new MemoryManager();
    }

    private int allocateInode() {
        byte[] memory = memoryManager.getFilesystemMemory();
        // Parcourir les inodes de 0 à MAX_INODES - 1.
		for (int numInode = 0; numInode < MemoryManager.MAX_INODES; numInode++) {
            Inode inode = new Inode(memoryManager, numInode);
		
			// Identifier le premier inode libre.
			// Retourner son numéro.
			if (inode.getFileType() == 0) { //Vérifie si le type du fichier de cet inode (getFileType()) vaut 0
					return numInode;
				}
		}
        return -1; //Pas d'inode libre
    }

    public boolean createFile(
            String directory,
            String filename) {

        int numInode = allocateInode();

        if (numInode == -1) {
            return false;
        }

        // Construire l'inode.
		// Si un inode est libre on crée l'Inode correspondant au numInode
		Inode inode = new Inode(memoryManager, numInode); 
		
		// L'initialiser comme fichier vide.
		int fileType = 1; // 1 indique qu'il s'agit d'un fichier
        int fileSize = 0; // fichier vide = taille nulle
        long creationTime = System.currentTimeMillis(); // Heure actuelle du système
        long modificationTime = creationTime;
        int[] directPointers = new int[Inode.DIRECT_POINTERS]; // Un tableau vide de 10 pointeurs (tous initialisés à 0)
        int indirectPointer = 0; //Aucun pointeur indirect au démarrage
        short permissions = (short) 0644; // Permissions standards : read & write
        int linkCount = 1; 
			// Aide IA : Le fichier possède un premier lien logique
			// car lorsqu'un fichier vient d'être créé, 
			// il est immédiatement rattaché à au moins un nom dans un répertoire.
		
		inode.writeToMemory( //Appel de la méthode pour sérialiser ses métadonnées
                fileType,
                fileSize,
                creationTime,
                modificationTime,
                directPointers,
                indirectPointer,
                permissions,
                linkCount
        );
        return true; //Fichier crée sans erreur.
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
}
