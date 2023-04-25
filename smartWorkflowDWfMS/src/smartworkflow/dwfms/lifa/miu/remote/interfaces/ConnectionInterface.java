/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import smartworkflow.dwfms.lifa.miu.util.exceptions.TinyCERemoteException;

/**
 *
 * @author Ndadji Maxime
 */
public interface ConnectionInterface extends Remote {
    public String getToken() throws TinyCERemoteException, RemoteException;
}
