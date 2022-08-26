import java.net.*;
import java.io.IOException;

/**W
 * This class is used to serve a read request
 * and is intended to be ran on a separate thread.
 */
public class RRQOperation extends Request {
    private boolean isRunning;
    private int retransmitCounter = 0;

    /**
     * @param packet Instance of an unpacked datagram for a WRQ or RRQ operation.
     * @throws IOException if an I/O error occurs.
     */
    public RRQOperation(ReqPacket packet) throws IOException {
        super(packet);

        isRunning = DataHandler.fileExists(this.getFileName());

        if (!isRunning) {
            DataHandler.log("Error Packet Sent.");
            String msg = packet.getFilename() + " not found.";
            DataHandler.log("Error: " + msg);
            this.send(DataHandler.wrapBytes(DataHandler.error(ErrorCodes.NotFound, msg), this));
        }
    }

    /**
     * Runs read request operation.
     */
    @Override
    public void run() {
        if(isRunning) {
            try {
                byte[] data;
                int dataPacketLen;

                do {
//                    DataHandler.log("Data Block: " + this.getBlock());
                    data = DataHandler.data(this.getBlock(), this.getFileName());
                    DatagramPacket dataPacket = DataHandler.wrapBytes(data, this);
                    this.send(dataPacket);

                    dataPacketLen = dataPacket.getData().length;

                    Packet packet;
                    
                    try {
                        packet = this.receive();

                        if(packet instanceof ACKPacket) {
                            this.incrementBlock();
                        } else if(packet instanceof ErrorPacket) {
                            System.out.println("Error: " + ((ErrorPacket) packet).getErrMsg());

                            isRunning = false;
                        }
                    } catch (SocketTimeoutException e) {
                        if(retransmitCounter < 3) {
                            retransmitCounter++;

                            System.out.println("ACK Response Timeout.");
                            System.out.println("Retransmitting last packet. Block No. " + this.getBlock());

                            dataPacketLen = DataHandler.dataLen + 4;
                        } else {
                            isRunning = false;
                        }
                    }
                } while (isRunning && !DataHandler.isLastPacket(dataPacketLen));

                if(isRunning) {
                    DataHandler.log("Read Request Completed.\n");
                } else {
                    DataHandler.log("Read Request Failed.\n");
                }
            } catch (IOException e) {
                DataHandler.log("Read Request Failed.\n");

                isRunning = false;
            }
        }
    }
}
