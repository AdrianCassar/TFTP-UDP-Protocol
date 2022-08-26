import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to serve a write request
 * and is intended to be ran on a separate thread.
 */
public class WRQOperation extends Request {
    private boolean isRunning;
    private int retransmitCounter = 0;

    /**
     * @param packet Instance of an unpacked datagram for a WRQ or RRQ operation.
     * @throws IOException if an I/O error occurs.
     */
    public WRQOperation(ReqPacket packet) throws IOException {
        super(packet);

        isRunning = true;

        if (DataHandler.fileExists(this.getFileName())) {
            try {
                Files.delete(Paths.get(this.getFileName()));
            } catch (IOException e) {
                System.out.println("Could not delete file: " + packet.getFilename());
                isRunning = false;
            }
        }
    }

    /**
     * Runs write request operation.
     */
    @Override
    public void run() {
        try {
            int dataPacketLen = 0;

            if (isRunning) {
                do {
                    sendACK();

                    try {
                        Packet packet = this.receive();

                        if (packet instanceof DataPacket) {
                            DataPacket dataPacket = (DataPacket) packet;
                            dataPacketLen = dataPacket.getLength();
                            DataHandler.saveFile(this.getFileName(), dataPacket);

                            this.incrementBlock();
                        } else if (packet instanceof ErrorPacket) {
                            System.out.println("Client: " + ((ErrorPacket) packet).getErrMsg());

                            isRunning = false;
                        }
                    } catch (SocketTimeoutException e) {
                        if(retransmitCounter < 3) {
                            retransmitCounter++;

                            System.out.println("Write Request Timeout.");
                            System.out.println("Retransmitting last packet. Block No. " + this.getBlock());

                            dataPacketLen = DataHandler.dataLen + 4;
                        } else {
                            isRunning = false;
                        }
                    }
                } while (isRunning && !DataHandler.isLastPacket(dataPacketLen));
            }

            if (isRunning) {
                DataHandler.log("Write Request Completed.\n");
            } else {
                DataHandler.log("Write Request Failed.\n");
            }
        } catch (IOException e) {
            DataHandler.log("Write Request Failed.\n");
            isRunning = false;
        }
    }

    /**
     * Sends ACK packet for the current block.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void sendACK() throws IOException {
//        DataHandler.log("WRQ Acknowledged: " + this.getBlock());

        this.send(DataHandler.wrapBytes(DataHandler.ack(this.getBlock()), this));
    }
}
