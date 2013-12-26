/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author marcher89
 */
public class GameClient implements Runnable {
    private PrintWriter out;
    private BufferedReader in;
    private final Socket socket;
    private boolean running;
    
 // Instantiation
    
    public GameClient(String address, int port) throws UnknownHostException, IOException {
        socket = new Socket(address, port);
        new Thread(this).start();
    }
    
// Status
    
    public String getAddress() {
        //TODO: Implement
        throw new NotImplementedException();
    }
    
    public int getPort() {
        //TODO: Implement
        throw new NotImplementedException();
    }
    
// Connection
    
    public void send(NetworkMessage msg) {
        //TODO: Implement
    }
    
    public void close() {
        //TODO: Implement
    }
    
// Implementation
    
    
    private void scanQueue() {
        //TODO: Implement
    }
    
    private void checkForNewMessages() {
        //TODO: Implement
    }
    
    private void messageReceived(NetworkMessage msg) {
        //TODO: Implement
    }
    
    @Override //From class Runnable
    public void run() {
        try {
            running = true;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            
            while(running) {
                scanQueue();
                checkForNewMessages();
            }
        } catch (IOException ex) {
            close();
        }
    }
}
