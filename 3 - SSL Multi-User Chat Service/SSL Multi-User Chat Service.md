# Multi-User-Chat-Service-Project
As part of my Secure Network Services module at Aston University, my courswork tasked me with creating a multi-user chat room using Java socket programming. This was then secured by implementing SSL/TLS programming to encrypt any messages exchanged between the clients and server.

## Creating the Multi-User Chat Room
The design chosen for implementing the multi-user chat was a multi-threaded client-server design where a client joins using the ClientSSLNetwork.java file with a username that they choose. A Server class is used to listen on a chosen port (1234 was chosen) and continuously loops until a client connects, at which point a thread of the ClientHandler class is launched that handles the client tracking, using an array list and initialises the BufferedReader and BufferedWriter. 

 After entering the chat room (which will not allow for a client to join without a username), the user can then post messages under that username which other clients that are connected can see. Separating the code into three separate, well-defined classes made it very easy to test individual components of the system and improves the readability for when changes are made in the future. First the Server class was tested to ensure that it was broadcasting on the correct port and that the methods for closing the socket was working properly.


<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room1.png">
</div>


<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/room2.png">
</div>
