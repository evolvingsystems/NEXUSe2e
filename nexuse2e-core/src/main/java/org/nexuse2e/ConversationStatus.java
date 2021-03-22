package org.nexuse2e;

public enum ConversationStatus {
    ERROR(-1), UNKNOWN(0), CREATED(1), PROCESSING(2), AWAITING_ACK(3), IDLE(4),
    SENDING_ACK(5), ACK_SENT_AWAITING_BACKEND(6), AWAITING_BACKEND(7),
    BACKEND_SENT_SENDING_ACK(8), COMPLETED(9);

    int ordinal = 0;

    ConversationStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public static ConversationStatus getByOrdinal(int ordinal) {
        if (-1 <= ordinal) {
            for (ConversationStatus oneType : ConversationStatus.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid ConversationStatus!");
    }
}
