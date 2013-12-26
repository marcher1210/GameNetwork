/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import gamenetwork.listeners.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author marcher89
 */
public abstract class AbstractNetworkCommunicator {
    
    protected ConcurrentLinkedQueue<NetworkMessage> queue;
    protected boolean running;
    
    protected GameNetwork network;
    
    
// Instantiation
    public AbstractNetworkCommunicator(GameNetwork network) {
        this.network = network;
        queue = new ConcurrentLinkedQueue<>();
        running = true;
    }
    
// Connection
    
    public void send(NetworkMessage msg) {
        queue.offer(msg);
    }
    
    public abstract void close();
    
// Implementation
    
    protected void scanQueue() {
        NetworkMessage msg;
        while((msg = queue.poll()) != null) {
            realSend(msg);
        }
    }
    
    protected abstract void realSend(NetworkMessage msg);
    
    protected abstract void messageReceived(NetworkMessage msg);
}
