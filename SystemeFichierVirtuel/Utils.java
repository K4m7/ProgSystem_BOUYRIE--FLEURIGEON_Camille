public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
		memory[offset] = (byte) (value >> 24 & 0xFF);
		memory[offset + 1] = (byte) (value >> 16 & 0xFF);
		memory[offset + 2] = (byte) (value >> 8 & 0xFF);
		memory[offset + 3] = (byte) (value & 0xFF);
		return 4;
	}

    public static int readInt(byte[] memory, int offset) {

        int b1 = memory[offset + 3] & 0xFF;
	    int b2 = memory[offset + 2] & 0xFF;
        int b3 = memory[offset + 1] & 0xFF;
        int b4 = memory[offset] & 0xFF;

        return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }
    public static int writeShort(byte[] memory, int offset, short value) {
        memory[offset] = (byte) ((value >> 8) & 0xFF);
		memory[offset +1] = (byte) (value & 0xFF);
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        int b1 = memory[offset + 1] & 0xFF;
		int b2 = memory[offset] & 0xFF;

        return (short) (b1 | (b2 << 8));
    }
	
	public static int writeLong(byte[] memory, int offset, long value) {
		memory[offset] = (byte) (value >> 56 & 0xFF);
		memory[offset + 1] = (byte) (value >> 48 & 0xFF);
		memory[offset + 2] = (byte) (value >> 40 & 0xFF);
		memory[offset + 3] = (byte) (value >> 32 & 0xFF);
		memory[offset + 4] = (byte) (value >> 24 & 0xFF);
		memory[offset + 5] = (byte) (value >> 16 & 0xFF);
		memory[offset + 6] = (byte) (value >> 8 & 0xFF);
		memory[offset + 7] = (byte) (value & 0xFF);
		return 8;
	}

	public static long readLong(byte[] memory, int offset) {
		long sortie = 0;
		for (int position = 0; position < 8; position++) {
			int decalage = (7-position) * 8;
			long octet = memory[offset+position] & 0xFF;
			sortie += (octet << decalage);
		}
		return sortie;
	}

	public static int writeString(
			byte[] memory,
			int offset,
			String str,
			int maxLength) {

		byte[] strToBytes = str.getBytes();
		int octetsACopier = Math.min(strToBytes.length, maxLength); //car si str < maxLength alors dépasse et si str > maxLength alors on garde que maxLength
		
		for (int octet = 0; octet < octetsACopier; octet++) {
			memory[offset + octet] = strToBytes[octet];
		}

		for (int octet = octetsACopier; octet < maxLength; octet++) {
			memory[offset + octet] = 0;
		
		}
		
		return maxLength;
	}

	public static String readString(
			byte[] memory,
			int offset,
			int maxLength) {

		int longueurString = 0;
		while (longueurString < maxLength && memory[offset + longueurString] != 0) {
			longueurString++;
		}
		
		byte[] octetsString = new byte[longueurString];
		for (int position = 0 ; position < longueurString; position++) {
			octetsString[position] = memory[offset+position];
		}
		
		return new String(octetsString);
	}
}
