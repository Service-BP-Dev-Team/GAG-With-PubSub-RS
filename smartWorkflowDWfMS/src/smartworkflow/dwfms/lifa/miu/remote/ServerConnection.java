/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import smartworkflow.dwfms.lifa.miu.remote.interfaces.ConnectionInterface;
import smartworkflow.dwfms.lifa.miu.util.exceptions.TinyCERemoteException;

/**
 *
 * @author Ndadji Maxime
 */
public class ServerConnection  extends UnicastRemoteObject implements ConnectionInterface{
	private static final long serialVersionUID = 1L;
	private int index = 0;
    private ArrayList<String> tokens = new ArrayList<String>();

    public ServerConnection(int tokenNum) throws RemoteException {
        generateTokens(tokenNum);
    }
    
    @Override
    public String getToken() throws TinyCERemoteException, RemoteException {
        String token = tokens.get(index);
        index = (index + 1) % tokens.size();
        return token;
    }
    
    private void generateTokens(int tokenNum){
        int n = 0, k;
        if(tokenNum <= 0)
            tokenNum = 20;
        while(n < tokenNum){
            k = (int)Math.round(Math.random() * 15000);
            k *= (int)Math.round(Math.random() * 10);
            if((k+"").length() >= 3 && !tokens.contains(k+"")){
                tokens.add(k+"");
                n++;
            }
        }
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }
}
