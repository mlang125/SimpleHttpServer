package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * @author Matt
 *
 */

public class Request {

    /* Request Header Fields */
    public static final String	    HOST	  = "Host";
    public static final String	    IF_MODIFIED_SINCE = "If-Modified-Since";

    private static final int 		INDEX_METHOD = 0;
    private static final int 		INDEX_REQUEST_URI = 1;
    private static final int 		INDEX_VERSION = 2;

    private HashMap<String, String> headers;
    private Method					method;

    private String 					requestURI;
    private String 					version;
    private Protocol				protocol;
    private String 					messageBody;
    private boolean 				existsMessageBody;


    public Request(InputStream in) throws IOException {
        this.parse(in);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * Get/Set Methods * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Method getMethod() { return this.method;	}

    public String getRequestURI() {
        return this.requestURI;
    }
    public String getVersion() {
        return this.version;
    }
    public Protocol getProtocol() { return this.protocol; }
    public String getHeader(String key) {
        return headers.get(key);
    }
    public boolean existsMessageBody() {
        return this.existsMessageBody;
    }
    public String getMessageBody() {
        return this.existsMessageBody ? this.messageBody : null;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        parseRequestLine(reader);
        parseRequestHeaders(reader);
        parseRequestBody(reader);
    }

    /*
     * Sets the header-line fields.
     * @param reader
     * 		The wrapped InputStream from client.
     * 		pre: pointer is set to first character.
     */
    private void parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine[] = (reader.readLine()).split(" ");
        this.method = Method.valueOf(requestLine[INDEX_METHOD]);
        this.requestURI = requestLine[INDEX_REQUEST_URI];
        this.version = requestLine[INDEX_VERSION];
        this.protocol = Protocol.fromHeader(requestLine[INDEX_VERSION]);
    }

    /*
     * Parses the HTTP Request header fields. If there are no IOExceptions, then
     * BufferedReader has been read until the CRLF. If there is no message body,
     * then the next call to readLine() should be null;
     */
    private void parseRequestHeaders(BufferedReader reader) throws IOException {
        this.headers = new HashMap<>();

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.equals("")) {
            int keyEndIndex = headerLine.indexOf(":");
            int valueStartIndex = keyEndIndex + 1;
            String key = headerLine.substring(0, keyEndIndex);
            String value = headerLine.substring(valueStartIndex, headerLine.length()).trim();

            headers.put(key, value);
        }
    }

    /* Obtains the message body if it exists */
    private void parseRequestBody(BufferedReader reader) throws IOException {
        String line = null;
        if (!reader.ready()) {
            this.messageBody = null;
            this.existsMessageBody = false;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(line);
            while ((line = reader.readLine()) != null && !line.equals("")) {
                sb.append(line);
            }
            this.existsMessageBody = true;
            this.messageBody = sb.toString();
        }
    }
}
