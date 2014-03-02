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
    
    /**
     * Create an instance of GameNetwork, prepared for joining a server.
     * Add the necessary listeners, (change the client name), and use {@link #startConnection()} to try to connect to the server.
     * @param host The IP address to join
     * @param port The port number to join
     */
    public GameNetwork(String host, int port) {
        comm = new GameClient(host, port);
        comm.setClientName("Client");
    }
    
    /**
     * Create an instance of GameNetwork, prepared for hosting a game.
     * Add the necessary listeners, (change the client name), and use {@link #startConnection()} to start listening for new connections.
     * @param port The port number on which to listen for incoming connections
     */
    public GameNetwork(int port) {
        comm = new GameServer(port);
        comm.setClientName("Server");
    }

// Connection
    public void startConnection() {
        assert !isStarted();
        comm.start();
    }

    public void disconnect() {
        assert isStarted();
        comm.close();
    }

// Status
    public boolean isStarted() {
        return comm != null && comm.isRunning();
    }

    public boolean isHost() {
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

    public Map<Integer, String> connectedClients() {
        assert isStarted();
        return comm.getConnectedClients();
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
        assert isStarted() && isHost() || id == myClientId();
        assert connectedClients().containsKey(id) : "Invalid client id";
        throw new NotImplementedException(); //TODO: Implement 
   }

    public void changeClientName(String newName) {
        comm.setClientName(newName);
        if(isStarted())
            changeClientName(myClientId(), newName);
    }

    public void changeGameSetting(int type, Object object) {
        assert isHost();
        throw new NotImplementedException(); //TODO: Implement
    }

    public void kickClient(int id) {
        assert isHost();
        assert id != myClientId() : "You can't kick youself, mkay?!";
        assert connectedClients().containsKey(id) : "Invalid client id";
        throw new NotImplementedException(); //TODO: Implement
    }

    public void startGame() {
        assert isHost();
        throw new NotImplementedException(); //TODO: Implement
    }

// Game functionality
    public void sendGameUpdate(int type, Object object) {
        assert isStarted();
        throw new NotImplementedException(); //TODO: Implement
    }

// Chat messages
    public void sendChatMessage(String msg) {
        assert isStarted();
        throw new NotImplementedException(); //TODO: Implement
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        GameNetwork server = new GameNetwork(12345),
                client1 = new GameNetwork("localhost", 12345),
                client2 = new GameNetwork("localhost", 12345),
                client3 = new GameNetwork("localhost", 12345);
        
        server.changeClientName("Overlord");
        client1.changeClientName("Client 1");
        client2.changeClientName("Client 2");
        client3.changeClientName("Cleint 3");

        
        client1.addChatMessageListener(new ChatMessageListener() {
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
        server.startConnection();
        Thread.sleep(1000);
        client1.startConnection();
        Thread.sleep(1000);
        client2.startConnection();
        Thread.sleep(1000);
        client3.startConnection();
        Thread.sleep(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String input;

        while ((input = br.readLine()) != null) {
            client1.sendChatMessage(input);
            if(input.equals("exit")) break;
        }
    }
}
