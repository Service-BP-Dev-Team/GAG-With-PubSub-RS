/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import smartworkflow.gui.lifa.miu.util.exceptions.TinyCERemoteException;

/**
 *
 * @author Ndadji Maxime
 */
public interface CommunicatorInterface extends Remote{

    public String createWorkflow(String cryptedEdition, String cryptedKey, String cryptedInitVect) throws TinyCERemoteException, RemoteException;
    
    public ArrayList<HashMap<String, String>> getPotentialWorkflow(String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException;

    public boolean deleteWorkflow(HashMap<String, String> workflow, String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException;

    public String getLocalWorkflowFor(HashMap<String, String> workflow, String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException;
    
}
