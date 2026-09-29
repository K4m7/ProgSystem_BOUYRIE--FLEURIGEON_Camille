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
        return 1024 + this.inodeNumber * 4;
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