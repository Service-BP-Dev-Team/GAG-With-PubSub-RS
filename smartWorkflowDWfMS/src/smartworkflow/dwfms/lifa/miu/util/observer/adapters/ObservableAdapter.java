/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.observer.adapters;

import java.util.ArrayList;
import java.util.List;

import smartworkflow.dwfms.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.exceptions.ElementNotFoundException;
import smartworkflow.dwfms.lifa.miu.util.observer.Observable;
import smartworkflow.dwfms.lifa.miu.util.observer.Observer;

/**
 *
 * @author Ndadji Maxime
 */
public abstract class ObservableAdapter implements Observable{

    protected List<Observer> observerList;
    
    public ObservableAdapter(){
        observerList = new ArrayList<Observer>();
    }
    
    public ObservableAdapter(List<Observer> observerList){
        this.observerList = observerList;
    }
    
    @Override
    public void addObserver(Observer observer) {
        if(!this.observerList.contains(observer))
            this.observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) throws ElementNotFoundException {
        try{
            this.observerList.remove(observer);
        }
        catch(Exception e){
            throw new ElementNotFoundException(e.getMessage());
        }
    }
    
    @Override
    public void notifyStartLoaded(String message) {
        for(Observer observer : observerList)
            observer.updateStartLoaded(message);
    }

    @Override
    public void notifyStartUtilities() {
        for(Observer observer : observerList)
            observer.updateStartUtilities();
    }

    @Override
    public void notifyGrammarAndViews(GrammarAndViews<String> gramAndViews) {
        for(Observer observer : observerList)
            observer.updateGrammarAndViews(gramAndViews);
    }

    @Override
    public void notifyGrammarError(String error) {
        for(Observer observer : observerList)
            observer.updateGrammarError(error);
    }

    @Override
    public void notifyLocalWorkflowCreated() {
        for(Observer observer : observerList)
            observer.updateLocalWorkflowCreated();
    }

    @Override
    public void notifyUserChanged() {
        for(Observer observer : observerList)
            observer.updateUserChanged();
    }
    
    @Override
    public void notifyThemeChanged() {
        for(Observer observer : observerList)
            observer.updateThemeChanged();
    }

    @Override
    public void notifyDistributedWorkflowCreated() {
        for(Observer observer : observerList)
            observer.updateDistributedWorkflowCreated();
    }

    @Override
    public void notifyEditWorkflow(LocalEditionWorkflow<String, String> workflow) {
        for(Observer observer : observerList)
            observer.updateEditWorkflow(workflow);
    }
    
    @Override
    public void notifySuccess(String message) {
         for(Observer observer : observerList)
            observer.updateSuccess(message);
    }

    @Override
    public void notifyError(String message) {
        for(Observer observer : observerList)
            observer.updateError(message);
    }
    
    @Override
    public void notifyTaskStarted(String t){
        for(Observer observer : observerList)
            observer.updateTaskStarted(t);
    }
    
    @Override
    public void notifyTaskEnded(String t){
        for(Observer observer : observerList)
            observer.updateTaskEnded(t);
    }
}
