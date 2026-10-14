public class Inode {

    private MemoryManager memoryManager;
    private int inodeNumber;

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;

    public Inode(
            MemoryManager memoryManager,
            int inodeNumber) {

        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    public int getInodeOffset() {
        return MemoryManager.INODE_TABLE_OFFSET + (this.inodeNumber * INODE_SIZE); 
			   //INODE_TABLE_OFFSET = 2 * BLOCK_SIZE --> adresse de base
			   //this.inodeNumber --> numéro de l'inode que l'on veut cibler
			   //INODE_SIZE --> taille en octets d'un seul inode
			   //Soit : [Début de la table des inodes] 
			   //	  + [La taille occupée par tous les inodes précédents]
    }

    public int getFileType() {
		//Récupère le tableau d'octets global de la mémoire du système de fichiers.
        byte[] memory = memoryManager.getFilesystemMemory(); 
        // Lire un int (4 octets) de la mem --> add lue = début inode (getInodeOffset()) 
		// + décalage de 4 octets car n°d'inode occupe 4 octets
		return Utils.readInt(memory, getInodeOffset() + 4);
	}

    public int getFileSize() {
        byte[] memory = memoryManager.getFilesystemMemory();
		// Lire taille fichier puis se placer au début d'inode 
		// +8 octets de décalage (4 pour n°d'inode + 4 pour type de fichier		
        return Utils.readInt(memory, getInodeOffset() + 8);
    }

    public int[] getDirectPointers() {
        byte[] memory = memoryManager.getFilesystemMemory();
		//Crée tab entiers vide de 10 cases pour stocker les pointeurs directs
        int[] pointers = new int[DIRECT_POINTERS]; 
		// Calcule l'adresse exacte où commencent les pointeurs directs
		int pointerOffset = getInodeOffset() + 28; // 4(n°)+ 4(type)+ 4(taille) +8(créa) + 8(modif) = 28 octets
        for (int pointeur = 0; pointeur < DIRECT_POINTERS; pointeur++) {
			pointers[pointeur] = Utils.readInt(memory, pointerOffset + (pointeur * 4));
		}
        return pointers;
    }
	
	public void writeToMemory(
			int fileType,
			int fileSize,
			long creationTime,
			long modificationTime,
			int[] directPointers,
			int indirectPointer,
			short permissions,
			int linkCount) {
		
		// Récupère le grand tableau d'octets qui représente tout le système de fichiers simulé.
		byte[] memory = memoryManager.getFilesystemMemory();
		int offset = getInodeOffset(); // Initialise le curseur

		// 1. Numéro d'inode
		Utils.writeInt(memory, offset, this.inodeNumber); 
		// On écris le n° de l'inode à l'emplacement du curseur et en int car l'int pèse 4 octets
		offset += 4; //On fait avancer le curseur de 4 puisque qu'on vient d'écrire un int.
		
		// 2. Type
		Utils.writeInt(memory, offset, fileType);
		offset += 4;
		
		// 3. Taille
		Utils.writeInt(memory, offset, fileSize);
		offset += 4;
		
		// 4. Création
		Utils.writeLong(memory, offset, creationTime); 
		// Même principe que pour l'int seulement on écrit plus sur 4 octets mais sur 8 
		// donc on utilise un long.
		offset += 8;
		
		// 5. Modification
		Utils.writeLong(memory, offset, modificationTime);
		offset += 8;
		
		// 6. 10 pointeurs directs
		for (int add = 0; add < DIRECT_POINTERS; add++) {
			// Vérif si tableau de pointeurs non null et contient une valeur pour l'index add(adresse)
            // alors on la prend sinon on écrit 0 (valeur par défaut)
			int pointer = (directPointers != null 
			                    && add < directPointers.length) ? directPointers[add] : 0;
			Utils.writeInt(memory, offset, pointer); //Même fonctionnement que précédemment
			offset += 4;
		}
		
		// 7. Pointeur indirect
		Utils.writeInt(memory, offset, indirectPointer);
		offset += 4;
		
		// 8. Permissions --> short sur 16 bits
		memory[offset] = (byte) (permissions >> 8); //On prend les 8 bits de poid fort (à gauche) et on les écrit
		memory[offset + 1] = (byte) permissions; //On prend les 8 bits de poids faible (à droite) et on les écrit
		offset += 2; // Short codé sur 16 bits et 1 octet = 8 bits 
					 // donc +2 octets pour se décaller de 16bits.
		
		// 9. Nombre de liens
		Utils.writeInt(memory, offset, linkCount);
		offset += 4; // Dernier pas pour que le curseur se trouve exactement au début de l'inode suivant
	}
}