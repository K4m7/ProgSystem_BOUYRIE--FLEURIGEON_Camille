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
	
	public boolean writeFile(
			int inodeNum,
			byte[] data) {

		int blocksNeeded =
				(data.length
				+ MemoryManager.BLOCK_SIZE - 1)
				/ MemoryManager.BLOCK_SIZE;

		if (blocksNeeded > Inode.DIRECT_POINTERS) {
			return false;
		}

		int[] blockPointers = new int[Inode.DIRECT_POINTERS];

		// Allouer blocksNeeded blocs.
		for (int numBlock = 0; numBlock < blocksNeeded; numBlock++) { //s'exécute pour tous les blocs à allouer
            int block = memoryManager.allocateBlock();
            if (block == -1) { //Renvoyé par allocateBlock()
                return false; // Échec si la mémoire est pleine
            }
			// Stocker le n° de bloc physique alloué (block) 
			// dans le tableau blockPointers à la position numBlock
            blockPointers[numBlock] = block;
        }
		
		byte[] memory = memoryManager.getFilesystemMemory();
		int bytesRemaining = data.length;
		int dataSrcOffset = 0;

		for (int block = 0; block < blocksNeeded; block++) { //Parcours des blocs
			// calculer la quantité à copier : 512 octets (BLOCK_SIZE) ou ce qui reste (bytesRemaining)
			int bytesToCopy = Math.min(bytesRemaining, MemoryManager.BLOCK_SIZE); 
			
			// récupérer le numéro du bloc
			int blockNum = blockPointers[block];
			
			// calculer son offset physique = adresse de départ exacte (en octets)
			int offsetPhysique = blockNum * MemoryManager.BLOCK_SIZE;
			
			// copier les données.
			// parcourt de chaque octet du fragment à copier (0 jusqu'à bytesToCopy-1)
            for (int octet = 0; octet < bytesToCopy; octet++) {
				// Copie l'octet depuis data à partir de dataSrcOffset + octet
				// vers emplacement physique exact dans memory
                memory[offsetPhysique + octet] = data[dataSrcOffset + octet];
            }
			
			//Avance le curseur de lecture de dataSrcOffset 
			//du nombre d'octets qui viennent d'être écrits
            dataSrcOffset += bytesToCopy;
			//On retire les bytes déjà copiés de bytesRemaining
            bytesRemaining -= bytesToCopy;
        }

		// Mettre à jour l'inode.
		Inode inode = new Inode(memoryManager, inodeNum);
        long now = System.currentTimeMillis(); //Pour fixer les dates de création et de modification

        inode.writeToMemory(
                1,                  // Type : fichier
                data.length,        // Taille totale du fichier
                now,                // Date de création
                now,                // Date de modification
                blockPointers,      // Table de pointeurs de blocs
                0,                  // Pointeur indirect
                (short) 0644,       // Permissions --> cast obligatoire !
                1                   // Nb Liens (minimum)
        );
		return true;
	}
	
	public byte[] readFile(int inodeNum) {
		Inode inode = new Inode(memoryManager, inodeNum);
		int fileSize = inode.getFileSize();

		if (fileSize == 0) {
			return new byte[0];
		}

		byte[] fileData = new byte[fileSize];
		byte[] memory = memoryManager.getFilesystemMemory();
		int[] blockPointers = inode.getDirectPointers();

		// Parcourir les blocs utilisés.
		// Difficultés à résoudre par moi-même 
		// Suggestion par une IA puis adaptation par mes soin de : 
        // "System.arraycopy(src, srcPos, dest, destPos, length);"
		for (int block = 0; block * MemoryManager.BLOCK_SIZE < fileSize; block++) {
			// adresse physique de départ (en octets) du bloc courant dans le tableau memory
            int addDepart = blockPointers[block] * MemoryManager.BLOCK_SIZE;
            // adresse où placer les octets dans le tableau fileData
			int addDestination = block * MemoryManager.BLOCK_SIZE;
            // Nombre d'octets à transférer : 512 octets (BLOCK_SIZE) ou ce qui reste (bytesRemaining)
			int nbOctetsTransfert = Math.min(MemoryManager.BLOCK_SIZE, fileSize - addDestination);

            System.arraycopy(memory, addDepart, fileData, addDestination, nbOctetsTransfert);
        }
		return fileData;
	}
}
