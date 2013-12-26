/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author marcher89
 */
public class GameClient extends AbstractNetworkCommunicator implements Runnable {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
// Instantiation
    
    public GameClient(String address, int port, GameNetwork network) throws UnknownHostException, IOException {
        super(network);
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
    
    public void close() {
        //TODO: Implement
        running = false;
    }
    
    
// Implementation
    
    protected void realSend(NetworkMessage msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    protected void messageReceived(NetworkMessage msg) {
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
