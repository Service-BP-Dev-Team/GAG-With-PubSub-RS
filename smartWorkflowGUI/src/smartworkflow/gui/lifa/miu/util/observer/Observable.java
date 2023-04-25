/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.observer;

import smartworkflow.gui.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.gui.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.gui.lifa.miu.util.exceptions.ElementNotFoundException;

/**
 *
 * @author Ndadji Maxime
 */
public interface Observable {
    public void addObserver(Observer observer);
    public void removeObserver(Observer observer) throws ElementNotFoundException;
    public void notifyStartLoaded(String message);
    public void notifyStartUtilities();
    public void notifyGrammarError(String error);
    public void notifyGrammarAndViews(GrammarAndViews<String> gramAndViews);
    public void notifyLocalWorkflowCreated();
    public void notifyUserChanged();
    public void notifyThemeChanged();
    public void notifyDistributedWorkflowCreated();
    public void notifyEditWorkflow(LocalEditionWorkflow<String, String> workflow);
    public void notifySuccess (String message);
    public void notifyError (String message);
    public void notifyTaskStarted(String t);
    public void notifyTaskEnded(String t);
}
