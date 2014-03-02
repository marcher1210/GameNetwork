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
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author marcher89
 */
public class GameClient extends AbstractNetworkCommunicator {
    private Socket socket;
    
    private final String address;
    private final int port;
    
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    private Map<Integer, String> connectedClients;
    
// Instantiation
    
    public GameClient(String address, int port) {
        super();
        this.address = address;
        this.port = port;
        connectedClients = new HashMap<>();
    }
    
// Status
    
    public String getAddress() {
        return socket.getLocalAddress().getHostAddress();
    }
    
    public int getPort() {
        return socket.getLocalPort();
    }
    
    public Map<Integer, String> getConnectedClients(){
        return connectedClients;
    }
    

// Implementation
    
    protected void startProcedure() {
        try {
            socket = new Socket(address, port);
            Thread t = new Thread(this);
            runningThreads.add(t);
            t.start();
        } catch (UnknownHostException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    protected void realSend(NetworkMessage msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }

    private void realClose() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            //TODO: error handling
            ex.printStackTrace();
        }
    }
    
    @Override //From class Runnable
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage(NetworkMessageType.FirstConnect, getClientName()));
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    while(isRunning()) {
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
            });
            runningThreads.add(t);
            t.start();
            while(isRunning()) {
                scanQueue();
            }
            try {
                t.join();
            } catch (InterruptedException ex) {
                //TODO: Error handling
                ex.printStackTrace();
            }
            realClose();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        }
    }
    
    protected void messageReceived(NetworkMessage msg) {
        switch(msg.getType()){
            case FirstConnect:
                assert false : "Client got FirstConnect from server";
                break; 
            case YourClientId:
                clientId = (int) msg.getObject();
                send(new NetworkMessage(NetworkMessageType.ClientNameChanged, new Tuple<>(getClientId(), getClientName())));
                break;
            case ConnectedClients:
                connectedClients = (Map<Integer, String>) msg.getObject();
                for (Entry<Integer, String> c : ((Map<Integer, String>) msg.getObject()).entrySet()) {
                    System.out.println("Connected client: id="+c.getKey()+", name="+c.getValue());
                }
                break;
            case ClientConnected:
                {
                    Tuple<Integer, String> t = (Tuple<Integer, String>) msg.getObject();
                    connectedClients.put(t.first, t.second);
                    for (LobbyActivityListener l : lobbyActivityListeners) {
                        l.clientConnected(t.first, t.second);
                    }
                }
                break; 
            case ClientDisconnected:
                connectedClients.remove((int) msg.getObject());
                for (LobbyActivityListener l : lobbyActivityListeners) {
                    l.clientDisconnected((int) msg.getObject());
                }
                break; 
            case ClientNameChanged:
                {
                    Tuple<Integer, String> t = (Tuple<Integer, String>) msg.getObject();
                    connectedClients.put(t.first, t.second);
                    for (LobbyActivityListener l : lobbyActivityListeners) {
                        l.clientNameChanged(t.first, t.second);
                    }
                }
                break; 
            case GameSetting:
                {
                    Tuple<Integer, Object> t = (Tuple<Integer, Object>) msg.getObject();
                    for (GameSettingListener l : gameSettingListeners) {
                        l.gameSettingChanged(t.first, t.second);
                    }
                }
                break; 
            case GameUpdate:
                {
                    Tuple<Integer, Object> t = (Tuple<Integer, Object>) msg.getObject();
                    for (GameUpdateListener l : gameUpdateListeners) {
                        l.gameUpdateReceived(t.first, t.second);
                    }
                }
                break; 
            case ChatMessage:
                for (ChatMessageListener l : chatMessageListeners) {
                    l.chatMessageReceived(msg.getSenderId(), (String) msg.getObject());
                }
                break;
        }
    }
}
