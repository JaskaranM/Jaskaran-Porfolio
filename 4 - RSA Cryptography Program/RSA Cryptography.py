import random
import math
from tkinter import *

#checks if the numbers are prime
def isPrime(number):
    if number < 2:
        return False
    for i in range(2, number // 2 + 1):
        if number % i == 0:
            return False
    return True

#generates prime number
def randomPrimeNumber(minValue, maxValue):
    prime = random.randint(minValue, maxValue)
    while not isPrime(prime):
        prime = random.randint(minValue, maxValue)
    return prime

#finds the modular inverse
def inverseMod(e, phi):
    for d in range(3, phi):
        if (d * e) % phi == 1:
            return d
    raise ValueError("Can't find an inverse")

#starts the tkinter window
root = Tk()
root.title("RSA Cryptography")
root.configure(background="#D85336")
root.minsize(750, 500)
root.maxsize(750, 500)

#centers everything
root.grid_columnconfigure(0, weight=1)
root.grid_columnconfigure(1, weight=1)
root.grid_columnconfigure(2, weight=1)

#starts all the lables and buttons
label = Label(root, text="Enter Message: ")
label.grid(column=0, row=1, padx=10, pady=10, sticky="e")
entry = Entry(root, width=50)
entry.grid(column=1, row=1, padx=10, pady=10, columnspan=2, sticky="w")
resultLabel = Label(root, text="", bg="#D85336", fg="white", anchor="w", justify="left", wraplength=700)
resultLabel.grid(column=0, row=4, padx=10, pady=10, columnspan=3, sticky="nsew")
#contains the logic for encryption and decryption
def RSAEncryption():
    message = entry.get()

    p = randomPrimeNumber(1000, 10000)
    q = randomPrimeNumber(1000, 10000)

    #changes q if it is the same as p (would be too easy to decipher if they were the same number)
    while p == q:
        q = randomPrimeNumber(1000, 10000)

    n = p * q
    phiN = (p - 1) * (q - 1)

    #ensures that e is coprime with phi(n)
    e = random.randint(3, phiN - 1)
    while math.gcd(e, phiN) != 1:
        e = random.randint(3, phiN - 1)

    # calculates the private key
    d = inverseMod(e, phiN)

    # encryption
    encryptedMessage = [ord(ch) for ch in message]
    cipherText = [pow(ch, e, n) for ch in encryptedMessage]

    #decryption
    decryptedMessage = [pow(ch, d, n) for ch in cipherText]
    decryptedText = "".join(chr(ch) for ch in decryptedMessage)

    #outputs results
    result = (f"Public key (e, n) = ({e}, {n})\n"
              f"Private key (d, n) = ({d}, {n})\n"
              f"Ciphertext: {cipherText}\n"
              f"Decrypted message: {decryptedText}\n"
              f"Prime p = {p}\n"
              f"Prime q = {q}\n"
              f"Phi of n: {phiN}")

    resultLabel.config(text=result)

#button to start RSA encryption
button = Button(root, text="Encrypt/Decrypt", command=RSAEncryption)
button.grid(column=0, row=2, padx=10, pady=10, columnspan=3, sticky="n")

root.mainloop()
