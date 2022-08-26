/*
  opcode  operation
    1     Read request (RRQ)
    2     Write request (WRQ)
    3     Data (DATA)
    4     Acknowledgment (ACK)
    5     Error (ERROR)
*/

/**
 * TFTP header opcodes used to identify the packet type.
 */
public enum Opcode {
    RRQ(1), WRQ(2), DATA(3), ACK(4), Error(5), Invalid(6);

    private final int value;

    Opcode(int value) {
        this.value = value;
    }

    /**
     * @return Opcode
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value The Opcode corresponding integer value.
     * @return  The corresponding Opcode code enum.
     */
    public static Opcode fromInteger(int value) {
        return switch (value) {
            case 1 -> RRQ;
            case 2 -> WRQ;
            case 3 -> DATA;
            case 4 -> ACK;
            case 5 -> Error;
            default -> Invalid;
        };
    }
}
