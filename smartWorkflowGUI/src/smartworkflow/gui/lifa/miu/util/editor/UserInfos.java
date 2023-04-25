/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ndadji Maxime
 */
public class UserInfos<K> implements Serializable{
	private static final long serialVersionUID = 1L;
	private String login;
    private String password;
    private ArrayList<K> view;
    private String clientHost;
    private boolean canSeeGlobalWorkflow;
    private boolean canDecideSynchro;

    public UserInfos() {
    }

    public UserInfos(String login, String password, ArrayList<K> view, String clientHost) {
        this.login = login;
        this.password = password;
        this.view = view;
        this.clientHost = clientHost;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<K> getView() {
        return view;
    }

    public void setView(ArrayList<K> view) {
        this.view = view;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public boolean isCanSeeGlobalWorkflow() {
        return canSeeGlobalWorkflow;
    }

    public void setCanSeeGlobalWorkflow(boolean canSeeGlobalWorkflow) {
        this.canSeeGlobalWorkflow = canSeeGlobalWorkflow;
    }

    public boolean isCanDecideSynchro() {
        return canDecideSynchro;
    }

    public void setCanDecideSynchro(boolean canDecideSynchro) {
        this.canDecideSynchro = canDecideSynchro;
    }
}