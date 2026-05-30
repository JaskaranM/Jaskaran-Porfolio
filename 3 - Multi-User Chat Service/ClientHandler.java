import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;

public class ClientHandler implements Runnable {

    // tracks all connected clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; // reads messages from clients
    private BufferedWriter bufferedWriter; // sends messages to clients
    private final TokenRateLimiter messageRateLimiter = new TokenRateLimiter(5, Duration.ofSeconds(1));

    // runs when a client connects to the server
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientHandlers.add(this);

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    // runs as long as a client is connected
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                // thread pauses until client passes a message
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient == null) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                if (messageRateLimiter.isAllowed()) {
                    broadcastMessage(messageFromClient);
                } else {
                    // tells the client that their message wasn't sent
                    this.bufferedWriter.write("Rate limit exceeded, message not sent. Please try again later.");
                    this.bufferedWriter.newLine();
                    this.bufferedWriter.flush();
                }

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // broadcasts messages to all connected clients
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : new ArrayList<>(clientHandlers)) {
            try {
                // sends to every client except the sender
                if (clientHandler != this) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                // client closed if it can't be reached
                clientHandler.closeEverything(clientHandler.socket,
                        clientHandler.bufferedReader,
                        clientHandler.bufferedWriter);
            }
        }
    }

    // removes current handler instance from the clientHandler list
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("[Server]: A client has left ");
    }

    // called to all aspects of a client to improve efficiency
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
