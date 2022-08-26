import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to serve a read request
 * and is intended to be ran on a separate thread.
 */
public class RRQOperation extends Request {
    private boolean isRunning;
    private int retransmitCounter = 0;

    /**
     * @param req The type of request either WRQ or RRQ.
     * @param fileName The name of the file which is being sent or retrieved.
     * @param serverIP The servers IP address.
     * @param serverPort The servers port.
     * @throws IOException if an I/O error occurs.
     */
    public RRQOperation(Opcode req, String fileName, InetAddress serverIP, int serverPort) throws IOException {
        super(req, fileName, serverIP, serverPort);

        isRunning = true;

        if (DataHandler.fileExists(this.getFileName())) {
            try {
                Files.delete(Paths.get(this.getFileName()));
            } catch (IOException e) {
                System.out.println("Could not delete file: " + fileName);
                isRunning = false;
            }
        }
    }

    /**
     * Runs read request operation.
     */
    @Override
    public void run() {
        if(isRunning) {
            try {
                int dataPacketLen = 0;

                DataHandler.log("Sent RRQ");
                this.send(DataHandler.wrapBytes(DataHandler.req(Opcode.RRQ, this.getFileName()), this));

                do {
                    try {
                        Packet packet = this.receive();

                        if (packet instanceof DataPacket) {
                            DataPacket dataPacket = (DataPacket) packet;
                            DataHandler.saveFile(this.getFileName(), dataPacket);

                            dataPacketLen = dataPacket.getLength();

                            sendACK();

                            this.incrementBlock();
                        } else if(packet instanceof ErrorPacket) {
                            System.out.println("Server: " + ((ErrorPacket) packet).getErrMsg());

                            isRunning = false;
                        }
                    } catch (SocketTimeoutException e) {
                        if(retransmitCounter < 3) {
                            retransmitCounter++;

                            System.out.println("Data Packet Response Timeout.");
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
        }  else {
            DataHandler.log("Read Request Failed.\n");
        }
    }

    private void sendACK() throws IOException {
//        DataHandler.log("RRQ Acknowledged: " + this.getBlock());

        this.send(DataHandler.wrapBytes(DataHandler.ack(this.getBlock()), this));
    }
}
