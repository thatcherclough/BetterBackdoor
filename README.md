# USBware
USBware is a program can convert a USB drive into a tool that installs/runs a reverse shell backdoor on a victim's Windows PC, as well as control and send commands to this backdoor.

## Features
USBware converts a USB drive into a backdoor or backdoor installation tool. 

This backdoor can:
- Run Command Prompt commands
- Run PowerShell scripts
- Run DuckyScripts to inject keystrokes
- Exfiltrate files based on extension
- Exfiltrate Microsoft Edge and WiFi passwords
- Send and receive files to and from victim's computer
- Start a KeyLogger
- Get a screenshot of victim's computer

This backdoor is created through server-client socket connections, with your computer acting as the server, and your victim's computer acting as a client.

To convert a USB drive into a backdoor or backdoor installation tool, USBware:
- Writes and encrypts your private IP address to 'ip.txt' on the USB drive. This is needed for the victim's computer, the client, to connect to your computer, the server.
- Either the backdoor and/or backdoor installation jar file(s) is/are copied to the USB drive.
- If desired, a Java Runtime Environment is copied to the USB drive. 
- A batch file 'run.bat' is created on the USB drive for running the jar file(s) in a packaged Java Runtime Environment.
- All '.duck' DuckyScripts and '.ps1' PowerShell scripts are copied to the USB drive.

After conversion, the USB drive should be inserted into a victim's PC and 'run.bat' should be executed. This will:
- Display a message with the vitim's current WiFi connection along with the password (if available).
- Either run or install the backdoor.
  - If the backdoor is set to install, 'run.bat' will:
    - Install all necessary backdoor files to 'C:\ProgramData\USBDrivers'.
    - Add backdoor to startup.
    - Start the backdoor.
  - If the backdoor is set to run, 'run.bat' will:
    - Start the backdoor.

Once running, to control the backdoor you must return to USBware and run option 1 at start while being connecting to the same WiFi network as the victim's computer.

## Requirements
- A Java JDK distribution must be installed and added to PATH.
- Maven must be installed and added to PATH.
- You must use the same computer to convert the USB drive and control the backdoor.
  - The computer used to convert the USB drive must be on the same WiFi network as the victim's computer.
  - The IPv4 address of this computer must remain static in the time between converting the USB drive and controlling the backdoor.
- The computer used to control the backdoor must have their firewall deactivated.
- If 'run.bat' is set to install the backdoor, it must be run as administrator to add the backdoor to startup.

## Compatibility
USBware is compatible with Windows and Linux, while the backdoor is only compatible with Windows.

## Installation
```
# clone USBware
git clone https://github.com/ThatcherDev/USBware.git

# change the working directory to USBware 
cd USBware

# build USBware with Maven
mvn clean package
```

## Usage
```
# run USBware
java -jar USBware.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2019� ThatcherDev.