public class PacketReader {
    private final byte[] data;
    private int pos;

    public PacketReader(byte[] data) {
        this.data = data;
        this.pos = 0;
    }

    public int readUnsignedByte() {
        return data[pos++] & 0xFF;
    }

    public int readUnsignedShort() {
        return (readUnsignedByte() << 8 | readUnsignedByte());
    }

    public int getPos() {
        return pos;
    }

    public String readQName() {
        StringBuilder sb = new StringBuilder();

        while(true) {
            int len = readUnsignedByte();
            if (len == 0) break;

            if (sb.length() > 0) sb.append(".");

            for (int i = 0; i < len; i++) {
                sb.append((char) readUnsignedByte());
            }
        }

        return sb.toString();
    }
}
