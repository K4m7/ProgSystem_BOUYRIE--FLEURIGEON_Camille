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
        return getInodeOffset() + 4;
    }

    public int getFileSize() {
        return getInodeOffset() + 8;
    }

    public int[] getDirectPointers() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] pointers =
                new int[DIRECT_POINTERS];

        for (int pointeur = 0; pointeur < DIRECT_POINTERS; pointeur++) {
			pointers[pointeur] = getInodeOffset() + 28 + pointeur*4;
		}
		
        return pointers;
    }
}