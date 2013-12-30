/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import gamenetwork.listeners.*;
import gamenetwork.util.Tuple;
import java.io.*;
import java.net.*;
import java.util.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author marcher89
 */
public class GameNetwork {

// Instance variables
    AbstractNetworkCommunicator comm;
    
// Instatiation
    public GameNetwork() {
        
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
           comm.addLobbyActivityListener(listener);
    }
    
    public void removeLobbyActivityListener(LobbyActivityListener listener) {
           comm.removeLobbyActivityListener(listener);
    }
    
    public void addGameSettingListener(GameSettingListener listener) {
           comm.addGameSettingListener(listener);
    }
    
    public void removeGameSettingListener(GameSettingListener listener) {
           comm.removeGameSettingListener(listener);
    }
    
    public void addChatMessageListener(ChatMessageListener listener) {
           comm.addChatMessageListener(listener);
    }
    
    public void removeChatMessageListener(ChatMessageListener listener) {
           comm.removeChatMessageListener(listener);
    }
    
    public void addGameUpdateListener(GameUpdateListener listener) {
           comm.addGameUpdateListener(listener);
    }
    
    public void removeGameUpdateListener(GameUpdateListener listener) {
           comm.removeGameUpdateListener(listener);
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
        client.start();
        Thread.sleep(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        client.send(new NetworkMessage(NetworkMessageType.GameUpdate, new Tuple<>(42, null)));
        
        client.addChatMessageListener(new ChatMessageListener() {

            @Override
            public void chatMessageReceived(int senderId, String message) {
                System.out.println("Chat message from "+senderId+":"+message);
            }
        });
        
        String input;

        while((input=br.readLine())!=null){
              client.send(new NetworkMessage(NetworkMessageType.ChatMessage, input));
        }
    }
}
