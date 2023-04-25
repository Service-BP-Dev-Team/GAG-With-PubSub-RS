/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.observer.adapters;

import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.gui.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.gui.lifa.miu.util.observer.Observer;

/**
 *
 * @author Ndadji Maxime
 */
public abstract class ObserverAdapter implements Observer{
    @Override
    public void updateStartLoaded(String message) {
        
    }

    @Override
    public void updateStartUtilities() {
        
    }

    @Override
    public void updateGrammarAndViews(GrammarAndViews<String> gramAndViews) {
        
    }

    @Override
    public void updateGrammarError(String error) {
        
    }

    @Override
    public void updateLocalWorkflowCreated() {
        
    }

    @Override
    public void updateUserChanged() {
        
    }
    
    @Override
    public void updateThemeChanged() {
        
    }

    @Override
    public void updateDistributedWorkflowCreated() {
        
    }

    @Override
    public void updateEditWorkflow(LocalEditionWorkflow<String, String> workflow) {
        
    }
    
    @Override
    public void updateSuccess(String message) {
        ModelModel.displayMessageDialog(ModelModel.config.getLangValue("success_notification"), message);
    }

    @Override
    public void updateError(String message) {
        ModelModel.displayErrorDialog(ModelModel.config.getLangValue("error_notification"), message);
    }
    
    @Override
    public void updateTaskStarted(String t){
        
    }
    
    @Override
    public void updateTaskEnded(String t){
        
    }
}
