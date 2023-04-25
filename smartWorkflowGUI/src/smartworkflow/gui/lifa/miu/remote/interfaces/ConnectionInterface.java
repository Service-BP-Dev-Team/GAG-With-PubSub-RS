/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import smartworkflow.gui.lifa.miu.util.exceptions.TinyCERemoteException;

/**
 *
 * @author Ndadji Maxime
 */
public interface ConnectionInterface extends Remote {
    public String getToken() throws TinyCERemoteException, RemoteException;
}
