import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final int PORT = 8080;
    private static final String SERVER_SERVICE_NAME = System.getenv("SERVER_SERVICE_NAME");

    static {
        try {
            // Ensure the directory exists
            File logDir = new File("/app/logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            FileHandler fileHandler = new FileHandler("/app/logs/client.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to set up logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (SERVER_SERVICE_NAME == null) {
            LOGGER.severe("Environment variable SERVER_SERVICE_NAME is not set.");
            return;
        }

        Scanner userInput = new Scanner(System.in);

        while (true) { // Continuous loop for interactive input
            try {
                InetAddress host = InetAddress.getByName(SERVER_SERVICE_NAME);

                try (Socket socket = new Socket(host, PORT);
                     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                    LOGGER.info("Connection established with the server.");
                    System.out.println("Enter request to server:");
                    String condition = userInput.nextLine();

                    if ("exit".equalsIgnoreCase(condition)) {
                        break; // Exit the loop on "exit" input
                    }

                    oos.writeObject(condition);
                    String message = (String) ois.readObject();
                    LOGGER.info("Received response from server: " + message);
                    System.out.println("Message: " + message);
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "An error occurred during communication with the server: ", e);
                }
            } catch (UnknownHostException e) {
                LOGGER.log(Level.SEVERE, "Unknown host: " + SERVER_SERVICE_NAME, e);
            }
        }
        userInput.close();
    }
}

