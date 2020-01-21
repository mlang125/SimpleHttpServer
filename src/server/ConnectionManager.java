package server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager implements Closeable {

    private int port;
    private ServerSocket serverSocket = null;


    public ConnectionManager(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(this.port);
    }

    public ClientConnection acceptConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        return new ClientConnection(clientSocket);
    }

    public void closeConnection() {

    }

    @Override
    public void close() {
        if (null != serverSocket) {
            try {
                serverSocket.close();
            } catch (IOException e) {

            }
        }

    }

}
