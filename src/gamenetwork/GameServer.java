/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
;

/**
 *
 * @author marcher89
 */
public class GameServer extends AbstractNetworkCommunicator {
    
    private final int ACCEPT_TIMEOUT = 250;
    
    private final int port;
    
    private ServerSocket server;
    
    List<Triplet<Socket, ObjectInputStream, ObjectOutputStream>> clients;
    
// Instantiation
     
    public GameServer(int port) {
        this.port = port;
        clients = new ArrayList<>();
    }
    
// Status
    
    public int getPort() {
        return server.getLocalPort();
    }
    
// Connection
    
    
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
    }
    
// Implementation

    
    protected void startProcedure() {
        try {
            server = new ServerSocket(port);
            new Thread(this).start();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    private void acceptNewClient() {
        try {
            server.setSoTimeout(ACCEPT_TIMEOUT);
            Socket soc = server.accept();
            ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
            out.writeObject(new NetworkMessage(NetworkMessageType.FirstConnect, "Hello server"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
            
            clients.add(new Triplet(soc, in, out));
            //TODO: Notify listener
        } catch (SocketException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        } catch (SocketTimeoutException ex) {
            //TODO: Error handling
            //This happens almost every time
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    protected void realSend(NetworkMessage msg) {
        for (Triplet<Socket, ObjectInputStream, ObjectOutputStream> client : clients) {
            try {
                client.getC().writeObject(msg);
                client.getC().flush();
            } catch (IOException ex) {
                // TODO: Error handling
                ex.printStackTrace();
            }
        }
    }
    
    protected void messageReceived(NetworkMessage msg) {
        System.out.println("Server: "+msg.getObject().toString());
    }
    
    @Override //From class Runnable
    public void run() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(isRunning()) {
                    for (Triplet<Socket, ObjectInputStream, ObjectOutputStream> client : clients) {
                        try {
                            NetworkMessage msg;
                            while((msg = (NetworkMessage)client.getB().readObject()) != null /*This right?*/) {
                                messageReceived(msg);
                                send(msg);
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
            }
        }).start();
        while(isRunning()) {
            acceptNewClient();
            scanQueue();
        }
    }

    private static class Triplet<T, U, V> {
        T a;
        U b;
        V c;

        Triplet(T a, U b, V c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        T getA(){ return a;}
        U getB(){ return b;}
        V getC(){ return c;}
    }
}
