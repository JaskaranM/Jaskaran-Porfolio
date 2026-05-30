import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

    private ServerSocket serverSocket;

    private static final String KEYSTORE_PATH = "ClientKeyStore.jks";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final int PORT = 1234;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                // waits for a client to connect
                Socket socket = serverSocket.accept();
                System.out.println("New Client has connected");
                // hands over socket to the clientHandler
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            closeServerSocket();
            System.err.println("Server had an error or was closed");
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // keystore is set up
        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
        // chose TLS 1.3 specifically
        sslServerSocket.setEnabledProtocols(new String[] { "TLSv1.3" });

        System.out.println("Secure Server started on port " + PORT);

        Server server = new Server(sslServerSocket);
        server.startServer();
    }

}
