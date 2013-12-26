/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcher89
 */
public class GameClient implements Runnable {
    ConcurrentLinkedQueue<NetworkMessage> queue;
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running;
    
// Instantiation
    
    public GameClient(String address, int port) throws UnknownHostException, IOException {
        queue = new ConcurrentLinkedQueue<>();
        running = true;
        socket = new Socket(address, port);
        new Thread(this).start();
    }
    
// Status
    
    public String getAddress() {
        return socket.getLocalAddress().getHostAddress();
    }
    
    public int getPort() {
        return socket.getLocalPort();
    }
    
// Connection
    
    public void send(NetworkMessage msg) {
        queue.offer(msg);
    }
    
    public void close() {
        //TODO: Implement
        running = false;
    }
    
    
// Implementation
    
    private void scanQueue() {
        NetworkMessage msg;
        while((msg = queue.poll()) != null) {
            try {
                out.writeObject(msg);
                out.flush();
            } catch (IOException ex) {
                //TODO: Error handling
                ex.printStackTrace();
            }
        }
    }
    
    private void messageReceived(NetworkMessage msg) {
        System.out.println("Client: "+msg.getObject().toString());
    }
    
    @Override //From class Runnable
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage(NetworkMessageType.FirstConnect, "Hello server"));
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while(running) {
                        try {
                            NetworkMessage msg;
                            while((msg = (NetworkMessage)in.readObject()) != null /*This right?*/) {
                                messageReceived(msg);
                            }
                        } catch (IOException ex) {
                            //TODO: Error handling
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            //TODO: Error handling
                            ex.printStackTrace();
                        } catch (ClassCastException ex) {
                            //TODO: Error handling
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
            while(running) {
                scanQueue();
            }
            // TODO: Close streams'n'stuff
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
}
