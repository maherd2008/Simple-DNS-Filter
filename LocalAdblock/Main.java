import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.io.PrintWriter;
import java.io.FileWriter;

public class Main {
    private static final String UPSTREAM_DNS = "1.1.1.1";
    private static final int PORT = 53;
    private static final int TIMEOUT_MS = 3000;

    public static void main(String[] args) throws Exception {
        String path = "lightBlocklist.txt";
        BlockList bl = new BlockList(path);

        PrintWriter writer = new PrintWriter(new FileWriter("log.txt"), true);

        DatagramSocket socket = new DatagramSocket(53); 
        System.out.println("Listening on port 53 and printing to log.txt");

        while (true) {
            byte[] buffer = new byte[512];
            DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(clientPacket);

            PacketReader reader = new PacketReader(clientPacket.getData());
            DNSHeader header = DNSHeader.parse(reader);
            DNSQuestion question = DNSQuestion.parse(reader);

            String domain = question.name.toLowerCase();
            LocalDateTime now = LocalDateTime.now();

            if (bl.isBlocked(domain)) {
                writer.println(domain + "\t" + "blocked" + "\t" + now);
                byte[] nxdomain = buildNxDomain(clientPacket.getData(), header.id);
                DatagramPacket reply = new DatagramPacket(nxdomain, nxdomain.length, clientPacket.getAddress(), clientPacket.getPort());

                socket.send(reply);
            } else {
                writer.println(domain + "\t" + "allowed" + "\t" + now);
                byte[] response = forwardUpstream(clientPacket.getData(), clientPacket.getLength());

                if (response != null) {
                    DatagramPacket reply = new DatagramPacket(response, response.length, clientPacket.getAddress(), clientPacket.getPort());
                    socket.send(reply);
                }
            }
        }
    }

    private static byte[] buildNxDomain(byte[] queryData, int id) {
        byte[] response = new byte[queryData.length];
        System.arraycopy(queryData, 0, response, 0, queryData.length);

        response[2] = (byte) 0x81;
        response[3] = (byte) 0x03;
        response[4] = 0x00; response[5] = 0x01;
        response[6] = 0x00; response[7] = 0x00;
        response[8] = 0x00; response[9] = 0x00;
        response[10] = 0x00; response[11] = 0x00;

        return response;
    }

    private static byte[] forwardUpstream(byte[] queryData, int queryLength) {
        try (DatagramSocket upstreamSocket = new DatagramSocket()) {
            upstreamSocket.setSoTimeout(TIMEOUT_MS);
            
            InetAddress upstreamAddr = InetAddress.getByName(UPSTREAM_DNS);
            DatagramPacket forwardPacket = new DatagramPacket(queryData, queryLength, upstreamAddr, PORT);
            upstreamSocket.send(forwardPacket);

            byte[] responseBuffer = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            upstreamSocket.receive(responsePacket);

            byte[] response = new byte[responsePacket.getLength()];
            System.arraycopy(responseBuffer, 0, response, 0, responsePacket.getLength());
            return response;
        } catch (SocketTimeoutException e) {
            System.out.println(" -> upstream timeout");
            return null;
        } catch (Exception e) {
            System.out.println(" -> upsream error: " + e.getMessage());
            return null;
        }
    }
}
