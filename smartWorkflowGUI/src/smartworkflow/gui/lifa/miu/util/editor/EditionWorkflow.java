/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.io.Serializable;

/**
 *
 * @author Ndadji Maxime
 */
public abstract class EditionWorkflow<P, S> implements Serializable{
	private static final long serialVersionUID = 1L;
	protected Grammar<P, S> grammar;
    protected String initialDocument;
    protected String currentDocument;
    
    protected String workflowKey;
    protected String workflowInitVector;
    
    protected String workflowName;
    protected String workflowID;
    protected String workflowServer;
    
    protected String ownerLogin;
    protected String ownerPassword;
    
    protected long creationTime;

    public String getCurrentDocument() {
        return currentDocument;
    }

    public void setCurrentDocument(String currentDocument) {
        this.currentDocument = currentDocument;
    }

    public Grammar<P, S> getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar<P, S> grammar) {
        this.grammar = grammar;
    }

    public String getInitialDocument() {
        return initialDocument;
    }

    public void setInitialDocument(String initialDocument) {
        this.initialDocument = initialDocument;
    }

    public String getWorkflowInitVector() {
        return workflowInitVector;
    }

    public void setWorkflowInitVector(String workflowInitVector) {
        this.workflowInitVector = workflowInitVector;
    }

    public String getWorkflowKey() {
        return workflowKey;
    }

    public void setWorkflowKey(String workflowKey) {
        this.workflowKey = workflowKey;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public String getWorkflowServer() {
        return workflowServer;
    }

    public void setWorkflowServer(String workflowServer) {
        this.workflowServer = workflowServer;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getWorkflowID() {
        return workflowID;
    }

    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
}
