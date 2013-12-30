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
public abstract class AbstractNetworkCommunicator implements Runnable {
    
    protected ConcurrentLinkedQueue<NetworkMessage> queue;
    private boolean running;
    protected int clientId = -1;
    private String clientName;

    protected Set<LobbyActivityListener> lobbyActivityListeners;
    protected Set<GameSettingListener> gameSettingListeners;
    protected Set<GameUpdateListener> gameUpdateListeners;
    protected Set<ChatMessageListener> chatMessageListeners;
    
// Instatiation
        
    public AbstractNetworkCommunicator() {
        lobbyActivityListeners = new HashSet<>();
        gameSettingListeners = new HashSet<>();
        gameUpdateListeners = new HashSet<>();
        chatMessageListeners = new HashSet<>();
        queue = new ConcurrentLinkedQueue<>();
        running = false;
    }
    
    
// Status
    
    public boolean isRunning() {
        return running;
    }
    
    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
        if(isRunning())
            send(new NetworkMessage(NetworkMessageType.ClientNameChanged, clientName));
    }
    
    
// Listeners
    
    public void addLobbyActivityListener(LobbyActivityListener listener) {
        lobbyActivityListeners.add(listener);
    }
    
    public void removeLobbyActivityListener(LobbyActivityListener listener) {
        lobbyActivityListeners.remove(listener);
    }
    
    public void addGameSettingListener(GameSettingListener listener) {
        gameSettingListeners.add(listener);
    }
    
    public void removeGameSettingListener(GameSettingListener listener) {
        gameSettingListeners.remove(listener);
    }
    
    public void addChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.add(listener);
    }
    
    public void removeChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.remove(listener);
    }
    
    public void addGameUpdateListener(GameUpdateListener listener) {
        gameUpdateListeners.add(listener);
    }
    
    public void removeGameUpdateListener(GameUpdateListener listener) {
        gameUpdateListeners.remove(listener);
    }
    
    
// Connection
    
    public void start() {
        running = true;
        startProcedure();
    }
    
    public void send(NetworkMessage msg) {
        msg.setSenderId(clientId);
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
    
    protected abstract void startProcedure();
    
    protected abstract void realSend(NetworkMessage msg);
    
    protected abstract void messageReceived(NetworkMessage msg);
}
