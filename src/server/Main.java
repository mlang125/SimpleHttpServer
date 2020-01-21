package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String CWD = System.getProperty("user.dir");

    private static String rootPath;
    private static int port;

    private static final String DEFAULT_PROPERTIES_FILE = "default.properties";
    protected static Properties properties = new Properties();

    public static void main(String[] args) {
        initProperties();
        initServerFileSystem();

        HTTPServer httpServer = new HTTPServer(rootPath, port);
        logger.log(Level.INFO, "Attempting to start server...");
        logger.log(Level.INFO, "Server Root Path:\t" + rootPath);
        logger.log(Level.INFO, "Server Port Number:\t" + port);

        httpServer.start();
    }

    /*
     * Create and set a Properties object configured with the server's properties
     * file and the properties of the system running the server.
     */
    public static void initProperties() {
        String propertiesPath = CWD + File.separator + DEFAULT_PROPERTIES_FILE;
        try {
            properties.load(new FileInputStream(propertiesPath));
            properties.putAll(System.getProperties());
            properties.put("user.dir", CWD);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to initialize properties: Properties file not found", e);
            System.exit(1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize properties: IOException", e);
            System.exit(1);
        }

        /* Set the webRoot and port variables */
        port = Integer.parseInt(properties.getProperty("port"));

        String rootDirectoryName = properties.getProperty("webroot");
        rootPath = Paths.get(CWD, rootDirectoryName).toString();
    }

    /* Creates the server's host directories if they do not already exist. */
    private static void initServerFileSystem() {
        File rootDirectory = new File(rootPath);
        /* Check if web directory is already initialized */
        if (rootDirectory.exists()) {
            logger.log(Level.INFO, "Server Directory exists.");
            boolean deleted = rootDirectory.delete();
            if (deleted) {
                logger.log(Level.INFO, "Server Directory deleted");
            } else {
                logger.log(Level.INFO, "Server Directory was not deleted");
            }
        }
        try {
            logger.log(Level.INFO, "Creating server directory");
            boolean created = rootDirectory.mkdir();
            if (created) {
                logger.log(Level.INFO, "Created web root directory.");
                createHostDirectories();
                writeIndexHtmlFile(rootPath);
            }

        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Failed to create web root directory... Exiting.", e);
            System.exit(1);
        }

    }

    /*
     * Creates the host directories within the web root directory if not already
     * there.
     */
    public static void createHostDirectories() {
        List<String> hosts = null;
        try {
            hosts = Files.readAllLines(Paths.get(CWD, properties.getProperty("hostnames")));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read lines from hostnames file", e);
            System.exit(1);
        }

        for (String host : hosts) {
            String hostPath = Paths.get(rootPath, host).toString();
            boolean created = new File(hostPath).mkdir();
            if (created) {
                logger.log(Level.INFO, "Created web root directory.");
                writeIndexHtmlFile(hostPath);
            }
        }
    }

    private static void writeIndexHtmlFile(String directoryPath) {
        String indexPath = Paths.get(directoryPath, "index.html").toString();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(indexPath), "utf-8"))) {
            writer.write("something" + directoryPath.toString());
        } catch (Exception e) {
            logger.log(Level.INFO, "Could not write index.html at path: " + directoryPath);
        }

    }


}
