import java.io.IOException;
import java.net.DatagramPacket;

/**
 * This class represents a request packet either RRQ or WRQ.
 */
public class ReqPacket extends Packet {

    private final String filename;
    private final String mode;

    /**
     * @param data The datagram containing the WRQ or RRQ packet.
     * @throws IOException if an I/O error occurs.
     */
    public ReqPacket(DatagramPacket data) throws IOException {
        super(data);

        filename = readString();
        mode = readString();
    }

    /**
     * @return The name of the file which is being sent or retrieved.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return The mode which the packet was went in.
     */
    public String getMode() {
        return mode;
    }
}
