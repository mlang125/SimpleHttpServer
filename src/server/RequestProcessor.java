package server;

import utils.ResponseStatus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProcessor {
    private static final Logger logger = Logger.getLogger(RequestProcessor.class.getCanonicalName());
    private ClientConnection clientConnection;
    private String rootPath;

    public RequestProcessor(ClientConnection clientConnection, String rootPath) {
        this.clientConnection = clientConnection;
        this.rootPath = rootPath;
    }

    public void process() {
        clientConnection.setRootPath(rootPath);
        clientConnection.readRequest();
        clientConnection.close();
    }

}
