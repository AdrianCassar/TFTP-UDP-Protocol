import java.io.*;
import java.net.DatagramPacket;

/**
 * A generic class that contains all the core features of a packet.
 * This class is intended to be inherited, but can be used on its own.
 */
public class Packet {
    private Opcode packetType;
    private final int length;

    private final DatagramPacket packet;

    protected ByteArrayInputStream byteArray;
    protected DataInputStream dataIn;

    /**
     * @param packet The datagram containing the packet.
     * @throws IOException if an I/O error occurs.
     */
    public Packet(DatagramPacket packet) throws IOException {
        this.packet = packet;

        length = packet.getLength();

        byteArray = new ByteArrayInputStream(packet.getData(), 0 , length);
        dataIn = new DataInputStream(byteArray);

        int packetType = dataIn.readShort();

        if (packetType < 0 || packetType > 5) {
            System.out.println("Invalid Packet type detected.");
            this.packetType = Opcode.Invalid;
        } else {
            this.packetType = Opcode.fromInteger(packetType);
        }
    }

    /**
     * @return The packet type.
     */
    public Opcode getPacketType() {
        return packetType;
    }

    /**
     * @return The next string within the packet.
     * @throws IOException if an I/O error occurs.
     */
    protected String readString() throws IOException {
        String strOut;

        try(ByteArrayOutputStream stringOut = new ByteArrayOutputStream()) {
            int val;
            while ((val = dataIn.read()) != 0 && val != -1) {
                stringOut.write(val);
            }

            strOut = stringOut.toString();
        }

        return strOut;
    }

    /**
     * @return  The length of this packet.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return The original datagram packet which this instance represents.
     */
    public DatagramPacket getPacket() {
        return packet;
    }
}
