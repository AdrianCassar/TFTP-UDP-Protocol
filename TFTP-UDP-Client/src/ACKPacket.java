import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Represents an ACK packet.
 */
public class ACKPacket extends Packet {
    private final int block;

    /**
     * @param data The datagram containing the ACK packet.
     * @throws IOException if an I/O error occurs.
     */
    public ACKPacket(DatagramPacket data) throws IOException {
        super(data);

        this.block = dataIn.readShort();
    }

    /**
     * @return The block number for this ACK packet.
     */
    public int getBlock() {
        return block;
    }
}
