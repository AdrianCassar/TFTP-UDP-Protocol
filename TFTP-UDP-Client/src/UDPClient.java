import java.io.*;
import java.net.*;

/**
 * A runnable UDP TFTP client.
 * Supports: read/write requests, acknowledgments, timeouts/re-transmission and error handling.
 */
public class UDPClient implements Runnable {

    private final int serverPort;
    private final InetAddress serverIP;

    public UDPClient() {
        this(InetAddress.getLoopbackAddress(), 10000);
    }

    public UDPClient(InetAddress serverIP, int serverPort) {
        System.out.println("Server: " + serverIP.getHostAddress() + ":" + serverPort + "\n");
//        System.out.println("Client: " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + "\n");

        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    /**
     * Starts the UDP TFTP client and waits until the user selects a to request.
     */
    @Override
    public void run() {
        String mainMenu = "1. Send File\n2. Retrieve File\n3. Quit";
        System.out.println(mainMenu);
        System.out.println();

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try {
            while (!(input = console.readLine().trim()).equals("3")) {
                if (input.equals("1") || input.equals("2")) {
                    System.out.println("Enter file name.");

                    String fileName = console.readLine().trim();

                    switch (input) {
                        case "1":
                            new WRQOperation(Opcode.WRQ, fileName, serverIP, serverPort).run();
                            break;
                        case "2":
                            new RRQOperation(Opcode.RRQ, fileName, serverIP, serverPort).run();
                            break;
                    }
                } else {
                    System.out.println("\nInvalid Option");
                }

                System.out.println();
                System.out.println(mainMenu);
                System.out.println();
            }

            System.out.println();
            System.out.println("Client Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

