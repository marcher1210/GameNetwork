/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.*;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcher89
 */
public class GameServer implements Runnable {
    
    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running;
    
    
// Instantiation
     
    public GameServer(int port) throws IOException {
        server = new ServerSocket(port);
    }
    
// Status
    
    public int getPort() {
        return server.getLocalPort();
    }
    
// Connection
    
    public void start() {
        new Thread(this).start();
    }
    
    public void send(NetworkMessage msg) {
        //TODO: Implement
    }
    
    /**
     * Stops listening for new clients
     */
    public void stopListening() {
        //TODO: Implement
    }
    
    /**
     * Closes the server and all connections to clients
     */
    public void close() {
        //TODO: Implement
        running = false;
    }
    
// Implementation

    private void scanQueue() {
        //TODO: Implement
    }
    
    private void acceptNewClient() {
        //TODO: Implement
    }
    
    private void checkForNewMessages() {
        //TODO: Implement
    }
    
    private void broadcast(NetworkMessage msg) {
        //TODO: Implement
    }
    
    private void messageReceived(NetworkMessage msg) {
        //TODO: Implement
    }
    
    @Override //From class Runnable
    public void run() {
        running = true;
        while(running) {
            acceptNewClient();
            scanQueue();
            checkForNewMessages();
        }
    }
}
