/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

import java.io.Serializable;

/**
 *
 * @author marcher89
 */
public class NetworkMessage implements Serializable {
    
    public NetworkMessage(NetworkMessageType type, Object object) {
        this.type = type;
        this.object = object;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public NetworkMessageType getType() {
        return type;
    }
    
    public Object getObject() {
        return object;
    }
    
    private int senderId;
    private NetworkMessageType type;
    private Object object;
    
}
