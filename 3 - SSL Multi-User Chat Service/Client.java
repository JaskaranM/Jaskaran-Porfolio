public class Client{
	public static void main(String[]args) throws IOException{		
		if (args.length != 1) {
		    System.out.println("Usage: java Client <hostname><portNumber>");
		    return;
		}	
		String hostName = args[0];
		int portNumber = 1234;
		
	try {	
		Socket clientSocket = new Socket(hostName, portNumber);		
    	System.out.println("Connected to Server");		
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
        String response = in.readLine();
        System.out.println("Message: " + response);
        
        clientSocket.close();
        
		} catch (IOException e) {
            System.err.println("Error: Couldn't get I/O connection to" + hostName);
		}
		
	}
}	
