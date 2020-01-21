package server;

import utils.DateParser;
import utils.ResponseStatus;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.Date;

public class Response {
    private static final String RESPONSE_LINE = "HTTP/1.1 %d %s\r\n";
    private static final String DATE = "Date: %s\r\n";
    public static final String SERVER = "NoSleepForGPA\r\n";

    private static final String CONTENT_LENGTH = "Content-Length: %d\r\n";
    public static final String CHARSET = "charset=ISO-8859-1\r\n";                /* For returning */
    private static final String NEWLINE = "\r\n";


    private ResponseStatus responseStatus;
    private Date lastModified = null;
    private int contentLength = 0;
    private String body = null;

    public Response() {
        this.responseStatus = ResponseStatus.STATUS_NOT_SET;
    }

    public Response(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setMessageBody(String body) {
        this.body = body;
        this.contentLength = null == this.body ? 0 : this.body.length();
        ;
    }


    public String getResponse() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(RESPONSE_LINE, responseStatus.getCode(), responseStatus.getPhrase()));
        sb.append(String.format(DATE, DateParser.getDateHeader()));

        if (this.contentLength > 0) {
            sb.append(String.format(CONTENT_LENGTH, this.contentLength));
            sb.append(CHARSET);
        }
        sb.append(NEWLINE);
        if (null != body) {
            sb.append(body);
        }

        return sb.toString();
    }


}
