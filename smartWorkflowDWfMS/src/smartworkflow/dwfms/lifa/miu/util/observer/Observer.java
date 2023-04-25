/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.observer;

import smartworkflow.dwfms.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;

/**
 *
 * @author Ndadji Maxime
 */
public interface Observer {
    public void updateStartLoaded(String message);
    public void updateStartUtilities();
    public void updateGrammarAndViews(GrammarAndViews<String> gramAndViews);
    public void updateGrammarError(String error);
    public void updateLocalWorkflowCreated();
    public void updateUserChanged();
    public void updateThemeChanged();
    public void updateDistributedWorkflowCreated();
    public void updateEditWorkflow(LocalEditionWorkflow<String, String> workflow);
    public void updateSuccess (String message);
    public void updateError (String message);
    public void updateTaskStarted(String t);
    public void updateTaskEnded(String t);
}
