/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork;

/**
 *
 * @author marcher89
 */
public class HostInfo {
    private final String address;
    private final String hostName;
    private final int port;

    public HostInfo(String address, int port, String hostName) {
        this.address = address;
        this.port = port;
        this.hostName = hostName;
    }

    public String getAddress() {
        return address;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }
    
}
