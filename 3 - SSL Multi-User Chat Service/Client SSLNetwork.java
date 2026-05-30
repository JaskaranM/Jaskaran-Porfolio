import javax.net.ssl.*;
import java.io.*;
import java.util.Scanner;

public class ClientSSLNetwork {
	public static final boolean DEBUG = false;
	public static final int HTTPS_PORT = 1234;
	public static final String HTTPS_HOST = "localhost";
	public static final String TRUSTSTORE_LOCATION = "ClientKeyStore.jks";
	public static final String TRUSTSTORE_PASSWORD = "password";

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_LOCATION);
		System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);

		if (DEBUG) {
			System.setProperty("javax.net.debug", "all");
		}
		SSLSocket client = null;
		BufferedWriter w = null;
		BufferedReader r = null;
		Scanner scanner = new Scanner(System.in);

		try {
			String name;
			System.out.println("Enter your name: ");
			name = scanner.nextLine();
			while (name.isEmpty()) {
				System.out.println("Can't be empty.Enter a username");
				name = scanner.nextLine();
			}

			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			client = (SSLSocket) factory.createSocket(HTTPS_HOST, HTTPS_PORT);

			client.startHandshake();

			w = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			r = new BufferedReader(new InputStreamReader(client.getInputStream()));

			Thread receiverThread = new Thread(new MessageReceiver(r));
			receiverThread.start();

			while (true) {
				String userInput = scanner.nextLine();
				String formattedMessage = name + ": " + userInput;
				w.write(formattedMessage);
				w.newLine();
				w.flush();
			}

		} catch (IOException e) {
			System.err.println("Error connecting  to " + HTTPS_HOST);
		} finally {
			closeEverything(client, r, w, scanner);
		}
	}

	static class MessageReceiver implements Runnable {

		private BufferedReader r;

		public MessageReceiver(BufferedReader r) {
			this.r = r;
		}

		public void run() {
			try {
				String message;
				while ((message = r.readLine()) != null) {
					System.out.println(message);
				}
			} catch (IOException e) {
				System.out.println("Connection closed.");
			}
		}
	}

	public static void closeEverything(SSLSocket client, BufferedReader r, BufferedWriter w, Scanner scanner) {
		try {
			if (r != null)
				r.close();
			if (w != null)
				w.close();
			if (client != null)
				client.close();
			if (scanner != null)
				scanner.close();
			System.out.println("SSL Client,reader and writer have been closed successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
