package com.gora;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;


//Watek czytania danych z serwera
public class ServerConnection implements Runnable{
    private SocketChannel serverSocket;
    //receive information
    private static final int BUFFER_SIZE = 1024;
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());


    ServerConnection(SocketChannel socket) throws IOException {
        this.serverSocket=socket;

    }
    @Override
    public void run() {


        while(true){
            ByteBuffer serverResponse=ByteBuffer.allocate(BUFFER_SIZE);
            try {
                serverSocket.read(serverResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
                //logger.log(Level.WARNING, e.getMessage());
            }

            String data = new String(serverResponse.array()).trim();
            if(data.length()>0){
                System.out.println("Server:\n"+data + "\n");
                if(data.equals("You have left the game \n")){
                    break;
                }
            }
            serverResponse.clear();
        }

    }
}