public class DNSHeader {
    public int id;
    public int flags;

    public int qdCount;
    public int anCount;
    public int nsCount;
    public int arCount;

    public static DNSHeader parse(PacketReader reader) {
        DNSHeader h = new DNSHeader();
        
        h.id = reader.readUnsignedShort();
        h.flags = reader.readUnsignedShort();

        h.qdCount = reader.readUnsignedShort();
        h.anCount = reader.readUnsignedShort();
        h.nsCount = reader.readUnsignedShort();
        h.arCount = reader.readUnsignedShort();

        return h;
    }
}
