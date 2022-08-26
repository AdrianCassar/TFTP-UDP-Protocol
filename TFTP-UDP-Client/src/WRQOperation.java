import java.net.*;
import java.io.IOException;

/**
 * This class is used to serve a write request
 * and is intended to be ran on a separate thread.
 */
public class WRQOperation extends Request {
    private boolean isRunning;
    private int retransmitCounter = 0;

    /**
     * @param req The type of request either WRQ or RRQ.
     * @param fileName The name of the file which is being sent or retrieved.
     * @param serverIP The servers IP address.
     * @param serverPort The servers port.
     * @throws IOException if an I/O error occurs.
     */
    public WRQOperation(Opcode req, String fileName, InetAddress serverIP, int serverPort) throws IOException {
        super(req, fileName, serverIP, serverPort);

        isRunning = DataHandler.fileExists(this.getFileName());

        if (!isRunning) {
            DataHandler.log("Error: " + fileName + " not found.");
        } else {
            this.incrementBlock();
        }
    }

    /**
     * Runs write request operation.
     */
    @Override
    public void run() {
        if(isRunning) {
            byte[] data;
            int dataPacketLen = 0;

            try {
                this.send(DataHandler.wrapBytes(DataHandler.req(Opcode.WRQ, this.getFileName()), this));

                do {
                    try {
                        Packet packet = this.receive();

                        if(packet instanceof ACKPacket) {
//                            DataHandler.loog("Data Block: " + this.getBlock());
                            data = DataHandler.data(this.getBlock(), this.getFileName());
                            DatagramPacket dataPacket = DataHandler.wrapBytes(data, this);
                            this.send(dataPacket);

                            dataPacketLen = dataPacket.getLength();

                            this.incrementBlock();
                        } else if(packet instanceof ErrorPacket) {
                            System.out.println("Error: " + ((ErrorPacket) packet).getErrMsg());

                            isRunning = false;
                        }
                    } catch (SocketTimeoutException e) {
                        if(retransmitCounter < 3) {
                            retransmitCounter++;

                            System.out.println("ACK Packet Response Timeout.");
                            System.out.println("Retransmitting last packet. Block No. " + this.getBlock());

                            dataPacketLen = DataHandler.dataLen + 4;
                        } else {
                            isRunning = false;
                        }
                    }
                } while (isRunning && !DataHandler.isLastPacket(dataPacketLen));

                if(isRunning) {
                    DataHandler.log("Write Request Completed.\n");
                } else {
                    DataHandler.log("Write Request Failed.\n");
                }
            } catch (IOException e) {
                DataHandler.log("Write Request Failed.\n");
                isRunning = false;
            }
        } else {
            DataHandler.log("Write Request Failed.\n");
        }
    }
}
