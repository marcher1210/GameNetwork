/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork.listeners;

/**
 *
 * @author marcher89
 */
public interface LobbyActivityListener {


    void clientConnected(int clientId, String clientName);

    void clientNameChanged(int clientId, String newClientName);

    void clientDisconnected(int clientId);
}
