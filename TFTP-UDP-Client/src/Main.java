import java.io.IOException;
import java.net.InetAddress;

public class Main {

    /**
     * Starts the server on the main thread.
     *
     * @param args
     * Default IP address is the loopback address/127.0.0.1, otherwise use custom IP.
     * Default port is 10000, otherwise use custom port.
     */
    public static void main(String[] args) throws IOException {
        if(args.length == 1 || args.length > 2) {
            System.out.println("java -jar TFTP-UDP-Client.jar\njava -jar TFTP-UDP-Client.jar ServerIP ServerPort\n");
        } else if(args.length == 2) {
            InetAddress serverIP = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);

            new UDPClient(serverIP, serverPort).run();
        } else {
            new UDPClient().run();
        }
    }
}