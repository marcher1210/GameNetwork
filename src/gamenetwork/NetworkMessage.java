/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

/**
 *
 * @author marcher89
 */
public class NetworkMessage {
    
    public NetworkMessage(NetworkMessageType type, Object object) {
        this.type = type;
        this.object = object;
    }
    
    public NetworkMessageType getType() {
        return type;
    }
    
    public Object getObject() {
        return object;
    }
    
    private NetworkMessageType type;
    private Object object;
    
}
