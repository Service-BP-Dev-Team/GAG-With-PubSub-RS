/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.remote.interfaces.CommunicatorInterface;
import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.editor.DistributedEditionWorkflow;
import smartworkflow.gui.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.gui.lifa.miu.util.exceptions.TinyCERemoteException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Ndadji Maxime
 */
public class RemoteCommunicator extends UnicastRemoteObject implements CommunicatorInterface{
	private static final long serialVersionUID = 1L;

	public RemoteCommunicator() throws RemoteException {
    }

    @SuppressWarnings("unchecked")
	@Override
    public String createWorkflow(String cryptedEdition, String cryptedKey, String cryptedInitVect) throws TinyCERemoteException, RemoteException {
        try{
            String key = APPLConstants.decryptMessage(cryptedKey);
            String initVect = APPLConstants.decryptMessage(cryptedInitVect);
            String json = APPLConstants.decryptMessage(cryptedEdition, key, initVect);
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            DistributedEditionWorkflow<String, String> edition = gson.fromJson(json, DistributedEditionWorkflow.class);
            edition.setCreationTime(DateTime.now().getMillis());
            return APPLConstants.encryptMessage(ModelModel.getConfig().saveNewDistributedEdition(edition));
        }catch(Throwable e){
            throw new TinyCERemoteException(e);
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> getPotentialWorkflow(String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException {
        try{
            return ModelModel.getConfig().getPotentialWorkflowsFor(cryptedLogin, cryptedPass);
        }catch(Throwable e){
            throw new TinyCERemoteException(e);
        }
    }

    @Override
    public boolean deleteWorkflow(HashMap<String, String> workflow, String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException {
        try{
            return ModelModel.getConfig().deleteWorkflow(workflow, cryptedLogin, cryptedPass);
        }catch(Throwable e){
            throw new TinyCERemoteException(e);
        }
    }

    @Override
    public String getLocalWorkflowFor(HashMap<String, String> workflow, String cryptedLogin, String cryptedPass) throws TinyCERemoteException, RemoteException {
        try{
            LocalEditionWorkflow<String, String> edition = ModelModel.getConfig().getLocalWorkflowFrom(APPLConstants.decryptMessage(workflow.get("id")), cryptedLogin, cryptedPass);
            if(edition == null)
                return null;
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            String json = gson.toJson(edition);
            return APPLConstants.encryptMessage(json);
        }catch(Throwable e){
            throw new TinyCERemoteException(e);
        }
    }
}
