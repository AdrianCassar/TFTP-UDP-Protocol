//Error Codes
//Value Meaning
//0 Not defined, see error message (if any).
//1 File not found.
//2 Access violation.
//3 Disk full or allocation exceeded.
//4 Illegal TFTP operation.
//5 Unknown transfer ID.
//6 File already exists.
//7 No such user.

/**
 * Type of TFTP error codes.
 */
public enum ErrorCodes {
    NotDefined(0), NotFound(1);

    private final int value;

    ErrorCodes(int value) {
        this.value = value;
    }

    /**
     * @return The corresponding error code enum.
     */
    public int getValue() {
        return value;
    }

    public static ErrorCodes fromInteger(int value) {
        switch(value) {
            case 0:
                return NotDefined;
            case 1:
                return NotFound;
            default:
                return NotDefined;
        }
    }
}
