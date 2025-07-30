# SOC Automation 



## Tools Used
> Sysmon – Sysmon is used as an endpoint detection tool and allows for the malicious activity to be detected

> Wazuh – Wazuh is acts as the SIEM in this project by collecting, aggregating, indexing and analysing data, making it very easy to spot intrusions and anomalies

> Shuffler -  Shuffler acts as the SOAR in the project by providing a user friendly interface to customise and create automated workflows that drastically improves the security of an organisation / network.

> The Hive – The Hive is the IR of the project by giving access to a multitude of options such as case management and advanced user management.

> VirusTotal – VirusTotal is a malware detection tool that works by analysing files and websites and checking them through a large variety of antiviruses, which check by comparing the signatures against a database of known signatures

> VirutalBox is a hosted hypervisor used to create a virtual machine which will acta as a PC in a network in this project (running Windows 10)

> DigitalOcean was used as a cloud infrastructure provider to allow me to create two machines – one running the Hive and the other running Wazuh

> PuTTy is a terminal emulator that gives me an easy interface to connect to the machines created on DigitalOcean

> Mimikatz is an exploit that steals credentials  by credential extracting and dumping. It will act as the malicious activity on the Windows 10 VM that will be flagged by the SOC system

<br><br>

## What is a SOC?
A security operations centre is responsible for detecting, analysing and responding to cyber-attacks in order to minimise the damage dealt by cyber-attacks. It does this by maintaining vigilance over the network, systems and applications running to ensure a proactive defensive posture.  SOCs are equipped with a multitude of tools to do so, including :
-	Malware detection tools
-	Endpoint detection  and response
-	IR (Incident Response)
-	SIEMs ( Security Information and Event Management)
-	SOAR ( Security Orchestration, Automation and Response)

These tools each cover different aspects and come together to create a secured network that can mitigate threats and quickly smother the effects of any that get through.

## Goal of the Project
The goal of my project is to set up a system where :
1.	A threat is automatically detected on the user’s PC using Sysmon 
2.	Threat is received by Wazuh and sends an alert to Shuffler
3.	Shuffler then runs the workflow, sending alerts to the Hive and also sending an email to the SOC analyst
4.	The SOC analyst chooses the response action and sends it through Shuffler and Wazuh Manager
5.	The action is carried out on the client machine and the threat is stopped

The flow of the SOC system can be better visualised with the diagram below:

 <br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture9.png">
</div>

<br><br>

## Steps
1.	First, install VirtualBox and loads a Windows 10 ISO on it. Then install Sysmon and download the config file and place it in the Sysmon directory
2.	CD to the Sysmon directory using powershell and run the command “.\sysmon64.exe -I sysmonconfig.xml”
3.	Next, build two machines on DigitalOcean, both running Ubuntu and name one “the Hive” and the other “Wazuh”.
4.	Build a firewall that only allows your public IP and ensure that this firewall is running on the two machines you just created. The firewall ensures that the machines are protected from scanners and bots
5.	Using PuTTy and the IP of the Wazuh machine, remote into the terminal and install Wazuh using a curl command and the url for Wazuh. Make sure to copy down the username and password shown on the terminal
6.	Remote into the Hive’s terminal and install Java, Cassandra, ElasticSearch and the Hive
7.	Modify the Cassandra file, using “nano /etc/cassandra/cassandra.yaml” and change the “listen_address” , “rpc_address” and seed_address” to the public IP of the Hive
8.	Restart Cassandra using “systemctl restart cassandra.service”
9.	Modify the ElasticSearch file, using  “nano /etc/elasticsearch/elasticsearch/yml” and change the networkhost address to the address of the Hive. Uncomment the http.port variable.
10.	Restart ElasticSearch using “systemctl restart ElasticSearch”
11.	Modify the Hive, using “nano/etc/thehive/application.conf” and change the hostname and application.baseUrl variable to the public IP of the Hive.
12.	Restart the Hive using  “systemctl restart thehive.service”
13.	Login to Wazuh, using the IP of the machine and the credentials you saved earlier
14.	Add an agent and put in the public IP of Wazuh. Copy the commands shown on the Wazuh dashboard and run them on the Window 10 virtual machine. The agent should now be visible in the Wazuh dashboard

<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture10.png">
</div>

 <br><br>

15.	On the Windows 10 VM, go to the ossec-agent folder and edit the ossec.conf file and add the following under the Log analysis section. Restart Wazuh under services

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture11.png">
</div>

<br><br>
 
