/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork.listeners;

/**
 *
 * @author marcher89
 */
public interface ChatMessageListener {
    void chatMessageReceived(int senderId, String message);
}
