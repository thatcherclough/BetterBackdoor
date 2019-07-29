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
- Either the backdoor or backdoor installation '.exe' executable is copied to the USB drive.
- If desired, a Java Runtime Environment is copied to the USB drive. 
- All '.duck' DuckyScripts and '.ps1' PowerShell scritps are copied to the USB drive.

After conversion, the USB drive should be inserted into a victim's PC and 'run.exe' should be executed. This will:
- Display a message with the vitim's current WiFi connection along with the password (if avaliable).
- Either run or install the backdoor.
  - If the backdoor is set to install, 'run.exe' will:
    - Install all necessary backdoor files to 'C:\ProgramData\USBDrivers'.
    - Add backdoor to startup.
    - Start the backdoor.
  - If the backdoor is set to run, 'run.exe' will:
    - Start the backdoor.

Once running, to control the backdoor you must return to USBware and run option 1 at start while being connecting to the same WiFi network as the victim's computer.

## Requirements
- Java must be installed and added to PATH.
- You must use the same computer to convert the USB drive and control the backdoor.
  - The computer used to convert the USB drive must be on the same WiFi network as the victim's computer.
  - The private IP address of this computer must remain static in the time between converting the USB drive and controlling the backdoor.
- The computer used to control the backdoor must have their firewall deactivated.
- If 'run.exe' is set to install the backdoor, it must be run s administrator to add the backdoor to startup.

## Compatibility
USBware is compatible with Windows and Linux, while the backdoor is only compatible with Windows.

## Installation
Download and extract the desired version of USBware from the [release page](https://github.com/ThatcherDev/USBware/releases).

## Usage
- Open a terminal window (cmd or bash).
- CD to the directory containing 'USBware.jar':
```
cd path\to\USBware
```
- Run USBware
```
java -jar USBware.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2019© ThatcherDev.