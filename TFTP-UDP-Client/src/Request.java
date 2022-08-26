import java.io.IOException;
import java.net.*;

public class Request extends Thread {
    private final DatagramSocket socket;
    private InetAddress hostAddress;
    private int port;

    private final String fileName;
    public int block;

    protected byte[] recvBuf;
    protected int bufferLen = 516;

    /**
     * @param req The type of request either WRQ or RRQ.
     * @param fileName The name of the file which is being sent or retrieved.
     * @param serverIP The servers IP address.
     * @param serverPort The servers port.
     * @throws IOException if an I/O error occurs.
     */
    public Request(Opcode req, String fileName, InetAddress serverIP, int serverPort) throws IOException {
        super(req.name() + " Thread");

        socket = new DatagramSocket();
        socket.setSoTimeout(2000);

        hostAddress = serverIP;
        port = serverPort;

        this.fileName = fileName;

        if (req.equals(Opcode.RRQ)) {
            block = 1;
        } else {
            block = 0;
        }
    }

    /**
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
     * @param packet The packet to be sent to the server.
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
     * Increments the block counter by 1.
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
