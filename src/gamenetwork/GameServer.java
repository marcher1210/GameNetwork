/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import gamenetwork.listeners.*;
import gamenetwork.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
;

/**
 *
 * @author marcher89
 */
public class GameServer extends AbstractNetworkCommunicator {
    
    private final int ACCEPT_TIMEOUT = 250;
    
    private final int port;
    
    private ServerSocket server;
    
    HashMap<Integer, ClientInfo> clients;
    
// Instantiation
     
    public GameServer(int port) {
        this.port = port;
        this.clientId = 0;
        clients = new HashMap<>();
    }
    
// Status
    
    public int getPort() {
        return server.getLocalPort();
    }
    
// Connection
    public void removeClient(int i) {
        closeConnection(clients.get(i));
        clients.remove(i);
    }
    
    /**
     * Stops listening for new clients
     */
    public void stopListening() {
        //TODO: Implement
        throw new NotImplementedException();
    }

    
// Internal Helper methods
   private int getFreeClientId() {
       int returnVal = 1;
       while(clients.containsKey(returnVal))
           returnVal++;
       assert !clients.containsKey(returnVal) : "The client id is not unique";
       return returnVal;
   }
   
   public Map<Integer, String> getConnectedClients() {
       Map<Integer, String> connectedClients = new HashMap<>();
       connectedClients.put(0, getClientName());
       for (Map.Entry<Integer, ClientInfo> entry : clients.entrySet()) {
           connectedClients.put(entry.getKey(), entry.getValue().getName());
       }
       return connectedClients;
   }
   
    
// Implementation

    protected void startProcedure() {
        try {
            server = new ServerSocket(port);
            Thread t = new Thread(this);
            runningThreads.add(t);
            t.start();
            Thread multicast = new Thread(new Runnable() {
                @Override
                public void run() {
                    multicastServerInfoLoop();
                }
            });
            runningThreads.add(multicast);
            multicast.start();
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
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
            
            int newId = getFreeClientId();
            String newName = "Player "+newId;
            ClientInfo newClient = new ClientInfo(newId, soc, in, out);
            newClient.setName(newName);
            
            out.writeObject(new NetworkMessage(NetworkMessageType.YourClientId, newId));
            out.flush();
            
            out.writeObject(new NetworkMessage(NetworkMessageType.ConnectedClients, getConnectedClients()));
            out.flush();
            
            realSend(new NetworkMessage(NetworkMessageType.ClientConnected, new Tuple<>(newId, newName)));
            for (LobbyActivityListener l : lobbyActivityListeners) {
                l.clientConnected(newId, newName);
            }
            clients.put(newId, newClient);
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
        for (ClientInfo client : clients.values()) {
            try {
                client.getOut().writeObject(msg);
                client.getOut().flush();
            } catch (IOException ex) {
                // TODO: Error handling
                ex.printStackTrace();
            }
        }
    }

    private void closeConnection(ClientInfo client) {
        try {
            client.in.close();
            client.out.close();
            client.socket.close();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    private void multicastServerInfoLoop() {
        try {
            try (DatagramSocket multicastSocket = new DatagramSocket()) {
                while(isRunning()){
                    try{
                        byte[] buf = new byte[256];
                        String multicastString = this.getPort()+";"+this.getClientName();
                        buf = multicastString.getBytes();

                        InetAddress group = InetAddress.getByName(multicastGroupName);
                        DatagramPacket packet;
                        packet = new DatagramPacket(buf, buf.length, group, multicastPort);
                        multicastSocket.send(packet);

                        try {
                            Thread.sleep(5000);
                        } 
                        catch (InterruptedException e) { }
                    } catch(IOException ex) {
                        //TODO: Error handling
                    }
                }
            }
        } catch (SocketException ex) {
            //TODO: Error handling
        }
    }
    
    private void multicastServerInfoOnce(){
        //TODO: Implement
    }
    
    @Override //From class Runnable
    public void run() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(isRunning()) {
                    for (ClientInfo client : clients.values()) {
                        try {
                            NetworkMessage msg;
                            while((msg = (NetworkMessage)client.getIn().readObject()) != null /*This right?*/) {
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
            }
        });
        runningThreads.add(t);
        t.start();
        while(isRunning()) {
            acceptNewClient();
            scanQueue();
        }
        try {
            t.join();
        } catch (InterruptedException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
        realSend(new NetworkMessage(NetworkMessageType.ClientDisconnected, getClientId()));
        for (ClientInfo client : clients.values()) {
            closeConnection(client);
        }
    }
    
    protected void messageReceived(NetworkMessage msg) {
        switch(msg.getType()){
            case FirstConnect:
                // Ignore
                break; 
            case YourClientId:
                assert false : "Server received YourClientId from client "+msg.getSenderId();
                break;
            case ConnectedClients:
                assert false : "Server received ConnectedClients from client "+msg.getSenderId();
                break;
            case ClientConnected:
                assert false : "Server received ClientConnected from client "+msg.getSenderId();
                break; 
            case ClientDisconnected:
                if(msg.getSenderId() != (int) msg.getObject())
                    assert false : "Server received ClientDisconnected from client "+msg.getSenderId()+" saying client number "+msg.getObject()+" disconnected.";
                removeClient((int)msg.getObject());
                realSend(msg);
                break; 
            case ClientNameChanged:
                {
                    Tuple<Integer, String> t = (Tuple<Integer, String>) msg.getObject();
                    clients.get(t.first).setName(t.second);
                    realSend(msg);
                    for (LobbyActivityListener l : lobbyActivityListeners) {
                        l.clientNameChanged(t.first, t.second);
                    }
                }
                break; 
            case GameSetting:
                {
                    Tuple<Integer, Object> t = (Tuple<Integer, Object>) msg.getObject();
                    realSend(msg);
                    for (GameSettingListener l : gameSettingListeners) {
                        l.gameSettingChanged(t.first, t.second);
                    }
                }
                break; 
            case GameUpdate:
                {
                    Tuple<Integer, Object> t = (Tuple<Integer, Object>) msg.getObject();
                    realSend(msg);
                    for (GameUpdateListener l : gameUpdateListeners) {
                        l.gameUpdateReceived(t.first, t.second);
                    }
                }
                break; 
            case ChatMessage:
                realSend(msg);
                for (ChatMessageListener l : chatMessageListeners) {
                    l.chatMessageReceived(msg.getSenderId(), (String) msg.getObject());
                }
                break;
        }
    }

    private static class ClientInfo {
        int id;
        String name;
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientInfo(int id, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            this.id = id;
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        int getId() { return id; }
        String getName() { return name; }
        void setName(String name) { this.name = name; }
        Socket getSocket(){ return socket; }
        ObjectInputStream getIn(){ return in; }
        ObjectOutputStream getOut(){ return out; }
    }
}
