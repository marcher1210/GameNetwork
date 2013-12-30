/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

/**
 *
 * @author marcher89
 */
public enum NetworkMessageType {
    FirstConnect, 
    YourClientId,
    ConnectedClients,
    ClientConnected, 
    ClientDisconnected, 
    ClientNameChanged, 
    GameSetting, 
    GameUpdate, 
    ChatMessage
}
