/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ndadji Maxime
 */
public class DistributedEditionWorkflow<P, S> extends EditionWorkflow<P, S>{
	private static final long serialVersionUID = 1L;

	private HashMap<String, UserInfos<S>> coAuthors = new HashMap<String, UserInfos<S>>();
    
    private String workflowPreviousKey;
    private String workflowPreviousInitVector;
    private String engine;
    
    private boolean isASynchroRunning;
    private ArrayList<HashMap<String, String>> synchroDocs = new ArrayList<HashMap<String, String>>();
    private ArrayList<ArrayList<S>> views = new ArrayList<ArrayList<S>>();
    
    //Clé = document + date de synchro
    private HashMap<String, ArrayList<HashMap<String, String>>> documentUpdates = new HashMap<String, ArrayList<HashMap<String, String>>>();

    public HashMap<String, UserInfos<S>> getCoAuthors() {
        return coAuthors;
    }

    public void setCoAuthors(HashMap<String, UserInfos<S>> coAuthors) {
        this.coAuthors = coAuthors;
    }

    public boolean isIsASynchroRunning() {
        return isASynchroRunning;
    }

    public void setIsASynchroRunning(boolean isASynchroRunning) {
        this.isASynchroRunning = isASynchroRunning;
    }

    public ArrayList<HashMap<String, String>> getSynchroDocs() {
        return synchroDocs;
    }

    public void setSynchroDocs(ArrayList<HashMap<String, String>> synchroDocs) {
        this.synchroDocs = synchroDocs;
    }

    public String getWorkflowPreviousInitVector() {
        return workflowPreviousInitVector;
    }

    public void setWorkflowPreviousInitVector(String workflowPreviousInitVector) {
        this.workflowPreviousInitVector = workflowPreviousInitVector;
    }

    public String getWorkflowPreviousKey() {
        return workflowPreviousKey;
    }

    public void setWorkflowPreviousKey(String workflowPreviousKey) {
        this.workflowPreviousKey = workflowPreviousKey;
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> getDocumentUpdates() {
        return documentUpdates;
    }

    public void setDocumentUpdates(HashMap<String, ArrayList<HashMap<String, String>>> documentUpdates) {
        this.documentUpdates = documentUpdates;
    }

    public ArrayList<ArrayList<S>> getViews() {
        return views;
    }

    public void setViews(ArrayList<ArrayList<S>> views) {
        this.views = views;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }
}
