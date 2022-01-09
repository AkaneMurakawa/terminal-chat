package com.github.terminal.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Terminal Chat Server Single, chat with client
 * based on TCP, only for learning.
 *
 * @author AkaneMurakawa
 * @date 2017-10-5
 */
public class ServerSingle {
    private ServerSocket server;

    public ServerSingle() throws IOException {
        System.out.println("Welcome to Terminal Chat!\n");
        System.out.println("Starting Server...");
        server = new ServerSocket(8088);
        System.out.println("Started success! input 'exit' to exit! Wait for Connecting...\n");
    }

    /**
     * start your server
     */
    public void startServer() {
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress().getHostName() + " online!\n");
                Thread t = new Thread(new ClientHandler(socket));
                t.start();

                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                //autoFlush = true.very important.!!!!!!!!!!!!!!!!!!!!!!!!!!
                PrintWriter pw = new PrintWriter(osw, true);

                String msg;
                String content;
                Scanner scan = new Scanner(System.in);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                do {
                    content = scan.nextLine();
                    msg = "\n[server]" + sdf.format(new Date()) + "\n" + content;
                    pw.println(msg);
                } while (!"exit".equals(content));

                System.out.println("Server Close!");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * this thread gets all client OutputStream all the time.
     *
     * @author HanaeYuuma
     */
    private class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);

                String msg;
                while ((msg = br.readLine()) != null) {
                    System.out.println(msg);
                }

            } catch (Exception e) {
            } finally {
                if (!socket.isClosed()) {
                    try {
                        System.out.println(socket.getInetAddress().getHostName() + " offline!");
                        socket.close();
                    } catch (Exception e2) {
                        // do nothing
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSingle server = new ServerSingle();
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
