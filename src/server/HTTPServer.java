package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPServer {

    private static final Logger logger = Logger.getLogger(HTTPServer.class.getCanonicalName());

    private static final int NUM_THREADS = 50;
    private RequestScheduler requestScheduler;

    private final String rootPath;
    private final int port;
    private volatile boolean running = true;
    private ConnectionManager connectionManager = null;
    /**
     * Create an HTTPServer.
     *
     * @param rootPath  The root directory for the server's filesystem '/'.
     * @param port           The port number the server will use.
     *
     * @throws IllegalArgumentException if rootPath is not a directory.
     */
    public HTTPServer(String rootPath, int port) {
        this.rootPath = rootPath;
        this.port = port;
    }

    /**
     * Start the HTTPServer.
     * Creates a serverSocket to accept connections at the port given at creation.
     */
    public void start()  {
        this.requestScheduler = new PooledRequestScheduler(NUM_THREADS);
        try {
            this.connectionManager = new ConnectionManager(port);
            logger.log(Level.INFO, "Accepting connections on port: " + port);
            listenForConnections();
        } catch (IOException e) {
            logger.log(Level.INFO, "Server failed to start.");
        }
        connectionManager.close();
    }

    private void listenForConnections() {
        while (running) {
            try {
                ClientConnection clientConnection = connectionManager.acceptConnection();
                RequestProcessor requestProcessor = new RequestProcessor(clientConnection, rootPath);
                requestScheduler.schedule(requestProcessor);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error accepting connection request.");
                e.printStackTrace();
            }
        }
    }


}
