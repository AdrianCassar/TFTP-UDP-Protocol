import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;

/**
 * A runnable UDP TFTP server.
 * Supports: simultaneous read/write requests, acknowledgments, timeouts/re-transmission and error handling.
 */
public class TFTPServer implements Runnable {

    private final DatagramSocket serverSocket;
    private Packet initialPacket;
    private boolean isRunning = true;

    private final int bufferLen;

    /**
     * Starts the server.
     * Default IP address is the loopback address/127.0.0.1.
     * Default port is 10000
     */
    public TFTPServer() throws SocketException {
        this(10000);
    }

    /**
     * @param port The port the TFTP server will run on.
     */
    public TFTPServer(int port) throws SocketException {
        serverSocket = new DatagramSocket(port, InetAddress.getLoopbackAddress());
        bufferLen = 516;

        System.out.println("Server: " + serverSocket.getLocalAddress().getHostAddress() + ":" + serverSocket.getLocalPort() + "\n");
        System.out.println("TFTP Server Started.");
        System.out.println("q - quit.\n");

        stopServer();
    }

    /**
     * Starts the UDP TFTP server and blocks until a request is received form the client.
     * A new thread is created to handle the request.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                byte[] recvBuf = new byte[bufferLen];

                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                serverSocket.receive(packet);

                initialPacket = new Packet(packet);
                printPacketInfo(packet);

                initialPacket = DataHandler.getPacket(packet);

                if (initialPacket instanceof ReqPacket) {
                    ReqPacket reqPacket = ((ReqPacket) initialPacket);

                    if (reqPacket.getPacketType().equals(Opcode.RRQ)) {
                        new RRQOperation(reqPacket).start();
                    } else {
                        new WRQOperation(reqPacket).start();
                    }
                }
            }
        } catch (IOException e) {
            errorInfo(e);
        }
    }

    /**
     * Prints information about the request packet.
     * @param packet The initial request packet.
     * @throws IOException if an I/O error occurs.
     */
    private void printPacketInfo(DatagramPacket packet) throws IOException {
        System.out.println("Starting " + initialPacket.getPacketType().name());
//        System.out.println("Packet Len Received: " + packet.getLength());

        byte[] array = Arrays.copyOf(packet.getData(), packet.getLength());
        System.out.println("Packet ASCII: " + new String(array) + "\n");
    }

    /**
     * The server will be closed if q is entered in the stdin.
     */
    private void stopServer() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

                    if ((console.readLine().trim().equals("q"))) {
                        serverSocket.close();
                        isRunning = false;
                        System.out.println("Server Closed");
                    }
                } catch (IOException e) {
                    errorInfo(e);
                }
            }
        }).start();
    }

    private void errorInfo(Exception e) {
        if(!(e instanceof SocketException)) {
            System.out.println("An error has occurred server is closing.");
            isRunning = false;
        }
    }
}