16.	Open Windows Security and add an exclusion. The excluded memory area will be where Mimikatz will be ran
17.	Download and save Mimikatz to the excluded memory area. Using powershell (with admin privileges), CD into the directory and run Mimikatz ( .\mimikatz.exe) 
18.	You should now be able to see events flowing in from the VM onto Wazuh 

<br><br>

 <div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture12.png">
</div>

<br><br>

Searching  for Mimikatz, I can see all the details about the malware running on the machine

 <br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture13.png">
</div>

<br><br>

19.	Use PuTTy to access the Wazuh terminal and navigate to the ossec.conf file, using nano /var/ossec/etc/ossec.conf. Locate <logall>no</logall> and replace the no with a yes. Restart Wazuh manager using “systemctl restart wazuh-manager.service”

20.	Currently, an attacker could simply rename the file to something else, and it would not trigger an alert. This can be nullified by creating a rule on Wazuh that uses the originalFileName. Navigate to ‘Rules’ under ‘Administration’, mange rule files, custom rules and then add the rule and the details about the rule.

<br><br>

 <div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture14.png">
</div>

<br><br>


21.	To verify that this works, I have renamed the file, and it still triggers an alert on Wazuh 

<br><br>


 <div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture15.png">
</div>

<br><br>


The malware is still detected and triggers an alert on Wazuh

<br><br>
 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture16.png">
</div>

<br><br>


22.	Login to Shuffler and create a workflow
23.	To start creating the workflow, add a webhook and copy the webhook URI
24.	Connect to Wazuh CLI (command line interface) and open the ossec.conf file again. Paste the integration tag, save it then restart Wazuh

<br><br>


 <div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture17.png">
</div>

<br><br>


25.	 Rerunning the Mimikatz malware should now send an alert to Shuffler that has the data of the alert

<br><br>

 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture18.png">
</div>

<br><br>

26.	Change the tool in Shuffler to Regex capture group and ensure that the input data and Regex parameters are like this:

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture19.png">
</div>

<br><br>

 
This will parse out the SHA256 hash which we can then feed into VirusTotal


27.	Create an account on VirusTotal and copy over your API key. Add VirusTotal to your workflow and input the API key. Change the action to “Get a hash report” and set the hash parameter to “$sha256_regex.group_0.#”. This will take the hash that was parsed out in the previous step and send it to VirusTotal


28.	Login to the Hive’s dashboard and create an organisation and a two users under it; One of the users should have the analyst profile and the other should be a service account. The principle of least privilege should be applied when giving permissions. Create an API key and copy it


29.	Add the Hive to your Shuffler workflow and authenticate it using the API key. Change the action to “Create Alert”. Modify the JSON code to fit your requirements for the alert

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture20.png">
</div>

<br><br>


30.	Add a new rule to your firewall on DigitalOcean to allow IPs to access port 9000 (where the Hive instance is running)

You should now be able to login as the analyst account and see the Mimikatz alert appear on the dashboard

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture21.png">
</div>

<br><br>

 
31.	Add the email action in Shuffler and connect it to VirusTotal. Add the recipients of the email, subject and the body of the email. 

<br><br>


 <div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture22.png">
</div>

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture23.png">
</div>

<br><br>

 
32.	Add the http action to your workflow and change the action to curl and add the following, replacing the Wazuh IP, username and password with your own

<br><br>

 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture24.png">
</div>

<br><br>


33.	Add the Wazuh action to the workflow and add “$exec.all_fields.agent.id” to the  agents list field
34.	Connect to the Wazuh CLI and navigate to the ossec.conf file. Under the “Active Response” section,  add an active response for the firewall drop and then restart the Wazuh manager

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture25.png">
</div>

 <br><br>


35.	Add the user_input action in Shuffler and set it up to show the source IP and send an email to the analyst

<br><br>

 
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture26.png">
</div>

<br><br>


Selecting on the TRUE link will present this:

<br><br>


<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture27.png">
</div>

 <br><br>


Returning to the Windows 10 VM, you can attempt to ping the IP address to verify that the IP address has been blocked

<br><br>

<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/Picture28.png">
</div>

## Summary
At the end of all of this, you should have a system that detects Mimikatz on PCs in a network, creates an alert, enriches the IOCS and sends an email to a SOC analyst to allow for a response action to be chose and executed. This current system only works for Mimikatz for demonstration purposes. However it can easily be expanded upon to detect a wide variety of threats by creating more rules in Wazuh but this would also mean that more response actions would need to be implemented as there is no one action that is the appropriate response for every threat.

