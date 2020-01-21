package server;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.*;

public class ClientConnection implements Closeable {
    private static final Logger logger = Logger.getLogger(ClientConnection.class.getCanonicalName());
    private Socket clientSocket;
    private Request request = null;

    private ResponseWriter responseWriter;
    private Response response = new Response();
    private boolean valid = false;

    private String rootPath;
    private String hostPath;


    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void close() {
        try {
            this.clientSocket.setKeepAlive(false);
            this.clientSocket.close();
            logger.log(Level.INFO, "closed: " + this.clientSocket.isClosed());

        } catch (IOException e) {
            logger.log(Level.INFO, "Error closing client");
        }

    }

    public void readRequest() {
        try {
            request = new Request(clientSocket.getInputStream());
            responseWriter = new ResponseWriter(clientSocket.getOutputStream());
            validateAndReply();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while processing request from client: " + clientSocket.getInetAddress());
        }
    }

    private void validateAndReply() throws IOException {
        validateHeaders();
        if (responseWriter.isStatusSet()) {
            responseWriter.writeHeader();
        } else {
            validateMethod();
        }

    }

    private void validateHeaders() throws IOException {
        if (request.getProtocol().equals(Protocol.HTTP_1_1)) {
            validateHttp11Resource();
        } else {
            hostPath = rootPath;
        }
    }

    private void validateHttp11Resource() throws IOException {
        String host = request.getHeader("Host");
        if (null == host) {
            responseWriter.setStatus(ResponseStatus.BAD_REQUEST);
        } else {
            if (!isValidHostDirectory(host)) {
                responseWriter.setStatus(ResponseStatus.NOT_FOUND);
            }
        }
    }


    private void validateMethod() throws IOException {
        Method method = request.getMethod();

        if (method.equals(Method.GET)) {
            doGet();
        } else if (method.equals(Method.POST)) {
            doPost();
        } else {
            responseWriter.writeHeader(ResponseStatus.METHOD_NOT_IMPLEMENTED);
        }
    }

    private void doGet() throws IOException {
        String uri = request.getRequestURI();
        uri = uri.endsWith("/") ? uri + "index.html" : uri;
        File requestFile = new File(hostPath, uri.substring(1, uri.length()));

        /* Check if requestFile is outside of root directory */

        ResponseWriter responseWriter = new ResponseWriter(clientSocket.getOutputStream());
        if (!(requestFile.canRead() && requestFile.getCanonicalPath().startsWith(hostPath))) {
            responseWriter.writeHeader(ResponseStatus.NOT_FOUND);
        } else {
            /* Check for If-Modified-Since. (Only send header if T) */
            String requestLastModified = request.getHeader(Request.IF_MODIFIED_SINCE);
            if (null != requestLastModified) {
                long requestTimeModified = DateParser.parseDateHeader(requestLastModified);
                long timeModified = requestFile.lastModified();

                if (timeModified == requestTimeModified) {
                    responseWriter.writeHeader(ResponseStatus.NOT_MODIFIED);
                } else {
                    responseWriter.writeHeader(ResponseStatus.OK);
                    responseWriter.writeBody(requestFile);
                }
            } else {
                responseWriter.writeHeader(ResponseStatus.OK);
                responseWriter.writeBody(requestFile);
            }
        }


    }

    private void doPost() throws IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/")) {
            responseWriter.writeHeader(ResponseStatus.BAD_REQUEST);
        } else {

            File requestFile = new File(hostPath, uri.substring(1, uri.length()));

            /* Check if requestFile is outside of root directory */
            if (!requestFile.getCanonicalPath().startsWith(hostPath)) {
                responseWriter.writeHeader(ResponseStatus.NOT_FOUND);
            } else {
                if (!requestFile.canWrite()) {
                    responseWriter.writeHeader(ResponseStatus.FORBIDDEN);
                } else {
                    FileWriter fileWriter = new FileWriter(requestFile, false);
                    fileWriter.write(request.getMessageBody());
                    responseWriter.writeHeader(ResponseStatus.OK);
                }
            }
        }
    }


    private boolean isValidHostDirectory(String host) {
        if (null == host) {
            throw new IllegalArgumentException("host cannot be null.");
        }

        if (host.length() > 0) {
            File file = new File(rootPath, host);
            try {
                if (file.canRead()
                        && file.getCanonicalPath().startsWith(rootPath)) {
                    this.hostPath = file.getPath();
                }
            } catch (IOException e) {
                return false;
            }
        } else {
            hostPath = rootPath;
        }
        return true;
    }


}
