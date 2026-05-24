public class DNSQuestion {
    public String name;
    public int type;
    public int clazz;

    public static DNSQuestion parse(PacketReader reader) {
        DNSQuestion q = new DNSQuestion();

        q.name = reader.readQName();

        q.type = reader.readUnsignedShort();
        q.clazz = reader.readUnsignedShort();

        return q;
    }
}
