package parag.LRUCache.exception;

public class RetrievalException extends Exception {

    private static final long serialVersionUID = 3020670648617043042L;

    /**
     * Constructor. Initializes this exception as a wrapper for another exception, with an explanatory message.
     *
     * @param msg the explanatory message
     * @param wrappedException the exception being wrapped
     */
    public RetrievalException(String msg, Throwable wrappedException) {
        super(msg, wrappedException);
    }

    /**
     * Constructor. Initializes this exception as a wrapper for another exception.
     *
     * @param wrappedException the exception being wrapped
     */
    public RetrievalException(Throwable wrappedException) {
        super(wrappedException);
    }

    /**
     * Initializes this exception with message
     *
     * @param msg
     */
    public RetrievalException(String msg) {
        super(msg);
    }

}
