import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Represents a data packet.
 */
public class DataPacket extends Packet {
    private final byte[] data;
    private final int block;

    /**
     * @param data  The datagram containing the data packet.
     * @throws IOException if an I/O error occurs.
     */
    public DataPacket(DatagramPacket data) throws IOException {
        super(data);

        block = dataIn.readShort();

        try(ByteArrayOutputStream dataOut = new ByteArrayOutputStream()) {
            int val;
            while ((val = dataIn.read()) != -1) {
                dataOut.write(val);
            }

            this.data = dataOut.toByteArray();
        }
    }

    /**
     * @return An array of bytes containing the data for the data packet.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return The block number for this Data packet.
     */
    public int getBlock() {
        return block;
    }
}
