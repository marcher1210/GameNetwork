/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamenetwork.util;

import java.io.Serializable;

/**
 *
 * @author marcher89
 */
public class Tuple<First, Second> implements Serializable {

    public final First first;
    public final Second second;

    public Tuple(First first, Second second) {
        this.first = first;
        this.second = second;
    }
}
