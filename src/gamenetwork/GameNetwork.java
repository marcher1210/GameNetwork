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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void prepareHostGame(int port) {
        assert !isStarted();
        comm = new GameServer(port);
        comm.setClientName("Server");
    }

    public void prepareJoinGame(String host, int port) {
        assert !isStarted();
        comm = new GameClient(host, port);
        comm.setClientName("Client");
    }

    public void startConnection() {
        assert !isStarted() && isPrepared();
        comm.start();
    }

    public void disconnect() {
        assert isStarted();
        comm.close();
    }

// Status
    public boolean isPrepared() {
        return comm != null;
    }

    public boolean isStarted() {
        return comm != null && comm.isRunning();
    }

    public boolean isHost() {
        assert isPrepared();
        return comm instanceof GameServer;
    }

    public int myClientId() {
        assert isStarted();
        return comm.getClientId();
    }

    public static Collection<InetAddress> myIpAddresses() {
        Collection<InetAddress> ret = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ret.add(addresses.nextElement());
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(GameNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public int portNumber() {
        assert isHost();
        return ((GameServer) comm).getPort();
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
        server.setClientName("Overlord");
        server.start();
        GameClient client = new GameClient("localhost", 12345);
        client.setClientName("Faithful client");
        client.addChatMessageListener(new ChatMessageListener() {
            @Override
            public void chatMessageReceived(int senderId, String message) {
                System.out.println("Chat message from " + senderId + ":" + message);
            }
        });
        server.addLobbyActivityListener(new LobbyActivityListener() {
            @Override
            public void clientConnected(int clientId, String clientName) {
                System.out.println("New client connected: id=" + clientId + ", name=" + clientName);
            }

            @Override
            public void clientNameChanged(int clientId, String newClientName) {
                System.out.println("Client changed name: id=" + clientId + ", new name=" + newClientName);
            }

            @Override
            public void clientDisconnected(int clientId) {
                System.out.println("Client disconnected: id=" + clientId);
            }
        });
        client.start();
        Thread.sleep(1000);
        new GameClient("localhost", 12345).start();
        Thread.sleep(1000);
        new GameClient("localhost", 12345).start();
        Thread.sleep(1000);
        new GameClient("localhost", 12345).start();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        client.send(new NetworkMessage(NetworkMessageType.GameUpdate, new Tuple<>(42, null)));
        
        
        String input;

        while ((input = br.readLine()) != null) {
            client.send(new NetworkMessage(NetworkMessageType.ChatMessage, input));
        }
    }
}
