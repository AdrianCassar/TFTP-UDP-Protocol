import java.io.IOException;
import java.net.*;

/**
 * This class represents the base class of a request.
 * This class is intended to be ran on its own thread.
 */
public class Request extends Thread {
    private final DatagramSocket socket;
    private InetAddress hostAddress;
    private int port;

    private final String fileName;
    private int block;

    protected byte[] recvBuf;
    protected int bufferLen = 516;

    /**
     * @param packet Instance of an unpacked datagram for a WRQ or RRQ operation.
     * @throws IOException if an I/O error occurs.
     */
    public Request(ReqPacket packet) throws IOException {
        super(packet.getPacketType().name() + " Thread");

        socket = new DatagramSocket();
        socket.setSoTimeout(2000);

        hostAddress = packet.getPacket().getAddress();
        port = packet.getPacket().getPort();
        fileName = packet.getFilename();

        if (packet.getPacketType().equals(Opcode.RRQ)) {
            block = 1;
        } else {
            block = 0;
        }
    }

    /**
     * Blocks until a packet is received and ten process the data into the relevant packet class.
     * @return The packet that was received from the server.
     * @throws IOException if an I/O error occurs.
     */
    public Packet receive() throws IOException {
        recvBuf = new byte[bufferLen];
        DatagramPacket buffPacket = new DatagramPacket(recvBuf, recvBuf.length);
        socket.receive(buffPacket);

        this.hostAddress = buffPacket.getAddress();
        this.port = buffPacket.getPort();

        return DataHandler.getPacket(buffPacket);
    }

    /**
     * Sends a packet to the client.
     * @param packet The packet to be sent to the client.
     * @throws IOException if an I/O error occurs.
     */
    public void send(DatagramPacket packet) throws IOException {
//        System.out.println("Packet Len Sent: " + packet.getLength());

        socket.send(packet);
    }

    /**
     * @return The current block number.
     */
    public int getBlock() {
        return block;
    }

    /**
     * Increments the block number by 1.
     */
    public void incrementBlock() {
        block++;
    }

    /**
     * @return The name of the file which is being sent or retrieved.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return The IP address of the server.
     */
    public InetAddress getHostAddress() {
        return hostAddress;
    }

    /**
     * @return The port of the server.
     */
    public int getPort() {
        return port;
    }
}