package server;

import utils.DateParser;
import utils.ResponseStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseWriter {
    private OutputStream outputStream;
    private ResponseStatus responseStatus = ResponseStatus.STATUS_NOT_SET;
    private boolean statusSet = false;

    public ResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setStatus(ResponseStatus responseStatus) {
        if (this.responseStatus.equals(ResponseStatus.STATUS_NOT_SET)) {
            this.responseStatus = responseStatus;
            statusSet = true;
        } else {
            throw new IllegalStateException("Status has already been set.");
        }
    }


    public boolean isStatusSet() {
        return statusSet;
    }

    public void writeHeader(ResponseStatus responseStatus) throws IOException {
        setStatus(responseStatus);
        writeHeader();
    }

    public void writeHeader() throws IOException {
        if (!statusSet) {
            throw new IllegalArgumentException("Status must be set before calling writeHeader().");
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(responseStatus.toString() + "\r\n");
        writer.write("Date: " + DateParser.getDateHeader() + "\r\n");
        writer.write("Server: Basic HTTP Server 1.1\r\n");
        writer.write("Connection: close\r\n");
        writer.flush();
    }

    public void writeBody(File file) throws IOException {
        writeContentLength(file.length());
        Path path = file.toPath();
        Files.copy(path, outputStream);
        outputStream.flush();

    }

    private void writeContentLength(long length) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write("Content-Length: " + length + "\r\n");
        writer.write("\r\n");
        writer.flush();
    }
}

