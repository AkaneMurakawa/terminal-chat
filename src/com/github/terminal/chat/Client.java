package com.github.terminal.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Terminal Chat Client
 * base on TCP, only for learning.
 *
 * @author AkaneMurakawa
 * @date 2017-10-5
 */
public class Client {
    private Socket socket;
    private String host;
    private int port;
    private String username;

    /**
     * initialize
     *
     * @throws IOException
     * @throws UnknownHostException
     */
    public Client() throws UnknownHostException, IOException {
        System.out.println("Welcome to Terminal Chat!\n");

        // set server host and port,
        Scanner scan = new Scanner(System.in);
        System.out.print("Please input your name\n~ $");
        username = scan.next();

        System.out.print("Please input your server host\n~ $");
        host = scan.next();

        System.out.print("Please input your server port\n~ $");
        port = scan.nextInt();
        System.out.println("Try to connect server...");

        socket = new Socket(host, port);
        System.out.println("Connection Success! input 'exit' to exit!\n");
    }

    /**
     * start your client.
     */
    public void startClient() {
        Thread t = new Thread(new ServerHandler());
        t.start();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            // autoFlush = true, very important!!!!!!!!!!!!!!!!!!!!!!!!!!
            PrintWriter pw = new PrintWriter(osw, true);

            String msg;
            String content;
            Scanner scan = new Scanner(System.in);
            long last = System.currentTimeMillis();
            do {
                content = scan.nextLine();
                if (System.currentTimeMillis() - last >= 1000) {
                    msg = "\n[" + username + "] " + sdf.format(new Date()) + "\n" + content;
                    pw.println(msg);
                } else {
                    System.out.println("* Speak too fast!");
                }
            } while (!"exit".equals(content));

            System.out.println("Good Bye!");
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * InputStream£¬show msg in terminal from server all the time.
     */
    private class ServerHandler implements Runnable {
        public void run() {
            while (true) {
                try {
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);

                    String msg;
                    while ((msg = br.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}















