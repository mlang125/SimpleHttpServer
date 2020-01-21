package utils;

/* @Documented
 *
 */
public enum ResponseStatus {
    STATUS_NOT_SET			      (0, ""),
    OK            				  (200, "OK"),
    NOT_MODIFIED 	              (304, "Not Modified"),
    BAD_REQUEST  			      (400, "Bad Request"),
    FORBIDDEN					(403, "Forbidden"),
    NOT_FOUND                    (404, "Not Found"),
    METHOD_NOT_ALLOWED			  (405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR       (500, "Internal Server Error"),
    METHOD_NOT_IMPLEMENTED      (501, "Not Implemented"),
    HTTP_VERSION_NOT_SUPPORTED  (505, "HTTP Version not supported");

    private int responseCode;
    private String responsePhrase;

    ResponseStatus(final int responseCode, final String responsePhrase) {
        this.responseCode = responseCode;
        this.responsePhrase = responsePhrase;
    }

    public int getCode() {
        return this.responseCode;
    }

    public String getPhrase() {
        return this.responsePhrase;
    }

    public boolean equals(ResponseStatus other) {
        return this.responseCode == other.responseCode;
    }

    public String toString() {
        return this.responseCode + " " + this.responsePhrase;
    }
}
