# Multi-User-Chat-Service-Project
As part of my Secure Network Services module at Aston University, my courswork tasked me with creating a multi-user chat room using Java socket programming with another teammate. This was then secured by implementing SSL/TLS programming to encrypt any messages exchanged between the clients and server.

## Creating the Multi-User Chat Room
The design chosen for implementing the multi-user chat was a multi-threaded client-server design where a client joins using the ClientSSLNetwork.java file with a username that they choose. A Server class is used to listen on a chosen port (1234 was chosen) and continuously loops until a client connects, at which point a thread of the ClientHandler class is launched that handles the client tracking, using an array list and initialises the BufferedReader and BufferedWriter. 

 After entering the chat room (which will not allow for a client to join without a username), the user can then post messages under that username which other clients that are connected can see. Separating the code into three separate, well-defined classes made it very easy to test individual components of the system and improves the readability for when changes are made in the future. First the Server class was tested to ensure that it was broadcasting on the correct port and that the methods for closing the socket was working properly.


<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room1.png">
</div>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room2.png">
</div>

After this first test, the functionality of a client being able to connect was tested to ensure that the socket communication of the components was working. With the socket communication foundation functioning as intended, the ClientHandler class could be created to handle the messages from a client by initialising the BufferedReader and BufferedWriter. When a client connects to the server, the endless loop is broken and a thread of the ClientHandler class is launched and added to an array list of all the active clients. The array list and threaded system allow for multiple clients to be handled at one time without issue and allows for an easy implementation of broadcasting messages to every client. The ClientHandler iterates through
the array list to broadcast the message to every active client apart from the original sender to prevent them from receiving an echo of their own message by using a simple conditional check.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room3.png">
</div>

<br><br>

# Secure Communication (SSL/TLS)

In securing our chat service, we implemented Secure Socket Layer (SSL) /Transport Layer Security (TLS) programming for both the server and the client to ensure encryption, an authentication system and consistency throughout the projects coding. 
The encryption of our chat service was enabled through a client “Trust store” and “SSL/TLS handshake”. A Trust store basically stores public key certificates from trusted Certificate Authorities so that when the server sends the request to connect it checks its certificates and if trusted by a Certificate Authority as well it will establish the connection. Before any connection is established, the client specifies the trust store location and password using system properties.

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room4.png">
</div>

<br><br>

An SSLSocketFactory is used to create a secure socket and A handshake, when initiated by calling “client.startHandshake();”,  exchanges certificates between client and server and verifies them and once encryption is complete, a secure session key is established, Once the handshake is complete, all future communications between the server and client are encrypted automatically. 

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room5.png">
</div>

<br><br>

Once the connection is established, we’ve used buffered character streams layered on top of the SSL to deal with the encrypted communication between the server and client, to allow messages to be sent and received at the same time, the client has an implemented multithreaded design and a dedicated receiver thread purely to listen for incoming messages while the main thread deals with user inputs. This helps with keeping up with real-time encrypted messaging between users.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room6.png">
</div>

<br><br>

When it came to Testing and Verification several steps were taken to make sure that encryption was functioning correctly. To begin with, the client and server were tested on the same machine to confirm that the SSL handshake completed successfully and that encrypted messages could be exchanged.
To have that final bit of insurance that all certificates were correctly trusted and everything was successful secure and encrypted, we used SSL Debugging. The debugging output can confirm all these implementations are working as they should, and this was also seen through testing on a local network testing the SSL/TLS.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room7.png">
</div>

<br><br>

# Additonal Security Measure
Part of the task required us to to find a security concern and to implement another security control in order to mitigate it. We were given full freedom to decide what security issue was of the largest concern. I decided to choose a control that would sustain the availability part of the CIA triad for this chat service as confidentiality is being upheld by the TLS secure communication implemented in level 2. Currently, a malicious user could affect the availability of the service by spamming messages or using a bot to do so, which causes resource starvation (a form of DoS attack) and consumes extra CPU power, memory, and bandwidth for all clients connected. In order to mitigate this attack whilst ensuring that legitimate users were not affected, a token bucket based rate limiter was decided upon that would replenish tokens for each user and allow them to message, whilst also limiting their capability of spamming messages in a burst. This was done using a method called isAllowed() which returned false if the client has no tokens to use to send a message, so the method returns false and ignores the broadcasting loop for it, saving CPU and network bandwidth.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room8.png">
</div>

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room9.png">
</div>

<br><br>

A capacity of five tokens with one replenishing per second was found to be a good limit as it was very effective at preventing message spam, whilst also not interfering with legitimate user activity. This capacity and token replenish rate was decided upon by testing with a variety of different numbers as too low of a capacity would affect the usage of legitimate users and a cap too high would still allow for a short burst of messages that would affect the experience of the users (due to lag from resource starvation). To test the efficacy of the security control, a client file was edited to continuously spam messages at a rate far faster than humans could send. Next, the server was started with a legitimate user connected and the malicious client. As soon as the malicious client exhausts their tokens, the attack frequency of the messages drastically decreases for the legitimate user, showing the effectiveness of the security measure. The legitimate user is still able to send messages whilst the malicious client is rate limited, showing that each user has their own limit that is not affected by other users.

# Cross Machine Deployment
In order to run the application between two physically separate devices, there were a few requirements in order to ensure that chat traffic could be sent between the devices. Firstly, the port used had to be added as an inbound rule in Windows Defender Firewall so that traffic could flow to the specified port. This is because Windows Defender has a deny policy by default for any unrequested traffic and by creating a rule to open a specific port instead of disabling  the firewall, it ensures that unnecessary ports are kept closed and that only the minimum level of privilege is given (principle of least privilege).

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room10.png">
</div>

<br><br>

After this, the IP specified in the Client class had to be changed as it is no longer locally hosted. To find the IP, the command ipconfig was used on the PC hosting the server and the IPv4 address was used. Finally, the ClientSSLNetwork file and a copy of the truststore must be placed in the same directory to ensure that it can verify the identity of the server and then connect. After starting the server and a client on the PC, I can connect to it using the laptop client and send messages between the two clients, meaning that the cross machine deployment has successfully worked.

# Network Traffic Analysis
To demonstrate the ability of bad actors being able to sniff packets, Wireshark (an opensource network traffic analyser) was used to observe traffic. In this section, I tested both the unsecured version of the chat room and the secured version in order to demonstrate the differences.

## Unsecured
Using a network packet sniffer such as Wireshark will allow for packets in the network traffic to be observed and for the messages exchanged between the clients to be read as they are unencrypted and in plaintext form. By filtering to only listen to tcp.port == 1234 (the port in use for the chat service),  we can see packets flow between the clients and server along with the source IP and destination IP.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room11.png">
</div>

<br><br>

Clicking on a packet allows us to see the message in plaintext form, displaying the unsecure state of the chat service. Wireshark also allows for users to Follow TCP stream, showing exclusively the client data in the packet, and giving readability for the entire conversation that occurred between clients.

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room12.png">
</div>

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room13.png">
</div>

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room14.png">
</div>

<br><br>

## Secured

