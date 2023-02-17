package com.gora;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Class representing client connection
 */
public class Client {
    private static final int BUFFER_SIZE = 1024;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static SocketChannel myClient;


    public static void main(String[] args) {
        new Inner();
        Inner.initialize();

    }

    public static class Inner {
        public static void initialize() {
            logger.log(Level.INFO,"Starting server...");
            try {
                int port = 9999;
                InetAddress hostIP = InetAddress.getLocalHost();
                InetSocketAddress myAddress =
                        new InetSocketAddress(hostIP, port);
                myClient = SocketChannel.open(myAddress);

                BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
                logger.info("Trying to connect to " + myAddress.getHostName() + ":" +  myAddress.getPort() + "...%n");

                ServerConnection server = new ServerConnection(myClient);
                new Thread(server).start();

                while (true) {
                    String inputString = keyboardReader.readLine();
                    ByteBuffer myBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                    myBuffer.put(inputString.getBytes());
                    myBuffer.flip();
                    myClient.write(myBuffer);
                    if (inputString.equals("\\exit")) break;
                }
                myClient.close();
                logger.log(Level.INFO,"Closing Client connection...");
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }

        public void sendCommand(String inputString) throws IOException {
            ByteBuffer myBuffer=ByteBuffer.allocate(BUFFER_SIZE);
            myBuffer.put(inputString.getBytes());
            myBuffer.flip();
            myClient.write(myBuffer);
        }

    }



}
