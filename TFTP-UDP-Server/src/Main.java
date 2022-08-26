import java.net.SocketException;

public class Main {

    /**
     * Starts the server on the main thread.
     *
     * @param args Default port is 10000, otherwise use custom port.
     */
    public static void main(String[] args) {
        try {
            if(args.length > 1) {
                System.out.println("java -jar TFTP-UDP-Server.jar\njava -jar TFTP-UDP-Server.jar ServerPort\n");
            } else if(args.length == 1) {
                int serverPort = Integer.parseInt(args[0]);

                new TFTPServer(serverPort).run();
            } else {
                new TFTPServer().run();
            }
        } catch (SocketException e) {
            System.out.println("Port 10000 is already in use.");
        }
    }
}
