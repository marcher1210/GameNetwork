/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import gamenetwork.listeners.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author marcher89
 */
public class GameNetwork {

// Instance variables
    
    private Set<LobbyActivityListener> lobbyActivityListeners;
    private Set<GameSettingListener> gameSettingListeners;
    private Set<GameUpdateListener> gameUpdateListeners;
    private Set<ChatMessageListener> chatMessageListeners;
    
// Creation
    public GameNetwork() {
        lobbyActivityListeners = new HashSet<>();
        gameSettingListeners = new HashSet<>();
        gameUpdateListeners = new HashSet<>();
        chatMessageListeners = new HashSet<>();
    }
    
    
// Connection
    
    public boolean hostGame(int port){
        throw new NotImplementedException();
    }
    
    public boolean joinGame(String host, int port) {
        throw new NotImplementedException();
    }
    
    public void disconnect() {
        throw new NotImplementedException();
    }
    
    
// Status
    
    public boolean isStarted() {
        throw new NotImplementedException();
    }
    
    public boolean isHost() {
        assert isStarted();
        throw new NotImplementedException();
    }
    
    public int myClientId() {
        assert isStarted();
        throw new NotImplementedException();
    }
    
    public Object myIp() {
        throw new NotImplementedException();
    }
    
    public int portNumber() {
        assert isHost();
        throw new NotImplementedException();
    }
    
    public HashMap<Integer, String> connectedClients() {
        assert isStarted();
        throw new NotImplementedException();
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
    
    
//Lobby activity
    
    public void changeClientName(int id, String newName) {
        assert isHost() || id == myClientId();
        assert connectedClients().containsKey(id) : "Invalid client id";
        throw new NotImplementedException();
    }
    
    public void changeClientName(String newName) {
        assert isStarted();
        changeClientName(myClientId(), newName);
    }
    
    public void changeGameSetting(int type, Object object) {
        assert isHost();
        throw new NotImplementedException();
    }
    
    public void kickClient(int id) {
        assert isHost();
        assert id != myClientId() : "You can't kick youself, mkay?!";
        assert connectedClients().containsKey(id) : "Invalid client id";
        throw new NotImplementedException();
    }
    
    public void startGame() {
        assert isHost();
        throw new NotImplementedException();
    }
    
// Game functionality
    
    public void sendGameUpdate(int type, Object object) {
        assert isStarted();
        throw new NotImplementedException();
    }
    
    
// Chat messages
    
    public void sendChatMessage(String msg) {
        assert isStarted();
        throw new NotImplementedException();
    }
    
    
    
    
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        GameServer server = new GameServer(12345);
        server.start();
        GameClient client = new GameClient("localhost", 12345);
        Thread.sleep(1000);
        client.send(new NetworkMessage(NetworkMessageType.GameUpdate, "Hej du!"));
    }
}
