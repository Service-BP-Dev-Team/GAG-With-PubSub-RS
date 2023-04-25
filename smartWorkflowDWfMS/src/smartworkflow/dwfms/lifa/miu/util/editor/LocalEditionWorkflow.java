/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.editor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ndadji Maxime
 */
public class LocalEditionWorkflow<P, S> extends EditionWorkflow<P, S>{
	private static final long serialVersionUID = 1L;

	public static final String
            UPDATE_TYPE_EDITION = "Edition",
            UPDATE_TYPE_SYNCHRO = "Synchronisation";
    
    private HashMap<String, UpdateInfos> documentUpdates = new HashMap<String, UpdateInfos>();
    private ArrayList<S> view;
    private boolean canSeeGlobalWorkflow;
    private boolean canDecideSynchro;
    
    private boolean waitingSynchro;
    private String documentToSynchronize;
    private String localID;
    private String localOwnerLogin;
    private String localOwnerPassword;

    public String getDocumentToSynchronize() {
        return documentToSynchronize;
    }

    public void setDocumentToSynchronize(String documentToSynchronize) {
        this.documentToSynchronize = documentToSynchronize;
    }

    public HashMap<String, UpdateInfos> getDocumentUpdates() {
        return documentUpdates;
    }

    public void setDocumentUpdates(HashMap<String, UpdateInfos> documentUpdates) {
        this.documentUpdates = documentUpdates;
    }

    public boolean isWaitingSynchro() {
        return waitingSynchro;
    }

    public void setWaitingSynchro(boolean waitingSynchro) {
        this.waitingSynchro = waitingSynchro;
    }

    public boolean isCanDecideSynchro() {
        return canDecideSynchro;
    }

    public void setCanDecideSynchro(boolean canDecideSynchro) {
        this.canDecideSynchro = canDecideSynchro;
    }

    public boolean isCanSeeGlobalWorkflow() {
        return canSeeGlobalWorkflow;
    }

    public void setCanSeeGlobalWorkflow(boolean canSeeGlobalWorkflow) {
        this.canSeeGlobalWorkflow = canSeeGlobalWorkflow;
    }

    public ArrayList<S> getView() {
        return view;
    }

    public void setView(ArrayList<S> view) {
        this.view = view;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public String getLocalOwnerLogin() {
        return localOwnerLogin;
    }

    public void setLocalOwnerLogin(String localOwnerLogin) {
        this.localOwnerLogin = localOwnerLogin;
    }

    public String getLocalOwnerPassword() {
        return localOwnerPassword;
    }

    public void setLocalOwnerPassword(String localOwnerPassword) {
        this.localOwnerPassword = localOwnerPassword;
    }
    
    public class UpdateInfos{
        private Long time;
        private String type;

        public UpdateInfos() {
        }

        public UpdateInfos(Long time, String type) {
            this.time = time;
            this.type = type;
        }
        
        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
