package tgb.cryptoexchange.ticket.exception;

/**
 * Пробрасывается в случае сохранения TicketReply с несуществующим Ticket.
 */
public class TicketReplyException extends RuntimeException {

    public TicketReplyException(String message) {
        super(message);
    }
}
