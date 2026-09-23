public class TestRunner {

	public static void main(String[] args) {
        testStep2();
		testStep3();
		testStep4();
		testStep5();
    }

	public static void testStep2() {
		System.out.println("=== TEST ÉTAPE 2 : Utils Entiers ===");

		byte[] buffer = new byte[32];

		int value = 0xF0A1B2E3;
		int written = Utils.writeInt(buffer, 3, value);

		assert written == 4 : "writeInt doit retourner 4";

		assert (buffer[3]  & 0xFF) == 0xF0 : "Octet 0 incorrect";
		assert (buffer[4]  & 0xFF) == 0xA1 : "Octet 1 incorrect";
		assert (buffer[5]  & 0xFF) == 0xB2 : "Octet 2 incorrect";
		assert (buffer[6]  & 0xFF) == 0xE3 : "Octet 3 incorrect";

		assert Utils.readInt(buffer, 3) == value :
				"Erreur writeInt / readInt";

		short shortValue = (short) 0xF0A1;
		int shortWritten = Utils.writeShort(buffer, 20, shortValue);

		assert shortWritten == 2 : "writeShort doit retourner 2";

		assert (buffer[20] & 0xFF) == 0xF0 :
				"Premier octet du short incorrect";

		assert (buffer[21] & 0xFF) == 0xA1 :
				"Deuxième octet du short incorrect";

		assert Utils.readShort(buffer, 20) == shortValue :
				"Erreur writeShort / readShort";

		System.out.println("[OK] Étape 2 validée !");
	}
	
	public static void testStep3() {
		System.out.println("=== TEST ÉTAPE 3 : Utils Long & String ===");

		byte[] buffer = new byte[64];

		long value = 0x1122334455667788L;

		int written = Utils.writeLong(buffer, 0, value);

		assert written == 8 : "writeLong doit retourner 8";

		assert (buffer[0] & 0xFF) == 0x11;
		assert (buffer[1] & 0xFF) == 0x22;
		assert (buffer[2] & 0xFF) == 0x33;
		assert (buffer[3] & 0xFF) == 0x44;
		assert (buffer[4] & 0xFF) == 0x55;
		assert (buffer[5] & 0xFF) == 0x66;
		assert (buffer[6] & 0xFF) == 0x77;
		assert (buffer[7] & 0xFF) == 0x88;

		assert Utils.readLong(buffer, 0) == value :
				"Erreur writeLong / readLong";

		for (int i = 16; i < 32; i++) {
			buffer[i] = (byte) 0x7F;
		}

		int stringWritten =
				Utils.writeString(buffer, 16, "MYFS", 16);

		assert stringWritten == 16 :
				"writeString doit retourner maxLength";

		assert (buffer[16] & 0xFF) == 'M';
		assert (buffer[17] & 0xFF) == 'Y';
		assert (buffer[18] & 0xFF) == 'F';
		assert (buffer[19] & 0xFF) == 'S';

		for (int i = 20; i < 32; i++) {
			assert buffer[i] == 0 :
					"La zone inutilisée doit être nettoyée";
		}

		assert Utils.readString(buffer, 16, 16).equals("MYFS") :
				"Erreur writeString / readString";

		System.out.println("[OK] Étape 3 validée !");
	}
	
	public static void testStep4() {
		System.out.println("=== TEST ÉTAPE 4 : Initialisation Mémoire ===");

		MemoryManager mm = new MemoryManager();

		byte[] mem = mm.getFilesystemMemory();

		assert mem != null :
				"La mémoire ne doit pas être nulle";

		assert mem.length == MemoryManager.TOTAL_MEMORY :
				"Taille mémoire incorrecte";

		assert Utils.readString(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET,
				16).equals("MYFS1.0") :
				"Signature du superbloc incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 16)
				== MemoryManager.BLOCK_SIZE :
				"Taille de bloc incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 20)
				== MemoryManager.TOTAL_MEMORY :
				"Taille mémoire incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 24)
				== MemoryManager.NUM_BLOCKS :
				"Nombre de blocs incorrect";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 28)
				== MemoryManager.MAX_INODES :
				"Nombre maximal d'inodes incorrect";

		System.out.println("[OK] Étape 4 validée !");
	}
	
	public static void testStep5() {
		System.out.println("=== TEST ÉTAPE 5 : Bitmap et Allocation ===");

		MemoryManager mm = new MemoryManager();

		assert mm.setBlockUsed(130, true) :
				"setBlockUsed doit réussir";

		assert mm.isBlockUsed(130) == 1 :
				"Le bloc 130 doit être occupé";

		assert mm.setBlockUsed(130, false) :
				"La libération doit réussir";

		assert mm.isBlockUsed(130) == 0 :
				"Le bloc 130 doit être libre";

		mm.setBlockUsed(129, true);

		int bitmapOffset =
				MemoryManager.BITMAP_OFFSET + (129 / 8);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x02 :
				"Le bit du bloc 129 est incorrect";

		mm.setBlockUsed(130, true);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x06 :
				"Les bits 129 et 130 sont incorrects";

		mm.setBlockUsed(130, false);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x02 :
				"La libération du bloc 130 est incorrecte";

		MemoryManager mm2 = new MemoryManager();

		int first = mm2.allocateBlock();
		int second = mm2.allocateBlock();

		assert first == 129 :
				"Le premier bloc de données doit être 129";

		assert second == 130 :
				"Le second bloc de données doit être 130";

		assert mm2.isBlockUsed(129) == 1;
		assert mm2.isBlockUsed(130) == 1;

		assert mm2.isBlockUsed(-1) == -1 :
				"Un bloc négatif doit être refusé";

		assert mm2.isBlockUsed(
				MemoryManager.NUM_BLOCKS) == -1 :
				"Un bloc hors limites doit être refusé";

		System.out.println("[OK] Étape 5 validée !");
	}
}