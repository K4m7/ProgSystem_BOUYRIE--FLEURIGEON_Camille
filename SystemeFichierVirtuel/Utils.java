public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
		
		byte b1 = (byte) (value >> 24 & 0xFF);
		byte b2 = (byte) (value >> 16 & 0xFF);
		byte b3 = (byte) (value >> 8 & 0xFF);
		byte b4 = (byte) (value & 0xFF);
		
		memory[offset] = b1;
		memory[offset + 1] = b2;
		memory[offset + 2] = b3;
		memory[offset + 3] = b4;
		
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
}
