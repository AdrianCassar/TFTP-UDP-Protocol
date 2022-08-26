import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Represents an error packet.
 */
public class ErrorPacket extends Packet {
    private final String errMsg;
    private final ErrorCodes errCode;

    /**
     * @param data The datagram containing the error packet.
     * @throws IOException if an I/O error occurs.
     */
    public ErrorPacket(DatagramPacket data) throws IOException {
        super(data);

        errCode = ErrorCodes.fromInteger(dataIn.readShort());
        errMsg = readString();
    }

    /**
     * @return The error message.
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * @return The type of error.
     */
    public ErrorCodes getErrCode() {
        return errCode;
    }
}
