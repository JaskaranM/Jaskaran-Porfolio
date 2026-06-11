I learnt about RSA cryptography in my mathematics for coding professionals module  at university. The simplified version on how RSA works is :

- Two large prime numbers are chosen and are kept secret
- The prime numbers are multiplied to create n (number that is used as the modulus for encryption and decryption)
- The receiver calculates ϕ(pq)=(p−1)(q−1) and chooses a number e which i relatively prime to ϕ(pq)
- de ≡ 1(modϕ(n)) is used to calculate to find what d is (d is the private key)
- The reveiver then redistributes both parts of the key n and e whilst keeping d a secret
- With the keys generated, they can be reused as often and needed

- The sender converts the message using the ASCII alphabet and then calculates  c≡m^e (modn) where m is the message and c is the ciphertext

- Receiver computes c^d ≡ m(modn) to reverse the process and find m
- the ASCII is translated back to letters

My program accomplishes the encrypting and decrypting aspects of RSA and I am quite pleased with it. The next steps would be to make it more efficient by implementing more effective methods such as extended Euclidean algorithm for the modular inverse. Another inefficiency of my code is how the prime number checker has been implemented, which could be substituted out for the Miller-Rabin Primality test which is a probabilistic algorithm that checks if larger numbers are prime. This would greatly reduce the amount of time that it takes to find two large prime numbers. Furthermore, this would also allow me to increase the range for the prime numbers that can be chosen, further reinforcing the security. Additionally the tkinter GUI that I made feels quite bland and could be improved to be more aesthetic and enjoyable to interact with for the user.

My next steps for this program would be to integrate my skills from my MQTT project and use it in conjunction with this RSA cryptography encryption to be able to send encrypted messages to devices. Another possible way I could allow for bidirectional messaging could be by implementing sockets and creating a client-server communication.

Overall I am pleased with my program and look forward to tweak and advance this program with the improvements above.
