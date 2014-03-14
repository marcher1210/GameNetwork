/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import static gamenetwork.AbstractNetworkCommunicator.multicastPort;
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

// Static variables
    private static boolean lookForHosts = false;
    private static final List<HostInfo> foundHosts = new ArrayList<>();
    
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
     * Create an instance of GameNetwork, prepared for joining a server.
     * Add the necessary listeners, (change the client name), and use {@link #startConnection()} to try to connect to the server.
     * @param hostInfo A HostInfo object obtained by the {@link #getFoundHosts()} method.
     */
    public GameNetwork(HostInfo hostInfo) {
        this(hostInfo.getAddress(), hostInfo.getPort());
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
    /**
     * Starts the connection, either for listening for new client (if host) or by trying to connect to the given host (if client).
     * Remember to assign listeners to the GameNetwork before calling this.
     */
    public void startConnection() {
        assert !isStarted();
        comm.start();
        stopListeningForHosts();
    }

    /**
     * Shuts down the connection. Disconnects all clients (if host) or the connection to the host (if client).
     */
    public void disconnect() {
        assert isStarted();
        comm.close();
    }

// Status
    /**
     * @return True, if the connection is started (via {@link #startConnection()}).
     */
    public boolean isStarted() {
        return comm != null && comm.isRunning();
    }

    /**
     * @return True if the GameNetwork instance is used to host a game (the {@link GameNetwork(int)} constructor.
     */
    public boolean isHost() {
        return comm instanceof GameServer;
    }

    /**
     * @return The unique client id in the network (always 0 for the host).
     */
    public int myClientId() {
        assert isStarted();
        return comm.getClientId();
    }
    
    /**
     * @return A list of IP addresses, that can be used to print on the host. 
     * Use with caution, as it might not always return a full list of addresses depending on the OS and hardware.
     */
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

    /**
     * ONLY TO BE USED WHEN HOSTING A GAME.
     * @return The port number on which the server listens for new connections.
     */
    public int portNumber() {
        assert isHost();
        return ((GameServer) comm).getPort();
    }

    /**
     * Can be used by both as a host and as a client.
     * @return A key-value map of connected clients. Keys are the clients' unique id (similar to {@link myClientId()}), values are the clients' (human readable) names.
     */
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
        comm.send(new NetworkMessage(NetworkMessageType.ClientNameChanged, new Tuple<>(id, newName)));
   }

    public void changeClientName(String newName) {
        comm.setClientName(newName);
        if(isStarted())
            changeClientName(myClientId(), newName);
    }

    public void changeGameSetting(int type, Object object) {
        assert isStarted() && isHost();
        comm.send(new NetworkMessage(NetworkMessageType.GameSetting, new Tuple<>(type, object)));
        throw new NotImplementedException(); //TODO: Implement
    }

    public void kickClient(int id) {
        assert isStarted() && isHost();
        assert id != myClientId() : "You can't kick youself, mkay?!";
        assert connectedClients().containsKey(id) : "Invalid client id";
        ((GameServer) comm).removeClient(id);
        comm.send(new NetworkMessage(NetworkMessageType.ClientDisconnected, id));
    }

    public void startGame() {
        assert isStarted() && isHost();
        throw new NotImplementedException(); //TODO: Implement
    }

// Game functionality
    public void sendGameUpdate(int type, Object object) {
        assert isStarted();
        comm.send(new NetworkMessage(NetworkMessageType.GameUpdate, new Tuple<>(type, object)));
    }

// Chat messages
    public void sendChatMessage(String msg) {
        assert isStarted();
        comm.send(new NetworkMessage(NetworkMessageType.ChatMessage, msg));
    }
    
    public static List<HostInfo> getFoundHosts(){
        startListeningForHosts();
        synchronized (foundHosts){
            return foundHosts;
        }
    }
    
    /**
     * @return A list with info about hosts found on the network.
     */
    public static void startListeningForHosts(){
        if(lookForHosts) return;
        lookForHosts = true;
        Thread multicast = new Thread(new Runnable() {
            @Override
            public void run() {
                lookForMulticastLoop();
            }
        });
        multicast.start();
    }
    
    public static void stopListeningForHosts(){
        lookForHosts = false;
    }
    
    private static void lookForMulticastLoop(){
        try {
            MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
            InetAddress group = InetAddress.getByName(AbstractNetworkCommunicator.multicastGroupName);
            multicastSocket.joinGroup(group);

            DatagramPacket packet;
            while(lookForHosts){
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                multicastSocket.receive(packet); //blocking
                multicastReceived(packet);
                //TODO: Remove "no more existing" hosts.
            }
            multicastSocket.leaveGroup(group);
            multicastSocket.close();
        } catch (IOException ex) {
            //TODO: Error handling
            ex.printStackTrace();
        } 
    }
    
    private static void multicastReceived(DatagramPacket packet) {
        String address = packet.getAddress().getHostAddress();
        String[] data = new String(packet.getData(), 0, packet.getLength()).split(";");
        if(data.length != 2) return;
        int port = Integer.parseInt(data[0]);
        String hostName = data[1];
        for (HostInfo hostInfo : foundHosts) { //This can be done unsynchronized as this should be the only WRITING thread (and we are not writing to the collection right now, right?)
            if(hostInfo.getAddress().equals(address) && hostInfo.getPort() == port) return; //It's already in the list, don't readd it.
        }
        synchronized(foundHosts) {
            foundHosts.add(new HostInfo(address, port, hostName));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        startListeningForHosts();
        for (int i = 0; i < 5; i++) {
            GameNetwork server = new GameNetwork(34643+i);
            server.changeClientName("Overlord "+i);
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
        }
        
        String input;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        HostInfo hostinfo = new HostInfo(null, 0, null);
        System.out.print("Press enter to see an updated list of found hosts.");
        while ((input = br.readLine()) != null) {
            try {
                int i = Integer.parseInt(input);
                hostinfo = GameNetwork.getFoundHosts().get(i);
                break;
            } 
            catch(NumberFormatException e){
                List<HostInfo> hosts = GameNetwork.getFoundHosts();
                for (int i = 0; i < hosts.size(); i++) {
                    HostInfo host = hosts.get(i);
                    System.out.println("["+i+"] "+host.getHostName()+" on "+host.getAddress()+":"+host.getPort());
                }
                System.out.print("Type a number to connect to the host or press enter to see an updated list: ");
            }
            
        }
        GameNetwork client1 = new GameNetwork(hostinfo);
        client1.addChatMessageListener(new ChatMessageListener() {
            @Override
            public void chatMessageReceived(int senderId, String message) {
                System.out.println("Chat message from " + senderId + ": " + message);
            }
        });
        Thread.sleep(1000);
        client1.startConnection();
        Thread.sleep(1000);

        System.out.print("Type a chat message or type exit to stop: "); 

        while ((input = br.readLine()) != null) {
            client1.sendChatMessage(input);
            if(input.equals("exit")) System.exit(0);
            Thread.sleep(1000);
            System.out.print("Type a chat message or type exit to stop: ");
        }
    }
}
